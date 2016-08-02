// Copyright 2015 Jelte Jansen
//
// This file is part of SiChecker.
//
// SiChecker is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// Foobar is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with Foobar.  If not, see <http://www.gnu.org/licenses/>.

import java.lang.System;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import static net.sourceforge.argparse4j.impl.Arguments.storeTrue;

public class SiChecker {
    String FROM_ADDR = "sitest2@si-test.sidnlabs.nl";
    String LOG_FILE = "/home/jelte/si_checker_java.log";

    ArrayList<String> output;
    String input_file;
    String schematron_file;
    String xslt_file;
    String xsd_file;
    boolean convert_pdf;
    ArrayList<PDFFile> PDFFiles;
    PrintWriter logfile;
    Logger log;

    ArrayList<DocumentData> documents;
    MailHandler mail_handler;
    
    public SiChecker() {
        documents = new ArrayList<DocumentData>();
        output = new ArrayList<String>();
        input_file = null;
        schematron_file = null;
        xslt_file = null;
        xsd_file = null;
        mail_handler = null;
        log = new Logger(LOG_FILE);
        log.logln("[START OF HANDLER]");
        log.logDate();
        PDFFiles = new ArrayList<PDFFile>();
    }
    
    public void close() {
        log.logln("[END OF HANDLER]");
        log.closeLog();
    }
    
    public static void main(String[] argv) {
        SiChecker sic = new SiChecker();
        sic.parseArguments(argv);
        sic.readInput();
        sic.validateFiles();
        sic.createPDF();
        sic.showResults();
        sic.close();
    }
    
    public void parseArguments(String[] argv) {
        ArgumentParser parser = ArgumentParsers.newArgumentParser("SiChecker")
                .defaultHelp(true)
                .description("Validate SI-UBL file");
        parser.addArgument("-m", "--mail").action(storeTrue())
                .help("Enable mail mode (default off)");
        parser.addArgument("-i", "--inputfile")
                .help("Read xml document definition from given file instead of stdin");
        parser.addArgument("-s", "--schematron")
                .help("Read schematron definition from given file");
        parser.addArgument("-l", "--stylesheet")
                .help("Read stylesheet definition from given file");
        parser.addArgument("-x", "--xsd")
                .help("Read xml schema definition from given file");
        parser.addArgument("-c", "--convert").action(storeTrue())
                .help("Convert the document to PDF (if it validates)");
        Namespace ns = null;
        
        try {
            ns = parser.parseArgs(argv);
            if (ns.get("schematron") == null &&
                ns.get("stylesheet") == null &&
                ns.get("xsd") == null) {
                System.out.println("Error: must supply one of -s, -l or -x");
            }
            if (ns.get("schematron") != null &&
                ns.get("stylesheet") != null) {
                System.out.println("Error: cannot use both -s and -l");
            }
            if ((Boolean)ns.get("mail")) {
                // this automatically sets output to mail as well
                mail_handler = new MailHandler(logfile, log, output);
            }
            input_file = ns.get("inputfile");
            schematron_file = ns.get("schematron");
            xslt_file = ns.get("stylesheet");
            xsd_file = ns.get("xsd");
            convert_pdf = (Boolean)ns.get("convert");
            
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }
    }


    public void readInput() {
        if (input_file != null) {
            DocumentData doc = new DocumentData();
            doc.setFilename(input_file);
            doc.setXMLSourceFromFile(input_file);
            documents.add(doc);
        } else if (mail_handler != null) {
            mail_handler.readMailInput(documents);
        } else {
            StreamSource inputFile = new StreamSource(System.in);
            DocumentData doc = new DocumentData();
            doc.setFilename("<stdin>");
            doc.setXMLSourceFromStream(System.in);
            documents.add(doc);
        }
    }
    
    public void validateFiles() {
        for (DocumentData doc : documents) {
            output.add("XML File: " + doc.getFilename());
            validateInputFile(doc);
        }
    }

    private void logMailMessage(Message message) throws IOException, MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        log.logln(baos.toString());
    }

    public void validateInputFile(DocumentData doc) {
        Results xsd_results = new Results();
        if (xsd_file != null) {
            SISchemaValidator schemaValidator = new SISchemaValidator();
            schemaValidator.validate(xsd_results, doc.getXMLSource(), xsd_file);
            doc.setValidated(xsd_results.noErrors());
            if (xsd_results.allOK()) {
                output.add("XML Schema (XSD) OK");
            } else {
                output.add(xsd_results.getErrorCount() + " errors in XSD validation");
                output.add(xsd_results.getWarningCount() + " warnings in XSD validation");
            }
        }

        Results xslt_results = new Results();
        if (xslt_file != null) {
            // if input was a stream, we need to reset it now
            SIXSLTValidator schemaValidator = new SIXSLTValidator();
            schemaValidator.validate(xslt_results, doc.getXMLSource(), xslt_file);
            doc.setValidated(xslt_results.noErrors());
            if (xslt_results.allOK()) {
                output.add("SI Schematron OK");
                output.add("");
            } else {
                output.add(xslt_results.getErrorCount() + " Schematron errors");
                output.add(xslt_results.getWarningCount() + " Schematron warnings");
            }
        }

        output.add("");
        output.add("");

        if (!xsd_results.allOK()) {
            output.add("XSD report:");
            for (String error : xsd_results.getErrors()) {
                output.add("XSD Error: " + error);
                output.add("");
            }
            for (String warning : xsd_results.getWarnings()) {
                output.add("XSD Warning: " + warning);
                output.add("");
            }
        }
        if (!xslt_results.allOK()) {
            output.add("Schematron report:");
            for (String error : xslt_results.getErrors()) {
                output.add("Error: " + error);
                output.add("");
            }
            for (String warning : xslt_results.getWarnings()) {
                output.add("Warning: " + warning);
                output.add("");
            }
        }
        
        if (convert_pdf) {
            convertDocument(doc);
        }
    }
    
    public void convertDocument(DocumentData docdata) {
        try {
            PDFCreator pdfc = new PDFCreator();
            Document d = pdfc.transform(docdata.getXMLSource(), "/home/jelte/repos/SI/si_checker_java/efactuur//nl/clockwork/efactuur/nl/pdf/ubl/InvoiceToFactuurCanonical.xsl");
            pdfc.transformToFile(docdata.getXMLSource(), "/home/jelte/repos/SI/si_checker_java/efactuur//nl/clockwork/efactuur/nl/pdf/ubl/InvoiceToFactuurCanonical.xsl", "/tmp/stap1.xml");
            Document d2 = pdfc.transform(docdata.getXMLSource(), "/home/jelte/repos/SI/si_checker_java/efactuur//nl/clockwork/efactuur/nl/pdf/CanonicalToFactuurPDF.xsl");
            pdfc.transformToFile(docdata.getXMLSource(), "/home/jelte/repos/SI/si_checker_java/efactuur/nl/clockwork/efactuur/nl/pdf/CanonicalToFactuurPDF.xsl", "/tmp/stap2.xml");
            pdfc.PDFToFile(d2, "/tmp/output.pdf");
        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public void createPDF() {
        if (convert_pdf) {
            PDFCreator pdfc = new PDFCreator();
            for (DocumentData doc : documents) {
                if (doc.validated()) {
                    try {
                        PDFFiles.add(pdfc.createPDF(doc));
                        output.add("Converted " + doc.getFilename() + " to PDF");
                    } catch (Exception exc) {
                        output.add("Unable to convert " + doc.getFilename() + " to PDF: " + exc.toString());
                        exc.printStackTrace();
                    }
                }
            }
        }
    }

    public void showResults() {
        if (mail_handler != null) {
            mail_handler.mailOutput(output, PDFFiles);
        } else {
            for (String line : output) {
                System.out.println(line);
            }
            for (PDFFile pdf : PDFFiles) {
                String outputpath = "./" + pdf.getName();
                try {
                    FileOutputStream fos = new FileOutputStream(outputpath);
                    pdf.getByteStream().writeTo(fos);
                    System.out.println("Wrote PDF to " + outputpath);
                } catch (IOException ioe) {
                    System.out.println("Error writing " + outputpath + ": " + ioe.toString());
                }
            }
        }
    }
}

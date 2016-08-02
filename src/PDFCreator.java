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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FileOutputStream;
import java.io.BufferedOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;

import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.sax.SAXResult;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.MimeConstants;

class PDFCreator {
    TransformerFactory transformFactory;

    public PDFCreator() {
        transformFactory = TransformerFactory.newInstance();
        TransformerFactoryImpl transformFactoryImpl = (TransformerFactoryImpl) transformFactory;
        net.sf.saxon.Configuration saxonConfig = transformFactoryImpl.getConfiguration();
        saxonConfig.setRecoveryPolicy(Configuration.RECOVER_SILENTLY);
        transformFactoryImpl.setConfiguration(saxonConfig);
    }

    public Document transform(Document doc, String xsl_filename) throws Exception {
        return transform(new DOMSource(doc), xsl_filename);
    }
    
    public Document transform(Source source, String xsl_filename) throws Exception {
        Transformer transformer =
        transformFactory.newTransformer(new StreamSource(new File(xsl_filename)));
        transformer.setParameter("message_id", "12345");
        transformer.setParameter("message_format", "SI");
        transformer.setParameter("message_version", "1");
        transformer.setParameter("bericht_soort", "a");
        transformer.setParameter("message_date", "2015-10-20T09:00:00");
        transformer.setParameter("original_message_type", "UBL");
        StreamResult result=new StreamResult(new StringWriter());
        transformer.transform(source, result);
        String xmlString=result.getWriter().toString();
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        return dBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
    }
    
    public void transformToFile(Document doc, String xsl_filename, String output_filename) throws Exception {
        transformToFile(new DOMSource(doc), xsl_filename, output_filename);
    }

    public void transformToFile(Source source, String xsl_filename, String output_filename) throws Exception {
        Transformer transformer =
        transformFactory.newTransformer(new StreamSource(new File(xsl_filename)));
        StreamResult result=new StreamResult(new FileWriter(new File(output_filename)));
        transformer.setParameter("message_id", "12345");
        transformer.setParameter("message_format", "SI");
        transformer.setParameter("message_version", "1");
        transformer.setParameter("bericht_soort", "a");
        transformer.setParameter("message_date", "2015-10-20T09:00:00");
        transformer.setParameter("original_message_type", "UBL");
        transformer.transform(source, result);
    }

    public void PDFToFile(Document doc, String output_filename) throws Exception {
        DOMSource src = new DOMSource(doc);
        
        // Step 1: Construct a FopFactory
        // (reuse if you plan to render multiple documents!)
        FopFactory fopFactory = FopFactory.newInstance(new File("/home/jelte/repos/SI/si_checker_java/deps/fop-2.0/conf/fop.xconf"));

        // Step 2: Set up output stream.
        // Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
        //OutputStream out2 = new BufferedOutputStream(new FileOutputStream(new File("/tmp/myfile.pdf")));
        //ByteArrayOutputStream out = new ByteArrayOutputStream();
        FileOutputStream out = new FileOutputStream(output_filename);

        // Step 3: Construct fop with desired output format
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

        // Step 4: Setup JAXP using identity transformer
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(); // identity transformer

        // Step 5: Setup input and output for XSLT transformation
        // Setup input stream
        //Source src = new StreamSource(new File("/home/jelte/repos/SI/si_checker_java/deps/fop-2.0/examples/fo/advanced/giro.fo"));

        // Resulting SAX events (the generated FO) must be piped through to FOP
        SAXResult res = new SAXResult(fop.getDefaultHandler());

        // Step 6: Start XSLT transformation and FOP processing
        transformer.transform(src, res);

        //out2.close();
        out.close();

    }

    public PDFFile createPDF(DocumentData doc) throws Exception {
        // this can probably be done more efficiently
        Transformer transformer =
        transformFactory.newTransformer(new StreamSource(new File("/home/jelte/repos/SI/si_checker_java/deps/Crane-UBL2UNLK-20110203-1600z/xslt/CraneUBL2UN380Invoice-NL.xsl")));
        StreamResult result=new StreamResult(new StringWriter());
        transformer.transform(doc.getXMLSource(), result);
        String xmlString=result.getWriter().toString();
        return new PDFFile(doc.getFilenameExtRepl("pdf"), convertString(xmlString));
    }

    private ByteArrayOutputStream convertString(String xmlString) throws Exception {
        //System.out.println(xmlString);
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        StreamSource source = new StreamSource(new ByteArrayInputStream(xmlString.getBytes()));
        return doPDF(source);
    }

    private ByteArrayOutputStream doPDF(StreamSource src) throws Exception {

        // Step 1: Construct a FopFactory
        // (reuse if you plan to render multiple documents!)
        FopFactory fopFactory = FopFactory.newInstance(new File("/home/jelte/repos/SI/si_checker_java/deps/fop-2.0/conf/fop.xconf"));

        // Step 2: Set up output stream.
        // Note: Using BufferedOutputStream for performance reasons (helpful with FileOutputStreams).
        //OutputStream out2 = new BufferedOutputStream(new FileOutputStream(new File("/tmp/myfile.pdf")));
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // Step 3: Construct fop with desired output format
        Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);

        // Step 4: Setup JAXP using identity transformer
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(); // identity transformer

        // Step 5: Setup input and output for XSLT transformation
        // Setup input stream
        //Source src = new StreamSource(new File("/home/jelte/repos/SI/si_checker_java/deps/fop-2.0/examples/fo/advanced/giro.fo"));

        // Resulting SAX events (the generated FO) must be piped through to FOP
        SAXResult res = new SAXResult(fop.getDefaultHandler());

        // Step 6: Start XSLT transformation and FOP processing
        transformer.transform(src, res);

        //out2.close();
        out.close();

        return out;
    }
}

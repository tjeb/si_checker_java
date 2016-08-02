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
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.xml.transform.stream.StreamSource;
import javax.activation.DataSource;
import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;


/**
 * Separate class to handle incoming and outgoing mail support
 */

public class MailHandler {
    String FROM_ADDR = "sitest2@si-test.sidnlabs.nl";

    PrintWriter logfile;
    Logger log;
    // TODO: should all output go to main?
    ArrayList<String> output;

    boolean mail_input;
    String mail_subject;
    Address[] mail_senders;


    public MailHandler(PrintWriter sic_logfile,
                       Logger sic_log,
                       ArrayList<String> sic_output) {
        log = sic_log;
        logfile = sic_logfile;
        output = sic_output;
    }

    
    private void logMailMessage(Message message) throws IOException, MessagingException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        message.writeTo(baos);
        log.logln(baos.toString());
    }

    public void readMailInput(ArrayList<DocumentData> documents) {
        Session s = Session.getDefaultInstance(new Properties());
        //InputStream is = new ByteArrayInputStream(System.in);
        try {
            MimeMessage message = new MimeMessage(s, System.in);
            log.logln("[START OF RECEIVED MAIL]");
            logMailMessage(message);
            log.logln("[END OF RECEIVED MAIL]");

            mail_subject = message.getSubject();
            mail_senders = message.getFrom();
            if (!message.getContentType().startsWith("multipart/mixed")) {
                // TODO: ignore these messages, will write response later
                output.add("");
                output.add("Received plaintext or HTML message.");
                output.add("");
                output.add("To use the e-mail validator, please " +
                           "attach one or more XML files to the " +
                           "message.");
                output.add("");
                output.add("Received message content type: '" + message.getContentType() + "'");
            } else {
                Multipart multipart = (Multipart) message.getContent();
                for (int i=0; i < multipart.getCount(); i++) {
                    BodyPart bodypart = multipart.getBodyPart(i);
                    if (bodypart.getContentType().startsWith("text/xml")) {
                        DocumentData doc = new DocumentData();
                        doc.setFilename(bodypart.getFileName());
                        String inputString = (String)bodypart.getContent();
                        doc.setXMLSourceFromBytes(inputString.getBytes());
                        documents.add(doc);
                    } else if (bodypart.getContentType().startsWith("application/xml")) {
                        DocumentData doc = new DocumentData();
                        doc.setFilename(bodypart.getFileName());
                        doc.setXMLSourceFromByteStream((ByteArrayInputStream)bodypart.getContent());
                        documents.add(doc);
                    } else if (bodypart.getContentType().startsWith("text/plain") ||
                               bodypart.getContentType().startsWith("text/html")) {
                        // ignore text parts
                    } else {
                        output.add("Unknown content type: '" + bodypart.getContentType() + "'");
                        output.add("");
                        output.add("Skipped attachment " + bodypart.getFileName());
                        output.add("");
                    }
                }
            }
        } catch (MessagingException mse) {
            mse.printStackTrace(logfile);
        } catch (IOException ioe) {
            ioe.printStackTrace(logfile);
        }
    }

    public void mailOutput(ArrayList<String> output, ArrayList<PDFFile> pdf_files) {
        output.add("[XX] pdf files: " + pdf_files.size());
        if (pdf_files.size() > 0) {
            mailOutputPDF(output, pdf_files);
            return;
        }
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", "localhost");
            Session session = Session.getInstance(props, null);

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(FROM_ADDR));
            msg.setRecipients(Message.RecipientType.TO,
                              mail_senders);
            if (mail_subject == null) {
                msg.setSubject("SimplerInvoicing mail validator");
            } else {
                if (mail_subject.startsWith("Re: ")) {
                    msg.setSubject(mail_subject);
                } else {
                    msg.setSubject("Re: " + mail_subject);
                }
            }

            StringBuffer sb = new StringBuffer();
            for (String line : output) {
                sb.append(line);
                sb.append("\n");
            }
            
            String text = sb.toString();

            msg.setText(text);

            msg.setHeader("X-Mailer", "SI Mail test tool");
            msg.setSentDate(new Date());

            log.logln("[START OF SENT MAIL]");
            logMailMessage(msg);
            log.logln("[END OF SENT MAIL]");

            // send the thing off
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void mailOutputPDF(ArrayList<String> output, ArrayList<PDFFile> pdf_files) {
        try {
            Properties props = System.getProperties();
            props.put("mail.smtp.host", "localhost");
            Session session = Session.getInstance(props, null);

            Message msg = new MimeMessage(session);

            msg.setFrom(new InternetAddress(FROM_ADDR));
            msg.setRecipients(Message.RecipientType.TO,
                              mail_senders);
            if (mail_subject == null) {
                msg.setSubject("SimplerInvoicing mail validator");
            } else {
                if (mail_subject.startsWith("Re: ")) {
                    msg.setSubject(mail_subject);
                } else {
                    msg.setSubject("Re: " + mail_subject);
                }
            }

            msg.setHeader("X-Mailer", "SI Mail test tool");
            msg.setSentDate(new Date());

            StringBuffer sb = new StringBuffer();
            for (String line : output) {
                sb.append(line);
                sb.append("\n");
            }
            
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();

            String text = sb.toString();
            messageBodyPart.setText(text);
            multipart.addBodyPart(messageBodyPart);

            // add any pdfs
            for (PDFFile pdf : pdf_files) {
                messageBodyPart = new MimeBodyPart();
                String filename = pdf.getName();

                DataSource ds = new ByteArrayDataSource(pdf.getByteStream().toByteArray(), "application/pdf");
                //ByteArrayDataSource ds = new ByteArrayDataSource(pdf.getInputStream(), "application/pdf");
                messageBodyPart.setDataHandler(new DataHandler(ds));
                messageBodyPart.setFileName(filename);
                multipart.addBodyPart(messageBodyPart);
            }

            msg.setContent(multipart);
            
            log.logln("[START OF SENT MAIL]");
            logMailMessage(msg);
            log.logln("[END OF SENT MAIL]");

            // send the thing off
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

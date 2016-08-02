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

import java.io.File;
import java.io.IOException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.XMLConstants;
import javax.xml.validation.*;
import org.xml.sax.SAXException;

import java.lang.System;

public class SISchemaValidator {
    net.sf.saxon.s9api.Processor processor;
    net.sf.saxon.s9api.SchemaManager schemaManager;
    
    public SISchemaValidator() {
        processor = new net.sf.saxon.s9api.Processor(false);
        schemaManager = processor.getSchemaManager();
    }
    
    public void validate(Results results, Source xmlFile, String xsdFileName) {
        Source xsdFile = new StreamSource(new File(xsdFileName));
        try {
            SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.setErrorHandler(results.getErrorHandler());
            validator.validate(xmlFile);
        } catch (SAXException e) {
            System.out.println(xmlFile.getSystemId() + " FATAL ERROR");
            System.out.println("Reason: " + e.getLocalizedMessage());
            return;
        } catch (IOException ioe) {
            System.out.println(ioe.toString());
        }
        
    }
}

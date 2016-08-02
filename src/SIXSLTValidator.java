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
import java.io.StringWriter;
import java.io.File;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import net.sf.saxon.Configuration;
import net.sf.saxon.TransformerFactoryImpl;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class SIXSLTValidator {
    net.sf.saxon.s9api.Processor processor;
    net.sf.saxon.s9api.SchemaManager schemaManager;
    
    public SIXSLTValidator() {
        processor = new net.sf.saxon.s9api.Processor(false);
        schemaManager = processor.getSchemaManager();
    }
    
    public void validate(Results results, Source xmlFile, String xsltFileName) {
        TransformerFactory transformFactory = TransformerFactory.newInstance();
        TransformerFactoryImpl transformFactoryImpl = (TransformerFactoryImpl) transformFactory;
        net.sf.saxon.Configuration saxonConfig = transformFactoryImpl.getConfiguration();
        saxonConfig.setRecoveryPolicy(Configuration.RECOVER_SILENTLY);
        transformFactoryImpl.setConfiguration(saxonConfig);

        try {
            Transformer transformer =
            transformFactory.newTransformer(new StreamSource(new File(xsltFileName)));
            StreamResult result=new StreamResult(new StringWriter());
            transformer.transform(xmlFile,result);
            String xmlString=result.getWriter().toString();
            parseXMLString(results, xmlString);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseXMLString(Results results, String xmlString) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new ByteArrayInputStream(xmlString.getBytes()));
            int i;
            NodeList failures = document.getElementsByTagName("svrl:failed-assert");
            for (i = 0; i < failures.getLength(); i++) {
                Node n = failures.item(i);
                Node flag_node = n.getAttributes().getNamedItem("flag");
                if (flag_node != null && "warning".equals(flag_node.getNodeValue())) {
                    results.addWarning(n.getTextContent().trim());
                } else {
                    results.addError(n.getTextContent().trim());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

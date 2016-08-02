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
import java.io.InputStream;
import java.io.IOException;
import java.io.File;
import javax.xml.transform.stream.StreamSource;

class DocumentData {
    String doc_filename;
    String doc_path;
    javax.xml.transform.stream.StreamSource source;
    boolean validated;

    public DocumentData() {
        doc_filename = null;
        doc_path = null;
        source = null;
        validated = false;
    }

    public void setFilename(String filename) {
        File f = new File(filename);
        doc_filename = f.getName();
        doc_path = f.getAbsolutePath();
    }

    public String getFilename() {
        return doc_filename;
    }

    /**
     * Returns the filename, but with a new extension
     */
    public String getFilenameExtRepl(String newExtension) {
        String filename = doc_filename;
        int i = filename.lastIndexOf('.');
        if (i >= 0) {
            filename = filename.substring(0, i);
        }
        if (newExtension.startsWith(".")) {
            return filename + newExtension;
        } else {
            return filename + "." + newExtension;
        }
    }

    public void setValidated(boolean val) {
        validated = val;
    }

    public boolean validated() {
        return validated;
    }

    public void setXMLSourceFromFile(String filename) {
        source = new StreamSource(new File(filename));
    }

    public void setXMLSourceFromStream(java.io.InputStream i) {
        source = new StreamSource(i);
    }

    public void setXMLSourceFromBytes(byte[] b) {
        setXMLSourceFromByteStream(new ByteArrayInputStream(b));
    }

    public void setXMLSourceFromByteStream(ByteArrayInputStream b) {
        source = new StreamSource(b);
    }

    public StreamSource getXMLSource() {
        // reset it if it was a stream
        try {
            if (source.getInputStream() != null) {
                source.getInputStream().reset();
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return source;
    }
}

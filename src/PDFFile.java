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

/**
 * Very simple class to hold the data for a pdf conversion result
 */
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

class PDFFile implements DataSource {
    String pdf_name;
    ByteArrayOutputStream boas;

    public PDFFile(String name, ByteArrayOutputStream bytestream) {
        pdf_name = name;
        boas = bytestream;
    }

    public String getName() {
        return pdf_name;
    }

    public ByteArrayOutputStream getByteStream() {
        return boas;
    }

    public String getContentType() {
        return "application/pdf; name=\"" + getName() + "\"";
    }

    public OutputStream getOutputStream() {
        return boas;
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(boas.toString().getBytes());
    }
}

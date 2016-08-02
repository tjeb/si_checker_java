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

import java.util.ArrayList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

public class ResultHandler implements ErrorHandler {
    ArrayList<String> error_list;
    ArrayList<String> warning_list;
    
    public ResultHandler(ArrayList<String> errors, ArrayList<String> warnings) {
        error_list = errors;
        warning_list = warnings;
    }

    public void error(SAXParseException exception) {
        String msg = exception.toString();
        if (!error_list.contains(msg)) {
            error_list.add("yo" + msg);
        }
    }

    public void	fatalError(SAXParseException exception) {
        String msg = exception.toString();
        if (!error_list.contains(msg)) {
            error_list.add(msg);
        }
        //throw exception;
    }

    public void	warning(SAXParseException exception) {
        String msg = exception.toString();
        if (!warning_list.contains(msg)) {
            warning_list.add(msg);
        }
    }

}

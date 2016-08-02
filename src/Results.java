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

public class Results {
    ArrayList<String> warnings;
    ArrayList<String> errors;
    
    ResultHandler error_handler;
    
    public Results() {
        warnings = new ArrayList<String>();
        errors = new ArrayList<String>();
        
        error_handler = new ResultHandler(errors, warnings);
    }
    
    public void addError(String error) {
        String msg = error.trim();
        if (!errors.contains(msg)) {
            errors.add(msg);
        }
    }

    public void addWarning(String warning) {
        String msg = warning.trim();
        if (!warnings.contains(msg)) {
            warnings.add(msg);
        }
    }
    
    public int getErrorCount() {
        return errors.size();
    }
    
    public int getWarningCount() {
        return warnings.size();
    }

    public ArrayList<String> getErrors() {
        return errors;
    }
    
    public ArrayList<String> getWarnings() {
        return warnings;
    }

    public boolean allOK() {
        return errors.size() == 0 && warnings.size() == 0;
    }

    public boolean noErrors() {
        return errors.size() == 0;
    }
    
    public ResultHandler getErrorHandler() {
        return error_handler;
    }
}

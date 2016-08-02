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
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.BufferedWriter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;

public class Logger {

    PrintWriter logfile;
    
    public Logger(String filename) {
        logfile = null;
        try {
            logfile = new PrintWriter(new BufferedWriter(new FileWriter(new File(filename), true)));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public void closeLog() {
        logfile.close();
    }

    public void log(String msg) {
        if (logfile != null) {
            logfile.write(msg);
        }
    }

    public void logln(String msg) {
        if (logfile != null) {
            log(msg);
            logfile.write("\n");
        }
    }

    public void logDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        logln("[DATE] " + dateFormat.format(date)); //2014/08/06 15:59:48
    }
    
}

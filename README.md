
This is the Java version of the si_checker.py mail tool.

This is an experimental Java version with PDF conversion support (using Apache FOP)

The tool verifies an SI-UBL XML document (or rather, any xml document) against an XSD and an SVRL XSL file (produced by schematron).

Additionally, it has experimental support to convert it to PDF, through the Crane FO files and Apache FOP.


To try it out, run

    make crane-deps

    make

    make pdf


Step one downloads and extracts the conversion XSL files from cranesoftwrights.com

Step two builds the software

Step three runs it and creates a PDF from an example invoice

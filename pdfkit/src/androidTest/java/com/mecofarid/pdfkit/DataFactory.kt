package com.mecofarid.pdfkit

object DataFactory {
    fun getPrintData() = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<title>Page Title</title>\n" +
            "<style>\n" +
            "body {\n" +
            "  background-color: black;\n" +
            "  text-align: center;\n" +
            "  color: white;\n" +
            "  font-family: Arial, Helvetica, sans-serif;\n" +
            "}\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<h1>This is a Heading</h1>\n" +
            "<p>This is a paragraph.</p>\n" +
            "<p>Edit the code in the window to the left, and click \"Run\" to view the result.</p>\n" +
            "<img src=\"avatar.png\" alt=\"Avatar\" style=\"width:200px\">\n" +
            "\n" +
            "</body>\n" +
            "</html>\n"
}
package com.mecofarid.pdfkitdemo

object PrinterFactory {
    val htmlData = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<style>\n" +
            "table {\n" +
            "  border-collapse: collapse;\n" +
            "  width: 100%;\n" +
            "}\n" +
            "\n" +
            "th, td {\n" +
            "  text-align: left;\n" +
            "  padding: 8px;\n" +
            "}\n" +
            "\n" +
            "tr:nth-child(even) {background-color: #f2f2f2;}\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<img src=\"random.jpg\" alt=\"Random image\">\n" +
            "\n" +
            "<h2>Striped Table</h2>\n" +
            "<p>For zebra-striped tables, use the nth-child() selector and add a background-color to all even (or odd) table rows:</p>\n" +
            "\n" +
            "<table>\n" +
            "  <tr>\n" +
            "  <th>First Name</th>\n" +
            "  <th>Last Name</th>\n" +
            "  <th>Points</th>\n" +
            "  </tr>\n" +
            "  <tr>\n" +
            "  <td>Peter</td>\n" +
            "  <td>Griffin</td>\n" +
            "  <td>\$100</td>\n" +
            "  </tr>\n" +
            "  <tr>\n" +
            "  <td>Lois</td>\n" +
            "  <td>Griffin</td>\n" +
            "  <td>\$150</td>\n" +
            "  </tr>\n" +
            "  <tr>\n" +
            "  <td>Joe</td>\n" +
            "  <td>Swanson</td>\n" +
            "  <td>\$300</td>\n" +
            "  </tr>\n" +
            "  <tr>\n" +
            "  <td>Cleveland</td>\n" +
            "  <td>Brown</td>\n" +
            "  <td>\$250</td>\n" +
            "  </tr>\n" +
            "</table>\n" +
            "\n" +
            "</body>\n" +
            "</html>\n"
}
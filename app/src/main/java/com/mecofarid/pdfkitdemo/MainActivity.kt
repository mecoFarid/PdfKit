package com.mecofarid.pdfkitdemo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mecofarid.pdfkit.PdfKit
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getFile("PdfKitDemo_1.pdf").let { outputFile ->
            PdfKit(application.applicationContext).startConversion(
                    url = "https://www.github.com/",
                    outputFile = outputFile,
                    javascriptEnabled = true,
                    onPdfPrintListener = object : PdfKit.OnPdfConversionListener {
                        override fun onError(e: Exception) {
                            println("PDFPRINT remote onError: $e")
                        }

                        override fun onSuccess(pdfFileLocation: File) {
                            println("PDFPRINT remote onSuccess: $outputFile")
                        }
                    }
            )
        }

        getFile("PdfKitDemo_2.pdf").let { outputFile ->
            PdfKit(this).startConversion(
                    baseUrl = getAssetDirectoryPath(),
                    data = PdfConverterDataFactory.htmlData,
                    javascriptEnabled = true,
                    outputFile = outputFile,
                    onPdfPrintListener = object : PdfKit.OnPdfConversionListener {
                        override fun onError(e: Exception) {
                            println("PDFPRINT local onError: $e")
                        }

                        override fun onSuccess(pdfFileLocation: File) {
                            println("PDFPRINT local onSuccess: $outputFile")
                        }
                    }
            )
        }
    }

    private fun getFile(fileName: String): File{
        return File(getExternalFilesDir("pdf"), fileName)
    }

    private fun getAssetDirectoryPath(): String{
        return "file:${File(getExternalFilesDir("images"), "")}/"
    }
}
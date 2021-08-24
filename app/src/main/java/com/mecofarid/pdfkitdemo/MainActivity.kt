package com.mecofarid.pdfkitdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.mecofarid.pdfkit.PdfKit
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        Thread()
//        Handler(HandlerThread("PdfConverterHandlerThread").looper) {
//            getFile("PdfKitDemo_1.pdf").let { outputFile ->
//                PdfKit(application.applicationContext).startConversion(
//                        url = "https://stackoverflow.com/",
//                        outputFile = outputFile,
//                        onPdfPrintListener = object : PdfKit.OnPdfConversionListener {
//                            override fun onError(e: Exception) {
//                                println("PDFPRINT onError: $e")
//                            }
//
//                            override fun onSuccess(pdfFileLocation: File) {
//                                println("PDFPRINT onSuccess: $outputFile")
//                            }
//                        }
//                )
//            }
//        }.start()




        getFile("PdfKitDemo_2.pdf").let { outputFile ->
            PdfKit(this).startConversion(
                    baseUrl = getAssetDirectoryPath(),
                    data = PdfConverterDataFactory.htmlData,
                    outputFile = outputFile,
                    onPdfPrintListener = object : PdfKit.OnPdfConversionListener {
                        override fun onError(e: Exception) {
                            println("PDFPRINT onError: $e")
                        }

                        override fun onSuccess(pdfFileLocation: File) {
                            println("PDFPRINT onSuccess: $outputFile")
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
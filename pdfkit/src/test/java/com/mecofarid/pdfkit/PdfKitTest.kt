package com.mecofarid.pdfkit

import android.content.Context
import android.os.Handler
import android.util.Log
import android.webkit.WebView

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.BeforeClass
import java.io.File

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PdfKitTest {
    companion object {
        @BeforeClass
        fun setUp(){

        }
    }

    @Test
    fun test_startConversion_1() {
        Handler(getContext().mainLooper).post {
            getExternalFilesDir("")?.let { outputFile ->
                PdfKit(getContext()).startConversion(
                        url = "https://developer.android.com/studio?gclid=CjwKCAjw6qqDBhB-EiwACBs6x5VfEKUBKFMxtFeyCYpeE4voPXD5jsq8agQ3mKqrOPjftsy1mPZT7hoCMzoQAvD_BwE&gclsrc=aw.ds",
                        outputFile = outputFile,
                        onPdfPrintListener = object : PdfKit.OnPdfConversionListener {
                            override fun onError(e: Exception) {
                                println("onError: $e")
                            }

                            override fun onSuccess(pdfFileLocation: File) {
                                println("onSuccess: $outputFile")
                                assertEquals(pdfFileLocation, outputFile)
                            }
                        }
                )
            }
        }
    }

    @Test
    fun test_startConversion_2() {
        getExternalFilesDir("PdfKitDemo_2.pdf")?.let {outputFile->
            PdfKit(getContext()).startConversion(
                    baseUrl = null,
                    data = DataFactory.getPrintData(),
                    mimeType = null,
                    encoding = null,
                    historyUrl = null,
                    outputFile = outputFile,
                    onPdfPrintListener = object : PdfKit.OnPdfConversionListener {
                        override fun onError(e: Exception) {
                            Log.d("TAG", "onError: $e")
                        }

                        override fun onSuccess(pdfFileLocation: File) {
                            assertEquals(pdfFileLocation, outputFile)
                        }
                    }
            )
        }
    }

    @Test
    fun test_startConversion_3() {
        getExternalFilesDir("PdfKitDemo_3.pdf")?.let {outputFile->
            PdfKit(getContext()).startConversion(
                    webView = WebView(getContext()),
                    outputFile = outputFile,
                    onPdfPrintListener = object : PdfKit.OnPdfConversionListener {
                        override fun onError(e: Exception) {
                            Log.d("TAG", "onError: $e")
                        }

                        override fun onSuccess(pdfFileLocation: File) {
                            assertEquals(pdfFileLocation, outputFile)
                        }
                    }
            )
        }
    }

    private fun getExternalFilesDir(fileName: String): File?{
        return getContext().getExternalFilesDir(fileName)
    }

    private fun getContext(): Context =
            ApplicationProvider.getApplicationContext()
}
package com.mecofarid.pdfkit

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import android.webkit.WebView
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.BeforeClass
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class PdfKitAndroidTest {
    companion object {
        @BeforeClass
        fun setUp(){

        }
    }

    @Test
    fun test_startConversion_1() {
        getExternalFilesDir("PdfKitDemo_1.pdf")?.let { outputFile ->
            println(println("Logger a-00"))
            PdfKit(getContext()).startConversion(
                    url = "https://stackoverflow.com/",
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

    @Test
    fun test_startConversion_2() {
            getExternalFilesDir("PdfKitDemo_2.pdf")?.let { outputFile ->
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

    private fun getExternalFilesDir(fileName: String): File{
        return File(getContext().getExternalFilesDir("pdf"), fileName).apply {
            createNewFile()
        }
    }

    private fun getContext(): Context =
            ApplicationProvider.getApplicationContext()

}
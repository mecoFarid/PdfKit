package com.mecofarid.pdfkit

import android.content.Context
import android.os.Handler
import android.print.PrintAttributes
import android.webkit.WebView
import android.print.InternalPdfConverter
import android.util.Log
import java.io.File

private const val PDF_EXTENSION = ".pdf"
class PdfKit(private val context: Context) {
    fun startConversion(
            url: String,
            headers: Map<String, String> = hashMapOf(),
            printAttributes: PrintAttributes? = null,
            outputFile: File,
            onPdfPrintListener: OnPdfConversionListener
    ) {

        Handler(context.mainLooper).post {
            WebView(context).also { webView ->
                startConversion(webView = webView, printAttributes = printAttributes, outputFile = outputFile, onPdfPrintListener = onPdfPrintListener)
                webView.loadUrl(url, headers)
            }
        }
    }

    fun startConversion(
            baseUrl: String? = null,
            data: String,
            mimeType: String? = null,
            encoding: String? = null,
            historyUrl: String? = null,
            printAttributes: PrintAttributes? = null,
            outputFile: File,
            onPdfPrintListener: OnPdfConversionListener
    ) {
        Handler(context.mainLooper).post {
            WebView(context).also { webView ->
                startConversion(webView = webView, printAttributes = printAttributes, outputFile = outputFile, onPdfPrintListener = onPdfPrintListener)
                webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
            }
        }
    }

    fun startConversion(
            webView: WebView,
            printAttributes: PrintAttributes? = null,
            outputFile: File,
            onPdfPrintListener: OnPdfConversionListener
    ) {
        if (!outputFile.isPdfFile()) {
            onPdfPrintListener.onError(IllegalArgumentException("Output file must end with \"$PDF_EXTENSION\" extension"))
            return
        }
        InternalPdfConverter.instance.startConversion(webView = webView, printAttributes = printAttributes, outputFile = outputFile, onPdfPrintListener = onPdfPrintListener)
    }

    interface OnPdfConversionListener {
        fun onError(e: Exception)
        fun onSuccess(pdfFileLocation: File)
    }

    private fun File.isPdfFile() =
            this.name.endsWith(PDF_EXTENSION, true)

}
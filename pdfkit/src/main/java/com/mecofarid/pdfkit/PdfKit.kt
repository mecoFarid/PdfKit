package com.mecofarid.pdfkit

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.print.PrintAttributes
import android.webkit.WebView
import android.print.InternalPdfConverter
import android.provider.Contacts
import android.util.Log
import com.mecofarid.logger.Logger
import java.io.File

private const val PDF_EXTENSION = "pdf"
private const val TAG = "PdfKit_TAG"
class PdfKit(private val context: Context) {
    private val mainThreadHandler by lazy {
        Handler(Looper.getMainLooper())
    }
    fun startConversion(
            url: String,
            headers: Map<String, String> = hashMapOf(),
            javascriptEnabled: Boolean = false,
            printAttributes: PrintAttributes? = null,
            outputFile: File,
            onPdfPrintListener: OnPdfConversionListener
    ) {
        Logger.d(TAG, "startConversion with: " +
                "URL:$url " +
                "Headers:$headers " +
                "PrintAttributes:$printAttributes " +
                "OutputFile:$outputFile" +
                "OnPdfConversionListener:$onPdfPrintListener"
        )
        startConversion(
            printAttributes = printAttributes,
            javascriptEnabled = javascriptEnabled,
            outputFile = outputFile,
            onPdfPrintListener = onPdfPrintListener,
            object : WebViewCreatedListener{
                override fun onWebViewCreated(webView: WebView) {
                    webView.loadUrl(url, headers)
                }
            }
        )
    }

    fun startConversion(
            baseUrl: String? = null,
            data: String,
            mimeType: String? = null,
            encoding: String? = null,
            historyUrl: String? = null,
            printAttributes: PrintAttributes? = null,
            javascriptEnabled: Boolean = false,
            outputFile: File,
            onPdfPrintListener: OnPdfConversionListener
    ) {
        Logger.d(TAG, "startConversion with: " +
                "BaseURL:$baseUrl " +
                "Data:$data " +
                "MimeType:$mimeType " +
                "Encoding:$encoding " +
                "HistoryURL:$historyUrl " +
                "PrintAttributes:$printAttributes " +
                "OutputFile:$outputFile" +
                "OnPdfConversionListener:$onPdfPrintListener"
        )

        startConversion(
            printAttributes = printAttributes,
            javascriptEnabled = javascriptEnabled,
            outputFile = outputFile,
            onPdfPrintListener = onPdfPrintListener,
            object : WebViewCreatedListener{
                override fun onWebViewCreated(webView: WebView) {
                    webView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl)
                }
            }
        )
    }

    private fun startConversion(
            printAttributes: PrintAttributes? = null,
            javascriptEnabled: Boolean,
            outputFile: File,
            onPdfPrintListener: OnPdfConversionListener,
            onWebViewCreatedListener: WebViewCreatedListener
    ) {

        Logger.d(TAG, "startConversion with: " +
                "PrintAttributes:$printAttributes " +
                "OutputFile:$outputFile" +
                "OnPdfConversionListener:$onPdfPrintListener"
        )

        if (!outputFile.isPdfFile()) {
            onPdfPrintListener.onError(IllegalArgumentException("Output file must end with \"$PDF_EXTENSION\" extension"))
            return
        }


        //Always post to main thread. Only WebView methods will be called on UI thread, conversion will be
        // on non-UI thread check [InternalPdfConverter#startConversionInternal()] for details
        mainThreadHandler.post {
            WebView(context).also { webView ->
                InternalPdfConverter.startConversion(
                        webView = webView,
                        printAttributes = printAttributes,
                        javascriptEnabled = javascriptEnabled,
                        outputFile = outputFile,
                        onPdfPrintListener = onPdfPrintListener
                )
                onWebViewCreatedListener.onWebViewCreated(webView)
            }
        }
    }

    interface OnPdfConversionListener {
        fun onError(e: Exception)
        fun onSuccess(pdfFileLocation: File)
    }

    private interface WebViewCreatedListener{
        fun onWebViewCreated(webView: WebView)
    }

    private fun File.isPdfFile() =
            this.extension.equals(other = PDF_EXTENSION, ignoreCase = true)
}
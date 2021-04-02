package com.mecofarid.pdfkit

import android.content.Context
import android.os.Build
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentAdapter.LayoutResultCallback
import android.print.PrintDocumentAdapter.WriteResultCallback
import android.print.PrintDocumentInfo
import android.webkit.WebView
import android.webkit.WebViewClient
import java.io.File
import java.io.FileDescriptor

private const val ERROR_WEBVIEW_LAYOUT_CANCELLED = "Error webview layout cancelled"
private const val ERROR_WEBVIEW_WRITE_CANCELLED = "Error webview write cancelled"
class PdfKit(private val context: Context) {
    public fun startConversion(){}

    private fun startConversionInternal(webView: WebView, outputFile: File, printAttributes: PrintAttributes?, onPdfPrintListener: OnPdfConversionListener){
        webView.webViewClient = object: WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                val printDocumentAdapter = getPrintAdapter(webView = webView)
                printDocumentAdapter.onLayout(null, printAttributes, null, object : LayoutResultCallback() {
                    override fun onLayoutCancelled() {
                        super.onLayoutCancelled()
                        onPdfPrintListener.onError(Exception(ERROR_WEBVIEW_LAYOUT_CANCELLED))
                    }

                    override fun onLayoutFailed(error: CharSequence?) {
                        super.onLayoutFailed(error)
                        onPdfPrintListener.onError(Exception(error.toString()))
                    }
                    override fun onLayoutFinished(info: PrintDocumentInfo, changed: Boolean) {
                        val fileDescriptorResult = getFileDescriptorResult(file = outputFile)
                        if (!fileDescriptorResult.isSuccess())
                            onPdfPrintListener.onError(fileDescriptorResult.getFailureData())

                        printDocumentAdapter.onWrite(arrayOf(PageRange.ALL_PAGES), fileDescriptorResult.getSuccessData(), null, object : WriteResultCallback() {
                            override fun onWriteCancelled() {
                                super.onWriteCancelled()
                                onPdfPrintListener.onError(Exception(ERROR_WEBVIEW_WRITE_CANCELLED))
                            }

                            override fun onWriteFailed(error: CharSequence) {
                                super.onWriteFailed(error)
                                onPdfPrintListener.onError(Exception(error.toString()))
                            }

                            override fun onWriteFinished(pages: Array<PageRange>) {
                                super.onWriteFinished(pages)
                                onPdfPrintListener.onSuccess()
                            }
                        })
                    }
                }, null)
            }
        }
    }

    private fun getPrintAdapter(webView: WebView): PrintDocumentAdapter =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) webView.createPrintDocumentAdapter("Document")
            else  webView.createPrintDocumentAdapter()

    private fun getFileDescriptorResult(file: File): FileDescriptorResult {
        var fileDescriptor: ParcelFileDescriptor? = null
        var exception: Exception? = null
        try {
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_WRITE)
        } catch (e: Exception) {
            exception = e
        }
        return FileDescriptorResult(fileDescriptor, exception)
    }

    private class FileDescriptorResult(
            private val fileDescriptor: ParcelFileDescriptor?,
            private val exception: Exception?
    ){
        fun isSuccess() = fileDescriptor != null
        fun getSuccessData(): ParcelFileDescriptor = fileDescriptor!!
        fun getFailureData(): Exception = exception!!
    }

    interface OnPdfConversionListener{
        fun onError(e: Exception)
        fun onSuccess(pdfFileLocation: File)
    }
}
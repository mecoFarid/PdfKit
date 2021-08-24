package android.print

import android.os.Build
import android.os.HandlerThread
import android.os.ParcelFileDescriptor
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mecofarid.logger.Logger
import com.mecofarid.pdfkit.PdfKit
import com.mecofarid.pdfkit.ConversionHandler
import java.io.File

private const val ERROR_WEBVIEW_LAYOUT_CANCELLED = "Error webview layout cancelled"
private const val ERROR_WEBVIEW_WRITE_CANCELLED = "Error webview write cancelled"
private const val TAG = "InternalPdfConverter";
internal object InternalPdfConverter {
    private fun startConversionInternal(webView: WebView, printAttributes: PrintAttributes, onPdfPrintListener: PdfKit.OnPdfConversionListener, outputFile: File) {
        Logger.d(TAG, "startConversionInternal: ")
        webView.clearCache(true)
        webView.clearHistory()
        webView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                Logger.d(TAG, "onPageFinished: ")
                super.onPageFinished(view, url)
                val printDocumentAdapter = getPrintAdapter(webView = webView)
                printDocumentAdapter.onLayout(
                        null,
                        printAttributes,
                        null,
                        object : PrintDocumentAdapter.LayoutResultCallback() {
                            override fun onLayoutCancelled() {
                                Logger.d(TAG, "onLayoutCancelled: ")
                                super.onLayoutCancelled()
                                onPdfPrintListener.onError(
                                        RuntimeException(
                                                ERROR_WEBVIEW_LAYOUT_CANCELLED
                                        )
                                )
                            }

                            override fun onLayoutFailed(error: CharSequence?) {
                                Logger.d(TAG, "onLayoutFailed: $error")
                                super.onLayoutFailed(error)
                                onPdfPrintListener.onError(RuntimeException(error.toString()))
                            }

                            override fun onLayoutFinished(info: PrintDocumentInfo, changed: Boolean) {
                                Logger.d(TAG, "onLayoutFinished: ")
                                val fileDescriptorResult = getFileDescriptorResult(file = outputFile)
                                if (!fileDescriptorResult.isSuccess()) {
                                    onPdfPrintListener.onError(fileDescriptorResult.getFailureData())
                                    return
                                }

                                printDocumentAdapter.onWrite(
                                        arrayOf(PageRange.ALL_PAGES),
                                        fileDescriptorResult.getSuccessData(),
                                        null,
                                        object : PrintDocumentAdapter.WriteResultCallback() {
                                            override fun onWriteCancelled() {
                                                Logger.d(TAG, "onWriteCancelled: ")
                                                super.onWriteCancelled()
                                                onPdfPrintListener.onError(
                                                        RuntimeException(
                                                                ERROR_WEBVIEW_WRITE_CANCELLED
                                                        )
                                                )
                                            }

                                            override fun onWriteFailed(error: CharSequence) {
                                                Logger.d(TAG, "onWriteFailed: $error")
                                                super.onWriteFailed(error)
                                                onPdfPrintListener.onError(RuntimeException(error.toString()))
                                            }

                                            override fun onWriteFinished(pages: Array<PageRange>) {
                                                Logger.d(TAG, "onWriteFinished: ")
                                                super.onWriteFinished(pages)
                                                onPdfPrintListener.onSuccess(outputFile)
                                            }
                                        })
                            }
                        },
                        null
                )
            }
        }
    }

    fun startConversion(
            webView: WebView,
            printAttributes: PrintAttributes?,
            outputFile: File,
            onPdfPrintListener: PdfKit.OnPdfConversionListener
    ) {
        val nonNullPrintAttributes = printAttributes ?: PrintAttributes.Builder()
                .setResolution(PrintAttributes.Resolution("RESOLUTION_ID", "RESOLUTION_LABEL", 600, 848))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .build()

        startConversionInternal(webView, nonNullPrintAttributes, onPdfPrintListener, outputFile)
    }

    private fun getFileDescriptorResult(file: File): FileDescriptorResult {
        var fileDescriptor: ParcelFileDescriptor? = null
        var exception: Exception? = null
        try {
            file.createNewFile()
            fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_TRUNCATE or ParcelFileDescriptor.MODE_READ_WRITE)
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

    private fun getPrintAdapter(webView: WebView): PrintDocumentAdapter =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) webView.createPrintDocumentAdapter("Document")
            else  webView.createPrintDocumentAdapter()

}
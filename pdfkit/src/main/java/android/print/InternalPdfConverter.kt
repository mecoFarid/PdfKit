package android.print

import android.os.Build
import android.os.Handler
import android.os.ParcelFileDescriptor
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.mecofarid.pdfkit.PdfKit
import java.io.File

const val ERROR_WEBVIEW_LAYOUT_CANCELLED = "Error webview layout cancelled"
const val ERROR_WEBVIEW_WRITE_CANCELLED = "Error webview write cancelled"
class InternalPdfConverter: Runnable {
    private lateinit var mWebView: WebView
    private lateinit var mOnPdfPrintListener: PdfKit.OnPdfConversionListener
    private lateinit var mOutputFile: File

    companion object {
        private lateinit var sInstance: InternalPdfConverter

        @get:Synchronized
        val instance: InternalPdfConverter
            get() {
                if (this::sInstance.isInitialized.not()) sInstance = InternalPdfConverter()
                return sInstance
            }
    }

    private var mPrintAttributes: PrintAttributes? =
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) null
            else PrintAttributes.Builder()
                .setResolution(PrintAttributes.Resolution("RESOLUTION_ID", "RESOLUTION_LABEL", 600, 848))
                .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                .build()

    override fun run() {
        mWebView.clearCache(true)
        mWebView.clearHistory()
        mWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                val printDocumentAdapter = getPrintAdapter(webView = mWebView)
                printDocumentAdapter.onLayout(null, mPrintAttributes, null, object : PrintDocumentAdapter.LayoutResultCallback() {
                    override fun onLayoutCancelled() {
                        super.onLayoutCancelled()
                        mOnPdfPrintListener.onError(RuntimeException(ERROR_WEBVIEW_LAYOUT_CANCELLED))
                    }

                    override fun onLayoutFailed(error: CharSequence?) {
                        super.onLayoutFailed(error)
                        mOnPdfPrintListener.onError(RuntimeException(error.toString()))
                    }

                    override fun onLayoutFinished(info: PrintDocumentInfo, changed: Boolean) {
                        val fileDescriptorResult = getFileDescriptorResult(file = mOutputFile)
                        if (!fileDescriptorResult.isSuccess()) {
                            mOnPdfPrintListener.onError(fileDescriptorResult.getFailureData())
                            return
                        }

                        printDocumentAdapter.onWrite(arrayOf(PageRange.ALL_PAGES), fileDescriptorResult.getSuccessData(), null, object : PrintDocumentAdapter.WriteResultCallback() {
                            override fun onWriteCancelled() {
                                super.onWriteCancelled()
                                mOnPdfPrintListener.onError(RuntimeException(ERROR_WEBVIEW_WRITE_CANCELLED))
                            }

                            override fun onWriteFailed(error: CharSequence) {
                                super.onWriteFailed(error)
                                mOnPdfPrintListener.onError(RuntimeException(error.toString()))
                            }

                            override fun onWriteFinished(pages: Array<PageRange>) {
                                super.onWriteFinished(pages)
                                mOnPdfPrintListener.onSuccess(mOutputFile)
                            }
                        })
                    }
                }, null)
            }
        }
    }

    fun startConversion(
            webView: WebView,
            printAttributes: PrintAttributes?,
            outputFile: File,
            onPdfPrintListener: PdfKit.OnPdfConversionListener
    ) {
        mWebView = webView
        if (printAttributes != null){
            mPrintAttributes = printAttributes
        }
        mOutputFile = outputFile
        mOnPdfPrintListener = onPdfPrintListener

        Handler(webView.context.mainLooper).post(this)
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
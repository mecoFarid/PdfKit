package com.mecofarid.pdfkit

import android.content.Context
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import android.webkit.WebView

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.BeforeClass
import org.junit.runners.JUnit4
import java.io.File
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(JUnit4::class)
class PdfKitTest {
    companion object {
        @BeforeClass
        fun setUp() {

        }
    }

    @Test
    fun test_startConversion_1() {
        for (i in 0..100) {
            ConversionHandler.execute {
                Log.d("TAG", "test_startConversion_1: ${Thread.currentThread().id}")
            }
        }
    }

    internal object ConversionHandler: HandlerThread("NAME"){

        private const val CORE_POOL_SIZE = 5
        private const val MAXIMUM_POOL_SIZE = 20
        private const val KEEP_ALIVE_SECONDS = 3L
        private val handler by lazy {
            object : Handler(looper) {
                override fun handleMessage(msg: Message) {
                    threadPoolExecutor.execute(msg.obj as Runnable)
                }
            }
        }

        private val threadPoolExecutor = ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAXIMUM_POOL_SIZE,
                KEEP_ALIVE_SECONDS,
                TimeUnit.SECONDS,
                LinkedBlockingQueue()
        ).also {
            it.allowCoreThreadTimeOut(true)
        }

        internal fun execute(runnable: Runnable){
            if (!isAlive)
                start()
            handler.post(runnable)
        }
    }
}
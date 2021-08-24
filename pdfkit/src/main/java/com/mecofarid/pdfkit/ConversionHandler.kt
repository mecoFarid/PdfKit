package com.mecofarid.pdfkit

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

private const val NAME = "ConversionHandler"
internal object ConversionHandler: HandlerThread(NAME){

    /**
     * As recommended in https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/os/AsyncTask.java
     */
    private const val CORE_POOL_SIZE = 1
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
    )

    internal fun execute(runnable: Runnable){
        if (!isAlive)
            start()
        handler.post(runnable)
    }
}
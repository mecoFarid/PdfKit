package android.print

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import android.util.Log
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadFactory
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

internal object InternalConversionHandler{

    private const val CORE_POOL_SIZE = 5
    private const val MAXIMUM_POOL_SIZE = 20
    private const val KEEP_ALIVE_SECONDS = 3L

    private val threadPoolExecutor = ThreadPoolExecutor(
            CORE_POOL_SIZE,
            MAXIMUM_POOL_SIZE,
            KEEP_ALIVE_SECONDS,
            TimeUnit.SECONDS,
            LinkedBlockingQueue()
    ).also {
        it.allowCoreThreadTimeOut(true)
    }

    fun execute(runnable: Runnable){
        threadPoolExecutor.execute(runnable)
    }
}
package dev.spravedlivo.flibuster.network

import android.graphics.BitmapFactory
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


object FlibustaHelper {
    private var url: String = "https://flibusta.is"
    private val client = OkHttpClient()

    fun request(query: String, onResponse: (Response) -> Unit, onError: (String) -> Unit) {
        val request = Request.Builder()
            .url("$url$query")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                onError(e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        onError("${response.code} ${response.message}")
                    }
                    else onResponse(response)
                }
            }
        })
    }
    // keep in mind that body is not availible after resume
    private suspend fun requestInScope(query: String, boundOnError: (String) -> Unit): String = suspendCoroutine { cont ->
        request(query, {
            cont.resume(it.body!!.string()) }, {
            boundOnError(it) })
    }

    fun requestScope(onError: (String) -> Unit, block: (suspend (String) -> String) -> Unit) {
        block {
            return@block requestInScope(it, onError)
        }
    }
    suspend fun imageFromUrl(localUrl: String, onError: (String) -> Unit) = suspendCoroutine {continuation ->
        request(localUrl, {
            val byteArray = it.body!!.bytes()
            continuation.resume(BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size))
        }, { onError(it) })
    }
}


package dev.spravedlivo.flibuster.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dev.spravedlivo.flibuster.Settings
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException
import java.io.InputStream
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

enum class ResponseType {
    BYTES,
    STRING;
}

data class ApiResponse(val response: Response?, val error: String?, val responseType: ResponseType) {
    var responseBodyString: String? = null
    var responseBodyBytes: ByteArray? = null

    init {
        if (response?.body != null) {
            when(responseType) {
                ResponseType.BYTES -> responseBodyBytes = response.body!!.bytes()
                ResponseType.STRING -> responseBodyString = response.body!!.string()
            }
        }
    }
}

object FlibustaHelper {
    private val client = OkHttpClient()

    suspend fun request(context: Context, query: String, responseType: ResponseType = ResponseType.STRING): ApiResponse {
        val url = Settings.readOrDefault(context, "flibusta_url") ?: return ApiResponse(null, "No flibusta url provided.", responseType)
        val request = Request.Builder()
            .url("$url$query")
            .build()

        return suspendCoroutine { continuation ->
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    continuation.resume(ApiResponse(null, e.message.toString(), responseType))
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        if (!response.isSuccessful) {
                            continuation.resume(ApiResponse(null, "${response.code} ${response.message}", responseType))
                        }
                        else continuation.resume(ApiResponse(response, null, responseType))
                    }
                }
            })
        }

    }
    // keep in mind that body is not availible after resume

    suspend fun imageFromUrl(context: Context, localUrl: String, onError: (String) -> Unit): Bitmap? {
        val resp = request(context, localUrl, ResponseType.BYTES)
        if (resp.error != null) { onError(resp.error); return null }
        val byteArray = resp.responseBodyBytes!!
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}


package dev.spravedlivo.flibuster.network

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.documentfile.provider.DocumentFile
import dev.spravedlivo.flibuster.Settings

suspend fun downloadBook(context: Context, url: String, fileName: String) {
    val resp = FlibustaHelper.request(context, url, ResponseType.BYTES)
    if (resp.error != null) {
        Toast.makeText(context, resp.error, Toast.LENGTH_SHORT).show()
        return
    }
    val folder = Settings.read(context, "download_folder")
    if (folder.isNullOrBlank()) {
        Toast.makeText(context, "Please set download folder in settings.", Toast.LENGTH_SHORT).show()
        return
    }
    val uri = DocumentFile.fromTreeUri(context, Uri.parse(folder))
    val created = uri?.createFile("", fileName)
    if (created == null) {
        Toast.makeText(context, "Please set download folder once again in settings.",
            Toast.LENGTH_SHORT
        ).show()
        return
    }
    val stream = context.contentResolver.openOutputStream(created.uri, "w")
    stream?.apply {
        stream.write(resp.responseBodyBytes!!)
        stream.flush()
        stream.close()
    }
    Toast.makeText(context, "Download finished!", Toast.LENGTH_SHORT).show()

}
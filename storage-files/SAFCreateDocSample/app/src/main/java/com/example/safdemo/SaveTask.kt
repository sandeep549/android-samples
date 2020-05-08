package com.example.safdemo

import android.content.ContentResolver
import android.net.Uri
import android.os.AsyncTask
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class SaveTask : AsyncTask<Void, Void, Void> {
    private val remoteUrl: String?
    private val contentResolver: ContentResolver
    private val uriToStore: Uri
    private val progressBar: ProgressBar

    constructor(
        remoteUrl: String,
        contentResolver: ContentResolver,
        uriToStore: Uri,
        progressBar: ProgressBar
    ) {
        this.contentResolver = contentResolver
        this.remoteUrl = remoteUrl
        this.uriToStore = uriToStore
        this.progressBar = progressBar
    }

    override fun onPreExecute() {
        super.onPreExecute()
        progressBar.visibility = View.VISIBLE
    }

    override fun doInBackground(vararg params: Void?): Void? {
        try {
            val MEGABYTE = 1024 * 1024;
            val url = URL(remoteUrl)
            val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
            urlConnection.connect()
            val inputStream: InputStream = urlConnection.getInputStream()

            val pfd = this.contentResolver.openFileDescriptor(uriToStore, "w")
            val fileOutputStream = FileOutputStream(pfd!!.fileDescriptor)

            val buffer = ByteArray(MEGABYTE)
            var bufferLength = 0
            while (inputStream.read(buffer).also { bufferLength = it } > 0) {
                fileOutputStream.write(buffer, 0, bufferLength)
            }
            fileOutputStream.close()
            pfd.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        // if(isCancelled) {
        //
        // }else{
            Toast.makeText(progressBar.context, "Download is complete", Toast.LENGTH_LONG).show()
            progressBar.visibility = View.INVISIBLE
        // }
    }
}
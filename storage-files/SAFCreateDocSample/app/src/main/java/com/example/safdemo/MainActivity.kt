package com.example.safdemo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileDescriptor

class MainActivity : AppCompatActivity() {

    private var saveTask: SaveTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createDocButton1.setOnClickListener {
            val createIntent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "application/pdf"
                putExtra(Intent.EXTRA_TITLE, "newFile.pdf")
            }
            startActivityForResult(createIntent, CREATE_DOC_REQUEST_CODE1)
        }
        createDocButton2.setOnClickListener {
            Toast.makeText(
                this,
                "Opps! DownloadManager only support file uri scheme and SAF return content uri scheme.",
                Toast.LENGTH_LONG
            ).show()
        }

        openDocButton1.setOnClickListener {
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "*/*"
            }
            startActivityForResult(intent, OPEN_DOC_REQUEST_CODE)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG,"OS API Version:${Build.VERSION.SDK_INT}")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            printSelectedFileInfo(data)
            when (requestCode) {
                CREATE_DOC_REQUEST_CODE1 -> {
                    data?.data?.let {
                        saveTask = SaveTask(PDF_URL, this.contentResolver, it, progressBar)
                        saveTask?.execute()
                    }
                }
            }
        }
    }

    private fun printSelectedFileInfo(data: Intent?) {
        Log.d(TAG, "uri: ${data?.data}")
        Log.d(TAG, "type: ${contentResolver.getType(data!!.data!!)}")

        data.data?.let { returnUri ->
            printUri(returnUri)
            contentResolver.query(returnUri, null, null, null, null)
        }?.use { cursor ->
            /*
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            cursor.moveToFirst()
            val name = cursor.getString(nameIndex)
            val size = cursor.getLong(sizeIndex).toString()
            Log.d(TAG, "name: $name, size:$size")
        }
    }

    private fun printUri(uri: Uri) {
        val parcelFileDescriptor: ParcelFileDescriptor =
            contentResolver.openFileDescriptor(uri, "r")!!
        val fileDescriptor: FileDescriptor = parcelFileDescriptor.fileDescriptor
        Log.d(TAG, "fileDescriptor:${fileDescriptor}")
    }

    override fun onDestroy() {
        super.onDestroy()
        // saveTask?.let {
        //     if(!it.isCancelled) it.cancel(true)
        // }
    }

    companion object {
        val TAG = "MainActivity"
        val CREATE_DOC_REQUEST_CODE1 = 10
        val CREATE_DOC_REQUEST_CODE2 = 11
        val OPEN_DOC_REQUEST_CODE = 12
        val OPEN_DOC_TREE_REQUEST_CODE = 13
        val SAVE_REQUEST_CODE = 14
        val PDF_URL = "https://maven.apache.org/maven-1.x/maven.pdf"
    }
}

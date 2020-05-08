package com.example.safdemo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private var saveTask: SaveTask? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        createDocButton1.setOnClickListener {
            triggerCreateDocumentIntent(CREATE_DOC_REQUEST_CODE1)
        }
        createDocButton2.setOnClickListener {
            Toast.makeText(
                this,
                "Opps! DownloadManager only support file uri scheme and SAF return content uri scheme.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun triggerCreateDocumentIntent(resquestCode: Int) {
        val createIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
        createIntent.addCategory(Intent.CATEGORY_OPENABLE)
        createIntent.type = "application/pdf"
        createIntent.putExtra(Intent.EXTRA_TITLE, "newFile.pdf")
        startActivityForResult(createIntent, resquestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CREATE_DOC_REQUEST_CODE1 -> {
                    println("data: ${data?.data}")
                    data?.data?.let {
                        saveTask = SaveTask(PDF_URL, this.contentResolver, it, progressBar)
                        saveTask?.execute()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // saveTask?.let {
        //     if(!it.isCancelled) it.cancel(true)
        // }
    }

    companion object {
        val CREATE_DOC_REQUEST_CODE1 = 10
        val CREATE_DOC_REQUEST_CODE2 = 11
        val OPEN_DOC_REQUEST_CODE = 12
        val OPEN_DOC_TREE_REQUEST_CODE = 13
        val SAVE_REQUEST_CODE = 14
        val PDF_URL = "https://maven.apache.org/maven-1.x/maven.pdf"
    }
}

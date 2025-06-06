package com.nhm.distributor.fcm

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import java.io.File


class DownloadCompleteReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if(id != -1L) {
                println("Download with ID $id finished!")
                Toast.makeText(context, "context.resources.getString(R.string.text_file_download_successfully",
                    Toast.LENGTH_SHORT).show()
//                openDownloadedAttachment(context, id);
            }
        }
    }

    @SuppressLint("Range")
    private fun openDownloadedAttachment(context: Context?, downloadId: Long) {
        val downloadManager = context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            val downloadLocalUri =
                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI))
            val downloadMimeType =
                cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_MEDIA_TYPE))
            if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && downloadLocalUri != null) {
//                openDownloadedAttachment(context, Uri.parse(downloadLocalUri), downloadMimeType)
                Log.e("TAG", "downloadLocalUri "+downloadLocalUri)
                Log.e("TAG", "downloadMimeType "+downloadMimeType)

//                val file: File = File(
//                    Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + filename
//                )
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.fromFile(File(downloadLocalUri)), "application/pdf")
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context?.startActivity(intent)
            }
        }
        cursor.close()
    }
}
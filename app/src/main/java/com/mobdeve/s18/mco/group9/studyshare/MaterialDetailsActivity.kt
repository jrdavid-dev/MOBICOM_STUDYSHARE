package com.mobdeve.s18.mco.group9.studyshare

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mobdeve.s18.mco.group9.studyshare.databinding.MaterialDetailsBinding
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MaterialDetailsActivity : AppCompatActivity() {

    private val STORAGE_PERMISSION_CODE = 100
    private var pendingFileUrl: String? = null
    private var pendingFileName: String? = null
    private var downloadId: Long = -1
    private lateinit var viewBinding: MaterialDetailsBinding

    // BroadcastReceiver to listen for download completion
    private val downloadCompleteReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (id == downloadId) {
                val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query().setFilterById(downloadId)
                val cursor = downloadManager.query(query)

                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(columnIndex)

                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            // Re-enable button and update UI
                            viewBinding.downloadBtn.isEnabled = true
                            viewBinding.downloadTv.text = "Download Material"
                            viewBinding.downloadIv.setImageResource(android.R.drawable.stat_sys_download_done)

                            Toast.makeText(
                                this@MaterialDetailsActivity,
                                "Downloaded successfully!",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                        DownloadManager.STATUS_FAILED -> {
                            // Re-enable button and update UI
                            viewBinding.downloadBtn.isEnabled = true
                            viewBinding.downloadTv.text = "Download Material"
                            viewBinding.downloadIv.setImageResource(android.R.drawable.stat_sys_download_done)

                            Toast.makeText(
                                this@MaterialDetailsActivity,
                                "Download failed",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
                cursor.close()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = MaterialDetailsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val materialName = intent.getStringExtra(IntentKeys.MATERIAL_NAME.name)
        val materialType = intent.getStringExtra(IntentKeys.MATERIAL_TYPE.name)
        val materialAuthor = intent.getStringExtra(IntentKeys.MATERIAL_AUTHOR.name)
        val materialDate = intent.getStringExtra(IntentKeys.MATERIAL_DATE.name)
        val colorIcon = intent.getStringExtra(IntentKeys.COLOR_ICON.name)
        val fileUrl = intent.getStringExtra(IntentKeys.FILE_URL.name)
        val fileName = intent.getStringExtra(IntentKeys.FILE_NAME.name)

        viewBinding.materialNameTv.text = materialName
        viewBinding.materialDetailsTypeTv.text = materialType
        viewBinding.materialDetailsAuthorTv.text = materialAuthor
        viewBinding.materialDetailsDateTv.text = materialDate
        viewBinding.colormMaterialDetailsFrame.backgroundTintList = ColorStateList.valueOf(Color.parseColor(colorIcon))

        // Register the BroadcastReceiver
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                downloadCompleteReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            registerReceiver(
                downloadCompleteReceiver,
                IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            )
        }

        viewBinding.downloadBtn.setOnClickListener {
            if (fileUrl.isNullOrEmpty() || fileName.isNullOrEmpty()) {
                Toast.makeText(this, "File information not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            pendingFileUrl = fileUrl
            pendingFileName = fileName

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                downloadFileToDevice(fileUrl, fileName)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    downloadFileToDevice(fileUrl, fileName)
                } else {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                }
            } else {
                downloadFileToDevice(fileUrl, fileName)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (pendingFileUrl != null && pendingFileName != null) {
                    downloadFileToDevice(pendingFileUrl!!, pendingFileName!!)
                }
            } else {
                Toast.makeText(
                    this,
                    "Storage permission denied. Cannot download file.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun downloadFileToDevice(fileUrl: String, fileName: String) {
        // Disable button and show downloading state
        viewBinding.downloadBtn.isEnabled = false
        viewBinding.downloadTv.text = "Downloading..."
        viewBinding.downloadIv.setImageResource(R.drawable.load)

        val request = DownloadManager.Request(Uri.parse(fileUrl))
            .setTitle(fileName)
            .setDescription("Downloading material...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadId = downloadManager.enqueue(request)

        Toast.makeText(this, "Downloading $fileName...", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Unregister the receiver to avoid memory leaks
        unregisterReceiver(downloadCompleteReceiver)
    }
}
package com.mobdeve.s18.mco.group9.studyshare

import android.app.DownloadManager
import android.content.Context
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewBinding: MaterialDetailsBinding = MaterialDetailsBinding.inflate(layoutInflater)
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

        // ... your existing code ...

        viewBinding.downloadBtn.setOnClickListener {
            if (fileUrl.isNullOrEmpty() || fileName.isNullOrEmpty()) {
                Toast.makeText(this, "File information not available", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Store for later use in permission callback
            pendingFileUrl = fileUrl
            pendingFileName = fileName

            // Check Android version and permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ doesn't need WRITE_EXTERNAL_STORAGE
                downloadFileToDevice(fileUrl, fileName)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Android 6-9 needs runtime permission
                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    downloadFileToDevice(fileUrl, fileName)
                } else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        STORAGE_PERMISSION_CODE
                    )
                }
            } else {
                // Below Android 6 - permission granted at install time
                downloadFileToDevice(fileUrl, fileName)
            }
        }
    }

    // Handle permission result
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted - download the file
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
        val request = DownloadManager.Request(Uri.parse(fileUrl))
            .setTitle(fileName)
            .setDescription("Downloading material...")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName)
            .setAllowedOverMetered(true)
            .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        downloadManager.enqueue(request)

        Toast.makeText(this, "Downloading $fileName...", Toast.LENGTH_SHORT).show()
    }
}

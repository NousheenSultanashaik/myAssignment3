package com.example.assignment3

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.assignment3.databinding.ActivitySaveBinding
import java.io.IOException
import java.util.*

class SaveActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveBinding
    private lateinit var imageUri: Uri
    private lateinit var btnSaveImage : Button
    private lateinit var imageView : ImageView
    private val channelId = "image_save_channel"
    private val notificationId = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the image URI from the intent
        val imageUriString = intent.getStringExtra("imageUri")
        if (imageUriString != null) {
            imageUri = Uri.parse(imageUriString)
            binding.imageView.setImageURI(imageUri)
            btnSaveImage = findViewById(R.id.btnSaveImage)
            imageView = findViewById(R.id.imageView)
            btnSaveImage.setOnClickListener {
                val bitmap = (binding.imageView.drawable as BitmapDrawable).bitmap
                if (imageUriString != null) {
                    imageUri = Uri.parse(imageUriString)
                    binding.imageView.setImageURI(imageUri)
                    btnSaveImage = findViewById(R.id.btnSaveImage)
                        if (bitmap != null) {
                            showImageSaveDialog(bitmap)
                        } else {
                            showImageSaveErrorDialog()
                        }
                    }

                   //this is used for mediastore for image
                    val contentResolver = applicationContext.contentResolver
                    val contentValues = ContentValues().apply {
                        put(
                            MediaStore.Images.Media.DISPLAY_NAME,
                            "Image_${System.currentTimeMillis()}.jpg"
                        )
                        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                    }

                    // Insert the image into MediaStore.Images.Media content provider
                    val imageUri = contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    try {
                        imageUri?.let {
                            val outputStream = contentResolver.openOutputStream(it)
                            outputStream?.use { stream ->
                                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream)
                                stream.flush()
                            }
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    showNotification("Image saved successfully")

                    val intent = Intent(this, GalaryActivity::class.java)
                    startActivity(intent)
                    finish()
                }


        }
    }

    private fun showNotification(message: String) {
        createNotificationChannel()

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.notification_icon)
            .setContentTitle("Image Saved")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val context = this // Store the outer context in a separate variable

        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Request the missing permission
                return
            }
            val rand : Int = Random().nextInt()

            notify(rand, builder.build())
        }
    }
//this i have used for creating the notification channel
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Image Save Channel"
            val descriptionText = "Channel for image save notifications"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }

            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    private fun showImageSaveDialog(bitmap: Bitmap) {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_image_upload, null)
        dialogBuilder.setView(dialogView)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        // Delayed transition to another activity
        Handler().postDelayed({
            alertDialog.dismiss()
            val intent = Intent(this, OtherActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000) // Delay in milliseconds (5 seconds)

    }

    private fun showImageSaveErrorDialog() {
        val dialogBuilder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_image_upload_error, null)
        dialogBuilder.setView(dialogView)

        dialogBuilder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()
    }

}
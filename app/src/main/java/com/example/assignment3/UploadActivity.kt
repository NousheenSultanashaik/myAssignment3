package com.example.assignment3

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.FileProvider
import com.example.assignment3.databinding.ActivityUploadBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class UploadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUploadBinding
    private lateinit var storageRef: StorageReference
    private lateinit var firebaseFirestore: FirebaseFirestore
    private lateinit var dialog: Dialog
    //this i have implimented for getting the captured image from camera
    private lateinit var capturedImageFile: File

    //adding this for notification
    private lateinit var notificationManager: NotificationManager
    private val notificationChannelId = "UploadNotificationChannel"
    private val notificationId = 1
    //this is used for dialog box
    private var isDialogVisible = false
    private var imageUri: Uri? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backarrow.setOnClickListener {
            onBackPressed()
        }

        initVar()
        registerClickEvent()
        createDialog()
        //from here i am starting the notification code
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

    }

    private fun initVar() {
        storageRef = FirebaseStorage.getInstance().reference.child("Images")
        firebaseFirestore = FirebaseFirestore.getInstance()

    }

    private val resultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            if (result.data == null) {
                // The image was captured from the camera
                val imageUri = Uri.fromFile(capturedImageFile)
                binding.uploadimgid.setImageURI(imageUri)
                val intent = Intent(this, SaveActivity::class.java)
                intent.putExtra("imageUri", imageUri.toString())
                startActivity(intent)
                uploadImage()
            } else {
                // The image was selected from the gallery
                val data: Intent? = result.data
                imageUri = data?.data
                binding.uploadimgid.setImageURI(imageUri)
                val intent = Intent(this, SaveActivity::class.java)
                intent.putExtra("imageUri", imageUri.toString())
                startActivity(intent)
                uploadImage()
            }
        }
    }


    private fun registerClickEvent() {
        binding.uploadbutton.setOnClickListener {
            openImagePicker()
        }



    }

    private fun openImagePicker() {
        val options = arrayOf<CharSequence>("Choose from Gallery", "Take Photo", "Cancel")
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Choose from Gallery" -> openGallery()
                options[item] == "Take Photo" -> openCamera()
                options[item] == "Cancel" -> dialog.dismiss()
            }
        }
        builder.show()
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        capturedImageFile = createTempImageFile()
        val photoURI: Uri = FileProvider.getUriForFile(
            this,
            "com.example.assignment3.fileprovider",
            capturedImageFile
        )
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
        resultLauncher.launch(intent)

    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        resultLauncher.launch(intent)

    }
    companion object {
        private const val REQUEST_SELECT_IMAGE = 1
        private const val REQUEST_IMAGE_CAPTURE = 2
    }

    private fun createDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_image_upload)
        dialog.setCancelable(false)
    }

    private fun showImageUploadDialog() {
        if (!isDialogVisible) {
            dialog.show()
            isDialogVisible = true
            Handler(Looper.getMainLooper()).postDelayed({
                dialog.dismiss()
                isDialogVisible = false
            }, 3000)
        }
    }

    private fun showImageUploadErrorDialog() {
        val errorDialog = Dialog(this)
        errorDialog.setContentView(R.layout.dialog_image_upload_error)
        errorDialog.setCancelable(false)
        errorDialog.show()
        Handler(Looper.getMainLooper()).postDelayed({
            errorDialog.dismiss()
        }, 3000)
    }


    private fun uploadImage() {
        //just now implemented afterward I can change this
        if (imageUri == null) {
            showImageUploadErrorDialog()
            return
        }

        showImageUploadDialog()
        storageRef = storageRef.child(System.currentTimeMillis().toString())
        imageUri?.let {
            storageRef.putFile(it).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        val map = HashMap<String, Any>()
                        map["pic"] = uri.toString()
                        firebaseFirestore.collection("images").add(map)
                            .addOnCompleteListener { firestoreTask ->
                                if (firestoreTask.isSuccessful) {
                                    Toast.makeText(
                                        this,
                                        "uploaded successfully",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    // Start the SaveActivity with the image URI as extra
                                    val intent = Intent(this, SaveActivity::class.java)
                                    intent.putExtra("imageUri", uri.toString()) // Pass the URI as a string
                                    startActivity(intent)
                                }
                            }
                    }
                } else {
                    Toast.makeText(this, "image uploading failed", Toast.LENGTH_SHORT).show()
                    binding.uploadimgid.setImageResource(R.drawable.uploadimg)
                }
            }
        }
    }


    //this is also the notification code
    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val notificationChannel = NotificationChannel(
            notificationChannelId,
            "Upload Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.description = "Notification channel for upload status"
        notificationManager.createNotificationChannel(notificationChannel)
    }
    private fun createTempImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

}












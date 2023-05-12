package com.example.assignment3

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.assignment3.databinding.ActivityGalaryBinding
import com.example.assignment3.databinding.ActivityLoginBinding
import com.example.assignment3.databinding.DialogImageBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso

class GalaryActivity : AppCompatActivity() {
    //here i have added binding from gradle
    private lateinit var binding: ActivityGalaryBinding
    private lateinit var uploadbutton:Button
    //here ihave used the firebase for storing it in the firebase storage
    private lateinit var firebaseFirestore: FirebaseFirestore
    private var mList = mutableListOf<String>()
    private lateinit var adapter: ImagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGalaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
   //when i click on back it will take me to back
        binding.backarrow.setOnClickListener {
            onBackPressed()
        }
        binding.uploadbutton.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }
        initVars()
            getImages()
        }
   //here i have used this function for creating the imageadapter for the firebase storing images
        private fun initVars() {
            firebaseFirestore = FirebaseFirestore.getInstance()
            binding.recyclerView.setHasFixedSize(true)
            binding.recyclerView.layoutManager = GridLayoutManager(this,3)
            adapter = ImagesAdapter(mList)
           //i have added from here
            // Set the item click listener
            adapter.setOnItemClickListener(object : ImagesAdapter.OnItemClickListener {
                override fun onItemClick(imagePath: String) {
                    // Call showImageDialog using the activity instance
                    showImageDialog(imagePath)
                }
            })
            //then i am calling the recyclerview
            binding.recyclerView.adapter = adapter

        }
    //this i have used for getting the images

        @SuppressLint("NotifyDataSetChanged")
        private fun getImages(){
            firebaseFirestore.collection("images")
                .get().addOnSuccessListener {
                    for(i in it){
                        mList.add(i.data["pic"].toString())
                    }
                    adapter.notifyDataSetChanged()

                }
        }
    private fun showImageDialog(imagePath: String) {
        val dialog = Dialog(this)
        val dialogBinding = DialogImageBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBinding.root)
        Picasso.get()
            .load(imagePath)
            .into(dialogBinding.imageView)

        // Close the dialog when clicked
        dialogBinding.imageView.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }




}


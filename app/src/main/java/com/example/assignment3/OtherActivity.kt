package com.example.assignment3

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.assignment3.databinding.ActivityLoginBinding
import com.example.assignment3.databinding.ActivityOtherBinding

class OtherActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOtherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galaryButton.setOnClickListener {
            val intent = Intent(this, GalaryActivity::class.java)
            startActivity(intent)
        }
        //this i have used for permissions
        binding.locationButton.setOnClickListener {
            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            ) {
                // Location is enabled
                val intent = Intent(this, MapActivity::class.java)
                startActivity(intent)
            } else {
                // Location is disabled
                Toast.makeText(this, "Please enable your location", Toast.LENGTH_SHORT).show()
            }
        }


    }
}
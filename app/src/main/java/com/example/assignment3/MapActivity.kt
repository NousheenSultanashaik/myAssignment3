package com.example.assignment3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Random
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.provider.SyncStateContract
import android.util.Log
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.auth.User
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import java.text.SimpleDateFormat
import java.util.*


class MapActivity : AppCompatActivity(), OnMapReadyCallback, DatePickerDialog.OnDateSetListener {

   lateinit var mapFragment: SupportMapFragment
   lateinit var googleMap: GoogleMap
   private lateinit var addressInput: EditText
   private lateinit var dateInput: EditText
   private lateinit var backarrow : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        addressInput = findViewById(R.id.addressInput)
        dateInput = findViewById(R.id.dateInput)
        backarrow = findViewById(R.id.backarrow)
        //this i have add now for myfirebasemessing serivice access
        FirebaseMessaging.getInstance().subscribeToTopic("location_updates")
         //till here i have added
        backarrow.setOnClickListener {
            onBackPressed()
        }
   //this i have used for update button which is in map activity
        val updateLocationButton: Button = findViewById(R.id.uploadbutton)
        updateLocationButton.setOnClickListener {
            val isAddressUpdated = isAddressUpdated()
            val isDateTimeUpdated = isDateTimeUpdated()
            //giving the condition here
            if (isAddressUpdated || isDateTimeUpdated) {
                showLocationUpdateDialog(true)
            } else {
                showLocationUpdateDialog(false)
            }
        }
        val defaultAddress = "Hitech City Rd, Patrika Nagar, HITEC CITY, Hyderabad, Telangana 500081"
        addressInput.setText(defaultAddress)
        addressInput.setOnClickListener {
            openMap()
        }
        dateInput.setOnClickListener {
            showDatePickerDialog()
        }
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

//this is the code for date and time for updating it
    @Suppress("UNREACHABLE_CODE")
    private fun isDateTimeUpdated(): Boolean {
        val currentDate = getCurrentDate()
        val selectedDate = dateInput.text.toString().trim()
        return currentDate != selectedDate
        //this i have added for myfirebasemessingserivce
        if (isAddressUpdated() || isDateTimeUpdated()) {
            sendLocationUpdateNotification()
        }
        
        //till here i have added


    }
  //this is for current date and time accessing
    private fun getCurrentDate(): Any {
        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time
        return dateFormat.format(currentDate)
    }
//this is for accesing the address accessing
    @Suppress("UNREACHABLE_CODE")
    private fun isAddressUpdated(): Boolean {
        val defaultAddress = "Hitech City Rd, Patrika Nagar, HITEC CITY, Hyderabad, Telangana 500081"
        val currentAddress = addressInput.text.toString().trim()
        return currentAddress != defaultAddress
        //this i have added for myfirebasemessingservice
        if (isAddressUpdated() || isDateTimeUpdated()) {
            sendLocationUpdateNotification()
        }
        //till here i have added


    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        // Set the default location and address
        val defaultLocation = LatLng(17.44155, 78.38264)
        val defaultAddress = "Hitech City Rd, Patrika Nagar, HITEC CITY, Hyderabad, Telangana 500081"
        // Add marker and move camera to the selected location
        // Add marker and move camera to the default location
        googleMap.addMarker(MarkerOptions().position(defaultLocation).title("Default Location"))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))

        // Set the default address in EditText
        addressInput.setText(defaultAddress)
        googleMap.setOnMapClickListener { latLng ->
            googleMap.clear()
            googleMap.addMarker(MarkerOptions().position(latLng).title("Selected Location"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10f))

            // Set the address in EditText
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses?.isNotEmpty() == true) {
                val address = addresses?.get(0)?.getAddressLine(0)
                addressInput.setText(address)
            }
        }
    }

    private fun openMap() {
        // Handle the map selection within the same activity or fragment
        Toast.makeText(this, "Open map for location selection", Toast.LENGTH_SHORT).show()
    }

    private fun showDatePickerDialog() {
        // Show the date picker dialog for selecting a date
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this, this, year, month, day)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        // Set the selected date in the EditText
        val selectedDate = Calendar.getInstance()
        selectedDate.set(year, month, dayOfMonth)

        val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(selectedDate.time)

        dateInput.setText(formattedDate)
    }
    @SuppressLint("ClickableViewAccessibility")
    private fun showLocationUpdateDialog(success: Boolean) {
        val addressInput = findViewById<TextInputEditText>(R.id.addressInput)
        val dateInput = findViewById<TextInputEditText>(R.id.dateInput)

        val address = addressInput.text.toString()
        val date = dateInput.text.toString()

        val isFieldsFilled = address.isNotEmpty() && date.isNotEmpty()
        val title = if (success && isFieldsFilled) "Location Updated Successfully" else "Status"
        val message = if (success && isFieldsFilled) "Location has been successfully updated." else "Failed to update the location. Please try again."

        val dialogBuilder = AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        // Set a custom view without any buttons
        alertDialog.apply {
            val customView = layoutInflater.inflate(R.layout.custom_dialog_view, null)
            setView(customView)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                dismiss()
            }, 3000) // 5000 milliseconds = 5 seconds

            customView.setOnTouchListener { _, _ ->
                handler.removeCallbacksAndMessages(null)
                dismiss()
                true
            }
        }
    }
    private fun sendLocationUpdateNotification() {
        val notificationData = mapOf(
            "title" to "Location Updated",
            "message" to "Location has been updated."
        )

        val random = Random()
        val messageId = random.nextInt()
   //this is the service key from firebase
        val remoteMessage =
            RemoteMessage.Builder("AAAAhhYwj7o:APA91bFRQocndMWIDlt2iYUvyEWRBEhzPav0QrW2WEYpnh4iW0u6vMq1W5AzkVOdjwzuROIuT3Z9POM3QO9DWte0sZrukBcHkTJtSEQWUnRgmkwuPGVze79YYJCacjwk-y13pxyeop43")
                .setMessageId(messageId.toString())
                .setData(notificationData)
                .build()

        FirebaseMessaging.getInstance().send(remoteMessage)


    }
    }
package com.mobdeve.s11.santos.andreali.everhourprototype

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class EntryTimerActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var ibPausePlay: ImageButton
    private lateinit var ibRestart: ImageButton
    private lateinit var cloTimer: ConstraintLayout
    private var isRunning = false
    private val handler = Handler()
    private var elapsedTime: Long = 0 // Time elapsed in milliseconds
    private var startTime: Long = 0 // When the timer was started
    private lateinit var updateTimerRunnable: Runnable
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entry_timer)

        tvTime = findViewById(R.id.tvTime)
        ibPausePlay = findViewById(R.id.ibPausePlay)
        ibRestart = findViewById(R.id.ibRestart)
        cloTimer = findViewById(R.id.cloTimer)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Retrieve the passed data
        val timeEntryId = intent.getStringExtra("TIME_ENTRY_ID")
        val projectId = intent.getStringExtra("PROJECT_ID")
        val workspaceId = intent.getStringExtra("WORKSPACE_ID")
        val projectName = intent.getStringExtra("PROJECT_NAME")
        val entryName = intent.getStringExtra("ENTRY_NAME")
        val elapsedTimeString = intent.getStringExtra("TIME_ELAPSED") ?: "00:00:00"
        elapsedTime = convertTimeStringToMillis(elapsedTimeString)  // Convert string to milliseconds

        // Set the project and entry name
        findViewById<TextView>(R.id.tvEntryTimer).text = projectName
        findViewById<TextView>(R.id.tvEntry).text = entryName

        updateTimerText()

        ibPausePlay.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                checkLocationPermissionAndStartTimer()
            }
        }

        cloTimer.setOnClickListener {
            showClockOutDialog(timeEntryId, projectId, workspaceId, entryName, projectName)
        }

        ibRestart.setOnClickListener {
            isRunning = false
            elapsedTime = 0
            updateTimerText()
        }

        // Navbar Buttons
        findViewById<ImageView>(R.id.ivHome).setOnClickListener {
            val intent = Intent(this, WorkspaceActivity::class.java)
            startActivity(intent)
        }
        findViewById<ImageView>(R.id.ivReport).setOnClickListener {
            // TODO: place report activity here
        }
        findViewById<ImageView>(R.id.ivAccount).setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
        }
    }

    private fun checkLocationPermissionAndStartTimer() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            // Permission already granted, get the location
            getLocationAndStartTimer()
        }
    }

    private fun getLocationAndStartTimer() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Request location permissions
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                // Log or use the location as needed
                val latitude = location.latitude
                val longitude = location.longitude
                // Do something with the location (e.g., save it or display it)
            }
            // Start the timer after getting the location
            startTimer()
        }.addOnFailureListener {
            // Handle location retrieval failure
            startTimer() // Still starts the timer even if location retrieval fails
        }
    }

    private fun showClockOutDialog(timeEntryId: String?, projectId: String?, workspaceId: String?, entryName: String?, projectName: String?) {
        val dialog = ClockOutDialogFragment().apply {
            arguments = Bundle().apply {
                putString("TIME_ENTRY_ID", timeEntryId)
                putLong("ELAPSED_TIME", elapsedTime)
                putString("PROJECT_ID", projectId)
                putString("WORKSPACE_ID", workspaceId)
                putString("ENTRY_NAME", entryName)
                putString("PROJECT_NAME", projectName)
            }
        }
        dialog.show(supportFragmentManager, "ClockOutDialog")
    }

    fun stopTimer() {
        if (isRunning) {
            pauseTimer()
            ibPausePlay.setImageResource(R.drawable.play_circle_v2) // Set to play icon when stopped
        }
    }

    private fun startTimer() {
        isRunning = true
        startTime = SystemClock.elapsedRealtime() - elapsedTime
        ibPausePlay.setImageResource(R.drawable.pause_circle) // Set to pause icon when running

        updateTimerRunnable = object : Runnable {
            override fun run() {
                elapsedTime = SystemClock.elapsedRealtime() - startTime
                updateTimerText()
                handler.postDelayed(this, 1000) // Update every second
            }
        }

        handler.post(updateTimerRunnable) // Start the runnable
    }

    private fun pauseTimer() {
        handler.removeCallbacks(updateTimerRunnable)
        elapsedTime = SystemClock.elapsedRealtime() - startTime
        isRunning = false
        ibPausePlay.setImageResource(R.drawable.play_circle_v2) // Set to play icon when paused
    }

    private fun updateTimerText() {
        val hours = (elapsedTime / 1000) / 3600
        val minutes = (elapsedTime / 1000 % 3600) / 60
        val seconds = (elapsedTime / 1000 % 60)
        val timeFormatted = String.format("%02d:%02d:%02d", hours, minutes, seconds)
        tvTime.text = timeFormatted
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, get the location and start the timer
                getLocationAndStartTimer()
            } else {
                // Permission denied
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Helper function to convert time string to milliseconds
    private fun convertTimeStringToMillis(timeString: String): Long {
        val parts = timeString.split(":")
        if (parts.size == 3) {
            val hours = parts[0].toIntOrNull() ?: 0
            val minutes = parts[1].toIntOrNull() ?: 0
            val seconds = parts[2].toIntOrNull() ?: 0

            return (hours * 3600 + minutes * 60 + seconds) * 1000L
        }
        return 0L
    }
}
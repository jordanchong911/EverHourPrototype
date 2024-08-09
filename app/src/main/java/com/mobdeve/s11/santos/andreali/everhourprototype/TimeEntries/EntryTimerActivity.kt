package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class EntryTimerActivity : AppCompatActivity() {

    private lateinit var tvTime: TextView
    private lateinit var ibPausePlay: ImageButton
    private lateinit var ibRestart: ImageButton
    private lateinit var cloTimer: ConstraintLayout
    private var isRunning = false
    private val handler = Handler()
    private var elapsedTime: Long = 0 // Time elapsed in milliseconds
    private lateinit var updateTimerRunnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.entry_timer)

        tvTime = findViewById(R.id.tvTime)
        ibPausePlay = findViewById(R.id.ibPausePlay)
        ibRestart = findViewById(R.id.ibRestart)
        cloTimer = findViewById(R.id.cloTimer)

        // Retrieve the passed data
        val timeEntryId = intent.getStringExtra("TIME_ENTRY_ID")
        val projectId = intent.getStringExtra("PROJECT_ID")
        val workspaceId = intent.getStringExtra("WORKSPACE_ID")
        val projectName = intent.getStringExtra("PROJECT_NAME")
        val entryName = intent.getStringExtra("ENTRY_NAME")

        // Set the project and entry name
        findViewById<TextView>(R.id.tvEntryTimer).text = projectName
        findViewById<TextView>(R.id.tvEntry).text = entryName

        ibPausePlay.setOnClickListener {
            if (isRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        cloTimer.setOnClickListener {
            showClockOutDialog(timeEntryId, projectId, workspaceId, entryName)
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

    private fun showClockOutDialog(timeEntryId: String?, projectId: String?, workspaceId: String?, entryName: String?) {
        val dialog = ClockOutDialogFragment().apply {
            arguments = Bundle().apply {
                putString("TIME_ENTRY_ID", timeEntryId)
                putLong("ELAPSED_TIME", elapsedTime)
                putString("PROJECT_ID", projectId)
                putString("WORKSPACE_ID", workspaceId)
                putString("ENTRY_NAME", entryName)
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
        ibPausePlay.setImageResource(R.drawable.pause_circle) // Set to pause icon when running

        updateTimerRunnable = object : Runnable {
            override fun run() {
                elapsedTime += 1000 // Increment by 1 second
                updateTimerText()
                handler.postDelayed(this, 1000) // Update every second
            }
        }

        handler.post(updateTimerRunnable) // Start the runnable
    }

    private fun pauseTimer() {
        handler.removeCallbacks(updateTimerRunnable)
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
}

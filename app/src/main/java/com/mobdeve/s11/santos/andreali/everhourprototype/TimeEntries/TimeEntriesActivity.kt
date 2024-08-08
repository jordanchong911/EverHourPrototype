package com.mobdeve.s11.santos.andreali.everhourprototype

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.EntryOverviewBinding

class TimeEntriesActivity : AppCompatActivity() {

    private lateinit var binding: EntryOverviewBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var projectId: String
    private lateinit var timeEntriesAdapter: TimeEntriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntryOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        projectId = intent.getStringExtra("PROJECT_ID") ?: run {
            Log.e("TimeEntriesActivity", "Project ID not found in intent")
            Toast.makeText(this, "Project ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        fetchTimeEntries()
    }

    private fun setupRecyclerView() {
        timeEntriesAdapter = TimeEntriesAdapter(mutableListOf())
        binding.rvPEntries.layoutManager = LinearLayoutManager(this)
        binding.rvPEntries.adapter = timeEntriesAdapter
    }

    private fun fetchTimeEntries() {
        dbRef.child("time_entries").child(projectId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val timeEntriesList = mutableListOf<TimeEntry>()
                for (timeEntrySnapshot in snapshot.children) {
                    val timeEntry = timeEntrySnapshot.getValue(TimeEntry::class.java)
                    if (timeEntry != null) {
                        timeEntriesList.add(timeEntry)
                    }
                }
                timeEntriesAdapter.updateTimeEntries(timeEntriesList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@TimeEntriesActivity, "Failed to load time entries: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}

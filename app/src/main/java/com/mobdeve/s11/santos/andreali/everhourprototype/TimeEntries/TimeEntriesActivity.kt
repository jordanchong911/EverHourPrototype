package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.Workspaces.WorkspaceActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.EntryOverviewBinding

class TimeEntriesActivity : AppCompatActivity() {

    private lateinit var binding: EntryOverviewBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var projectId: String
    private lateinit var projectName: String
    private lateinit var workspaceId: String
    private lateinit var timeEntriesAdapter: TimeEntriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntryOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference

        // Retrieve the projectId, projectName, and workspaceId from the intent
        projectId = intent.getStringExtra("PROJECT_ID") ?: run {
            Log.e("TimeEntriesActivity", "Project ID not found in intent")
            Toast.makeText(this, "Project ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        projectName = intent.getStringExtra("PROJECT_NAME") ?: run {
            Log.e("TimeEntriesActivity", "Project Name not found in intent")
            Toast.makeText(this, "Project Name missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: run {
            Log.e("TimeEntriesActivity", "Workspace ID not found in intent")
            Toast.makeText(this, "Workspace ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        fetchTimeEntries()

        // Navbar Buttons
        binding.ivHome.setOnClickListener {
            val intent = Intent(this, WorkspaceActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ivReport.setOnClickListener {
            //TODO: place report activity here
        }
        binding.ivAccount.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        val fragmentManager = supportFragmentManager  // Get FragmentManager from Activity
        val context = this // Get context from Activity

        // Instantiate the adapter with all required parameters
        timeEntriesAdapter = TimeEntriesAdapter(
            timeEntries = mutableListOf(),
            fragmentManager = fragmentManager,
            projectId = projectId,
            projectName = projectName,
            workspaceId = workspaceId,
            context = context
        )

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

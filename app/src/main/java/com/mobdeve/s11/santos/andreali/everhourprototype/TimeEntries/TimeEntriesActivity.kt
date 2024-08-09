package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.EntryOverviewBinding

class TimeEntriesActivity : AppCompatActivity() {

    private lateinit var binding: EntryOverviewBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var projectId: String
    private lateinit var projectName: String
    private lateinit var workspaceId: String
    private lateinit var timeEntriesAdapter: TimeEntriesAdapter
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = EntryOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.e("TimeEntriesActivity", "User ID not found")
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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
        binding.tvEntryOv.text = projectName

        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: run {
            Log.e("TimeEntriesActivity", "Workspace ID not found in intent")
            Toast.makeText(this, "Workspace ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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

        binding.btnCreateEntry.setOnClickListener {
            showEntryPromptDialog()
        }

        setupRecyclerView()
        fetchTimeEntries()
    }

    private fun setupRecyclerView() {
        timeEntriesAdapter = TimeEntriesAdapter(
            timeEntries = mutableListOf(),
            fragmentManager = supportFragmentManager,
            projectId = projectId,
            projectName = projectName,
            workspaceId = workspaceId,
            context = this
        )

        binding.rvPEntries.layoutManager = LinearLayoutManager(this)
        binding.rvPEntries.adapter = timeEntriesAdapter
    }

    private fun fetchTimeEntries() {
        dbRef.child("workspaces").child(workspaceId)
            .child("projects").child(projectId).child("time_entries")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val timeEntriesList = mutableListOf<TimeEntry>()
                    for (timeEntrySnapshot in snapshot.children) {
                        val timeEntry = timeEntrySnapshot.getValue(TimeEntry::class.java)
                        if (timeEntry != null) {
                            timeEntriesList.add(timeEntry)
                        }
                    }
                    timeEntriesAdapter.updateTimeEntries(timeEntriesList)
                    if (timeEntriesList.isEmpty()) {
                        Toast.makeText(this@TimeEntriesActivity, "No time entries found.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@TimeEntriesActivity, "Failed to load time entries: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showEntryPromptDialog() {
        val entryPromptDialog = EntryPromptDialogFragment()
        entryPromptDialog.setOnNameEnteredListener { name ->
            showEntryRateDialog(name)
        }
        entryPromptDialog.show(supportFragmentManager, "EntryPromptDialogFragment")
    }

    private fun showEntryRateDialog(entryName: String) {
        val entryRateDialog = EntryRateDialogFragment()
        entryRateDialog.setOnRateEnteredListener { rate ->
            saveTimeEntry(entryName, rate)
        }
        entryRateDialog.show(supportFragmentManager, "EntryRateDialogFragment")
    }

    private fun saveTimeEntry(entryName: String, entryRate: Int) {
        val timeEntryID = dbRef.child("workspaces").child(workspaceId)
            .child("projects").child(projectId).child("time_entries").push().key ?: return
        val timeEntry = TimeEntry(
            workplaceID = workspaceId,
            projectID = projectId,
            timeEntryID = timeEntryID,
            name = entryName,
            timeElapsed = "00:00:00", // Default start time
            rate = entryRate
        )
        dbRef.child("workspaces").child(workspaceId)
            .child("projects").child(projectId).child("time_entries").child(timeEntryID)
            .setValue(timeEntry)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Time entry added successfully", Toast.LENGTH_SHORT).show()
                    fetchTimeEntries()
                } else {
                    Toast.makeText(this, "Failed to add time entry", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
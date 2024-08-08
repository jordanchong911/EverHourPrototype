package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectDetailsBinding

class ProjectDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ProjectDetailsBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var projectId: String
    private lateinit var workspaceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProjectDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        projectId = intent.getStringExtra("PROJECT_ID") ?: ""
        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: ""

        if (projectId.isEmpty() || workspaceId.isEmpty()) {
            Toast.makeText(this, "Missing project or workspace ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        fetchProjectDetails()

        // Navbar Buttons
        binding.ivHome.setOnClickListener {
            val intent = Intent(this, WorkspaceActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ivReport.setOnClickListener{
            //TODO: place report activity here
        }
        binding.ivAccount.setOnClickListener{
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnTimeEntries.setOnClickListener {
            checkTimeEntries()
        }
    }

    private fun fetchProjectDetails() {
        dbRef.child("projects").child(workspaceId).child(projectId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val project = snapshot.getValue(Project::class.java)
                if (project != null) {
                    displayProjectDetails(project)
                } else {
                    Toast.makeText(this@ProjectDetailsActivity, "Project not found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProjectDetailsActivity, "Failed to load project details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun displayProjectDetails(project: Project) {
        binding.tvProjectDetails.text = project.name
        binding.tvClientName.text = project.client
        binding.tvRoleIC.text = project.roleIC
        // Populate other fields with project details
    }

    private fun checkTimeEntries() {
        dbRef.child("time_entries").child(projectId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val intent = Intent(this@ProjectDetailsActivity, TimeEntriesActivity::class.java)
                    intent.putExtra("PROJECT_ID", projectId)
                    startActivity(intent)
                } else {
                    // No time entries, show the entry prompt dialog
                    showEntryPromptDialog()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProjectDetailsActivity, "Failed to check time entries: ${error.message}", Toast.LENGTH_SHORT).show()
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
        val timeEntryID = dbRef.child("time_entries").child(projectId).push().key ?: return
        val timeEntry = TimeEntry(
            workplaceID = workspaceId,
            projectID = projectId,
            timeEntryID = timeEntryID,
            name = entryName,
            timeElapsed = "00:00:00", // Default start time
            rate = entryRate
        )
        dbRef.child("time_entries").child(projectId).child(timeEntryID).setValue(timeEntry)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Time entry added successfully", Toast.LENGTH_SHORT).show()
                    // Optionally, you can navigate to the TimeEntriesActivity or update the UI
                } else {
                    Toast.makeText(this, "Failed to add time entry", Toast.LENGTH_SHORT).show()
                }
            }
    }
}

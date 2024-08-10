package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.Projects.ProjectsActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.WorkspaceDetailsBinding

class WorkspaceDetailsActivity : AppCompatActivity() {

    private lateinit var binding: WorkspaceDetailsBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceId: String
    private var currentName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkspaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference

        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: run {
            Toast.makeText(this, "Workspace ID not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        currentName = intent.getStringExtra("WORKSPACE_NAME") ?: ""

        // Navbar Buttons
        binding.ivHome.setOnClickListener {
            val intent = Intent(this, WorkspaceActivity::class.java)
            startActivity(intent)
            finish()
        }
        binding.ivReport.setOnClickListener {
            // TODO: place report activity here
        }
        binding.ivAccount.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.tvWorkspaceDetails.text = currentName // Set workspace name
        fetchWorkspaceDetails()
        setupActionListeners()
    }

    private fun fetchWorkspaceDetails() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val workspaceRef = dbRef.child("workspaces").child(workspaceId)

        workspaceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val workspace = snapshot.getValue(Workspace::class.java)
                if (workspace != null) {
                    currentName = workspace.name
                    binding.tvWorkspaceDetails.text = workspace.name
                    binding.tvTrackedNum.text = workspace.hours.toString()
                    fetchProjectCount()
                    fetchMemberCount() // Fetch member count
                }
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load workspace details: ${error.message}")
            }
        })
    }

    private fun fetchProjectCount() {
        val projectsRef = dbRef.child("workspaces").child(workspaceId).child("projects")

        projectsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvCountNum.text = snapshot.childrenCount.toString() // Update project count
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load project count: ${error.message}")
            }
        })
    }

    private fun fetchMemberCount() {
        val membersRef = dbRef.child("workspaces").child(workspaceId).child("members")

        membersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvTrackedNum.text = snapshot.childrenCount.toString() // Update member count
            }

            override fun onCancelled(error: DatabaseError) {
                showToast("Failed to load member count: ${error.message}")
            }
        })
    }

    private fun setupActionListeners() {
        binding.ivEdit.setOnClickListener { showUpdateWorkspaceDialog() }
        binding.btnMembers.setOnClickListener { navigateToMembersActivity() }
        binding.btnProjects.setOnClickListener { navigateToProjectsActivity() }
    }

    private fun showUpdateWorkspaceDialog() {
        if (currentName.isNotEmpty()) {
            val dialogFragment = UpdateWorkspaceDialogFragment.newInstance(workspaceId, currentName)
            dialogFragment.setOnWorkspaceUpdatedListener(object : UpdateWorkspaceDialogFragment.OnWorkspaceUpdatedListener {
                override fun onWorkspaceUpdated() {
                    fetchWorkspaceDetails() // Refresh the workspace details
                }
            })
            dialogFragment.show(supportFragmentManager, "UpdateWorkspaceDialog")
        } else {
            showToast("Current workspace name not available.")
        }
    }

    private fun navigateToMembersActivity() {
        Intent(this, MembersActivity::class.java).apply {
            putExtra("WORKSPACE_ID", workspaceId)
        }.also {
            startActivity(it)
        }
    }

    private fun navigateToProjectsActivity() {
        Intent(this, ProjectsActivity::class.java).apply {
            putExtra("WORKSPACE_ID", workspaceId)
            putExtra("WORKSPACE_NAME", currentName)
        }.also {
            startActivity(it)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

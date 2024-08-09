package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.mobdeve.s11.santos.andreali.everhourprototype.Account.AccountActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.Workspaces.WorkspaceActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.ProjectDetailsBinding

class ProjectDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ProjectDetailsBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var projectId: String
    private lateinit var workspaceId: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ProjectDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: run {
            Log.e("ProjectDetailsActivity", "User ID not found")
            Toast.makeText(this, "User ID missing", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

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
        binding.ivReport.setOnClickListener {
            // TODO: place report activity here
        }
        binding.ivAccount.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnTimeEntries.setOnClickListener {
            val intent = Intent(this@ProjectDetailsActivity, TimeEntriesActivity::class.java).apply {
                putExtra("PROJECT_ID", projectId)
                putExtra("WORKSPACE_ID", workspaceId) // Pass workspaceId here
                putExtra("PROJECT_NAME", binding.tvProjectDetails.text.toString()) // Pass project name
            }
            startActivity(intent)
        }
    }

    private fun fetchProjectDetails() {
        dbRef.child("workspaces").child(userId).child(workspaceId).child("projects").child(projectId).addListenerForSingleValueEvent(object : ValueEventListener {
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
        //TODO: binding.tvTrackedNum.text =
        //TODO: binding.tvCountNum.text =
    }

}

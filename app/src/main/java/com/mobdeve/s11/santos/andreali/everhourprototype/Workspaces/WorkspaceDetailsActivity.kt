package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.WorkspaceDetailsBinding

class WorkspaceDetailsActivity : AppCompatActivity(),
    MemberInviteDialogFragment.OnMemberInviteListener,
    MemberRoleDialogFragment.OnRoleSetListener {

    private lateinit var binding: WorkspaceDetailsBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceId: String
    private var currentName: String = "" // Default empty string

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkspaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: return

        // Save the workspace ID
        saveWorkspaceId(workspaceId)

        fetchWorkspaceDetails(workspaceId)

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

        binding.ivEdit.setOnClickListener {
            showUpdateWorkspaceDialog()
        }

        binding.btnMembers.setOnClickListener {
            // Directly navigate to MembersActivity
            val intent = Intent(this, MembersActivity::class.java)
            intent.putExtra("WORKSPACE_ID", workspaceId)
            startActivity(intent)
        }

        binding.btnProjects.setOnClickListener {
            val intent = Intent(this, ProjectsActivity::class.java).apply {
                putExtra("WORKSPACE_ID", workspaceId)
                putExtra("WORKSPACE_NAME", currentName)
            }
            startActivity(intent)
        }
    }

    private fun fetchWorkspaceDetails(workspaceId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val workspaceRef = dbRef.child("workspaces").child(userId).child(workspaceId)

        workspaceRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val workspace = snapshot.getValue(Workspace::class.java)
                if (workspace != null) {
                    currentName = workspace.name
                    binding.tvWorkspaceDetails.text = workspace.name
                    binding.tvTrackedNum.text = workspace.hours.toString()

                    // Fetch the count of projects
                    fetchProjectCount(workspaceId)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WorkspaceDetailsActivity, "Failed to load workspace details: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun fetchProjectCount(workspaceId: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val projectsRef = dbRef.child("workspaces").child(userId).child(workspaceId).child("projects")

        projectsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val projectCount = snapshot.childrenCount
                binding.tvCountNum.text = projectCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WorkspaceDetailsActivity, "Failed to load project count: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showUpdateWorkspaceDialog() {
        if (currentName.isNotEmpty()) { // Ensure currentName is initialized
            val dialogFragment = UpdateWorkspaceDialogFragment(workspaceId, currentName)
            dialogFragment.show(supportFragmentManager, "UpdateWorkspaceDialog")
        } else {
            Toast.makeText(this, "Current workspace name not available.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showInviteMemberDialog() {
        val dialogFragment = MemberInviteDialogFragment()
        dialogFragment.show(supportFragmentManager, "MemberInviteDialog")
    }

    override fun onMemberInvited(email: String) {
        val roleDialog = MemberRoleDialogFragment.newInstance(email)
        roleDialog.show(supportFragmentManager, "MemberRoleDialog")
    }

    override fun onRoleSet(email: String, role: String) {
        val member = Member(email = email, role = role, workspaceId = workspaceId)
        val memberRef = dbRef.child("members").child(workspaceId).child(email.replace(".", ","))
        memberRef.setValue(member)
        Toast.makeText(this, "Role set for $email", Toast.LENGTH_SHORT).show()
    }

    private fun saveWorkspaceId(workspaceId: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("current_workspace_id", workspaceId)
            apply()
        }
    }
}

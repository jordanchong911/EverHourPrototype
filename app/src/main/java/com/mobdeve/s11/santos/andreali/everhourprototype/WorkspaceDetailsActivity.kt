package com.mobdeve.s11.santos.andreali.everhourprototype

import Workspace
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.WorkspaceDetailsBinding

class WorkspaceDetailsActivity : AppCompatActivity(),
    MemberInviteDialogFragment.OnMemberInviteListener,
    MemberRoleDialogFragment.OnRoleSetListener {

    private lateinit var binding: WorkspaceDetailsBinding
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceId: String
    private lateinit var currentName: String // Store the current workspace name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkspaceDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbRef = FirebaseDatabase.getInstance().reference
        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: return

        fetchWorkspaceDetails(workspaceId)

        binding.ivHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }

        binding.ivEdit.setOnClickListener {
            // Create and show the dialog fragment for updating workspace name
            showUpdateWorkspaceDialog()
        }

        binding.btnMembers.setOnClickListener {
            checkForMembers()
        }
    }

    fun fetchWorkspaceDetails(workspaceId: String) {
        dbRef.child("workspaces").child(FirebaseAuth.getInstance().currentUser?.uid ?: "").child(workspaceId).get()
            .addOnSuccessListener { snapshot ->
                val workspace = snapshot.getValue(Workspace::class.java)
                if (workspace != null) {
                    currentName = workspace.name // Save the current name
                    binding.tvWorkspaceDetails.text = workspace.name
                    // Set other workspace details as needed
                    binding.tvTrackedNum.text = workspace.hours.toString()
                    binding.tvCountNum.text = workspace.projectsCount.toString()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to load workspace details: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun showUpdateWorkspaceDialog() {
        if (::currentName.isInitialized) { // Ensure currentName is initialized
            val dialogFragment = UpdateWorkspaceDialogFragment(workspaceId, currentName)
            dialogFragment.show(supportFragmentManager, "UpdateWorkspaceDialog")
        } else {
            Toast.makeText(this, "Current workspace name not available.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkForMembers() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return

        dbRef.child("members").child(workspaceId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val memberCount = snapshot.childrenCount
                    if (memberCount > 0) {
                        // Navigate to MembersActivity
                        val intent = Intent(this@WorkspaceDetailsActivity, MembersActivity::class.java)
                        intent.putExtra("WORKSPACE_ID", workspaceId)
                        startActivity(intent)
                    } else {
                        // Show invite member dialog
                        showInviteMemberDialog()
                    }
                } else {
                    // Show invite member dialog
                    showInviteMemberDialog()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WorkspaceDetailsActivity, "Failed to check members.", Toast.LENGTH_SHORT).show()
            }
        })
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
        // Handle the role assignment logic here
        Toast.makeText(this, "Role $role set for $email", Toast.LENGTH_SHORT).show()
        // Example: Save the email and role to the database
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val memberData = mapOf("email" to email, "role" to role)
        dbRef.child("members").child(workspaceId).child(userId).setValue(memberData)
            .addOnSuccessListener {
                Toast.makeText(this, "Role assigned successfully.", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Failed to assign role: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}

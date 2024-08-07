package com.mobdeve.s11.santos.andreali.everhourprototype

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MembersActivity : AppCompatActivity(),
    MemberInviteDialogFragment.OnMemberInviteListener,
    MemberRoleDialogFragment.OnRoleSetListener,
    MemberAdapter.OnRoleClickListener,
    MemberAdapter.OnOptionsClickListener,
    MemberDeleteDialogFragment.OnDeleteListener { // Implement OnDeleteListener

    private lateinit var recyclerView: RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.members_overview)

        recyclerView = findViewById(R.id.rvMembers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get the workspace ID from the intent
        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: return

        // Initialize Firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference("members")

        // Initialize adapter with empty list
        memberAdapter = MemberAdapter(emptyList())
        memberAdapter.setOnRoleClickListener(this)
        memberAdapter.setOnOptionsClickListener(this)
        recyclerView.adapter = memberAdapter

        // Fetch members
        fetchMembers()

        // Setup invite button click listener
        findViewById<Button>(R.id.btnInvite).setOnClickListener {
            showInviteMemberDialog()
        }
    }

    private fun fetchMembers() {
        dbRef.child(workspaceId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val membersList = mutableListOf<Member>()
                for (memberSnapshot in snapshot.children) {
                    val member = memberSnapshot.getValue(Member::class.java)
                    if (member != null) {
                        membersList.add(member)
                    }
                }
                Log.d("MembersActivity", "Fetched members: $membersList")
                // Update RecyclerView with the new list
                memberAdapter.updateMembers(membersList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MembersActivity", "Failed to read value.", error.toException())
            }
        })
    }

    private fun showInviteMemberDialog() {
        val dialogFragment = MemberInviteDialogFragment.newInstance(workspaceId)
        dialogFragment.show(supportFragmentManager, "MemberInviteDialog")
    }

    override fun onMemberInvited(email: String) {
        val roleDialog = MemberRoleDialogFragment.newInstance(email)
        roleDialog.show(supportFragmentManager, "MemberRoleDialog")
    }

    override fun onRoleSet(email: String, role: String) {
        val member = Member(email = email, role = role, workspaceId = workspaceId)
        val memberRef = dbRef.child(workspaceId).child(email.replace(".", ","))
        memberRef.setValue(member)
        Toast.makeText(this, "Role set for $email", Toast.LENGTH_SHORT).show()
    }

    override fun onRoleClick(email: String, currentRole: String) {
        val roleDialog = MemberRoleDialogFragment.newInstance(email)
        roleDialog.show(supportFragmentManager, "MemberRoleDialog")
    }

    override fun onOptionsClick(email: String) {
        val dialogFragment = MemberDeleteDialogFragment.newInstance(email)
        dialogFragment.show(supportFragmentManager, "MemberDeleteDialog")
    }

    override fun onDelete(email: String) {
        val memberRef = dbRef.child(workspaceId).child(email.replace(".", ","))
        memberRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Member $email deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to delete member", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.Account.AccountActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.Workspaces.WorkspaceActivity

class MembersActivity : AppCompatActivity(),
    MemberInviteDialogFragment.OnMemberInviteListener,
    MemberRoleDialogFragment.OnRoleSetListener,
    MemberAdapter.OnRoleClickListener,
    MemberAdapter.OnOptionsClickListener,
    MemberDeleteDialogFragment.OnDeleteListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceId: String
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.members_overview)

        recyclerView = findViewById(R.id.rvMembers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Get the workspace ID from the intent
        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: return

        // Initialize Firebase reference
        val auth = FirebaseAuth.getInstance()
        userId = auth.currentUser?.uid ?: return
        dbRef = FirebaseDatabase.getInstance().reference.child("workspaces").child(userId).child(workspaceId).child("members")

        // Initialize adapter with empty list
        memberAdapter = MemberAdapter(this, this)
        recyclerView.adapter = memberAdapter

        // Fetch members
        fetchMembers()

        // Setup invite button click listener
        findViewById<Button>(R.id.btnInvite).setOnClickListener {
            showInviteMemberDialog()
        }

        // Navbar Buttons
        findViewById<ImageView>(R.id.ivHome).setOnClickListener {
            val intent = Intent(this, WorkspaceActivity::class.java)
            startActivity(intent)
            finish()
        }
        findViewById<ImageView>(R.id.ivReport).setOnClickListener {
            // TODO: place report activity here
        }
        findViewById<ImageView>(R.id.ivAccount).setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchMembers() {
        dbRef.addValueEventListener(object : ValueEventListener {
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
                memberAdapter.submitList(membersList)
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
        val memberRef = dbRef.child(email.replace(".", ","))
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
        val memberRef = dbRef.child(email.replace(".", ","))
        memberRef.removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Member $email deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to delete member", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

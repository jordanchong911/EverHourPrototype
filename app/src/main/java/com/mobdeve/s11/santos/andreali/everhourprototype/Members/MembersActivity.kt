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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.members_overview)

        recyclerView = findViewById(R.id.rvMembers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        workspaceId = intent.getStringExtra("WORKSPACE_ID") ?: return
        dbRef = FirebaseDatabase.getInstance().reference.child("workspaces").child(workspaceId).child("members")

        memberAdapter = MemberAdapter(this, this)
        recyclerView.adapter = memberAdapter

        fetchMembers()

        findViewById<Button>(R.id.btnInvite).setOnClickListener {
            showInviteMemberDialog()
        }

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
                memberAdapter.submitList(membersList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MembersActivity", "Failed to read value.", error.toException())
            }
        })
    }

    private fun showInviteMemberDialog() {
        val dialogFragment = MemberInviteDialogFragment.newInstance(workspaceId)
        dialogFragment.setOnMemberInviteListener(this)
        dialogFragment.show(supportFragmentManager, "MemberInviteDialog")
    }

    override fun onMemberInvited(email: String, userId: String, firstName: String, lastName: String) {
        val roleDialog = MemberRoleDialogFragment.newInstance(email, "", firstName, lastName)
        roleDialog.setOnRoleSetListener(this)
        roleDialog.show(supportFragmentManager, "MemberRoleDialog")
    }

    override fun onRoleSet(email: String, role: String, firstName: String, lastName: String) {
        val member = Member(email = email, role = role, fname = firstName, lname = lastName, workspaceId = workspaceId)
        val memberRef = dbRef.child(email.replace(".", ","))
        memberRef.setValue(member).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Role set for $email", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Failed to set role for $email", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRoleClick(email: String, currentRole: String) {
        val memberSnapshot = dbRef.child(email.replace(".", ","))
        memberSnapshot.get().addOnSuccessListener { snapshot ->
            val member = snapshot.getValue(Member::class.java)
            val roleDialog = MemberRoleDialogFragment.newInstance(email, member?.role ?: "", member?.fname ?: "", member?.lname ?: "")
            roleDialog.setOnRoleSetListener(this)
            roleDialog.show(supportFragmentManager, "MemberRoleDialog")
        }
    }

    override fun onOptionsClick(email: String) {
        val dialogFragment = MemberDeleteDialogFragment.newInstance(email)
        dialogFragment.setOnDeleteListener(this)
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

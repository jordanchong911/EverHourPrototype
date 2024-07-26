package com.mobdeve.s11.santos.andreali.everhourprototype

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MembersActivity : AppCompatActivity(),
    MemberInviteDialogFragment.OnMemberInviteListener,
    MemberRoleDialogFragment.OnRoleSetListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var memberAdapter: MemberAdapter
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.members_overview)

        recyclerView = findViewById(R.id.rvMembers)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize Firebase reference
        dbRef = FirebaseDatabase.getInstance().getReference("members")

        // Initialize adapter with empty list
        memberAdapter = MemberAdapter(emptyList())
        recyclerView.adapter = memberAdapter

        // Fetch members
        fetchMembers()

        // Setup invite button click listener
        findViewById<Button>(R.id.btnInvite).setOnClickListener {
            showInviteMemberDialog()
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
                memberAdapter.updateMembers(membersList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.w("MembersActivity", "Failed to read value.", error.toException())
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
        val memberRef = dbRef.child(email.replace(".", ","))
        memberRef.child("role").setValue(role)
        Toast.makeText(this, "Role set for $email", Toast.LENGTH_SHORT).show()
    }
}

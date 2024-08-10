package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.firebase.database.*

class MemberInviteDialogFragment : DialogFragment() {

    interface OnMemberInviteListener {
        fun onMemberInvited(email: String, userId: String, firstName: String, lastName: String)
    }

    private var listener: OnMemberInviteListener? = null
    private lateinit var db: FirebaseDatabase
    private lateinit var dbRef: DatabaseReference
    private lateinit var invitedMembersRef: DatabaseReference
    private lateinit var userSpinner: Spinner
    private lateinit var userList: MutableList<String>
    private lateinit var userMap: MutableMap<String, UserInfo> // Maps full names to UserInfo

    companion object {
        private const val ARG_WORKSPACE_ID = "workspace_id"

        fun newInstance(workspaceId: String): MemberInviteDialogFragment {
            val fragment = MemberInviteDialogFragment()
            val args = Bundle().apply {
                putString(ARG_WORKSPACE_ID, workspaceId)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? OnMemberInviteListener
        if (listener == null) {
            throw ClassCastException("$context must implement OnMemberInviteListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val workspaceId = arguments?.getString(ARG_WORKSPACE_ID) ?: ""
        Log.d("MemberInviteDialogFragment", "Workspace ID: $workspaceId")

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val view = inflater.inflate(R.layout.member_invite_ol, null)

            db = FirebaseDatabase.getInstance()
            dbRef = db.reference.child("users")
            invitedMembersRef = db.reference.child("workspaces").child(workspaceId).child("members")

            userSpinner = view.findViewById(R.id.userSpinner)
            val btnInvite = view.findViewById<Button>(R.id.btnInvite)

            userList = mutableListOf()
            userMap = mutableMapOf()

            loadUsers()
            monitorInvitedMembers()

            btnInvite.setOnClickListener {
                val selectedUser = userSpinner.selectedItem.toString()
                val userInfo = userMap[selectedUser]
                if (userInfo != null) {
                    listener?.onMemberInvited(userInfo.email, userInfo.userId, userInfo.firstName, userInfo.lastName)
                    dismiss()
                } else {
                    Toast.makeText(context, "Error with selected user", Toast.LENGTH_SHORT).show()
                }
            }

            builder.setView(view)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun loadUsers() {
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return  // Check if the fragment is attached

                Log.d("MemberInviteDialogFragment", "Loading users...")

                userList.clear()  // Clear the existing list to avoid duplication
                userMap.clear()   // Clear the existing map to avoid duplication

                for (userSnapshot in snapshot.children) {
                    val userModel = userSnapshot.getValue(UserModel::class.java)
                    val userId = userSnapshot.key
                    if (userModel != null && userId != null) {
                        val fullName = "${userModel.fname} ${userModel.lname}"
                        userList.add(fullName)
                        userMap[fullName] = UserInfo(userModel.email, userId, userModel.fname, userModel.lname)
                    }
                }
                Log.d("MemberInviteDialogFragment", "Users loaded: $userList")
                updateSpinner()
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(context, "Failed to load users: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun monitorInvitedMembers() {
        invitedMembersRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("MemberInviteDialogFragment", "Invited member added: ${snapshot.key}")
                updateSpinner()
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                Log.d("MemberInviteDialogFragment", "Invited member changed: ${snapshot.key}")
                updateSpinner()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                Log.d("MemberInviteDialogFragment", "Invited member removed: ${snapshot.key}")
                updateSpinner()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(context, "Failed to monitor invited members: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    private fun updateSpinner() {
        if (!isAdded) return  // Check if the fragment is attached

        Log.d("MemberInviteDialogFragment", "Updating spinner...")

        val invitedMembers = mutableSetOf<String>()
        invitedMembersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!isAdded) return  // Check again after data is retrieved

                invitedMembers.clear() // Clear the set to avoid duplication

                for (child in snapshot.children) {
                    child.key?.let { invitedMembers.add(it) }
                }

                Log.d("MemberInviteDialogFragment", "Invited members: $invitedMembers")

                val availableUsers = userList.filter { userMap[it]?.userId !in invitedMembers }
                val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, availableUsers)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                userSpinner.adapter = adapter
            }

            override fun onCancelled(error: DatabaseError) {
                if (isAdded) {
                    Toast.makeText(context, "Failed to update spinner: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    // Data class for user information
    data class UserInfo(
        val email: String,
        val userId: String,
        val firstName: String,
        val lastName: String
    )

    fun setOnMemberInviteListener(listener: OnMemberInviteListener) {
        this.listener = listener
    }
}

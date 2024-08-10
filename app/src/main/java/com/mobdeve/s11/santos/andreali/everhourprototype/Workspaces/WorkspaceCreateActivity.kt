package com.mobdeve.s11.santos.andreali.everhourprototype

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class WorkspaceCreateActivity : AppCompatActivity() {

    private lateinit var workspaceNameEditText: EditText
    private lateinit var createButton: Button
    private lateinit var dbRef: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.workspace_create)

        workspaceNameEditText = findViewById(R.id.etvWorkspaceName)
        createButton = findViewById(R.id.btnCreateWS)
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        createButton.setOnClickListener {
            val workspaceName = workspaceNameEditText.text.toString()
            if (workspaceName.isNotEmpty()) {
                val userId = auth.currentUser?.uid ?: return@setOnClickListener
                val workspaceId = dbRef.child("workspaces").push().key ?: return@setOnClickListener
                val workspace = Workspace(workspaceId, workspaceName)

                dbRef.child("workspaces").child(workspaceId).setValue(workspace)
                    .addOnSuccessListener {
                        setResult(Activity.RESULT_OK) // Set result code here
                        finish() // Finish activity and remove from back stack
                    }
                    .addOnFailureListener {
                        // Handle failure, possibly show a Toast or error message
                    }
            }
        }
    }
}

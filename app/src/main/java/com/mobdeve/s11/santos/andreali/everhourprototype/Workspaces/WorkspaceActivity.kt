package com.mobdeve.s11.santos.andreali.everhourprototype

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.WorkspaceOverviewBinding

class WorkspaceActivity : AppCompatActivity() {

    private lateinit var binding: WorkspaceOverviewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceAdapter: WorkspaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkspaceOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth and DatabaseReference
        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference

        // Set up RecyclerView and Adapter
        binding.rvWorkspaces.layoutManager = LinearLayoutManager(this)
        workspaceAdapter = WorkspaceAdapter(mutableListOf()) { workspace ->
            val intent = Intent(this, WorkspaceDetailsActivity::class.java).apply {
                putExtra("WORKSPACE_ID", workspace.id) // Pass the workspace ID
                putExtra("WORKSPACE_NAME", workspace.name) // Pass the workspace name
            }
            startActivity(intent)
            finish()
        }
        binding.rvWorkspaces.adapter = workspaceAdapter

        // Fetch workspaces
        fetchWorkspaces()

        // Handle button click to create a new workspace
        binding.btnCreateWs.setOnClickListener {
            val intent = Intent(this, WorkspaceCreateActivity::class.java)
            startActivityForResult(intent, CREATE_WORKSPACE_REQUEST_CODE)
        }

        // Handle other button clicks
        binding.ivHome.setOnClickListener {
            // Already here
        }
        binding.ivReport.setOnClickListener {
            // TODO: Place report activity here
        }
        binding.ivAccount.setOnClickListener {
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Fetch workspaces and update the RecyclerView
    fun fetchWorkspaces() {
        val dbRef = FirebaseDatabase.getInstance().reference.child("workspaces")

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val workspaces = mutableListOf<Workspace>()
                for (workspaceSnapshot in snapshot.children) {
                    val workspace = workspaceSnapshot.getValue(Workspace::class.java)
                    if (workspace != null) {
                        workspaces.add(workspace)
                    }
                }
                workspaceAdapter.updateData(workspaces)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WorkspaceActivity, "Failed to load workspaces: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    // Handle result from WorkspaceCreateActivity
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_WORKSPACE_REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("WorkspaceActivity", "New workspace created, refreshing list")
            fetchWorkspaces() // Refresh workspaces when a new one is created
        }
    }

    companion object {
        private const val CREATE_WORKSPACE_REQUEST_CODE = 1
    }
}

package com.mobdeve.s11.santos.andreali.everhourprototype.Workspaces

import Workspace
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mobdeve.s11.santos.andreali.everhourprototype.databinding.WorkspaceOverviewBinding
import com.mobdeve.s11.santos.andreali.everhourprototype.AccountActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.WorkspaceAdapter
import com.mobdeve.s11.santos.andreali.everhourprototype.WorkspaceCreateActivity
import com.mobdeve.s11.santos.andreali.everhourprototype.WorkspaceDetailsActivity

class WorkspaceActivity : AppCompatActivity() {

    private lateinit var binding: WorkspaceOverviewBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var dbRef: DatabaseReference
    private lateinit var workspaceAdapter: WorkspaceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = WorkspaceOverviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: return

        binding.rvWorkspaces.layoutManager = LinearLayoutManager(this)
        workspaceAdapter = WorkspaceAdapter(mutableListOf()) { workspace ->
            val intent = Intent(this, WorkspaceDetailsActivity::class.java).apply {
                putExtra("WORKSPACE_ID", workspace.id) // Pass the workspace ID
            }
            startActivity(intent)
        }
        binding.rvWorkspaces.adapter = workspaceAdapter

        fetchWorkspaces() // Initial fetch

        // Handle button click to create a new workspace
        binding.btnCreateWs.setOnClickListener {
            val intent = Intent(this, WorkspaceCreateActivity::class.java)
            startActivityForResult(intent, CREATE_WORKSPACE_REQUEST_CODE)
        }

        // Handle navigation
        binding.ivHome.setOnClickListener {
            // already here
        }
        binding.ivReport.setOnClickListener{
            //TODO: place report activity here
        }
        binding.ivAccount.setOnClickListener{
            val intent = Intent(this, AccountActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Fetch workspaces and update the RecyclerView
    fun fetchWorkspaces() {
        val userId = auth.currentUser?.uid ?: return
        dbRef.child("workspaces").child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val workspaces = mutableListOf<Workspace>()
                for (data in snapshot.children) {
                    val workspace = data.getValue(Workspace::class.java)
                    if (workspace != null) {
                        workspaces.add(workspace)
                    }
                }
                workspaceAdapter.updateData(workspaces)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@WorkspaceActivity, "Failed to load workspaces.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CREATE_WORKSPACE_REQUEST_CODE && resultCode == RESULT_OK) {
            fetchWorkspaces() // Refresh workspaces when a new one is created
        }
    }

    companion object {
        private const val CREATE_WORKSPACE_REQUEST_CODE = 1
    }
}

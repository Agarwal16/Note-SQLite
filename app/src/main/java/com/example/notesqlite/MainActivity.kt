package com.example.notesqlite

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.notesqlite.adapter.NotesAdapter
import com.example.notesqlite.database.NoteDatabaseHelper
import com.example.notesqlite.databinding.ActivityMainBinding
import com.example.notesqlite.fragments.AddNoteFragment
import com.example.notesqlite.fragments.LoginFragment
import com.example.notesqlite.fragments.UpdateNoteFragment

class MainActivity : AppCompatActivity(), NotesAdapter.OnNoteClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var db: NoteDatabaseHelper
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        if (!sharedPreferences.getBoolean("isLoggedIn", false)) {
            loadLoginFragment()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = NoteDatabaseHelper(this)
        notesAdapter = NotesAdapter(db.getAllNotes(), this)
        binding.notesRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.notesRecyclerView.adapter = notesAdapter

        binding.addButton.setOnClickListener {
            val addNoteFragment = AddNoteFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, addNoteFragment)
                .addToBackStack(null)
                .commit()
            binding.notesRecyclerView.visibility = View.GONE
            binding.addButton.visibility = View.GONE
            binding.notesHeading.visibility = View.GONE
            binding.fragmentContainer.visibility = View.VISIBLE
        }

        supportFragmentManager.setFragmentResultListener("addNoteRequestKey", this) { requestKey, bundle ->
            if (requestKey == "addNoteRequestKey") {
                val isNoteAdded = bundle.getBoolean("isNoteAdded", false)
                if (isNoteAdded) {
                    notesAdapter.refreshData(db.getAllNotes())
                }
                binding.notesRecyclerView.visibility = View.VISIBLE
                binding.addButton.visibility = View.VISIBLE
                binding.notesHeading.visibility = View.VISIBLE
            }
        }

        supportFragmentManager.setFragmentResultListener("updateNoteRequestKey", this) { requestKey, bundle ->
            if (requestKey == "updateNoteRequestKey") {
                val isNoteUpdated = bundle.getBoolean("isNoteUpdated", false)
                if (isNoteUpdated) {
                    notesAdapter.refreshData(db.getAllNotes())
                }
                binding.notesRecyclerView.visibility = View.VISIBLE
                binding.addButton.visibility = View.VISIBLE
                binding.notesHeading.visibility = View.VISIBLE
            }
        }
    }

    private fun loadLoginFragment() {
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, LoginFragment())
            .commit()
    }

    override fun onUpdateClick(noteId: Int) {
        val updateNoteFragment = UpdateNoteFragment().apply {
            arguments = Bundle().apply {
                putInt("note_id", noteId)
            }
        }
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, updateNoteFragment)
            .addToBackStack(null)
            .commit()
        binding.notesRecyclerView.visibility = View.GONE
        binding.addButton.visibility = View.GONE
        binding.notesHeading.visibility = View.GONE
        binding.fragmentContainer.visibility = View.VISIBLE
    }

    override fun onDeleteClick(noteId: Int) {
        db.deleteNote(noteId)
        notesAdapter.refreshData(db.getAllNotes())
        Toast.makeText(this, "Note Deleted", Toast.LENGTH_SHORT).show()
    }
}

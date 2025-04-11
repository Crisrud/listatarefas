package dev.cristianosilva.listatarefas.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.cristianosilva.listatarefas.R
import dev.cristianosilva.listatarefas.TaskActivity
import dev.cristianosilva.listatarefas.utils.Navigator

class TaskFragment : Fragment() {

    private lateinit var listView: ListView
    private val listItems = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var firebaseUser = firebaseAuth.currentUser
    private val uid = firebaseUser?.uid
    private val db_ref =  FirebaseDatabase.getInstance().getReference("users/${uid}/tasks")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_task, container, false)

        val listView = view.findViewById<ListView>(R.id.tasksList)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, listItems)
        listView.adapter = adapter

        if(firebaseUser != null){
            loadData(adapter,listView)
        }

        return view
    }

    private fun loadData(adapter: ArrayAdapter<String>, listView: ListView) {
        db_ref.addValueEventListener(object : ValueEventListener {

            var ctx = this@TaskFragment.requireContext()
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                listItems.clear()


                for(taskSnapshot in dataSnapshot.children) {
                    val task = taskSnapshot.child("title").value.toString()
                    listItems.add(task)

                }
                adapter.notifyDataSetChanged()


                listView.setOnItemLongClickListener { parent, view, position, id ->

                    val task = listItems[position]
                    val taskId = dataSnapshot.children.toList()[position].key

                    // Remove the item from the list
                    if (taskId != null) {
                        AlertDialog.Builder(ctx)
                            .setTitle(R.string.remove_task)
                            .setMessage("${getString(R.string.remove_task_message)}${task}?")
                            .setPositiveButton(R.string.confirma) { dialog, which ->
                                db_ref.child(taskId).removeValue();
                                dialog.dismiss()
                                Toast.makeText(ctx, R.string.task_removed, Toast.LENGTH_SHORT).show()

                            }
                            .setNegativeButton(R.string.cancela) {
                                    dialog, which ->
                                dialog.dismiss()
                            }
                            .show()
                    };
                    true
                }

                listView.setOnItemClickListener { parent, view, position, id ->
                    val taskId = dataSnapshot.children.toList()[position].key


                    val act = Intent(ctx, TaskActivity::class.java)
                    act.putExtra("taskId", taskId)
                    Navigator.goToScreen(ctx, act)
                }


            }

            override fun onCancelled(databaseError: DatabaseError) {
                if (isAdded && FirebaseAuth.getInstance().currentUser != null) { // Verifica se o Fragment está attached
                    Toast.makeText(ctx, R.string.error_loading_tasks, Toast.LENGTH_SHORT).show()
                }
            }
        })

    }

}
package dev.cristianosilva.listatarefas.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.cristianosilva.listatarefas.R
import dev.cristianosilva.listatarefas.TaskActivity
import dev.cristianosilva.listatarefas.utils.Navigator

class ButFabFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_but_fab, container, false)

        val fabAddTask = view.findViewById<FloatingActionButton>(R.id.fab_add_task)

        fabAddTask.setOnClickListener {
            Navigator.goToScreen(requireContext(), TaskActivity::class.java)
         }

        return view
    }


}
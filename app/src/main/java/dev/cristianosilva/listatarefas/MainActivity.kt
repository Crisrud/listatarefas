package dev.cristianosilva.listatarefas

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import dev.cristianosilva.listatarefas.fragments.ButFabFragment
import dev.cristianosilva.listatarefas.fragments.TaskFragment
import dev.cristianosilva.listatarefas.fragments.WeatherFragment
import dev.cristianosilva.listatarefas.utils.AuthUtils
import dev.cristianosilva.listatarefas.utils.Navigator


data class Task(val id: Int, val title: String, val subtitle: String? = null)

class MainActivity : AppCompatActivity() {

    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();
    val firebaseUser: FirebaseUser? = firebaseAuth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {


        // Chama o método signOut() para desautenticar o usuário
        //firebaseAuth.signOut()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_weather, WeatherFragment()).commit()
        supportFragmentManager.beginTransaction().replace(R.id.fragment_but_fab, ButFabFragment()).commit()

        if (firebaseUser != null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_tasks, TaskFragment())
                .commit()
        }

       // val fabAddTask = findViewById<FloatingActionButton>(R.id.fab_add_task)
        val logout = findViewById<ImageView>(R.id.logout)
        val profile = findViewById<ImageView>(R.id.profile)
        //val listView = findViewById<ListView>(R.id.tasksList)


        //val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        //listView.adapter = adapter



        //fabAddTask.setOnClickListener{
        //    Navigator.goToScreen(this,TaskActivity::class.java)
        //}

        logout.setOnClickListener {
            AuthUtils.logout(this);
        }

        profile.setOnClickListener {
            Navigator.goToScreen(this,ProfileActivity::class.java)
        }

        verifySession(firebaseUser)

        //if(firebaseUser != null){
        //    loadData(adapter,listView)
        //}


        requestNotificationPermission()

    }

    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }
    }




    private fun verifySession(firebaseUser: Any?){
        if (firebaseUser == null){
            Navigator.goToScreen(this,LoginActivity::class.java)

        }
    }






}
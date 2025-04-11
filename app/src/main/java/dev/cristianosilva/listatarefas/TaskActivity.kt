package dev.cristianosilva.listatarefas

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.Calendar


class TaskActivity : AppCompatActivity() {
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db_ref = FirebaseDatabase.getInstance().getReference("users/${uid}/tasks")
    var task_id: String = ""

    @SuppressLint("DefaultLocale")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        val title = findViewById<EditText>(R.id.title_input);
        val description = findViewById<EditText>(R.id.description_input);
        val date = findViewById<EditText>(R.id.date_input);
        val time = findViewById<EditText>(R.id.time_input);

        val saveBtn = findViewById<Button>(R.id.save_btn)
        val backButton = findViewById<ImageView>(R.id.back_button)

        val dateIcon = findViewById<ImageView>(R.id.date_icon);
        val timeIcon = findViewById<ImageView>(R.id.time_icon);

        val day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val year = Calendar.getInstance().get(Calendar.YEAR)

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val minutes = Calendar.getInstance().get(Calendar.MINUTE)

        loadTask(title, description, date, time)

        saveBtn.setOnClickListener {
            val titleText = title.text.toString();
            val descriptionText = description.text.toString();
            val dateText = date.text.toString();
            val timeText = time.text.toString();

            saveTask(titleText, descriptionText, dateText, timeText )
        }

        fun openDatePicker(){
            val datePicker = DatePickerDialog(this,
                { _, ano, mes, dia ->
                    val selectedDate = String.format("%02d/%02d/%04d", dia, mes + 1, ano)
                    date.setText(selectedDate)
                },year, month, day);
            datePicker.show();
        }

        fun openTimePicker(){
            val timePicker = TimePickerDialog(this,
                { _, hora, minutos ->
                    val selectedTime = String.format("%02d:%02d", hora, minutos)
                    time.setText(selectedTime)
                },hour, minutes, true);
            timePicker.show();
        }

        /* Evento de click na data*/
        dateIcon.setOnClickListener {
            openDatePicker();

        }

        date.setOnClickListener {
            openDatePicker();
        }



        /* Evento de click no horário*/
        timeIcon.setOnClickListener {
            openTimePicker();
        }

        time.setOnClickListener {
            openTimePicker();
        }



        backButton.setOnClickListener {
            finish()
        }
    }

    fun loadTask(title: EditText, description: EditText, date: EditText, time: EditText) {
        this.task_id = intent.getStringExtra("taskId") ?: ""
        if(task_id === "") return


        db_ref.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val task = snapshot.child(task_id)
                        title.setText(task.child("title").value.toString())
                        description.setText(task.child("description").value.toString())
                        date.setText(task.child("date").value.toString())
                        time.setText(task.child("time").value.toString())
                    } else {
                        Toast.makeText(this@TaskActivity, R.string.task_not_found, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    Toast.makeText(this@TaskActivity, R.string.error_loading_task, Toast.LENGTH_SHORT).show()
                }
            }
        )

        /*db_ref.child(task_id).get().addOnSuccessListener { task ->
            if(task.exists()) {
                title.setText(task.child("title").value.toString())
                description.setText(task.child("description").value.toString())
                date.setText(task.child("date").value.toString())
                time.setText(task.child("time").value.toString())
            } else {
                Toast.makeText(this, R.string.task_not_found, Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, R.string.error_loading_task, Toast.LENGTH_SHORT).show()
        }*/
    }

    fun saveTask(title: String, description: String, date: String, time: String) {

        if(task_id === "") {
            val task = hashMapOf(
                "title" to title,
                "description" to description,
                "date" to date,
                "time" to time
            )

            db_ref.push().setValue(task);
            Toast.makeText(this, "Tarefa salva!", Toast.LENGTH_SHORT).show()
            android.os.Handler().postDelayed({
                finish()
            }, 5000)
        } else {
            val db_ref = FirebaseDatabase.getInstance().getReference("users/${uid}/tasks/${task_id}")

            db_ref.addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val task = snapshot.value as HashMap<String, String>

                            task["title"] = title
                            task["description"] = description
                            task["date"] = date
                            task["time"] = time

                            db_ref.setValue(task)
                            Toast.makeText(this@TaskActivity, R.string.task_updated, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@TaskActivity, R.string.task_not_found, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                        Toast.makeText(this@TaskActivity, R.string.error_loading_task, Toast.LENGTH_SHORT).show()
                    }
                }
            )


            android.os.Handler().postDelayed({
                finish()
            }, 3000)
        }

    }
}
package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_assign_skill.*
import kotlinx.android.synthetic.main.activity_assign_task.*
import java.util.*

class AssignTaskActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, AdminActivity::class.java))
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_task)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#FFFF5722")

        assign_task.setOnClickListener {

            if (task.text!!.isEmpty()) {
                task.error = "Task Name cannot be Empty"
                return@setOnClickListener
            } else if (task.length() > 15) {
                task.error = "Task Name cannot be more than 15 Characters Length. Enter Detailed Description Below."
                return@setOnClickListener
            } else if (desc.text!!.isEmpty()) {
                desc.error = "Task Description cannot be Empty"
                return@setOnClickListener
            } else if (xpoints.text.toString()!!.contains(" ") || xpoints.text.toString().contains(".") || xpoints.text.toString().contains(",") || xpoints.text.toString().contains("+") || xpoints.text.toString().contains("-")) {
                xpoints.error = "Bonus XP can only have numbers."
                return@setOnClickListener
            } else if (xpoints.text!!.isEmpty() || xpoints.text.toString() == "0") {
                xpoints.error = "Bonus XP cannot be 0. Otherwise, nobody might pick up the Task."
                return@setOnClickListener
            } else if (time.text!!.isEmpty()) {
                time.error = "Please provide an Estimated Time."
                return@setOnClickListener
            }

            val uuid = UUID.randomUUID()
            val ref = FirebaseDatabase.getInstance().getReference("/tasks/$uuid")
            ref.setValue(Tasks(uuid.toString(), task.text.toString(), desc.text.toString(), "Open", xpoints.text.toString().toInt(), time.text.toString(), "Not yet Taken"))
                .addOnSuccessListener {
                    task.setText("")
                    desc.setText("")
                    xpoints.setText("")
                    time.setText("")
                    Toast.makeText(this, "Task Assigned!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, it.toString(), Toast.LENGTH_LONG).show()
                }

        }

    }
}

class Tasks (val uuid : String, val task : String, val desc : String, val status : String, val xp : Int, val time : String, val takenBy : String) {
    constructor() : this("", "","","", 0, "", "")
}
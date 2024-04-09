package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_task.*

class TaskStatsActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MyStatsActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_stats)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#00FF5722")

        var uuid = intent.getStringExtra("Task")

        val ref = FirebaseDatabase.getInstance().getReference("/tasks")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(Tasks::class.java)
                if (data!!.uuid == uuid) {
                    task.text = data.task
                    description.text = data.desc
                    status.text = data.status
                    bonus.text = data.xp.toString() + " XP"
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

    }
}
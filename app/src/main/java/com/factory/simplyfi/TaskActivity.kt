package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_task.*

class TaskActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#00FF5722")

        var uuid = intent.getStringExtra("Task")

        val ref = FirebaseDatabase.getInstance().getReference("/tasks")
        ref.addChildEventListener(object :  ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(Tasks::class.java)
                if (data!!.uuid == uuid) {
                    task.text = data.task
                    description.text = data.desc
                    status.text = data.status
                    bonus.text = data.xp.toString() + " XP"
                    time.text = data.time

                    if (data.status == "Open") {
                        takeJob.visibility = View.VISIBLE
                    }

                    if (data.takenBy == FirebaseAuth.getInstance().uid && data.status == "Ongoing") {
                        completed.visibility = View.VISIBLE
                    }
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

        takeJob.setOnClickListener {
            var a = Alerter
            a.create(this)
                .setBackgroundColorInt(Color.GRAY)
                .setIcon(R.drawable.doubts)
                .setDuration(20000)
                .setTitle("Take Job")
                .setDismissable(false)
                .setText("Are you sure you want to Take Up this Job ?")
                .addButton("Yes", R.style.AlertButton, View.OnClickListener {


                    ref.addChildEventListener(object :  ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val data = snapshot.getValue(Tasks::class.java)
                            if (data!!.uuid == uuid) {
                                if (data.status == "Open") {
                                    FirebaseDatabase.getInstance().getReference("/tasks/$uuid/status")
                                        .setValue("Ongoing")
                                    FirebaseDatabase.getInstance().getReference("/tasks/$uuid/takenBy")
                                        .setValue(FirebaseAuth.getInstance().uid)
                                        .addOnSuccessListener {
                                            a.hide()
                                            Toast.makeText(this@TaskActivity, "Task Taken Successfully", Toast.LENGTH_LONG).show()
                                            finish()
                                            overridePendingTransition(0, 0)
                                            startActivity(getIntent())
                                            overridePendingTransition(0, 0)
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@TaskActivity, "Unable to Take Up Task", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    Toast.makeText(this@TaskActivity, "This task was taken by another Oompa Loompa just now", Toast.LENGTH_LONG).show()
                                    finish()
                                    overridePendingTransition(0, 0)
                                    startActivity(getIntent())
                                    overridePendingTransition(0, 0)
                                }
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



                })
                .addButton("No", R.style.AlertButton, View.OnClickListener {
                    a.hide()
                })
                .enableIconPulse(true)
                .disableOutsideTouch()
                .show()
        }

        completed.setOnClickListener {
            var a = Alerter
            a.create(this)
                .setBackgroundColorInt(Color.GRAY)
                .setIcon(R.drawable.doubts)
                .setDuration(20000)
                .setTitle("Send for Approval")
                .setDismissable(false)
                .setText("Are you sure you have completed the Task and want to send it for Approval ?")
                .addButton("Yes", R.style.AlertButton, View.OnClickListener {


                    ref.addChildEventListener(object :  ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val data = snapshot.getValue(Tasks::class.java)
                            if (data!!.uuid == uuid) {
                                if (data.status == "Ongoing") {
                                    FirebaseDatabase.getInstance().getReference("/tasks/$uuid/status")
                                        .setValue("Pending Approval")
                                        .addOnSuccessListener {
                                            a.hide()
                                            Toast.makeText(this@TaskActivity, "Task Sent for Approval", Toast.LENGTH_LONG).show()
                                            finish()
                                            overridePendingTransition(0, 0)
                                            startActivity(getIntent())
                                            overridePendingTransition(0, 0)
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(this@TaskActivity, "Unable to Send for Approval", Toast.LENGTH_LONG).show()
                                        }
                                } else {
                                    Toast.makeText(this@TaskActivity, "Unable to Send for Approval", Toast.LENGTH_LONG).show()
                                }
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



                })
                .addButton("No", R.style.AlertButton, View.OnClickListener {
                    a.hide()
                })
                .enableIconPulse(true)
                .disableOutsideTouch()
                .show()
        }

    }
}
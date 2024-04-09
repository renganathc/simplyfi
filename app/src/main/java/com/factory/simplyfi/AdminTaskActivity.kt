package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_admin_task.*
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.activity_task.bonus
import kotlinx.android.synthetic.main.activity_task.description
import kotlinx.android.synthetic.main.activity_task.status
import kotlinx.android.synthetic.main.activity_task.task
import kotlinx.android.synthetic.main.activity_task.time

class AdminTaskActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, ManageTasksActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_task)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#00FF5722")

        var uuid = intent.getStringExtra("Task")

        // Not taken as null to prevent Database errors.
        var uid : String? = FirebaseAuth.getInstance().uid
        var taskXP = 20

        val ref = FirebaseDatabase.getInstance().getReference("/tasks")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(Tasks::class.java)
                if (data!!.uuid == uuid) {
                    a_task.text = data.task
                    a_description.text = data.desc
                    a_status.text = data.status
                    a_bonus.text = data.xp.toString() + " XP"
                    a_time.text = data.time
                    uid = data.takenBy
                    taskXP = data.xp

                    if (data.status == "Pending Approval") {
                        adminApprove.visibility = View.VISIBLE
                        adminReject.visibility = View.VISIBLE
                    }

                    val dref = FirebaseDatabase.getInstance().getReference("/users")
                    dref.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val data2 = snapshot.getValue(User::class.java)
                            if (data2!!.uid == uid) {
                                a_taken_by.text = data2.username
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

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }

        })






        adminApprove.setOnClickListener {

            val fref = FirebaseDatabase.getInstance().getReference("/tasks")
            fref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val data7 = snapshot.getValue(Tasks::class.java)
                    if (data7!!.uuid == uuid) {


                        if (data7.status == "Pending Approval") {
                            FirebaseDatabase.getInstance().getReference("/tasks/$uuid/status")
                                .setValue("Completed")
                            var ref4 = FirebaseDatabase.getInstance().getReference("/users")
                            ref4.addChildEventListener(object : ChildEventListener {
                                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                                    var data5 = snapshot.getValue(User::class.java)
                                    if (data5!!.uid == uid) {
                                        var x = data5.xp

                                        FirebaseDatabase.getInstance().getReference("/users/${data5.uid}/xp")
                                            .setValue(x+taskXP)
                                            .addOnSuccessListener {
                                                Toast.makeText(this@AdminTaskActivity, "Task Approved!", Toast.LENGTH_LONG).show()
                                                finish()
                                                overridePendingTransition(0, 0)
                                                startActivity(getIntent())
                                                overridePendingTransition(0, 0)
                                            }
                                            .addOnFailureListener {
                                                Toast.makeText(this@AdminTaskActivity, "Couldn't Approve Task", Toast.LENGTH_LONG).show()
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

                            } )
                        } else {
                            Toast.makeText(this@AdminTaskActivity, "The task was Approved/Rejected by another Admin just now.", Toast.LENGTH_LONG).show()
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

        }

        adminReject.setOnClickListener {

            val kref = FirebaseDatabase.getInstance().getReference("/tasks")
            kref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    val data8 = snapshot.getValue(Tasks::class.java)
                    if (data8!!.uuid == uuid) {


                        if (data8.status == "Pending Approval") {
                            FirebaseDatabase.getInstance().getReference("/tasks/$uuid/status")
                                .setValue("Ongoing")
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@AdminTaskActivity,
                                        "Task Rejected",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    finish()
                                    overridePendingTransition(0, 0)
                                    startActivity(getIntent())
                                    overridePendingTransition(0, 0)
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this@AdminTaskActivity,
                                        "Couldn't Reject Task",
                                        Toast.LENGTH_LONG
                                    ).show()
                                }
                        } else {
                            Toast.makeText(
                                this@AdminTaskActivity,
                                "The task was Approved/Rejected by another Admin just now.",
                                Toast.LENGTH_LONG
                            ).show()
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

        }

    }
}
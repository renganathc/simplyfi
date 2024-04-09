package com.factory.simplyfi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_admin_approval.*

class AdminApprovalActivity : AppCompatActivity() {
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, AdminActivity::class.java))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_approval)

        val uid = intent.getStringExtra("uid")

        var ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var details = snapshot.getValue(User::class.java)
                if (details!!.uid == uid) {
                    user_name.text = details.username
                    email_id.text = details.email
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

        approve.setOnClickListener {
            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var details = snapshot.getValue(User::class.java)
                    val user_uid = details!!.uid
                    if (user_uid == uid) {
                        if (details.accountType == "Administrator-Pending") {
                            FirebaseDatabase.getInstance()
                                .getReference("/users/$user_uid/accountType")
                                .setValue("Administrator")
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@AdminApprovalActivity,
                                        "Approved",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    startActivity(Intent(this@AdminApprovalActivity, AdminActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { Toast.makeText(
                                    this@AdminApprovalActivity,
                                    "An Error Occured.",
                                    Toast.LENGTH_LONG
                                ).show() }
                        } else if (details.accountType == "Administrator") {
                            Toast.makeText(
                                this@AdminApprovalActivity,
                                "This account was Approved by another Admin just now.",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this@AdminApprovalActivity, AdminActivity::class.java))
                            finish()
                        } else if (details.accountType == "Administrator-Rejected") {
                            Toast.makeText(
                                this@AdminApprovalActivity,
                                "This account was Rejected by another Admin just now.",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this@AdminApprovalActivity, AdminActivity::class.java))
                            finish()
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

        reject.setOnClickListener {
            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var details = snapshot.getValue(User::class.java)
                    val user_uid = details!!.uid
                    if (user_uid == uid) {
                        if (details.accountType == "Administrator-Pending") {
                            FirebaseDatabase.getInstance()
                                .getReference("/users/$user_uid/accountType")
                                .setValue("Administrator-Rejected")
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        this@AdminApprovalActivity,
                                        "Rejected",
                                        Toast.LENGTH_LONG
                                    ).show()
                                    startActivity(Intent(this@AdminApprovalActivity, AdminActivity::class.java))
                                    finish()
                                }
                                .addOnFailureListener { Toast.makeText(
                                    this@AdminApprovalActivity,
                                    "An Error Occured.",
                                    Toast.LENGTH_LONG
                                ).show() }
                        } else if (details.accountType == "Administrator") {
                            Toast.makeText(
                                this@AdminApprovalActivity,
                                "This account was Approved by another Admin just now.",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this@AdminApprovalActivity, AdminActivity::class.java))
                            finish()
                        } else if (details.accountType == "Administrator-Rejected") {
                            Toast.makeText(
                                this@AdminApprovalActivity,
                                "This account was Rejected by another Admin just now.",
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this@AdminApprovalActivity, AdminActivity::class.java))
                            finish()
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
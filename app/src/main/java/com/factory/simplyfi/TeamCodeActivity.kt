package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_admin.info4
import kotlinx.android.synthetic.main.activity_team_code.code
import kotlinx.android.synthetic.main.activity_team_code.join_button

class TeamCodeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_team_code)

        join_button.setOnClickListener{


            var ref = FirebaseDatabase.getInstance().getReference("/users")

            ref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                    val data = p0.getValue(User::class.java)

                    if (data!!.teamID == code.text.toString()) {

                        updateAccount(code.text.toString())

                    }
                }

                override fun onChildChanged(p0: DataSnapshot, p1: String?) {

                }

                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onChildMoved(p0: DataSnapshot, p1: String?) {

                }

                override fun onChildRemoved(p0: DataSnapshot) {

                }
            })

            Alerter.Companion.create(this)
                .disableOutsideTouch()
                .enableProgress(true)
                .setTitle("Requesting Approval")
                .setDuration(3500)
                .setDismissable(false)
                .setBackgroundColorInt(Color.GRAY)
                .setText("Please wait ...")
                .show()





        }
    }

    private fun updateAccount(code: String) {

        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var details = snapshot.getValue(User::class.java)
                val user_uid = details!!.uid
                if (user_uid == FirebaseAuth.getInstance().uid) {
                        FirebaseDatabase.getInstance()
                            .getReference("/users/$user_uid/accountType")
                            .setValue("Staff-Pending")
                            .addOnSuccessListener {
                                FirebaseDatabase.getInstance()
                                    .getReference("/users/$user_uid/teamID")
                                    .setValue(code)
                                    .addOnSuccessListener {
                                        Toast.makeText(
                                            this@TeamCodeActivity,
                                            "Joined Team Approval List",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        startActivity(Intent(this@TeamCodeActivity, AdminVPActivity::class.java))
                                        finish()
                                    }

                            }
                            .addOnFailureListener { Toast.makeText(
                                this@TeamCodeActivity,
                                "An Error Occured.",
                                Toast.LENGTH_LONG
                            ).show()
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
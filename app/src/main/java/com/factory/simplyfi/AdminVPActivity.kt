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
import com.google.firebase.ktx.Firebase
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_admin_vpactivity.*
import kotlinx.android.synthetic.main.activity_dashboard.*

class AdminVPActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_vpactivity)

        supportActionBar?.hide()

        var ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var userinfo = p0.getValue(User::class.java)
                if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString()) {
                    g_name3.text = userinfo.username
                    if (userinfo.accountType == "Administrator-Rejected") {
                        ad_text.text = "\uD83D\uDE2D\uD83D\uDE25 Sorry, but your Admin Account was not Approved."
                        c_staff.visibility = View.VISIBLE
                    }
                }
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                var userinfo = p0.getValue(User::class.java)
                if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString()) {
                    g_name3.text = userinfo.username
                    if (userinfo.accountType == "Administrator-Rejected") {
                        ad_text.text = "\uD83D\uDE2D\uD83D\uDE25 Sorry, but your Admin Account was not Approved."
                        c_staff.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }
        })

        delete.setOnClickListener {
            var a = Alerter
            a.create(this)
                .setBackgroundColorInt(Color.GRAY)
                .setIcon(R.drawable.ic_baseline_follow_the_signs_24)
                .setTitle("Delete Account")
                .setText("Are you sure you want to Delete your Account ? This action cannot be reversed.")
                .addButton("Yes", R.style.AlertButton, View.OnClickListener {
                    if (FirebaseAuth.getInstance().uid != null) {
                        FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().uid}/accountType")
                            .setValue("Deleted")
                        FirebaseAuth.getInstance().currentUser!!.delete()
                            .addOnSuccessListener {
                                Toast.makeText(this, "Account Deleted", Toast.LENGTH_LONG).show()
                                startActivity(Intent(this, MainActivity::class.java))
                                finishAffinity()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Unable to Delete Account", Toast.LENGTH_LONG).show()
                            }
                    } else {
                        Toast.makeText(this, "Account is already Deleted", Toast.LENGTH_LONG).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finishAffinity()
                    }
                })
                .addButton("No", R.style.AlertButton, View.OnClickListener {
                    a.hide()
                })
                .enableIconPulse(true)
                .disableOutsideTouch()
                .show()
        }

        sign_out.setOnClickListener {
            var a = Alerter
            a.create(this)
                .setBackgroundColorInt(Color.GRAY)
                .setIcon(R.drawable.ic_baseline_follow_the_signs_24)
                .setTitle("Sign Out")
                .setText("Are you sure you want to sign out ??")
                .addButton("Yes", R.style.AlertButton, View.OnClickListener {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this, "Signed Out Successfully", Toast.LENGTH_SHORT).show()
                    finishAffinity()
                })
                .addButton("No", R.style.AlertButton, View.OnClickListener {
                    a.hide()
                })
                .enableIconPulse(true)
                .disableOutsideTouch()
                .show()
        }

        c_staff.setOnClickListener {
            var a = Alerter
            a.create(this)
                .setBackgroundColorInt(Color.GRAY)
                .setIcon(R.drawable.ic_baseline_follow_the_signs_24)
                .setTitle("Staff Account")
                .setText("Are you sure you want to continue as a Staff Member ? This action cannot be reversed.")
                .addButton("Yes", R.style.AlertButton, View.OnClickListener {
                    var ref2 = FirebaseDatabase.getInstance().getReference("/users")
                    ref2.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                            var data3 = snapshot.getValue(User::class.java)
                            if(data3!!.uid == FirebaseAuth.getInstance().uid) {
                                FirebaseDatabase.getInstance().getReference("/users/${data3.uid}/accountType").setValue("Staff")
                                    .addOnSuccessListener {
                                        Toast.makeText(this@AdminVPActivity, "You are now a Staff Member", Toast.LENGTH_LONG).show()
                                        startActivity(Intent(this@AdminVPActivity, DashboardActivity::class.java))
                                        finishAffinity()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this@AdminVPActivity, "An error Occured.", Toast.LENGTH_LONG).show()
                                    }
                            }
                        }

                        override fun onChildChanged(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
                        }

                        override fun onChildRemoved(snapshot: DataSnapshot) {
                        }

                        override fun onChildMoved(
                            snapshot: DataSnapshot,
                            previousChildName: String?
                        ) {
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
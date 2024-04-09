package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()

        this.window.statusBarColor = Color.parseColor("#82d1d5")

        register.setOnClickListener {
            var intent = Intent(this, RegistrationActivity::class.java)
            startActivity(intent)
            finish()
        }

        login_button.setOnClickListener {

            //var progressbar = ProgressDialog(this)
            //progressbar.setCancelable(false)
            //progressbar.setMessage("Signing In to your account...")
            //progressbar.show()

            //Handler().postDelayed({progressbar.dismiss()},2100)

            Alerter.Companion.create(this)
                .disableOutsideTouch()
                .enableProgress(true)
                .setTitle("Signing In...")
                .setDuration(11000)
                .setDismissable(false)
                .setDuration(20000)
                .setBackgroundColorInt(Color.RED)
                .setText("Please wait while we sign you in")
                .show()

            var email = email_id2.text.toString().trim()
            var password = passwrd.text.toString().trim()

            if (email.isEmpty()){
                email_id2.error = "Email cannot be empty"
                return@setOnClickListener
            }
            else if (password.isEmpty()){
                Toast.makeText(this, "Please enter your Password", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    var ref = FirebaseDatabase.getInstance().getReference("/users")

                    ref.addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                            var userinfo = p0.getValue(User::class.java)

                            if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString()) {
                                if (userinfo.accountType == "Staff") {
                                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    Toast.makeText(this@MainActivity, "Signed In successfully", Toast.LENGTH_SHORT).show()
                                } else if (userinfo.accountType == "Administrator") {
                                    val intent = Intent(this@MainActivity, AdminActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    Toast.makeText(this@MainActivity, "Signed In successfully", Toast.LENGTH_SHORT).show()
                                } else if (userinfo.accountType == "Administrator-Pending") {
                                    val intent = Intent(this@MainActivity, AdminVPActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    Toast.makeText(this@MainActivity, "Signed In successfully", Toast.LENGTH_SHORT).show()
                                } else if (userinfo.accountType == "Administrator-Rejected") {
                                    val intent = Intent(this@MainActivity, AdminVPActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                    Toast.makeText(this@MainActivity, "Signed In successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Alerter.Companion.create(this@MainActivity)
                                        .setTitle("Couldn't Sign You In")
                                        .setDuration(4000)
                                        .enableSwipeToDismiss()
                                        .setBackgroundColorInt(Color.RED)
                                        .setIcon(R.drawable.ic_baseline_error_outline_24)
                                        .enableIconPulse(true)
                                        .show()
                                }
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
                }
                .addOnFailureListener {
                    if(it.message == "The email address is badly formatted.") {
                        email_id2.error = "Email is not in proper format"
                    }else if(it.message == "The password is invalid or the user does not have a password.") {
                        Alerter.Companion.create(this)
                            .setTitle("Couldn't Sign You In")
                            .setText("Incorrect Password")
                            .setDuration(4000)
                            .enableSwipeToDismiss()
                            .setBackgroundColorInt(Color.RED)
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .enableIconPulse(true)
                            .show()
                    } else if (it.message == "There is no user record corresponding to this identifier. The user may have been deleted."){
                        Alerter.Companion.create(this)
                            .setTitle("Couldn't Sign You In")
                            .setText("There is no user using this account. Please create an account by registering with us.")
                            .setDuration(4000)
                            .enableSwipeToDismiss()
                            .setBackgroundColorInt(Color.RED)
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .enableIconPulse(true)
                            .show()
                    } else {
                        Alerter.Companion.create(this)
                            .setTitle("Couldn't Sign You In")
                            .setText("${it.message}")
                            .setDuration(4000)
                            .enableSwipeToDismiss()
                            .setBackgroundColorInt(Color.RED)
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .enableIconPulse(true)
                            .show()
                    }
                }
        }

    }
}
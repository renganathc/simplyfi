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

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#FF5722")

        Handler().postDelayed({
            if (FirebaseAuth.getInstance().uid != null) {

                var signedIn = false
                var ref = FirebaseDatabase.getInstance().getReference("/users")


                    ref.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        var userinfo = p0.getValue(User::class.java)

                        if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString() && !signedIn) {
                            if (userinfo.accountType == "Staff") {
                                val intent = Intent(this@SplashScreenActivity, DashboardActivity::class.java)
                                signedIn = true
                                startActivity(intent)
                                finish()
                            } else if (userinfo.accountType == "Administrator") {
                                val intent = Intent(this@SplashScreenActivity, AdminActivity::class.java)
                                signedIn = true
                                startActivity(intent)
                                finish()
                            } else if (userinfo.accountType == "Administrator-Pending") {
                                val intent = Intent(this@SplashScreenActivity, AdminVPActivity::class.java)
                                signedIn = true
                                startActivity(intent)
                                finish()
                            } else if (userinfo.accountType == "Administrator-Rejected") {
                                val intent = Intent(this@SplashScreenActivity, AdminVPActivity::class.java)
                                signedIn = true
                                startActivity(intent)
                                finish()
                            } else {
                                val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                                signedIn = true
                                Toast.makeText(this@SplashScreenActivity, "Unable to Sign In",  Toast.LENGTH_LONG).show()
                                startActivity(intent)
                                finish()
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




                Handler().postDelayed({
                    if (!signedIn) {
                        val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                        Toast.makeText(
                            this@SplashScreenActivity,
                            "Unable to Sign In. Please check your Network Connection",
                            Toast.LENGTH_LONG
                        ).show()
                        signedIn = true
                        startActivity(intent)
                        finish()

                    }
                }, 7000)


            }
            else {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }, 1100)

    }
}
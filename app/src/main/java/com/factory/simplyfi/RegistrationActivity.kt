package com.factory.simplyfi

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.tapadoo.alerter.Alerter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.email_id2
import kotlinx.android.synthetic.main.activity_registration.*
import java.util.*

class RegistrationActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()

        if (free == false) {
            Handler().postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
            }, 3000)
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    var accountType = "null"
    var free = true

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            // Is the button now checked?
            val checked = view.isChecked

            // Check which radio button was clicked
            when (view.getId()) {
                R.id.staff ->
                    if (checked) {
                        accountType = "Team-Code-Pending"
                    }
                R.id.admin ->
                    if (checked) {
                        accountType = "Administrator"
                    }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        supportActionBar?.hide()

        register_button.setOnClickListener {

            free = false
            register_button.isEnabled = false

            val un = username.text.toString().trim()
            val email = email_id2.text.toString().trim()
            // mail = email_id.text.toString().trim()
            val password = pw.text.toString().trim()
            val cpa = cpw.text.toString().trim()


            if (email.isEmpty() && un.isEmpty()) {
                email_id2.error = "Enter an Email"
                username.error = "Enter a Username"
                free = true
                register_button.isEnabled = true
                return@setOnClickListener
            } else if (un.length > 20) {
                username.error = "Username should not be more than 20 characters long"
                free = true
                register_button.isEnabled = true
                return@setOnClickListener
            } else if (email.isEmpty()) {
                email_id2.error = "Enter an Email"
                free = true
                register_button.isEnabled = true
                return@setOnClickListener
            } else if (password.isEmpty()) {
                Toast.makeText(this, "Please enter a Password", Toast.LENGTH_LONG).show()
                free = true
                register_button.isEnabled = true
                return@setOnClickListener
            } else if(password.length < 6) {
                Toast.makeText(this, "Password should be at least 6 Characters long", Toast.LENGTH_SHORT).show()
                free = true
                register_button.isEnabled = true
                return@setOnClickListener
            } else if (password != cpa) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_LONG).show()
                free = true
                register_button.isEnabled = true
                return@setOnClickListener
            } else if (uriOfImage == null) {
                Toast.makeText(this, "Please choose a Profile Picture", Toast.LENGTH_SHORT).show()
                free = true
                register_button.isEnabled = true
                return@setOnClickListener
            }

            Log.d("MainActivity", "Email : $email")
            Log.d("MainActivity", "Password : $password")

            Alerter.Companion.create(this)
                .disableOutsideTouch()
                .enableProgress(true)
                .setTitle("Creating your Account...")
                .setDuration(15000)
                .setDismissable(false)
                .setBackgroundColorInt(Color.GRAY)
                .setText("Please wait while we are creating your account $un ...")
                .show()

            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) {
                        register_button.isEnabled = true
                        free = true
                        return@addOnCompleteListener
                    }

                    saveFileToDatabase("Image URL not assigned")
                    Log.d("MainActivity", "User created with UID: ${it.result!!.user!!.uid}")
                }
                .addOnFailureListener {
                    register_button.isEnabled = true
                    free = true
                    Log.d("MainActivity", "Account could not be created : ${it.message}")
                    if(it.message == "The email address is badly formatted."){
                        email_id2.error = "Email is not in proper format"
                        return@addOnFailureListener
                    }
                    else{
                        Alerter.Companion.create(this)
                            .setTitle("Couldn't Create Account !!")
                            .setText(it.message.toString())
                            .setDuration(4000)
                            .setIcon(R.drawable.ic_baseline_error_outline_24)
                            .enableSwipeToDismiss()
                            .setBackgroundColorInt(Color.RED)
                            .show()
                    }
                }
        }

        profile_image.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }

        back_to_login.setOnClickListener {
            startActivity(Intent(this@RegistrationActivity, MainActivity::class.java))
            finish()
        }
    }

    var uriOfImage : Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            uriOfImage = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriOfImage)
            profile_image.setImageBitmap(bitmap)
            //profile_image.borderWidth = 5
        }
    }

    // Temporarily not in use...

    private fun saveFileToDatabase(photoUrl: String){
        var url = photoUrl
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val dddref = FirebaseDatabase.getInstance().getReference("/users/$uid")
        val email = email_id2.text.toString().trim()
        var user = User()


        if (accountType == "Team-Code-Pending") {
            user = User(uid, username.text.toString(), url, email, accountType!!, 0, 0, "Pending")
        } else if (accountType == "Administrator") {

            val chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"
            val random = java.util.Random()
            val result = StringBuilder(6)

            repeat(6) {
                result.append(chars[random.nextInt(chars.length)])
            }

            user = User(uid, username.text.toString(), url, email, accountType!!, 0, 0, result.toString())
        }


        dddref.setValue(user)
            .addOnSuccessListener {
                Alerter.Companion.create(this)
                    .setTitle("Account Created Successfully !!")
                    .setDuration(4000)
                    .setIcon(R.drawable.ic_baseline_accessibility_new_24)
                    .enableSwipeToDismiss()
                    .setBackgroundColorInt(Color.GREEN)
                    .show()

                register_button.isEnabled = false

                var ref = FirebaseDatabase.getInstance().getReference("/users")

                ref.addChildEventListener(object : ChildEventListener {
                    override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                        var userinfo = p0.getValue(User::class.java)

                        if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString()) {
                            if (userinfo.accountType == "Staff") {
                                val intent = Intent(this@RegistrationActivity, DashboardActivity::class.java)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@RegistrationActivity, "Signed In successfully", Toast.LENGTH_SHORT).show()
                            } else if (userinfo.accountType == "Team-Code-Pending") {
                                val intent = Intent(this@RegistrationActivity, TeamCodeActivity::class.java)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@RegistrationActivity, "Signed In successfully", Toast.LENGTH_SHORT).show()
                            }
                            else if (userinfo.accountType == "Administrator") {
                                val intent = Intent(this@RegistrationActivity, AdminActivity::class.java)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@RegistrationActivity, "Signed In successfully", Toast.LENGTH_SHORT).show()
                            } else if (userinfo.accountType == "Staff-Pending") {
                                val intent = Intent(this@RegistrationActivity, AdminVPActivity::class.java)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@RegistrationActivity, "Signed In successfully", Toast.LENGTH_SHORT).show()
                            } else if (userinfo.accountType == "Staff-Rejected") {
                                val intent = Intent(this@RegistrationActivity, AdminVPActivity::class.java)
                                startActivity(intent)
                                finish()
                                Toast.makeText(this@RegistrationActivity, "Signed In successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Alerter.Companion.create(this@RegistrationActivity)
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
                Alerter.Companion.create(this)
                    .setTitle("Couldn't Create Account !!")
                    .setText(it.message.toString())
                    .setDuration(4000)
                    .setIcon(R.drawable.ic_baseline_error_outline_24)
                    .enableSwipeToDismiss()
                    .setBackgroundColorInt(Color.RED)
                    .show()

                register_button.isEnabled = true
                free = true
            }
    }

}
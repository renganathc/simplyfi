package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_assign_skill.*
import java.util.*

class AssignSkillActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, AdminActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_assign_skill)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#FFFF5722")

        add_button.setOnClickListener {
            if (skill_title.text!!.isEmpty()) {
                skill_title.error = "Skill Name cannot be Empty"
                return@setOnClickListener
            } else if (skill_title.length() > 40) {
                skill_title.error = "Skill Name cannot be more than 40 Characters Length. Enter Detailed Description Below."
                return@setOnClickListener
            }
            else if (skill_desc.text!!.isEmpty()) {
                skill_desc.error = "Skill Description cannot be Empty"
                return@setOnClickListener
            }

            var uuid = UUID.randomUUID().toString()
            FirebaseDatabase.getInstance().getReference("/skills/$uuid")
                .setValue(Skill(skill_title.text.toString(), skill_desc.text.toString(), uuid))
                .addOnSuccessListener {
                    skill_title.setText("")
                    skill_desc.setText("")
                    Toast.makeText(this, "Skill Uploaded!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "An error occured.", Toast.LENGTH_LONG).show()
                }
        }

    }
}

class Skill (val name : String, val desc : String, val uuid : String) {
    constructor() : this("","","")
}
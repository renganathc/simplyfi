package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_assign_skill.*
import kotlinx.android.synthetic.main.activity_skill_viewer.*

class SkillViewerActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, LearnSkillsActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_skill_viewer)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#00FF5722")

        var uuid = intent.getStringExtra("Skill")

        var ref = FirebaseDatabase.getInstance().getReference("/skills")
        ref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var sdata = snapshot.getValue(Skill::class.java)
                if (sdata!!.uuid == uuid) {
                    skill.text = sdata.name
                    skill_description.text = sdata.desc
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

        var mref = FirebaseDatabase.getInstance().getReference("/skill-readBy/$uuid")
        mref.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var data = snapshot.getValue(String::class.java)
                if (data == FirebaseAuth.getInstance().uid) {
                    read.isEnabled = false
                    read.text = "✅ Already Learnt"
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

        read.setOnClickListener {
            var gref = FirebaseDatabase.getInstance().getReference("/users")
            gref.addChildEventListener(object : ChildEventListener {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    var data = snapshot.getValue(User::class.java)
                    if (data!!.uid == FirebaseAuth.getInstance().uid) {
                        var skillsLearned = data.skillsRead
                        FirebaseDatabase.getInstance().getReference("/users/${FirebaseAuth.getInstance().uid}/skillsRead")
                            .setValue(skillsLearned+1)
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

            FirebaseDatabase.getInstance().getReference("/skill-readBy/$uuid/${FirebaseAuth.getInstance().uid}")
                .setValue(FirebaseAuth.getInstance().uid)
                .addOnSuccessListener {
                    Toast.makeText(this, "Marked as Learnt", Toast.LENGTH_SHORT).show()
                    read.isEnabled = false
                    read.text = "✅ Already Learnt"
                }
        }



    }
}
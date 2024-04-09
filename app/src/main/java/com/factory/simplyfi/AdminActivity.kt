package com.factory.simplyfi

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.tapadoo.alerter.Alerter
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_dashboard.greeting
import kotlinx.android.synthetic.main.admin_requests.view.*
import kotlinx.android.synthetic.main.from.view.*
import java.text.SimpleDateFormat
import java.util.*

class AdminActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        supportActionBar?.hide()

        val c = Calendar.getInstance().time

        var ref2 = FirebaseDatabase.getInstance().getReference("/users")

        ref2.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val userinfo = p0.getValue(User::class.java)

                if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString()) {
                    g_name2.text = userinfo.username
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

        var time = SimpleDateFormat("HH", Locale.getDefault())
        var formattedTime = time.format(c).toInt()

        if (formattedTime >= 6 && formattedTime < 11) greeting2.text = "Good Morning,"
        else if (formattedTime >= 11 && formattedTime < 16) greeting2.text = "Good Afternoon,"
        else if (formattedTime >= 16 && formattedTime < 16) greeting2.text = "Good Evening,"
        else greeting2.text = "Time to Rest,"

        var p = 0

        var ref = FirebaseDatabase.getInstance().getReference("/users")

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val data = p0.getValue(User::class.java)

                if (data!!.accountType == "Administrator-Pending") {
                    adapter.add(AdminRequests(data.username, data.email, data.uid))
                    p++
                    if (p>0) {
                        info4.visibility = View.INVISIBLE
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

        forum2.setOnClickListener {
            startActivity(Intent(this, ForumActivity::class.java))
        }

        assign.setOnClickListener {
            val intent = Intent(this, AssignTaskActivity::class.java)
            startActivity(intent)
            finish()
        }

        add_skill.setOnClickListener {
            val intent = Intent(this, AssignSkillActivity::class.java)
            startActivity(intent)
            finish()
        }

        approve.setOnClickListener {
            startActivity(Intent(this, ManageTasksActivity::class.java))
            finish()
        }

        exit.setOnClickListener {

            var a = Alerter
            a.create(this)
                .setBackgroundColorInt(Color.GRAY)
                .setIcon(R.drawable.ic_baseline_follow_the_signs_24)
                .setDuration(20000)
                .setDismissable(false)
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

        adapter.setOnItemClickListener { item, view ->
            val i = item as AdminRequests

            val intent = Intent(this, AdminApprovalActivity::class.java)
            intent.putExtra("uid", i.uid)
            startActivity(intent)
            finish()
        }

        recycler.adapter = adapter


    }
}

class AdminRequests(val name : String, var email : String, val uid : String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.name.text = name
        viewHolder.itemView.email.text = email
    }

    override fun getLayout(): Int {
        return  R.layout.admin_requests
    }
}
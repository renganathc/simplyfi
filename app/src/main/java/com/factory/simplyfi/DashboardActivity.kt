package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
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
import kotlinx.android.synthetic.main.activity_my_stats.*
import kotlinx.android.synthetic.main.pending_tasks.view.*
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        supportActionBar?.hide()

        var ccref = FirebaseDatabase.getInstance().getReference("/users")

        ccref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var userinfo = p0.getValue(User::class.java)

                if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString()) {
                    g_name.text = userinfo.username
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

        taskRecycler.adapter = adapter

        adapter.setOnItemClickListener { item, view ->
            val i = item as PendingTask

            val intent = Intent(this, TaskActivity::class.java)
            intent.putExtra("Task", i.data.uuid)
            startActivity(intent)
            finish()
        }

        var p = 0

        var ddref = FirebaseDatabase.getInstance().getReference("/tasks")

        ddref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(Tasks::class.java)
                if (data!!.status != "Completed") {
                    adapter.add(PendingTask(data!!))
                    p++
                    if (p>0) {
                        info2.visibility = View.INVISIBLE
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

        stats.setOnClickListener {
            startActivity(Intent(this, MyStatsActivity::class.java))
            finish()
        }

        forum.setOnClickListener {
            startActivity(Intent(this, ForumActivity::class.java))
        }

        skills.setOnClickListener {
            startActivity(Intent(this, LearnSkillsActivity::class.java))
            finish()
        }


        sign.setOnClickListener {

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

        val c = Calendar.getInstance().time

        var time = SimpleDateFormat("HH", Locale.getDefault())
        var formattedTime = time.format(c).toInt()

         if (formattedTime >= 6 && formattedTime < 11) greeting.text = "Good Morning,"
         else if (formattedTime >= 11 && formattedTime < 16) greeting.text = "Good Afternoon,"
         else if (formattedTime >= 16 && formattedTime < 16) greeting.text = "Good Evening,"
         else greeting.text = "Time to Rest,"

    }
}

class PendingTask(val data : Tasks) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.task.text =  data.task
        viewHolder.itemView.status.text = "\uD83D\uDD17 Status : " + data.status
        viewHolder.itemView.bonus.text = "⭐ Bonus : " + data.xp.toString() + " XP"
        viewHolder.itemView.time.text = "⌚ Est. Time : " + data.time

        if (data.takenBy == FirebaseAuth.getInstance().uid) {
            viewHolder.itemView.card.setCardBackgroundColor(Color.parseColor("#84CC87"))
        }

    }

    override fun getLayout(): Int {
        return R.layout.pending_tasks
    }

}
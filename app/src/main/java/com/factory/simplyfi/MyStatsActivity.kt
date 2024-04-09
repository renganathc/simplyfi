package com.factory.simplyfi

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_admin.*
import kotlinx.android.synthetic.main.activity_manage_tasks.*
import kotlinx.android.synthetic.main.activity_manage_tasks.ataskRecycler
import kotlinx.android.synthetic.main.activity_my_stats.*
import kotlinx.android.synthetic.main.pending_tasks.view.*

class MyStatsActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_stats)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#00FF5722")

        var ref4 = FirebaseDatabase.getInstance().getReference("/users")

        ref4.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val userinfo = p0.getValue(User::class.java)

                if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString()) {
                    bonus_earned.text = "Total Bonus Earned : " + userinfo.xp.toString() + " XP"
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


        mtaskRecycler.adapter = adapter

        var p = 0

        var dref = FirebaseDatabase.getInstance().getReference("/tasks")

        dref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(Tasks::class.java)
                if (data!!.status == "Completed" && data.takenBy == FirebaseAuth.getInstance().uid) {
                    adapter.add(MyStats(data!!))
                    p++
                    if (p>0) {
                        info.visibility = View.INVISIBLE
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

        adapter.setOnItemClickListener { item, view ->
            val i = item as MyStats

            val intent = Intent(this, TaskStatsActivity::class.java)
            intent.putExtra("Task", i.data.uuid)
            startActivity(intent)
            finish()
        }
    }
}

class MyStats(val data : Tasks) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.task.text =  data.task
        viewHolder.itemView.status.text = "\uD83D\uDD17 Status : " + data.status
        viewHolder.itemView.bonus.text = "⭐ Bonus Earned : " + data.xp.toString() + " XP"
    }

    override fun getLayout(): Int {
        return R.layout.my_stats
    }

}
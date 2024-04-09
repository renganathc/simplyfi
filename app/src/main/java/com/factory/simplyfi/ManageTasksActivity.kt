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
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_manage_tasks.*
import kotlinx.android.synthetic.main.pending_tasks.view.*

class ManageTasksActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, AdminActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_tasks)


        supportActionBar?.hide()


        ataskRecycler.adapter = adapter

        var p = 0

        var dref = FirebaseDatabase.getInstance().getReference("/tasks")

        dref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(Tasks::class.java)
                adapter.add(AdminPendingTask(data!!))
                p++
                total_tasks.text = "Total Tasks : " + p
                if (p>0) {
                    info3.visibility = View.INVISIBLE
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
            val i = item as AdminPendingTask

            val intent = Intent(this, AdminTaskActivity::class.java)
            intent.putExtra("Task", i.data.uuid)
            startActivity(intent)
            finish()
        }

    }
}

class AdminPendingTask(val data : Tasks) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.task.text =  data.task
        viewHolder.itemView.status.text = "\uD83D\uDD17 Status : " + data.status
        viewHolder.itemView.bonus.text = "⭐ Bonus : " + data.xp.toString() + " XP"
        viewHolder.itemView.time.text = "⌚ Est. Time : " + data.time

        if (data.status == "Pending Approval") {
            viewHolder.itemView.card.setCardBackgroundColor(Color.parseColor("#84CC87"))
        }

    }

    override fun getLayout(): Int {
        return R.layout.pending_tasks
    }

}
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
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_learn_skills.*
import kotlinx.android.synthetic.main.activity_manage_tasks.*
import kotlinx.android.synthetic.main.activity_manage_tasks.ataskRecycler
import kotlinx.android.synthetic.main.activity_manage_tasks.info3
import kotlinx.android.synthetic.main.pending_tasks.view.*
import kotlinx.android.synthetic.main.skill_card.view.*

class LearnSkillsActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, DashboardActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn_skills)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#00FF5722")

        skillsRecycler.adapter = adapter

        var p = 0

        var gref = FirebaseDatabase.getInstance().getReference("/users")
        gref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                var data = snapshot.getValue(User::class.java)
                if (data!!.uid == FirebaseAuth.getInstance().uid) {
                    read.text = "Total Skills Read : " + data.skillsRead
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

        var dref = FirebaseDatabase.getInstance().getReference("/skills")

        dref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val data = snapshot.getValue(Skill::class.java)
                adapter.add(Skills(data!!))
                p++
                if (p>0) {
                    infos.visibility = View.INVISIBLE
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
            val i = item as Skills

            val intent = Intent(this, SkillViewerActivity::class.java)
            intent.putExtra("Skill", i.data.uuid)
            startActivity(intent)
            finish()
        }

    }
}

class Skills(val data : Skill) : Item<ViewHolder>() {

    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.skill.text =  data.name

    }

    override fun getLayout(): Int {
        return R.layout.skill_card
    }

}
package com.factory.simplyfi

import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.factory.simplyfi.R.layout.admin_from
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.activity_forum.*
import kotlinx.android.synthetic.main.admin_from.view.*
import kotlinx.android.synthetic.main.admin_to.view.*
import kotlinx.android.synthetic.main.from.view.*
import kotlinx.android.synthetic.main.to.view.*

const val TOPIC = "/topics/myTopic2"

class ForumActivity : AppCompatActivity() {

    var p = -1

    val adapter = GroupAdapter<ViewHolder>()
    var accountType = "Staff"
    var xp = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forum)

        supportActionBar?.hide()
        this.window.statusBarColor = Color.parseColor("#00FF5722")

        var dref = FirebaseDatabase.getInstance().getReference("/users")
        dref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var userinfo = p0.getValue(User::class.java)

                if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString()) {
                    accountType = userinfo.accountType
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

        var ref = FirebaseDatabase.getInstance().getReference("/users")
        var name = "Oompa Loompa"

        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var userinfo = p0.getValue(User::class.java)

                if (userinfo!!.uid == FirebaseAuth.getInstance().uid.toString()) {
                    name = userinfo.username
                    xp = userinfo.xp
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

        sendbutton.setOnClickListener {
            if (messageboxtext.text.toString().isEmpty() || messageboxtext.text.toString() <= "                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            ") {
                Toast.makeText(this , "Message cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            else {
                sendTheMessage(name)
            }
        }




        var uid = FirebaseAuth.getInstance().uid





        listenForMessages()

        recyclert.adapter = adapter
    }

    private fun listenForMessages(){
        val ref = FirebaseDatabase.getInstance().getReference("/messages")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)


                    if (chatMessage!!.from_id == FirebaseAuth.getInstance().uid) {
                        if (chatMessage.senderAccountType == "Administrator") {
                            adapter.add(AdminChatTo(chatMessage.text, "You"))
                            messageboxtext.text = null
                            p++
                            recyclert.scrollToPosition(p)
                        } else {
                            adapter.add(ChatTo(chatMessage.text, "You", chatMessage.xp.toString()))
                            messageboxtext.text = null
                            p++
                            recyclert.scrollToPosition(p)
                        }
                    } else if (chatMessage.from_id != FirebaseAuth.getInstance().uid) {
                        if (chatMessage.senderAccountType == "Administrator") {
                            adapter.add(
                                AdminChatFrom(
                                    chatMessage.text,
                                    chatMessage.name
                                )
                            )
                            p++
                            recyclert.scrollToPosition(p)
                        } else {
                            adapter.add(
                                ChatFrom(
                                    chatMessage.text,
                                    chatMessage.name,
                                    chatMessage.xp.toString()
                                )
                            )
                            p++
                            recyclert.scrollToPosition(p)
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

    private fun sendTheMessage(name : String){
        var fromid = FirebaseAuth.getInstance().uid
        var text = messageboxtext.text.toString().trim()
        var reference = FirebaseDatabase.getInstance().getReference("/messages").push()
        var message = ChatMessage(text, accountType, reference.key!!, fromid!!, name, 0)
        reference.setValue(message)
    }




}

class ChatFrom(val text : String, var name : String, var xp : String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewfrom.text = text
        viewHolder.itemView.nameFrom.text = name + " [$xp XP]"
    }

    override fun getLayout(): Int {
        return  R.layout.from
    }
}

class ChatTo(var text : String, var name : String, var xp : String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.textViewto.text = text
        viewHolder.itemView.nameTo.text = name + " [$xp XP]"
    }

    override fun getLayout(): Int {
        return R.layout.to
    }
}

class AdminChatFrom(val text : String, var name : String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.atextViewfrom.text = text
        viewHolder.itemView.anameFrom.text = name + " [Admin]"
    }

    override fun getLayout(): Int {
        return  admin_from
    }
}

class AdminChatTo(var text : String, var name : String): Item<ViewHolder>(){
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.atextViewto.text = text
        viewHolder.itemView.anameTo.text = name + " [Admin]"
    }

    override fun getLayout(): Int {
        return R.layout.admin_to
    }
}
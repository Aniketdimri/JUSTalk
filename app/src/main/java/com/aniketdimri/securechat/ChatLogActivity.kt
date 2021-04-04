package com.aniketdimri.securechat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {
    val adapter = GroupAdapter<GroupieViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)
        supportActionBar?.title="Chat Log"
        listenForMessages()
        val username =intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        supportActionBar?.title = username.username.toUpperCase()

        recyclerView_chat_log.adapter=adapter
        send_button_chat_log.setOnClickListener {
            performSendMessage();
        }
    }
    private fun listenForMessages(){
        val fromId =FirebaseAuth.getInstance().toString()
        val user= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        if(user.uid == null) return
        val toId=user!!.uid
        val toId1=toId.replace(".","*")
        val fromId1 = fromId.replace(".","*")

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        reference.addChildEventListener(object: ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage=snapshot.getValue(ChatMessage::class.java)
                if(chatMessage!=null){
                    if(chatMessage.fromId == FirebaseAuth.getInstance().uid){
                        val currentUser =LatestMessageActivity.currentUser
                        adapter.add(ChatFromItem(chatMessage.text,currentUser!!))
                    }
                    else{
                        val toUser= intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
                        adapter.add(ChatToItem(chatMessage.text,toUser))
                    }

                }
                recyclerView_chat_log.scrollToPosition(adapter.itemCount-1)

            }

            override fun onCancelled(error: DatabaseError) {

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }
        })
    }

    private fun performSendMessage(){
        val text=message_chat_log.text.toString()
        val fromId =FirebaseAuth.getInstance().toString().replace(".","*")
        val user = intent.getParcelableExtra<User>(NewMessageActivity.USER_KEY)
        val toId=user.uid.replace(".","*")
        if(fromId==null) return
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()
        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val chatMessage = ChatMessage(reference.key!!,text,fromId,toId,System.currentTimeMillis()/1000)
        reference.setValue(chatMessage)
            .addOnSuccessListener {
                message_chat_log.text.clear()
                recyclerView_chat_log.scrollToPosition(adapter.itemCount-1)
            }
        toReference.setValue(chatMessage)
        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId" +
                "/$toId")
        latestMessageRef.setValue(chatMessage)
        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId" +
                "/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }
}
class ChatFromItem(private val text:String, private val user:User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_from_row.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_from_row)
    }
    override fun getLayout(): Int {
       return R.layout.chat_from_row;
    }
}
class ChatToItem(private val text:String, private val user:User) : Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.textView_to_row.text = text
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView_to_row)
    }
        override fun getLayout(): Int {
        return R.layout.chat_to_row;
    }
}


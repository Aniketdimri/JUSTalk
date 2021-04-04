package com.aniketdimri.securechat


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_latest_message.*
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageActivity : AppCompatActivity() {
    companion object{
        var currentUser: User?=null
    }
    private val adapter =GroupAdapter<GroupieViewHolder>()
    val latestMessagesMap= HashMap<String,ChatMessage>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_latest_message)
        recyclerView_latest_message.adapter=adapter
        recyclerView_latest_message.addItemDecoration(DividerItemDecoration(this,
        DividerItemDecoration.VERTICAL))
        adapter.setOnItemClickListener { item, view ->
            val intent= Intent(this,ChatLogActivity::class.java)
            val row= item as LatestMessageRow

            intent.putExtra(NewMessageActivity.USER_KEY,  row.chatPartnerUser)
            startActivity(intent)
        }
        listenForLatestMessages()
        fetchCurrentUser()
        verifyUser()
    }


    private fun listenForLatestMessages(){
        val fromId=FirebaseAuth.getInstance()
        val ref=FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId")
        ref.addChildEventListener(object: ChildEventListener{
            override fun onCancelled(error: DatabaseError) { }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {  }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)?: return
                latestMessagesMap[snapshot.key!!]=chatMessage
                refreshRecyclerViewMessage()
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val chatMessage = snapshot.getValue(ChatMessage::class.java)?: return
                latestMessagesMap[snapshot.key!!]=chatMessage
                refreshRecyclerViewMessage()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                TODO("Not yet implemented")
            }

        })




    }

    private fun refreshRecyclerViewMessage() {
        adapter.clear()
        latestMessagesMap.values.forEach{
            adapter.add(LatestMessageRow(it))
        }
    }

    private fun fetchCurrentUser(){
        val uid = FirebaseAuth.getInstance().uid
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")
        ref.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                currentUser = snapshot.getValue(User::class.java)
            }
        })
    }
    private fun verifyUser(){
        val uid = FirebaseAuth.getInstance().uid
        if(uid==null){
            val intent = Intent(this,MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when  (item.itemId){
            R.id.menu_new_message ->{
                val intent = Intent(this,NewMessageActivity::class.java)
                startActivity(intent)
            }
            R.id.menu_sign_out ->{
                FirebaseAuth.getInstance().signOut()
                val intent =Intent(this,MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu,menu)
        return super.onCreateOptionsMenu(menu)
    }
}



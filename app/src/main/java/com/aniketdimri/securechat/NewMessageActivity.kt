package com.aniketdimri.securechat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.activity_new_message.*
import kotlinx.android.synthetic.main.row_message_view.view.*

class NewMessageActivity : AppCompatActivity() {
    companion object{
        val USER_KEY="USER_KEY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_message)
        supportActionBar?.title ="Select User"
        val adapter =GroupAdapter<GroupieViewHolder>()

        recycler_view.adapter = adapter
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val adapter = GroupAdapter<GroupieViewHolder>()
                snapshot.children.forEach {
                    val user = it.getValue(User::class.java)
                    if(user!=null){
                        adapter.add(UserItem(user))
                    }

                }
                adapter.setOnItemClickListener { item, view ->
                    val userItem = item as UserItem
                    val intent = Intent(view.context,ChatLogActivity::class.java)
                    intent.putExtra(USER_KEY,userItem.user)
                    startActivity(intent)
                    finish()
                }
                recycler_view.adapter = adapter

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })

    }
}
class UserItem(val user:User): Item<GroupieViewHolder>(){
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message.text = user.username
        Picasso.get().load(user.profileImageUrl).into(viewHolder.itemView.imageView)
    }
    override fun getLayout(): Int {
        return R.layout.row_message_view;
    }
}



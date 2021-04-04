package com.aniketdimri.securechat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.android.synthetic.main.latest_message_row.view.*

class LatestMessageRow(val chatMessage: ChatMessage): Item<GroupieViewHolder>(){

    var chatPartnerUser:User? =null
    override fun getLayout(): Int {
        return R.layout.latest_message_row
    }

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.message_textView.text = chatMessage.text
        val chatPart:String = if(chatMessage.fromId== FirebaseAuth.getInstance().uid){
            chatMessage.toId;
        }else{
            chatMessage.fromId
        }
        val ref = FirebaseDatabase.getInstance().getReference("/users/$chatPart")
        ref.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot){
                chatPartnerUser=snapshot.getValue(User::class.java)

                viewHolder.itemView.username_textView.text=chatPartnerUser?.username
                val targetImageView=viewHolder.itemView.imageView2
                Picasso.get().load(chatPartnerUser?.profileImageUrl).into(targetImageView)
            }

        })

    }

}

package com.aniketdimri.securechat


import android.content.Intent
import android.graphics.ImageDecoder
import android.graphics.ImageDecoder.createSource
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        register.setOnClickListener {
            performRegister()
        }
        already_have_account.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
        register_button.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }
    private var selectedPhotoUri:Uri?=null

    //This will take care of the value intent has received
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var uri = data?.data
        try {
            uri?.let {
                selectedPhotoUri = uri
                val source = createSource(this.contentResolver, uri)
                val bitmap = ImageDecoder.decodeBitmap(source)
                register_image.setImageBitmap(bitmap)
                register_button.alpha=0f
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun performRegister() {
        val email = email.text.toString()
        val password = password.text.toString()
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter password or email correctly", Toast.LENGTH_LONG)
                .show()
            return
        }
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (!it.isSuccessful) return@addOnCompleteListener
                Log.d("Register","Data stored")
                uploadImageToFirebase();
            }
            .addOnFailureListener {
                Log.d("Main", "Failed to create ${it.message}")
                Toast.makeText(this, "Failed to create user", Toast.LENGTH_LONG).show()
            }

    }
    private fun uploadImageToFirebase(){
        if(selectedPhotoUri==null){return}
        val filename =UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")
        selectedPhotoUri = selectedPhotoUri
        ref.putFile(selectedPhotoUri!!).addOnSuccessListener {
            Log.d("Register","Successfully added")
            ref.downloadUrl.addOnSuccessListener {
                it.toString()
                Log.d("Register","Selected the downloaded image")
                saveUserToDatabase(it.toString())
            }
                .addOnFailureListener{
                    Log.d("Register","Failure in database")

                }
        }
    }
    private fun saveUserToDatabase(profileImageUrl: String){
        val uid=FirebaseAuth.getInstance().uid?:""
        val ref=FirebaseDatabase.getInstance().getReference("/users/$uid")
        val user = User(uid,username.text.toString(),profileImageUrl)
        ref.setValue(user).addOnSuccessListener {
            Log.d("Register","Registered to database")
            val intent = Intent(this,LatestMessageActivity::class.java)
            Toast.makeText(this,"Logged In",Toast.LENGTH_SHORT).show()
            intent.flags =Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }
}

@Parcelize
class User(val uid:String,val username:String,val profileImageUrl: String):Parcelable{
    constructor(): this("","","")
}
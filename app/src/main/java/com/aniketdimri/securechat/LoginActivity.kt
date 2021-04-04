package com.aniketdimri.securechat

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*


class LoginActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        button_login.setOnClickListener{
            val email= email_login.text.toString()
            val password = password_login.text.toString()
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password)
                .addOnCompleteListener{}
                .addOnFailureListener{}
        }
        back_to_registration.setOnClickListener {
            finish()
        }
    }

}
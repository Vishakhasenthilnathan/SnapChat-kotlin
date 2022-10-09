package com.project.snapchat

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var emailId : String?=null
    var password1 : String?=null
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)




        goButton.setOnClickListener(View.OnClickListener { goButtonClicked() })
        val currentUser = mAuth.currentUser
        if(currentUser != null){
            login()
        }

    }


    fun goButtonClicked() {
        emailId = email.text.toString()
        val goButton:Button = findViewById(R.id.goButton)
        password1= password.text.toString()
        if(emailId==null || emailId==""|| password==null || password1=="" ||password1==""){
            Toast.makeText(baseContext, "Please enter email and password",
                Toast.LENGTH_SHORT).show()
            return
        }
        else {
            mAuth.signInWithEmailAndPassword(emailId!!, password1!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        login()
                    } else {
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext, "Authentication failed due to the reason: "+ task.exception,
                            Toast.LENGTH_SHORT
                        ).show()
                        //add to database
                        signup()
                    }
                }
        }

    }

    private fun signup() {
        mAuth.createUserWithEmailAndPassword(emailId!!, password1!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                   login()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()

                }
            }

    }

    fun login(){

        val intent = Intent(this,SnapActivity::class.java)
        startActivity(intent)
    }
}
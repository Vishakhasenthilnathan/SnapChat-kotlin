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
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        goButton.setOnClickListener(View.OnClickListener { goButtonClicked() })
        val currentUser = mAuth.currentUser
        if (currentUser != null) {
            login()
        }

    }

    fun goButtonClicked() {
        if (email.text.toString().isNullOrEmpty() || password.text.toString().isNullOrEmpty()) {
            Toast.makeText(
                baseContext, "Please enter email and password",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            mAuth.signInWithEmailAndPassword(email.text.toString()!!, password.text.toString()!!)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        login()
                    } else {
                        signup()
                    }
                }
        }
    }

    private fun signup() {
        mAuth.createUserWithEmailAndPassword(email.text.toString()!!, password.text.toString()!!)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    FirebaseDatabase.getInstance().getReference().child("users").child(task.result.user!!.uid).child("email").setValue(email?.text.toString())
                    login()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext, task.exception?.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }

    }

    fun login() {
        val intent = Intent(this, SnapActivity::class.java)
        startActivity(intent)
    }
}
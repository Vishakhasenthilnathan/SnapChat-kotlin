package com.project.snapchat

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_choose_user.*
import kotlinx.android.synthetic.main.activity_snap.*
import kotlinx.android.synthetic.main.activity_view_snaps.*
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class ViewSnapsActivity : AppCompatActivity() {
    val mAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_snaps)

        messageTextView?.text = intent.getStringExtra("message").toString()

        val task = ImageDownloader()
        val myImage: Bitmap
        try {
            myImage = task.execute(intent.getStringExtra("imageURL")).get()
            snapImageView.setImageBitmap(myImage)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        FirebaseDatabase.getInstance().reference.child("users").child(mAuth.currentUser!!.uid)
            .child("snaps").child(intent.getStringExtra("snapKey").toString()).removeValue()
        FirebaseStorage.getInstance().getReference().child("snapImages")
            .child(intent.getStringExtra("imageName").toString()).delete()
    }

    @SuppressLint("StaticFieldLeak")
    inner class ImageDownloader : AsyncTask<String, Void, Bitmap>() {
        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg urls: String?): Bitmap? {
            try {
                val url = URL(urls[0])
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()
                val inp = connection.inputStream
                return BitmapFactory.decodeStream(inp)
            } catch (e: Exception) {
                e.printStackTrace()
                return null
            }
        }

    }
}
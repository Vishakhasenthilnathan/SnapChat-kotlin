package com.project.snapchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_choose_user.*
import kotlinx.android.synthetic.main.activity_create_snap.*
import kotlinx.android.synthetic.main.activity_snap.*

class SnapActivity : AppCompatActivity() {
    private val mAuth = FirebaseAuth.getInstance()
    var emails : ArrayList<String> = ArrayList()
    var snapShotList: ArrayList<DataSnapshot> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snap)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1 , emails)
        snapListView?.adapter = adapter

        //get the snaps object from the database by navigating through users->uid -->snaps
        FirebaseDatabase.getInstance().reference.child("users").child(mAuth.currentUser!!.uid).child("snaps").addChildEventListener(object :
            ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //add the retrieved emails to the email list to display in adapter
                emails.add(snapshot.child("from").value as String)
                snapShotList.add(snapshot)
                adapter.notifyDataSetChanged()
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
                var index=0
                for (snap: DataSnapshot in snapShotList){
                    if(snap.key==snapshot.key){
                        snapShotList.removeAt(index)
                        emails.removeAt(index)
                    }
                    index++
                }
                adapter.notifyDataSetChanged()
            }
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {}

        })

        snapListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            var snapshot = snapShotList.get(i)
            val intent = Intent(this,ViewSnapsActivity::class.java)
            intent.putExtra("imageURL",snapshot.child("imageUrl").value as String)
            intent.putExtra("imageName",snapshot.child("imageName").value as String)
            intent.putExtra("message",snapshot.child("message").value as String)
            intent.putExtra("snapKey",snapshot.key as String)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
       val inflater = menuInflater
        inflater.inflate(R.menu.snaps_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId ==R.id.createSnap){
            val intent = Intent(this,CreateSnapActivity::class.java)
            startActivity(intent)
        }else if(item.itemId ==R.id.logout){
            mAuth.signOut()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        mAuth.signOut()
        super.onBackPressed()
    }
}
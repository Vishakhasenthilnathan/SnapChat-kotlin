package com.project.snapchat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.activity_create_snap.*
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapActivity : AppCompatActivity() {

    val ImageName:String =  UUID.randomUUID().toString()+".jpg"
    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStore: FirebaseStorage? = null
    private var storageReference: StorageReference? = null
    lateinit var imagePreview: ImageView
    lateinit var btn_choose_image: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        chooseButton.setOnClickListener(View.OnClickListener { getPhoto() })
        nextbutton.setOnClickListener(View.OnClickListener { next() })
        firebaseStore = FirebaseStorage.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

    }


    private fun getPhoto() {
        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
        }
        else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
    }
    private fun next() {





        try {

//            val storage = Firebase.storage
//
//            // [START upload_create_reference]
//            // Create a storage reference from our app
//            val storageRef = storage.reference
//
//            // Create a reference to "mountains.jpg"
//            val snapImagesRef = storageRef.child(ImageName)
//
//            // Create a reference to 'images/mountains.jpg'
//            val snapFolderImagesRef = storageRef.child("snapImages/"+ImageName)
//
//            // While the file names are the same, the references point to different files
//            snapImagesRef.name == snapFolderImagesRef.name // true
//            snapImagesRef.path == snapFolderImagesRef.path // false
            snapImage.isDrawingCacheEnabled = true
            snapImage.buildDrawingCache()
            val bitmap = (snapImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            var uploadTask = FirebaseStorage.getInstance().getReference().child("snapImages").child(ImageName).putBytes(data)
            uploadTask.addOnFailureListener {
                // Handle unsuccessful uploads
                Toast.makeText(this,"Upload Failed",Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
                // ...
                Toast.makeText(this,"Upload Success",Toast.LENGTH_SHORT).show()
            }

        }
        catch (e: java.lang.Exception){
            e.printStackTrace()
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val selectedImage = data!!.data
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                val bitmap: Bitmap =
                    MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                    snapImage?.setImageBitmap(bitmap)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }



}
package com.project.snapchat

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_create_snap.*
import java.io.ByteArrayOutputStream
import java.util.*


class CreateSnapActivity : AppCompatActivity() {

    val ImageName:String =  UUID.randomUUID().toString()+".jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_snap)
        title = "Create a snap"
        chooseButton.setOnClickListener(View.OnClickListener { getPhoto() })
        nextbutton.setOnClickListener(View.OnClickListener { send() })
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

    private fun send() {
        try {
            //create a bitmap of the image chosen
            val bitmap = (snapImage.drawable as BitmapDrawable).bitmap
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()

            //create a upload task and upload the image bitmap to the folder snapImages/ImageName
            val uploadTask = FirebaseStorage.getInstance().reference.child("snapImages").child(ImageName).putBytes(data)
            uploadTask.addOnFailureListener(OnFailureListener {
                Toast.makeText(this,"UploadFailed",Toast.LENGTH_SHORT).show()
            }).addOnSuccessListener { }

            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                //get the download url -not necessary here but adding due to unknown erro
                FirebaseStorage.getInstance().getReference().child("snapImages")
                    .child(ImageName).downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadUri = task.result
                    Log.i("uri on complete", downloadUri.toString())

                    //go to choose users activity
                    val intent = Intent(this, ChooseUserActivity::class.java)
                    intent.putExtra("imageURL",downloadUri.toString())
                    intent.putExtra("imageName",ImageName)
                    intent.putExtra("message",snapMessageText?.text.toString())
                    startActivity(intent)
                }
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
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getPhoto()
            }
        }
    }
}
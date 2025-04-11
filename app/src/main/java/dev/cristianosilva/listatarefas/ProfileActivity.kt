package dev.cristianosilva.listatarefas

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import de.hdodenhof.circleimageview.CircleImageView
import dev.cristianosilva.listatarefas.utils.Navigator
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_CAMERA = 0
    private val PERMISSION_REQUEST_MEDIA = 1

    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val db_ref = FirebaseDatabase.getInstance().getReference("users/${uid}/profile/")

    //var profileDatabaseId: String = ""
    var _image: Bitmap? = null

    companion object {
        const val REQUEST_IMAGE_CAMERA = 1
        const val REQUEST_IMAGE_GALLERY = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val homebtn = findViewById<ImageView>(R.id.home)
        val logoutbtn = findViewById<ImageView>(R.id.logout)
        val saveBtn = findViewById<Button>(R.id.save_btn)


        val cameraAction = findViewById<FloatingActionButton>(R.id.camera_action)
        val galleryAction = findViewById<ImageView>(R.id.gallery_action)

        findViewById<EditText>(R.id.emailInput).setText(FirebaseAuth.getInstance().currentUser?.email)

        homebtn.setOnClickListener {
            Navigator.goToScreen(this, MainActivity::class.java)
        }

        logoutbtn.setOnClickListener {
            Navigator.goToScreen(this, LoginActivity::class.java)
        }

        saveBtn.setOnClickListener {

            saveProfile()


        }

        cameraAction.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, REQUEST_IMAGE_CAMERA)
        }

        galleryAction.setOnClickListener {
            intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
        }

        requestPermissions()
        loadData()
    }

    fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CAMERA
            )
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_MEDIA_IMAGES),
                PERMISSION_REQUEST_MEDIA
            )
        }
    }

    fun saveProfile() {

        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val nameInput = findViewById<EditText>(R.id.nameInput)
        val phoneInput = findViewById<EditText>(R.id.phoneInput)

        /*Converte  para base64*/
        val baos = ByteArrayOutputStream(); // não grava a imagem no dispositivo
        _image?.compress(Bitmap.CompressFormat.PNG, 100, baos)

        val data = baos.toByteArray()

        val base64String = android.util.Base64.encodeToString(data, android.util.Base64.DEFAULT)



            val db_ref = FirebaseDatabase.getInstance().getReference("users/${uid}/profile/")
            db_ref.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val profile = hashMapOf(
                            "username" to usernameInput.text.toString(),
                            "name" to nameInput.text.toString(),
                            "phone" to phoneInput.text.toString(),
                            "image" to base64String
                        )
                        db_ref.setValue(profile)
                    } else {
                        Toast.makeText(this@ProfileActivity, R.string.profile_not_found, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })


        Toast.makeText(this, R.string.profile_updated, Toast.LENGTH_SHORT).show()
        android.os.Handler().postDelayed({
            finish()
        }, 3000)


    }

    fun loadData(){
        db_ref.addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {

                        findViewById<EditText>(R.id.usernameInput).setText(snapshot.child("username").value.toString())
                        findViewById<EditText>(R.id.nameInput).setText(snapshot.child("name").value.toString())
                        findViewById<EditText>(R.id.phoneInput).setText(snapshot.child("phone").value.toString())

                        /*Converte base64 para imagem*/
                        val image = snapshot.child("image").value.toString()
                        val decodedString = android.util.Base64.decode(image, android.util.Base64.DEFAULT)
                        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                        findViewById<CircleImageView>(R.id.profile_image).setImageBitmap(decodedByte)
                        _image = decodedByte
                    } else {
                        Toast.makeText(this@ProfileActivity, R.string.profile_not_found, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {

                }
            }
        )

    }


    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        val profileImage = findViewById<CircleImageView>(R.id.profile_image)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_IMAGE_CAMERA -> {

                    val imageBitmap = data?.extras?.get("data") as Bitmap
                    profileImage.setImageBitmap(imageBitmap)
                    _image = imageBitmap
                }
                REQUEST_IMAGE_GALLERY -> {
                    val selectedImage: Uri? = data?.data
                    if (selectedImage != null) {
                        val imageBitmap = MediaStore.Images.Media.getBitmap(
                            this.contentResolver,
                            selectedImage
                        )
                        /*val rotatedBitmap = Bitmap.createBitmap(
                            imageBitmap,
                            0,
                            0,
                            imageBitmap.width,
                            imageBitmap.height,
                            Matrix().apply { postRotate(270f) }, // Rotação de 90 graus
                            true
                        )*/
                        profileImage.setImageBitmap(imageBitmap)
                        this._image = imageBitmap
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        var message = ""
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                message = getString(R.string.permission_granted)
            } else {
                message = getString(R.string.permission_denied)
            }
        } else if (requestCode == PERMISSION_REQUEST_MEDIA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                message = getString(R.string.permission_media_granted)
            } else {
                message = getString(R.string.permission_media_denied)
            }

        }

        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}

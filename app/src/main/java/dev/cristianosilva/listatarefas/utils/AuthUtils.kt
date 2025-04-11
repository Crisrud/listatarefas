package dev.cristianosilva.listatarefas.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import dev.cristianosilva.listatarefas.LoginActivity

class AuthUtils {
    companion object {
        val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

        fun logout(context: Context){
            try {
                firebaseAuth.signOut()

                Toast.makeText(context, "Até logo!!!", Toast.LENGTH_SHORT).show()
                Navigator.goToScreen(context, LoginActivity::class.java)
            } catch (error: Exception) {
                Toast.makeText(context, "Falha ao realizar logout", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
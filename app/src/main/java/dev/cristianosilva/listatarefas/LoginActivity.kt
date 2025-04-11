package dev.cristianosilva.listatarefas

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import dev.cristianosilva.listatarefas.utils.AuthUtils.Companion.firebaseAuth
import dev.cristianosilva.listatarefas.utils.Navigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        val btnLogin = findViewById<Button>(R.id.submitLoginBtn)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val registerLink = findViewById<TextView>(R.id.createAccount)

        btnLogin.setOnClickListener {
            val email = findViewById<TextView>(R.id.emailInput).text.toString()
            val password = findViewById<TextView>(R.id.passwordInput).text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                login(email, password)
               /* val resultSucess = login(email, password)
                if (resultSucess) {
                    withContext(Dispatchers.Main) {
                        Navigator.goToScreen(this@LoginActivity, MainActivity::class.java)
                    }
                }else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@LoginActivity, "Falha ao realizar login", Toast.LENGTH_SHORT).show()
                    }
                }*/
            }
        }

        forgotPassword.setOnClickListener{
            Navigator.goToScreen(this,ForgotPasswordActivity::class.java)
        }

        registerLink.setOnClickListener{
           Navigator.goToScreen(this,RegisterActivity::class.java)
        }
    }

    suspend fun login(email: String, password: String) {
        try{
            firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = firebaseAuth.currentUser
                    if (user != null) {
                        Toast.makeText(this@LoginActivity, "Seja bem-vindo ${user.email}", Toast.LENGTH_SHORT).show()
                        Navigator.goToScreen(this@LoginActivity, MainActivity::class.java)

                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Falha ao realizar login", Toast.LENGTH_SHORT).show()

                }
            }

        } catch (e: Exception){
            print(e)
        }
    }

    override fun onBackPressed() {
        // Sair do app quando pressionar voltar na tela de login
        finishAffinity()
    }



}
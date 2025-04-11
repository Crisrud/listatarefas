package dev.cristianosilva.listatarefas

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import dev.cristianosilva.listatarefas.fragments.PasswordDifficult
import dev.cristianosilva.listatarefas.utils.Navigator
import dev.cristianosilva.listatarefas.utils.Password
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterActivity : AppCompatActivity() {
    val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val btnRegister = findViewById<Button>(R.id.submitRegisterBtn)
        val forgotPassword = findViewById<TextView>(R.id.forgotPassword)
        val loginLink = findViewById<TextView>(R.id.login)
        val password = supportFragmentManager.findFragmentById(R.id.passwordInput) as PasswordDifficult
        val confirmPassword = findViewById<EditText>(R.id.passwordConfirmInput)
        lateinit var text: Editable;

        btnRegister.setOnClickListener {
            val email = findViewById<TextView>(R.id.emailInput).text.toString()

            CoroutineScope(Dispatchers.IO).launch {
                register(email, password.passwordInput.text.toString(), confirmPassword.text.toString())
            }
        }

        password.passwordInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(s: Editable?) {

                if(s != null) {
                    text = s;
                }
                var result = Password.verifyPasswordDificult(text.toString())
                if (result < 3) {
                    btnRegister.isEnabled = false;
                } else {
                    btnRegister.isEnabled = true;
                }
            }


        })

        forgotPassword.setOnClickListener{
            Navigator.goToScreen(this,ForgotPasswordActivity::class.java)
        }

        loginLink.setOnClickListener{
            Navigator.goToScreen(this,LoginActivity::class.java)
        }

        suspend fun create(email: String, password: String) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(baseContext, "Usuário OK", Toast.LENGTH_SHORT).show()

                        Navigator.goToScreen(this, LoginActivity::class.java)

                    } else {
                        Toast.makeText(baseContext, "Falha ao criar conta", Toast.LENGTH_SHORT).show()

                    }
                }
        }

    }

    suspend fun register(email: String, password: String, confirmPassword: String) {
        try {
        if (password != confirmPassword) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    this@RegisterActivity,
                    "As senhas não conferem.",
                    Toast.LENGTH_SHORT,
                ).show()
            }

            } else if(password == "" || confirmPassword == "") {
                runOnUiThread {
                    Toast.makeText(
                        this,
                        "Preencha os campos de senha.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            } else {

                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            runOnUiThread {
                                Toast.makeText(
                                    baseContext,
                                    "Usuário cadastrado com sucesso!!!",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                            Navigator.goToScreen(this, MainActivity::class.java)
                        } else {
                            runOnUiThread {
                                Toast.makeText(
                                    baseContext,
                                    "Falha ao registrar usuário.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            /*Toast.makeText(
                this,
                "Ocorreu um erro inesperado.",
                Toast.LENGTH_SHORT,
            ).show()*/
            println(e.message)
        }

    }

}
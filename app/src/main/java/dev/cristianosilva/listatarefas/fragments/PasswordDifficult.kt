package dev.cristianosilva.listatarefas.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import dev.cristianosilva.listatarefas.R
import dev.cristianosilva.listatarefas.utils.Password

class PasswordDifficult : Fragment() {
    private lateinit var textViewDifficult: TextView;
    lateinit var passwordInput: EditText;
    lateinit var text: Editable;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_password_difficult, container, false)

        textViewDifficult = view.findViewById<TextView>(R.id.textInputPasswordDifficult)
        passwordInput = view.findViewById<EditText>(R.id.passwordInput)

        passwordInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                text = passwordInput.text
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                text = passwordInput.text
            }

            override fun afterTextChanged(s: Editable?) {
                if( s != null) {
                    text = s;

                }

                val difficult = Password.verifyPasswordDificult(text.toString())
                if(difficult == 0) {
                    textViewDifficult.text = resources.getString(R.string.password_empty);
                }
                else if(difficult == 1) {
                    textViewDifficult.text = resources.getString(R.string.password_very_weak);
                }
                else if(difficult == 2) {
                    textViewDifficult.text = resources.getString(R.string.password_weak);
                }
                else if(difficult == 3) {
                    textViewDifficult.text = resources.getString(R.string.password_normal);
                }
                else if(difficult == 4) {
                    textViewDifficult.text = resources.getString(R.string.password_strong);
                }
                else if(difficult == 5) {
                    textViewDifficult.text = resources.getString(R.string.password_very_strong);
                }
                else {
                    textViewDifficult.text = resources.getString(R.string.password_very_weak);
                }

            }
        })


        return view;
    }


}
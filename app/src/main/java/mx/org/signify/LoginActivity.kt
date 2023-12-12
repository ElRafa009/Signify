package mx.org.signify

// LoginActivity.kt
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseException
import com.parse.ParseUser

class LoginActivity : AppCompatActivity() {

    private lateinit var editTextPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val editTextUsernameOrEmail = findViewById<EditText>(R.id.editTextUsernameOrEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val btnGoToRegister = findViewById<Button>(R.id.btnGoToRegister)

        btnLogin.setOnClickListener {
            val usernameOrEmail = editTextUsernameOrEmail.text.toString().trim()
            val password = editTextPassword.text.toString()

            // Realizar el inicio de sesión
            ParseUser.logInInBackground(usernameOrEmail, password) { user, e ->
                if (user != null) {
                    // Inicio de sesión exitoso
                    val intentWelcome = Intent(this, WelcomeActivity::class.java)
                    startActivity(intentWelcome)
                    finish()
                } else {
                    // Error en el inicio de sesión
                    // Manejar el error apropiadamente

                    handleLoginError(e)
                }
            }
        }

        btnGoToRegister.setOnClickListener {
            // Ir a la actividad de registro
            val intentRegister = Intent(this, RegisterActivity::class.java)
            startActivity(intentRegister)
        }
    }

    private fun handleLoginError(e: ParseException?) {
        editTextPassword.setText("") // Poner en blanco el editText de la contraseña

        // Mostrar un mensaje de error
        val errorMessage = "Usuario o contraseña incorrectos"
        //Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
        editTextPassword.error = errorMessage
    }
}



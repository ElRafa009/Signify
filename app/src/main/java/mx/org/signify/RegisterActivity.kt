package mx.org.signify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.parse.ParseException
import com.parse.ParseQuery
import com.parse.ParseUser

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        val editTextUsername = findViewById<EditText>(R.id.editTextUsername)
        val editTextEmail = findViewById<EditText>(R.id.editTextEmail)
        val editTextPassword = findViewById<EditText>(R.id.editTextPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val username = editTextUsername.text.toString().trim()
            val email = editTextEmail.text.toString().trim()
            val password = editTextPassword.text.toString()

            // Validar disponibilidad del nombre de usuario y correo electrónico
            if (!isUsernameAvailable(username)) {
                editTextUsername.error = "Nombre de usuario no disponible"
                //showToast("Nombre de usuario no disponible")
                return@setOnClickListener
            }

            if (!isEmailAvailable(email)) {
                editTextEmail.error = "Correo electrónico no disponible"
                //showToast("Correo electrónico no disponible")
                return@setOnClickListener
            }

            // Crear un nuevo usuario
            val newUser = ParseUser()
            newUser.username = username
            newUser.email = email
            newUser.setPassword(password)

            // Guardar el usuario en Back4App
            newUser.signUpInBackground{ e ->
                if (e == null) {
                    Log.d("MainActivity","Usiario registrado exitosamente.")

                    // Ir a WelcomeActivity
                    val intentWelcome = Intent(this, WelcomeActivity::class.java)
                    startActivity(intentWelcome)

                    // Finalizar Registro para que no se pueda regresar a esta pantalla
                    finish()
                }else{
                    Log.e("MainActivity","Error al registrar usuario: ${e.localizedMessage}")
                }
            }
        }
        // Prueba de conexion
        /*val firstObject = ParseObject("FirstClass")
        firstObject.put("message","Hey ! First message from android. Parse is now connected")
        firstObject.saveInBackground{
            if (it != null){
                it.localizedMessage?.let { message -> Log.e("MainActivity", message) }
            }else{
                Log.d("MainActivity","Object saved.")
            }
        }*/
    }

    private fun isUsernameAvailable(username: String): Boolean {
        val lowercaseUsername = username.toLowerCase()
        val query = ParseQuery.getQuery<ParseUser>("_User")
        query.whereEqualTo("username", lowercaseUsername)

        return try {
            val users = query.find()
            users.isEmpty()
        } catch (e: ParseException) {
            // En caso de error, asumimos que el nombre de usuario está disponible
            true
        }
    }

    private fun isEmailAvailable(email: String): Boolean {
        val lowercaseEmail = email.toLowerCase()
        val query = ParseQuery.getQuery<ParseUser>("_User")
        query.whereEqualTo("email", lowercaseEmail)

        return try {
            val users = query.find()
            users.isEmpty()
        } catch (e: ParseException) {
            // En caso de error, asumimos que el correo electrónico está disponible
            true
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


}
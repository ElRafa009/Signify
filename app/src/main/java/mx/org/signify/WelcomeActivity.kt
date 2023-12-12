package mx.org.signify

// WelcomeActivity.kt
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseUser

class WelcomeActivity : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        // Obtener el nombre de usuario desde la sesión actual
        val currentUser = ParseUser.getCurrentUser()
        val username = currentUser?.username ?: "Usuario Desconocido"

        // Mostrar mensaje de bienvenida con el nombre de usuario
        val welcomeMessage = "Hola! $username!"
        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        welcomeTextView.text = welcomeMessage

        // Esperar unos segundos antes de pasar a la siguiente actividad
        Handler().postDelayed({
            val intentMain = Intent(this, MainActivity::class.java)
            startActivity(intentMain)
            finish()
        }, 3000) // 3000 milisegundos (3 segundos)
    }
}

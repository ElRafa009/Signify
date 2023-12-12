package mx.org.signify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toolbar
import com.parse.ParseUser

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Obtener el nombre de usuario desde la sesión actual
        val currentUser = ParseUser.getCurrentUser()
        val username = currentUser?.username ?: "Usuario Desconocido"

        // Mostrar mensaje de bienvenida con el nombre de usuario
        val welcomeMessage = "$username"
        val welcomeTextView = findViewById<TextView>(R.id.welcomeTextView)
        welcomeTextView.text = welcomeMessage

        // Configurar el botón para cerrar sesión
        val btnLogout = findViewById<Button>(R.id.btnLogout)
        btnLogout.setOnClickListener {
            // Cerrar sesión
            ParseUser.logOut()

            // Ir a la pantalla de login
            val intentRegistro = Intent(this, LoginActivity::class.java)
            startActivity(intentRegistro)

            // Finalizar MainActivity para que no se pueda regresar a esta pantalla
            finish()
        }

        // Configurar el botón para abrir el formulario
        val btnOpenForm = findViewById<ImageButton>(R.id.btnOpenForm)
        btnOpenForm.setOnClickListener {
            // Abre el formulario
            // Puedes iniciar una nueva actividad para el formulario o mostrar un cuadro de diálogo, dependiendo de tus necesidades
            // Ejemplo: startActivity(Intent(this, TuActividadDeFormulario::class.java))
        }
    }
}
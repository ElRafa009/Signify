package mx.org.signify

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ListView
import android.widget.TextView
import android.widget.Toolbar
import com.parse.ParseObject
import com.parse.ParseQuery
import com.parse.ParseUser

const val FORM_ACTIVITY_REQUEST_CODE = 123

class MainActivity : AppCompatActivity() {

    val adapter = ClienteAdapter()

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
            // Abrir la actividad del formulario y esperar un resultado
            val intentForm = Intent(this, FormActivity::class.java)
            startActivityForResult(intentForm, FORM_ACTIVITY_REQUEST_CODE)
        }

        // Verificar y crear la tabla "Cliente" si no existe
        checkAndCreateClienteTable()

        // Obtener la referencia del ListView desde el diseño
        val listView = findViewById<ListView>(R.id.listView)

        // Configurar el ListView y su adaptador

        listView.adapter = adapter
        fetchDataFromClienteTable(adapter)

    }
    private fun checkAndCreateClienteTable() {
        // Verificar si la tabla "Cliente" existe
        val query = ParseQuery.getQuery<ParseObject>("Cliente")
        query.limit = 1
        query.findInBackground { results, e ->
            if (e == null && results.isEmpty()) {
                // La tabla "Cliente" no existe, crearla
                val clienteObject = ParseObject("Cliente")
                clienteObject.saveInBackground { saveException ->
                    if (saveException == null) {
                        Log.d("MainActivity", "Tabla Cliente creada exitosamente.")
                    } else {
                        Log.e("MainActivity", "Error al crear tabla Cliente: ${saveException.localizedMessage}")
                    }
                }
            } else if (e != null) {
                // Hubo un error en la consulta, maneja el error según tus necesidades
                Log.e("MainActivity", "Error al verificar la existencia de la tabla Cliente: ${e.localizedMessage}")
            }
        }
    }

    private fun fetchDataFromClienteTable(adapter: ClienteAdapter) {
        val currentUser = ParseUser.getCurrentUser()
        if (currentUser != null) {
            val query = ParseQuery.getQuery<ParseObject>("Cliente")
            query.whereEqualTo("user", currentUser)
            query.findInBackground { results, e ->
                if (e == null) {
                    // Convertir las fechas antes de ordenar
                    val sortedResults = results.sortedBy {
                        it.getString("fechaEmision")?.toDate("dd/MM/yyyy")
                    }

                    // Actualizar el adaptador con los datos ordenados
                    adapter.submitList(sortedResults)
                } else {
                    // Hubo un error en la consulta, maneja el error según tus necesidades
                    Log.e("MainActivity", "Error al obtener datos de la tabla Cliente: ${e.localizedMessage}")
                }
            }
        } else {
            // El usuario no está autenticado, puedes manejar esto según tus necesidades
            Log.e("MainActivity", "Usuario no autenticado al obtener datos de la tabla Cliente")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Verifica si el código de solicitud es el de FormActivity y si el resultado es RESULT_OK
        if (requestCode == FORM_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            // Realiza acciones de actualización aquí (puedes llamar a fetchDataFromClienteTable o lo que sea necesario)
            fetchDataFromClienteTable(adapter)
        }
    }

}
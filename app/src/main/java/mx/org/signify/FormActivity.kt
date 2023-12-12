package mx.org.signify

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseObject
import com.parse.ParseUser
import java.util.*

const val PICK_KEY_FILE_REQUEST_CODE = 123

class FormActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    lateinit var editTextFechaEmision: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        // Inicializa editTextFechaEmision
        editTextFechaEmision = findViewById(R.id.editTextFechaEmision)

        // Configura el botón de subir archivo .key
        val btnUploadKeyFile = findViewById<Button>(R.id.btnUploadKeyFile)
        btnUploadKeyFile.setOnClickListener {
            // Abre el explorador de archivos
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // Todos los tipos de archivos
            startActivityForResult(intent, PICK_KEY_FILE_REQUEST_CODE)
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            // Obtener datos del formulario
            val nombreCliente = findViewById<EditText>(R.id.editTextNombreClient).text.toString()
            val emailCliente = findViewById<EditText>(R.id.editTextEmail).text.toString()
            val fechaEmision = findViewById<EditText>(R.id.editTextFechaEmision).text.toString()

            // Guardar datos en la tabla Cliente con la relación al usuario logueado
            saveDataToClienteTable(nombreCliente, emailCliente, fechaEmision)

            // Regresar a MainActivity después de guardar los datos
            val intent = Intent(this@FormActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun saveDataToClienteTable(nombreCliente: String, emailCliente: String, fechaEmision: String) {
        // Crear un nuevo objeto Parse
        val clienteObject = ParseObject("Cliente")
        clienteObject.put("nombre", nombreCliente)
        clienteObject.put("email", emailCliente)
        clienteObject.put("fechaEmision", fechaEmision)

        // Asociar el cliente con el usuario actual
        val currentUser = ParseUser.getCurrentUser()
        clienteObject.put("user", currentUser)

        // Guardar el objeto en el servidor
        clienteObject.saveInBackground { e ->
            if (e == null) {
                Log.d("FormActivity", "Datos del cliente guardados exitosamente.")
                // Puedes realizar acciones adicionales después de guardar los datos, si es necesario.
            } else {
                Log.e("FormActivity", "Error al guardar datos del cliente: ${e.localizedMessage}")
                // Manejar el error según tus necesidades.
            }
        }
    }

    // Maneja el resultado de la selección de archivo
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_KEY_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Seleccionar archivo exitoso
            val selectedFileUri = data?.data
            val fileName = getFileName(selectedFileUri)

            // Actualiza el texto del botón con el nombre del archivo
            val btnUploadKeyFile = findViewById<Button>(R.id.btnUploadKeyFile)
            btnUploadKeyFile.text = "Archivo seleccionado: $fileName"
        }
    }

    // Función para obtener el nombre del archivo desde la URI
    private fun getFileName(uri: Uri?): String {
        val cursor = contentResolver.query(uri!!, null, null, null, null)
        val nameIndex = cursor!!.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val fileName = cursor.getString(nameIndex)
        cursor.close()
        return fileName
    }

    @Suppress("UNUSED_PARAMETER")
    fun showDatePickerDialog(view: View) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this@FormActivity, this@FormActivity, year, month, day)
        datePickerDialog.show()
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = "$dayOfMonth/${month + 1}/$year"
        editTextFechaEmision.setText(selectedDate)
    }
}

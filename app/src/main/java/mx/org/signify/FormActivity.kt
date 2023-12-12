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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parse.ParseFile
import com.parse.ParseObject
import com.parse.ParseUser
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

const val PICK_KEY_FILE_REQUEST_CODE = 123
const val REFRESH_REQUEST_CODE = 456

class FormActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {

    lateinit var editTextFechaEmision: EditText
    lateinit var editTextNombreCliente: EditText
    lateinit var editTextEmailCliente: EditText
    lateinit var btnUploadKeyFile: Button

    // Variable para almacenar el archivo seleccionado
    private var selectedFile: ByteArray? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)

        // Inicializa los elementos de la vista
        editTextFechaEmision = findViewById(R.id.editTextFechaEmision)
        editTextNombreCliente = findViewById(R.id.editTextNombreClient)
        editTextEmailCliente = findViewById(R.id.editTextEmail)
        btnUploadKeyFile = findViewById(R.id.btnUploadKeyFile)

        // Configura el botón de subir archivo .key
        btnUploadKeyFile.setOnClickListener {
            // Abre el explorador de archivos
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*" // Todos los tipos de archivos
            startActivityForResult(intent, PICK_KEY_FILE_REQUEST_CODE)
        }

        val btnContinue = findViewById<Button>(R.id.btnContinue)
        btnContinue.setOnClickListener {
            // Obtener datos del formulario
            val nombreCliente = editTextNombreCliente.text.toString()
            val emailCliente = editTextEmailCliente.text.toString()
            val fechaEmision = editTextFechaEmision.text.toString()

            // Verificar que todos los campos estén llenos
            if (nombreCliente.isNotEmpty() && emailCliente.isNotEmpty() && fechaEmision.isNotEmpty()) {
                // Verificar que se haya seleccionado un archivo
                if (selectedFile != null) {
                    // Guardar datos y archivo en la tabla Cliente con la relación al usuario logueado
                    saveDataToClienteTable(nombreCliente, emailCliente, fechaEmision, selectedFile!!)

                    // Devolver un resultado para indicar que la actividad se completó con éxito
                    setResult(RESULT_OK)
                    // Finalizar FormActivity para volver a MainActivity
                    finish()
                } else {
                    // Mostrar mensaje si no se ha seleccionado un archivo
                    Toast.makeText(this, "Por favor, seleccione un archivo.", Toast.LENGTH_SHORT).show()
                }
            } else {
                // Mostrar mensaje si algún campo está vacío
                Toast.makeText(this, "Por favor, complete todos los campos.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveDataToClienteTable(nombreCliente: String, emailCliente: String, fechaEmision: String, fileData: ByteArray) {
        // Crear un nuevo objeto Parse
        val clienteObject = ParseObject("Cliente")
        clienteObject.put("nombre", nombreCliente)
        clienteObject.put("email", emailCliente)
        clienteObject.put("fechaEmision", fechaEmision)

        // Crear un objeto ParseFile
        val parseFile = ParseFile("archivo_key", fileData)
        clienteObject.put("archivoKey", parseFile)

        // Asociar el cliente con el usuario actual
        val currentUser = ParseUser.getCurrentUser()
        clienteObject.put("user", currentUser)

        // Guardar el objeto en el servidor
        clienteObject.saveInBackground { e ->
            if (e == null) {
                Log.d("FormActivity", "Datos del cliente y archivo guardados exitosamente.")
                // Puedes realizar acciones adicionales después de guardar los datos, si es necesario.
            } else {
                Log.e("FormActivity", "Error al guardar datos del cliente y archivo: ${e.localizedMessage}")
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
            btnUploadKeyFile.text = "Archivo seleccionado: $fileName"

            // Convierte el archivo a un ByteArray
            selectedFile = convertFileToByteArray(selectedFileUri)
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

    // Función para convertir el archivo a ByteArray
    private fun convertFileToByteArray(uri: Uri?): ByteArray? {
        var inputStream: InputStream? = null
        try {
            inputStream = contentResolver.openInputStream(uri!!)
            val byteBuffer = ByteArrayOutputStream()
            val bufferSize = 1024
            val buffer = ByteArray(bufferSize)
            var len: Int
            while (inputStream?.read(buffer).also { len = it!! } != -1) {
                byteBuffer.write(buffer, 0, len)
            }
            return byteBuffer.toByteArray()
        } catch (e: IOException) {
            Log.e("FormActivity", "Error al convertir archivo a ByteArray: ${e.localizedMessage}")
        } finally {
            inputStream?.close()
        }
        return null
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

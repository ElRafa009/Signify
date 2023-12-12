package mx.org.signify

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.parse.ParseObject
import com.parse.ParseUser
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

class ClienteAdapter : BaseAdapter() {

    private val clientes: MutableList<ParseObject> = mutableListOf()

    override fun getCount(): Int {
        return clientes.size
    }

    override fun getItem(position: Int): Any {
        return clientes[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        // Aquí debes inflar y configurar la vista de cada elemento en el ListView
        val inflater = LayoutInflater.from(parent?.context)
        val view = inflater.inflate(R.layout.list_item_cliente, parent, false)

        // Obtener los datos del cliente en la posición actual
        val cliente = clientes[position]

        // Validar que el cliente pertenezca al usuario actual
        val currentUser = ParseUser.getCurrentUser()
        if (cliente.getParseUser("user")?.objectId == currentUser?.objectId) {
            val nombreClienteTextView = view.findViewById<TextView>(R.id.textViewNombreCliente)
            nombreClienteTextView.text = cliente.getString("nombre")

            val emailClienteTextView = view.findViewById<TextView>(R.id.textViewEmailCliente)
            emailClienteTextView.text = cliente.getString("email")

            val fechaEmisionTextView = view.findViewById<TextView>(R.id.textViewFechaEmision)
            fechaEmisionTextView.text = cliente.getString("fechaEmision")

            // Calcular la diferencia de años
            val fechaEmisionString = cliente.getString("fechaEmision") ?: ""
            val fechaEmision = fechaEmisionString.toDate("dd/MM/yyyy")
            val diferenciaAnios = obtenerDiferenciaAnios(fechaEmision)

            // Crear un GradientDrawable para establecer el fondo y el borde
            val gradientDrawable = GradientDrawable()
            gradientDrawable.shape = GradientDrawable.RECTANGLE
            gradientDrawable.setColor(Color.parseColor("#D3D3D3"))  // Color de fondo gris claro
            gradientDrawable.setStroke(10, parent?.context?.resources?.getColor(R.color.black) ?: 0)  // Borde negro

            // Establecer el GradientDrawable como fondo de la vista
            view.background = gradientDrawable

            // Aplicar el color del borde según la diferencia de años
            val borderColor = when {
                diferenciaAnios < 2 -> R.color.verde  // R.color.verde es tu recurso de color verde
                diferenciaAnios < 4 -> R.color.amarillo  // R.color.amarillo es tu recurso de color amarillo
                else -> R.color.rojo  // R.color.rojo es tu recurso de color rojo
            }

            // Establecer el color del borde interior
            gradientDrawable.setColor(parent?.context?.resources?.getColor(borderColor) ?: 0)

            // Añadir un clic del botón de imagen
            val imageButton = view.findViewById<ImageButton>(R.id.imageButton1)
            imageButton.setOnClickListener {
                // Obtener el contexto desde parent?.context
                val context = parent?.context
                // Verificar que el contexto no sea nulo antes de mostrar el Toast
                context?.let {
                    // Mostrar un Toast al hacer clic en el botón de imagen
                    Toast.makeText(it, "Correo enviado", Toast.LENGTH_SHORT).show()
                }
            }

        } else {
            // Si el cliente no pertenece al usuario actual, oculta la vista
            view.visibility = View.GONE
        }

        return view
    }

    private fun obtenerDiferenciaAnios(fecha: Date): Long {
        val fechaActual = Calendar.getInstance().time
        val diferenciaMilisegundos = fechaActual.time - fecha.time
        val diferenciaDias = TimeUnit.MILLISECONDS.toDays(diferenciaMilisegundos)
        return diferenciaDias / 365
    }

    fun submitList(newList: List<ParseObject>) {
        clientes.clear()
        clientes.addAll(newList)
        notifyDataSetChanged()
    }
}

package com.example.sanpauloapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Listarusuarios : AppCompatActivity() {

    private lateinit var searchEditText: EditText
    private lateinit var listView: ListView


    private var users = ArrayList<String>()
    private lateinit var adapter: ArrayAdapter<String>
    private var userIds = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_listarusuarios)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.listView)
        searchEditText = findViewById(R.id.searchEditText)

        // Configurar el adaptador y el manejo de clics del ListView
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, users)
        listView.adapter = adapter
        listView.onItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
            showOptionsDialog(userIds[position])
        }
        searchEditText.setOnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                // Ocultar el teclado
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                // Realizar la búsqueda
                buscarEmpleado(searchEditText.text.toString())
                return@setOnKeyListener true
            }
            false
        }
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No se necesita implementar
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                buscarEmpleado(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {
                // No se necesita implementar
            }
        })


        // Obtener todos los usuarios y mostrarlos

        val url = "https://labsanpualointelligence.online/Finales/getusuarios.php"
        //val url = "http://192.168.3.164/Wam/getusuarios.php"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                for (i in 0 until response.length()) {
                    val user = response.getJSONObject(i)
                    val id = user.getString("idusuario")
                    val nombres = user.getString("Nombres")
                    val apellido1 = user.getString("Apellido1")
                    val apellido2 = user.getString("Apellido2")
                    val telefono = user.getString("Telefono")
                    val codigo = user.getString("Codigo")
                    val idCargo = user.getString("Cargo")
                    val idHorario = user.getString("Turno")
                    val userData =
                        "Nombre: $nombres $apellido1 $apellido2\nTeléfono: $telefono\nCódigo: $codigo\nCargo: $idCargo\nHorario: $idHorario"
                    users.add(userData)
                    userIds.add(id)
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }
    private fun showOptionsDialog(idusuario: String) {
        val options = arrayOf("Editar", "Eliminar")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Opciones")
        builder.setItems(options) { dialog, which ->
            when (which) {
                0 -> {
                    // Obtener detalles del usuario y abrir la pantalla de Modificar
                    obtenerDetallesUsuario(idusuario)
                }
                1 -> deleteUser(idusuario)
            }
        }
        builder.create().show()
    }

    private fun obtenerDetallesUsuario(idusuario: String) {
        val url = "https://labsanpualointelligence.online/Finales/getDetallesUsuario.php?idusuario=$idusuario"
        //val url = "http://192.168.3.164/Wam/getDetallesUsuario.php?idusuario=$idusuario"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                if (response.has("Nombres")) {
                    val idusuario = response.getString("idusuario")
                    val nombres = response.getString("Nombres")
                    val apellido1 = response.getString("Apellido1")
                    val apellido2 = response.getString("Apellido2")
                    val domicilio = if (response.has("Domicilio")) response.getString("Domicilio") else ""
                    val telefono = response.getString("Telefono")
                    val codigo = response.getString("Codigo")
                    val turno = if (response.has("IdHorario")) response.getString("IdHorario") else ""

                    // Iniciar la actividad Modificar y pasar los datos del usuario como extras
                    val intent = Intent(this, Modificar::class.java).apply {
                        putExtra("idusuario", idusuario)
                        putExtra("Nombres", nombres)
                        putExtra("Apellido1", apellido1)
                        putExtra("Apellido2", apellido2)
                        putExtra("Domicilio", domicilio)
                        putExtra("Telefono", telefono)
                        putExtra("Codigo", codigo)
                        putExtra("IdHorario", turno)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "No se encontraron detalles para el usuario con id $idusuario", Toast.LENGTH_SHORT).show()
                }
            },
            { error ->
                Toast.makeText(this, "Error al obtener detalles del usuario: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        Volley.newRequestQueue(this).add(request)
    }
    private fun deleteUser(idusuario: String) {
        mostrarDialogoConfirmacion(idusuario)
        Toast.makeText(this, "Eliminar usuario $idusuario", Toast.LENGTH_SHORT).show()
    }
    private fun mostrarDialogoConfirmacion(idusuario: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar Persona")
        builder.setMessage("¿Estás seguro de que deseas eliminar esta persona?")
        builder.setPositiveButton("Sí") { dialog, which ->
            eliminarPersona(idusuario)
            Toast.makeText(this, "Persona eliminada correctamente", Toast.LENGTH_SHORT).show()
        }
        builder.setNegativeButton("Cancelar") { dialog, which ->
        }
        builder.show()
    }

    private fun eliminarPersona(idusuario: String) {
        val url = "https://labsanpualointelligence.online/Finales/Eliminar.php"
        //val url = "http://192.168.3.164/Wam/Eliminar.php"

        val request = object : StringRequest(
            Method.POST, url,
            Response.Listener { response ->
                if (response.equals("datos eliminados", ignoreCase = true)) {
                    Toast.makeText(this, "Persona eliminada correctamente", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(
                    this,
                    "Error al eliminar persona: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["idusuario"] = idusuario
                return params
            }
        }
        Volley.newRequestQueue(this).add(request)
    }


    private fun buscarEmpleado(nombre: String) {
        //val url = "http://192.168.3.164/Wam/BusquedaVista.php?nombres=$nombre"
        val url = "https://labsanpualointelligence.online/Finales/BusquedaVista.php?nombres=$nombre"
        val jsonArrayRequest = JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                users.clear()
                for (i in 0 until response.length()) {
                    val user = response.getJSONObject(i)
                    val nombres = user.getString("Nombres")
                    val apellido1 = user.getString("Apellido1")
                    val apellido2 = user.getString("Apellido2")
                    val telefono = user.getString("Telefono")
                    val codigo = user.getString("Codigo")
                    val idCargo = user.getString("Cargo")
                    val idHorario = user.getString("Turno")
                    val userData =
                        "Nombre: $nombres $apellido1 $apellido2\nTeléfono: $telefono\nCódigo: $codigo\nCargo: $idCargo\nHorario: $idHorario"
                    users.add(userData)
                }
                adapter.notifyDataSetChanged()
            },
            { error ->
                // Manejar errores de la solicitud
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(jsonArrayRequest)
    }
}

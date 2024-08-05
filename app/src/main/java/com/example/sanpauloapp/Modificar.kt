package com.example.sanpauloapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class Modificar : AppCompatActivity() {
    private lateinit var idusuarioo:EditText
    private lateinit var editnombres: EditText
    private lateinit var editapellido1: EditText
    private lateinit var editapellido2: EditText
    private lateinit var editdomicilio: EditText
    private lateinit var edittelefono: EditText
    private lateinit var editcodigot: EditText
    private lateinit var turnoManana: RadioButton
    private lateinit var turnoTarde: RadioButton
    private lateinit var editButton: Button
    private lateinit var extras: Bundle // Declarar extras como variable miembro


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar)

        idusuarioo = findViewById(R.id.Idusuario)
        editnombres = findViewById(R.id.editnombres)
        editapellido1 = findViewById(R.id.editapellido1)
        editapellido2 = findViewById(R.id.editapellido2)
        editdomicilio = findViewById(R.id.editdomicilio)
        edittelefono = findViewById(R.id.edittelefono)
        editcodigot = findViewById(R.id.editcodigot)
        turnoManana = findViewById(R.id.turnoManana)
        turnoTarde = findViewById(R.id.turnoTarde)
        editButton = findViewById(R.id.editButton) // Inicializar editButton

        // Obtener los extras del intent
        extras = intent.extras!! // Corregir la inicialización de extras
        if (extras != null) {
            val idusuario = extras.getString("idusuario")
            val nombres = extras.getString("Nombres")
            val apellido1 = extras.getString("Apellido1")
            val apellido2 = extras.getString("Apellido2")
            val domicilio = extras.getString("Domicilio")
            val telefono = extras.getString("Telefono")
            val codigo = extras.getString("Codigo")
            val turno = extras.getString("IdHorario")

            // Establecer valores en las vistas
            idusuarioo.setText(idusuario)
            editnombres.setText(nombres)
            editapellido1.setText(apellido1)
            editapellido2.setText(apellido2)
            editdomicilio.setText(domicilio)
            edittelefono.setText(telefono)
            editcodigot.setText(codigo)
            if (turno == "Mañana") {
                turnoManana.isChecked = true
            } else {
                turnoTarde.isChecked = true
            }
        }
        editButton.setOnClickListener {
            val idusuario = extras?.getString("idusuario")
            if (idusuario != null) {
                guardarCambios(idusuario)
            } else {
                Toast.makeText(this, "Error: No se pudo obtener el ID del usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCambios(idusuario: String) {
        // Obtener los valores de los campos de texto
        val nuevosNombres = editnombres.text.toString()
        val nuevoApellido1 = editapellido1.text.toString()
        val nuevoApellido2 = editapellido2.text.toString()
        val nuevoDomicilio = editdomicilio.text.toString()
        val nuevoTelefono = edittelefono.text.toString()
        val nuevoCodigo = editcodigot.text.toString()
        val nuevoTurno = if (turnoManana.isChecked) "Mañana" else "Tarde"

        // Verificar si todos los campos están llenos
        if (nuevosNombres.isNotEmpty() && nuevoApellido1.isNotEmpty() && nuevoApellido2.isNotEmpty() &&
            nuevoDomicilio.isNotEmpty() && nuevoTelefono.isNotEmpty() && nuevoCodigo.isNotEmpty()
        ) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Confirmar")
            builder.setMessage("¿Estás seguro de que deseas agregar estos datos?")
            builder.setPositiveButton("Sí") { _, _ ->
                // Si el usuario confirma, proceder con la inserción de datos
                sendDataToServer(idusuario, nuevosNombres, nuevoApellido1, nuevoApellido2, nuevoDomicilio, nuevoTelefono, nuevoCodigo, nuevoTurno)
            }
            builder.setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            val dialog = builder.create()
            dialog.show()
        } else {
            // Mostrar un mensaje de error si algún campo está vacío
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun sendDataToServer(
        idusuario: String,
        nuevosNombres: String,
        nuevoApellido1: String,
        nuevoApellido2: String,
        nuevoDomicilio: String,
        nuevoTelefono: String,
        nuevoCodigo: String,
        nuevoTurno: String
    ) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando...")
        progressDialog.show()

        val request = object : StringRequest(
            Request.Method.POST,
            "https://labsanpualointelligence.online/Finales/Modificar.php",
            Response.Listener { response ->
                progressDialog.dismiss()
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MenuActivity::class.java)
                    startActivity(intent)
                    finish()
            },
            Response.ErrorListener { error ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al actualizar datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["idusuario"] = idusuario
                params["Nombres"] = nuevosNombres
                params["Apellido1"] = nuevoApellido1
                params["Apellido2"] = nuevoApellido2
                params["Domicilio"] = nuevoDomicilio
                params["Telefono"] = nuevoTelefono
                params["Codigo"] = nuevoCodigo
                params["IdHorario"] = nuevoTurno
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }
}



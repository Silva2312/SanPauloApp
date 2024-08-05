
package com.example.sanpauloapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Agregar : AppCompatActivity() {

    private lateinit var cargoSpinner: Spinner
    private lateinit var turnoSpinner: Spinner
    private lateinit var txtnombres: EditText
    private lateinit var txtap1: EditText
    private lateinit var txtap2: EditText
    private lateinit var txtdomicilio: EditText
    private lateinit var txttelefono: EditText
    private lateinit var txtcodigo: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_agregar)

        txtnombres = findViewById(R.id.nombres)
        txtap1 = findViewById(R.id.apellido1)
        txtap2 = findViewById(R.id.apellido2)
        txtdomicilio = findViewById(R.id.domicilio)
        txttelefono = findViewById(R.id.telefono)
        txtcodigo = findViewById(R.id.codigot)
        turnoSpinner = findViewById(R.id.turnoSpinner)
        cargoSpinner = findViewById(R.id.cargoSpinner)

        val saveButton: Button = findViewById(R.id.saveButton)

        saveButton.setOnClickListener {
            insertData()
        }
    }

    private fun insertData() {
        val nombre = txtnombres.text.toString().trim()
        val ap1 = txtap1.text.toString().trim()
        val ap2 = txtap2.text.toString().trim()
        val domicilio = txtdomicilio.text.toString().trim()
        val telefono = txttelefono.text.toString().trim()
        val codigo = txtcodigo.text.toString().trim()
        val idcargo = cargoSpinner.selectedItemPosition + 1 // Sumamos 1 porque los índices de los spinners comienzan en 0
        val idhorario = turnoSpinner.selectedItemPosition + 1 // Sumamos 1 porque los índices de los spinners comienzan en 0

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Estás seguro de que deseas agregar estos datos?")
        builder.setPositiveButton("Sí") { _, _ ->
            // Si el usuario confirma, proceder con la inserción de datos
            sendDataToServer(nombre, ap1, ap2, domicilio, telefono, codigo, idcargo, idhorario)
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun sendDataToServer(
        nombre: String,
        ap1: String,
        ap2: String,
        domicilio: String,
        telefono: String,
        codigo: String,
        idcargo: Int,
        idhorario: Int
    ) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando...")
        progressDialog.show()

        val request = object : StringRequest(
            Request.Method.POST,
            "http://192.168.3.164/Insertar.php",
            Response.Listener { response ->
                progressDialog.dismiss()
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
                if (response == "Datos insertados correctamente") {
                    startActivity(Intent(applicationContext, MenuActivity::class.java))
                    finish()
                }
            },
            Response.ErrorListener { error ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error al insertar datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Nombres"] = nombre
                params["Apellido1"] = ap1
                params["Apellido2"] = ap2
                params["Domicilio"] = domicilio
                params["Telefono"] = telefono
                params["Codigo"] = codigo
                params["IdCargo"] = idcargo.toString()
                params["IdHorario"] = idhorario.toString()
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }
}

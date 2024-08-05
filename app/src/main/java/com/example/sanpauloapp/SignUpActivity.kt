package com.example.sanpauloapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class SignUpActivity : AppCompatActivity() {

    private lateinit var sigusuario: EditText
    private lateinit var sigcontra: EditText
    private lateinit var sigcorreo: EditText
    private lateinit var sigtelefono: EditText
    private lateinit var signupButton: Button
    private lateinit var Cancelar: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up)


        sigusuario = findViewById(R.id.signup_name)
        sigcontra = findViewById(R.id.signup_password)
        sigcorreo = findViewById(R.id.signup_email)
        sigtelefono = findViewById(R.id.signup_telefono)
        Cancelar = findViewById(R.id.Cancelar)
        signupButton = findViewById(R.id.signup_button)

        signupButton.setOnClickListener {
            if (sigusuario.text.toString().isEmpty() || sigcontra.text.toString().isEmpty() || sigcorreo.text.toString().isEmpty() || sigtelefono.text.toString().isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            } else {
                insertData()
            }
        }
        Cancelar.setOnClickListener {
            startActivity(Intent(applicationContext, MenuActivity::class.java))
            finish()
        }
    }
    private fun insertData() {
        val usuario = sigusuario.text.toString()
        val contrasena = sigcontra.text.toString()
        val correo = sigcorreo.text.toString()
        val numtelefono = sigtelefono.text.toString()

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirmar")
        builder.setMessage("¿Estás seguro de que deseas agregar estos datos?")
        builder.setPositiveButton("Sí") { _, _ ->
            // Si el usuario confirma, proceder con la inserción de datos
            verificarUsuarioExistente(usuario, contrasena, correo, numtelefono)
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
    }

    private fun sendDataToServer(
        usuario: String,
        contrasena: String,
        correo: String,
        telefono: String,
    ) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Cargando...")
        progressDialog.show()

        val request = object : StringRequest(
            Request.Method.POST,
            "https://labsanpualointelligence.online/Finales/regadmin.php",
            //http://192.168.3.164/pruebas2/regadmin.php
            Response.Listener { response ->
                progressDialog.dismiss()
                Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                startActivity(Intent(applicationContext, MenuActivity::class.java))
                finish()
                if (response == "Registro exitoso") {

                }
            },
            Response.ErrorListener { error ->
                progressDialog.dismiss()
                //Toast.makeText(this, "Error al insertar datos: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Nombre"] = usuario
                params["Contraseña"] = contrasena
                params["Correo"] = correo
                params["NumTelefono"] = telefono
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }


    private fun verificarUsuarioExistente(usuario: String, contra: String, correo: String, telefono: String) {
        //val uell="https://193.203.166.182/finales/regadmin.php"
        val url = "https://labsanpualointelligence.online/Finales/VerificarUsuario.php"
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val usuarioExistente = jsonResponse.getBoolean("usuarioExistente")

                if (usuarioExistente) {
                    // El usuario ya existe, mostrar un mensaje o tomar alguna acción
                    Toast.makeText(this, "El usuario ya está registrado", Toast.LENGTH_SHORT).show()
                } else {
                    // El usuario no existe, proceder con el registro
                    sendDataToServer(usuario, contra, correo, telefono)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Nombre"] = usuario
                params["Contraseña"] = contra
                params["Correo"] = correo
                params["NumTelefono"] = telefono
                // Aquí puedes enviar otros parámetros necesarios para la verificación del usuario
                return params
            }
        }

        // Agregar la solicitud a la cola de solicitudes de Volley
        Volley.newRequestQueue(this).add(stringRequest)
    }
}
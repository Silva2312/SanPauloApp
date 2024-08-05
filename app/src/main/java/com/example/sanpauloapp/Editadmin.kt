package com.example.sanpauloapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley

class Editadmin : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_editar_admin)

        // Obtener referencias a los elementos de la interfaz
        val nuusuario: EditText = findViewById(R.id.editName)
        val nucontra: EditText = findViewById(R.id.editpassword)
        val nucorreo: EditText = findViewById(R.id.editcorreo)
        val nutelefono: EditText = findViewById(R.id.edittelefono)

        val saveButton: Button = findViewById(R.id.saveButton)

        // Obtener los datos del usuario a editar
        val idsup = intent.getStringExtra("Idsup") ?: ""
        val nusuario = intent.getStringExtra("Nombre") ?: ""
        val ncontra = intent.getStringExtra("Contraseña") ?: ""
        val ncorreo = intent.getStringExtra("Correo") ?: ""
        val ntelefono = intent.getStringExtra("NumTelefono") ?: ""

        // Mostrar los datos del usuario en los campos de edición
        nuusuario.setText(nusuario)
        nucontra.setText(ncontra)
        nucorreo.setText(ncorreo)
        nutelefono.setText(ntelefono)

        saveButton.setOnClickListener {
            val nuevosusuario = nuusuario.text.toString()
            val nuevacontra = nucontra.text.toString()
            val nuevocorreo = nucorreo.text.toString()
            val nuevotelefono = nutelefono.text.toString()
            sendDataToServer(idsup, nuevosusuario, nuevacontra, nuevocorreo, nuevotelefono)
        }
    }

    private fun sendDataToServer(
        idSupervisor: String,
        usuario: String,
        contra: String,
        correo: String,
        telefono: String
    ) {
        // Verificar si todos los campos están llenos y el formato del correo electrónico es válido
        if (usuario.isNotEmpty() && contra.isNotEmpty() && correo.isNotEmpty() && telefono.isNotEmpty() && isValidEmail(correo)) {
            val progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Cargando...")
            progressDialog.show()

            val request = object : StringRequest(
                Method.POST,
                "https://labsanpualointelligence.online/Finales/actudatosadmin.php",
                Response.Listener { response ->
                    progressDialog.dismiss()
                    handleResponse(response)
                },
                Response.ErrorListener { error ->
                    progressDialog.dismiss()
                    Toast.makeText(this, "Error al actualizar datos: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            ) {
                @Throws(AuthFailureError::class)
                override fun getParams(): Map<String, String> {
                    val params = HashMap<String, String>()
                    params["Idsup"] = idSupervisor
                    params["Nombre"] = usuario
                    params["Contraseña"] = contra
                    params["Correo"] = correo
                    params["NumTelefono"] = telefono
                    return params
                }
            }
            val requestQueue = Volley.newRequestQueue(this)
            requestQueue.add(request)
        } else {
            Toast.makeText(this, "Todos los campos deben estar llenos y el correo electrónico debe tener un formato válido", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")
        return emailRegex.matches(email)
    }

    private fun handleResponse(response: String) {
        if (response.trim() == "Datos actualizados correctamente") {
            Toast.makeText(this, "Actualización exitosa", Toast.LENGTH_SHORT).show()
            // Redirigir al MenuActivity
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish() // Cierra la actividad actual
        } else {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show()
        }
    }
}

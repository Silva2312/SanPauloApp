package com.example.sanpauloapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley


class MainActivity : AppCompatActivity() {
    private lateinit var usuario: EditText
    private lateinit var contra: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView

    private val FILL_FIELDS_MESSAGE = "Por favor, llene todos los campos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        usuario = findViewById(R.id.Usuario)
        contra = findViewById(R.id.Contra)
        loginButton = findViewById(R.id.Inicio)
        signupText = findViewById(R.id.Olvide)

        loginButton.setOnClickListener {
            val username = usuario.text.toString().trim()
            val password = contra.text.toString().trim()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                iniciarSesion(username, password)
            } else {
                Toast.makeText(this, FILL_FIELDS_MESSAGE, Toast.LENGTH_SHORT).show()
            }
        }
        signupText.setOnClickListener {
            val intent = Intent(this, OlvideContra::class.java)
            startActivity(intent)
        }

    }
    private fun iniciarSesion(usuario: String, contrasena: String) {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Iniciando sesión...")
        progressDialog.show()
        val request = object : StringRequest(
            Method.POST,
            //se modifica por el servidor
            "https://labsanpualointelligence.online/Finales/IniciarSesion.php",
            Response.Listener { response ->
                if (response == "true") {
                    Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()

                    // Iniciar MenuActivity y pasar el nombre de usuario
                    val intent = Intent(this, MenuActivity::class.java).apply {
                        putExtra("Nombre", usuario)
                        putExtra("Contraseña", contrasena)
                    }
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Nombre de usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error al iniciar sesión: ${error.message}", Toast.LENGTH_SHORT).show()
                progressDialog.dismiss()
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Nombre"] = usuario
                params["Contraseña"] = contrasena
                return params
            }
        }
        val requestQueue = Volley.newRequestQueue(this)
        requestQueue.add(request)
    }
}



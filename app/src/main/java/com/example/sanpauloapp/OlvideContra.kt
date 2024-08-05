package com.example.sanpauloapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class OlvideContra : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var btnRecuperar: Button

    private val URL_RECUPERAR_CONTRASENA = "https://labsanpualointelligence.online/Finales/recuperar2.php"
    private val ERROR_MESSAGE_DEFAULT = "Error al recuperar la contraseña. Inténtalo de nuevo más tarde."

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_olvide_contra)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        etEmail = findViewById(R.id.etEmail)
        btnRecuperar = findViewById(R.id.btnRecuperar)

        btnRecuperar.setOnClickListener {
            val email = etEmail.text.toString().trim()
            if (email.isNotEmpty()) {
                recuperarContrasena(email)
            } else {
                Toast.makeText(this, "Por favor, ingresa tu correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun recuperarContrasena(email: String) {
        val request = object : StringRequest(
            Request.Method.POST, URL_RECUPERAR_CONTRASENA,
            Response.Listener { response ->
                val jsonResponse = JSONObject(response)
                val success = jsonResponse.getBoolean("success")
                val message = jsonResponse.getString("message")

                if (success) {
                    val newPassword = jsonResponse.getString("new_password")
                    val dialogBuilder = AlertDialog.Builder(this)
                    dialogBuilder.setMessage("Tu nueva contraseña es: $newPassword")
                        .setCancelable(false)
                        .setPositiveButton("Aceptar") { dialog, _ ->
                            dialog.dismiss()
                        }
                    val alertDialog = dialogBuilder.create()
                    alertDialog.show()
                } else {
                    //ERROR_MESSAGE_DEFAULT
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Correo"] = email
                return params
            }
        }

        Volley.newRequestQueue(this).add(request)
    }
}

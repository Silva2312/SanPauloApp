package com.example.sanpauloapp

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class MenuActivity : AppCompatActivity() {

    private lateinit var usuario: String
    private lateinit var contrasena: String
    private lateinit var correo: String
    private lateinit var telefono: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayoutt)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val consultas = findViewById<CardView>(R.id.consultas)
        val reportes = findViewById<CardView>(R.id.reportes)
        val addadmin = findViewById<CardView>(R.id.addadmin)
        val editadmin = findViewById<CardView>(R.id.editadmin)
        usuario = intent.getStringExtra("Nombre") ?: ""
        contrasena = intent.getStringExtra("Contraseña") ?: ""
        correo = intent.getStringExtra("Correo") ?: ""
        telefono = intent.getStringExtra("NumTelefono") ?: ""
        consultas.setOnClickListener {
            try {
                val intent = Intent(this, Listarusuarios::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la actividad de consultas", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        }
        reportes.setOnClickListener {
            try {
                // Crea un Intent implícito para abrir la URL en Chrome
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://labsanpualointelligence.online/Reporte"))

                // Especifica el paquete de la aplicación de Chrome
                intent.setPackage("com.android.chrome")

                // Verifica si hay Chrome instalado y disponible para manejar el Intent
                if (intent.resolveActivity(packageManager) != null) {
                    startActivity(intent)
                } else {
                    // Si Chrome no está disponible, abre la URL en el navegador web predeterminado
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://labsanpualointelligence.online/Reporte")))
                }
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la URL", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }
        addadmin.setOnClickListener {
            try {

                val intent = Intent(this, SignUpActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la actividad de agregar admin", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        }
        editadmin.setOnClickListener {
            try {
                val intent = Intent(this, Editadmin::class.java).apply {
                    putExtra("Nombre", usuario)
                    putExtra("Contraseña", contrasena)
                    putExtra("Correo", correo)
                    putExtra("NumTelefono", telefono)
                    obtenerDatosUsuario()
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la actividad de administrar perfil", Toast.LENGTH_SHORT)
                    .show()
                e.printStackTrace()
            }
        }
    }

    private fun obtenerDatosUsuario() {
        val progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Obteniendo datos del usuario...")
        progressDialog.show()

        val url = "https://labsanpualointelligence.online/Finales/ObtenerDatosUsuario.php"
        val stringRequest = object : StringRequest(
            Request.Method.POST, url,
            Response.Listener { response ->
                progressDialog.dismiss()
                val jsonResponse = JSONObject(response)
                val success = jsonResponse.getBoolean("success")
                val message = jsonResponse.getString("message")

                if (success) {
                    val correo = jsonResponse.getString("Correo")
                    val telefono = jsonResponse.getString("NumTelefono")

                    // Pasar los datos del usuario a la actividad Editadmin
                    val intent = Intent(this, Editadmin::class.java).apply {
                        putExtra("Nombre", usuario)
                        putExtra("Contraseña", contrasena)
                        putExtra("Correo", correo)
                        putExtra("NumTelefono", telefono)
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                }
            },
            Response.ErrorListener { error ->
                progressDialog.dismiss()
                Toast.makeText(this, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        ) {
            override fun getParams(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Nombre"] = usuario
                params["Contraseña"] = contrasena
                params["Correo"] = correo
                params["NumTelefono"] = telefono
                return params
            }
        }

        Volley.newRequestQueue(this).add(stringRequest)
    }
}

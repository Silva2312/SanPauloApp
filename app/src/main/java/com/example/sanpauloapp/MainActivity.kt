package com.example.sanpauloapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var username: EditText
    private lateinit var password: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Inicializar vistas
        username = findViewById(R.id.username)
        password = findViewById(R.id.password)
        loginButton = findViewById(R.id.loginButton)

        // Agregar OnClickListener al botón de inicio de sesión
        loginButton.setOnClickListener {
            // Verificar credenciales de inicio de sesión
            if (username.text.toString() == "luis" && password.text.toString() == "1234") {
                // Iniciar sesión exitosa
                Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                // Ir a la pantalla de menú
                val intent = Intent(this, Menu::class.java)
                startActivity(intent)
                finish() // Finalizar esta actividad para evitar que el usuario vuelva atrás
            } else {
                // Iniciar sesión fallida
                Toast.makeText(this, "Login Failed!", Toast.LENGTH_SHORT).show()
            }
        }

        // Aplicar ajustes de borde a borde
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}

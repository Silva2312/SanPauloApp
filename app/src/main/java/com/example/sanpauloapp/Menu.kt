package com.example.sanpauloapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Toast

class Menu : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.constraintLayout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Encontrar las CardView por sus IDs
        val agregar = findViewById<CardView>(R.id.agregar)
        val modificar = findViewById<CardView>(R.id.cmodificar)
        val eliminar = findViewById<CardView>(R.id.celiminar)
        val buscarCard = findViewById<CardView>(R.id.cbuscar)
        val reportesCard = findViewById<CardView>(R.id.creportes)
        agregar.setOnClickListener {
            try {
                val intent = Intent(this, Agregar::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la actividad de agregar", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        modificar.setOnClickListener {
            try {
                val intent = Intent(this, Modificar::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la actividad de modificar", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        eliminar.setOnClickListener {
            try {
                val intent = Intent(this, Eliminar::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la actividad de eliminar", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        buscarCard.setOnClickListener {
            try {
                val intent = Intent(this, Buscar::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la actividad de buscar", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

        reportesCard.setOnClickListener {
            try {
                val intent = Intent(this, Reportes::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "No se pudo abrir la actividad de reportes", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        }

    }
}

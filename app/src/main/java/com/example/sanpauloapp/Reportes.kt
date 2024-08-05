package com.example.sanpauloapp

import android.Manifest
import android.app.DatePickerDialog
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class Reportes : AppCompatActivity() {
    private lateinit var btnDatePicker: Button
    private lateinit var btnDatePicker2: Button
    private lateinit var tvSelectedDate: TextView
    private lateinit var tvSelectedDate2: TextView
    private lateinit var btnreport: Button
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_reportes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        btnDatePicker = findViewById(R.id.btnDatePicker)
        btnDatePicker2 = findViewById(R.id.btnDatePicker2)
        tvSelectedDate = findViewById(R.id.tvSelectedDate)
        btnreport = findViewById(R.id.btnreporte)
        tvSelectedDate2 = findViewById(R.id.tvSelectedDate2)

        btnDatePicker.setOnClickListener {
            showDatePicker(tvSelectedDate)
        }
        btnDatePicker2.setOnClickListener {
            showDatePicker(tvSelectedDate2)
        }
        // Dentro de tu función onCreate()
        btnreport.setOnClickListener {
            val startDate = tvSelectedDate.text.toString().substringAfter(": ").trim()
            val endDate = tvSelectedDate2.text.toString().substringAfter(": ").trim()

            if (startDate.isEmpty() || endDate.isEmpty()) {
                Toast.makeText(this, "Selecciona ambas fechas para generar el reporte", Toast.LENGTH_SHORT).show()
            } else {
                generateReport(startDate, endDate)
            }
        }
    }

    private fun showDatePicker(textView: TextView) {
        val datePickerDialog = DatePickerDialog(
            this, { _, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate = dateFormat.format(selectedDate.time)
                textView.text = "Fecha: $formattedDate"
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun generateReport(startDate: String, endDate: String) {
        val url = "http://192.168.3.164/Wam/reportes.php" // Reemplaza con la URL de tu script PHP

        val queue = Volley.newRequestQueue(this)

        // Crear un objeto JSONObject con los parámetros
        val params = JSONObject().apply {
            put("startDate", startDate)
            put("endDate", endDate)
        }

        // Crear la solicitud JsonObjectRequest con los parámetros
        val stringRequest = JsonObjectRequest(
            Request.Method.POST, url, params, // Pasar el objeto JSONObject como parámetro
            { response ->
                // Manejar respuesta exitosa
                val reportData = response.getString("data") // Acceder a la clave "data" de la respuesta JSON
                if (reportData.isNullOrEmpty()) {
                    Toast.makeText(this, "No hay datos para las fechas seleccionadas", Toast.LENGTH_SHORT).show()
                } else {
                    saveReportToDevice(reportData)  // Llamar a la función para guardar el reporte
                }
            },
            { error ->
                // Manejar error en la solicitud
                Toast.makeText(this, "Error generando reporte: $error", Toast.LENGTH_SHORT).show()
            }
        )

        // Agregar la solicitud a la cola de solicitudes de Volley
        queue.add(stringRequest)
    }

    private fun saveReportToDevice(reportData: String) {
        // Verificar si se ha otorgado el permiso de almacenamiento externo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestStoragePermission.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            return // Salir si no se ha otorgado el permiso
        }

        // Generar un nombre de archivo único
        val fileName = "reporte_acceso_${System.currentTimeMillis()}.txt" // Cambiar .txt a la extensión deseada (PDF, CSV, etc.)

        // Obtener el directorio de almacenamiento externo para descargas
        val directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
        if (directory == null) {
            Toast.makeText(this, "Error al obtener el directorio de descargas", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear el archivo y escribir los datos del reporte
        val file = File(directory, fileName)
        val fileOutputStream = FileOutputStream(file)
        fileOutputStream.write(reportData.toByteArray())
        fileOutputStream.close()

        // Mostrar un mensaje Toast indicando el éxito al guardar
        Toast.makeText(this, "Reporte guardado en: $file", Toast.LENGTH_SHORT).show()
    }

    private val requestStoragePermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                // Se otorgó el permiso, vuelve a intentar guardar el reporte
                // Aquí podrías llamar a la función generateReport nuevamente con las mismas fechas
            } else {
                Toast.makeText(this, "Permiso de almacenamiento externo denegado", Toast.LENGTH_SHORT).show()
            }
        }
}

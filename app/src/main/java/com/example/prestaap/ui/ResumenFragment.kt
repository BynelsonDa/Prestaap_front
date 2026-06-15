package com.example.prestaap.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.prestaap.R
import com.example.prestaap.UiState
import com.example.prestaap.databinding.FragmentResumenBinding
import com.example.prestaap.viewmodel.ResumenViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import android.content.ContentValues
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import com.example.prestaap.data.model.ResumenIngresosResponse
import java.io.File
import java.io.FileOutputStream

class ResumenFragment : Fragment() {

    private var _binding: FragmentResumenBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ResumenViewModel by viewModels()
    private lateinit var adapter: MetodosPagoAdapter
    private var ultimoResumen: ResumenIngresosResponse? = null

    private val calendarInicio: Calendar = Calendar.getInstance()
    private val calendarFin: Calendar = Calendar.getInstance()

    private val displayFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    private val apiFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentResumenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        setupBottomNav()
        setupRecyclerView()
        setupDatePickers()
        setupButtons()
        observeViewModel()

        actualizarResumen()
    }

    private fun setupBottomNav() {
        binding.bottomNav.selectedItemId = R.id.nav_resumen

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_inicio -> {
                    findNavController().navigate(R.id.zonasFragment)
                    true
                }

                R.id.nav_clientes -> {
                    findNavController().navigate(R.id.clientesFragment)
                    true
                }

                R.id.nav_resumen -> true

                else -> true
            }
        }
    }

    private fun setupRecyclerView() {
        adapter = MetodosPagoAdapter()
        binding.rvMetodosPago.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMetodosPago.adapter = adapter
    }

    private fun setupDatePickers() {
        binding.tvFechaInicio.text = displayFormat.format(calendarInicio.time)
        binding.tvFechaFin.text = displayFormat.format(calendarFin.time)

        binding.btnFechaInicio.setOnClickListener {
            mostrarDatePicker(calendarInicio) {
                binding.tvFechaInicio.text = displayFormat.format(calendarInicio.time)
                actualizarResumen()
            }
        }

        binding.btnFechaFin.setOnClickListener {
            mostrarDatePicker(calendarFin) {
                binding.tvFechaFin.text = displayFormat.format(calendarFin.time)
                actualizarResumen()
            }
        }
    }

    private fun mostrarDatePicker(
        calendar: Calendar,
        onDateSelected: () -> Unit,
    ) {
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                onDateSelected()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH),
        ).show()
    }

    private fun setupButtons() {
        binding.btnPdf.setOnClickListener {
            val resumen = ultimoResumen

            if (resumen == null) {
                Toast.makeText(
                    requireContext(),
                    "Aún no hay datos para generar el PDF",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            generarPdfResumen(resumen)
        }
    }

    private fun actualizarResumen() {
        val fechaInicio = apiFormat.format(calendarInicio.time)
        val fechaFin = apiFormat.format(calendarFin.time)
        viewModel.cargarResumen(fechaInicio, fechaFin)
    }

    private fun observeViewModel() {
        viewModel.resumenState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.contentLayout.visibility = View.GONE
                }

                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE

                    val data = state.data
                    ultimoResumen = data

                    binding.tvTotalPrestado.text = formatPeso(data.totalPrestado.toLong())
                    binding.tvCapitalPrestado.text = formatPeso(data.capitalPrestado.toLong())
                    binding.tvInteresesPrestados.text = formatPeso(data.interesesPrestados.toLong())

                    binding.tvTotalRecibido.text = formatPeso(data.totalRecibido.toLong())
                    binding.tvCapitalRecibido.text = formatPeso(data.capitalRecibido.toLong())
                    binding.tvInteresesRecibidos.text = formatPeso(data.interesesRecibidos.toLong())

                    adapter.submitList(data.metodosPago)
                }

                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.contentLayout.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun generarPdfResumen(resumen: ResumenIngresosResponse) {
    try {
        val pdfDocument = PdfDocument()

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        val titlePaint = Paint().apply {
            color = android.graphics.Color.rgb(30, 64, 175)
            textSize = 22f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val subtitlePaint = Paint().apply {
            color = android.graphics.Color.rgb(75, 85, 99)
            textSize = 13f
        }

        val sectionPaint = Paint().apply {
            color = android.graphics.Color.rgb(31, 41, 55)
            textSize = 16f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        val normalPaint = Paint().apply {
            color = android.graphics.Color.rgb(55, 65, 81)
            textSize = 13f
        }

        val valuePaint = Paint().apply {
            color = android.graphics.Color.rgb(17, 24, 39)
            textSize = 13f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }

        var y = 60f

        canvas.drawText("Resumen Financiero", 40f, y, titlePaint)
        y += 24f

        canvas.drawText("Control de ingresos", 40f, y, subtitlePaint)
        y += 20f

        canvas.drawText(
            "Periodo: ${binding.tvFechaInicio.text} - ${binding.tvFechaFin.text}",
            40f,
            y,
            subtitlePaint
        )
        y += 35f

        canvas.drawLine(40f, y, 555f, y, normalPaint)
        y += 35f

        canvas.drawText("Resumen de préstamos", 40f, y, sectionPaint)
        y += 28f

        y = dibujarFilaPdf(canvas, "Total prestado", formatPeso(resumen.totalPrestado.toLong()), y, normalPaint, valuePaint)
        y = dibujarFilaPdf(canvas, "Capital prestado", formatPeso(resumen.capitalPrestado.toLong()), y, normalPaint, valuePaint)
        y = dibujarFilaPdf(canvas, "Intereses prestados", formatPeso(resumen.interesesPrestados.toLong()), y, normalPaint, valuePaint)

        y += 20f

        canvas.drawText("Ingresos recibidos", 40f, y, sectionPaint)
        y += 28f

        y = dibujarFilaPdf(canvas, "Total recibido", formatPeso(resumen.totalRecibido.toLong()), y, normalPaint, valuePaint)
        y = dibujarFilaPdf(canvas, "Capital recibido", formatPeso(resumen.capitalRecibido.toLong()), y, normalPaint, valuePaint)
        y = dibujarFilaPdf(canvas, "Intereses recibidos", formatPeso(resumen.interesesRecibidos.toLong()), y, normalPaint, valuePaint)

        y += 20f

        canvas.drawText("Ingresos por método de pago", 40f, y, sectionPaint)
        y += 28f

        if (resumen.metodosPago.isEmpty()) {
            canvas.drawText("No hay métodos de pago registrados.", 40f, y, normalPaint)
            y += 22f
        } else {
            resumen.metodosPago.forEach { metodo ->
                y = dibujarFilaPdf(
                    canvas,
                    metodo.metodoPago,
                    formatPeso(metodo.monto.toLong()),
                    y,
                    normalPaint,
                    valuePaint
                )
            }
        }

        y += 35f
        canvas.drawLine(40f, y, 555f, y, normalPaint)
        y += 25f

        canvas.drawText(
            "Generado desde Prestaap",
            40f,
            y,
            subtitlePaint
        )

        pdfDocument.finishPage(page)

        guardarPdf(pdfDocument)

    } catch (e: Exception) {
        Toast.makeText(
            requireContext(),
            "Error al generar PDF: ${e.message}",
            Toast.LENGTH_LONG
        ).show()
    }
}

private fun dibujarFilaPdf(
    canvas: Canvas,
    label: String,
    value: String,
    y: Float,
    normalPaint: Paint,
    valuePaint: Paint
): Float {
    canvas.drawText(label, 55f, y, normalPaint)
    canvas.drawText(value, 360f, y, valuePaint)
    return y + 24f
}

private fun guardarPdf(pdfDocument: PdfDocument) {
    val fileName = "resumen_ingresos_${System.currentTimeMillis()}.pdf"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.MIME_TYPE, "application/pdf")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
        }

        val resolver = requireContext().contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        if (uri != null) {
            resolver.openOutputStream(uri).use { outputStream ->
                if (outputStream != null) {
                    pdfDocument.writeTo(outputStream)
                }
            }

            pdfDocument.close()

            Toast.makeText(
                requireContext(),
                "PDF guardado en Descargas",
                Toast.LENGTH_LONG
            ).show()
        } else {
            pdfDocument.close()
            Toast.makeText(
                requireContext(),
                "No se pudo guardar el PDF",
                Toast.LENGTH_LONG
            ).show()
        }
    } else {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)

        FileOutputStream(file).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }

        pdfDocument.close()

        Toast.makeText(
            requireContext(),
            "PDF guardado en Descargas",
            Toast.LENGTH_LONG
        ).show()
    }
}

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
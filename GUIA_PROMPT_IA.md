# Guía de Prompt para Generar Vistas en Prestaap

Copia el bloque de **Contexto Base** completo al inicio de CADA prompt, luego añade la sección de **Solicitud específica**.

---

## BLOQUE DE CONTEXTO BASE (pégalo siempre)

```
Estoy trabajando en una app Android llamada Prestaap. Es una app de gestión de préstamos.
Necesito que generes archivos de código Kotlin y XML completos, listos para copiar y pegar.

=== ARQUITECTURA ===
Patrón: MVVM
Paquete raíz: com.example.prestaap

Estructura de paquetes:
  com.example.prestaap.ui/           → Fragments y Adapters (solo lógica de vista)
  com.example.prestaap.viewmodel/    → ViewModels con LiveData
  com.example.prestaap.data.model/   → Data classes que mapean el JSON del backend
  com.example.prestaap.data.api/     → ApiService (Retrofit) y RetrofitClient
  com.example.prestaap.data.repository/ → Repositorios que llaman a la API

=== REGLAS OBLIGATORIAS ===
1. SIEMPRE usar ViewBinding. NUNCA usar findViewById.
2. Los Fragments NUNCA llaman a la API directamente. Siempre a través del ViewModel.
3. TODA operación asíncrona usa este sealed class (ya existe, no lo recrear):
   sealed class UiState<out T> {
       object Loading : UiState<Nothing>()
       data class Success<T>(val data: T) : UiState<T>()
       data class Error(val message: String) : UiState<Nothing>()
   }
4. Importar UiState desde: import com.example.prestaap.UiState
5. Los ViewModels usan viewModelScope.launch { } para corrutinas.
6. Los Repositorios usan try/catch y devuelven UiState<T>.
7. Los Adapters usan DiffUtil y tienen un método submitList().

=== CLIENTE HTTP ===
RetrofitClient ya existe en com.example.prestaap.data.api.RetrofitClient
Acceder así: RetrofitClient.apiService.nombreDelMetodo()
El cliente ya incluye el token de Firebase automáticamente en cada petición.

=== API YA EXISTENTE (ApiService.kt) ===
@GET("zonas") → List<Zona>
@POST("clientes") @Body NuevoClienteRequest → Response<Void>
@GET("clientes") → Response<List<ClienteResponse>>
@GET("clientes/zona/{zonaId}") → Response<List<ClienteZona>>
@GET("creditos/{id}/detalle") → Response<CreditoDetalle>
@GET("creditos/cliente/{cedula}/resumen") → Response<List<CreditoResumen>>
@GET("frecuencias-pago") → Response<List<FrecuenciaPago>>
@POST("creditos") @Body CrearCreditoRequest → Response<CrearCreditoResponse>

=== MODELOS YA EXISTENTES ===
ClienteZona(cedula: Long, nombre: String, direccion: String, cantidadCreditos: Int, montoTotalPrestamo: Double, saldoPendiente: Double)
CreditoResumen(id: Int, label: String, montoPrestamo: Double, montoRestante: Double, cuotasPagadas: Int, totalCuotas: Int)
Zona(id: Int, nombre: String) [inferido]

=== CONVENCIONES DE NOMBRES ===
Fragment   → {Nombre}Fragment     | archivo: {Nombre}Fragment.kt
ViewModel  → {Nombre}ViewModel    | archivo: {Nombre}ViewModel.kt
Repository → {Nombre}Repository   | archivo: {Nombre}Repository.kt
Layout XML → fragment_{nombre}.xml  (snake_case)
Item lista → item_{nombre}.xml
Binding    → Fragment{Nombre}Binding (se genera automática de fragment_{nombre}.xml)

=== DISEÑO VISUAL (XML) ===
Todos los layouts de Fragment siguen ESTE patrón exacto:
  - Root: ConstraintLayout con android:background="@color/k_primary"
  - Header azul: FrameLayout de ~160dp con fondo @color/k_primary + ic_wave_pattern
  - Área de contenido: ConstraintLayout con android:background="@drawable/bg_content_rounded"
    que se superpone al header (marginTop="120dp")
  - BottomNavigationView al fondo (id: bottomNav, menu: @menu/bottom_nav_menu)
  - FloatingActionButton sobre el bottomNav (cuando aplique)

Colores disponibles:
  @color/k_primary   → azul principal
  @color/text_dark   → texto oscuro
  @color/text_hint   → texto gris placeholder
  @color/text_white  → blanco

Drawables reutilizables ya existentes:
  @drawable/bg_content_rounded   → fondo blanco con esquinas arriba redondeadas
  @drawable/ic_wave_pattern      → patrón de ola para el header
  @drawable/ic_back              → ícono de volver
  @drawable/ic_search            → ícono de búsqueda
  @drawable/bg_search_field      → fondo del campo de búsqueda
  @drawable/bg_spinner           → fondo del spinner
  @drawable/bg_badge_pendiente   → badge amarillo
  @drawable/bg_badge_pagado      → badge verde
  @drawable/bg_badge_atrasado    → badge rojo

=== NAVEGACIÓN ===
Se usa Navigation Component (NavController).
Para navegar: findNavController().navigate(R.id.action_origenFragment_to_destinoFragment, bundle)
Para volver: findNavController().navigateUp()
Para pasar datos entre fragments: Bundle con putString/putInt/putLong

=== MANEJO DE ESTADOS EN EL FRAGMENT ===
viewModel.liveData.observe(viewLifecycleOwner) { state ->
    when (state) {
        is UiState.Loading -> { /* mostrar progressBar */ }
        is UiState.Success -> { /* ocultar progressBar, usar state.data */ }
        is UiState.Error   -> { /* ocultar progressBar, mostrar Toast */ }
    }
}

=== FORMATO DE PESO COLOMBIANO ===
Ya existe la función: formatPeso(amount: Long): String
Definida en ClientesAdapter.kt, retorna "$1.250.000"
```

---

## CÓMO CONSTRUIR TU SOLICITUD ESPECÍFICA

Después del bloque base, añade esta sección completando cada punto:

```
=== LO QUE NECESITO ===

NOMBRE DE LA FUNCIONALIDAD: [ej: "Historial de Pagos"]

ARCHIVOS QUE DEBES GENERAR (uno por uno, completos):
  1. [NombreFragment].kt              → en ui/
  2. fragment_[nombre].xml            → en res/layout/
  3. [NombreViewModel].kt             → en viewmodel/
  4. [NombreRepository].kt            → en data/repository/  (si necesita nueva lógica de API)
  5. item_[nombre].xml                → en res/layout/       (si hay lista)
  6. [Nombre]Adapter.kt               → en ui/               (si hay lista)
  7. [NombreModelo].kt                → en data/model/       (si hay nuevo modelo JSON)

ENDPOINT QUE CONSUME:
  [Método HTTP] /[ruta] → devuelve [estructura JSON esperada]
  Ejemplo: GET /pagos/credito/{creditoId} → List<{ id, fecha, monto, estado }>

DATOS QUE LLEGAN POR BUNDLE DESDE EL FRAGMENT ANTERIOR:
  [campo]: [tipo]   →   arguments?.get[Tipo]("[campo]") ?: [valorDefecto]
  Ejemplo:
    creditoId: Int  →  arguments?.getInt("creditoId") ?: 0
    cedula: Long    →  arguments?.getLong("cedula") ?: 0L

DESCRIPCIÓN DE LA PANTALLA:
  [Describe qué ve el usuario: qué lista muestra, qué botones hay, qué acciones puede hacer]

NAVEGACIÓN:
  - Viene desde: [NombreFragment]
  - Al hacer click en X navega a: [OtroFragment] pasando [qué datos]
  - Acción en nav_graph a crear: action_[origen]_to_[destino]
```

---

## EJEMPLOS COMPLETOS DE PROMPTS

### Ejemplo 1 — Pantalla con lista simple

```
[PEGAR EL BLOQUE DE CONTEXTO BASE AQUÍ]

=== LO QUE NECESITO ===

NOMBRE DE LA FUNCIONALIDAD: Historial de Pagos de un Crédito

ARCHIVOS QUE DEBES GENERAR:
  1. PagosFragment.kt
  2. fragment_pagos.xml
  3. PagosViewModel.kt
  4. PagosRepository.kt
  5. item_pago.xml
  6. PagosAdapter.kt
  7. Pago.kt (nuevo modelo)

ENDPOINT QUE CONSUME:
  GET /pagos/credito/{creditoId}
  Respuesta JSON: List<{ id: Int, fecha: String, monto: Double, estado: String }>

DATOS QUE LLEGAN POR BUNDLE:
  creditoId: Int  →  arguments?.getInt("creditoId") ?: 0

DESCRIPCIÓN DE LA PANTALLA:
  Muestra una lista de pagos realizados para un crédito específico.
  Cada ítem muestra: fecha del pago, monto formateado en pesos colombianos y un badge de estado.
  Header azul con título "Historial de Pagos" y botón de volver.
  ProgressBar mientras carga. Toast en caso de error.

NAVEGACIÓN:
  - Viene desde: CreditoDetalleFragment
  - No navega a ningún otro Fragment
  - Acción a crear en nav_graph: action_creditoDetalleFragment_to_pagosFragment
```

### Ejemplo 2 — Pantalla de formulario

```
[PEGAR EL BLOQUE DE CONTEXTO BASE AQUÍ]

=== LO QUE NECESITO ===

NOMBRE DE LA FUNCIONALIDAD: Registrar Pago Manual

ARCHIVOS QUE DEBES GENERAR:
  1. RegistrarPagoFragment.kt
  2. fragment_registrar_pago.xml
  3. RegistrarPagoViewModel.kt
  4. RegistrarPagoRequest.kt (modelo del body del POST)

ENDPOINT QUE CONSUME:
  POST /pagos
  Body JSON: { creditoId: Int, monto: Double, fechaPago: String }
  Respuesta: Response<Void>

DATOS QUE LLEGAN POR BUNDLE:
  creditoId: Int  →  arguments?.getInt("creditoId") ?: 0

DESCRIPCIÓN DE LA PANTALLA:
  Formulario con campo de monto (número decimal) y selector de fecha.
  Botón "Registrar Pago" que llama al endpoint POST.
  Mientras procesa: deshabilitar el botón y mostrar ProgressBar.
  Al éxito: Toast "Pago registrado" y navegar hacia atrás.
  Al error: Toast con el mensaje de error.

NAVEGACIÓN:
  - Viene desde: CreditoDetalleFragment
  - Al éxito: findNavController().navigateUp()
  - Acción a crear: action_creditoDetalleFragment_to_registrarPagoFragment
```

### Ejemplo 3 — Pantalla de detalle (sin lista)

```
[PEGAR EL BLOQUE DE CONTEXTO BASE AQUÍ]

=== LO QUE NECESITO ===

NOMBRE DE LA FUNCIONALIDAD: Detalle de Zona

ARCHIVOS QUE DEBES GENERAR:
  1. ZonaDetalleFragment.kt
  2. fragment_zona_detalle.xml
  3. ZonaDetalleViewModel.kt
  4. ZonaDetalle.kt (nuevo modelo si el endpoint devuelve más info que el modelo Zona actual)

ENDPOINT QUE CONSUME:
  GET /zonas/{zonaId}
  Respuesta JSON: { id: Int, nombre: String, totalClientes: Int, totalCartera: Double, cobrador: String }

DATOS QUE LLEGAN POR BUNDLE:
  zonaId: Int     →  arguments?.getInt("zonaId") ?: 0
  zonaNombre: String  →  arguments?.getString("zonaNombre") ?: ""

DESCRIPCIÓN DE LA PANTALLA:
  Pantalla de solo lectura que muestra la información detallada de una zona.
  Muestra: nombre, total de clientes, cartera total formateada en pesos, nombre del cobrador.
  Cada dato se muestra en una fila con etiqueta a la izquierda y valor a la derecha.
  Header azul con el nombre de la zona y botón de volver.

NAVEGACIÓN:
  - Viene desde: ZonasFragment
  - No navega a ningún otro Fragment
  - Acción a crear: action_zonasFragment_to_zonaDetalleFragment
```

---

## CHECKLIST PARA VERIFICAR EL PROMPT ANTES DE ENVIARLO

Antes de enviar tu prompt a la IA, verifica:

- [ ] Pegué el bloque de contexto base completo
- [ ] Especifiqué TODOS los archivos que necesito que genere
- [ ] Describí el endpoint exacto (método + ruta + estructura JSON de respuesta)
- [ ] Indiqué qué datos llegan por Bundle y de qué tipo son
- [ ] Describí visualmente lo que debe mostrar la pantalla
- [ ] Especifiqué la navegación (de dónde viene, a dónde va, qué acción crear en el nav_graph)

---

## TIPS EXTRA

**Si el endpoint aún no existe en ApiService.kt**, añade al prompt:
```
AGREGAR ESTE MÉTODO A ApiService.kt:
  @GET("ruta/{param}")
  suspend fun nombreMetodo(@Path("param") param: Int): Response<TipoRespuesta>
```

**Si necesitas un nuevo modelo de datos**, describe el JSON exacto:
```
NUEVO MODELO (NombreModelo.kt):
JSON que devuelve el backend:
{
  "campo1": 123,
  "campo2": "texto",
  "campo3": true
}
```

**Si quieres reutilizar un adapter existente**, dilo explícitamente:
```
Para la lista reutilizar ClientesAdapter.kt que ya existe, no generes uno nuevo.
```

**Si necesitas un BottomSheet en lugar de un Fragment completo**:
```
No generes un Fragment, genera un BottomSheetDialogFragment con su layout bottom_sheet_[nombre].xml
Ejemplo existente de referencia: AbonarBottomSheet.kt y bottom_sheet_abonar.xml
```

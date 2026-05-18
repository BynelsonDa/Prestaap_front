# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build commands

```bash
# Compilar APK debug
./gradlew assembleDebug

# Instalar en dispositivo/emulador conectado
./gradlew installDebug

# Unit tests
./gradlew test

# Tests instrumentados (requiere dispositivo/emulador)
./gradlew connectedAndroidTest

# Lint
./gradlew lint

# Limpiar caché de build
./gradlew clean
```

El SDK está en `C:\Users\ISAAC\AppData\Local\Android\Sdk` (definido en `local.properties`).

## Arquitectura: MVVM

```
com.example.prestaap/
├── ui/                    ← Fragments y Adapters. Solo lógica de vista.
├── viewmodel/             ← ViewModels con LiveData. Sin referencias a Android Views.
├── data/
│   ├── model/             ← Data classes que mapean el JSON del backend
│   ├── api/               ← ApiService (Retrofit) y RetrofitClient
│   └── repository/        ← Repositorios que llaman a la API
├── StartupActivity.kt     ← Launcher: pantalla de bienvenida
├── LoginActivity.kt       ← Pantalla de login
└── MainActivity.kt        ← Contenedor principal de Fragments (dashboard)
```

## Dos paradigmas de UI coexistiendo

Este proyecto tiene **dos sistemas de UI activos al mismo tiempo** — esto es intencional:

- **`StartupActivity` y `LoginActivity`**: usan XML + ViewBinding + `Theme.FastCredit` (MaterialComponents). Sus layouts están en `res/layout/`.
- **`MainActivity`** y todo lo que venga después: usa **Jetpack Compose** + `Theme.Prestaap`. No tiene layout XML.

Al crear nuevas pantallas, seguir el paradigma según la capa:
- Pantallas de autenticación → XML + ViewBinding
- Dashboard y features → Compose

## Backend

| Entorno | Base URL |
|---|---|
| Emulador Android | `http://10.0.2.2:8080/` |
| Dispositivo físico | `http://192.168.x.x:8080/` |

| Método | Endpoint | Respuesta |
|---|---|---|
| `GET` | `/api/productos` | `List<Producto>` |
| `POST` | `/api/login` | `Token` |
| `GET` | `/api/usuario/{id}` | `Usuario` |

## Reglas

- **ViewBinding siempre** — nunca `findViewById`. Ya habilitado en `build.gradle.kts` (`viewBinding = true`).
- **Fragments nunca llaman a la API directamente** — siempre a través del ViewModel.
- **Tres estados en toda operación asíncrona:** `loading`, `success`, `error` mediante un sealed class `UiState<T>`.

```kotlin
sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
```

## Convenciones de nombres

| Tipo | Patrón | Ejemplo |
|---|---|---|
| Fragment | `{Nombre}Fragment` | `HomeFragment` |
| ViewModel | `{Nombre}ViewModel` | `HomeViewModel` |
| Layout de Fragment | `fragment_{nombre}.xml` | `fragment_home.xml` |
| Layout de ítem | `item_{nombre}.xml` | `item_producto.xml` |

## Dependencias pendientes de agregar

Cuando se implemente la capa de datos, agregar en `app/build.gradle.kts`:

```kotlin
implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")
implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
```

package com.example.prestaap.data.api

import com.example.prestaap.data.model.ClienteResponse
import com.example.prestaap.data.model.ClienteZona
import com.example.prestaap.data.model.CrearCreditoRequest
import com.example.prestaap.data.model.CrearCreditoResponse
import com.example.prestaap.data.model.CreditoDetalle
import com.example.prestaap.data.model.CreditoResumen
import com.example.prestaap.data.model.FrecuenciaPago
import com.example.prestaap.data.model.NuevoClienteRequest
import com.example.prestaap.data.model.Zona
import com.example.prestaap.data.model.AuthResponse
import com.example.prestaap.data.model.AbonoCreditoRequest
import com.example.prestaap.data.model.PagoResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("zonas")
    suspend fun getZonas(): List<Zona>

    @POST("clientes")
    suspend fun crearCliente(@Body request: NuevoClienteRequest): Response<Void>

    @GET("clientes")
    suspend fun getClientes(): Response<List<ClienteResponse>>

    @GET("clientes/zona/{zonaId}")
    suspend fun getClientesPorZona(@Path("zonaId") zonaId: Int): Response<List<ClienteZona>>

    @GET("creditos/{id}/detalle")
    suspend fun getDetalleCredito(@Path("id") id: Int): Response<CreditoDetalle>

    @GET("creditos/cliente/{cedula}/resumen")
    suspend fun getResumenCreditos(@Path("cedula") cedula: Long): Response<List<CreditoResumen>>

    @GET("frecuencias-pago")
    suspend fun getFrecuenciasPago(): Response<List<FrecuenciaPago>>

    @POST("creditos")
    suspend fun crearCredito(@Body request: CrearCreditoRequest): Response<CrearCreditoResponse>

    @POST("api/auth/google")
    suspend fun autenticarConGoogle(@Body body: Map<String, String>): Response<AuthResponse>

    @POST("pagos/credito/{id}")
    suspend fun crearPagoCredito(
        @Path("id") creditoId: Int,
        @Body request: AbonoCreditoRequest
    ): Response<PagoResponse>
}

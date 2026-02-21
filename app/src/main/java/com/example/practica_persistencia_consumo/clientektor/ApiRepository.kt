package com.example.practica_persistencia_consumo.clientektor

import com.example.practica_persistencia_consumo.CocheDao
import com.example.practica_persistencia_consumo.CocheMecanicoDao
import com.example.practica_persistencia_consumo.MotorDao
import com.example.practica_persistencia_consumo.PropietarioDao
import io.ktor.client.call.body
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.HttpStatusCode

/**
 * Repositorio que une la API remota (KTOR) con la base de datos local (Room).
 *
 * Patrón: cada operación hace primero la llamada HTTP y, si tiene éxito,
 * refleja el cambio en Room para mantener la UI reactiva sin recargar todo.
 */
class ApiRepository(
    private val cocheDao: CocheDao,
    private val motorDao: MotorDao,
    private val propietarioDao: PropietarioDao,
    private val cocheMecanicoDao: CocheMecanicoDao
) {
    private val client = ApiClient.client

    // ─────────────────────────── COCHES ───────────────────────────────────────

    /** GET /coches  →  descarga todos y los upsertea en Room */
    suspend fun fetchAndSyncCoches(): Result<List<CocheDto>> = runCatching {
        val dtos: List<CocheDto> = client.get(ApiRoutes.COCHES).body()
        dtos.forEach { cocheDao.upsertCoche(it.toEntity()) }
        dtos
    }

    /** POST /coches */
    suspend fun createCoche(dto: CocheDto): Result<CocheDto> = runCatching {
        val created: CocheDto = client.post(ApiRoutes.COCHES) { setBody(dto) }.body()
        cocheDao.upsertCoche(created.toEntity())
        created
    }

    /** PUT /coches/{id} */
    suspend fun updateCoche(dto: CocheDto): Result<CocheDto> = runCatching {
        val updated: CocheDto = client.put("${ApiRoutes.COCHES}/${dto.id}") { setBody(dto) }.body()
        cocheDao.upsertCoche(updated.toEntity())
        updated
    }

    /** DELETE /coches/{id} */
    suspend fun deleteCoche(id: Int): Result<Unit> = runCatching {
        val response = client.delete("${ApiRoutes.COCHES}/$id")
        if (response.status == HttpStatusCode.Companion.OK || response.status == HttpStatusCode.Companion.NoContent) {
            // Borramos de Room solo si la API lo confirma
            val local = cocheDao.getCocheByIdOnce(id)
            if (local != null) cocheDao.deleteCoche(local)
        }
    }

    // ─────────────────────────── MOTORES ──────────────────────────────────────

    suspend fun fetchAndSyncMotores(): Result<List<MotorDto>> = runCatching {
        val dtos: List<MotorDto> = client.get(ApiRoutes.MOTORES).body()
        dtos.forEach { motorDao.upsertMotor(it.toEntity()) }
        dtos
    }

    suspend fun createMotor(dto: MotorDto): Result<MotorDto> = runCatching {
        val created: MotorDto = client.post(ApiRoutes.MOTORES) { setBody(dto) }.body()
        motorDao.upsertMotor(created.toEntity())
        created
    }

    suspend fun updateMotor(dto: MotorDto): Result<MotorDto> = runCatching {
        val updated: MotorDto = client.put("${ApiRoutes.MOTORES}/${dto.id}") { setBody(dto) }.body()
        motorDao.upsertMotor(updated.toEntity())
        updated
    }

    suspend fun deleteMotor(id: Int): Result<Unit> = runCatching {
        val response = client.delete("${ApiRoutes.MOTORES}/$id")
        if (response.status == HttpStatusCode.Companion.OK || response.status == HttpStatusCode.Companion.NoContent) {
            val local = motorDao.getMotorByIdOnce(id)
            if (local != null) motorDao.deleteMotor(local)
        }
    }

    // ─────────────────────────── PROPIETARIOS ─────────────────────────────────

    suspend fun fetchAndSyncPropietarios(): Result<List<PropietarioDto>> = runCatching {
        val dtos: List<PropietarioDto> = client.get(ApiRoutes.PROPIETARIOS).body()
        dtos.forEach { propietarioDao.upsertPropietario(it.toEntity()) }
        dtos
    }

    suspend fun createPropietario(dto: PropietarioDto): Result<PropietarioDto> = runCatching {
        val created: PropietarioDto = client.post(ApiRoutes.PROPIETARIOS) { setBody(dto) }.body()
        propietarioDao.upsertPropietario(created.toEntity())
        created
    }

    suspend fun updatePropietario(dto: PropietarioDto): Result<PropietarioDto> = runCatching {
        val updated: PropietarioDto = client.put("${ApiRoutes.PROPIETARIOS}/${dto.propietarioId}") { setBody(dto) }.body()
        propietarioDao.upsertPropietario(updated.toEntity())
        updated
    }

    suspend fun deletePropietario(id: Int): Result<Unit> = runCatching {
        val response = client.delete("${ApiRoutes.PROPIETARIOS}/$id")
        if (response.status == HttpStatusCode.Companion.OK || response.status == HttpStatusCode.Companion.NoContent) {
            val local = propietarioDao.getPropietarioByIdOnce(id)
            if (local != null) propietarioDao.deletePropietario(local)
        }
    }

    // ─────────────────────────── MECÁNICOS ────────────────────────────────────

    suspend fun fetchAndSyncMecanicos(): Result<List<MecanicoDto>> = runCatching {
        val dtos: List<MecanicoDto> = client.get(ApiRoutes.MECANICOS).body()
        dtos.forEach { cocheMecanicoDao.upsertMecanico(it.toEntity()) }
        dtos
    }

    suspend fun createMecanico(dto: MecanicoDto): Result<MecanicoDto> = runCatching {
        val created: MecanicoDto = client.post(ApiRoutes.MECANICOS) { setBody(dto) }.body()
        cocheMecanicoDao.upsertMecanico(created.toEntity())
        created
    }

    suspend fun updateMecanico(dto: MecanicoDto): Result<MecanicoDto> = runCatching {
        val updated: MecanicoDto = client.put("${ApiRoutes.MECANICOS}/${dto.id}") { setBody(dto) }.body()
        cocheMecanicoDao.upsertMecanico(updated.toEntity())
        updated
    }

    suspend fun deleteMecanico(id: Int): Result<Unit> = runCatching {
        val response = client.delete("${ApiRoutes.MECANICOS}/$id")
        if (response.status == HttpStatusCode.Companion.OK || response.status == HttpStatusCode.Companion.NoContent) {
            val local = cocheMecanicoDao.getMecanicoByIdOnce(id)
            if (local != null) cocheMecanicoDao.deleteMecanico(local)
        }
    }

    // ─────────────────────────── COCHE-MECÁNICO ───────────────────────────────

    suspend fun fetchAndSyncCocheMecanico(): Result<List<CocheMecanicoDto>> = runCatching {
        val dtos: List<CocheMecanicoDto> = client.get(ApiRoutes.COCHEMECANICO).body()
        dtos.forEach { cocheMecanicoDao.insertCocheMecanicoCrossRef(it.toEntity()) }
        dtos
    }

    /** Sincroniza toda la base de datos con la API de una sola vez */
    suspend fun syncAll(): Result<String> = runCatching {
        val p = fetchAndSyncPropietarios().getOrThrow()
        val c = fetchAndSyncCoches().getOrThrow()
        val m = fetchAndSyncMotores().getOrThrow()
        val mec = fetchAndSyncMecanicos().getOrThrow()
        val cm = fetchAndSyncCocheMecanico().getOrThrow()
        "Sincronizado: ${p.size} propietarios, ${c.size} coches, " +
                "${m.size} motores, ${mec.size} mecánicos, ${cm.size} asignaciones"
    }
}
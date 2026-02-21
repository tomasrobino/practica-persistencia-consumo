package com.example.practica_persistencia_consumo.clientektor

import com.example.practica_persistencia_consumo.CocheDao
import com.example.practica_persistencia_consumo.CocheMecanicoCrossRef
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

class ApiRepository(
    private val cocheDao: CocheDao,
    private val motorDao: MotorDao,
    private val propietarioDao: PropietarioDao,
    private val cocheMecanicoDao: CocheMecanicoDao
) {
    private val client = ApiClient.client



    suspend fun fetchAndSyncCoches(): Result<List<CocheDto>> = runCatching {
        val dtos: List<CocheDto> = client.get(ApiRoutes.COCHES).body()
        dtos.forEach { cocheDao.upsertCoche(it.toEntity()) }
        dtos
    }

    suspend fun createCoche(dto: CocheDto): Result<CocheDto> = runCatching {
        val created: CocheDto = client.post(ApiRoutes.COCHES) {
            setBody(CreateCocheDto(
                color = dto.color,
                marca = dto.marca,
                modelo = dto.modelo,
                propietarioId = dto.propietarioId
            ))
        }.body()
        cocheDao.upsertCoche(created.toEntity())
        created
    }

    suspend fun updateCoche(dto: CocheDto): Result<CocheDto> = runCatching {
        val updated: CocheDto = client.put("${ApiRoutes.COCHES}/${dto.id}") { setBody(dto) }.body()
        cocheDao.upsertCoche(updated.toEntity())
        updated
    }

    suspend fun deleteCoche(id: Int): Result<Unit> = runCatching {
        val response = client.delete("${ApiRoutes.COCHES}/$id")
        if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent) {
            val local = cocheDao.getCocheByIdOnce(id)
            if (local != null) cocheDao.deleteCoche(local)
        }
    }



    suspend fun fetchAndSyncMotores(): Result<List<MotorDto>> = runCatching {
        val dtos: List<MotorDto> = client.get(ApiRoutes.MOTORES).body()
        dtos.forEach { motorDao.upsertMotor(it.toEntity()) }
        dtos
    }

    suspend fun createMotor(dto: MotorDto): Result<MotorDto> = runCatching {
        val created: MotorDto = client.post(ApiRoutes.MOTORES) {
            setBody(CreateMotorDto(
                marca = dto.marca,
                modelo = dto.modelo,
                cilindrada = dto.cilindrada,
                cocheId = dto.cocheId
            ))
        }.body()
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
        if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent) {
            val local = motorDao.getMotorByIdOnce(id)
            if (local != null) motorDao.deleteMotor(local)
        }
    }



    suspend fun fetchAndSyncPropietarios(): Result<List<PropietarioDto>> = runCatching {
        val dtos: List<PropietarioDto> = client.get(ApiRoutes.PROPIETARIOS).body()
        dtos.forEach { propietarioDao.upsertPropietario(it.toEntity()) }
        dtos
    }

    suspend fun createPropietario(dto: PropietarioDto): Result<PropietarioDto> = runCatching {
        val created: PropietarioDto = client.post(ApiRoutes.PROPIETARIOS) {
            setBody(CreatePropietarioDto(
                nombre = dto.nombre,
                telefono = dto.telefono
            ))
        }.body()
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
        if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent) {
            val local = propietarioDao.getPropietarioByIdOnce(id)
            if (local != null) propietarioDao.deletePropietario(local)
        }
    }



    suspend fun fetchAndSyncMecanicos(): Result<List<MecanicoDto>> = runCatching {
        val dtos: List<MecanicoDto> = client.get(ApiRoutes.MECANICOS).body()
        dtos.forEach { cocheMecanicoDao.upsertMecanico(it.toEntity()) }
        dtos
    }

    // FIX: use CreateMecanicoDto (no id field) so the server auto-generates the id.
    suspend fun createMecanico(dto: MecanicoDto): Result<MecanicoDto> = runCatching {
        val created: MecanicoDto = client.post(ApiRoutes.MECANICOS) {
            setBody(CreateMecanicoDto(
                nombre = dto.nombre,
                especialidad = dto.especialidad
            ))
        }.body()
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
        if (response.status == HttpStatusCode.OK || response.status == HttpStatusCode.NoContent) {
            val local = cocheMecanicoDao.getMecanicoByIdOnce(id)
            if (local != null) cocheMecanicoDao.deleteMecanico(local)
        }
    }



    suspend fun fetchAndSyncCocheMecanico(): Result<List<CocheMecanicoDto>> = runCatching {
        val dtos: List<CocheMecanicoDto> = client.get(ApiRoutes.COCHEMECANICO).body()
        dtos.forEach { cocheMecanicoDao.insertCocheMecanicoCrossRef(it.toEntity()) }
        dtos
    }

    suspend fun createCocheMecanico(cocheId: Int, mecanicoId: Int): Result<Unit> = runCatching {
        val dto = CocheMecanicoDto(cocheId = cocheId, mecanicoId = mecanicoId)
        client.post(ApiRoutes.COCHEMECANICO) { setBody(dto) }
        cocheMecanicoDao.insertCocheMecanicoCrossRef(dto.toEntity())
    }

    suspend fun deleteCocheMecanico(cocheId: Int, mecanicoId: Int): Result<Unit> = runCatching {
        client.delete(ApiRoutes.COCHEMECANICO) {
            setBody(CocheMecanicoDto(cocheId = cocheId, mecanicoId = mecanicoId))
        }
        cocheMecanicoDao.deleteCocheMecanicoCrossRef(CocheMecanicoCrossRef(cocheId, mecanicoId))
    }



    suspend fun syncAll(): Result<String> = runCatching {
        val p   = fetchAndSyncPropietarios().getOrThrow()
        val c   = fetchAndSyncCoches().getOrThrow()
        val m   = fetchAndSyncMotores().getOrThrow()
        val mec = fetchAndSyncMecanicos().getOrThrow()
        val cm  = fetchAndSyncCocheMecanico().getOrThrow()
        "Sincronizado: ${p.size} propietarios, ${c.size} coches, " +
                "${m.size} motores, ${mec.size} mecánicos, ${cm.size} asignaciones"
    }
}
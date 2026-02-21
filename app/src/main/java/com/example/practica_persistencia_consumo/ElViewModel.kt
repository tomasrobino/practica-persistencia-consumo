package com.example.practica_persistencia_consumo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica_persistencia_consumo.clientektor.ApiRepository
import com.example.practica_persistencia_consumo.clientektor.CocheDto
import com.example.practica_persistencia_consumo.clientektor.MecanicoDto
import com.example.practica_persistencia_consumo.clientektor.MotorDto
import com.example.practica_persistencia_consumo.clientektor.PropietarioDto
import com.example.practica_persistencia_consumo.clientektor.toDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ElViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ConcesionarioDatabase.getDatabase(application)
    private val cocheDao = db.cocheDao()
    private val motorDao = db.motorDao()
    private val propietarioDao = db.propietarioDao()
    private val cocheMecanicoDao = db.cocheMecanicoDao()

    private val apiRepository = ApiRepository(
        cocheDao = cocheDao,
        motorDao = motorDao,
        propietarioDao = propietarioDao,
        cocheMecanicoDao = cocheMecanicoDao
    )


    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()



    val cochesConMotor: StateFlow<List<CocheConMotor>> = cocheDao.getCochesConMotor().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
    )
    val allCoches: StateFlow<List<Coche>> = cocheDao.getAllCoches().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
    )
    val allMotores: StateFlow<List<Motor>> = motorDao.getAllMotores().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
    )
    val propietariosConCoches: StateFlow<List<PropietarioConCoches>> = propietarioDao.getPropietariosConCoches().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
    )
    val allPropietarios: StateFlow<List<Propietario>> = propietarioDao.getAllPropietarios().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
    )
    val mecanicosConCoches: StateFlow<List<MecanicosConCoche>> = cocheMecanicoDao.getMecanicosConCoches().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
    )
    val allMecanicos: StateFlow<List<Mecanico>> = cocheMecanicoDao.getAllMecanicos().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
    )
    val cochesConMecanicos: StateFlow<List<CocheConMecanicos>> = cocheMecanicoDao.getCochesConMecanicos().stateIn(
        scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
    )



    fun insertCoche(color: String, marca: String, modelo: String, propietarioId: Int?) {
        viewModelScope.launch { cocheDao.insert(Coche(color = color, marca = marca, modelo = modelo, propietarioId = propietarioId)) }
    }
    fun updateCoche(coche: Coche) { viewModelScope.launch { cocheDao.updateCoche(coche) } }
    fun deleteCoche(coche: Coche) { viewModelScope.launch { cocheDao.deleteCoche(coche) } }

    fun insertMotor(marca: String, modelo: String, cilindrada: Int, cocheId: Int) {
        viewModelScope.launch { motorDao.insertMotor(Motor(marca = marca, modelo = modelo, cilindrada = cilindrada, cocheId = cocheId)) }
    }
    fun updateMotor(motor: Motor) { viewModelScope.launch { motorDao.updateMotor(motor) } }
    fun deleteMotor(motor: Motor) { viewModelScope.launch { motorDao.deleteMotor(motor) } }

    fun insertPropietario(nombre: String, telefono: String) {
        viewModelScope.launch { propietarioDao.insertPropietario(Propietario(nombre = nombre, telefono = telefono)) }
    }
    fun updatePropietario(propietario: Propietario) { viewModelScope.launch { propietarioDao.updatePropietario(propietario) } }
    fun deletePropietario(propietario: Propietario) { viewModelScope.launch { propietarioDao.deletePropietario(propietario) } }

    fun insertMecanico(nombre: String, especialidad: String) {
        viewModelScope.launch { cocheMecanicoDao.insertMecanico(Mecanico(nombre = nombre, especialidad = especialidad)) }
    }
    fun updateMecanico(mecanico: Mecanico) { viewModelScope.launch { cocheMecanicoDao.updateMecanico(mecanico) } }
    fun deleteMecanico(mecanico: Mecanico) { viewModelScope.launch { cocheMecanicoDao.deleteMecanico(mecanico) } }

    fun insertCocheMecanicoCrossRef(cocheId: Int, mecanicoId: Int) {
        viewModelScope.launch { cocheMecanicoDao.insertCocheMecanicoCrossRef(CocheMecanicoCrossRef(cocheId = cocheId, mecanicoId = mecanicoId)) }
    }
    fun deleteCocheMecanicoCrossRef(cocheId: Int, mecanicoId: Int) {
        viewModelScope.launch { cocheMecanicoDao.deleteCocheMecanicoCrossRef(CocheMecanicoCrossRef(cocheId = cocheId, mecanicoId = mecanicoId)) }
    }



    fun syncAll() {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("Sincronizando con la API...")
            apiRepository.syncAll()
                .onSuccess { msg -> _syncStatus.value = SyncStatus.Success(msg) }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error sync: ${e.message}") }
        }
    }

    fun fetchCoches() = apiLaunch("GET /coches") { apiRepository.fetchAndSyncCoches().map { "${it.size} coches" } }
    fun fetchMotores() = apiLaunch("GET /motores") { apiRepository.fetchAndSyncMotores().map { "${it.size} motores" } }
    fun fetchPropietarios() = apiLaunch("GET /propietarios") { apiRepository.fetchAndSyncPropietarios().map { "${it.size} propietarios" } }
    fun fetchMecanicos() = apiLaunch("GET /mecanicos") { apiRepository.fetchAndSyncMecanicos().map { "${it.size} mecánicos" } }
    fun fetchCocheMecanico() = apiLaunch("GET /cochemecanico") { apiRepository.fetchAndSyncCocheMecanico().map { "${it.size} asignaciones" } }



    fun apiCreateCoche(color: String, marca: String, modelo: String, propietarioId: Int?) =
        apiLaunch("POST /coches") {
            apiRepository.createCoche(
                CocheDto(
                    color = color,
                    marca = marca,
                    modelo = modelo,
                    propietarioId = propietarioId
                )
            )
                .map { "Coche creado id ${it.id}" }
        }

    fun apiCreateMotor(marca: String, modelo: String, cilindrada: Int, cocheId: Int) =
        apiLaunch("POST /motores") {
            apiRepository.createMotor(
                MotorDto(
                    marca = marca,
                    modelo = modelo,
                    cilindrada = cilindrada,
                    cocheId = cocheId
                )
            )
                .map { "Motor creado id ${it.id}" }
        }

    fun apiCreatePropietario(nombre: String, telefono: String) =
        apiLaunch("POST /propietarios") {
            apiRepository.createPropietario(PropietarioDto(nombre = nombre, telefono = telefono))
                .map { "Propietario creado id ${it.propietarioId}" }
        }

    fun apiCreateMecanico(nombre: String, especialidad: String) =
        apiLaunch("POST /mecanicos") {
            apiRepository.createMecanico(MecanicoDto(nombre = nombre, especialidad = especialidad))
                .map { "Mecánico creado id ${it.id}" }
        }

    fun apiCreateCocheMecanico(cocheId: Int, mecanicoId: Int) =
        apiLaunch("POST /cochemecanico") {
            apiRepository.createCocheMecanico(cocheId, mecanicoId)
                .map { "Asignación coche $cocheId ↔ mecánico $mecanicoId creada" }
        }



    fun apiUpdateCoche(coche: Coche) =
        apiLaunch("PUT /coches/${coche.id}") {
            apiRepository.updateCoche(coche.toDto()).map { "Coche ${it.id} actualizado" }
        }

    fun apiUpdateMotor(motor: Motor) =
        apiLaunch("PUT /motores/${motor.id}") {
            apiRepository.updateMotor(motor.toDto()).map { "Motor ${it.id} actualizado" }
        }

    fun apiUpdatePropietario(propietario: Propietario) =
        apiLaunch("PUT /propietarios/${propietario.propietarioId}") {
            apiRepository.updatePropietario(propietario.toDto()).map { "Propietario ${it.propietarioId} actualizado" }
        }

    fun apiUpdateMecanico(mecanico: Mecanico) =
        apiLaunch("PUT /mecanicos/${mecanico.id}") {
            apiRepository.updateMecanico(mecanico.toDto()).map { "Mecánico ${it.id} actualizado" }
        }




    fun apiDeleteCoche(coche: Coche) =
        apiLaunch("DELETE /coches/${coche.id}") {
            apiRepository.deleteCoche(coche.id).map { "Coche ${coche.id} eliminado" }
        }

    fun apiDeleteMotor(motor: Motor) =
        apiLaunch("DELETE /motores/${motor.id}") {
            apiRepository.deleteMotor(motor.id).map { "Motor ${motor.id} eliminado" }
        }

    fun apiDeletePropietario(propietario: Propietario) =
        apiLaunch("DELETE /propietarios/${propietario.propietarioId}") {
            apiRepository.deletePropietario(propietario.propietarioId).map { "Propietario ${propietario.propietarioId} eliminado" }
        }

    fun apiDeleteMecanico(mecanico: Mecanico) =
        apiLaunch("DELETE /mecanicos/${mecanico.id}") {
            apiRepository.deleteMecanico(mecanico.id).map { "Mecánico ${mecanico.id} eliminado" }
        }

    fun apiDeleteCocheMecanico(cocheId: Int, mecanicoId: Int) =
        apiLaunch("DELETE /cochemecanico") {
            apiRepository.deleteCocheMecanico(cocheId, mecanicoId)
                .map { "Asignación coche $cocheId ↔ mecánico $mecanicoId eliminada" }
        }




    fun clearSyncStatus() { _syncStatus.value = SyncStatus.Idle }

    /**
     * Lanza una operación API actualizando el banner de estado automáticamente.
     * [block] debe devolver un Result<String> con el mensaje de éxito.
     */
    private fun apiLaunch(operacion: String, block: suspend () -> Result<String>) {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("$operacion...")
            block()
                .onSuccess { msg -> _syncStatus.value = SyncStatus.Success("$operacion → $msg") }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error $operacion: ${e.message}") }
        }
    }
}


sealed class SyncStatus {
    data object Idle : SyncStatus()
    data class Loading(val message: String) : SyncStatus()
    data class Success(val message: String) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}
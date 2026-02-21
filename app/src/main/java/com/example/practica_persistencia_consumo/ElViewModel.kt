package com.example.practica_persistencia_consumo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.practica_persistencia_consumo.clientektor.ApiRepository
import com.example.practica_persistencia_consumo.clientektor.CocheDto
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

    // ─── Repositorio API ───────────────────────────────────────────────────────
    private val apiRepository = ApiRepository(
        cocheDao = cocheDao,
        motorDao = motorDao,
        propietarioDao = propietarioDao,
        cocheMecanicoDao = cocheMecanicoDao
    )

    // ─── Estado de sincronización ──────────────────────────────────────────────
    private val _syncStatus = MutableStateFlow<SyncStatus>(SyncStatus.Idle)
    val syncStatus: StateFlow<SyncStatus> = _syncStatus.asStateFlow()

    // ─── Flows de Room ─────────────────────────────────────────────────────────

    val cochesConMotor: StateFlow<List<CocheConMotor>> = cocheDao.getCochesConMotor().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allCoches: StateFlow<List<Coche>> = cocheDao.getAllCoches().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allMotores: StateFlow<List<Motor>> = motorDao.getAllMotores().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val propietariosConCoches: StateFlow<List<PropietarioConCoches>> = propietarioDao.getPropietariosConCoches().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allPropietarios: StateFlow<List<Propietario>> = propietarioDao.getAllPropietarios().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val mecanicosConCoches: StateFlow<List<MecanicosConCoche>> = cocheMecanicoDao.getMecanicosConCoches().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allMecanicos: StateFlow<List<Mecanico>> = cocheMecanicoDao.getAllMecanicos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cochesConMecanicos: StateFlow<List<CocheConMecanicos>> = cocheMecanicoDao.getCochesConMecanicos().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // ─── CRUD local (Room) ─────────────────────────────────────────────────────

    fun insertCoche(color: String, marca: String, modelo: String, propietarioId: Int?) {
        viewModelScope.launch {
            cocheDao.insert(Coche(color = color, marca = marca, modelo = modelo, propietarioId = propietarioId))
        }
    }

    fun updateCoche(coche: Coche) {
        viewModelScope.launch { cocheDao.updateCoche(coche) }
    }

    fun deleteCoche(coche: Coche) {
        viewModelScope.launch { cocheDao.deleteCoche(coche) }
    }

    fun insertMotor(marca: String, modelo: String, cilindrada: Int, cocheId: Int) {
        viewModelScope.launch {
            motorDao.insertMotor(Motor(marca = marca, modelo = modelo, cilindrada = cilindrada, cocheId = cocheId))
        }
    }

    fun updateMotor(motor: Motor) {
        viewModelScope.launch { motorDao.updateMotor(motor) }
    }

    fun deleteMotor(motor: Motor) {
        viewModelScope.launch { motorDao.deleteMotor(motor) }
    }

    fun insertPropietario(nombre: String, telefono: String) {
        viewModelScope.launch {
            propietarioDao.insertPropietario(Propietario(nombre = nombre, telefono = telefono))
        }
    }

    fun updatePropietario(propietario: Propietario) {
        viewModelScope.launch { propietarioDao.updatePropietario(propietario) }
    }

    fun deletePropietario(propietario: Propietario) {
        viewModelScope.launch { propietarioDao.deletePropietario(propietario) }
    }

    fun insertMecanico(nombre: String, especialidad: String) {
        viewModelScope.launch {
            cocheMecanicoDao.insertMecanico(Mecanico(nombre = nombre, especialidad = especialidad))
        }
    }

    fun updateMecanico(mecanico: Mecanico) {
        viewModelScope.launch { cocheMecanicoDao.updateMecanico(mecanico) }
    }

    fun deleteMecanico(mecanico: Mecanico) {
        viewModelScope.launch { cocheMecanicoDao.deleteMecanico(mecanico) }
    }

    fun insertCocheMecanicoCrossRef(cocheId: Int, mecanicoId: Int) {
        viewModelScope.launch {
            cocheMecanicoDao.insertCocheMecanicoCrossRef(CocheMecanicoCrossRef(cocheId = cocheId, mecanicoId = mecanicoId))
        }
    }

    fun deleteCocheMecanicoCrossRef(cocheId: Int, mecanicoId: Int) {
        viewModelScope.launch {
            cocheMecanicoDao.deleteCocheMecanicoCrossRef(CocheMecanicoCrossRef(cocheId = cocheId, mecanicoId = mecanicoId))
        }
    }

    // ─── Operaciones API (GET / POST / PUT / DELETE) ───────────────────────────

    /** Descarga y persiste todos los datos de la API de una vez */
    fun syncAll() {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("Sincronizando con la API...")
            apiRepository.syncAll()
                .onSuccess { msg -> _syncStatus.value = SyncStatus.Success(msg) }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error: ${e.message}") }
        }
    }

    /** GET /coches */
    fun fetchCoches() {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("GET /coches...")
            apiRepository.fetchAndSyncCoches()
                .onSuccess { list -> _syncStatus.value = SyncStatus.Success("GET /coches → ${list.size} coches") }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error GET coches: ${e.message}") }
        }
    }

    /** POST /coches */
    fun apiCreateCoche(color: String, marca: String, modelo: String, propietarioId: Int?) {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("POST /coches...")
            apiRepository.createCoche(
                CocheDto(
                    color = color,
                    marca = marca,
                    modelo = modelo,
                    propietarioId = propietarioId
                )
            )
                .onSuccess { _syncStatus.value = SyncStatus.Success("POST /coches OK → id ${it.id}") }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error POST coches: ${e.message}") }
        }
    }

    /** PUT /coches/{id} */
    fun apiUpdateCoche(coche: Coche) {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("PUT /coches/${coche.id}...")
            apiRepository.updateCoche(coche.toDto())
                .onSuccess { _syncStatus.value = SyncStatus.Success("PUT /coches/${coche.id} OK") }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error PUT coches: ${e.message}") }
        }
    }

    /** DELETE /coches/{id} */
    fun apiDeleteCoche(coche: Coche) {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("DELETE /coches/${coche.id}...")
            apiRepository.deleteCoche(coche.id)
                .onSuccess { _syncStatus.value = SyncStatus.Success("DELETE /coches/${coche.id} OK") }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error DELETE coches: ${e.message}") }
        }
    }

    /** GET /motores */
    fun fetchMotores() {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("GET /motores...")
            apiRepository.fetchAndSyncMotores()
                .onSuccess { list -> _syncStatus.value = SyncStatus.Success("GET /motores → ${list.size} motores") }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error GET motores: ${e.message}") }
        }
    }

    /** GET /propietarios */
    fun fetchPropietarios() {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("GET /propietarios...")
            apiRepository.fetchAndSyncPropietarios()
                .onSuccess { list -> _syncStatus.value = SyncStatus.Success("GET /propietarios → ${list.size} propietarios") }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error GET propietarios: ${e.message}") }
        }
    }

    /** GET /mecanicos */
    fun fetchMecanicos() {
        viewModelScope.launch {
            _syncStatus.value = SyncStatus.Loading("GET /mecanicos...")
            apiRepository.fetchAndSyncMecanicos()
                .onSuccess { list -> _syncStatus.value = SyncStatus.Success("GET /mecanicos → ${list.size} mecánicos") }
                .onFailure { e -> _syncStatus.value = SyncStatus.Error("Error GET mecanicos: ${e.message}") }
        }
    }

    fun clearSyncStatus() {
        _syncStatus.value = SyncStatus.Idle
    }
}

/** Estado de la operación API para mostrar feedback en la UI */
sealed class SyncStatus {
    data object Idle : SyncStatus()
    data class Loading(val message: String) : SyncStatus()
    data class Success(val message: String) : SyncStatus()
    data class Error(val message: String) : SyncStatus()
}
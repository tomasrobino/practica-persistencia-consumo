package com.example.practica_persistencia_consumo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ElViewModel(application: Application) : AndroidViewModel(application) {

    private val db = ConcesionarioDatabase.getDatabase(application)
    private val cocheDao = db.cocheDao()
    private val motorDao = db.motorDao()
    private val propietarioDao = db.propietarioDao()
    private val cocheMecanicoDao = db.cocheMecanicoDao()

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
}
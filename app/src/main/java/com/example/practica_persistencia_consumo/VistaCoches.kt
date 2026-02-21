package com.example.practica_persistencia_consumo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun CocheConMotorItem(cocheConMotor: CocheConMotor) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🚗 Coche", style = MaterialTheme.typography.titleMedium)
            Text("Color: ${cocheConMotor.coche.color}")
            Text("Marca: ${cocheConMotor.coche.marca}")
            Text("Modelo: ${cocheConMotor.coche.modelo}")

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            if (cocheConMotor.motor != null) {
                Text("🔧 Motor", style = MaterialTheme.typography.titleSmall)
                Text("Marca: ${cocheConMotor.motor.marca}")
                Text("Modelo: ${cocheConMotor.motor.modelo}")
                Text("Cilindrada: ${cocheConMotor.motor.cilindrada} cc")
            } else {
                Text("Sin motor asignado", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@Composable
fun PropietarioConCochesItem(propietarioConCoches: PropietarioConCoches) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("👤 Propietario", style = MaterialTheme.typography.titleMedium)
            Text("Nombre: ${propietarioConCoches.propietario.nombre}")
            Text("Teléfono: ${propietarioConCoches.propietario.telefono}")

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            if (propietarioConCoches.coches.isEmpty()) {
                Text("Sin coches registrados", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("🚗 Coches (${propietarioConCoches.coches.size}):", style = MaterialTheme.typography.titleSmall)
                propietarioConCoches.coches.forEach { coche ->
                    Text("• ${coche.marca} ${coche.modelo} — ${coche.color}")
                }
            }
        }
    }
}

@Composable
fun MecanicoConCochesItem(mecanicoConCoches: MecanicosConCoche) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("🔩 Mecánico", style = MaterialTheme.typography.titleMedium)
            Text("Nombre: ${mecanicoConCoches.mecanico.nombre}")
            Text("Especialidad: ${mecanicoConCoches.mecanico.especialidad}")

            Spacer(Modifier.height(8.dp))
            Divider()
            Spacer(Modifier.height(8.dp))

            if (mecanicoConCoches.coches.isEmpty()) {
                Text("Sin coches asignados", style = MaterialTheme.typography.bodySmall)
            } else {
                Text("🚗 Coches asignados (${mecanicoConCoches.coches.size}):", style = MaterialTheme.typography.titleSmall)
                mecanicoConCoches.coches.forEach { coche ->
                    Text("• ${coche.marca} ${coche.modelo} — ${coche.color}")
                }
            }
        }
    }
}

@Composable
fun ListaCoches(
    cocheDao: CocheDao,
    propietarioDao: PropietarioDao,
    cocheMecanicoDao: CocheMecanicoDao
) {
    val cochesConMotor by cocheDao.getCochesConMotor().collectAsState(initial = emptyList())
    val propietariosConCoches by propietarioDao.getPropietariosConCoches().collectAsState(initial = emptyList())
    val mecanicosConCoches by cocheMecanicoDao.getMecanicosConCoches().collectAsState(initial = emptyList())

    val scope = rememberCoroutineScope()

    Column {
        Button(
            onClick = {
                scope.launch {
                    val propietarioId = propietarioDao.insertPropietario(
                        Propietario(nombre = "Juan García", telefono = "600123456")
                    ).toInt()

                    cocheDao.insertCocheConMotor(
                        Coche(color = "Rojo", modelo = "Serie 3", marca = "BMW", propietarioId = propietarioId),
                        Motor(marca = "BMW", modelo = "B58", cilindrada = 3000, cocheId = 0)
                    )
                }
            },
            modifier = Modifier.padding(8.dp)
        ) {
            Text("Agregar datos de prueba")
        }

        LazyColumn {
            item {
                Text(
                    "🔗 Coches con Motor (1:1)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 4.dp)
                )
            }
            items(cochesConMotor) { cocheConMotor ->
                CocheConMotorItem(cocheConMotor = cocheConMotor)
            }

            item {
                Text(
                    "👤 Propietarios con Coches (1:N)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 4.dp)
                )
            }
            items(propietariosConCoches) { propietarioConCoches ->
                PropietarioConCochesItem(propietarioConCoches = propietarioConCoches)
            }

            item {
                Text(
                    "🔩 Mecánicos con Coches (N:M)",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 4.dp)
                )
            }
            items(mecanicosConCoches) { mecanicoConCoches ->
                MecanicoConCochesItem(mecanicoConCoches = mecanicoConCoches)
            }
        }
    }
}
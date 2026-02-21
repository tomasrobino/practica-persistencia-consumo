package com.example.practica_persistencia_consumo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle


@Composable
fun PantallaPrincipal(vm: ElViewModel) {
    var tabSeleccionado by remember { mutableIntStateOf(0) }
    val tabs = listOf("Coches", "Motores", "Propietarios", "Mecanicos", "Asignaciones", "API Sync")

    Column(modifier = Modifier.fillMaxSize()) {
        ScrollableTabRow(selectedTabIndex = tabSeleccionado) {
            tabs.forEachIndexed { index, titulo ->
                Tab(
                    selected = tabSeleccionado == index,
                    onClick = { tabSeleccionado = index },
                    text = { Text(titulo) }
                )
            }
        }
        when (tabSeleccionado) {
            0 -> TabCoches(vm)
            1 -> TabMotores(vm)
            2 -> TabPropietarios(vm)
            3 -> TabMecanicos(vm)
            4 -> TabAsignaciones(vm)
            5 -> TabApiSync(vm)
        }
    }
}


@Composable
fun TabCoches(vm: ElViewModel) {
    val coches by vm.cochesConMotor.collectAsStateWithLifecycle()
    val propietarios by vm.allPropietarios.collectAsStateWithLifecycle()

    var color by remember { mutableStateOf("") }
    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var propietarioIdSeleccionado by remember { mutableStateOf<Int?>(null) }

    var cocheEditando by remember { mutableStateOf<Coche?>(null) }
    var editColor by remember { mutableStateOf("") }
    var editMarca by remember { mutableStateOf("") }
    var editModelo by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Nuevo coche", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = color, onValueChange = { color = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(4.dp))
            Text("Propietario (opcional)", style = MaterialTheme.typography.labelMedium)
            propietarios.forEach { p ->
                val seleccionado = propietarioIdSeleccionado == p.propietarioId
                OutlinedButton(
                    onClick = { propietarioIdSeleccionado = if (seleccionado) null else p.propietarioId },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    colors = if (seleccionado) ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) else ButtonDefaults.outlinedButtonColors()
                ) { Text("${p.nombre} (id: ${p.propietarioId})") }
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (color.isNotBlank() && marca.isNotBlank() && modelo.isNotBlank()) {
                        vm.insertCoche(color, marca, modelo, propietarioIdSeleccionado)
                        color = ""; marca = ""; modelo = ""; propietarioIdSeleccionado = null
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Insertar coche") }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("Coches registrados", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }

        items(coches, key = { it.coche.id }) { cocheConMotor ->
            val coche = cocheConMotor.coche
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (cocheEditando?.id == coche.id) {
                        OutlinedTextField(value = editColor, onValueChange = { editColor = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editMarca, onValueChange = { editMarca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editModelo, onValueChange = { editModelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
                        Row {
                            Button(onClick = {
                                vm.updateCoche(coche.copy(color = editColor, marca = editMarca, modelo = editModelo))
                                cocheEditando = null
                            }) { Text("Guardar") }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { cocheEditando = null }) { Text("Cancelar") }
                        }
                    } else {
                        Text("${coche.marca} ${coche.modelo}", style = MaterialTheme.typography.titleSmall)
                        Text("Color: ${coche.color}")
                        Text("Propietario id: ${coche.propietarioId ?: "ninguno"}")
                        Text("Motor: ${cocheConMotor.motor?.let { "${it.marca} ${it.modelo}" } ?: "sin motor"}")
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedButton(onClick = {
                                cocheEditando = coche
                                editColor = coche.color
                                editMarca = coche.marca
                                editModelo = coche.modelo
                            }) { Text("Editar") }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { vm.deleteCoche(coche) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TabMotores(vm: ElViewModel) {
    val motores by vm.allMotores.collectAsStateWithLifecycle()
    val coches by vm.allCoches.collectAsStateWithLifecycle()

    var marca by remember { mutableStateOf("") }
    var modelo by remember { mutableStateOf("") }
    var cilindrada by remember { mutableStateOf("") }
    var cocheIdSeleccionado by remember { mutableStateOf<Int?>(null) }

    var motorEditando by remember { mutableStateOf<Motor?>(null) }
    var editMarca by remember { mutableStateOf("") }
    var editModelo by remember { mutableStateOf("") }
    var editCilindrada by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Nuevo motor", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = marca, onValueChange = { marca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = modelo, onValueChange = { modelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = cilindrada, onValueChange = { cilindrada = it },
                label = { Text("Cilindrada (cc)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(4.dp))
            Text("Selecciona coche", style = MaterialTheme.typography.labelMedium)
            coches.forEach { c ->
                val seleccionado = cocheIdSeleccionado == c.id
                OutlinedButton(
                    onClick = { cocheIdSeleccionado = c.id },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                    colors = if (seleccionado) ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ) else ButtonDefaults.outlinedButtonColors()
                ) { Text("${c.marca} ${c.modelo} (id: ${c.id})") }
            }
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    val cc = cilindrada.toIntOrNull()
                    if (marca.isNotBlank() && modelo.isNotBlank() && cc != null && cocheIdSeleccionado != null) {
                        vm.insertMotor(marca, modelo, cc, cocheIdSeleccionado!!)
                        marca = ""; modelo = ""; cilindrada = ""; cocheIdSeleccionado = null
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Insertar motor") }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("Motores registrados", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }

        items(motores, key = { it.id }) { motor ->
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (motorEditando?.id == motor.id) {
                        OutlinedTextField(value = editMarca, onValueChange = { editMarca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editModelo, onValueChange = { editModelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(
                            value = editCilindrada, onValueChange = { editCilindrada = it },
                            label = { Text("Cilindrada") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row {
                            Button(onClick = {
                                val cc = editCilindrada.toIntOrNull() ?: motor.cilindrada
                                vm.updateMotor(motor.copy(marca = editMarca, modelo = editModelo, cilindrada = cc))
                                motorEditando = null
                            }) { Text("Guardar") }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { motorEditando = null }) { Text("Cancelar") }
                        }
                    } else {
                        Text("${motor.marca} ${motor.modelo}", style = MaterialTheme.typography.titleSmall)
                        Text("Cilindrada: ${motor.cilindrada} cc")
                        Text("Coche id: ${motor.cocheId}")
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedButton(onClick = {
                                motorEditando = motor
                                editMarca = motor.marca
                                editModelo = motor.modelo
                                editCilindrada = motor.cilindrada.toString()
                            }) { Text("Editar") }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { vm.deleteMotor(motor) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TabPropietarios(vm: ElViewModel) {
    val propietariosConCoches by vm.propietariosConCoches.collectAsStateWithLifecycle()

    var nombre by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    var propietarioEditando by remember { mutableStateOf<Propietario?>(null) }
    var editNombre by remember { mutableStateOf("") }
    var editTelefono by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Nuevo propietario", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(
                value = telefono, onValueChange = { telefono = it },
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (nombre.isNotBlank() && telefono.isNotBlank()) {
                        vm.insertPropietario(nombre, telefono)
                        nombre = ""; telefono = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Insertar propietario") }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("Propietarios registrados", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }

        items(propietariosConCoches, key = { it.propietario.propietarioId }) { propietarioConCoches ->
            val propietario = propietarioConCoches.propietario
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (propietarioEditando?.propietarioId == propietario.propietarioId) {
                        OutlinedTextField(value = editNombre, onValueChange = { editNombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(
                            value = editTelefono, onValueChange = { editTelefono = it },
                            label = { Text("Teléfono") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row {
                            Button(onClick = {
                                vm.updatePropietario(propietario.copy(nombre = editNombre, telefono = editTelefono))
                                propietarioEditando = null
                            }) { Text("Guardar") }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { propietarioEditando = null }) { Text("Cancelar") }
                        }
                    } else {
                        Text(propietario.nombre, style = MaterialTheme.typography.titleSmall)
                        Text("Tel: ${propietario.telefono}")
                        if (propietarioConCoches.coches.isEmpty()) {
                            Text("Sin coches", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text("Coches: ${propietarioConCoches.coches.joinToString { "${it.marca} ${it.modelo}" }}")
                        }
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedButton(onClick = {
                                propietarioEditando = propietario
                                editNombre = propietario.nombre
                                editTelefono = propietario.telefono
                            }) { Text("Editar") }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { vm.deletePropietario(propietario) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TabMecanicos(vm: ElViewModel) {
    val mecanicosConCoches by vm.mecanicosConCoches.collectAsStateWithLifecycle()

    var nombre by remember { mutableStateOf("") }
    var especialidad by remember { mutableStateOf("") }

    var mecanicoEditando by remember { mutableStateOf<Mecanico?>(null) }
    var editNombre by remember { mutableStateOf("") }
    var editEspecialidad by remember { mutableStateOf("") }

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Nuevo mecanico", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = nombre, onValueChange = { nombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = especialidad, onValueChange = { especialidad = it }, label = { Text("Especialidad") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (nombre.isNotBlank() && especialidad.isNotBlank()) {
                        vm.insertMecanico(nombre, especialidad)
                        nombre = ""; especialidad = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Insertar mecanico") }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
            Text("Mecanicos registrados", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
        }

        items(mecanicosConCoches, key = { it.mecanico.id }) { mecanicoConCoches ->
            val mecanico = mecanicoConCoches.mecanico
            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (mecanicoEditando?.id == mecanico.id) {
                        OutlinedTextField(value = editNombre, onValueChange = { editNombre = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editEspecialidad, onValueChange = { editEspecialidad = it }, label = { Text("Especialidad") }, modifier = Modifier.fillMaxWidth())
                        Row {
                            Button(onClick = {
                                vm.updateMecanico(mecanico.copy(nombre = editNombre, especialidad = editEspecialidad))
                                mecanicoEditando = null
                            }) { Text("Guardar") }
                            Spacer(Modifier.width(8.dp))
                            OutlinedButton(onClick = { mecanicoEditando = null }) { Text("Cancelar") }
                        }
                    } else {
                        Text(mecanico.nombre, style = MaterialTheme.typography.titleSmall)
                        Text("Especialidad: ${mecanico.especialidad}")
                        if (mecanicoConCoches.coches.isEmpty()) {
                            Text("Sin coches asignados", style = MaterialTheme.typography.bodySmall)
                        } else {
                            Text("Coches: ${mecanicoConCoches.coches.joinToString { "${it.marca} ${it.modelo}" }}")
                        }
                        Row(modifier = Modifier.padding(top = 8.dp)) {
                            OutlinedButton(onClick = {
                                mecanicoEditando = mecanico
                                editNombre = mecanico.nombre
                                editEspecialidad = mecanico.especialidad
                            }) { Text("Editar") }
                            Spacer(Modifier.width(8.dp))
                            Button(
                                onClick = { vm.deleteMecanico(mecanico) },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) { Text("Eliminar") }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TabAsignaciones(vm: ElViewModel) {
    val cochesConMecanicos by vm.cochesConMecanicos.collectAsStateWithLifecycle()
    val mecanicos by vm.allMecanicos.collectAsStateWithLifecycle()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Text("Asignaciones coche - mecanico (N:M)", style = MaterialTheme.typography.titleMedium)
            Text("Selecciona un mecanico para asignarlo o desasignarlo de cada coche.", style = MaterialTheme.typography.bodySmall)
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        items(cochesConMecanicos, key = { it.coche.id }) { cocheConMecanicos ->
            val coche = cocheConMecanicos.coche
            val mecanicoIdsAsignados = cocheConMecanicos.mecanicos.map { it.id }.toSet()

            Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${coche.marca} ${coche.modelo}", style = MaterialTheme.typography.titleSmall)
                    Text("Color: ${coche.color}")
                    Spacer(Modifier.height(8.dp))
                    Text("Mecanicos disponibles:", style = MaterialTheme.typography.labelMedium)
                    mecanicos.forEach { mecanico ->
                        val asignado = mecanico.id in mecanicoIdsAsignados
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "${mecanico.nombre} - ${mecanico.especialidad}",
                                modifier = Modifier.weight(1f).padding(end = 8.dp),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (asignado) {
                                Button(
                                    onClick = { vm.deleteCocheMecanicoCrossRef(coche.id, mecanico.id) },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                                ) { Text("Desasignar") }
                            } else {
                                Button(
                                    onClick = { vm.insertCocheMecanicoCrossRef(coche.id, mecanico.id) }
                                ) { Text("Asignar") }
                            }
                        }
                    }
                }
            }
        }
    }
}


// ─────────────────────────── NUEVA TAB: API SYNC ──────────────────────────────

@Composable
fun TabApiSync(vm: ElViewModel) {
    val syncStatus by vm.syncStatus.collectAsStateWithLifecycle()
    val coches by vm.allCoches.collectAsStateWithLifecycle()

    // Campos para POST de un coche nuevo vía API
    var postColor by remember { mutableStateOf("") }
    var postMarca by remember { mutableStateOf("") }
    var postModelo by remember { mutableStateOf("") }

    // Selección de coche para PUT/DELETE
    var cocheSeleccionado by remember { mutableStateOf<Coche?>(null) }
    var editColor by remember { mutableStateOf("") }
    var editMarca by remember { mutableStateOf("") }
    var editModelo by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ── Banner de estado ──────────────────────────────────────────────────
        item {
            when (val s = syncStatus) {
                is SyncStatus.Idle -> {}
                is SyncStatus.Loading -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(end = 12.dp))
                            Text(s.message)
                        }
                    }
                }
                is SyncStatus.Success -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFD4EDDA))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("✅ ${s.message}", modifier = Modifier.weight(1f))
                            OutlinedButton(onClick = { vm.clearSyncStatus() }) { Text("OK") }
                        }
                    }
                }
                is SyncStatus.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8D7DA))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("❌ ${s.message}", modifier = Modifier.weight(1f))
                            OutlinedButton(onClick = { vm.clearSyncStatus() }) { Text("OK") }
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        // ── GET: Sincronizar todo ─────────────────────────────────────────────
        item {
            Text("GET — Sincronizar con la API", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Button(onClick = { vm.syncAll() }, modifier = Modifier.fillMaxWidth()) {
                Text("🔄  Sync ALL (propietarios + coches + motores + mecánicos)")
            }
            Spacer(Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { vm.fetchCoches() }, modifier = Modifier.weight(1f)) { Text("GET /coches") }
                OutlinedButton(onClick = { vm.fetchMotores() }, modifier = Modifier.weight(1f)) { Text("GET /motores") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { vm.fetchPropietarios() }, modifier = Modifier.weight(1f)) { Text("GET /propietarios") }
                OutlinedButton(onClick = { vm.fetchMecanicos() }, modifier = Modifier.weight(1f)) { Text("GET /mecánicos") }
            }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        // ── POST: Crear coche ─────────────────────────────────────────────────
        item {
            Text("POST /coches — Crear coche en la API", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(value = postColor, onValueChange = { postColor = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = postMarca, onValueChange = { postMarca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = postModelo, onValueChange = { postModelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    if (postColor.isNotBlank() && postMarca.isNotBlank() && postModelo.isNotBlank()) {
                        vm.apiCreateCoche(postColor, postMarca, postModelo, null)
                        postColor = ""; postMarca = ""; postModelo = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("POST → Crear coche") }
            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
        }

        // ── PUT / DELETE: Selecciona un coche ─────────────────────────────────
        item {
            Text("PUT / DELETE — Selecciona un coche", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(4.dp))
        }

        items(coches, key = { it.id }) { coche ->
            val seleccionado = cocheSeleccionado?.id == coche.id
            Card(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                elevation = CardDefaults.cardElevation(if (seleccionado) 6.dp else 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (seleccionado) MaterialTheme.colorScheme.primaryContainer
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("${coche.marca} ${coche.modelo} (id: ${coche.id})", style = MaterialTheme.typography.titleSmall)
                    Text("Color: ${coche.color}")

                    if (seleccionado) {
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(value = editColor, onValueChange = { editColor = it }, label = { Text("Color") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editMarca, onValueChange = { editMarca = it }, label = { Text("Marca") }, modifier = Modifier.fillMaxWidth())
                        OutlinedTextField(value = editModelo, onValueChange = { editModelo = it }, label = { Text("Modelo") }, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(8.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    vm.apiUpdateCoche(coche.copy(color = editColor, marca = editMarca, modelo = editModelo))
                                    cocheSeleccionado = null
                                },
                                modifier = Modifier.weight(1f)
                            ) { Text("PUT → Actualizar") }
                            Button(
                                onClick = {
                                    vm.apiDeleteCoche(coche)
                                    cocheSeleccionado = null
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) { Text("DELETE") }
                        }
                        OutlinedButton(
                            onClick = { cocheSeleccionado = null },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Cancelar") }
                    } else {
                        Spacer(Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = {
                                cocheSeleccionado = coche
                                editColor = coche.color
                                editMarca = coche.marca
                                editModelo = coche.modelo
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) { Text("Seleccionar para editar / borrar") }
                    }
                }
            }
        }
    }
}
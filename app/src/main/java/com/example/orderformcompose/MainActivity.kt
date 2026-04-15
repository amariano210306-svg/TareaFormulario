package com.example.orderformcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    var isFormVisible by remember { mutableStateOf(false) }

    AnimatedContent(
        targetState = isFormVisible,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "nav_anim"
    ) { showForm ->
        if (showForm) {
            OrderFormScreen(onBack = { isFormVisible = false })
        } else {
            StartScreen(onStart = { isFormVisible = true })
        }
    }
}

@Composable
fun StartScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Text(
            text = "Jaguar Food & Skip",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(30.dp))
        Button(
            onClick = onStart,
            modifier = Modifier.height(55.dp).width(250.dp)
        ) {
            Text("RESPONDER FORMULARIO")
        }
    }
}
@Composable
fun OrderFormScreen(onBack: () -> Unit) {
    var nombreCliente by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var producto by remember { mutableStateOf("") }
    var cantidad by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    var procesando by remember { mutableStateOf(false) }
    var avisoFinal by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Lógica de validación
    val nombreValido = nombreCliente.trim().length >= 3
    val telefonoValido = telefono.length >= 8 && telefono.all { it.isDigit() }
    val direccionValida = direccion.isNotBlank()
    val cantidadValida = (cantidad.toIntOrNull() ?: 0) > 0
    val productoValido = producto.isNotBlank()

    val sePuedeEnviar = nombreValido && telefonoValido && direccionValida && cantidadValida && productoValido

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
            }
            Text(
                "Datos del Pedido",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        OutlinedTextField(
            value = nombreCliente,
            onValueChange = { nombreCliente = it },
            label = { Text("Nombre") },
            leadingIcon = { Icon(Icons.Default.Person, null) },
            isError = nombreCliente.isNotEmpty() && !nombreValido,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = telefono,
            onValueChange = { telefono = it },
            label = { Text("Teléfono") },
            leadingIcon = { Icon(Icons.Default.Phone, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = telefono.isNotEmpty() && !telefonoValido,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = direccion,
            onValueChange = { direccion = it },
            label = { Text("Dirección de entrega") },
            leadingIcon = { Icon(Icons.Default.LocationOn, null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = producto,
            onValueChange = { producto = it },
            label = { Text("¿Qué producto desea?") },
            leadingIcon = { Icon(Icons.Default.Info, null) },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = cantidad,
            onValueChange = { cantidad = it },
            label = { Text("Cantidad") },
            leadingIcon = { Icon(Icons.Default.Add, null) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = notas,
            onValueChange = { notas = it },
            label = { Text("Notas o extras (opcional)") },
            leadingIcon = { Icon(Icons.Default.Edit, null) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(25.dp))

        if (procesando) {
            CircularProgressIndicator(modifier = Modifier.padding(bottom = 15.dp))
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            Button(
                onClick = {
                    coroutineScope.launch {
                        procesando = true
                        avisoFinal = "Procesando su solicitud..."
                        delay(2500)
                        procesando = false
                        avisoFinal = "¡Pedido confirmado con éxito! ✅"
                    }
                },
                modifier = Modifier.weight(1f),
                enabled = sePuedeEnviar && !procesando
            ) {
                Text("ENVIAR")
            }

            OutlinedButton(
                onClick = {
                    nombreCliente = ""; telefono = ""; direccion = ""; producto = ""
                    cantidad = ""; notas = ""; avisoFinal = ""
                },
                modifier = Modifier.weight(1f),
                enabled = !procesando
            ) {
                Text("LIMPIAR")
            }
        }

        if (avisoFinal.isNotEmpty()) {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (avisoFinal.contains("éxito")) Color(0xFFE8F5E9) else Color(0xFFF1F1F1)
                )
            ) {
                Text(
                    text = avisoFinal,
                    modifier = Modifier.padding(16.dp),
                    color = if (avisoFinal.contains("éxito")) Color(0xFF2E7D32) else Color.Black,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
package com.example.orderformcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
                    OrderScreen()
                }
            }
        }
    }
}

@Composable
fun OrderScreen() {

    // datos del formulario
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var product by remember { mutableStateOf("") }
    var qty by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    // estados de UI
    var loading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    // validaciones básicas
    val validName = name.length > 2
    val validPhone = phone.length >= 8 && phone.all { it.isDigit() }
    val validAddress = address.isNotBlank()
    val validQty = (qty.toIntOrNull() ?: 0) > 0
    val validProduct = product.isNotBlank()

    val formOk = validName && validPhone && validAddress && validQty && validProduct

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Formulario de Pedido")

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            isError = name.isNotEmpty() && !validName,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("Teléfono") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            isError = phone.isNotEmpty() && !validPhone,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            label = { Text("Dirección") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = product,
            onValueChange = { product = it },
            label = { Text("Producto") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = qty,
            onValueChange = { qty = it },
            label = { Text("Cantidad") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            label = { Text("Notas") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (loading) {
            CircularProgressIndicator()
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {

            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        message = "Enviando..."
                        delay(2000)
                        loading = false
                        message = "Pedido enviado"
                    }
                },
                enabled = formOk && !loading
            ) {
                Text("Enviar")
            }

            OutlinedButton(
                onClick = {
                    name = ""
                    phone = ""
                    address = ""
                    product = ""
                    qty = ""
                    notes = ""
                    message = ""
                },
                enabled = !loading
            ) {
                Text("Limpiar")
            }
        }

        if (message.isNotEmpty()) {
            Spacer(modifier = Modifier.height(15.dp))

            Text(
                text = message,
                color = if (message.contains("enviado")) Color(0xFF2E7D32) else Color.Gray
            )
        }
    }
}
package com.example.viajai_maf

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun EditarViagemScreen(
    origemInicial: String = "",
    destinoInicial: String = "",
    dataInicial: String = "",
    onVoltar: () -> Unit,
    onSalvar: (String, String, String, Double) -> Unit
) {
    var origem by remember { mutableStateOf(origemInicial) }
    var destino by remember { mutableStateOf(destinoInicial) }
    var data by remember { mutableStateOf(dataInicial) }
    var orcamento by remember { mutableStateOf("") }
    val valor = orcamento.replace(',', '.').toDoubleOrNull()
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFFE87735), Color(0xFF8B261D)))).verticalScroll(rememberScrollState()).padding(18.dp)) {
        Row { IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White) }; Text("Planejar viagem", color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold) }
        Spacer(Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Detalhes da viagem", style = MaterialTheme.typography.titleLarge, color = Color(0xFF8B261D), fontWeight = FontWeight.Bold)
                OutlinedTextField(origem, { origem = it }, label = { Text("Origem") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(destino, { destino = it }, label = { Text("Destino") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(data, { data = it }, label = { Text("Data da viagem") }, placeholder = { Text("DD/MM/AAAA") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                OutlinedTextField(orcamento, { orcamento = it }, label = { Text("Orçamento (R$)") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                Button(onClick = { onSalvar(origem.trim(), destino.trim(), data.trim(), valor ?: 0.0) }, enabled = origem.isNotBlank() && destino.isNotBlank() && data.isNotBlank() && valor != null && valor > 0, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE87735))) { Text("Salvar viagem") }
            }
        }
    }
}

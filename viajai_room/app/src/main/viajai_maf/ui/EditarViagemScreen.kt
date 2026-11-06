package com.example.viajai_maf.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private val laranjaFormulario = Color(0xFFEB813D)
private val vermelhoFormulario = Color(0xFF8E291F)
private val roxoFormulario = Color(0xFF7052A5)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarViagemScreen(
    tituloInicial: String = "",
    origemInicial: String = "",
    destinoInicial: String = "",
    dataInicial: String = "",
    orcamentoInicial: Double? = null,
    editando: Boolean = false,
    onVoltar: () -> Unit,
    onSalvar: (String, String, String, Double, String) -> Unit
) {
    var titulo by remember { mutableStateOf(tituloInicial) }
    var origem by remember { mutableStateOf(origemInicial) }
    var destino by remember { mutableStateOf(destinoInicial) }
    var data by remember { mutableStateOf(normalizarDataViagem(dataInicial)) }
    var orcamento by remember { mutableStateOf(orcamentoInicial?.takeIf { it > 0 }?.toString()?.replace('.', ',') ?: "") }
    var mostrarCalendario by remember { mutableStateOf(false) }
    // Tocar em qualquer parte do campo de data também abre o calendário.
    val campoDataInteracao = remember { MutableInteractionSource() }
    LaunchedEffect(campoDataInteracao) {
        campoDataInteracao.interactions.collect { if (it is PressInteraction.Release) mostrarCalendario = true }
    }
    val valor = orcamento.replace(".", "").replace(',', '.').toDoubleOrNull()
    val formatoData = remember { SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR")).apply { isLenient = false; timeZone = TimeZone.getTimeZone("UTC") } }
    val dataValida = remember(data) { try { data.length == 10 && formatoData.parse(data) != null } catch (_: Exception) { false } }
    val podeSalvar = origem.isNotBlank() && destino.isNotBlank() && dataValida && valor != null && valor > 0

    Column(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(laranjaFormulario, vermelhoFormulario)))
            .verticalScroll(rememberScrollState()).padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White) }
            Text(if (editando) "Editar viagem" else "Planejar viagem", color = Color.White,
                style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(16.dp))
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.fillMaxWidth().padding(17.dp), verticalArrangement = Arrangement.spacedBy(13.dp)) {
                Text("Detalhes da viagem", style = MaterialTheme.typography.titleLarge, color = vermelhoFormulario)
                OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Título da viagem (opcional)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(5.dp))
                OutlinedTextField(value = origem, onValueChange = { origem = it }, label = { Text("Origem") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(5.dp))
                OutlinedTextField(value = destino, onValueChange = { destino = it }, label = { Text("Destino") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(5.dp))
                OutlinedTextField(value = data, onValueChange = {}, readOnly = true,
                    label = { Text("Data da viagem") }, placeholder = { Text("DD/MM/AAAA") },
                    trailingIcon = { IconButton(onClick = { mostrarCalendario = true }) { Icon(Icons.Default.CalendarMonth, "Abrir calendário") } },
                    interactionSource = campoDataInteracao,
                    modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(5.dp))
                OutlinedTextField(value = orcamento, onValueChange = { orcamento = it.filter { c -> c.isDigit() || c == ',' || c == '.' } }, label = { Text("Orçamento (R$)") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(5.dp))
                Button(onClick = { onSalvar(origem.trim(), destino.trim(), data.trim(), valor ?: 0.0, titulo.trim()) }, enabled = podeSalvar,
                    modifier = Modifier.fillMaxWidth().height(46.dp), shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = roxoFormulario, disabledContainerColor = Color(0xFFE3E3E3))) {
                    Text(if (editando) "Salvar alterações" else "Salvar viagem")
                }
            }
        }
    }

    if (mostrarCalendario) {
        val millisAtual = try { formatoData.parse(data)?.time } catch (_: Exception) { null }
        val hoje = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0) }.timeInMillis
        val estadoCalendario = rememberDatePickerState(initialSelectedDateMillis = millisAtual ?: hoje)
        DatePickerDialog(onDismissRequest = { mostrarCalendario = false },
            confirmButton = { TextButton(onClick = { estadoCalendario.selectedDateMillis?.let { data = formatoData.format(it) }; mostrarCalendario = false }) { Text("Confirmar") } },
            dismissButton = { TextButton(onClick = { mostrarCalendario = false }) { Text("Cancelar") } }) {
            DatePicker(state = estadoCalendario)
        }
    }
}

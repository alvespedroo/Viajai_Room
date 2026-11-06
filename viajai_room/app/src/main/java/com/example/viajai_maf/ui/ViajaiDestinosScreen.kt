package com.example.viajai_maf

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val laranjaDestino = Color(0xFFE87735)
private val vermelhoDestino = Color(0xFF8B261D)

@Composable
fun ViajaiDestinosScreen(
    onNavigateToChecklist: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToDestinos: () -> Unit,
    onNavigateToGastos: () -> Unit = {},
    dados: ViajaiDados? = null,
    onAvaliacoes: (Int) -> Unit = {}
) {
    val viagem = dados?.viagemAtual
    var busca by remember { mutableStateOf("") }
    var avaliacoes by remember { mutableStateOf<List<AvaliacaoEntity>>(emptyList()) }
    LaunchedEffect(dados, dados?.avaliacoesVersao) { dados?.carregarTodasAvaliacoes { avaliacoes = it } }
    val hospedagens = listOf(
        Triple("Casa São Paulo", R.drawable.pousada_sao_paulo, 2),
        Triple("Pousada Maraka", R.drawable.pousada_maraka, 1),
        Triple("Pousada Zamberlan", R.drawable.pousada_zamberlan, 3)
    )
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(laranjaDestino, vermelhoDestino))).verticalScroll(rememberScrollState()).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNavigateToHome) { Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White) }
            Text("Destinos", color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        }
        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.fillMaxWidth().padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text(if (viagem == null) "Sua próxima aventura começa aqui" else "Sua viagem", color = vermelhoDestino, fontWeight = FontWeight.Bold)
                Text(if (viagem == null) "Escolha o destino e planeje sua viagem." else "${viagem.origem}  →  ${viagem.destino}", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                if (viagem != null) {
                    Text("Data: ${viagem.data}")
                    Text("Orçamento: R$ %.2f".format(viagem.orcamento), color = vermelhoDestino)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = onNavigateToChecklist, modifier = Modifier.weight(1f)) { Text("Checklist") }
                        OutlinedButton(onClick = onNavigateToGastos, modifier = Modifier.weight(1f)) { Icon(Icons.Default.ReceiptLong, null); Spacer(Modifier.width(4.dp)); Text("Gastos") }
                    }
                }
                Button(onClick = onNavigateToDestinos, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = laranjaDestino)) {
                    Icon(Icons.Default.Add, null); Spacer(Modifier.width(5.dp)); Text("Planejar nova viagem")
                }
            }
        }
        Text("Hospedagens para explorar", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        OutlinedTextField(value = busca, onValueChange = { busca = it }, placeholder = { Text("Buscar hospedagem por nome") }, leadingIcon = { Icon(Icons.Default.Search, null) }, modifier = Modifier.fillMaxWidth(), singleLine = true, colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
        hospedagens.filter { it.first.contains(busca, ignoreCase = true) }.forEach { lugar ->
            Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column {
                    Image(painterResource(lugar.second), contentDescription = lugar.first, modifier = Modifier.fillMaxWidth().height(150.dp), contentScale = ContentScale.Crop)
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                        val reviewsLugar = avaliacoes.filter { it.lugarId == lugar.third }
                        val mediaLugar = reviewsLugar.map { it.nota }.average()
                        Text(lugar.first, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(if (reviewsLugar.isEmpty()) "Sem avaliações • consulte valores atualizados" else "${String.format(java.util.Locale("pt", "BR"), "%.1f", mediaLugar)} • ${reviewsLugar.size} ${if (reviewsLugar.size == 1) "avaliação" else "avaliações"}", color = vermelhoDestino, fontSize = 13.sp)
                        TextButton(onClick = {
                            onAvaliacoes(lugar.third)
                        }) { Text("Ver avaliações e comentários") }
                    }
                }
            }
        }
        Spacer(Modifier.height(20.dp))
    }
}

package com.example.viajai_maf

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.NumberFormat
import java.util.Locale

private val vermelhoHome = Color(0xFF922D24)
private val laranjaHome = Color(0xFFEB8448)

@Composable
fun ViajaiHomeScreen(
    onNavigateToChecklist: () -> Unit,
    onNavigateToDestinos: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToBagagem: () -> Unit = {},
    onNavigateToGastos: () -> Unit = {},
    onContinue: (Int) -> Unit = {},
    onExplore: () -> Unit = {},
    dados: ViajaiDados = ViajaiDados()
) {
    val viagem = dados.viagemAtual
    val totalGasto = viagem?.let { dados.gastosDaViagem(it.id) } ?: 0.0
    val valor = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(totalGasto)
    val preparados = dados.bagagens.count { it.preparado }
    val totalBagagem = dados.bagagens.size
    val score = dados.score
    val corScore = when {
        score >= 100 -> Color(0xFF2E8B57)
        score >= 50 -> Color(0xFFF2B705)
        else -> Color(0xFFD94343)
    }
    Column(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(laranjaHome, vermelhoHome)))
            .verticalScroll(rememberScrollState()).padding(horizontal = 18.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) {
            Row(Modifier.fillMaxWidth().padding(14.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("Viajaí", color = vermelhoHome, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                Icon(Icons.Outlined.NotificationsNone, "Notificações", tint = vermelhoHome)
            }
        }
        Text("Sua próxima viagem", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(viagem?.destino ?: "Planejar viagem", color = vermelhoHome, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                if (viagem != null) {
                    Text("${viagem.origem}  →  ${viagem.destino}", fontSize = 13.sp)
                    Text("Data: ${viagem.data}", fontSize = 13.sp)
                }
                if (viagem != null) Text("Preparação: $score%", color = corScore, fontWeight = FontWeight.Medium)
                if (viagem != null) LinearProgressIndicator(progress = { score.coerceIn(0, 100) / 100f }, modifier = Modifier.fillMaxWidth().height(8.dp), color = corScore, trackColor = Color(0xFFF4E4DD))
                Button(onClick = { if (viagem == null) onNavigateToDestinos() else onContinue(viagem.id) }, colors = ButtonDefaults.buttonColors(containerColor = vermelhoHome), modifier = Modifier.fillMaxWidth()) { Text(if (viagem == null) "+ Planejar viagem" else "Continuar viagem") }
            }
        }
        if (viagem != null) {
        Text("Planejamento", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeTile("Checklist", "${dados.preparo.count { it.viagemId == viagem?.id && it.marcado }}/${dados.preparo.count { it.viagemId == viagem?.id }} itens", Icons.Outlined.Checklist, Modifier.weight(1f), onNavigateToChecklist)
            HomeTile("Bagagem", "$preparados/$totalBagagem itens", Icons.Outlined.Luggage, Modifier.weight(1f), onNavigateToBagagem)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            HomeTile("Total gasto", valor, Icons.Outlined.AccountBalanceWallet, Modifier.weight(1f), onNavigateToGastos)
            HomeTile("Destinos", "Ver hospedagens", Icons.Outlined.Place, Modifier.weight(1f), onExplore)
        }
        Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun HomeTile(titulo: String, valor: String, icone: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier.height(125.dp), shape = RoundedCornerShape(14.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.fillMaxSize().padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
            Icon(icone, contentDescription = null, tint = vermelhoHome, modifier = Modifier.size(25.dp))
            Spacer(Modifier.height(6.dp))
            Text(titulo, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
            Text(valor, fontSize = 11.sp, color = vermelhoHome, maxLines = 1)
        }
    }
}

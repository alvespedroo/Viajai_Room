package com.example.viajai_maf

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

private val laranjaReview = Color(0xFFE87735)
private val vermelhoReview = Color(0xFF8B261D)
private val ouro = Color(0xFFFFA900)

@Composable
fun ComentariosReviewScreen(pousadaId: Int, onVoltar: () -> Unit, dados: ViajaiDados) {
    val comentarios = remember(pousadaId) { mutableStateListOf<AvaliacaoEntity>() }
    var autor by remember { mutableStateOf("") }
    var texto by remember { mutableStateOf("") }
    var nota by remember { mutableFloatStateOf(5f) }
    var avaliacaoEditando by remember { mutableStateOf<AvaliacaoEntity?>(null) }
    var textoEdicao by remember { mutableStateOf("") }
    var notaEdicao by remember { mutableFloatStateOf(5f) }
    var avaliacaoExcluir by remember { mutableStateOf<AvaliacaoEntity?>(null) }
    var carregando by remember(pousadaId) { mutableStateOf(true) }
    val lugar = lugaresExplorar.firstOrNull { it.id == pousadaId }
    val tituloLugar = lugar?.nome ?: when (pousadaId) {
        1 -> "Pousada Maraka"
        2 -> "Casa São Paulo"
        3 -> "Pousada Zamberlan"
        else -> "Lugar #$pousadaId"
    }
    val media = comentarios.map { it.nota }.average().takeIf { !it.isNaN() } ?: 0.0

    LaunchedEffect(Unit) { dados.carregarNomePerfil { nome -> if (autor.isBlank() && nome.isNotBlank()) autor = nome } }

    LaunchedEffect(pousadaId) {
        carregando = true
        dados.carregarAvaliacoes(pousadaId) { itens ->
            comentarios.clear()
            comentarios.addAll(itens)
            carregando = false
        }
    }

    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(laranjaReview, vermelhoReview)))) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVoltar) { Icon(Icons.Default.ArrowBack, "Voltar", tint = Color.White) }
            Column(Modifier.weight(1f)) {
                Text("Avaliações", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(tituloLugar, color = Color.White.copy(alpha = 0.9f), style = MaterialTheme.typography.bodySmall)
            }
        }
        LazyColumn(Modifier.fillMaxSize().padding(horizontal = 14.dp), verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 28.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, null, tint = ouro, modifier = Modifier.size(38.dp))
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                if (comentarios.isEmpty()) "Ainda sem avaliações" else "Média: ${String.format(Locale.US, "%.1f", media)} / 5,0",
                                color = vermelhoReview, fontWeight = FontWeight.Bold
                            )
                            Text("${comentarios.size} avaliação(ões) salvas neste aparelho", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(16.dp)) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Deixe sua avaliação", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("Sua nota: ${String.format(Locale.US, "%.1f", nota)} / 5,0", color = vermelhoReview, fontWeight = FontWeight.SemiBold)
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            (1..5).forEach { estrela ->
                                val icone = when {
                                    nota >= estrela -> Icons.Default.Star
                                    nota >= estrela - 0.5f -> Icons.Default.StarHalf
                                    else -> Icons.Default.StarBorder
                                }
                                Box(Modifier.size(width = 42.dp, height = 48.dp), contentAlignment = Alignment.Center) {
                                    Icon(icone, contentDescription = "$estrela estrelas", tint = ouro, modifier = Modifier.size(34.dp))
                                    Row(Modifier.matchParentSize()) {
                                        Box(Modifier.weight(1f).fillMaxHeight().clickable { nota = estrela - 0.5f })
                                        Box(Modifier.weight(1f).fillMaxHeight().clickable { nota = estrela.toFloat() })
                                    }
                                }
                            }
                        }
                        OutlinedTextField(value = autor, onValueChange = { autor = it }, label = { Text("Seu nome") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                        OutlinedTextField(value = texto, onValueChange = { texto = it }, label = { Text("Conte sua experiência") }, modifier = Modifier.fillMaxWidth(), minLines = 3)
                        Button(
                            onClick = {
                                val nomeFinal = autor.trim()
                                val textoFinal = texto.trim()
                                if (nomeFinal.isNotBlank() && textoFinal.isNotBlank()) {
                                    dados.adicionarAvaliacao(pousadaId, nomeFinal, textoFinal, nota.toDouble()) { nova ->
                                        comentarios.add(0, nova)
                                        autor = ""
                                        texto = ""
                                        nota = 5f
                                    }
                                }
                            },
                            enabled = autor.isNotBlank() && texto.isNotBlank() && !carregando,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = laranjaReview)
                        ) { Icon(Icons.Default.Send, null); Spacer(Modifier.width(7.dp)); Text("Publicar avaliação") }
                    }
                }
            }
            if (carregando) {
                item { Box(Modifier.fillMaxWidth().padding(20.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = Color.White) } }
            } else if (comentarios.isEmpty()) {
                item {
                    Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                        Column(Modifier.fillMaxWidth().padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.ChatBubbleOutline, null, tint = laranjaReview, modifier = Modifier.size(34.dp))
                            Spacer(Modifier.height(8.dp))
                            Text("Seja a primeira pessoa a avaliar!", fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
            items(comentarios, key = { "review_${it.id}" }) { comentario ->
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(14.dp)) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = Color(0xFFFFE5D9), modifier = Modifier.size(42.dp)) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, "Foto de perfil", tint = vermelhoReview) }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(comentario.autor, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    repeat(5) { index ->
                                        val rating = comentario.nota
                                        val icon = when {
                                            rating >= index + 1 -> Icons.Default.Star
                                            rating >= index + 0.5 -> Icons.Default.StarHalf
                                            else -> Icons.Default.StarBorder
                                        }
                                        Icon(icon, null, tint = ouro, modifier = Modifier.size(15.dp))
                                    }
                                    Spacer(Modifier.width(5.dp))
                                    Text(String.format(Locale.US, "%.1f", comentario.nota), color = vermelhoReview, fontSize = 12.sp)
                                }
                            }
                        }
                        Text(comentario.texto)
                        if (comentario.isMinha) {
                            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterEnd) {
                                MenuAcoesDados(
                                    onEditar = { avaliacaoEditando = comentario; textoEdicao = comentario.texto; notaEdicao = comentario.nota.toFloat() },
                                    onExcluir = { avaliacaoExcluir = comentario }
                                )
                            }
                        }
                        HorizontalDivider(color = Color(0xFFFFE5D9))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Essa avaliação foi útil?", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.weight(1f))
                            TextButton(onClick = {
                                dados.alternarCurtidaAvaliacao(comentario) { atualizado ->
                                    val i = comentarios.indexOfFirst { it.id == atualizado.id }
                                    if (i >= 0) comentarios[i] = atualizado
                                }
                            }) {
                                Icon(if (comentario.curtiu) Icons.Default.Favorite else Icons.Default.FavoriteBorder, "Curtir", tint = if (comentario.curtiu) vermelhoReview else Color.Gray)
                                Spacer(Modifier.width(5.dp))
                                Text("${comentario.curtidas}")
                            }
                        }
                    }
                }
            }
        }
    }

    avaliacaoEditando?.let { item ->
        AlertDialog(
            onDismissRequest = { avaliacaoEditando = null },
            title = { Text("Editar avaliação") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Sua nota: ${String.format(Locale.US, "%.1f", notaEdicao)} / 5,0")
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        (1..5).forEach { estrela ->
                            val icone = when {
                                notaEdicao >= estrela -> Icons.Default.Star
                                notaEdicao >= estrela - 0.5f -> Icons.Default.StarHalf
                                else -> Icons.Default.StarBorder
                            }
                            Box(Modifier.size(width = 42.dp, height = 48.dp), contentAlignment = Alignment.Center) {
                                Icon(icone, contentDescription = "$estrela estrelas", tint = ouro, modifier = Modifier.size(34.dp))
                                Row(Modifier.matchParentSize()) {
                                    Box(Modifier.weight(1f).fillMaxHeight().clickable { notaEdicao = estrela - 0.5f })
                                    Box(Modifier.weight(1f).fillMaxHeight().clickable { notaEdicao = estrela.toFloat() })
                                }
                            }
                        }
                    }
                    OutlinedTextField(value = textoEdicao, onValueChange = { textoEdicao = it },
                        label = { Text("Comentário") }, minLines = 3)
                }
            },
            confirmButton = {
                TextButton(enabled = textoEdicao.isNotBlank(), onClick = {
                    dados.editarAvaliacao(item, textoEdicao, notaEdicao.toDouble()) { atualizada ->
                        val index = comentarios.indexOfFirst { it.id == atualizada.id }
                        if (index >= 0) comentarios[index] = atualizada
                        avaliacaoEditando = null
                    }
                }) { Text("Salvar") }
            },
            dismissButton = { TextButton(onClick = { avaliacaoEditando = null }) { Text("Cancelar") } }
        )
    }

    avaliacaoExcluir?.let { item ->
        AlertDialog(
            onDismissRequest = { avaliacaoExcluir = null },
            title = { Text("Excluir avaliação?") },
            text = { Text("Essa avaliação será removida permanentemente.") },
            confirmButton = {
                TextButton(onClick = {
                    dados.excluirAvaliacao(item) {
                        comentarios.removeAll { it.id == item.id }
                        avaliacaoExcluir = null
                    }
                }) { Text("Excluir", color = vermelhoReview) }
            },
            dismissButton = { TextButton(onClick = { avaliacaoExcluir = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
fun MenuAcoesDados(onEditar: () -> Unit, onExcluir: () -> Unit) {
    var expandido by remember { mutableStateOf(false) }
    Box {
        IconButton(onClick = { expandido = true }) {
            Icon(Icons.Default.MoreVert, contentDescription = "Opções", tint = Color.Gray)
        }
        DropdownMenu(expanded = expandido, onDismissRequest = { expandido = false }) {
            DropdownMenuItem(
                text = { Text("Editar") },
                onClick = { expandido = false; onEditar() },
                leadingIcon = { Icon(Icons.Default.Edit, null) }
            )
            DropdownMenuItem(
                text = { Text("Excluir", color = Color.Red) },
                onClick = { expandido = false; onExcluir() },
                leadingIcon = { Icon(Icons.Default.Delete, null, tint = Color.Red) }
            )
        }
    }
}

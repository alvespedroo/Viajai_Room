package com.example.viajai_maf.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viajai_maf.data.AvaliacaoEntity
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

private val vermelho = Color(0xFF8B261D)
private val laranja = Color(0xFFEA813C)
private val creme = Color(0xFFFFF7EF)
private val fundo = Brush.verticalGradient(listOf(laranja, vermelho))
private fun dinheiro(valor: Double): String = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)

@Composable private fun Fundo(titulo: String, onVoltar: (() -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(Modifier.fillMaxSize().background(fundo).padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (onVoltar != null) IconButton(onClick = onVoltar) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Voltar", tint = Color.White) }
            Text(titulo, color = Color.White, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(12.dp))
        Column(Modifier.weight(1f).fillMaxWidth(), content = content)
    }
}
@Composable private fun Cartao(content: @Composable ColumnScope.() -> Unit) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.padding(14.dp), content = content) }
}
@Composable private fun Barra(score: Int) {
    val corScore = when {
        score >= 100 -> Color(0xFF2E8B57)
        score >= 50 -> Color(0xFFF2B705)
        else -> Color(0xFFD94343)
    }
    Text("Preparação: $score%", fontWeight = FontWeight.Bold, color = corScore)
    LinearProgressIndicator(
        progress = { score.coerceIn(0, 100) / 100f },
        modifier = Modifier.fillMaxWidth().height(8.dp),
        color = corScore,
        trackColor = creme
    )
}
@Composable private fun ProgressoParte(rotulo: String, feitos: Int, total: Int) {
    Row(Modifier.fillMaxWidth().padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(rotulo, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
        Text("$feitos/$total", style = MaterialTheme.typography.bodySmall)
    }
    LinearProgressIndicator(
        progress = { if (total == 0) 0f else feitos / total.toFloat() },
        modifier = Modifier.fillMaxWidth().height(6.dp), color = vermelho, trackColor = creme
    )
}
@Composable fun ChecklistPainelScreen(dados: ViajaiDados, onViagem: () -> Unit, onBagagem: () -> Unit, onGastos: () -> Unit, onDetalhe: (Int) -> Unit, onEditarViagem: (Int) -> Unit = {}) {
    val viagem = dados.viagemAtual
    var novoBloco by remember { mutableStateOf("") }
    var novoItem by remember { mutableStateOf("") }
    var editarBloco by remember { mutableStateOf<String?>(null) }
    var nomeEditado by remember { mutableStateOf("") }
    var editarItemId by remember { mutableStateOf<Int?>(null) }
    var itemEditado by remember { mutableStateOf("") }
    Fundo("Checklist") {
        if (viagem == null) {
            Cartao { Text("Nenhuma viagem em planejamento."); Button(onClick = onViagem) { Text("+ Planejar viagem") } }
        } else {
            val itens = dados.preparo.filter { it.viagemId == viagem.id }
            val blocos = itens.map { it.categoria }.distinct()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                item { Cartao {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(viagem.nomeExibicao, style = MaterialTheme.typography.titleLarge, color = vermelho, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        TextButton(onClick = { onEditarViagem(viagem.id) }) { Text("Editar") }
                    }
                    Text("${viagem.origem} → ${viagem.destino}", color = Color(0xFF7957A8), style = MaterialTheme.typography.bodySmall)
                    Barra(dados.scoreDaViagem(viagem.id))
                    ProgressoParte("Checklist", itens.count { it.marcado }, itens.size)
                    ProgressoParte("Bagagem", dados.bagagens.count { it.preparado }, dados.bagagens.size)
                    Text("Checklist e bagagem têm o mesmo peso na preparação.", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
                    Spacer(Modifier.height(6.dp))
                    Text("Orçamento: ${dinheiro(viagem.orcamento)}")
                    TextButton(onClick = { onDetalhe(viagem.id) }) { Text("Detalhes, concluir ou cancelar viagem") }
                } }
                item { Cartao {
                    Text("Bagagem", style = MaterialTheme.typography.titleMedium, color = vermelho, fontWeight = FontWeight.Bold)
                    val faltandoMala = dados.bagagens.count { !it.preparado }
                    Text(if (faltandoMala == 0) "Tudo certo: bagagem 100% pronta." else "Faltam $faltandoMala ${if (faltandoMala == 1) "item" else "itens"} para colocar na mala.")
                    Button(onClick = onBagagem, colors = ButtonDefaults.buttonColors(containerColor = vermelho), modifier = Modifier.fillMaxWidth()) { Text("Abrir bagagem") }
                } }
                item { Cartao {
                    Text("Selecionar viagem", fontWeight = FontWeight.Bold)
                    dados.viagens.forEach { v -> TextButton(onClick = { dados.viagemSelecionadaId = v.id }) { Text(v.nomeExibicao + " • " + v.origem + " → " + v.destino + if (v.id == viagem.id) " ✓" else "") } }
                } }
                items(blocos, key = { "bloco_${viagem.id}_$it" }) { bloco ->
                    Cartao {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(bloco, fontWeight = FontWeight.Bold, color = vermelho, modifier = Modifier.weight(1f))
                            TextButton(onClick = { editarBloco = bloco; nomeEditado = bloco }) { Text("Editar") }
                            IconButton(onClick = { dados.removerCategoria(viagem.id, bloco) }) { Icon(Icons.Default.Delete, "Excluir bloco") }
                        }
                        if (editarBloco == bloco) {
                            OutlinedTextField(nomeEditado, { nomeEditado = it }, label = { Text("Nome do bloco") }, modifier = Modifier.fillMaxWidth())
                            Button(onClick = {
                                if (nomeEditado.isNotBlank()) dados.preparo.filter { it.viagemId == viagem.id && it.categoria == bloco }.forEach { item -> dados.atualizarPreparo(item.copy(categoria = nomeEditado.trim())) }
                                editarBloco = null
                            }) { Text("Salvar nome") }
                        }
                        itens.filter { it.categoria == bloco }.forEach { tarefa ->
                            val deslocamentoX = remember(tarefa.id) { Animatable(0f) }
                            val scopeArraste = rememberCoroutineScope()
                            var larguraItemPx by remember(tarefa.id) { mutableFloatStateOf(0f) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .onSizeChanged { larguraItemPx = it.width.toFloat() }
                                    .background(Color(0xFFD94343))
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.End,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Excluir",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color.White
                                    )
                                }
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .graphicsLayer { translationX = deslocamentoX.value }
                                        .background(Color.White)
                                        .pointerInput(tarefa.id, larguraItemPx) {
                                            detectHorizontalDragGestures(
                                                onHorizontalDrag = { change, dragAmount ->
                                                    change.consume()
                                                    scopeArraste.launch {
                                                        deslocamentoX.snapTo((deslocamentoX.value + dragAmount).coerceIn(-larguraItemPx, 0f))
                                                    }
                                                },
                                                onDragEnd = {
                                                    scopeArraste.launch {
                                                        if (larguraItemPx > 0f && deslocamentoX.value <= -larguraItemPx * 0.42f) {
                                                            deslocamentoX.animateTo(-larguraItemPx)
                                                            dados.removerPreparo(tarefa.id)
                                                        } else {
                                                            deslocamentoX.animateTo(0f)
                                                        }
                                                    }
                                                },
                                                onDragCancel = {
                                                    scopeArraste.launch { deslocamentoX.animateTo(0f) }
                                                }
                                            )
                                        },
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = tarefa.marcado,
                                        onCheckedChange = { marcado ->
                                            if (dados.preparo.any { it.id == tarefa.id }) dados.atualizarPreparo(tarefa.copy(marcado = marcado))
                                        }
                                    )
                                    if (editarItemId == tarefa.id) {
                                        OutlinedTextField(itemEditado, { itemEditado = it }, modifier = Modifier.weight(1f).padding(end = 8.dp), singleLine = true)
                                        TextButton(onClick = {
                                            if (dados.preparo.any { it.id == tarefa.id } && itemEditado.isNotBlank()) dados.atualizarPreparo(tarefa.copy(nome = itemEditado.trim()))
                                            editarItemId = null
                                        }) { Text("OK") }
                                    } else {
                                        Text(
                                            tarefa.nome,
                                            modifier = Modifier.weight(1f).fillMaxWidth().clickable { editarItemId = tarefa.id; itemEditado = tarefa.nome }.padding(vertical = 14.dp, horizontal = 4.dp),
                                            style = MaterialTheme.typography.bodyLarge
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                        OutlinedTextField(novoItem, { novoItem = it }, label = { Text("Novo item em $bloco") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(10.dp))
                        TextButton(
                            onClick = { if (novoItem.isNotBlank()) { dados.adicionarPreparo(bloco, novoItem.trim(), viagem.id); novoItem = "" } },
                            modifier = Modifier.align(Alignment.End)
                        ) { Text("+ Adicionar item") }
                    }
                }
                item { Cartao {
                    Text("Adicionar bloco", fontWeight = FontWeight.Bold)
                    OutlinedTextField(novoBloco, { novoBloco = it }, label = { Text("Nome do bloco") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
                    Button(onClick = { if (novoBloco.isNotBlank() && novoBloco !in blocos) { dados.adicionarPreparo(novoBloco.trim(), "Novo item", viagem.id); novoBloco = "" } }) { Text("+ Criar bloco") }
                } }
                item { Button(onClick = onGastos, modifier = Modifier.fillMaxWidth()) { Text("Gastos") } }
            }
        }
    }
}
@Composable fun BagagemViajaiScreen(dados: ViajaiDados, onVoltar: () -> Unit, onDetalhe: (Int) -> Unit) {
    var nome by remember { mutableStateOf("") }; var qtd by remember { mutableStateOf("1") }
    Fundo("Minha Bagagem", onVoltar) {
        LazyColumn {
            item { Cartao {
                Text("Adicionar item", fontWeight = FontWeight.Bold)
                OutlinedTextField(nome, { nome = it }, label = { Text("Item") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(qtd, { qtd = it.filter(Char::isDigit) }, label = { Text("Quantidade") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { val n = qtd.toIntOrNull(); if (nome.isNotBlank() && n != null && n > 0) { dados.adicionarBagagem(nome.trim(), n); nome = ""; qtd = "1" } }) { Text("Adicionar") }
            } }
            items(dados.bagagens, key = { it.id }) { item -> Cartao {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(item.preparado, onCheckedChange = { v -> dados.atualizarBagagem(item.copy(preparado = v)) })
                    Column(Modifier.weight(1f).clickable { onDetalhe(item.id) }) { Text(item.nome, fontWeight = FontWeight.Bold); Text("Quantidade: ${item.quantidade} • Detalhes →") }
                    IconButton(onClick = { dados.removerBagagem(item.id) }) { Icon(Icons.Default.Delete, "Remover") }
                }
            } }
        }
    }
}
@Composable fun DetalheBagagemScreen(dados: ViajaiDados, id: Int, onVoltar: () -> Unit) {
    val item = dados.bagagens.firstOrNull { it.id == id }
    Fundo("Detalhes da bagagem", onVoltar) { Cartao {
        if (item == null) Text("Item não encontrado") else {
            Text(item.nome, style = MaterialTheme.typography.titleLarge)
            Text("Quantidade: ${item.quantidade}")
            Text(if (item.preparado) "Já está na mala" else "Ainda falta colocar na mala")
            Button(onClick = { dados.atualizarBagagem(item.copy(preparado = !item.preparado)) }) { Text("Alternar preparação") }
        }
    } }
}
@Composable fun DetalheViagemScreen(dados: ViajaiDados, id: Int, onVoltar: () -> Unit, onGastos: () -> Unit, onEditar: (Int) -> Unit = {}, onBagagem: () -> Unit = {}) {
    val viagemCarregada = dados.viagemPorId(id)
    Fundo("Detalhes da viagem", onVoltar) { Cartao {
        if (viagemCarregada == null) Text("Carregando viagem do banco...") else {
            Text(viagemCarregada.nomeExibicao, style = MaterialTheme.typography.titleLarge)
            Text("${viagemCarregada.origem} → ${viagemCarregada.destino} • Data: ${viagemCarregada.data}")
            StatusBadge(viagemCarregada.status)
            Button(onClick = { onEditar(viagemCarregada.id) }, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7052A5))) { Text("Editar viagem") }
            Spacer(Modifier.height(12.dp)); Barra(dados.scoreDaViagem(viagemCarregada.id))
            val itensChecklist = dados.preparo.filter { it.viagemId == viagemCarregada.id }
            ProgressoParte("Checklist", itensChecklist.count { it.marcado }, itensChecklist.size)
            ProgressoParte("Bagagem", dados.bagagens.count { it.preparado }, dados.bagagens.size)
            Text("Checklist e bagagem têm o mesmo peso na preparação.", style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.padding(top = 6.dp))
            OutlinedButton(onClick = onBagagem, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("Abrir bagagem") }
            Text("Orçamento: ${dinheiro(viagemCarregada.orcamento)}")
            val total = dados.gastosDaViagem(viagemCarregada.id)
            Text("Gastos registrados: ${dinheiro(total)}")
            Text("Saldo estimado: ${dinheiro(viagemCarregada.orcamento - total)}")
            Button(onClick = onGastos) { Text("Gerenciar gastos") }
            Spacer(Modifier.height(8.dp))
            val itensDaChecklist = dados.preparo.filter { it.viagemId == viagemCarregada.id }
            val faltantes = itensDaChecklist.count { !it.marcado }
            val faltantesBagagem = dados.bagagens.count { !it.preparado }
            val temPendencia = faltantes > 0 || faltantesBagagem > 0
            var mostrarConfirmacaoConclusao by remember(viagemCarregada.id) { mutableStateOf(false) }
            var mostrarConfirmacaoCancelamento by remember(viagemCarregada.id) { mutableStateOf(false) }
            val viagemEncerrada = viagemCarregada.concluida || viagemCarregada.cancelada
            if (!viagemEncerrada) {
                Button(
                    onClick = {
                        if (temPendencia) mostrarConfirmacaoConclusao = true
                        else dados.concluirViagem(viagemCarregada) { onVoltar() }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = vermelho)
                ) { Text("Concluir viagem") }
                OutlinedButton(onClick = { mostrarConfirmacaoCancelamento = true }, modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC62828))) { Text("Cancelar viagem") }
            }
            if (mostrarConfirmacaoCancelamento) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacaoCancelamento = false },
                    title = { Text("Cancelar viagem?") },
                    text = { Text("A viagem sai do planejamento, mas continua no histórico do perfil como cancelada.") },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarConfirmacaoCancelamento = false
                            dados.cancelarViagem(viagemCarregada) { onVoltar() }
                        }) { Text("Cancelar viagem", color = Color(0xFFC62828)) }
                    },
                    dismissButton = { TextButton(onClick = { mostrarConfirmacaoCancelamento = false }) { Text("Manter") } }
                )
            }
            if (mostrarConfirmacaoConclusao) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacaoConclusao = false },
                    title = { Text("Preparação incompleta") },
                    text = {
                        val pendencias = buildList {
                            if (faltantes > 0) add("$faltantes ${if (faltantes == 1) "item" else "itens"} na checklist")
                            if (faltantesBagagem > 0) add("$faltantesBagagem ${if (faltantesBagagem == 1) "item" else "itens"} na bagagem")
                        }.joinToString(" e ")
                        Text("Ainda faltam $pendencias. Quer concluir a viagem mesmo assim?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarConfirmacaoConclusao = false
                            dados.concluirViagem(viagemCarregada) { onVoltar() }
                        }) { Text("Concluir mesmo assim") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarConfirmacaoConclusao = false }) { Text("Voltar e revisar") }
                    }
                )
            }
        }
    } }
}
@Composable fun GastosViajaiScreen(dados: ViajaiDados, onVoltar: () -> Unit, onEditarOrcamento: (Int) -> Unit = {}) {
    var nome by remember { mutableStateOf("") }; var valor by remember { mutableStateOf("") }
    val viagem = dados.viagemAtual
    val total = viagem?.let { dados.gastosDaViagem(it.id) } ?: 0.0
    val orcamento = viagem?.orcamento ?: 0.0
    Fundo("Total de Gastos", onVoltar) {
        LazyColumn {
            item { Cartao {
                Text("Total gasto", color = vermelho)
                Text(dinheiro(total), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Orçamento: ${dinheiro(orcamento)}", modifier = Modifier.weight(1f))
                    if (viagem != null) TextButton(onClick = { onEditarOrcamento(viagem.id) }) { Text("Editar orçamento", color = Color(0xFF7052A5)) }
                }
                LinearProgressIndicator(progress = { if (orcamento <= 0) 0f else (total / orcamento).toFloat().coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth(), color = laranja)
                Text("Saldo: ${dinheiro(orcamento - total)}")
            } }
            item { Cartao {
                Text("Nova despesa", fontWeight = FontWeight.Bold)
                OutlinedTextField(nome, { nome = it }, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(valor, { valor = it }, label = { Text("Valor (R$)") }, modifier = Modifier.fillMaxWidth())
                Button(onClick = { val v = valor.replace(',', '.').toDoubleOrNull(); if (viagem != null && nome.isNotBlank() && v != null && v > 0) { dados.adicionarDespesa(nome.trim(), "Outros", v, viagem.id); nome = ""; valor = "" } }) { Text("Adicionar gasto") }
            } }
            items(dados.despesas.filter { it.viagemId == viagem?.id }, key = { "despesa_${it.id}" }) { despesa -> Cartao {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) { Text(despesa.descricao, fontWeight = FontWeight.Bold); Text(dinheiro(despesa.valor)) }
                    IconButton(onClick = { dados.removerDespesa(despesa.id) }) { Icon(Icons.Default.Delete, "Remover despesa") }
                }
            } }
        }
    }
}
@Composable
fun PerfilViajaiScreen(dados: ViajaiDados, onViagens: () -> Unit, onAbrirLugar: (Int) -> Unit, onEditarViagem: (Int) -> Unit = {}) {
    var filtroSalvos by remember { mutableStateOf("Todos") }
    var nome by remember { mutableStateOf("") }
    var nomeSalvo by remember { mutableStateOf("") }
    var editandoNome by remember { mutableStateOf(false) }
    val minhasAvaliacoes = dados.avaliacoes.filter { it.isMinha }
    // Nome salvo vem do PerfilViewModel; quando ele muda (carregou ou foi salvo), o campo acompanha.
    LaunchedEffect(dados.nomePerfil) {
        nomeSalvo = dados.nomePerfil
        nome = dados.nomePerfil
    }
    Fundo("Meu Perfil") {
        Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Cartao {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    Surface(shape = RoundedCornerShape(50), color = Color(0xFFFFE5D9), modifier = Modifier.size(58.dp)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Person, "Avatar", tint = vermelho, modifier = Modifier.size(35.dp)) }
                    }
                    Column {
                        Text(nome.ifBlank { "Nome de usuário" }, fontWeight = FontWeight.Bold, color = vermelho, style = MaterialTheme.typography.titleMedium)
                        Text("Meu perfil de viajante", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Configurações da conta", fontWeight = FontWeight.SemiBold, color = vermelho)
                    TextButton(
                        onClick = {
                            nome = nomeSalvo
                            editandoNome = !editandoNome
                        },
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(if (editandoNome) "Cancelar" else "Alterar nome", color = vermelho)
                    }
                }
                if (editandoNome) {
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome de usuário") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Button(
                        onClick = {
                            if (nome.isNotBlank() && nome.trim() != nomeSalvo) {
                                dados.salvarNomePerfil(nome.trim()) {
                                    nomeSalvo = nome.trim()
                                    nome = nomeSalvo
                                    editandoNome = false
                                }
                            }
                        },
                        enabled = nome.isNotBlank() && nome.trim() != nomeSalvo,
                        colors = ButtonDefaults.buttonColors(containerColor = vermelho),
                        modifier = Modifier.wrapContentWidth()
                    ) { Text("Salvar") }
                }
                if (nomeSalvo.isNotBlank()) Text("Nome salvo neste aparelho", color = Color(0xFF388E3C), style = MaterialTheme.typography.bodySmall)
            }
            Cartao {
                Text("Lugares Salvos", fontWeight = FontWeight.Bold, color = vermelho, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf("Todos", "Cidades", "Hotéis", "Restaurantes", "Passeios").forEach { opcao ->
                        FilterChip(selected = filtroSalvos == opcao, onClick = { filtroSalvos = opcao },
                            label = { Text(opcao, style = MaterialTheme.typography.labelSmall) },
                            colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFFFFE4D5)))
                    }
                }
                val salvosFiltrados = lugaresExplorar.filter { lugar ->
                    lugar.id in dados.lugaresSalvos && (filtroSalvos == "Todos" || lugar.categoria == filtroSalvos)
                }
                if (salvosFiltrados.isEmpty()) {
                    Text(if (dados.lugaresSalvos.isEmpty()) "Você ainda não salvou nenhum lugar." else "Nenhum lugar salvo nesta categoria.",
                        style = MaterialTheme.typography.bodyMedium)
                }
                salvosFiltrados.forEach { lugar ->
                    Card(onClick = { onAbrirLugar(lugar.id) }, modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                        shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = creme)) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(8.dp)) {
                            if (lugar.foto != null) {
                                Image(painter = painterResource(lugar.foto), contentDescription = lugar.nome,
                                    contentScale = ContentScale.Crop, modifier = Modifier.size(width = 94.dp, height = 76.dp))
                            } else {
                                Surface(color = Color(0xFFFFE5D9), shape = RoundedCornerShape(10.dp), modifier = Modifier.size(width = 94.dp, height = 76.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Place, contentDescription = null, tint = vermelho, modifier = Modifier.size(30.dp)) }
                                }
                            }
                            Spacer(Modifier.width(10.dp))
                            Column(Modifier.weight(1f)) {
                                Text(lugar.nome, fontWeight = FontWeight.Bold, color = vermelho, maxLines = 2, overflow = TextOverflow.Ellipsis)
                                Text(lugar.regiao, style = MaterialTheme.typography.bodySmall)
                                Text("Ver detalhes ›", color = vermelho, style = MaterialTheme.typography.labelSmall)
                            }
                            IconButton(onClick = { dados.alternarLugarSalvo(lugar.id) }) {
                                Icon(Icons.Default.Delete, "Remover dos salvos", tint = vermelho)
                            }
                        }
                    }
                }
            }
            Cartao {
                Text("Histórico de avaliações", fontWeight = FontWeight.Bold, color = vermelho, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(6.dp))
                if (minhasAvaliacoes.isEmpty()) {
                    Text("Suas avaliações publicadas aparecerão aqui.", style = MaterialTheme.typography.bodySmall)
                }
                minhasAvaliacoes.forEach { avaliacao ->
                    val lugarAvaliacao = lugaresExplorar.firstOrNull { it.id == avaliacao.lugarId }
                    HorizontalDivider()
                    Spacer(Modifier.height(7.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        if (lugarAvaliacao?.foto != null) {
                            Image(
                                painter = painterResource(lugarAvaliacao.foto),
                                contentDescription = lugarAvaliacao.nome,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(width = 88.dp, height = 76.dp).clip(RoundedCornerShape(10.dp))
                            )
                        } else {
                            Surface(
                                color = Color(0xFFFFE5D9), shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(width = 88.dp, height = 76.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(Icons.Default.Place, contentDescription = "Imagem indisponível", tint = vermelho, modifier = Modifier.size(30.dp))
                                }
                            }
                        }
                        Column(Modifier.weight(1f)) {
                            Text(lugarAvaliacao?.nome ?: "Lugar #${avaliacao.lugarId}", fontWeight = FontWeight.SemiBold, color = vermelho)
                            Text("Nota: ${String.format(Locale("pt", "BR"), "%.1f", avaliacao.nota)} / 5", style = MaterialTheme.typography.bodySmall)
                            Text(avaliacao.texto, style = MaterialTheme.typography.bodyMedium, maxLines = 3, overflow = TextOverflow.Ellipsis)
                        }
                        TextButton(onClick = { onAbrirLugar(avaliacao.lugarId) }) { Text("Abrir") }
                    }
                    Spacer(Modifier.height(7.dp))
                }
            }
            Cartao {
                Text("Histórico de viagens", fontWeight = FontWeight.Bold, color = vermelho)
                val historico = (dados.viagens + dados.viagensEncerradas).sortedByDescending { it.id }
                if (historico.isEmpty()) Text("Nenhuma viagem cadastrada.")
                historico.forEach { viagem ->
                    HorizontalDivider()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
                            Text(viagem.nomeExibicao, fontWeight = FontWeight.SemiBold)
                            Text("${viagem.data}  •  ${viagem.origem} → ${viagem.destino}", style = MaterialTheme.typography.bodySmall)
                        }
                        StatusBadge(viagem.status)
                    }
                }
            }
            Cartao {
                Text("Minhas viagens", fontWeight = FontWeight.Bold, color = vermelho)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column(Modifier.weight(1f)) { Text("${dados.viagens.size}", style = MaterialTheme.typography.headlineSmall, color = vermelho); Text("Viagens planejadas", style = MaterialTheme.typography.bodySmall) }
                    Column(Modifier.weight(1f)) { Text("${dados.score}%", style = MaterialTheme.typography.headlineSmall, color = vermelho); Text("Preparação", style = MaterialTheme.typography.bodySmall) }
                }
                Spacer(Modifier.height(8.dp))
                dados.viagens.forEach { viagem ->
                    HorizontalDivider()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f).padding(vertical = 8.dp)) {
                            Text(viagem.nomeExibicao, fontWeight = FontWeight.SemiBold)
                            Text("${viagem.data}  •  ${viagem.origem} → ${viagem.destino}", style = MaterialTheme.typography.bodySmall)
                        }
                        TextButton(onClick = { onEditarViagem(viagem.id) }) { Text("Editar", color = Color(0xFF7052A5)) }
                    }
                }
                Button(onClick = onViagens, colors = ButtonDefaults.buttonColors(containerColor = vermelho), modifier = Modifier.fillMaxWidth()) { Text("Ver minhas viagens") }
            }
        }
    }
}

package com.example.viajai_maf

import android.content.Context
import android.util.Log
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.NumberFormat
import java.util.Locale

private val vermelho = Color(0xFF8B261D)
private val laranja = Color(0xFFEA813C)
private val creme = Color(0xFFFFF7EF)
private val fundo = Brush.verticalGradient(listOf(laranja, vermelho))
private fun dinheiro(valor: Double): String = NumberFormat.getCurrencyInstance(Locale("pt", "BR")).format(valor)

data class ViagemMaf(val id: Int, val origem: String, val destino: String, val data: String, val orcamento: Double)
data class BagagemMaf(val id: Int, val nome: String, val quantidade: Int, val preparado: Boolean)
data class DespesaMaf(val id: Int, val descricao: String, val categoria: String, val valor: Double, val viagemId: Int = 1)
data class PreparoMaf(val id: Int, val categoria: String, val nome: String, val marcado: Boolean, val viagemId: Int = 1)

class ViajaiDados(context: Context? = null) {
    private var contador = 20
    fun proximoId(): Int = ++contador
    private val db = context?.let { AppDatabase.getDatabase(it) }
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    val lugaresSalvos = mutableStateListOf<Int>()
    val viagensConcluidas = mutableStateListOf<ViagemMaf>()
    val viagens = mutableStateListOf<ViagemMaf>()
    var viagemSelecionadaId by mutableIntStateOf(-1)
    var avaliacoesVersao by mutableIntStateOf(0)
    val viagemAtual: ViagemMaf? get() = viagens.firstOrNull { it.id == viagemSelecionadaId } ?: viagens.firstOrNull()
    fun scoreDaViagem(id: Int): Int { val lista = preparo.filter { it.viagemId == id }; return if (lista.isEmpty()) 0 else lista.count { it.marcado } * 100 / lista.size }
    fun gastosDaViagem(id: Int): Double = despesas.filter { it.viagemId == id }.sumOf { it.valor }
    val bagagens = mutableStateListOf(BagagemMaf(1, "Roupas", 5, true), BagagemMaf(2, "Carregador", 1, false))
    val despesas = mutableStateListOf<DespesaMaf>()
    val preparo = mutableStateListOf<PreparoMaf>()

    init {
        if (db != null) scope.launch {
            try {
                val viagensDb = db.viagemDao().buscarTodos()
                val checklistDb = db.checklistDao().buscarTodos()
                var bagagemDb = db.bagagemDao().buscarTodos()
                if (bagagemDb.isEmpty()) {
                    db.bagagemDao().inserir(BagagemEntity(nome = "Roupas", quantidade = 5, preparado = true))
                    db.bagagemDao().inserir(BagagemEntity(nome = "Carregador", quantidade = 1, preparado = false))
                    bagagemDb = db.bagagemDao().buscarTodos()
                }
                val despesasDb = db.despesaDao().buscarTodos()
                val salvosDb = db.lugarSalvoDao().buscarTodos()
                withContext(Dispatchers.Main) {
                    viagens.clear()
                    viagens.addAll(viagensDb.filter { !it.concluida }.map { ViagemMaf(it.id, it.origem, it.destino, it.data, it.orcamento) })
                    viagensConcluidas.clear()
                    viagensConcluidas.addAll(viagensDb.filter { it.concluida }.map { ViagemMaf(it.id, it.origem, it.destino, it.data, it.orcamento) })
                    preparo.clear()
                    preparo.addAll(checklistDb.map { PreparoMaf(it.id, it.categoria, it.nome, it.marcado, it.viagemId) })
                    bagagens.clear()
                    bagagens.addAll(bagagemDb.map { BagagemMaf(it.id, it.nome, it.quantidade, it.preparado) })
                    despesas.clear()
                    despesas.addAll(despesasDb.map { DespesaMaf(it.id, it.descricao, it.categoria, it.valor, it.viagemId) })
                    lugaresSalvos.clear()
                    lugaresSalvos.addAll(salvosDb.map { it.lugarId })
                    viagemSelecionadaId = viagens.firstOrNull()?.id ?: -1
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao carregar os dados", e) }
        }
    }

    fun recarregarViagens() {
        val database = db ?: return
        scope.launch {
            try {
                val itens = database.viagemDao().buscarTodos()
                withContext(Dispatchers.Main) {
                    viagens.clear(); viagens.addAll(itens.filter { !it.concluida }.map { ViagemMaf(it.id, it.origem, it.destino, it.data, it.orcamento) })
                    viagensConcluidas.clear(); viagensConcluidas.addAll(itens.filter { it.concluida }.map { ViagemMaf(it.id, it.origem, it.destino, it.data, it.orcamento) })
                    if (viagens.none { it.id == viagemSelecionadaId }) viagemSelecionadaId = viagens.firstOrNull()?.id ?: -1
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao recarregar viagens", e) }
        }
    }

    fun recarregarChecklist() {
        val database = db ?: return
        scope.launch {
            try {
                val itens = database.checklistDao().buscarTodos()
                withContext(Dispatchers.Main) {
                    preparo.clear()
                    preparo.addAll(itens.map { PreparoMaf(it.id, it.categoria, it.nome, it.marcado, it.viagemId) })
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao recarregar checklist", e) }
        }
    }

    fun recarregarBagagem() {
        val database = db ?: return
        scope.launch {
            try {
                val itens = database.bagagemDao().buscarTodos()
                withContext(Dispatchers.Main) {
                    bagagens.clear()
                    bagagens.addAll(itens.map { BagagemMaf(it.id, it.nome, it.quantidade, it.preparado) })
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao recarregar bagagem", e) }
        }
    }

    fun recarregarDespesas() {
        val database = db ?: return
        scope.launch {
            try {
                val itens = database.despesaDao().buscarTodos()
                withContext(Dispatchers.Main) {
                    despesas.clear()
                    despesas.addAll(itens.map { DespesaMaf(it.id, it.descricao, it.categoria, it.valor, it.viagemId) })
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao recarregar despesas", e) }
        }
    }

    fun recarregarLugaresSalvos() {
        val database = db ?: return
        scope.launch {
            try {
                val itens = database.lugarSalvoDao().buscarTodos()
                withContext(Dispatchers.Main) {
                    lugaresSalvos.clear()
                    lugaresSalvos.addAll(itens.map { it.lugarId })
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao recarregar lugares salvos", e) }
        }
    }

    fun carregarAvaliacoes(lugarId: Int, onResult: (List<AvaliacaoEntity>) -> Unit) {
        val database = db ?: return
        scope.launch {
            try {
                val itens = database.avaliacaoDao().buscarPorLugar(lugarId)
                withContext(Dispatchers.Main) { onResult(itens) }
            } catch (e: Exception) {
                Log.e("ViajaI-Room", "Falha ao carregar avaliações", e)
                withContext(Dispatchers.Main) { onResult(emptyList()) }
            }
        }
    }

    fun carregarTodasAvaliacoes(onResult: (List<AvaliacaoEntity>) -> Unit) {
        val database = db ?: return
        scope.launch {
            try {
                val itens = database.avaliacaoDao().buscarTodas()
                withContext(Dispatchers.Main) { onResult(itens) }
            } catch (e: Exception) {
                Log.e("ViajaI-Room", "Falha ao carregar todas as avaliações", e)
                withContext(Dispatchers.Main) { onResult(emptyList()) }
            }
        }
    }

    fun carregarMinhasAvaliacoes(onResult: (List<AvaliacaoEntity>) -> Unit) {
        val database = db ?: return
        scope.launch {
            try {
                val itens = database.avaliacaoDao().buscarMinhas()
                withContext(Dispatchers.Main) { onResult(itens) }
            } catch (e: Exception) {
                Log.e("ViajaI-Room", "Falha ao carregar histórico de avaliações", e)
                withContext(Dispatchers.Main) { onResult(emptyList()) }
            }
        }
    }

    fun adicionarAvaliacao(lugarId: Int, autor: String, texto: String, nota: Double, onResult: (AvaliacaoEntity) -> Unit) {
        val database = db ?: return
        scope.launch {
            try {
                val item = AvaliacaoEntity(lugarId = lugarId, autor = autor, texto = texto, nota = nota, isMinha = true)
                val id = database.avaliacaoDao().inserir(item).toInt()
                withContext(Dispatchers.Main) {
                    avaliacoesVersao++
                    onResult(item.copy(id = id))
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao salvar avaliação", e) }
        }
    }

    fun alternarCurtidaAvaliacao(item: AvaliacaoEntity, onResult: (AvaliacaoEntity) -> Unit) {
        val database = db ?: return
        scope.launch {
            try {
                val atualizado = item.copy(
                    curtidas = (item.curtidas + if (item.curtiu) -1 else 1).coerceAtLeast(0),
                    curtiu = !item.curtiu
                )
                database.avaliacaoDao().atualizar(atualizado)
                withContext(Dispatchers.Main) { onResult(atualizado) }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao salvar curtida", e) }
        }
    }

    fun editarAvaliacao(item: AvaliacaoEntity, texto: String, nota: Double, onResult: (AvaliacaoEntity) -> Unit) {
        val database = db ?: return
        scope.launch {
            try {
                val atualizada = item.copy(texto = texto, nota = nota)
                database.avaliacaoDao().atualizar(atualizada)
                withContext(Dispatchers.Main) {
                    avaliacoesVersao++
                    onResult(atualizada)
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao editar avaliação", e) }
        }
    }

    fun excluirAvaliacao(item: AvaliacaoEntity, onComplete: () -> Unit) {
        val database = db ?: return
        scope.launch {
            try {
                database.avaliacaoDao().deletar(item)
                withContext(Dispatchers.Main) {
                    avaliacoesVersao++
                    onComplete()
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao excluir avaliação", e) }
        }
    }

    fun carregarNomePerfil(onResult: (String) -> Unit) {
        val database = db ?: return
        scope.launch {
            try {
                val nome = database.perfilUsuarioDao().buscar()?.nome.orEmpty()
                withContext(Dispatchers.Main) { onResult(nome) }
            } catch (e: Exception) {
                Log.e("ViajaI-Room", "Falha ao carregar nome do perfil", e)
                withContext(Dispatchers.Main) { onResult("") }
            }
        }
    }

    fun salvarNomePerfil(nome: String, onComplete: () -> Unit = {}) {
        val database = db ?: return
        scope.launch {
            try {
                val nomeAtualizado = nome.trim()
                database.perfilUsuarioDao().salvar(PerfilUsuarioEntity(nome = nomeAtualizado))
                database.avaliacaoDao().atualizarAutorMinhas(nomeAtualizado)
                withContext(Dispatchers.Main) {
                    avaliacoesVersao++
                    onComplete()
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao salvar nome do perfil", e) }
        }
    }

    fun inserirViagem(origem: String, destino: String, data: String, orcamento: Double, onComplete: (Int) -> Unit) {
        val database = db
        if (database == null) {
            val id = proximoId()
            viagens.add(ViagemMaf(id, origem, destino, data, orcamento))
            viagemSelecionadaId = id
            criarChecklistPadrao(id)
            onComplete(id)
            return
        }
        scope.launch {
            try {
                val id = database.viagemDao().inserir(ViagemEntity(origem = origem, destino = destino, data = data, orcamento = orcamento)).toInt()
                val iniciais = listOf("Documentos" to "RG", "Documentos" to "CNH", "Revisão do carro" to "Pneus", "Revisão do carro" to "Estepe", "Emergência" to "Kit médico")
                iniciais.forEach { (categoria, nome) -> database.checklistDao().inserir(ChecklistEntity(categoria = categoria, nome = nome, marcado = false, viagemId = id)) }
                val itens = database.checklistDao().buscarPorViagem(id)
                withContext(Dispatchers.Main) {
                    viagens.add(ViagemMaf(id, origem, destino, data, orcamento))
                    preparo.addAll(itens.map { PreparoMaf(it.id, it.categoria, it.nome, it.marcado, it.viagemId) })
                    viagemSelecionadaId = id
                    onComplete(id)
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao salvar viagem", e) }
        }
    }

    fun adicionarPreparo(categoria: String, nome: String, viagemId: Int, marcado: Boolean = false) {
        val database = db
        if (database == null) { preparo.add(PreparoMaf(proximoId(), categoria, nome, marcado, viagemId)); return }
        scope.launch {
            try {
                val id = database.checklistDao().inserir(ChecklistEntity(categoria = categoria, nome = nome, marcado = marcado, viagemId = viagemId)).toInt()
                withContext(Dispatchers.Main) { preparo.add(PreparoMaf(id, categoria, nome, marcado, viagemId)) }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao inserir item de checklist", e) }
        }
    }

    fun atualizarPreparo(item: PreparoMaf) {
        val database = db
        if (database == null) { val i = preparo.indexOfFirst { it.id == item.id }; if (i >= 0) preparo[i] = item; return }
        scope.launch {
            try {
                database.checklistDao().atualizar(ChecklistEntity(item.id, item.categoria, item.nome, item.marcado, item.viagemId))
                withContext(Dispatchers.Main) { val i = preparo.indexOfFirst { it.id == item.id }; if (i >= 0) preparo[i] = item }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao atualizar item de checklist", e) }
        }
    }

    fun removerPreparo(id: Int) {
        val database = db
        if (database == null) { preparo.removeAll { it.id == id }; return }
        scope.launch {
            try {
                database.checklistDao().buscarPorId(id)?.let { database.checklistDao().deletar(it) }
                withContext(Dispatchers.Main) { preparo.removeAll { it.id == id } }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao remover item de checklist", e) }
        }
    }

    fun removerCategoria(viagemId: Int, categoria: String) {
        val database = db
        if (database == null) { preparo.removeAll { it.viagemId == viagemId && it.categoria == categoria }; return }
        scope.launch {
            try {
                database.checklistDao().deletarCategoria(viagemId, categoria)
                withContext(Dispatchers.Main) { preparo.removeAll { it.viagemId == viagemId && it.categoria == categoria } }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao remover categoria", e) }
        }
    }

    fun buscarViagemPorId(id: Int, onResult: (ViagemMaf?) -> Unit) {
        val database = db
        if (database == null) { onResult(viagens.firstOrNull { it.id == id }); return }
        scope.launch {
            try {
                val entity = database.viagemDao().buscarPorId(id)
                withContext(Dispatchers.Main) { onResult(entity?.let { ViagemMaf(it.id, it.origem, it.destino, it.data, it.orcamento) }) }
            } catch (e: Exception) {
                Log.e("ViajaI-Room", "Falha ao buscar viagem por ID", e)
                withContext(Dispatchers.Main) { onResult(null) }
            }
        }
    }

    fun concluirViagem(viagem: ViagemMaf, onComplete: () -> Unit) {
        val database = db
        if (database == null) {
            viagensConcluidas.add(viagem)
            viagens.removeAll { it.id == viagem.id }
            viagemSelecionadaId = viagens.firstOrNull()?.id ?: -1
            onComplete()
            return
        }
        scope.launch {
            try {
                val entity = database.viagemDao().buscarPorId(viagem.id)
                if (entity != null) database.viagemDao().atualizar(entity.copy(concluida = true))
                withContext(Dispatchers.Main) {
                    viagensConcluidas.add(viagem)
                    viagens.removeAll { it.id == viagem.id }
                    viagemSelecionadaId = viagens.firstOrNull()?.id ?: -1
                    onComplete()
                }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao concluir viagem", e) }
        }
    }

    fun adicionarBagagem(nome: String, quantidade: Int) {
        val database = db
        if (database == null) { bagagens.add(BagagemMaf(proximoId(), nome, quantidade, false)); return }
        scope.launch {
            try {
                val id = database.bagagemDao().inserir(BagagemEntity(nome = nome, quantidade = quantidade, preparado = false)).toInt()
                withContext(Dispatchers.Main) { bagagens.add(BagagemMaf(id, nome, quantidade, false)) }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao inserir item de bagagem", e) }
        }
    }

    fun atualizarBagagem(item: BagagemMaf) {
        val database = db
        if (database == null) { val i = bagagens.indexOfFirst { it.id == item.id }; if (i >= 0) bagagens[i] = item; return }
        scope.launch {
            try {
                database.bagagemDao().atualizar(BagagemEntity(item.id, item.nome, item.quantidade, item.preparado))
                withContext(Dispatchers.Main) { val i = bagagens.indexOfFirst { it.id == item.id }; if (i >= 0) bagagens[i] = item }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao atualizar item de bagagem", e) }
        }
    }

    fun removerBagagem(id: Int) {
        val database = db
        if (database == null) { bagagens.removeAll { it.id == id }; return }
        scope.launch {
            try {
                database.bagagemDao().buscarPorId(id)?.let { database.bagagemDao().deletar(it) }
                withContext(Dispatchers.Main) { bagagens.removeAll { it.id == id } }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao remover item de bagagem", e) }
        }
    }

    fun adicionarDespesa(descricao: String, categoria: String, valor: Double, viagemId: Int) {
        val database = db
        if (database == null) { despesas.add(DespesaMaf(proximoId(), descricao, categoria, valor, viagemId)); return }
        scope.launch {
            try {
                val id = database.despesaDao().inserir(DespesaEntity(descricao = descricao, categoria = categoria, valor = valor, viagemId = viagemId)).toInt()
                withContext(Dispatchers.Main) { despesas.add(DespesaMaf(id, descricao, categoria, valor, viagemId)) }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao inserir despesa", e) }
        }
    }

    fun removerDespesa(id: Int) {
        val database = db
        if (database == null) { despesas.removeAll { it.id == id }; return }
        scope.launch {
            try {
                database.despesaDao().buscarPorId(id)?.let { database.despesaDao().deletar(it) }
                withContext(Dispatchers.Main) { despesas.removeAll { it.id == id } }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao remover despesa", e) }
        }
    }

    fun alternarLugarSalvo(lugarId: Int) {
        val database = db
        if (database == null) { if (lugarId in lugaresSalvos) lugaresSalvos.remove(lugarId) else lugaresSalvos.add(lugarId); return }
        scope.launch {
            try {
                val salvo = database.lugarSalvoDao().buscarPorLugarId(lugarId)
                if (salvo == null) database.lugarSalvoDao().inserir(LugarSalvoEntity(lugarId = lugarId)) else database.lugarSalvoDao().deletar(salvo)
                withContext(Dispatchers.Main) { if (lugarId in lugaresSalvos) lugaresSalvos.remove(lugarId) else lugaresSalvos.add(lugarId) }
            } catch (e: Exception) { Log.e("ViajaI-Room", "Falha ao alterar lugar salvo", e) }
        }
    }

    fun criarChecklistPadrao(viagemId: Int) {
        listOf("Documentos" to "RG", "Documentos" to "CNH", "Revisão do carro" to "Pneus", "Revisão do carro" to "Estepe", "Emergência" to "Kit médico").forEach { (categoria, nome) -> adicionarPreparo(categoria, nome, viagemId) }
    }
    val score: Int get() = viagemAtual?.let { scoreDaViagem(it.id) } ?: 0
}

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
@Composable fun ChecklistPainelScreen(dados: ViajaiDados, onViagem: () -> Unit, onBagagem: () -> Unit, onGastos: () -> Unit, onDetalhe: (Int) -> Unit) {
    LaunchedEffect(Unit) { dados.recarregarViagens(); dados.recarregarChecklist() }
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
                    Text(viagem.destino, style = MaterialTheme.typography.titleLarge, color = vermelho, fontWeight = FontWeight.Bold)
                    Barra(dados.scoreDaViagem(viagem.id))
                    Text("${itens.count { it.marcado }}/${itens.size} itens concluídos")
                    Text("Orçamento: ${dinheiro(viagem.orcamento)}")
                    TextButton(onClick = { onDetalhe(viagem.id) }) { Text("Detalhes e concluir viagem") }
                } }
                item { Cartao {
                    Text("Selecionar viagem", fontWeight = FontWeight.Bold)
                    dados.viagens.forEach { v -> TextButton(onClick = { dados.viagemSelecionadaId = v.id }) { Text(v.destino + if (v.id == viagem.id) " ✓" else "") } }
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
                item { Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = onBagagem, modifier = Modifier.weight(1f)) { Text("Bagagem") }
                    Button(onClick = onGastos, modifier = Modifier.weight(1f)) { Text("Gastos") }
                } }
            }
        }
    }
}
@Composable fun BagagemViajaiScreen(dados: ViajaiDados, onVoltar: () -> Unit, onDetalhe: (Int) -> Unit) {
    LaunchedEffect(Unit) { dados.recarregarBagagem() }
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
@Composable fun DetalheViagemScreen(dados: ViajaiDados, id: Int, onVoltar: () -> Unit, onGastos: () -> Unit) {
    var viagem by remember(id) { mutableStateOf<ViagemMaf?>(null) }
    LaunchedEffect(id) { dados.buscarViagemPorId(id) { viagem = it } }
    val viagemCarregada = viagem
    Fundo("Detalhes da viagem", onVoltar) { Cartao {
        if (viagemCarregada == null) Text("Carregando viagem do banco...") else {
            Text(viagemCarregada.destino, style = MaterialTheme.typography.titleLarge)
            Text("Origem: ${viagemCarregada.origem} • Data: ${viagemCarregada.data}")
            Spacer(Modifier.height(12.dp)); Barra(dados.scoreDaViagem(viagemCarregada.id))
            Text("Orçamento: ${dinheiro(viagemCarregada.orcamento)}")
            val total = dados.gastosDaViagem(viagemCarregada.id)
            Text("Gastos registrados: ${dinheiro(total)}")
            Text("Saldo estimado: ${dinheiro(viagemCarregada.orcamento - total)}")
            Button(onClick = onGastos) { Text("Gerenciar gastos") }
            Spacer(Modifier.height(8.dp))
            val itensDaChecklist = dados.preparo.filter { it.viagemId == viagemCarregada.id }
            val faltantes = itensDaChecklist.count { !it.marcado }
            var mostrarConfirmacaoConclusao by remember(viagemCarregada.id) { mutableStateOf(false) }
            Button(
                onClick = {
                    if (faltantes > 0) mostrarConfirmacaoConclusao = true
                    else dados.concluirViagem(viagemCarregada) { onVoltar() }
                },
                colors = ButtonDefaults.buttonColors(containerColor = vermelho)
            ) { Text("Concluir viagem") }
            if (mostrarConfirmacaoConclusao) {
                AlertDialog(
                    onDismissRequest = { mostrarConfirmacaoConclusao = false },
                    title = { Text("Checklist incompleta") },
                    text = {
                        Text("Ainda ${if (faltantes == 1) "falta" else "faltam"} $faltantes ${if (faltantes == 1) "item" else "itens"} na checklist. Quer concluir a viagem mesmo assim?")
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            mostrarConfirmacaoConclusao = false
                            dados.concluirViagem(viagemCarregada) { onVoltar() }
                        }) { Text("Concluir mesmo assim") }
                    },
                    dismissButton = {
                        TextButton(onClick = { mostrarConfirmacaoConclusao = false }) { Text("Voltar à checklist") }
                    }
                )
            }
        }
    } }
}
@Composable fun GastosViajaiScreen(dados: ViajaiDados, onVoltar: () -> Unit) {
    LaunchedEffect(Unit) { dados.recarregarDespesas() }
    var nome by remember { mutableStateOf("") }; var valor by remember { mutableStateOf("") }
    val viagem = dados.viagemAtual
    val total = viagem?.let { dados.gastosDaViagem(it.id) } ?: 0.0
    val orcamento = viagem?.orcamento ?: 0.0
    Fundo("Total de Gastos", onVoltar) {
        LazyColumn {
            item { Cartao {
                Text("Total gasto", color = vermelho)
                Text(dinheiro(total), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Orçamento: ${dinheiro(orcamento)}")
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
fun PerfilViajaiScreen(dados: ViajaiDados, onViagens: () -> Unit, onAbrirLugar: (Int) -> Unit) {
    LaunchedEffect(Unit) { dados.recarregarLugaresSalvos(); dados.recarregarViagens() }
    var filtroSalvos by remember { mutableStateOf("Todos") }
    var nome by remember { mutableStateOf("") }
    var nomeSalvo by remember { mutableStateOf("") }
    var editandoNome by remember { mutableStateOf(false) }
    val minhasAvaliacoes = remember { mutableStateListOf<AvaliacaoEntity>() }
    LaunchedEffect(dados.avaliacoesVersao) { dados.carregarMinhasAvaliacoes { itens -> minhasAvaliacoes.clear(); minhasAvaliacoes.addAll(itens) } }
    LaunchedEffect(Unit) {
        dados.carregarNomePerfil { salvo ->
            nomeSalvo = salvo
            nome = salvo
        }
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
                if (dados.viagensConcluidas.isEmpty()) Text("Nenhuma viagem concluída.")
                dados.viagensConcluidas.forEach { Text("${it.destino} • ${it.data}") }
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
                    Spacer(Modifier.height(8.dp))
                    Text(viagem.destino, fontWeight = FontWeight.SemiBold)
                    Text("${viagem.data}  •  ${viagem.origem}", style = MaterialTheme.typography.bodySmall)
                    Spacer(Modifier.height(8.dp))
                }
                Button(onClick = onViagens, colors = ButtonDefaults.buttonColors(containerColor = vermelho), modifier = Modifier.fillMaxWidth()) { Text("Ver minhas viagens") }
            }
        }
    }
}

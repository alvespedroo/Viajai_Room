package com.example.viajai_maf.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.viajai_maf.data.AppContainer
import com.example.viajai_maf.data.AvaliacaoEntity
import com.example.viajai_maf.data.BagagemEntity
import com.example.viajai_maf.data.ChecklistEntity
import com.example.viajai_maf.data.ViagemEntity
import com.example.viajai_maf.viewmodel.AvaliacaoUiState
import com.example.viajai_maf.viewmodel.AvaliacaoViewModel
import com.example.viajai_maf.viewmodel.AvaliacaoViewModelFactory
import com.example.viajai_maf.viewmodel.BagagemUiState
import com.example.viajai_maf.viewmodel.BagagemViewModel
import com.example.viajai_maf.viewmodel.BagagemViewModelFactory
import com.example.viajai_maf.viewmodel.ChecklistUiState
import com.example.viajai_maf.viewmodel.ChecklistViewModel
import com.example.viajai_maf.viewmodel.ChecklistViewModelFactory
import com.example.viajai_maf.viewmodel.DespesaUiState
import com.example.viajai_maf.viewmodel.DespesaViewModel
import com.example.viajai_maf.viewmodel.DespesaViewModelFactory
import com.example.viajai_maf.viewmodel.PerfilUiState
import com.example.viajai_maf.viewmodel.PerfilViewModel
import com.example.viajai_maf.viewmodel.PerfilViewModelFactory
import com.example.viajai_maf.viewmodel.ViagemUiState
import com.example.viajai_maf.viewmodel.ViagemViewModel
import com.example.viajai_maf.viewmodel.ViagemViewModelFactory

// ---------- Modelos usados pelas telas ----------

data class ViagemMaf(
    val id: Int, val origem: String, val destino: String, val data: String, val orcamento: Double, val titulo: String = "",
    val concluida: Boolean = false, val cancelada: Boolean = false
) {
    val nomeExibicao: String get() = titulo.ifBlank { "$origem → $destino" }
    val status: StatusViagem get() = calcularStatus(data, concluida, cancelada)
}
data class BagagemMaf(val id: Int, val nome: String, val quantidade: Int, val preparado: Boolean)
data class DespesaMaf(val id: Int, val descricao: String, val categoria: String, val valor: Double, val viagemId: Int = 1)
data class PreparoMaf(val id: Int, val categoria: String, val nome: String, val marcado: Boolean, val viagemId: Int = 1)

fun normalizarDataViagem(valor: String): String {
    val texto = valor.trim()
    if (texto.matches(Regex("\\d{8,}"))) return ""
    val formatos = listOf("dd/MM/yyyy", "yyyy-MM-dd", "dd-MM-yyyy")
    for (padrao in formatos) try {
        val formato = java.text.SimpleDateFormat(padrao, java.util.Locale("pt", "BR")).apply { isLenient = false }
        val data = formato.parse(texto) ?: continue
        return java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale("pt", "BR")).format(data)
    } catch (_: Exception) { }
    return texto
}

private fun ViagemEntity.paraMaf() =
    ViagemMaf(id, origem, destino, normalizarDataViagem(data), orcamento, titulo, concluida, cancelada)
private fun ChecklistEntity.paraMaf() = PreparoMaf(id, categoria, nome, marcado, viagemId)
private fun BagagemEntity.paraMaf() = BagagemMaf(id, nome, quantidade, preparado)

/**
 * Ponte entre as telas e os ViewModels.
 *
 * Aqui NÃO existe banco, DAO, CoroutineScope nem Dispatchers: só leitura do `uiState` de cada
 * ViewModel (já coletado como State do Compose) e chamadas aos métodos deles. Toda a regra de
 * dados mora nos ViewModels (viewmodel/) e nos Repositories (data/repository/).
 */
class ViajaiDados(
    private val viagemVm: ViagemViewModel, private val viagemUi: State<ViagemUiState>,
    private val checklistVm: ChecklistViewModel, private val checklistUi: State<ChecklistUiState>,
    private val bagagemVm: BagagemViewModel, private val bagagemUi: State<BagagemUiState>,
    private val despesaVm: DespesaViewModel, private val despesaUi: State<DespesaUiState>,
    private val perfilVm: PerfilViewModel, private val perfilUi: State<PerfilUiState>,
    private val avaliacaoVm: AvaliacaoViewModel, private val avaliacaoUi: State<AvaliacaoUiState>
) {
    // ----- Estado lido pelas telas -----
    val viagens: List<ViagemMaf> get() = viagemUi.value.ativas.map { it.paraMaf() }
    val viagensEncerradas: List<ViagemMaf> get() = viagemUi.value.encerradas.map { it.paraMaf() }
    val viagemAtual: ViagemMaf? get() = viagemUi.value.atual?.paraMaf()
    var viagemSelecionadaId: Int
        get() = viagemUi.value.selecionadaId
        set(valor) = viagemVm.selecionar(valor)
    val preparo: List<PreparoMaf> get() = checklistUi.value.itens.map { it.paraMaf() }
    val bagagens: List<BagagemMaf> get() = bagagemUi.value.itens.map { it.paraMaf() }
    val despesas: List<DespesaMaf> get() = despesaUi.value.itens.map { DespesaMaf(it.id, it.descricao, it.categoria, it.valor, it.viagemId) }
    val lugaresSalvos: List<Int> get() = perfilUi.value.lugaresSalvos
    val nomePerfil: String get() = perfilUi.value.nome
    val avaliacoes: List<AvaliacaoEntity> get() = avaliacaoUi.value.avaliacoes
    val carregandoAvaliacoes: Boolean get() = avaliacaoUi.value.carregando

    fun viagemPorId(id: Int): ViagemMaf? = viagemUi.value.viagens.firstOrNull { it.id == id }?.paraMaf()

    // ----- Cálculos derivados -----
    fun gastosDaViagem(id: Int): Double = despesas.filter { it.viagemId == id }.sumOf { it.valor }

    /** Progresso (0 a 100) do checklist da viagem, ou null se ela ainda não tem itens. */
    fun progressoChecklist(id: Int): Int? {
        val lista = preparo.filter { it.viagemId == id }
        return if (lista.isEmpty()) null else lista.count { it.marcado } * 100 / lista.size
    }
    /** Progresso (0 a 100) da bagagem, ou null se a lista está vazia. */
    fun progressoBagagem(): Int? = bagagens.let { if (it.isEmpty()) null else it.count { item -> item.preparado } * 100 / it.size }
    /**
     * Preparação da viagem: checklist e bagagem têm o MESMO peso (média dos dois percentuais).
     * Se uma das listas estiver vazia, vale só a outra, para a viagem não ficar presa em 50%.
     */
    fun scoreDaViagem(id: Int): Int {
        val partes = listOfNotNull(progressoChecklist(id), progressoBagagem())
        return if (partes.isEmpty()) 0 else partes.sum() / partes.size
    }
    val score: Int get() = viagemAtual?.let { scoreDaViagem(it.id) } ?: 0

    // ----- Viagens -----
    fun inserirViagem(origem: String, destino: String, data: String, orcamento: Double, titulo: String = "", onComplete: (Int) -> Unit) =
        viagemVm.criar(origem, destino, data, orcamento, titulo, onComplete)
    fun atualizarViagem(id: Int, origem: String, destino: String, data: String, orcamento: Double, titulo: String, onComplete: () -> Unit = {}) =
        viagemVm.atualizar(id, origem, destino, data, orcamento, titulo, onComplete)
    fun concluirViagem(viagem: ViagemMaf, onComplete: () -> Unit) = viagemVm.concluir(viagem.id, onComplete)
    fun cancelarViagem(viagem: ViagemMaf, onComplete: () -> Unit) = viagemVm.cancelar(viagem.id, onComplete)

    // ----- Checklist -----
    fun adicionarPreparo(categoria: String, nome: String, viagemId: Int, marcado: Boolean = false) = checklistVm.adicionar(categoria, nome, viagemId, marcado)
    fun atualizarPreparo(item: PreparoMaf) = checklistVm.atualizar(ChecklistEntity(item.id, item.categoria, item.nome, item.marcado, item.viagemId))
    fun removerPreparo(id: Int) = checklistVm.remover(id)
    fun removerCategoria(viagemId: Int, categoria: String) = checklistVm.removerCategoria(viagemId, categoria)

    // ----- Bagagem -----
    fun adicionarBagagem(nome: String, quantidade: Int) = bagagemVm.adicionar(nome, quantidade)
    fun atualizarBagagem(item: BagagemMaf) = bagagemVm.atualizar(BagagemEntity(item.id, item.nome, item.quantidade, item.preparado))
    fun removerBagagem(id: Int) = bagagemVm.remover(id)

    // ----- Gastos -----
    fun adicionarDespesa(descricao: String, categoria: String, valor: Double, viagemId: Int) = despesaVm.adicionar(descricao, categoria, valor, viagemId)
    fun removerDespesa(id: Int) = despesaVm.remover(id)

    // ----- Perfil e lugares salvos -----
    fun alternarLugarSalvo(lugarId: Int) = perfilVm.alternarLugarSalvo(lugarId)
    fun salvarNomePerfil(nome: String, onComplete: () -> Unit = {}) = perfilVm.salvarNome(nome, onComplete)

    // ----- Avaliações -----
    fun adicionarAvaliacao(lugarId: Int, autor: String, texto: String, nota: Double, onResult: (AvaliacaoEntity) -> Unit = {}) =
        avaliacaoVm.adicionar(lugarId, autor, texto, nota, onResult)
    fun alternarCurtidaAvaliacao(item: AvaliacaoEntity, onResult: (AvaliacaoEntity) -> Unit = {}) = avaliacaoVm.alternarCurtida(item, onResult)
    fun editarAvaliacao(item: AvaliacaoEntity, texto: String, nota: Double, onResult: (AvaliacaoEntity) -> Unit = {}) = avaliacaoVm.editar(item, texto, nota, onResult)
    fun excluirAvaliacao(item: AvaliacaoEntity, onComplete: () -> Unit = {}) = avaliacaoVm.excluir(item, onComplete)
}

/**
 * Obtém cada ViewModel com `viewModel(factory = ...)` (a Factory injeta o Repository),
 * coleta o `uiState` com `collectAsState()` e entrega tudo às telas.
 */
@Composable
fun rememberViajaiDados(container: AppContainer): ViajaiDados {
    val viagemVm: ViagemViewModel = viewModel(factory = ViagemViewModelFactory(container.viagemRepository, container.checklistRepository))
    val checklistVm: ChecklistViewModel = viewModel(factory = ChecklistViewModelFactory(container.checklistRepository))
    val bagagemVm: BagagemViewModel = viewModel(factory = BagagemViewModelFactory(container.bagagemRepository))
    val despesaVm: DespesaViewModel = viewModel(factory = DespesaViewModelFactory(container.despesaRepository))
    val perfilVm: PerfilViewModel = viewModel(factory = PerfilViewModelFactory(container.perfilRepository, container.lugarSalvoRepository, container.avaliacaoRepository))
    val avaliacaoVm: AvaliacaoViewModel = viewModel(factory = AvaliacaoViewModelFactory(container.avaliacaoRepository))

    val viagemUi = viagemVm.uiState.collectAsState()
    val checklistUi = checklistVm.uiState.collectAsState()
    val bagagemUi = bagagemVm.uiState.collectAsState()
    val despesaUi = despesaVm.uiState.collectAsState()
    val perfilUi = perfilVm.uiState.collectAsState()
    val avaliacaoUi = avaliacaoVm.uiState.collectAsState()

    return remember(viagemVm, checklistVm, bagagemVm, despesaVm, perfilVm, avaliacaoVm) {
        ViajaiDados(viagemVm, viagemUi, checklistVm, checklistUi, bagagemVm, bagagemUi, despesaVm, despesaUi, perfilVm, perfilUi, avaliacaoVm, avaliacaoUi)
    }
}

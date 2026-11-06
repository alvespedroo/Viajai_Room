package com.example.viajai_maf.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.viajai_maf.data.LugarSalvoEntity
import com.example.viajai_maf.data.PerfilUsuarioEntity
import com.example.viajai_maf.data.repository.AvaliacaoRepository
import com.example.viajai_maf.data.repository.LugarSalvoRepository
import com.example.viajai_maf.data.repository.PerfilUsuarioRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** Nome do usuário e ids dos lugares salvos (tela de Perfil). */
data class PerfilUiState(
    val nome: String = "",
    val lugaresSalvos: List<Int> = emptyList(),
    val carregando: Boolean = true,
    val erro: String? = null
)

class PerfilViewModel(
    private val perfilRepository: PerfilUsuarioRepository,
    private val lugarSalvoRepository: LugarSalvoRepository,
    private val avaliacaoRepository: AvaliacaoRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        executar("Falha ao carregar nome do perfil") {
            val nome = perfilRepository.buscar()?.nome.orEmpty()
            _uiState.update { it.copy(nome = nome) }
        }
        observar(
            lugarSalvoRepository.buscarTodos(),
            aoFalhar = { erro -> _uiState.update { it.copy(carregando = false, erro = erro) } }
        ) { lista -> _uiState.update { it.copy(lugaresSalvos = lista.map { l -> l.lugarId }, carregando = false, erro = null) } }
    }

    /** Salva o nome e atualiza o autor das avaliações feitas pelo usuário. */
    fun salvarNome(nome: String, onComplete: () -> Unit = {}) {
        executar("Falha ao salvar nome do perfil") {
            val novoNome = nome.trim()
            perfilRepository.salvar(PerfilUsuarioEntity(nome = novoNome))
            avaliacaoRepository.atualizarAutorMinhas(novoNome)
            _uiState.update { it.copy(nome = novoNome) }
            onComplete()
        }
    }

    fun alternarLugarSalvo(lugarId: Int) {
        executar("Falha ao alterar lugar salvo") {
            val salvo = lugarSalvoRepository.buscarPorLugarId(lugarId)
            if (salvo == null) lugarSalvoRepository.inserir(LugarSalvoEntity(lugarId = lugarId)) else lugarSalvoRepository.deletar(salvo)
        }
    }
}

class PerfilViewModelFactory(
    private val perfilRepository: PerfilUsuarioRepository,
    private val lugarSalvoRepository: LugarSalvoRepository,
    private val avaliacaoRepository: AvaliacaoRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PerfilViewModel::class.java)) return PerfilViewModel(perfilRepository, lugarSalvoRepository, avaliacaoRepository) as T
        throw IllegalArgumentException("ViewModel desconhecido: ${modelClass.name}")
    }
}

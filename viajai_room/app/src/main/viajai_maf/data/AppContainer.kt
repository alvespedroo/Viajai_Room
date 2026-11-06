package com.example.viajai_maf.data

import android.content.Context
import com.example.viajai_maf.data.repository.AvaliacaoRepository
import com.example.viajai_maf.data.repository.BagagemRepository
import com.example.viajai_maf.data.repository.ChecklistRepository
import com.example.viajai_maf.data.repository.DespesaRepository
import com.example.viajai_maf.data.repository.LugarSalvoRepository
import com.example.viajai_maf.data.repository.PerfilUsuarioRepository
import com.example.viajai_maf.data.repository.ViagemRepository

/**
 * Único lugar que conhece o banco e os DAOs: monta o Room e entrega os Repositories prontos.
 * A camada de interface (ui) só recebe Repositories (via ViewModelFactory) e nunca toca em DAO.
 */
class AppContainer(context: Context) {
    private val banco = AppDatabase.getDatabase(context.applicationContext)

    val viagemRepository = ViagemRepository(banco.viagemDao())
    val checklistRepository = ChecklistRepository(banco.checklistDao())
    val bagagemRepository = BagagemRepository(banco.bagagemDao())
    val despesaRepository = DespesaRepository(banco.despesaDao())
    val lugarSalvoRepository = LugarSalvoRepository(banco.lugarSalvoDao())
    val avaliacaoRepository = AvaliacaoRepository(banco.avaliacaoDao())
    val perfilRepository = PerfilUsuarioRepository(banco.perfilUsuarioDao())
}

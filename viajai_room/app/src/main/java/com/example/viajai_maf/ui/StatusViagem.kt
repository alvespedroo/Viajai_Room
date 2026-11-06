package com.example.viajai_maf.ui

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Situação de uma viagem, mostrada no histórico do perfil e nos detalhes. */
enum class StatusViagem(val rotulo: String, val cor: Color) {
    PLANEJAMENTO("Em planejamento", Color(0xFF3F6FB5)),
    EM_ANDAMENTO("Em andamento", Color(0xFFE08A00)),
    CONCLUIDA("Concluída", Color(0xFF2E8B57)),
    CANCELADA("Cancelada", Color(0xFFC62828))
}

private val formatoDataViagem = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/**
 * Regras do status:
 * - cancelada e concluída são decisões do usuário e têm prioridade;
 * - senão, a viagem fica "em andamento" a partir do dia da data marcada;
 * - antes disso (ou se a data for inválida), está "em planejamento".
 */
fun calcularStatus(data: String, concluida: Boolean, cancelada: Boolean, hoje: LocalDate = LocalDate.now()): StatusViagem = when {
    cancelada -> StatusViagem.CANCELADA
    concluida -> StatusViagem.CONCLUIDA
    else -> {
        val dataViagem = runCatching { LocalDate.parse(data, formatoDataViagem) }.getOrNull()
        if (dataViagem != null && !dataViagem.isAfter(hoje)) StatusViagem.EM_ANDAMENTO else StatusViagem.PLANEJAMENTO
    }
}

/** Etiqueta colorida com o status. */
@Composable
fun StatusBadge(status: StatusViagem, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(50), color = status.cor.copy(alpha = 0.14f)) {
        Text(
            status.rotulo,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = status.cor
        )
    }
}

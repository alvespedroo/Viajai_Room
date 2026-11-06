package com.example.viajai_maf

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

data class ChecklistTarefa(val id: Int, val nome: String, val estaMarcado: Boolean)
data class ChecklistBloco(val id: Int, val titulo: String, val tarefas: List<ChecklistTarefa>)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.statusBarColor = android.graphics.Color.rgb(232, 119, 53)
        setContent {
            MaterialTheme {
                AppNavigation()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViajaiChecklistScreenPreview() {
    ViajaiChecklistScreen(
        onNavigateToHome = {},
        onNavigateToDestinos = {}
    )
}

@Composable
fun ViajaiChecklistScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToDestinos: () -> Unit
) {
    val context = LocalContext.current

    val blocosPreDefinidos = remember {
        mutableStateListOf(
            ChecklistBloco(
                id = 1,
                titulo = "Documentos",
                tarefas = listOf(
                    ChecklistTarefa(1, "RG", true),
                    ChecklistTarefa(2, "CNH", true),
                    ChecklistTarefa(3, "Seguro/Assistência", false)
                )
            ),
            ChecklistBloco(
                id = 2,
                titulo = "Revisão do Carro",
                tarefas = listOf(
                    ChecklistTarefa(4, "Pneu", true),
                    ChecklistTarefa(5, "Estepe", false),
                    ChecklistTarefa(6, "Faróis", true)
                )
            ),
            ChecklistBloco(
                id = 3,
                titulo = "Emergência",
                tarefas = listOf(
                    ChecklistTarefa(7, "Dinheiro em espécie", true),
                    ChecklistTarefa(8, "Kit Médico", true),
                    ChecklistTarefa(9, "Alimentação", true)
                )
            )
        )
    }

    val gradientBackground = Brush.verticalGradient(
        colors = listOf(Color(0xFFE47E45), Color(0xFF8B261D))
    )

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(gradientBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = onNavigateToDestinos) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                }

                blocosPreDefinidos.forEach { bloco ->
                    ChecklistBlockCard(
                        bloco = bloco,
                        onTarefaClick = { tarefaClicada ->
                            val index = blocosPreDefinidos.indexOf(bloco)
                            if (index != -1) {
                                val novasTarefas = bloco.tarefas.map {
                                    if (it.id == tarefaClicada.id) it.copy(estaMarcado = !it.estaMarcado) else it
                                }
                                blocosPreDefinidos[index] = bloco.copy(tarefas = novasTarefas)
                            }
                        },
                        onAddItemClick = {
                            val index = blocosPreDefinidos.indexOf(bloco)
                            if (index != -1) {
                                val novoId = (System.currentTimeMillis() % 10000).toInt()
                                val novasTarefas = bloco.tarefas + ChecklistTarefa(
                                    novoId,
                                    "Novo Item Adicionado",
                                    false
                                )

                                blocosPreDefinidos[index] = bloco.copy(tarefas = novasTarefas)
                                Toast.makeText(
                                    context,
                                    "Item adicionado em ${bloco.titulo}!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onNomeTarefaChange = { tarefaAlvo, novoTexto ->
                            val index = blocosPreDefinidos.indexOf(bloco)
                            if (index != -1) {
                                val tarefasEditadas = bloco.tarefas.map {
                                    if (it.id == tarefaAlvo.id) it.copy(nome = novoTexto) else it
                                }
                                blocosPreDefinidos[index] = bloco.copy(tarefas = tarefasEditadas)
                            }
                        },
                        onDeleteBlocoClick = {
                            blocosPreDefinidos.remove(bloco)
                            Toast.makeText(context, "${bloco.titulo} excluído!", Toast.LENGTH_SHORT).show()
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = "Adicionar Bloco +",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .clickable {
                                val novoBlocoId = blocosPreDefinidos.size + 1
                                blocosPreDefinidos.add(
                                    ChecklistBloco(
                                        id = novoBlocoId,
                                        titulo = "Novo Bloco $novoBlocoId",
                                        tarefas = listOf(ChecklistTarefa(novoBlocoId * 100, "Exemplo de Item", false))
                                    )
                                )
                                Toast.makeText(context, "Novo Bloco Criado!", Toast.LENGTH_SHORT).show()
                            }
                    )
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }
        }
    }
}

@Composable
fun ChecklistBlockCard(
    bloco: ChecklistBloco,
    onTarefaClick: (ChecklistTarefa) -> Unit,
    onAddItemClick: () -> Unit,
    onNomeTarefaChange: (ChecklistTarefa, String) -> Unit,
    onDeleteBlocoClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = bloco.titulo,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "+",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier
                            .clickable { onAddItemClick() }
                            .padding(horizontal = 8.dp)
                    )
                    Text(
                        text = "❌",
                        fontSize = 16.sp,
                        modifier = Modifier
                            .clickable { onDeleteBlocoClick() }
                            .padding(horizontal = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            bloco.tarefas.forEach { tarefa ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (tarefa.estaMarcado) "☑️" else "⬜",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .clickable { onTarefaClick(tarefa) }
                            .padding(end = 10.dp)
                    )
                    BasicTextField(
                        value = tarefa.nome,
                        onValueChange = { novoTexto -> onNomeTarefaChange(tarefa, novoTexto) },
                        textStyle = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black,
                            fontWeight = FontWeight.Medium
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

package com.example.viajai_maf

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val corPrincipal = Color(0xFF922D24)
private val corLaranja = Color(0xFFEB8448)

data class LugarExplorar(val id: Int, val nome: String, val regiao: String, val categoria: String,
                         val nota: String, val descricao: String, val foto: Int? = null)

val lugaresExplorar = listOf(
    LugarExplorar(1, "Pousada Maraka", "São Paulo, SP", "Hotéis", "—", "Uma opção de hospedagem para quem busca uma estadia prática e confortável durante a viagem. Confira as avaliações recentes para conhecer as experiências de outras pessoas.", R.drawable.pousada_maraka),
    LugarExplorar(2, "Casa São Paulo", "São Paulo, SP", "Hotéis", "—", "Hospedagem em São Paulo para servir de base durante a visita à cidade. Consulte as avaliações dos viajantes e confirme valores, disponibilidade e serviços antes de reservar.", R.drawable.pousada_sao_paulo),
    LugarExplorar(3, "Pousada Zamberlan", "São Paulo, SP", "Hotéis", "—", "Alternativa de hospedagem para descansar entre os passeios. Leia os comentários publicados para entender melhor as experiências registradas pela comunidade.", R.drawable.pousada_zamberlan),
    LugarExplorar(4, "Casa do Porco", "São Paulo, SP", "Restaurantes", "—", "Restaurante conhecido pela cozinha brasileira contemporânea e pelo trabalho com carne suína. É uma opção para quem quer explorar uma experiência gastronômica na capital paulista.", R.drawable.casa_do_porco_foto),
    LugarExplorar(5, "Restaurante Manu", "Curitiba, PR", "Restaurantes", "—", "Restaurante de Curitiba com proposta autoral e atenção aos ingredientes e à sazonalidade. Consulte as avaliações e informações atualizadas antes de planejar a visita.", R.drawable.manu_foto),
    LugarExplorar(6, "Jardim Botânico", "Curitiba, PR", "Passeios", "—", "Um dos cartões-postais de Curitiba, com jardins, estufa de estrutura marcante e caminhos para passear ao ar livre e fotografar a paisagem.", R.drawable.jardim_botanico_foto),
    LugarExplorar(7, "Praias de Florianópolis", "Florianópolis, SC", "Passeios", "—", "Florianópolis reúne praias com perfis diferentes, de faixas de areia tranquilas a pontos procurados para surfe e atividades ao ar livre. Escolha a região conforme o clima e o estilo do passeio.", R.drawable.praia_florianopolis_foto),
    LugarExplorar(8, "Gramado", "Gramado, RS", "Cidades", "—", "Destino da Serra Gaúcha conhecido pela arquitetura de inspiração europeia, gastronomia, parques e programação sazonal. Vale organizar o roteiro conforme a época do ano e a localização das atrações.", R.drawable.gramado),
    LugarExplorar(9, "Balneário Camboriú", "Balneário Camboriú, SC", "Cidades", "—", "Cidade litorânea com orla movimentada, praias, mirantes e atrações urbanas. O roteiro pode combinar passeios à beira-mar com atividades para diferentes idades.", R.drawable.balneario_camboriu),
    LugarExplorar(10, "Cataratas do Iguaçu", "Foz do Iguaçu, PR", "Passeios", "—", "Conjunto de quedas-d'água cercado por Mata Atlântica, com trilhas e mirantes no Parque Nacional do Iguaçu. Reserve tempo para caminhar e confira regras e horários do parque antes da visita.", R.drawable.cataratas_iguacu),
    LugarExplorar(11, "Cristo Redentor", "Rio de Janeiro, RJ", "Passeios", "—", "Monumento no alto do Corcovado, com vista panorâmica de áreas do Rio de Janeiro. A experiência depende das condições do tempo, então vale conferir a previsão e os meios oficiais de acesso.", R.drawable.cristo_redentor),
    LugarExplorar(13, "Recife", "Recife, PE", "Cidades", "—", "Capital pernambucana com pontes, rios, patrimônio histórico, mercados e uma cena cultural marcante. O centro histórico e a orla oferecem experiências diferentes para montar um roteiro variado.", R.drawable.recife_foto),
    LugarExplorar(14, "Rio de Janeiro", "Rio de Janeiro, RJ", "Cidades", "—", "Cidade conhecida pelas praias, montanhas, mirantes, áreas verdes e cartões-postais. Planeje deslocamentos entre regiões e confira condições locais antes de sair para os passeios.", R.drawable.rio_de_janeiro),
    LugarExplorar(15, "Curitiba", "Curitiba, PR", "Cidades", "—", "Capital paranaense com parques, espaços culturais, feiras, gastronomia e opções de transporte para explorar diferentes bairros. É possível combinar atrações ao ar livre com passeios pelo centro.", R.drawable.curitiba_parana),
    LugarExplorar(17, "Elevador Lacerda", "Salvador, BA", "Passeios", "—", "Elevador público e cartão-postal de Salvador que conecta a Cidade Alta à Cidade Baixa. Do entorno, é possível apreciar a Baía de Todos-os-Santos e seguir o passeio pelo centro histórico.", R.drawable.elevador_lacerda),
    LugarExplorar(20, "São Paulo", "São Paulo, SP", "Cidades", "—", "A maior cidade do Brasil oferece museus, parques, centros culturais, mercados, bairros históricos e uma cena gastronômica diversa. Organize o roteiro por região para aproveitar melhor o tempo e reduzir deslocamentos.", R.drawable.sao_paulo),
    LugarExplorar(21, "Bairro da Liberdade", "São Paulo, SP", "Passeios", "—", "Bairro paulistano conhecido pela forte presença da cultura japonesa e de outras comunidades asiáticas, com lojas, restaurantes, mercados e feiras. Caminhe pelas ruas comerciais e confira a programação local para descobrir eventos e sabores da região.", R.drawable.bairro_da_liberdade)
)

fun lugarNome(id: Int): String = lugaresExplorar.firstOrNull { it.id == id }?.nome ?: "Lugar #$id"

@Composable
fun PesquisarScreen(onSelecionarDestino: (Int) -> Unit, dados: ViajaiDados) {
    var busca by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf("Cidades") }
    var avaliacoes by remember { mutableStateOf<List<AvaliacaoEntity>>(emptyList()) }
    LaunchedEffect(dados.avaliacoesVersao) { dados.carregarTodasAvaliacoes { avaliacoes = it } }
    val filtrados = lugaresExplorar.filter { lugar ->
        lugar.categoria == categoria && (busca.isBlank() || listOf(lugar.nome, lugar.regiao, lugar.descricao)
            .any { it.contains(busca.trim(), ignoreCase = true) })
    }
    Column(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(corLaranja, corPrincipal))).padding(horizontal = 16.dp)) {
        Spacer(Modifier.height(16.dp))
        Text("Explorar destinos", fontSize = 24.sp, color = Color.White, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(busca, { busca = it }, modifier = Modifier.fillMaxWidth(), singleLine = true,
            leadingIcon = { Icon(Icons.Outlined.Search, "Pesquisar") },
            placeholder = { Text("Buscar em ${categoria.lowercase()}...") }, shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(focusedContainerColor = Color.White, unfocusedContainerColor = Color.White))
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(3.dp), verticalAlignment = Alignment.CenterVertically) {
            listOf("Cidades", "Hotéis", "Restaurantes", "Passeios").forEach { opcao ->
                FilterChip(selected = categoria == opcao, onClick = { categoria = opcao },
                    label = { Text(opcao, fontSize = 10.sp, maxLines = 1) }, modifier = Modifier.padding(horizontal = 3.dp),
                    colors = FilterChipDefaults.filterChipColors(containerColor = Color.White,
                        selectedContainerColor = Color(0xFFFFE4D5)))
            }
        }
        Spacer(Modifier.height(10.dp))
        Text("${categoria} • ${filtrados.size} resultado(s)", color = Color.White, fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(10.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp), contentPadding = PaddingValues(bottom = 26.dp)) {
            if (filtrados.isEmpty()) item { Text("Nenhum resultado encontrado.", color = Color.White) }
            items(filtrados, key = { "explorar_${it.id}" }) { lugar ->
                Card(onClick = { onSelecionarDestino(lugar.id) }, shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column {
                        if (lugar.foto != null) {
                            Image(painterResource(lugar.foto), contentDescription = lugar.nome,
                                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxWidth().height(135.dp))
                        } else {
                            Surface(color = Color(0xFFFFE5D9), modifier = Modifier.fillMaxWidth().height(92.dp)) {
                                Row(Modifier.fillMaxSize().padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                                    Icon(Icons.Outlined.Place, contentDescription = null, tint = corPrincipal, modifier = Modifier.size(28.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Imagem para adicionar", color = corPrincipal, fontSize = 12.sp)
                                }
                            }
                        }
                        Column(Modifier.padding(13.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(lugar.nome, fontWeight = FontWeight.Bold, fontSize = 16.sp,
                                    modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                                IconButton(onClick = {
                                    dados.alternarLugarSalvo(lugar.id)
                                }) {
                                    Icon(if (lugar.id in dados.lugaresSalvos) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                                        "Salvar lugar", tint = corPrincipal)
                                }
                            }
                            Text(lugar.regiao, fontSize = 12.sp, color = Color.Gray)
                            Text(lugar.descricao, fontSize = 12.sp)
                            val reviewsLugar = avaliacoes.filter { it.lugarId == lugar.id }
                            val mediaLugar = reviewsLugar.map { it.nota }.average()
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Star, null, tint = Color(0xFFFFA000), modifier = Modifier.size(17.dp))
                                Text(if (reviewsLugar.isEmpty()) " Sem avaliações" else " ${String.format(java.util.Locale("pt", "BR"), "%.1f", mediaLugar)} (${reviewsLugar.size} ${if (reviewsLugar.size == 1) "avaliação" else "avaliações"})", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.weight(1f))
                                Text("Ver avaliações  ›", fontSize = 12.sp, color = corPrincipal)
                            }
                        }
                    }
                }
            }
        }
    }
}

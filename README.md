# Viajaí 🚗✈️

Aplicativo Android para planejamento, organização e acompanhamento de viagens de carro, desenvolvido em Kotlin com Jetpack Compose e persistência de dados utilizando Room.

## 👥 Integrantes

- Pedro Henrique Alves
- Eduardo Fogaça Bader

## 🎓 Informações acadêmicas

- **Instituição:** Universidade Positivo
- **Curso:** Análise e Desenvolvimento de Sistemas
- **Disciplina:** Desenvolvimento de Aplicativos Móveis
- **Professor:** André Luis Jeller Selleti

---

## 📱 Sobre o projeto

O **Viajaí** é um aplicativo desenvolvido para auxiliar no planejamento e na organização de viagens de carro, oferecendo ferramentas que permitem ao usuário preparar sua viagem com mais praticidade e segurança.

O aplicativo disponibiliza funcionalidades como checklist de itens necessários para a viagem, controle e acompanhamento de gastos, pesquisa de locais e avaliação de destinos, incluindo cidades, restaurantes, hotéis e pontos turísticos.

Seu principal objetivo é ajudar os viajantes a se organizarem antecipadamente, evitando esquecimentos, imprevistos e dificuldades durante o percurso.

O aplicativo permite cadastrar várias viagens, mantendo as informações de cada uma organizadas individualmente, sem misturar checklists, despesas ou dados de destinos diferentes.

## ✨ Funcionalidades

### 1. Dashboard inicial

A tela inicial apresenta um painel com as principais informações da viagem selecionada:

- Origem e destino.
- Score de preparação da viagem.
- Botão para continuar o planejamento.
- Progresso do checklist e da bagagem.
- Total de gastos registrados.
- Destinos relacionados à viagem.
- Acesso à checklist e à gestão de gastos.
- Opção para adicionar uma nova viagem.
- Acesso às opções de hospedagem dos locais relacionados à viagem.

### 2. Gerenciamento de viagens

O usuário pode cadastrar e gerenciar mais de uma viagem.

Cada viagem possui suas próprias informações e seus próprios dados, permitindo que o planejamento seja realizado individualmente.

Na tela de checklist, é possível selecionar qual viagem está sendo preparada. Assim, o usuário consegue alternar entre viagens sem perder as informações já cadastradas.

### 3. Checklist de viagem

Permite organizar os itens necessários para a viagem, incluindo a conferência da bagagem.

O usuário pode acompanhar o progresso de preparação e visualizar quantos itens foram preenchidos em relação ao total.

O checklist é associado à viagem selecionada, mantendo os dados organizados.

### 4. Gestão de gastos

O aplicativo permite cadastrar despesas individualmente e acompanhar os valores relacionados à viagem.

Entre as funcionalidades estão:

- Cadastro de despesas.
- Consulta dos gastos registrados.
- Visualização do total gasto.
- Acompanhamento do orçamento e do saldo estimado.
- Gerenciamento de despesas durante o planejamento e o encerramento da viagem.

### 5. Pesquisa de locais

O usuário pode pesquisar locais específicos e salvar opções para utilizar em viagens futuras.

Os locais salvos ficam disponíveis na aba de perfil, organizados por categorias:

- Todos.
- Cidades.
- Hotéis.
- Restaurantes.
- Passeios.

Também é possível remover locais da lista de salvos.

### 6. Avaliações de locais

O aplicativo permite avaliar lugares por meio de notas em estrelas e comentários.

O usuário pode consultar suas avaliações e também editar ou excluir comentários já registrados.

### 7. Perfil e histórico

A aba de perfil reúne informações importantes do usuário:

- **Lugares salvos:** locais favoritos organizados por categorias e filtros.
- **Histórico de avaliações:** avaliações realizadas, com possibilidade de acessar e editar os comentários.
- **Histórico de viagens:** viagens encerradas, apresentando informações como local e data.
- **Viagens em andamento:** painel com o número de viagens planejadas, score de preparação, origem e destino, permitindo acessar diretamente a checklist.

### 8. Encerramento de viagem

O Viajaí permite finalizar uma viagem após a conferência das informações.

Caso existam itens pendentes na checklist, o aplicativo apresenta um alerta informando a quantidade de itens que ainda faltam preencher e solicita a confirmação do usuário antes de prosseguir.

Na etapa de encerramento, são apresentados:

- Origem.
- Chegada.
- Data da viagem.
- Score de preparação.
- Orçamento.
- Gastos registrados.
- Saldo estimado.

O usuário ainda pode gerenciar os gastos nessa etapa antes de concluir a viagem.

Após a conclusão, a viagem fica registrada no histórico de viagens encerradas.

---

## 🗄️ Persistência de dados com Room

Neste projeto, foi utilizada a biblioteca **Room**, uma solução de persistência de dados do Android que facilita o armazenamento e a consulta de informações em um banco de dados SQLite.

A implementação foi organizada utilizando:

- **Entities:** representam as tabelas e os dados armazenados no banco.
- **DAOs (Data Access Objects):** responsáveis pelas operações de acesso aos dados.
- **AppDatabase:** classe responsável pela configuração e disponibilização do banco de dados.
- **KSP:** utilizado para processar as anotações do Room e gerar os códigos necessários.

O projeto possui múltiplas entidades e DAOs, com operações de inserção, consulta, atualização e exclusão, além de consultas específicas para recuperar registros por identificador e outros critérios.

### Operações de banco de dados

As operações de persistência são realizadas utilizando corrotinas, com execução das tarefas de banco de dados na thread de entrada e saída (IO).

Também são utilizados blocos `try/catch` para tratamento de possíveis erros, com registro de informações de depuração por meio de `Log.e`.

As telas utilizam `LaunchedEffect` para carregar os dados necessários durante sua inicialização ou quando determinados identificadores são alterados.

### Organização da implementação

A estrutura do banco de dados está concentrada no arquivo:

`RoomDatabase.kt`

A lógica de acesso aos dados e integração com as telas está organizada principalmente em:

`ViajaiMAFScreens.kt`

A navegação entre as telas é organizada em:

`AppNavigation.kt`

> 📌 **Atualização (Trabalho 4):** esses arquivos foram reorganizados em pacotes (`data/RoomDatabase.kt`, `ui/AppNavigation.kt`, `ui/ViajaiMAFScreens.kt`) e o acesso aos dados saiu das telas, indo para Repositories e ViewModels. Veja a seção **Trabalho 4 — Arquitetura MVVM** mais abaixo.

---

## 🧪 Testes de persistência

Foram realizados testes para verificar o funcionamento das operações de cadastro, consulta, edição e exclusão dos dados.

Também foi verificado o comportamento da persistência após fechar e abrir novamente o aplicativo.

**Resultado observado:** os dados cadastrados permanecem armazenados após o encerramento e a reabertura do aplicativo, demonstrando o funcionamento da persistência local com Room.

A demonstração em vídeo apresenta o funcionamento do aplicativo e suas principais funcionalidades.

---

## 🛠️ Tecnologias utilizadas

- **Kotlin:** linguagem de programação.
- **Android Studio:** ambiente de desenvolvimento.
- **Jetpack Compose:** construção das interfaces.
- **Material 3:** componentes visuais.
- **Room:** persistência local de dados.
- **SQLite:** armazenamento do banco de dados.
- **KSP:** processamento das anotações do Room.
- **Coroutines:** execução de operações assíncronas.
- **Navigation Compose:** navegação entre telas.
- **ViewModel e Lifecycle:** suporte ao gerenciamento do ciclo de vida da interface.
- **Arquitetura MVVM:** separação em camadas (View, ViewModel, Repository e Model).
- **Flow e StateFlow:** atualização automática da interface quando os dados do Room mudam.

---

## ▶️ Como executar o projeto

### Pré-requisitos

- Android Studio instalado.
- SDK Android 35.
- Dispositivo Android ou emulador com API 26 ou superior.

### Passos para execução

1. Clone o repositório:

   ```bash
   git clone URL_DO_REPOSITORIO
   ```

2. Abra o projeto no Android Studio.

3. Aguarde a sincronização do Gradle e o download das dependências.

4. Selecione um emulador ou conecte um dispositivo Android.

5. Execute o aplicativo pelo botão **Run** do Android Studio.

---

## 🎥 Demonstração do aplicativo

O vídeo abaixo apresenta o funcionamento do Viajaí, demonstrando suas principais funcionalidades e o comportamento da persistência de dados.

**Vídeo de demonstração:**  
https://drive.google.com/file/d/12I-5NOh7JItBI3p0qnywtjgC6FGj2zLd/view?usp=drive_link

---

## 🔗 Repositório

https://github.com/alvespedroo/Viajai_Room

---

# Respostas sobre o desenvolvimento do Viajaí

### 1. Como estava o projeto no Trabalho 2, e o que mudou para chegar até aqui?

No Trabalho 2, o Viajaí já possuía sua estrutura principal, com as telas, a navegação e as funcionalidades de planejamento de viagens. Porém, boa parte das informações era mantida em listas e estados da própria aplicação, utilizando recursos como `mutableStateListOf`.

Para chegar à versão atual, implementamos a persistência de dados utilizando Room. Com isso, as informações passaram a ser armazenadas em um banco de dados local, permitindo que viagens, checklists, despesas, avaliações e outros registros continuem disponíveis mesmo depois que o aplicativo é fechado.

Também organizamos o acesso aos dados por meio de Entities, DAOs e uma classe Database, além de adaptar as telas para carregar e atualizar as informações do banco.

### 2. Quais Entity, DAO e Database foram criados e por que escolheram esses campos para cada Entity?

Criamos sete Entities, cada uma representando um conjunto de informações necessário para o funcionamento do aplicativo:

- **ViagemEntity:** armazena o ID, origem, destino, data, orçamento e status de conclusão da viagem. Esses campos permitem identificar cada viagem, apresentar suas informações e diferenciar viagens em andamento das finalizadas.
- **ChecklistEntity:** armazena o ID, categoria, nome do item, status de marcação e ID da viagem relacionada. Assim, cada item pode ser identificado, organizado por categoria e associado à viagem correspondente.
- **BagagemEntity:** armazena o ID, nome do item, quantidade e status de preparação. Esses campos permitem controlar os objetos que precisam ser levados.
- **DespesaEntity:** armazena o ID, descrição, categoria, valor e ID da viagem. Dessa forma, conseguimos registrar gastos individualmente e relacioná-los à viagem correspondente.
- **LugarSalvoEntity:** armazena o ID do registro e o identificador do local salvo, permitindo manter uma lista de lugares favoritos.
- **AvaliacaoEntity:** armazena o ID, identificador do local, autor, comentário, nota, quantidade de curtidas e informações sobre a avaliação do próprio usuário. Esses campos permitem registrar e gerenciar as avaliações.
- **PerfilUsuarioEntity:** armazena o ID e o nome do usuário, mantendo as informações básicas do perfil.

Para acessar essas entidades, criamos sete DAOs:

`ViagemDao`, `ChecklistDao`, `BagagemDao`, `DespesaDao`, `LugarSalvoDao`, `AvaliacaoDao` e `PerfilUsuarioDao`.

Também criamos a classe **AppDatabase**, responsável por reunir as entidades, disponibilizar os DAOs e configurar o banco de dados Room. Ela utiliza o padrão Singleton para evitar a criação desnecessária de várias instâncias do banco.

### 3. Como decidiram estruturar as queries do DAO? Quais métodos existem além dos quatro básicos?

Primeiro, implementamos as operações básicas de CRUD para permitir inserir, consultar, atualizar e excluir registros.

Depois, analisamos as necessidades de cada funcionalidade do aplicativo e criamos consultas específicas para evitar carregar informações desnecessárias e facilitar a organização dos dados.

Alguns exemplos são:

- **ViagemDao:** `buscarPorId()` permite recuperar uma viagem específica, enquanto `buscarPorStatus()` separa as viagens concluídas das que ainda estão em andamento.
- **ChecklistDao:** `buscarPorViagem()` retorna somente os itens relacionados à viagem selecionada. Também existe `deletarCategoria()`, que permite excluir os itens de uma categoria específica daquela viagem.
- **DespesaDao:** `buscarPorViagem()` permite consultar os gastos de uma viagem sem misturá-los com os de outras.
- **AvaliacaoDao:** `buscarPorLugar()` consulta as avaliações de um local específico e `buscarMinhas()` retorna as avaliações feitas pelo usuário.
- **LugarSalvoDao:** `buscarPorLugarId()` verifica se determinado local já está salvo.
- **PerfilUsuarioDao:** possui métodos para salvar e consultar as informações do perfil.

A escolha dessas consultas foi feita pensando nas funcionalidades reais do Viajaí, principalmente na necessidade de separar os dados de cada viagem e facilitar a recuperação de informações específicas.

### 4. Qual foi a dificuldade em migrar de mutableStateListOf para Room? O que quebrou no caminho e como resolveram?

A principal mudança foi adaptar a forma como os dados eram armazenados e recuperados. Antes, muitas informações ficavam diretamente nas listas de estado da aplicação. Com Room, foi necessário criar as entidades, os DAOs e adaptar as funções para realizar operações no banco de dados.

Um dos desafios foi garantir que as telas continuassem atualizando corretamente depois das operações no banco. Como as consultas são executadas de forma assíncrona, foi necessário organizar as corrotinas e atualizar os estados da interface após o retorno dos dados.

Também utilizamos `LaunchedEffect` para carregar as informações quando determinadas telas são abertas e blocos `try/catch` para tratar possíveis erros durante as operações.

Não foi necessário eliminar completamente o `mutableStateListOf`, pois ele ainda pode ser utilizado para manter dados temporários e atualizar a interface. A diferença é que agora o Room é responsável por manter os dados persistidos.

### 5. Algum integrante da dupla teve dificuldade em algum ponto? Como resolveram?

Sim. Durante o desenvolvimento, tive dificuldade em implementar a interação de arrastar um item da checklist para o lado e exibir o texto "Excluir", indicando a possibilidade de remover aquele item.

Tentei ajustar o comportamento visual e a interação, mas estava encontrando dificuldades para fazer o texto aparecer corretamente durante o gesto de arrastar. Minha dupla conseguiu implementar essa parte e me ajudou a resolver o problema.

Esse momento mostrou a importância de trabalharmos em conjunto, compartilhando conhecimentos e ajudando um ao outro quando surgem dificuldades. Assim, conseguimos avançar no desenvolvimento e concluir a funcionalidade.

---

# 🧱 Trabalho 4 — Arquitetura MVVM

No Trabalho 4 **não criamos nenhuma tela, campo ou funcionalidade nova**. O objetivo foi pegar o app que já funcionava com Room (Trabalho 3) e reorganizá-lo em **MVVM (Model — View — ViewModel)**, separando a interface, o estado da tela e o acesso aos dados em camadas bem definidas. É um trabalho de refatoração: o app precisa continuar fazendo exatamente o que fazia antes.

## 🗺️ Visão geral das camadas

```
   ┌─────────────────────────────────────────────────────────────┐
   │  VIEW  (pacote ui)                                          │
   │  Telas @Composable: só leem o uiState e chamam métodos      │
   └──────────────────────────┬──────────────────────────────────┘
                              │  collectAsState()  /  chamadas de ação
   ┌──────────────────────────▼──────────────────────────────────┐
   │  VIEWMODEL  (pacote viewmodel)                              │
   │  UiState + _uiState (privado) / uiState (StateFlow público) │
   │  viewModelScope.launch { ... }                              │
   └──────────────────────────┬──────────────────────────────────┘
                              │
   ┌──────────────────────────▼──────────────────────────────────┐
   │  REPOSITORY  (pacote data.repository)                       │
   │  Única camada que conversa com o DAO                        │
   └──────────────────────────┬──────────────────────────────────┘
                              │
   ┌──────────────────────────▼──────────────────────────────────┐
   │  MODEL  (pacote data)                                       │
   │  Entities, DAOs (com Flow), AppDatabase (Room) e AppContainer│
   └─────────────────────────────────────────────────────────────┘
```

## 📁 Estrutura de pacotes

```
com.example.viajai_maf
├── data
│   ├── RoomDatabase.kt          # Entities, DAOs e AppDatabase
│   ├── AppContainer.kt          # monta o banco e entrega os Repositories
│   └── repository
│       ├── ViagemRepository.kt
│       ├── ChecklistRepository.kt
│       ├── BagagemRepository.kt
│       ├── DespesaRepository.kt
│       ├── LugarSalvoRepository.kt
│       ├── AvaliacaoRepository.kt
│       └── PerfilUsuarioRepository.kt
├── viewmodel
│   ├── ViagemViewModel.kt       # ViagemUiState + ViewModel + Factory
│   ├── ChecklistViewModel.kt
│   ├── BagagemViewModel.kt
│   ├── DespesaViewModel.kt
│   ├── PerfilViewModel.kt
│   ├── AvaliacaoViewModel.kt
│   └── ViewModelUtils.kt        # funções auxiliares (executar / observar)
└── ui
    ├── MainActivity.kt
    ├── AppNavigation.kt
    ├── ViajaiDados.kt           # liga as telas aos ViewModels
    ├── ViajaiHomeScreen.kt
    ├── ViajaiMAFScreens.kt      # checklist, bagagem, gastos, detalhes e perfil
    ├── ViajaiDestinosScreen.kt
    ├── PesquisarScreen.kt
    ├── ComentariosReviewScreen.kt
    ├── EditarViagemScreen.kt
    └── theme                    # Color, Theme e Type
```

## 🔄 Como um dado percorre as camadas (exemplo: adicionar item na bagagem)

1. O usuário toca em **Adicionar** na `BagagemViajaiScreen` (**View**), que chama `dados.adicionarBagagem(...)`.
2. A chamada chega ao `BagagemViewModel.adicionar(...)`, que abre um `viewModelScope.launch` e chama o Repository.
3. O `BagagemRepository` repassa para o `BagagemDao.inserir(...)` (**Model**), e o Room grava no banco.
4. Como `buscarTodos()` do DAO retorna um `Flow`, o **Room emite sozinho a lista atualizada**.
5. O ViewModel recebe a nova lista e atualiza o `_uiState`. A tela, que coleta o `uiState`, se redesenha automaticamente.

Ou seja: a tela **não precisa mais buscar os dados de novo** depois de inserir, atualizar ou remover.

---

# Respostas sobre o Trabalho 4

### 1. Como estava o projeto no Trabalho 3, e o que mudou na reorganização?

No Trabalho 3 o Room já funcionava e os dados sobreviviam ao fechar o app, mas tudo estava misturado. As telas (`@Composable`) falavam direto com os DAOs, criavam o próprio `CoroutineScope` com `Dispatchers.IO`, e a classe `ViajaiDados` guardava as listas em `mutableStateListOf` e precisava ser avisada para recarregar (`recarregarViagens()`, `recarregarChecklist()` etc.) depois de cada operação. Cada tela também usava um `LaunchedEffect` só para buscar os dados de novo.

Na reorganização:

- o código foi dividido em **pacotes** (`ui`, `data`, `data.repository`, `viewmodel`);
- foram criados **Repositories**, e só eles falam com os DAOs;
- o `buscarTodos()` dos DAOs passou a retornar **`Flow`**, então a lista se atualiza sozinha;
- cada área do app ganhou um **ViewModel** com um **UiState**, usando `viewModelScope` no lugar do `CoroutineScope` solto;
- os ViewModels são criados por **ViewModelFactory**, que recebe o Repository;
- as telas deixaram de ter banco, DAO e `Dispatchers`: só leem o estado e chamam métodos.

As Entities, os DAOs e as telas continuam os mesmos: nenhum campo, tela ou funcionalidade foi adicionado.

### 2. Como o trio decidiu dividir os pacotes? O que foi para `ui`, `data`, `data.repository` e `viewmodel`?

| Pacote | O que ficou nele | Por quê |
|---|---|---|
| `ui` | `MainActivity`, `AppNavigation`, todas as telas `@Composable` e o `ViajaiDados` (que liga as telas aos ViewModels) | Tudo que desenha ou navega. Nada de banco aqui. |
| `data` | `RoomDatabase.kt` (7 Entities, 7 DAOs e `AppDatabase`) e o `AppContainer` | É o Model: como os dados são guardados. O `AppContainer` monta o banco e entrega os Repositories prontos. |
| `data.repository` | 7 Repositories, um por Entity | Camada intermediária entre o ViewModel e o DAO. |
| `viewmodel` | 6 ViewModels, cada um com seu `UiState` e sua `ViewModelFactory`, e o `ViewModelUtils` | Guarda o estado das telas e chama os Repositories. |

A pasta `ui/theme` foi mantida como estava. Os arquivos de banco ficaram em `data` do jeito que já existiam no Trabalho 3, só com a troca para `Flow` no `buscarTodos()`.

Um detalhe: o `ViajaiDados` ficou em `ui` porque é ele que pega os ViewModels (com `viewModel(factory = ...)`), coleta o `uiState` com `collectAsState()` e entrega os dados para as telas. Assim as telas continuaram praticamente iguais às do Trabalho 3. Dentro dele **não existe** banco, DAO, `CoroutineScope` nem `Dispatchers`.

### 3. Por que o Repository existe? O que ele resolve que não existia antes?

Antes, quem chamava o DAO era a própria tela. Isso deixava a interface dependente de **como** os dados são guardados: se o armazenamento mudasse, teríamos que mexer em várias telas.

Agora só o Repository conversa com o DAO, e só o ViewModel conversa com o Repository. O que isso resolve:

- **Uma única fonte de verdade** para os dados de cada Entity.
- **Troca de origem sem quebrar o resto:** se um dia os dados vierem de uma API, só o Repository muda. ViewModel e telas continuam iguais.
- **Telas mais simples:** elas não sabem que existe Room.

Nossos Repositories repassam as chamadas para o DAO (`buscarTodos`, `inserir`, `atualizar`, `deletar` e as consultas específicas, como `buscarPorLugar` e `deletarCategoria`). O `AppContainer` cria todos eles em um lugar só.

### 4. O que tem dentro do UiState de cada tela, e por que escolheram esses campos?

Seguindo o padrão do enunciado, cada ViewModel tem um `_uiState` privado (`MutableStateFlow`, só o ViewModel altera) e um `uiState` público (`StateFlow`, somente leitura para a tela).

| UiState | Campos | Por que esses campos |
|---|---|---|
| `ViagemUiState` | `viagens`, `selecionadaId`, `carregando`, `erro` | Lista de viagens vinda do banco e qual está selecionada. A viagem "atual" e a separação entre em planejamento e concluídas são calculadas a partir da lista. |
| `ChecklistUiState` | `itens`, `carregando`, `erro` | Todos os itens de checklist; a tela filtra pelos itens da viagem selecionada. |
| `BagagemUiState` | `itens`, `carregando`, `erro` | Itens da mala. |
| `DespesaUiState` | `itens`, `carregando`, `erro` | Gastos registrados. A tela soma e filtra por viagem. |
| `PerfilUiState` | `nome`, `lugaresSalvos`, `carregando`, `erro` | Nome do usuário e ids dos lugares salvos (tela de Perfil). |
| `AvaliacaoUiState` | `avaliacoes`, `carregando`, `erro` | Todas as avaliações; a tela filtra por lugar ou pelas "minhas". |

Todos têm **`carregando`**, para saber se a primeira leitura do banco ainda não terminou, e **`erro`**, para registrar se o `Flow` falhar. A lista principal é o que a tela precisa para se desenhar. Os dados de formulário (texto digitado) continuam como estado local da tela, porque só importam enquanto a tela está aberta.

### 5. Alguma funcionalidade quebrou durante a reorganização? Como encontraram e corrigiram?

Sim. Depois de mover os arquivos para os novos pacotes, **o app fechava sozinho assim que abria**.

- **Como encontramos:** o código tinha compilado, então o erro estava em outro lugar. A suspeita foi o `AndroidManifest.xml`.
- **Causa:** o manifest ainda dizia `android:name=".MainActivity"`, mas a `MainActivity` tinha ido para o pacote `ui`. O Android procurava a classe no lugar antigo, não encontrava e o app fechava.
- **Correção:** trocar para `android:name=".ui.MainActivity"`.

O aprendizado é que mover uma classe não afeta só o código Kotlin: tudo que referencia o nome dela por texto (como o manifest) também precisa ser atualizado.

> ⚠️ **PREENCHER (se houver):** se vocês tiveram outro problema (por exemplo, uma tela que não atualizava ou uma importação quebrada), acrescentem aqui o que era, como acharam e como resolveram.

### 6. Algum integrante do trio teve dificuldade em algum ponto? Como resolveram?

> ⚠️ **PREENCHER:** descrevam com suas palavras quem teve dificuldade, em qual parte (por exemplo: entender o `Flow`, o `UiState` ou a `ViewModelFactory`) e como o trio resolveu.

---

## 🧪 Testes do Trabalho 4

O objetivo foi garantir que o app continua fazendo tudo que fazia no Trabalho 3. Roteiro de testes:

- [x] Planejar uma nova viagem (e o checklist padrão ser criado)
- [x] Marcar, editar, adicionar e excluir itens do checklist (inclusive arrastando para excluir)
- [x] Adicionar, marcar e remover itens da bagagem
- [x] Adicionar e remover gastos, com total e saldo atualizando
- [x] Salvar e remover lugares nos favoritos
- [x] Publicar, editar, curtir e excluir avaliações
- [x] Alterar o nome do perfil
- [x] Concluir uma viagem (com e sem itens pendentes no checklist)
- [x] Fechar e abrir o app e confirmar que os dados continuam lá

## 📝 Observações

- A única ocorrência de `CoroutineScope` nas telas é o `rememberCoroutineScope()` do gesto de arrastar para excluir um item do checklist (`ViajaiMAFScreens.kt`). Ele serve só para a **animação** (`Animatable`) e **não acessa o banco**.
- O `buscarTodos()` dos DAOs que listam tabelas inteiras retorna `Flow`. A leitura do nome do perfil (`PerfilUsuarioDao.buscar()`) continua sendo uma consulta única (`suspend`), pois lê um único registro.

---

## 📌 Considerações finais

O desenvolvimento do Viajaí permitiu aplicar conceitos de desenvolvimento Android utilizando Kotlin e Jetpack Compose, além de trabalhar com persistência local de dados por meio do Room.

A implementação possibilitou organizar informações de diferentes viagens, realizar operações CRUD, desenvolver consultas específicas e manter os dados armazenados mesmo após o fechamento do aplicativo.

O projeto busca oferecer uma experiência prática e organizada para o planejamento de viagens, reunindo em um único aplicativo recursos de checklist, controle financeiro, pesquisa de locais e avaliações.

Com o Trabalho 4, o projeto também passou a seguir a arquitetura MVVM, o que deixa o código mais organizado e mais fácil de manter e evoluir.

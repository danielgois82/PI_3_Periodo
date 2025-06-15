# Projeto MPI (Monitoramento do Programa de Integridade)

## Visão Geral do Projeto

### Descrição 
Este é um projeto para disposivitos móveis para ser usado na plataforma Android e o objetivo é a gestão administrativa de um programa de integridade que contém pilares, subpilares, ações e atividades com seus respectivos usuários que usarão o sistema.


### Funcionalidades Principais 
* CRUD de entidades: Os usuários, a depender dos níveis de acesso, podem cadastrar, consultar, editar e excluir: Pilares, Subpilares, Ações e Atividades.
* Aprovação de atividades ou ações: Os usuários do tipo "Coordenador" podem aprovar atividades ou ações, oficializando a aptidão delas para que sejam iniciadas.
* Finalização de atividades ou ações: Os usuários do tipo "Coordenador" podem finalizar atividades ou ações, oficializando a conclusão delas.
* Geração de alertas: O sistema notifica os usuários sobre datas críticas de cada atividade ou ação, sempre que faltam 30, 15 e 7 dias ou quando o prazo já foi passado.
* Geração de relatórios: O sistema permite a geração de relatórios finais consolidados por pilar, prazo e responsável.
* Exportação de relatórios: O sistema permite que os relatórios sejam exportados em formato PDF. 
* Geração de Dashboard: O sistema permite ao usuário visualizar o painel com métricas relaciondas ao progresso dos pialres e pendências relaciondas as ações.



### Público-alvo

O aplicativo foi desenvolvido para analistas, coordenadores e gestores envolvidos no contexto de Complaince da Fecomércio-PE.
.
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Configurando o ambiente de desenvolvimento

### Requisitos

Versão mínima de SDK: 29

### Dependências

* ("com.itextpdf:itextg:5.5.10")
* ("androidx.activity:activity-ktx:1.8.0")
* ("com.github.PhilJay:MPAndroidChart:v3.1.0")
* ("androidx.core:core-ktx:1.12.0")
* ("androidx.appcompat:appcompat:1.6.1")
* ("com.google.android.material:material:1.10.0")
* ("androidx.constraintlayout:constraintlayout:2.1.4")
* (libs.androidx.activity)
* testImplementation("junit:junit:4.13.2")
* androidTestImplementation("androidx.test.ext:junit:1.1.5")
* androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

### Clonagem e execução do app

#### Geração do APK

Passo a passo: Build > Generate App Bundles or APKs > Generate APKs

#### Execução em dispositivo físico Via USB

É necessário estar com o modo desenvolvedor(tocando no build number 8 vezes consecutivas em configurações) e a depuração USB do dispositivo(opção que é ativada em configurações) ativados.

Estando com as duas configurações ativas, basta conectar seu dispositivo móvel no computador e selecionar na opção 'Run App'.

#### Execução no emulador

No Android Studio, vá em Tools > Device Manager.

Clique em Create device e escolha um dispositivo (ex: Pixel 6). Clique em Next.

Selecione uma "System Image" compatível com a API Level 29 ou superior (ex: API Level 34). Se não tiver, clique em Download. Clique em Next.

Clique em Finish. Seu emulador aparecerá na lista.

Clique no botão de "Play" (triângulo verde) ao lado do nome do seu emulador no Device Manager para iniciá-lo.

Com o emulador rodando, selecione-o no menu suspenso de dispositivos no Android Studio e clique no botão "Run 'app'".
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Estrutura de pastas e pacotes
```
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── AndroidManifest.xml           <-- Arquivo de manifesto: Define componentes do app (activities, services, permissions), pacote, versão.
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── example/
│   │   │   │           └── mpi/              <-- Pacote raiz da lógica de negócio e UI do app.
│   │   │   │               ├── data/         <-- Camada de dados: Modelos e classes de persistência.
│   │   │   │               │   ├── Acao.kt                 <-- Classe de modelo para Ação.
│   │   │   │               │   ├── AprovacaoItem.kt        <-- Classe de modelo para itens de aprovação.
│   │   │   │               │   ├── Atividade.kt            <-- Classe de modelo para Atividade.
│   │   │   │               │   ├── Calendario.kt           <-- Classe de modelo para Calendário.
│   │   │   │               │   ├── DatabaseHelper.kt       <-- Ajuda a gerenciar o banco de dados SQLite (criação, upgrades, etc.).
│   │   │   │               │   ├── FinalizacaoItem.kt      <-- Classe de modelo para itens de finalização.
│   │   │   │               │   ├── Notificacao.kt          <-- Classe de modelo para Notificação.
│   │   │   │               │   ├── PercentualAtividade.kt  <-- Classe de modelo para percentuais de atividades.
│   │   │   │               │   ├── Pilar.kt                <-- Classe de modelo para Pilar (objetivo maior).
│   │   │   │               │   ├── Subpilar.kt             <-- Classe de modelo para Subpilar (divisão do pilar).
│   │   │   │               │   ├── TipoUsuario.kt          <-- Classe de modelo para tipos de usuário.
│   │   │   │               │   └── Usuario.kt              <-- Classe de modelo para Usuário.
│   │   │   │               ├── repository/   <-- Camada de repositório: Abstração do acesso a dados.
│   │   │   │               │   ├── AcaoRepository.kt             <-- Lógicas para operações CRUD de Ação no banco de dados e realização de consultas.
│   │   │   │               │   ├── AtividadeRepository.kt        <-- Lógicas para operações CRUD de Atividade no banco de dados e realização de consultas.
│   │   │   │               │   ├── CalendarioRepository.kt       <-- Lógicas para operações para tabela Calendário e realização de consultas.
│   │   │   │               │   ├── NotificacaoRepository.kt      <-- Lógicas para operações para tabela de Notificação e realização de consultas.
│   │   │   │               │   ├── PercentualAtividadeRepository.kt <-- Lógicas para atualizações e consultas relaciondas a tabela PercentualAtividade.
│   │   │   │               │   ├── PilarRepository.kt            <-- Lógicas para operações CRUD de Pilar no banco de dados e realização de consultas.
│   │   │   │               │   ├── SubpilarRepository.kt         <-- Lógicas para operações CRUD de Subpilar no banco de dados e realização de consultas.
│   │   │   │               │   ├── TipoUsuarioRepository.kt      <-- Lógicas e consultas para a tabela de TipoUsuario.
│   │   │   │               │   └── UsuarioRepository.kt          <-- Lógicas e consultas para a tabela de Usuário.
│   │   │   │               ├── ui/           <-- Camada de Interface do Usuário (UI): Activities, Fragments, Adapters.
│   │   │   │               │   ├── acao/               <-- Telas e lógica de UI relacionadas a Ações.
│   │   │   │               │   │   ├── AcaoActivity.kt           <-- Activity principal para Ações(Listagem).
│   │   │   │               │   │   ├── AcaoAdapter.kt            <-- Adaptador para exibir listas de Ações.
│   │   │   │               │   │   ├── AcaoItemFragment.kt       <-- Fragmento para um item individual de Ação (reutilizável).
│   │   │   │               │   │   ├── CadastroAcao.kt           <-- Activity para cadastro de novas Ações.
│   │   │   │               │   │   └── EditarAcaoActivity.kt     <-- Activity para edição de Ações existentes.
│   │   │   │               │   ├── aprovacao/          <-- Telas e lógica de UI relacionadas a Aprovações.
│   │   │   │               │   │   ├── AprovacaoAcaoAdapter.kt      <-- Adaptador para ações em aprovação.
│   │   │   │               │   │   ├── AprovacaoActivity.kt         <-- Activity principal para Aprovações(Listagem).
│   │   │   │               │   │   ├── AprovacaoAtividadeAdapter.kt <-- Adaptador para atividades em aprovação.
│   │   │   │               │   │   └── AprovacaoMistaAdapter.kt     <-- Adaptador para listas mistas de aprovação.
│   │   │   │               │   ├── atividade/          <-- Telas e lógica de UI relacionadas a Atividades.
│   │   │   │               │   │   ├── AtividadeActivity.kt        <-- Activity principal para Atividades(Listagem).
│   │   │   │               │   │   ├── AtividadeAdapter.kt         <-- Adaptador para exibir listas de Atividades.
│   │   │   │               │   │   ├── AtividadeItemFragment.kt    <-- Fragmento para um item individual de Atividade.
│   │   │   │               │   │   ├── CadastroAtividade.kt        <-- Activity para cadastro de novas Atividades.
│   │   │   │               │   │   └── EditarAtividadeActivity.kt  <-- Activity para edição de Atividades existentes.
│   │   │   │               │   ├── dashboard/          <-- Telas relacionadas aos paineis de dashboard.
│   │   │   │               │   │   ├── DashboardActivity.kt      <-- Activity principal do Dashboard.
│   │   │   │               │   │   ├── Opcao1Activity.kt         <-- Activity para uma opção específica do Dashboard.
│   │   │   │               │   │   ├── Opcao2Activity.kt         <-- Activity para outra opção do Dashboard.
│   │   │   │               │   │   └── Opcao3Activity.kt         <-- Activity para mais uma opção do Dashboard.
│   │   │   │               │   ├── finalizacao/        <-- Telas e lógica de UI relacionadas a Finalizações.
│   │   │   │               │   │   ├── FinalizacaoAcaoAdapter.kt      <-- Adaptador para ações em finalização.
│   │   │   │               │   │   ├── FinalizacaoActivity.kt         <-- Activity principal para Finalizações(Listagem).
│   │   │   │               │   │   ├── FinalizacaoAtividadeAdapter.kt <-- Adaptador para atividades em finalização.
│   │   │   │               │   │   └── FinalizacaoMistaAdapter.kt     <-- Adaptador para listas mistas de finalização.
│   │   │   │               │   ├── notificacao/        <-- Telas e lógica de UI relacionadas a Notificações.
│   │   │   │               │   │   ├── NotificacaoActivity.kt      <-- Activity principal para Notificações(Listagem).
│   │   │   │               │   │   ├── NotificacaoAdapter.kt       <-- Adaptador para exibir listas de Notificações.
│   │   │   │               │   │   ├── NotificacaoItemFragment.kt  <-- Fragmento para um item individual de Notificação.
│   │   │   │               │   │   └── NotificacaoService.kt       <-- Serviço para gerenciar notificações em segundo plano.
│   │   │   │               │   ├── pilar/              <-- Telas e lógica de UI relacionadas a Pilares.
│   │   │   │               │   │   ├── CadastroPilar.kt          <-- Activity para cadastro de novos Pilares.
│   │   │   │               │   │   ├── EditarPilarActivity.kt    <-- Activity para edição de Pilares existentes.
│   │   │   │               │   │   ├── PilarActivity.kt          <-- Activity principal para Pilares(Listagem).
│   │   │   │               │   │   ├── PilarAdapter.kt           <-- Adaptador para exibir listas de Pilares.
│   │   │   │               │   │   └── PilarItemFragment.kt      <-- Fragmento para um item individual de Pilar.
│   │   │   │               │   ├── relatorio/          <-- Telas e lógica de UI relacionadas a Relatórios.
│   │   │   │               │   │   ├── RelatorioActivity.kt        <-- Activity principal para Relatórios.
│   │   │   │               │   │   ├── RelatorioOKRActivity.kt     <-- Activity para relatório específico de OKRs (Objectives and Key Results).
│   │   │   │               │   │   └── RelatorioPilarActivity.kt   <-- Activity para relatório específico de Pilares.
│   │   │   │               │   ├── subpilar/           <-- Telas e lógica de UI relacionadas a Subpilares.
│   │   │   │               │   │   ├── CadastroSubpilar.kt         <-- Activity para cadastro de novos Subpilares.
│   │   │   │               │   │   ├── EditarSubpilarActivity.kt   <-- Activity para edição de Subpilares existentes.
│   │   │   │               │   │   ├── SubpilarActivity.kt         <-- Activity principal para Subpilares(Listagem).
│   │   │   │               │   │   ├── SubpilarAdapter.kt          <-- Adaptador para exibir listas de Subpilares.
│   │   │   │               │   │   └── SubpilarItemFragment.kt     <-- Fragmento para um item individual de Subpilar.
│   │   │   │               │   ├── LoginActivity.kt      <-- Activity para a tela de login.
│   │   │   │               │   ├── MenuActivity.kt       <-- Activity para o menu principal do aplicativo.
│   │   │   │               │   └── PercentualActivity.kt <-- Activity relacionada a exibição/inserção de percentuais.
│   │   │   │               └── util/           <-- Utilitários gerais e classes de suporte.
│   │   │   │                   └── DateUtils.kt          <-- Classe utilitária para manipulação e formatação de datas.
│   │   │   └── res/                          <-- Recursos da aplicação (layout, imagens, strings, etc.).
│   │   │       ├── drawable/                 <-- Desenhaveis (imagens, ícones, formas XML).
│   │   │       ├── font/                     <-- Fontes personalizadas.
│   │   │       ├── layout/                   <-- Arquivos XML que definem a interface do usuário das Activities/Fragments. Contém todos os layouts visuais do app.
│   │   │       ├── mipmap/                   <-- Ícones do aplicativo em diferentes densidades.
│   │   │       ├── values/                   <-- Valores como strings (textos), cores, estilos e temas.
│   │   │       └── xml/                      <-- Arquivos XML diversos, como preferências.
│   │   └── androidTest/                  <-- Código para testes instrumentados (executados em um dispositivo/emulador).
│   │       └── com/example/mpi/
│   │           └── (testes relacionados)
│   └── test/                       <-- Código para testes unitários (executados na JVM local).
│       └── com/example/mpi/
│           └── (testes relacionados)
├── build.gradle.kts (app)          <-- Arquivo de configuração do módulo "app": define SDKs, dependências, etc.
└── build.gradle.kts (project)      <-- Arquivo de configuração do projeto: define repositórios, plugins globais.
```
.
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Principais bibliotecas e Frameworks utilizados.

* com.github.PhilJay:MPAndroidChart v3.1.0 : Biblioteca para a geração dos Dashboards; 
* com.itextpdf:itextg v 5.5.10 : Biblioteca para a geração dos arquivos PDFs. 
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Fluxos de navegação

Os fluxos de nagevação estão melhor e visualmente descritivos no link a seguir: https://senacpernambuco-my.sharepoint.com/:w:/g/personal/daniel_gois_edu_pe_senac_br/EXhlGOquqdVFm-N3lCdUBg0BFWBMVn4fFM309sdA4Qmi0A?e=Xn1SBc

## Tecnologias utilizadas:

#### IDE Android Studio
#### Linguagem de programação Kotlin
#### Versionamento de código GIT
#### Banco de dados SQLite
#### Prototípo de telas via Figma
#### Diagrama de classes com o www.draw.io

## Instruções de instalação e execução:

#### Instalação via arquivo .apk pois não será colocado na loja do google, execução igual a quaisquer aplicativos para Android.

## Estrutura do projeto e principais arquivos: (a definir).

## Contribuições dos membros do grupo:

#### Todos os integrantes do grupo contribuíram em todas as fases do projeto, desde o refinamento dos requisitos, modelagem do banco de dados, prototípos, design, codificação e documentação.

## Links para demais artefatos entregues:

#### Paleta de cores: 
https://senacpernambuco-my.sharepoint.com/:w:/g/personal/daniel_gois_edu_pe_senac_br/EXtOKUD6LpFDhzOprnIil0IBuWPKkljHlRiuCDuaW_J3xg?e=lRGvPy
#### Documento de requisitos, história do usuário, Diagrama de classes, backlog do produto, quadro Kanban e protoípo de baixa fidalidade:
https://senacpernambuco-my.sharepoint.com/:w:/g/personal/daniel_gois_edu_pe_senac_br/EZRCW2Ujn61DiW8Vy_oPE78BeREZH2MF4HFtOeMmTZz5wg

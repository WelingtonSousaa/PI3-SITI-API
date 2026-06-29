# SITI-API

API REST do projeto SITI (Sistema Inteligente de Transporte Intercampi), desenvolvida com Java 21, Spring Boot 3.5 e MySQL. A aplicação centraliza funcionalidades de cadastro, autenticação e gerenciamento de frotas e itinerários, fornecendo suporte para os aplicativos do Passageiro, do Motorista e do Painel Administrativo.

Nesta versão mais recente, a API conta com **Inteligência de Roteirização**, disparo automático de e-mails, gerenciamento de filas de espera e uma robusta interface interativa via **Swagger UI**.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 21
- **Framework Principal**: Spring Boot 3.5.13
- **Persistência**: Spring JDBC (Template) + MySQL / H2 Database (Testes)
- **Cache**: Spring Cache (Caffeine Cache)
- **Agendamento**: Spring Scheduling (Cron Jobs automáticos)
- **Notificações**: Spring Mail (Envio de e-mails via SMTP)
- **Documentação**: Springdoc OpenAPI (Swagger UI)
- **Utilitários**: Lombok, DataFaker

---

## ⚙️ Passo a Passo de Execução (Como Rodar o Projeto)

### Pré-requisitos
* **Java 21** instalado nas variáveis de ambiente.
* **MySQL 8.0+** rodando localmente (com a pasta `bin` configurada no PATH do Windows).

### 1. Criar o Banco de Dados Local
O sistema depende de tabelas e procedimentos armazenados (*Stored Procedures*) nativos do MySQL.
Abra seu terminal (PowerShell ou Git Bash) na pasta raiz do projeto e execute a importação dos arquivos SQL que estão na pasta `database/`.

**Se estiver usando PowerShell:**
```powershell
Get-Content database\create_db.sql | mysql -u root -p
```

**Se estiver usando Git Bash ou Linux/Mac:**
```bash
mysql -u root -p < database/create_db.sql
```
*(Nota: O MySQL pedirá sua senha local, basta digitá-la. Se suas credenciais forem diferentes de `root`/`root`, lembre-se de atualizá-las no arquivo `src/main/resources/application-dev.yaml`).*

*(Nota 2: A importação do arquivo `procedures.sql` não é mais necessária, pois os Repositórios foram refatorados para utilizar JDBC padrão em vez de Stored Procedures do MySQL. Isso garante a compatibilidade universal com diferentes bancos de dados, inclusive para rodar nossos testes E2E localmente no H2).*

### 2. Rodar os Testes (E2E com H2 Database)
Para garantir que a API está funcionando perfeitamente, desenvolvemos uma suíte exaustiva de testes End-to-End (E2E) cobrindo **todos os endpoints e possibilidades (sucesso e falha)**. Eles não afetam seu banco de dados MySQL, pois rodam inteiramente em memória usando o banco H2.

Para rodar todos os testes automatizados da aplicação:

**No Windows (PowerShell/CMD):**
```bash
.\mvnw.cmd test
```

**No Linux/Mac:**
```bash
./mvnw test
```

### 3. Rodar a Aplicação Spring Boot
Não é necessário ter o Maven instalado globalmente, pois o projeto usa o **Maven Wrapper**. No seu terminal, ainda na raiz do projeto, execute:

**No Windows (PowerShell/CMD):**
```bash
.\mvnw.cmd spring-boot:run
```

**No Linux/Mac:**
```bash
./mvnw spring-boot:run
```
O Maven irá baixar as dependências, compilar o projeto e iniciar a API. Você verá nos logs a mensagem de que o Tomcat foi iniciado na porta `8080`.

### 4. Acessar o Swagger UI e Testar
Com a API rodando, você não precisa configurar o Postman. Acesse a interface interativa do Swagger no seu navegador:

🔗 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Aqui você poderá ver todos os endpoints, entender o que cada um espera e testá-los em tempo real.
* **Dica de Autenticação**: Para acessar as rotas protegidas pelo Swagger, crie um token simulado (ex: cadastre um usuário no `/auth/login` ou gere um access token) e utilize o botão verde **"Authorize"** no topo da página. Preencha seu *Bearer Token* e informe a *Role* correspondente (`ADMIN`, `USER` ou `DRIVE`).

---

## 🧠 Inteligência do Sistema e Lógicas Automáticas

A API possui um módulo avançado (Schedule) para otimizar as viagens diárias de forma totalmente autônoma:
* **Fechamento de Votação (RF010)**: Ao dar o horário limite (configurável pelo painel do Admin), o sistema encerra a captação de intenção de viagens para as rotas do dia.
* **Otimização de Paradas (RF011)**: Paradas sem nenhum aluno que manifestou interesse são sumariamente removidas da rota gerada para o Motorista.
* **Superlotação e Lista de Espera (RF012 / RF025)**: Se uma rota receber mais intenções de embarque do que a capacidade do ônibus, os alunos excedentes são movidos para uma Fila de Espera, e o Administrador recebe um Alerta Crítico por e-mail.
* **Lembretes Automáticos (RF024)**: Meia hora antes do encerramento das votações, alunos que ainda não confirmaram a presença recebem um lembrete via e-mail.

---

## 🔒 Regras de Segurança e Acesso

A API protege a maioria de seus endpoints exigindo os seguintes cabeçalhos nas requisições REST (configurados automaticamente no Swagger, mas obrigatórios via Front-end):

1. **`Authorization`**: Token de acesso JWT (Ex: `Bearer eyJhbGciOi...`).
2. **`Role`**: Papel do usuário logado (`ADMIN`, `DRIVE`, `USER`).

* **Auto-Provisionamento (Ambiente Dev)**: Tokens no padrão mock (ex: `mock-jwt-token-carlos@siti.com`) farão com que a API provisione o usuário automaticamente no banco de dados se ele não existir, facilitando testes sem dor de cabeça.

---

## 🐳 Executando com Docker (Alternativa Isolada)

Caso não queira configurar o MySQL ou o Java na sua máquina, você pode subir tudo via Docker.

1. **Faça o Build da Imagem**:
   ```bash
   docker build -t siti-api .
   ```
2. **Inicie o Container** (substitua com suas credenciais de banco):
   ```bash
   docker run --rm -p 8080:8080 -e DB_USER=root -e DB_PASSWORD=root siti-api
   ```

---

## 🔗 Links Úteis
* **Quadro de Tarefas / Documentação**: [GitHub Projects](https://github.com/users/r7melo/projects/12)

# SITI-API

API REST do projeto SITI (Sistema Inteligente de Transporte Intercampi), desenvolvida com Java 21, Spring Boot 3.5 e MySQL. A aplicação centraliza funcionalidades de cadastro, autenticação e gerenciamento de frotas e itinerários, fornecendo suporte para os aplicativos do Passageiro, do Motorista e do Painel Administrativo.

---

## 🔒 Regras Gerais e Cabeçalhos de Segurança

A API protege a maioria de seus endpoints exigindo os seguintes cabeçalhos nas requisições REST:

1. **`Authorization`**: Token JWT/Access Key de autenticação (Ex: `Bearer eyJhbGciOi...` ou tokens mock de desenvolvimento no formato `Bearer mock-jwt-token-<email>`).
2. **`Role`**: Papel do usuário logado (`ADMIN`, `DRIVE`, `USER`).

**Configuração de CORS**:
* **Origem permitida**: `http://localhost:5173`
* **Métodos**: `GET, POST, PUT, DELETE, OPTIONS`
* **Cabeçalhos permitidos**: `Content-Type, Authorization, Role`

---

## 🔑 Resolução de Roles e Auto-Provisionamento

* **Resolução de Roles Baseada em Banco**: O papel (Role) do usuário logado é determinado exclusivamente pela presença do seu ID nas tabelas correspondentes:
  * Se ID está em `administrators` -> papel `ADMIN`.
  * Se ID está em `drivers` -> papel `DRIVE`.
  * Se ID está em `passengers` -> papel `USER`.
* **Auto-Provisionamento de Testes**: Em ambiente de desenvolvimento, quando um token mockado (ex: `mock-jwt-token-carlos@siti.com`) é utilizado e o usuário não existe na base, a API provisiona automaticamente o usuário e seu registro de papel específico (com status `'Ativo'`) diretamente no banco de dados para agilizar os testes.

---

## 🛠️ Tecnologias

- Java 21
- Spring Boot 3.5.13
- Spring Web
- Spring JDBC (Template)
- Spring Cache (Caffeine Cache)
- MySQL / H2 Database (para testes)
- Lombok
- DataFaker (geração de dados nos testes)

---

## 📂 Estrutura do Projeto

```text
SITI-API/
+-- database/
|   +-- create_db.sql                   # Estrutura do banco e tabelas (MySQL)
|   +-- procedures.sql                   # Stored Procedures para otimização
|   +-- SITI-API.postman_collection.json # Coleção do Postman para testes manuais
+-- src/main/java/com/siti/sitiapi/
|   +-- configs/                         # CORS, Cache, interceptador de autenticação
|   +-- controller/                      # Rotas REST agrupadas por módulo
|   +-- dto/                             # Data Transfer Objects (DTOs)
|   +-- exception/                       # Tratamento centralizado de exceções
|   +-- model/                           # Entidades de domínio
|   +-- repository/                      # Acesso ao banco via JDBC e Procedures
|   +-- service/                         # Lógica e regras de negócio
+-- src/main/resources/
|   +-- application.yaml                 # Configuração padrão de profiles
|   +-- application-dev.yaml             # Configuração local (MySQL)
|   +-- application-prod.yaml            # Configuração de produção
+-- Dockerfile
+-- mvnw / mvnw.cmd                      # Maven Wrapper
+-- pom.xml
```

---

## ⚙️ Configuração e Inicialização Local

### Pré-requisitos
* Java 21+ instalado
* MySQL rodando localmente

### 1. Preparando o Banco de Dados
Crie a estrutura do banco e instale as stored procedures executando:
```bash
mysql -u root -p < database/create_db.sql
mysql -u root -p sitidb < database/procedures.sql
```
*Caso seu MySQL possua credenciais personalizadas, ajuste as configurações no arquivo [application-dev.yaml](file:///c:/Users/Romario Melo/IdeaProjects/SITI-API/src/main/resources/application-dev.yaml).*

### 2. Executando a Aplicação
Inicie a aplicação utilizando o Maven Wrapper:
```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux / macOS
./mvnw spring-boot:run
```
A API subirá na porta `8080` (acessível via `http://localhost:8080`).

### 3. Rodando os Testes Automatizados
Para garantir a integridade dos dados e o correto funcionamento dos módulos, execute a suite de testes:
```bash
.\mvnw.cmd test
```

---

## 📡 Endpoints da API

As rotas de `/auth/login` e `/users/register` são públicas. Todas as outras rotas exigem o envio dos cabeçalhos `Authorization` e `Role`.

### 🔑 1. Autenticação e Registro

#### Registrar Novo Usuário
* **POST** `/users/register`
* **Request Body**:
  ```json
  {
    "email": "estudante.siti@crateus.edu.br",
    "password": "senha",
    "identifierDocument": "123.456.789-00"
  }
  ```
* **Resposta Esperada (201 Created)**:
  ```json
  {
    "success": true,
    "message": "Cadastro enviado para homologação!"
  }
  ```

#### Login de Usuário
* **POST** `/auth/login`
* **Request Body**:
  ```json
  {
    "email": "estudante.siti@crateus.edu.br",
    "password": "senha"
  }
  ```
* **Resposta Esperada (200 OK)**:
  ```json
  {
    "token": "a1b2c3d4-e5f6...",
    "role": "USER",
    "user": {
      "id": 1,
      "email": "estudante.siti@crateus.edu.br",
      "role": "USER",
      "name": "Estudante SITI"
    }
  }
  ```

---

### 🛡️ 2. Módulo do Administrador (`ADMIN`)
Endpoints administrativos para homologação de usuários, gerenciamento de frotas, motoristas e avisos.

* **GET** `/admin/pending-homologations` - Lista cadastros com status `'Pendente'`.
* **POST** `/admin/homologate/{id}` - Aprova o cadastro de um usuário (altera o status para `'Ativo'`).
* **POST** `/admin/reject/{id}` - Reprova e remove o cadastro de um usuário.
* **GET** `/admin/routes` - Visualiza todas as rotas ativas.
* **GET** `/admin/vehicles` - Lista ônibus cadastrados com contagem de votos diários de viagem.
* **POST** `/admin/vehicles` - Cadastra um novo ônibus.
* **GET** `/admin/drivers` - Lista motoristas operacionais.
* **POST** `/admin/drivers` - Vincula dados operacionais (CNH/Telefone) a um usuário.
* **GET** `/admin/settings` - Obtém parâmetros operacionais do sistema (horários de votação).
* **PUT** `/admin/settings` - Atualiza as configurações operacionais.
* **POST** `/admin/notices` - Publica um novo aviso/notícia no feed do aplicativo.
* **GET** `/admin/support-messages` - Visualiza dúvidas e sugestões enviadas por passageiros.

---

### 🎓 3. Módulo do Passageiro (`USER` / `PASSENGER`)
Funcionalidades dedicadas ao acompanhamento de rotas, envio de sugestões e intenções de viagem.

* **GET** `/passenger/profile` - Dados cadastrais do aluno e status da sua homologação.
* **GET** `/passenger/routes` - Lista as rotas disponíveis do dia, paradas intermediárias e acessibilidade de cada veículo.
* **POST** `/passenger/votes` - Registra o voto diário de intenção de viagem e parada de embarque pretendida.
* **GET** `/passenger/notices` - Acessa o mural de avisos da universidade.
* **GET** `/passenger/contacts` - Visualiza o contato do motorista escalado para a rota escolhida hoje e informações do setor de transportes.
* **POST** `/passenger/support` - Envia uma mensagem com dúvidas, críticas ou sugestões.
* **POST** `/passenger/photo` - Permite atualizar a foto de perfil do estudante (visualização do motorista no embarque).

---

### 🚍 4. Módulo do Motorista (`DRIVE`)
Controle das viagens, lista de embarques e incidentes operacionais.

* **GET** `/driver/profile` - Acessa dados operacionais do motorista (CNH, Validade).
* **GET** `/driver/routes` - Visualiza as viagens agendadas ou em andamento sob sua escala no dia.
* **GET** `/driver/vehicle` - Exibe os dados do ônibus escalado para o motorista no dia de hoje.
* **PUT** `/driver/routes/{id}/status` - Permite iniciar e concluir a viagem alterando o status (ex: `'Em Viagem'`, `'Finalizada'`).
* **GET** `/driver/routes/{routeId}/passengers` - Obtém a lista de passageiros que confirmaram interesse e embarque naquela rota hoje.
* **PUT** `/driver/passengers/{passengerId}/status` - Confirma a presença ou ausência do estudante na hora do embarque (`'Confirmado'`, `'Ausente'`, `'Pendente'`).
* **POST** `/driver/failures` - Registra falhas mecânicas ou de acessibilidade identificadas no ônibus.

---

## 🐳 Executando com Docker

Você pode subir a aplicação em produção empacotando-a em um container Docker:

1. **Build da Imagem**:
   ```bash
   docker build -t siti-api .
   ```
2. **Executar Container**:
   ```bash
   docker run --rm -p 8080:8080 \
     -e DB_USER=root \
     -e DB_PASSWORD=root \
     siti-api
   ```
*Nota: Em ambiente Docker (`prod`), a aplicação espera encontrar a conexão de banco de dados apontando para a rede mapeada do compose (`db:3306`).*

---

## 🔗 Links Úteis
* **Quadro de Tarefas**: [Quadro de Projetos do GitHub](https://github.com/users/r7melo/projects/12)

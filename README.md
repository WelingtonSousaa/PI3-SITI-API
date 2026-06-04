# SITI-API

API REST do projeto SITI, desenvolvida com Java 21, Spring Boot e MySQL. A aplicacao centraliza funcionalidades de cadastro, autenticacao e estruturas de dados ligadas ao transporte, como usuarios, passageiros, administradores, motoristas, onibus, rotas, paradas, viagens e solicitacoes de transporte.

## Tecnologias

- Java 21
- Spring Boot 3.5.13
- Maven Wrapper
- MySQL
- Spring Web
- Spring JDBC
- Spring Cache com Caffeine
- Lombok

## Estrutura do projeto

```text
SITI-API/
├── database/
│   ├── create_db.sql                  # Cria o banco e as tabelas
│   ├── procedures.sql                  # Cria as procedures usadas pela API
│   └── SITI-API.postman_collection.json
├── src/main/java/com/siti/sitiapi/
│   ├── configs/                        # CORS, cache e autenticacao por interceptor
│   ├── controller/                     # Rotas REST
│   ├── dto/                            # Objetos de entrada e resposta
│   ├── exception/                      # Tratamento de erros
│   ├── model/                          # Modelos de dominio
│   ├── repository/                     # Acesso ao banco via JDBC/procedures
│   └── service/                        # Regras de negocio
├── src/main/resources/
│   ├── application.yaml                # Perfil ativo padrao
│   ├── application-dev.yaml            # Configuracao local
│   └── application-prod.yaml           # Configuracao para Docker/producao
├── Dockerfile
├── mvnw
├── mvnw.cmd
└── pom.xml
```

## Pre-requisitos

Para rodar localmente:

- Java 21 instalado
- MySQL rodando localmente
- Git

Nao e necessario instalar Maven manualmente, pois o projeto usa Maven Wrapper (`mvnw`/`mvnw.cmd`).

## Configuracao do banco local

O perfil padrao da aplicacao e `dev`, configurado em `src/main/resources/application.yaml`.

No perfil `dev`, a API tenta conectar em:

```yaml
url: jdbc:mysql://localhost:3306/sitidb
username: root
password: root
```

Se o seu MySQL usa outro usuario ou senha, ajuste `src/main/resources/application-dev.yaml` antes de executar.

Para preparar o banco:

```bash
mysql -u root -p < database/create_db.sql
mysql -u root -p sitidb < database/procedures.sql
```

No Windows PowerShell, se estiver usando o usuario `root` com senha `root`, tambem pode executar os scripts abrindo o cliente MySQL:

```bash
mysql -u root -p
```

Depois, dentro do MySQL:

```sql
SOURCE database/create_db.sql;
SOURCE database/procedures.sql;
```

## Como executar localmente

Clone o repositorio e entre na pasta:

```bash
git clone <url-do-repositorio>
cd SITI-API
```

Execute a aplicacao:

```bash
# Windows
.\mvnw.cmd spring-boot:run

# Linux/macOS
./mvnw spring-boot:run
```

Por padrao, a API sobe em:

```text
http://localhost:8080
```

## Build

Para compilar e gerar o `.jar`:

```bash
# Windows
.\mvnw.cmd clean package

# Linux/macOS
./mvnw clean package
```

O artefato sera gerado em `target/`.

## Executando com Docker

O `Dockerfile` gera a aplicacao com Maven e executa o `.jar` usando o perfil `prod`.

Build da imagem:

```bash
docker build -t siti-api .
```

Execucao:

```bash
docker run --rm -p 8080:8080 \
  -e DB_USER=root \
  -e DB_PASSWORD=root \
  siti-api
```

No perfil `prod`, a aplicacao espera encontrar o MySQL em:

```text
jdbc:mysql://db:3306/siti_db
```

Ou seja, para rodar em Docker de ponta a ponta, crie uma rede/compose onde o container do MySQL tenha o nome `db`, ou ajuste `application-prod.yaml` conforme o seu ambiente.

## Endpoints principais

### Registrar usuario

```http
POST /users/register
Content-Type: application/json
```

Body:

```json
{
  "email": "usuario@email.com",
  "password": "123456",
  "identifierDocument": "12345678900"
}
```

Resposta esperada:

```json
{
  "id": 1,
  "email": "usuario@email.com"
}
```

### Login

```http
POST /auth/login
Content-Type: application/json
```

Body:

```json
{
  "email": "usuario@email.com",
  "password": "123456"
}
```

Resposta esperada:

```json
{
  "accessKey": "token-gerado",
  "role": "USER"
}
```

### Testar autenticacao

```http
GET /auth/authenticate
Authorization: Bearer token-gerado
```

Resposta esperada:

```text
Acesso liberado para usuario@email.com
```

As rotas `/users/register` e `/auth/login` sao publicas. As demais rotas passam pelo interceptor de autenticacao e precisam do header `Authorization: Bearer <accessKey>`.

## Postman

Existe uma collection pronta em:

```text
database/SITI-API.postman_collection.json
```

Importe esse arquivo no Postman para testar os fluxos da API.

## Troubleshooting rapido

- **Erro de conexao com MySQL**: confirme se o MySQL esta rodando, se o banco `sitidb` existe e se usuario/senha batem com `application-dev.yaml`.
- **Erro chamando login ou registro**: confirme se as procedures de `database/procedures.sql` foram executadas.
- **HTTP 401 em rotas protegidas**: faca login novamente e envie o header `Authorization` no formato `Bearer <accessKey>`.
- **Porta 8080 ocupada**: execute com outra porta:

```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

## Links do projeto

- Quadro do projeto: https://github.com/users/r7melo/projects/12

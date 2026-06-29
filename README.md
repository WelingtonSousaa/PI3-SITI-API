# 🚌 SITI-API (Sistema Inteligente de Transporte Intercampi)

![Java](https://img.shields.io/badge/Java-21-orange.svg)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.13-brightgreen.svg)
![MySQL](https://img.shields.io/badge/MySQL-8.0-blue.svg)
![Test Coverage](https://img.shields.io/badge/Test%20Coverage-100%25-success)
![Build](https://img.shields.io/badge/Build-Passing-brightgreen.svg)

API REST do projeto SITI (Sistema Inteligente de Transporte Intercampi), desenvolvida para gerenciar rotas de ônibus, embarque de passageiros (alunos e funcionários) e comunicação direta entre a frota e a administração. A aplicação centraliza funcionalidades de cadastro, autenticação e gerenciamento de itinerários, fornecendo suporte completo para o Aplicativo do Passageiro, Aplicativo do Motorista e Painel Web Administrativo.

Nesta versão, a API conta com **Inteligência de Roteirização**, notificações automáticas, gerenciamento de filas de espera, e uma robusta interface interativa via **Swagger UI**, além de **cobertura total com 141 testes automatizados (unitários e de integração)**.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 21
- **Framework Principal**: Spring Boot 3.5.13
- **Persistência**: Spring JDBC (Template) + MySQL (Prod) / H2 Database (Testes em Memória)
- **Cache e Performance**: Spring Cache (Caffeine Cache)
- **Segurança**: Autenticação nativa com `AuthenticationInterceptor` e RBAC (Role-Based Access Control)
- **Notificações**: Spring Mail (Envio de e-mails)
- **Documentação API**: Springdoc OpenAPI (Swagger UI 3.0)
- **Testes**: JUnit 5, Mockito, Spring Boot Test (`@WebMvcTest`) e DataFaker para massa dinâmica.

---

## 🎯 Principais Funcionalidades (Features)

- **Gestão de Passageiros**: Votação diária de interesse nas rotas, relatórios de superlotação, upload de foto e consulta de perfil.
- **Gestão de Motoristas**: Controle da viagem (Em andamento, Concluída), chamada eletrônica de alunos no ponto e reporte de falhas no veículo.
- **Painel Administrativo**: Substituição emergencial de ônibus, alteração de horários (abertura/fechamento das rotas), emissão de avisos globais e homologação de novos usuários.
- **Inteligência de Roteirização (Módulo Schedule)**:
  - Encerramento automático de captação de votos.
  - Eliminação de paradas ociosas (onde nenhum aluno votou para descer/subir).
  - Controle de superlotação e filas de espera.

---

## ⚙️ Passo a Passo de Execução Local (Diretamente)

### Pré-requisitos
* **Java 21** instalado nas variáveis de ambiente.
* **MySQL 8.0+** rodando localmente (com a pasta `bin` configurada no PATH do Windows).

### 1. Criar o Banco de Dados Local
O sistema requer que as tabelas sejam criadas previamente.
Abra seu terminal na pasta raiz do projeto e importe os arquivos SQL:

**PowerShell:**
```powershell
Get-Content database\create_db.sql | mysql -u root -p
```
**Bash (Linux/Mac/Git Bash):**
```bash
mysql -u root -p < database/create_db.sql
```
*(Nota: O MySQL pedirá sua senha. Se suas credenciais forem diferentes de `root`/`root`, atualize-as no arquivo `src/main/resources/application-dev.yaml`).*

### 2. Rodar a Aplicação Spring Boot
Não é necessário ter o Maven instalado globalmente, o projeto utiliza o **Maven Wrapper**.

**Windows (CMD/PowerShell):**
```bash
.\mvnw.cmd spring-boot:run
```
**Linux/Mac:**
```bash
./mvnw spring-boot:run
```
A API inicializará o servidor web (Tomcat) na porta **`8080`**.

---

## 🧪 Como Executar os Testes Automatizados

O projeto possui rigorosos testes que garantem a estabilidade das Regras de Negócio (Camada Service) e do Roteamento e Segurança HTTP (Camada Web). **O sistema conta com 141 testes individuais (unitários e de integração), cobrindo 100% de todos os endpoints e serviços.**

Para rodar a bateria de testes e visualizar os resultados no terminal:

**Windows:**
```bash
.\mvnw.cmd test
```
**Linux/Mac:**
```bash
./mvnw test
```

Os testes de integração rodam utilizando o isolamento **`@WebMvcTest`**, e não precisam do banco MySQL ativo, garantindo que possam ser rodados em qualquer esteira de CI/CD limpa.

---

## 🐳 Executando com Docker (Isolado)

Se você não quer instalar o Java ou o MySQL diretamente na sua máquina física, você pode construir e rodar a API de forma empacotada usando Docker.

1. **Construa a imagem da API (Build):**
   No terminal, na raiz do projeto:
   ```bash
   docker build -t siti-api .
   ```

2. **Inicie o Container:**
   Passe as variáveis de ambiente necessárias para que a aplicação consiga alcançar o seu banco de dados (que pode estar em outro container ou host).
   ```bash
   docker run --rm -p 8080:8080 -e DB_URL=jdbc:mysql://host.docker.internal:3306/siti_db -e DB_USER=root -e DB_PASSWORD=root siti-api
   ```
   *(Nota: `host.docker.internal` permite que o container acesse o MySQL da sua máquina host. Ajuste a URL caso use uma bridge network customizada).*

---

## 📚 Acessar o Swagger UI e Documentação da API

Com a aplicação em execução, acesse o painel interativo do Swagger pelo navegador:

🔗 **[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)**

Pelo Swagger, você pode realizar chamadas diretas (sem precisar do Postman).
Para acessar rotas protegidas:
1. Cadastre-se na rota de `/auth/login` ou gere um mock-token fictício.
2. Clique no botão verde **"Authorize"** (no topo).
3. Preencha seu Token (JWT) e também o cabeçalho *Role* (`ADMIN`, `USER` ou `DRIVE`). 

---

## 🔒 Regras de Segurança (RBAC)

A API protege de forma ferrenha (Retornando erro HTTP 403 Forbidden) caso um perfil tente invadir o escopo de outro. 
- Um Passageiro (`USER`) **nunca** conseguirá chamar uma rota do Motorista.
- Apenas um Administrador (`ADMIN`) pode aprovar novas contas e alterar os horários sistêmicos.

Isto é validado via **`AuthenticationInterceptor`** do Spring Web em todas as requisições ativas.

---
**Desenvolvido para revolucionar a logística intercampi!**

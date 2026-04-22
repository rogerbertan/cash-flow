# cash-flow

Uma API REST para gerenciamento de finanças pessoais construída com Spring Boot.

## Sobre o Projeto

Uma API RESTful para controle de fluxo de caixa pessoal, desenvolvida com Java 21 e Spring Boot 4. O projeto utiliza inteligência artificial como parte central da experiência, oferecendo:

- **Gerenciamento de Categorias**: Criar, atualizar, deletar e listar categorias do tipo `RECEITA` ou `DESPESA`
- **Gerenciamento de Transações**: Registrar e acompanhar movimentações financeiras com operações CRUD completas
- **Funcionalidades com IA** (Google Gemini):
  - Sugestão inteligente de categoria com base na descrição da transação
  - Análise de padrões de gastos e insights financeiros personalizados
- **Histórico paginado** de transações
- **Migrações de banco** automatizadas com Flyway

O projeto segue os princípios de **Clean Architecture**, separando claramente as responsabilidades entre as camadas de domínio, aplicação e infraestrutura.

## Arquitetura

A aplicação é organizada em três camadas principais:

- **Domínio** (`domain/`) — entidades imutáveis (`Category`, `Transaction`) e value objects (`Balance`, `MonthlySummary`, `SpendingInsights`), sem dependências externas
- **Aplicação** (`application/`) — casos de uso com responsabilidade única e interfaces de gateway para persistência e serviços de IA
- **Infraestrutura** (`infra/`) — controllers REST, entidades JPA, implementações dos gateways, DTOs, mappers e tratamento global de exceções

**Padrões utilizados:** Use Cases com método `execute()`, Gateway Pattern, Mapper Pattern e DTOs como records Java imutáveis.

## Tecnologias

- [![Java][Java]][Java-url]
- [![Spring Boot][SpringBoot]][SpringBoot-url]
- [![PostgreSQL][PostgreSQL]][PostgreSQL-url]
- [![Maven][Maven]][Maven-url]

## Pré-requisitos

- Java 21+
- Maven 3.x (Maven Wrapper incluído)
- PostgreSQL 16+
- Chave de API do Google (para funcionalidades de IA)

## Instalação

```sh
git clone https://github.com/rogerbertan/cash-flow.git
cd cash-flow
```

Configure as variáveis de ambiente:

```sh
export DB_HOST=localhost
export DB_NAME=cash_flow
export DB_USER=postgres
export DB_PASSWORD=postgres
export GOOGLE_API_KEY=sua_chave_aqui
```

Crie o banco de dados e execute a aplicação:

```sh
createdb cash_flow
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:8080`.

## Uso

### Endpoints

#### Categorias (`/api/categories`)

| Método | Endpoint               | Descrição                  |
|--------|------------------------|----------------------------|
| GET    | `/api/categories`      | Listar todas as categorias |
| GET    | `/api/categories/{id}` | Buscar categoria por ID    |
| POST   | `/api/categories`      | Criar nova categoria       |
| PUT    | `/api/categories/{id}` | Atualizar categoria        |
| DELETE | `/api/categories/{id}` | Deletar categoria          |

#### Transações (`/api/transactions`)

| Método | Endpoint                              | Descrição                               |
|--------|---------------------------------------|-----------------------------------------|
| GET    | `/api/transactions`                   | Listar transações (paginado, 20/página) |
| GET    | `/api/transactions/{id}`              | Buscar transação por ID                 |
| POST   | `/api/transactions`                   | Criar nova transação                    |
| PUT    | `/api/transactions/{id}`              | Atualizar transação                     |
| DELETE | `/api/transactions/{id}`              | Deletar transação                       |
| POST   | `/api/transactions/suggest-category`  | Sugestão de categoria via IA            |

#### Insights de IA (`/api/ai`)

| Método | Endpoint                          | Descrição                                                                       |
|--------|-----------------------------------|---------------------------------------------------------------------------------|
| GET    | `/api/ai/insights?period=monthly` | Análise de gastos via IA (períodos: `monthly`, `weekly`, `quarterly`, `yearly`) |

### Exemplo de Requisição

```sh
curl -X POST http://localhost:8080/api/transactions \
  -H "Content-Type: application/json" \
  -d '{"description": "Salário mensal", "amount": 5000.00, "date": "2026-04-01", "categoryId": 1, "type": "INCOME"}'
```

Para mais exemplos, consulte a [Coleção do Postman](postman_collection.json).

## Qualidade de Código

O projeto utiliza **Checkstyle** e **Spotless** para garantir consistência e qualidade.

```sh
./mvnw spotless:apply      # formata o código automaticamente
./mvnw checkstyle:check    # verifica violações de estilo
./mvnw test                # executa todos os testes
```

<!-- LINKS E IMAGENS MARKDOWN -->
[Java]: https://img.shields.io/badge/Java-21-007396?style=for-the-badge&logo=openjdk&logoColor=white
[Java-url]: https://openjdk.org/
[SpringBoot]: https://img.shields.io/badge/Spring_Boot-4.0.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white
[SpringBoot-url]: https://spring.io/projects/spring-boot
[PostgreSQL]: https://img.shields.io/badge/PostgreSQL-16+-316192?style=for-the-badge&logo=postgresql&logoColor=white
[PostgreSQL-url]: https://www.postgresql.org/
[Maven]: https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white
[Maven-url]: https://maven.apache.org/
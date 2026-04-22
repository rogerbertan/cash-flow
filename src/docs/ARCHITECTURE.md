# Clean Architecture

## Visão Geral

```mermaid
graph TD
    subgraph INFRA["Camada de Infraestrutura"]
        subgraph APP["Camada de Aplicação"]
            subgraph UC["Use Cases"]
                UC1[CreateCategoryUseCase]
                UC2[FindAllCategoriesUseCase]
                UC3[CreateTransactionUseCase]
                UC4[FindAllTransactionsUseCase]
                UC5[GetBalanceUseCase]
                UC6[GetMonthlySummaryUseCase]
                UC7[SuggestTransactionCategoryUseCase]
                UC8[GenerateSpendingInsightsUseCase]
            end

            subgraph GW["Gateway Interfaces"]
                GW1[CategoryGateway]
                GW2[TransactionGateway]
                GW3[AICategorizerGateway]
                GW4[AIInsightsGateway]
            end

            subgraph DOMAIN["Camada de Domínio"]
                D1[Category - record]
                D2[Transaction - record]
                D3[Type - enum]
                D4[Balance - value object]
                D5[MonthlySummary - value object]
            end
        end

        CTRL[Controllers]
        DTO[DTOs]
        MAP[Mappers]
        GWI[Gateway Impls]
        JPA[JPA Repositories]
    end
```

**Princípio Central**: As dependências apontam para dentro. A camada de domínio tem **zero dependências externas**. A camada de aplicação depende apenas do domínio.

---

## Estrutura das Camadas

### Camada de Domínio

```
domain/
├── entities/           # Objetos de negócio imutáveis (records)
│   ├── Category
│   └── Transaction
├── enums/             # Enumerações de negócio
│   └── Type
└── valueobjects/      # Estruturas de dados imutáveis
    ├── Balance
    ├── CategorySuggestion
    ├── CategorySummary
    ├── MonthlySummary
    ├── SpendingInsights
    └── TransactionAnalysisData
```

### Camada de Aplicação

```
application/
├── gateway/           # Contratos de persistência e serviços (interfaces)
│   ├── CategoryGateway
│   ├── TransactionGateway
│   ├── AICategorizerGateway
│   └── AIInsightsGateway
└── usecases/          # Operações de negócio
    ├── category/
    │   ├── CreateCategoryUseCase
    │   ├── FindAllCategoriesUseCase
    │   ├── FindCategoryByIdUseCase
    │   ├── UpdateCategoryUseCase
    │   └── DeleteCategoryUseCase
    ├── transaction/
    │   ├── CreateTransactionUseCase
    │   ├── FindAllTransactionUseCase
    │   ├── FindTransactionByIdUseCase
    │   ├── UpdateTransactionUseCase
    │   ├── DeleteTransactionUseCase
    │   └── SuggestTransactionCategoryUseCase
    ├── summary/
    │   ├── GetBalanceUseCase
    │   ├── GetMonthlySummaryUseCase
    │   └── GetCategoriesSummaryUseCase
    └── insights/
        └── GenerateSpendingInsightsUseCase
```

### Camada de Infraestrutura

```
infra/
├── presentation/      # Controllers REST API
│   ├── CategoryController
│   ├── TransactionController
│   ├── SummaryController
│   ├── AIInsightsController
│   └── HealthController
├── dto/               # Objetos de Request/Response da API
│   ├── BalanceResponse
│   ├── CategoriesSummaryResponse
│   ├── CategoryCreateRequest
│   ├── CategoryResponse
│   ├── CategorySuggestionRequest
│   ├── CategorySuggestionResponse
│   ├── CategoryUpdateRequest
│   ├── ErrorResponse
│   ├── MonthlySummaryResponse
│   ├── SpendingInsightsRequest
│   ├── SpendingInsightsResponse
│   ├── TransactionCreateRequest
│   ├── TransactionResponse
│   └── TransactionUpdateRequest
├── mapper/            # Conversões de objetos
│   ├── BalanceResponseMapper
│   ├── CategoryCreateMapper
│   ├── CategoryEntityMapper
│   ├── CategoryResponseMapper
│   ├── CategorySuggestionMapper
│   ├── CategorySummaryResponseMapper
│   ├── CategoryUpdateRequestMapper
│   ├── MonthlySummaryResponseMapper
│   ├── SpendingInsightsMapper
│   ├── TransactionCreateMapper
│   ├── TransactionEntityMapper
│   ├── TransactionResponseMapper
│   └── TransactionUpdateRequestMapper
├── gateway/           # Implementações dos gateways
│   ├── CategoryRepositoryGateway
│   ├── TransactionRepositoryGateway
│   ├── GeminiCategorizerGateway
│   └── GeminiInsightsGateway
├── persistence/       # Entidades JPA e repositórios
│   ├── CategoryEntity
│   ├── CategoryRepository
│   ├── TransactionEntity
│   └── TransactionRepository
├── exception/         # Tratamento de exceções
│   ├── BudgetPlannerException (abstract)
│   ├── ResourceNotFoundException
│   ├── InvalidTransactionException
│   ├── AICategorizeException
│   ├── AIInsightsException
│   └── GlobalExceptionHandler
├── config/            # Configuração
│   └── AIProperties
├── util/              # Utilitários
│   ├── PeriodCalculator
│   └── DateRange
└── beans/             # Injeção de dependência
    └── BeanConfiguration
```

---

## Fluxo de Dependências

```mermaid
flowchart LR
    CTRL[Controllers] --> UC[Use Cases]
    UC --> GWI[Gateway Interface]
    GWI -->|implements| GWL[Gateway Impl]
    GWL --> REPO[Repositories]
```

---

## Fluxo de uma Requisição

### Exemplo: Criando uma Transaction

```mermaid
sequenceDiagram
    participant C as Client
    participant CTRL as TransactionController
    participant CM as TransactionCreateMapper
    participant UC as CreateTransactionUseCase
    participant GW as TransactionGateway
    participant GWI as TransactionRepositoryGateway
    participant JPA as TransactionJpaRepository

    C->>CTRL: POST /api/transactions {JSON}
    CTRL->>CM: 1. Map DTO → Domain
    CM->>UC: 2. execute(transaction)
    UC->>GW: 3. gateway.create(transaction)
    GW->>GWI: 4. Validate & persist
    Note over GWI: • Category exists<br/>• Amount > 0<br/>• Type matches
    GWI->>JPA: 5. save(entity)
    JPA-->>GWI: TransactionEntity
    GWI-->>UC: Transaction (domain)
    UC-->>CTRL: Transaction (domain)
    CTRL->>CM: 6. Map Domain → DTO
    CM-->>C: 201 Created {JSON}
```

---

## Relacionamento entre Componentes

### Padrão Use Case

```java
public class CreateCategoryUseCase {
    private final CategoryGateway gateway;

    public CreateCategoryUseCase(CategoryGateway gateway) {
        this.gateway = gateway;
    }

    public Category execute(Category category) {
        return gateway.create(category);
    }
}
```

**Características:**
- Responsabilidade única
- Método único `execute()`
- Depende da interface do gateway (não da implementação)
- Sem dependências de framework

### Padrão Gateway

```mermaid
classDiagram
    class CategoryGateway {
        <<interface>>
        +create(category) Category
        +findAll() List~Category~
        +findById(id) Category
        +update(id, category) Category
        +delete(id) void
    }

    class CategoryRepositoryGateway {
        +create(category) Category
        +findAll() List~Category~
        +findById(id) Category
        +update(id, category) Category
        +delete(id) void
    }

    CategoryGateway <|.. CategoryRepositoryGateway : implements
```

### Fluxo dos Mappers

```mermaid
flowchart LR
    REQ[Request DTO] -->|CreateMapper| DOM1[Domain Entity]
    DOM1 --> UC[Use Case Logic]
    UC --> DOM2[Domain Entity]
    DOM2 -->|ResponseMapper| RES[Response DTO]
```

---

## Estratégia de Validação

```mermaid
flowchart TD
    A[Requisição HTTP] --> B[Controller Layer]
    B -->|"@Valid, @NotNull, @Size"| C{Válido?}
    C -->|Não| ERR1[400 Bad Request]
    C -->|Sim| D[Gateway Implementation]
    D -->|"Regras de negócio<br/>Validação cruzada<br/>Existência de categoria<br/>Amount > 0<br/>Type matching"| E{Válido?}
    E -->|Não| ERR2[422 / 404]
    E -->|Sim| F[Database Layer]
    F -->|"Foreign keys<br/>Check constraints<br/>Unique constraints"| G[Persistido com sucesso]
```

---

## Decisões Arquiteturais

### 1. Camada de Aplicação Explícita
- Use cases e interfaces de gateway vivem em `application/`, separados do `domain/`
- O domínio permanece puro: apenas entities, enums e value objects
- Fronteira clara entre orquestração de lógica de negócio e regras centrais de negócio

### 2. Use Cases Sem Interfaces
- Simplificado do padrão interface + implementação
- Classes concretas são suficientes para operações de responsabilidade única
- Reduz boilerplate e complexidade

### 3. Padrão Gateway para Persistência
- A camada de aplicação define contratos via interfaces
- A infraestrutura fornece as implementações
- Permite testes com mocks
- Permite trocar estratégias de persistência

### 4. Validação nas Implementações de Gateway
- Regras de negócio aplicadas no nível do gateway
- Use cases permanecem simples e focados
- Ponto único de aplicação de lógica de negócio

### 5. Entidades Imutáveis
- Java records para entities, value objects e DTOs
- Previne mutação acidental de estado
- Thread-safe por design
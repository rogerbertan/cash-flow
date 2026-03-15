# Clean Architecture

## Overview

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│                    INFRASTRUCTURE LAYER                     │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │                                                       │ │
│  │                  APPLICATION LAYER                    │ │
│  │                                                       │ │
│  │   ┌───────────────────────────────────────────┐     │ │
│  │   │             Use Cases                     │     │ │
│  │   │                                           │     │ │
│  │   │  • CreateCategoryUseCase                  │     │ │
│  │   │  • FindAllCategoriesUseCase               │     │ │
│  │   │  • CreateTransactionUseCase               │     │ │
│  │   │  • FindAllTransactionsUseCase             │     │ │
│  │   │  • GetBalanceUseCase                      │     │ │
│  │   │  • GetMonthlySummaryUseCase               │     │ │
│  │   │  • SuggestTransactionCategoryUseCase      │     │ │
│  │   │  • GenerateSpendingInsightsUseCase        │     │ │
│  │   └───────────────────────────────────────────┘     │ │
│  │                                                       │ │
│  │   ┌───────────────────────────────────────────┐     │ │
│  │   │        Gateway Interfaces                 │     │ │
│  │   │                                           │     │ │
│  │   │  • CategoryGateway                        │     │ │
│  │   │  • TransactionGateway                     │     │ │
│  │   │  • AICategorizerGateway                   │     │ │
│  │   │  • AIInsightsGateway                      │     │ │
│  │   └───────────────────────────────────────────┘     │ │
│  │                                                       │ │
│  │   ┌───────────────────────────────────────────┐     │ │
│  │   │         DOMAIN LAYER                      │     │ │
│  │   │                                           │     │ │
│  │   │  • Category (record)                      │     │ │
│  │   │  • Transaction (record)                   │     │ │
│  │   │  • Type (enum)                            │     │ │
│  │   │  • Balance, MonthlySummary (value objects)│     │ │
│  │   └───────────────────────────────────────────┘     │ │
│  │                                                       │ │
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  Controllers │ DTOs │ Mappers │ Gateway Impl │ JPA Repos   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Core Principle**: Dependencies point inward. The domain layer has **zero external dependencies**. The application layer depends only on the domain.

## Layer Structure

### Domain Layer
```
domain/
├── entities/           # Immutable business objects (records)
│   ├── Category
│   └── Transaction
├── enums/             # Business enumerations
│   └── Type
└── valueobjects/      # Immutable data structures
    ├── Balance
    ├── CategorySuggestion
    ├── CategorySummary
    ├── MonthlySummary
    ├── SpendingInsights
    └── TransactionAnalysisData
```

### Application Layer
```
application/
├── gateway/           # Persistence & service contracts (interfaces)
│   ├── CategoryGateway
│   ├── TransactionGateway
│   ├── AICategorizerGateway
│   └── AIInsightsGateway
└── usecases/          # Business operations
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

### Infrastructure Layer
```
infra/
├── presentation/      # REST API Controllers
│   ├── CategoryController
│   ├── TransactionController
│   ├── SummaryController
│   ├── AIInsightsController
│   └── HealthController
├── dto/               # API Request/Response objects
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
├── mapper/            # Object conversions
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
├── gateway/           # Gateway implementations
│   ├── CategoryRepositoryGateway
│   ├── TransactionRepositoryGateway
│   ├── GeminiCategorizerGateway
│   └── GeminiInsightsGateway
├── persistence/       # JPA entities & repositories
│   ├── CategoryEntity
│   ├── CategoryRepository
│   ├── TransactionEntity
│   └── TransactionRepository
├── exception/         # Exception handling
│   ├── BudgetPlannerException (abstract)
│   ├── ResourceNotFoundException
│   ├── InvalidTransactionException
│   ├── AICategorizeException
│   ├── AIInsightsException
│   └── GlobalExceptionHandler
├── config/            # Configuration
│   └── AIProperties
├── util/              # Utilities
│   ├── PeriodCalculator
│   └── DateRange
└── beans/             # Dependency injection
    └── BeanConfiguration
```

## Dependency Flow

```
┌─────────────┐      ┌─────────────┐      ┌─────────────┐
│             │      │             │      │             │
│ Controllers │─────▶│  Use Cases  │─────▶│  Gateways   │
│             │      │             │      │ (interface) │
└─────────────┘      └─────────────┘      └──────┬──────┘
                                                  │
                                                  │ implements
                                                  │
                                         ┌────────▼────────┐
                                         │                 │
                                         │ Gateway Impls   │
                                         │                 │
                                         └────────┬────────┘
                                                  │
                                                  │
                                         ┌────────▼────────┐
                                         │                 │
                                         │  Repositories   │
                                         │                 │
                                         └─────────────────┘
```

## Request Flow

### Example: Creating a Transaction

```
     HTTP REQUEST                    APPLICATION               INFRASTRUCTURE

         │
         │  POST /api/transactions
         │  {JSON body}
         │
         ▼
┌────────────────────┐
│ Transaction        │
│ Controller         │
└──────┬─────────────┘
       │
       │ 1. Receive request
       │
       ▼
┌────────────────────┐
│ Transaction        │
│ CreateMapper       │
└──────┬─────────────┘
       │
       │ 2. Map DTO → Domain
       │
       ▼                              ┌────────────────────┐
       ├─────────────────────────────▶│ Create             │
       │                              │ TransactionUseCase │
       │                              └──────┬─────────────┘
       │                                     │
       │                                     │ 3. Execute use case
       │                                     │
       │                              ┌──────▼─────────────┐
       │                              │ Transaction        │
       │                              │ Gateway            │
       │                              │ (interface)        │
       │                              └──────┬─────────────┘
       │                                     │
       │                                     │
       │                              ┌──────▼──────────────────┐
       │                              │ Transaction             │
       │                              │ RepositoryGateway       │
       │                              │                         │
       │                              │ 4. Validate:            │
       │                              │   • Category exists     │
       │                              │   • Amount > 0          │
       │                              │   • Type matches        │
       │                              └──────┬──────────────────┘
       │                                     │
       │                                     │ 5. Convert & persist
       │                                     │
       │                              ┌──────▼─────────────┐
       │                              │ Transaction        │
       │                              │ JpaRepository      │
       │                              └──────┬─────────────┘
       │                                     │
       │◀────────────────────────────────────┘
       │ 6. Return domain entity
       │
       ▼
┌────────────────────┐
│ Transaction        │
│ ResponseMapper     │
└──────┬─────────────┘
       │
       │ 7. Map Domain → DTO
       │
       ▼
    HTTP RESPONSE
    201 Created
    {JSON body}
```

## Component Relationships

### Use Case Pattern

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

**Characteristics:**
- Single responsibility
- Single `execute()` method
- Depends on gateway interface (not implementation)
- No framework dependencies

### Gateway Pattern

```
┌─────────────────────┐
│  CategoryGateway    │  ◀─── Application interface
│  (interface)        │
│                     │
│  + create()         │
│  + findAll()        │
│  + findById()       │
│  + update()         │
│  + delete()         │
└──────────┬──────────┘
           │
           │ implements
           │
┌──────────▼─────────────────┐
│ CategoryRepositoryGateway  │  ◀─── Infrastructure implementation
│                            │
│  Responsibilities:         │
│  • Business validation     │
│  • Entity conversion       │
│  • Repository coordination │
└────────────────────────────┘
```

### Mapper Flow

```
Request DTO ─────▶ Create Mapper ─────▶ Domain Entity
                                              │
                                              ▼
                                        Use Case Logic
                                              │
                                              ▼
                                        Domain Entity ─────▶ Response Mapper ─────▶ Response DTO
```

## Validation Strategy

```
┌─────────────────────────────────────────────────────────┐
│                      VALIDATION LAYERS                   │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Controller Layer                                        │
│  ├─ @Valid annotation (basic DTO validation)            │
│  └─ @NotNull, @Size, etc.                               │
│                                                          │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Gateway Implementation Layer                            │
│  ├─ Business rules validation                           │
│  ├─ Cross-entity validation                             │
│  ├─ Category existence checks                           │
│  ├─ Amount validation (> 0)                             │
│  ├─ Type matching validation                            │
│  └─ Referential integrity                               │
│                                                          │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  Database Layer                                          │
│  ├─ Foreign key constraints                             │
│  ├─ Check constraints                                   │
│  └─ Unique constraints                                  │
│                                                          │
└─────────────────────────────────────────────────────────┘
```

## Key Architectural Decisions

### 1. Explicit Application Layer
- Use cases and gateway interfaces live in `application/`, separate from `domain/`
- Domain remains pure: entities, enums, and value objects only
- Clear boundary between business logic orchestration and core business rules

### 2. Use Cases Without Interfaces
- Simplified from interface + implementation pattern
- Concrete classes are sufficient for single responsibility operations
- Reduces boilerplate and complexity

### 3. Gateway Pattern for Persistence
- Application layer defines contracts via interfaces
- Infrastructure provides implementations
- Enables testing with mocks
- Allows swapping persistence strategies

### 4. Validation in Gateway Implementations
- Business rules enforced at gateway level
- Use cases remain simple and focused
- Single point of business logic enforcement

### 5. Immutable Entities
- Java records for entities, value objects, and DTOs
- Prevents accidental state mutation
- Thread-safe by design
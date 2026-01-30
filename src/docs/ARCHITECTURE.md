# Clean Architecture

## Overview

```
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│                    INFRASTRUCTURE LAYER                     │
│                                                             │
│  ┌───────────────────────────────────────────────────────┐ │
│  │                                                       │ │
│  │                   DOMAIN LAYER                        │ │
│  │                                                       │ │
│  │   ┌───────────────────────────────────────────┐     │ │
│  │   │         Entities & Business Rules         │     │ │
│  │   │                                           │     │ │
│  │   │  • Category (record)                      │     │ │
│  │   │  • Transaction (record)                   │     │ │
│  │   │  • Type (enum)                            │     │ │
│  │   └───────────────────────────────────────────┘     │ │
│  │                                                       │ │
│  │   ┌───────────────────────────────────────────┐     │ │
│  │   │             Use Cases                     │     │ │
│  │   │                                           │     │ │
│  │   │  • CreateCategoryUseCase                  │     │ │
│  │   │  • FindAllCategoriesUseCase               │     │ │
│  │   │  • CreateTransactionUseCase               │     │ │
│  │   │  • FindAllTransactionsUseCase             │     │ │
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
│  └───────────────────────────────────────────────────────┘ │
│                                                             │
│  Controllers │ DTOs │ Mappers │ Gateway Impl │ JPA Repos   │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

**Core Principle**: Dependencies point inward. The domain layer has **zero external dependencies**.

## Layer Structure

### Domain Layer
```
domain/
├── entities/           # Immutable business objects (records)
│   ├── Category
│   └── Transaction
├── enums/             # Business enumerations
│   └── Type
├── usecases/          # Business operations
│   ├── category/
│   │   ├── CreateCategoryUseCase
│   │   ├── FindAllCategoriesUseCase
│   │   ├── FindCategoryByIdUseCase
│   │   ├── UpdateCategoryUseCase
│   │   └── DeleteCategoryUseCase
│   ├── transaction/
│   │   ├── CreateTransactionUseCase
│   │   ├── FindAllTransactionsUseCase
│   │   ├── FindTransactionByIdUseCase
│   │   ├── UpdateTransactionUseCase
│   │   ├── DeleteTransactionUseCase
│   │   └── SuggestTransactionCategoryUseCase
│   ├── summary/
│   │   ├── GetBalanceUseCase
│   │   ├── GetMonthlySummaryUseCase
│   │   └── GetCategoriesSummaryUseCase
│   └── insights/
│       └── GenerateSpendingInsightsUseCase
├── gateway/           # Persistence & service contracts (interfaces)
│   ├── CategoryGateway
│   ├── TransactionGateway
│   ├── AICategorizerGateway
│   └── AIInsightsGateway
└── valueobjects/      # Immutable data structures
    ├── CategorySuggestion
    ├── SpendingInsights
    ├── TransactionAnalysisData
    ├── MonthlySummary
    ├── Balance
    └── CategorySummary
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
│   ├── CategoryCreateRequest
│   ├── CategoryResponse
│   ├── CategorySuggestionRequest
│   ├── CategorySuggestionResponse
│   ├── SpendingInsightsRequest
│   ├── SpendingInsightsResponse
│   ├── TransactionCreateRequest
│   └── TransactionResponse
├── mapper/            # Object conversions
│   ├── CategoryCreateMapper
│   ├── CategoryUpdateRequestMapper
│   ├── CategoryResponseMapper
│   ├── CategorySuggestionMapper
│   ├── SpendingInsightsMapper
│   ├── TransactionCreateMapper
│   ├── TransactionUpdateRequestMapper
│   └── TransactionResponseMapper
├── gateway/           # Gateway implementations
│   ├── CategoryRepositoryGateway
│   ├── TransactionRepositoryGateway
│   ├── GeminiCategorizerGateway
│   └── GeminiInsightsGateway
├── persistence/       # JPA entities & repositories
│   ├── CategoryEntity
│   ├── CategoryJpaRepository
│   ├── TransactionEntity
│   └── TransactionJpaRepository
├── exception/         # Exception handling
│   ├── BudgetPlannerException
│   ├── ResourceNotFoundException
│   ├── InvalidTransactionException
│   ├── CategoryInUseException
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
     HTTP REQUEST                    DOMAIN                    INFRASTRUCTURE

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
│  CategoryGateway    │  ◀─── Domain interface
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

### 1. Use Cases Without Interfaces
- Simplified from interface + implementation pattern
- Concrete classes are sufficient for single responsibility operations
- Reduces boilerplate and complexity

### 2. Gateway Pattern for Persistence
- Domain defines contracts via interfaces
- Infrastructure provides implementations
- Enables testing with mocks
- Allows swapping persistence strategies

### 3. Validation in Gateway Implementations
- Business rules enforced at gateway level
- Use cases remain simple and focused
- Single point of business logic enforcement

### 4. Immutable Entities
- Java records for entities and DTOs
- Prevents accidental state mutation
- Thread-safe by design

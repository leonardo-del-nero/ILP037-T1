# User Service - Sistema Multi-tenant com Design Patterns

Este projeto é uma aplicação **Spring Boot Full Stack** desenvolvida para a disciplina **ILP037 - Técnicas de Programação II**.

O sistema representa a **evolução arquitetural** de uma API legada para uma aplicação robusta, utilizando **Design Patterns (GoF)**, respeitando princípios SOLID e implementando uma arquitetura **Multi-tenant** segura com interface administrativa em **Thymeleaf**.

---

## 🔄 De Legado para Arquitetura Limpa (Refatoração)

O projeto original era uma API REST com acoplamento forte a bibliotecas externas (DozerMapper), regras de negócio misturadas e falhas de concorrência na gestão de tenants.

**Nesta versão refatorada (v2.0), entregamos:**

1.  **Sistema Híbrido (Web + API):** Além dos endpoints JSON, implementamos um **Dashboard Administrativo** com Thymeleaf e Bootstrap.
2.  **Design Patterns:** Substituição de lógicas rígidas por padrões flexíveis (Strategy, Factory, Adapter).
3.  **Tenant Isolation (Thread-Safe):** Evoluímos o `TenantResolver` de um Singleton instável para uso de `ThreadLocal`, garantindo isolamento total entre requisições simultâneas.

---

## 🚀 Principais Funcionalidades

* **Arquitetura Multi-Tenant**: Isolamento de dados baseado em coluna (`discriminator`) via header `x-tenant` (API) ou Sessão (Web).
* **Design Patterns**: Aplicação prática de padrões Criacionais, Estruturais e Comportamentais.
* **Interface Web Responsiva**: Dashboard administrativo criado com Thymeleaf e Bootstrap 5.
* **Auditoria**: Sistema de log de operações em arquivo (`audit.log`) via Adapter.
* **Segurança**: Hashing de senhas com BCrypt e validação de força de senha customizável.

---

## 🏛️ Arquitetura do Sistema

A aplicação segue a **Layered Architecture** (Camadas) típica do Spring Boot, mas enriquecida para suportar o modelo híbrido:

* **Controller Layer**:
    * `UserController`: API REST (`/userService/users`).
    * `WebController`: Interface Gráfica Thymeleaf (`/`).
* **Service Layer**: Camada de negócio agnóstica que orquestra os Design Patterns.
* **Domain Layer**: Entidades (`UserEntity`) e Interfaces.
* **Infrastructure**: Implementações concretas (Adapters, Strategies, TenantResolvers).

> **Nota sobre C4 Model:** Os diagramas de contexto e container (Nível 1 e 2) exigidos na atividade encontram-se na pasta `/docs` do repositório.

---

## 🏗️ Design Patterns Aplicados

Detalhes técnicos da refatoração para resolver problemas de acoplamento do sistema legado.

### 1. Strategy (Comportamental) - Validação de Senha

* **Problema (Legado)**: Regras de validação (tamanho, regex) ficavam espalhadas ou presas a anotações rígidas (`@Constraint`), dificultando a troca dinâmica de políticas de segurança.
* **Solução (Pattern)**: Interface `PasswordStrategy`.
* **Implementação**: O Service delega a validação. A classe `StrongPasswordStrategy` encapsula a regra atual (mínimo 8 chars + caracteres especiais).

```java
// Interface
public interface PasswordStrategy {
    void validate(String password);
}

// Implementação Concreta
@Component
@Primary
public class StrongPasswordStrategy implements PasswordStrategy {
    public void validate(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        // ... validação de regex
    }
}
```

### 2. Factory (Criacional) - Criação de Usuário

* **Problema (Legado)**: O Service dependia diretamente do `DozerMapper` e injetava o `PasswordEncoder`, misturando responsabilidades de mapeamento com regras de criptografia.
* **Solução (Pattern)**: Centralizar a criação na `UserFactory`.
* **Implementação**: A Factory remove a dependência do Dozer e encapsula a regra de que "todo novo usuário deve ter a senha hashada".

```java
@Component
@RequiredArgsConstructor
public class UserFactory {
    private final PasswordEncoder passwordEncoder;

    public UserEntity createEntityFromDTO(UserDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.getUsername());
        // A Factory encapsula a regra de hash BCrypt
        entity.setPassword(passwordEncoder.encode(dto.getPassword())); 
        entity.setRoles(dto.getRoles());
        return entity;
    }
}
```

### 3. Adapter (Estrutural) - Sistema de Auditoria

* **Problema (Legado)**: Inexistência de logs estruturados ou dependência direta de `System.out` e classes de I/O dentro do Service.
* **Solução (Pattern)**: Interface `AuditService` e adaptador `FileAuditAdapter`.
* **Implementação**: O `FileAuditAdapter` adapta a interface de domínio para a escrita física em arquivo (`audit.log`), isolando a complexidade de `java.io`.

```java
// O Service depende apenas desta Interface
public interface AuditService {
    void log(String operacao, String detalhes);
}

// O Adapter implementa a escrita em arquivo
@Component
public class FileAuditAdapter implements AuditService {
    public void log(String operacao, String detalhes) {
        try (FileWriter fw = new FileWriter("audit.log", true)) {
            // Escrita no disco
        }
    }
}
```

---

## 🛠️ Tecnologias Utilizadas

* **Java 21**: Linguagem base.
* **Spring Boot 3.5.5**: Framework principal.
* **Spring Data JPA**: Persistência.
* **H2 Database**: Banco em memória.
* **Thymeleaf + Bootstrap 5**: Frontend.
* **Maven**: Gestão de dependências.

---

## ⚙️ Como Executar

### Pré-requisitos

* Java 21 instalado.

### Passos

1.  Clone o repositório.
2.  Na raiz do projeto, execute via terminal:
    * **Linux/Mac:** `./mvnw spring-boot:run`
    * **Windows:** `./mvnw.cmd spring-boot:run`
3.  Acesse o Dashboard: `http://localhost:8080`

### 🧪 Executando Testes Unitários

O projeto inclui testes automatizados para validar os Design Patterns.

```bash
./mvnw test
```

---

## 🔌 Endpoints da API

Para integrações externas (Postman/Insomnia), a URL base é `/userService/users`.
**Nota:** É obrigatório enviar o header `x-tenant`.

| Verbo | Endpoint | Descrição |
| :--- | :--- | :--- |
| `POST` | `/` | Cria usuário (JSON body) |
| `GET` | `/` | Lista usuários do tenant |
| `PUT` | `/{id}` | Atualiza usuário |
| `DELETE` | `/{id}` | Remove usuário |

---

## 📂 Estrutura de Pastas Relevante

```text
src/main/java/br/com/project/userService
├── adapter      # Pattern Adapter (FileAuditAdapter)
├── controller   # Controllers (API e Web)
├── factory      # Pattern Factory (UserFactory)
├── service      # Regras de Negócio
├── strategy     # Pattern Strategy (PasswordStrategy)
└── tenant       # TenantFilter e Resolver (ThreadLocal)
```

**Projeto desenvolvido por Leonardo Del Nero, João Victor, João Pedro Freire e Gustavo Pereira Gomes para a disciplina de Técnicas de Programação II.**
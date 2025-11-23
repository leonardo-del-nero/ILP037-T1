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
A aplicação segue a **Layered Architecture** (Camadas) típica do Spring Boot, mas enriquecida para suportar o modelo híbrido, separando claramente as responsabilidades de Interface Gráfica e API.

### 1. MVC (Model-View-Controller) - Frontend
Utilizado para o Dashboard Administrativo (Server-Side Rendering).

* **Controller (`C`):** `.../controller/WebController.java`
    * *Responsabilidade:* Intercepta requisições do navegador, gerencia a sessão do tenant e popula o `Model`.
* **View (`V`):** `src/main/resources/templates/`
    * *Responsabilidade:* Arquivos HTML (`index.html`, `users.html`) renderizados dinamicamente pelo Thymeleaf.
* **Model (`M`):** Objeto `Model` do Spring
    * *Responsabilidade:* Transporta dados (ex: `UserDTO`, `currentTenant`) do Controller para a View.

### 2. CSR (Controller-Service-Repository) - API REST
Utilizado no núcleo do backend para regras de negócio e endpoints JSON.

* **Controller (`C`):** `.../controller/UserController.java`
    * *Responsabilidade:* Ponto de entrada da API. Recebe JSON, valida inputs e retorna códigos HTTP.
* **Service (`S`):** `.../service/UserService.java`
    * *Responsabilidade:* Camada agnóstica onde residem as regras de negócio e a orquestração dos Design Patterns.
* **Repository (`R`):** `.../repository/UserRepository.java`
    * *Responsabilidade:* Interface de comunicação com o banco de dados via Spring Data JPA.

---

> **Nota sobre C4 Model:** Os diagramas de contexto e container (Nível 1 e 2) exigidos na atividade encontram-se na pasta `/docs` do repositório.


## 🏗️ Design Patterns Aplicados

Detalhes da refatoração focando na **justificativa** de cada escolha para resolver problemas de acoplamento.

### 1. Strategy (Comportamental) - Validação de Senha

* **Problema**: Regras de validação (tamanho, regex) rígidas dificultavam a troca de políticas de segurança sem alterar a classe principal.
* **Justificativa (Por que usar?)**: O padrão permite trocar a "estratégia" de validação em tempo de execução ou por configuração, respeitando o princípio **Open/Closed (OCP)**.
* **Implementação**: O Service delega a validação para a interface `PasswordStrategy`.

```java
// Interface
public interface PasswordStrategy {
    void validate(String password);
}

// Implementação Concreta (Strategy)
@Component
@Primary
public class StrongPasswordStrategy implements PasswordStrategy {
    public void validate(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Erro: Senha fraca.");
        }
        // ... validação de regex
    }
}
```

### 2. Factory (Criacional) - Criação de Usuário

* **Problema**: A criação de usuários envolvia lógica complexa (conversão DTO, hash de senha, roles padrão) espalhada pelo Service.
* **Justificativa (Por que usar?)**: Centraliza a complexidade de instanciação e garante a **consistência** dos dados. A Factory assegura que *nenhum* usuário seja criado no sistema sem que a senha passe pelo algoritmo de hash (BCrypt).
* **Implementação**: Classe `UserFactory` encapsula o `PasswordEncoder`.

```java
@Component
@RequiredArgsConstructor
public class UserFactory {
    private final PasswordEncoder passwordEncoder;

    public UserEntity createEntityFromDTO(UserDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.getUsername());
        // A Factory aplica a regra de segurança obrigatoriamente
        entity.setPassword(passwordEncoder.encode(dto.getPassword())); 
        entity.setRoles(dto.getRoles());
        return entity;
    }
}
```

### 3. Adapter (Estrutural) - Sistema de Auditoria

* **Problema**: O Service dependia diretamente de bibliotecas de I/O (`java.io.FileWriter`), dificultando testes e acoplando o negócio ao sistema de arquivos.
* **Justificativa (Por que usar?)**: Desacopla a regra de negócio da infraestrutura. O Service depende apenas da abstração (`AuditService`), permitindo que a implementação concreta (Arquivo, Banco, Console) seja trocada sem impacto no código core.
* **Implementação**: O `FileAuditAdapter` adapta a interface de domínio para a escrita física.

```java
// Interface (Domínio)
public interface AuditService {
    void log(String operacao, String detalhes);
}

// Adapter (Infraestrutura)
@Component
public class FileAuditAdapter implements AuditService {
    public void log(String operacao, String detalhes) {
        try (FileWriter fw = new FileWriter("audit.log", true)) {
            // Adaptação para escrita em disco
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
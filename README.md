# User Service - Multi-tenant Application

Este é um projeto **Spring Boot** que fornece um sistema completo de gestão de usuários. O projeto foi desenhado para demonstrar a aplicação prática de **Design Patterns (GoF)**, arquitetura **Multi-tenant**, e desenvolvimento Web com **Thymeleaf**.

O sistema possui tanto uma **API REST** quanto uma **Interface Web (Dashboard)** para administração.

## 🚀 Principais Características

* **Arquitetura Multi-Tenant**: Isolamento de dados baseado em coluna (`discriminator`) via header `x-tenant` ou sessão web.
* **Design Patterns**: Aplicação de padrões Criacionais, Estruturais e Comportamentais.
* **Interface Web Responsiva**: Dashboard administrativo criado com Thymeleaf e Bootstrap 5.
* **Auditoria**: Sistema de log de operações em arquivo (`audit.log`) via Adapter.
* **Segurança**: Hashing de senhas com BCrypt e validação de força de senha customizável.
* **API REST**: Endpoints documentados para integração externa.

---

## 🏗️ Design Patterns Aplicados

O projeto foca na utilização de boas práticas de Engenharia de Software. Abaixo estão os detalhes e exemplos de código da implementação:

### 1. Strategy (Comportamental)
* **Problema**: A necessidade de validar senhas com regras que podem mudar (ex: senha forte, senha simples, validação corporativa).
* **Solução**: Interface `PasswordStrategy`.
* **Implementação**: A classe `StrongPasswordStrategy` encapsula a lógica de validação.
* **Princípio Open/Closed (OCP)**: Esta implementação respeita o princípio Open/Closed do SOLID. O sistema está **aberto para extensão** (podemos criar uma `SimplePasswordStrategy` ou `CorporatePasswordStrategy`) mas **fechado para modificação** (não precisamos alterar o código do `UserService` para mudar a regra de validação).

**Exemplo de Código:**
```java
// Interface (Contrato)
public interface PasswordStrategy {
    void validate(String password);
}

// Implementação Concreta (Estratégia)
@Component
@Primary
public class StrongPasswordStrategy implements PasswordStrategy {
    @Override
    public void validate(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("A senha deve ter pelo menos 8 caracteres.");
        }
        // ... outras validações (regex, etc)
    }
}
```

### 2. Factory (Criacional)
* **Problema**: A criação de objetos de domínio (`UserEntity`) e DTOs (`UserDTO`) estava acoplada e espalhada pelo código, misturando lógica de hash de senha.
* **Solução**: Classe `UserFactory`.
* **Implementação**: Centraliza a conversão `DTO <-> Entity` e a regra de encriptação da senha no momento da criação da entidade, removendo essa responsabilidade do Service.

**Exemplo de Código:**
```java
@Component
@RequiredArgsConstructor
public class UserFactory {
    
    private final PasswordEncoder passwordEncoder;

    // Encapsula a criação e a regra de criptografia
    public UserEntity createEntityFromDTO(UserDTO dto) {
        UserEntity entity = new UserEntity();
        entity.setUsername(dto.getUsername());
        // A lógica de hash fica isolada aqui
        entity.setPassword(passwordEncoder.encode(dto.getPassword())); 
        entity.setRoles(dto.getRoles());
        return entity;
    }
}
```

### 3. Adapter (Estrutural)
* **Problema**: O sistema precisava registrar logs de auditoria, mas a implementação concreta (arquivo, banco, API externa) poderia variar ou ser incompatível com a interface de domínio.
* **Solução**: Interface `AuditService` e adaptador `FileAuditAdapter`.
* **Implementação**: O `FileAuditAdapter` adapta a interface de domínio `AuditService` para a escrita em sistema de arquivos (Java IO). O Service apenas chama `.log()` sem conhecer a tecnologia de persistência (arquivo, banco, console).

**Exemplo de Código:**
```java
// Interface esperada pelo sistema (Domain)
public interface AuditService {
    void log(String operacao, String detalhes);
}

// Adaptador que conecta o sistema ao Java IO (File System)
@Component
public class FileAuditAdapter implements AuditService {
    @Override
    public void log(String operacao, String detalhes) {
        // Adapta a chamada simples para a complexidade de IO
        try (FileWriter fileWriter = new FileWriter("audit.log", true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            
            printWriter.printf("[%s] OP: %s | DETALHES: %s%n", 
                LocalDateTime.now(), operacao, detalhes);
                
        } catch (IOException e) {
            System.err.println("Erro ao escrever no log: " + e.getMessage());
        }
    }
}
```

---

## 🛠️ Tecnologias Utilizadas

* **Java 21**: Linguagem base.
* **Spring Boot 3.5.5**: Framework principal.
* **Spring Data JPA / Hibernate**: Persistência de dados.
* **H2 Database**: Banco em memória.
* **Thymeleaf**: Template engine para o Frontend.
* **Bootstrap 5**: Estilização da interface.
* **Maven**: Gestão de dependências.

---

## ⚙️ Como Executar

### Pré-requisitos
* Java 21 instalado.
* Porta `8080` livre.

### Passos para Execução
1.  Clone o repositório.
2.  Na raiz do projeto, execute via terminal:

    **Linux/macOS:**
    ```bash
    ./mvnw spring-boot:run
    ```

    **Windows:**
    ```bash
    ./mvnw.cmd spring-boot:run
    ```

3.  Acesse a aplicação em: `http://localhost:8080`

### 🧪 Executando Testes Unitários

O projeto inclui testes automatizados (JUnit/Mockito) para validar as regras de negócio e os padrões implementados. Para executá-los:

**Linux/macOS:**
```bash
./mvnw test
```

**Windows:**
```bash
./mvnw.cmd test
```

---

## 🖥️ Interface Web e Multi-tenancy

O sistema possui um mecanismo inteligente de gestão de tenants:

1.  **Seleção de Tenant**: Ao acessar a home, você pode selecionar ou criar um "Tenant" (organização).
2.  **Sessão**: O tenant selecionado é salvo na sessão do navegador.
3.  **Isolamento**: Todos os usuários criados ou listados pertencem exclusivamente ao tenant ativo.
4.  **Fallback**: Se nenhum tenant for definido, o sistema tenta usar o tenant padrão `bradev`.

---

## 🔌 Endpoints da API

Para integrações via Postman/Insomnia, utilize a URL base `/userService/users`.
**Nota:** É obrigatório enviar o header `x-tenant` nas requisições da API.

| Método | Endpoint | Descrição | Header Obrigatório |
| :--- | :--- | :--- | :--- |
| `GET` | `/` | Lista usuários do tenant. | `x-tenant: clienteA` |
| `POST` | `/` | Cria novo usuário. | `x-tenant: clienteA` |
| `GET` | `/{id}` | Busca usuário por ID. | `x-tenant: clienteA` |
| `PUT` | `/{id}` | Atualiza usuário. | `x-tenant: clienteA` |
| `DELETE` | `/{id}` | Remove usuário. | `x-tenant: clienteA` |

---

## 📂 Estrutura de Pastas Relevante

```text
src/main/java/br/com/project/userService
├── adapter      # Padrão Adapter (AuditService)
├── controller   # Controladores Web e API
├── factory      # Padrão Factory (UserFactory)
├── service      # Regras de Negócio
├── strategy     # Padrão Strategy (PasswordStrategy)
└── tenant       # Filtros e Resolver de Multi-tenancy
```
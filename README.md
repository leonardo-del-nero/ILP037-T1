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

O projeto foca na utilização de boas práticas de Engenharia de Software através dos seguintes padrões:

### 1. Strategy (Comportamental)
* **Problema**: A necessidade de validar senhas com regras que podem mudar (ex: senha forte, senha simples, validação corporativa).
* **Solução**: Interface `PasswordStrategy`.
* **Implementação**: A classe `StrongPasswordStrategy` encapsula a lógica de validação (mínimo 8 caracteres, caracteres especiais), permitindo trocar a política de segurança sem alterar o `UserService`.

### 2. Factory (Criacional)
* **Problema**: A criação de objetos de domínio (`UserEntity`) e DTOs (`UserDTO`) estava acoplada e espalhada pelo código, misturando lógica de hash de senha.
* **Solução**: Classe `UserFactory`.
* **Implementação**: Centraliza a conversão `DTO <-> Entity` e a regra de encriptação da senha no momento da criação da entidade, removendo essa responsabilidade do Service.

### 3. Adapter (Estrutural)
* **Problema**: O sistema precisava registrar logs de auditoria, mas a implementação concreta (arquivo, banco, API externa) poderia variar.
* **Solução**: Interface `AuditService` e adaptador `FileAuditAdapter`.
* **Implementação**: O `FileAuditAdapter` adapta a interface de domínio `AuditService` para a escrita em sistema de arquivos (Java IO), permitindo que o Service apenas chame `.log()` sem conhecer a tecnologia de persistência do log.

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

### Passos
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

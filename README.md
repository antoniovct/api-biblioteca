# 📚 Sistema de Biblioteca

> Um sistema de gerenciamento de biblioteca feito com Spring Boot. Permite cadastro de usuários, empréstimo e reserva de livros, com controle de validação de e-mails e permissões diferenciadas para ADMIN e LEITOR.


---

## 📌 Sobre o Projeto

Este projeto é uma **API RESTful** desenvolvida para gerenciar uma biblioteca, permitindo que os usuários realizem as seguintes ações:

🔹 **Cadastro de usuários**: Criação de novos usuários com validação de e-mail.  
🔹 **Empréstimo e devolução de livros**: Processos de empréstimo e devolução com controle de datas e notificações.  
🔹 **Reserva de livros**: Possibilidade de reservar livros para leitura futura.  
🔹 **Bloqueio de usuários**: O ADMIN pode bloquear usuários para impedir empréstimos e reservas.  
🔹 **Segurança e autenticação**: Utiliza **OAuth2** com JWT para proteger os endpoints da API.

---

## 🚀 Tecnologias Utilizadas

As seguintes tecnologias foram utilizadas no desenvolvimento deste projeto:

- **Spring Boot 3**: Framework para desenvolvimento de microsserviços e APIs RESTful.
- **Spring Data JPA**: Simplifica o acesso a dados e interação com o banco de dados.
- **Spring Security + OAuth2 Resource Server**: Protege os endpoints da API utilizando autenticação baseada em **JWT** (JSON Web Tokens).
- **Spring Web**: Para criar a estrutura de comunicação HTTP da API.
- **Spring Validation**: Para garantir que os dados de entrada estejam corretos e validados.
- **Java Mail Sender**: Envio de e-mails para validar o cadastro do usuário.
- **Flyway Migrations**: Gerenciamento das versões do banco de dados e migrações automáticas.
- **MySQL**: Banco de dados relacional utilizado para armazenar as informações da biblioteca.
- **Spring Boot DevTools**: Para facilitar o desenvolvimento com hot reload e monitoramento.
- **Lombok**: Reduz o boilerplate code gerando automaticamente getters, setters, e construtores.
- **Spring Doc (OpenAPI)**: Geração automática da documentação da API com Swagger.

---

## 🔑 Regras de Negócio

### Roles e Permissões

- **ADMIN**: Possui **todas as permissões**, podendo realizar todas as operações CRUD de usuários e livros, bem como bloquear e desbloquear usuários.
- **LEITOR**: Possui **permissões limitadas**.

### Funcionalidade dos Usuários

- **Cadastro de Usuário**: O cadastro de novos usuários requer informações básicas como nome, e-mail e senha. A senha é **criptografada com Bcrypt**.
- **Validação de E-mail**: Após o cadastro, um **e-mail de validação** é enviado para garantir que o usuário tenha acessado seu e-mail. O código de validação deve ser inserido na aplicação para completar o processo de ativação.
- **Bloqueio de Usuário**: O **ADMIN** pode bloquear um usuário para impedir que ele faça empréstimos ou reservas de livros.
- **Gerenciamento de Usuários**: Somente o **ADMIN** pode realizar operações de CRUD (criação, leitura, atualização e exclusão) de usuários.

### Funcionalidades de Livros

- **Cadastro e Listagem de Livros**: O sistema permite o cadastro e a consulta de livros disponíveis na biblioteca.
- **Listagem Personalizada de Livros**: O sistema permite a busca de livros pelo título ou categoria.
- **Atualização de Livros**: O sistema permite a atualização dos dados de um livro.

### Funcionalidades de Empréstimos de Livros

- **Empréstimo de Livros**: O sistema permite que um usuário só tenha 2 empréstimos ativos e da prioridade dos empréstimos para o usuário que fez a reserva do Livro.
- **Listagem de Empréstimos**: O sistema permite a listagem dos empréstimos.
- **Devolução de Empréstimos**: Na lógica de devolução do livro, o estoque é atualizado automaticamente, assim como as reservas.
- **Renovação de Empréstimos**: O sistema permite o usuário renovar o empréstimo somente se o livro não estiver reservado.

### Funcionalidades de Reservas de Livros

- **Cadastrar nova Reserva**: Um usuário só pode ter até 3 Reservas ativas, acima disso não é permitido cadastrar uma nova.
- **Listagem de Reservas**: O sistema permite a listagem de Reservas, inclusive por status.
- **Reserva de Livros**: O sistema permite a atualização do status de uma Reserva.

### Funcionalidades de Notificações

- **Lembrete de Devolução**: O sistema envia um e-mail para o usuário, informando que o prazo do empréstimo está próximo, um dia antes.
- **Aviso de Atraso**: Diariamente o sistema envia um e-mail para o usuário informando que o prazo de empréstimo já acabou e está atrasado. 
- **Notificar Livro Disponível**: O sistema envia um e-mail notificando o usuário que o livro reservado está disponível.
---

## 🛠️ Como Executar o Projeto

### 📌 Pré-requisitos
Antes de começar, certifique-se de ter instalado em sua máquina:

- **Java 21**
- **Maven**
- **Docker** e **Docker Compose**
- **Postman** (ou outra ferramenta para testar a API, como o Insomnia)

### 🐳 Subindo o Banco de Dados com Docker
A aplicação utiliza **MySQL** como banco de dados e já possui um **docker-compose.yml** configurado. Para subir o banco de dados, execute o seguinte comando na raiz do projeto:

```bash
docker-compose up -d
```
Isso iniciará um contêiner com o banco de dados pronto para uso.

### 📂 Configuração de E-mail
A configuração das credenciais de e-mail deve ser feita no arquivo application.properties. Edite os seguintes parâmetros:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${USERNAME_EMAIL}
spring.mail.password=${PASSWORD_EMAIL}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```
💡 Importante:

- Substitua ${USERNAME_EMAIL} e ${PASSWORD_EMAIL} pelas credenciais de um e-mail válido para envio das notificações.

### 🔐 Acesso à API
Para acessar os endpoints da API, é necessário realizar autenticação. O sistema já contém um usuário ADMIN inserido no banco de dados para facilitar os testes.

#### 🔑 Credenciais do usuário ADMIN:
- E-mail: ```user@admin.com.br```
- Senha: ```usuario```

### 🔓 Como obter o Token JWT
1. Realize login enviando uma requisição POST para o endpoint:
```
POST http://localhost:8080/login
```
2. A autenticação é feita via Basic Auth.
3. O retorno será um token JWT, que deve ser enviado no header das requisições protegidas.

### 📖 Acessando a Documentação da API
A documentação interativa da API está disponível através do ***Spring Doc + Swagger***. Você pode acessá-la pelo seguinte endpoint:

#### 🔗 ```http://localhost:8080/swagger-ui.html```

Nela, você poderá visualizar todos os endpoints disponíveis, testar requisições e verificar os parâmetros esperados.
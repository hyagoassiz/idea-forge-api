# Princípios Gerais

Gere código consistente com o restante do projeto.

Prioridades:

1. Simplicidade.
2. Legibilidade.
3. Reutilização.
4. Consistência com os padrões existentes.

Antes de criar algo novo, verifique se existe uma implementação semelhante que possa ser reutilizada ou evoluída.

Implemente somente o necessário para atender ao requisito atual.

Não antecipe funcionalidades futuras.

Não crie classes, DTOs, services, repositories ou abstrações sem necessidade real.

Não altere código não relacionado à solicitação.

Se houver dúvida sobre uma regra de negócio, peça esclarecimentos antes de implementar.

# Arquitetura

- Siga a estrutura de pacotes existente.
- Organize funcionalidades por domínio de negócio.
- Funcionalidades específicas devem ficar em `modules`.
- Código compartilhado deve ficar em `common`.
- Evite acoplamento desnecessário entre módulos.
- Prefira evoluir implementações existentes antes de criar novas abstrações.
- Crie novos pacotes somente quando forem realmente necessários.
- Não crie pacotes vazios.

# Estrutura

src/main/java/com/ideaforge

├── common/
│ ├── config/
│ ├── exception/
│ ├── security/
│ └── util/
└── modules/
└── user/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/

Exemplo de módulo:

modules/
└── users/
├── controller/
├── service/
├── repository/
├── entity/
├── dto/
└── mapper/

# Java

- Utilize Java 21.
- Nunca utilize `Object` como substituto de um tipo específico.
- Utilize tipos explícitos em métodos públicos e APIs.
- Prefira `record` para DTOs.
- Utilize `class` para entidades, services e repositories.
- Evite casts desnecessários.
- Não duplique modelos existentes.
- Evite valores mágicos.

# Spring Boot

- Utilize arquitetura em camadas.
- Controllers recebem requisições e retornam respostas.
- Services concentram regras de negócio.
- Repositories acessam o banco de dados.
- Entities representam o modelo persistido.
- DTOs representam o contrato da API.
- Não coloque regras de negócio diretamente em controllers ou repositories.

# Controllers

- Utilize `@RestController`.
- Uma responsabilidade por controller.
- Utilize `@Valid` para validar entradas.
- Receba apenas DTOs de Request.
- Retorne apenas DTOs de Response.
- Nunca exponha entidades diretamente.
- Não implemente regras de negócio nos controllers.

# Services

- Centralize toda regra de negócio nos services.
- Services podem utilizar outros services quando necessário.
- Utilize `@Transactional` apenas quando fizer sentido.
- Não faça mapeamentos manuais entre Entity e DTO.
- Utilize os mappers do módulo.

# Repositories

- Utilize Spring Data JPA.
- Não implemente regras de negócio nos repositories.
- Prefira métodos derivados do Spring Data antes de criar consultas JPQL.
- Crie consultas customizadas apenas quando realmente necessário.

# DTOs

- Utilize `record` para DTOs de Request e Response.
- Separe DTOs de entrada e saída.
- Nunca reutilize um DTO de Request como Response.
- Utilize nomes claros e descritivos.
- Toda conversão entre Entity e DTO deve ser realizada com MapStruct.

Exemplos:

`CreateUserRequestDTO`

`LoginRequestDTO`

`UserResponseDTO`

`BoardResponseDTO`

# MapStruct

- Utilize MapStruct para todos os mapeamentos entre Entity e DTO.
- Configure os mappers com `componentModel = "spring"`.
- Mantenha um mapper por módulo.
- Evite mapeamentos manuais repetitivos.
- Adicione apenas os métodos realmente utilizados.

# Validação

- Utilize Jakarta Validation (`jakarta.validation`).
- Mantenha validações estruturais nos DTOs.
- Utilize `@Valid` nos controllers.
- Regras de negócio devem permanecer nos services.

# Exceções

- Crie exceções específicas de domínio quando necessário.
- Centralize o tratamento de erros em `@RestControllerAdvice`.
- Não utilize `try/catch` nos controllers apenas para retornar códigos HTTP.

# Métodos

- Prefira métodos pequenos e com responsabilidade única.
- Utilize nomes descritivos.
- Declare explicitamente o retorno quando não for óbvio.
- Evite duplicação de lógica.
- Extraia métodos privados apenas quando melhorarem a legibilidade.

# Exportações

- Utilize uma única classe pública por arquivo.
- Evite classes utilitárias estáticas quando um Service for mais apropriado.
- Mantenha interfaces apenas quando houver benefício real de abstração.

# Nomenclatura

Utilize inglês para todos os identificadores do código:

- pacotes
- classes
- interfaces
- métodos
- variáveis
- DTOs
- entities
- mappers
- repositories
- services
- enums
- arquivos

Não misture português e inglês no mesmo identificador.

Utilize nomes claros, descritivos e consistentes.

Evite abreviações desnecessárias.

Sempre que possível, siga a nomenclatura adotada pelo ecossistema Java, Spring Boot e Spring Data.

Textos exibidos ao usuário devem utilizar o idioma da aplicação.

# Regra de Decisão

Antes de criar qualquer coisa nova, verifique:

1. Existe código semelhante que possa ser reutilizado?
2. A implementação existente pode ser evoluída?
3. A nova abstração é realmente necessária?
4. Isso é necessário para o requisito atual?

Escolha sempre a solução mais simples que mantenha a arquitetura e os padrões existentes.

# Idea Forge

O Idea Forge segue uma arquitetura incremental.

Implemente apenas o necessário para o requisito atual.

Não antecipe funcionalidades futuras.

Não crie abstrações prematuramente.

Priorize simplicidade, legibilidade, consistência e facilidade de manutenção.

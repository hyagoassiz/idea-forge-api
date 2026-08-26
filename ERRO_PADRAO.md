# Padrão de Respostas de Erro da API

Todas as respostas de erro da API Cash Control seguem um único formato padronizado, independentemente do tipo de erro (validação, regra de negócio ou conflito).

## Estrutura da Resposta

```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Descrição legível do erro",
  "errors": [
    {
      "field": "nome_do_campo",
      "code": "CODIGO_DO_ERRO",
      "message": "Descrição específica do erro"
    }
  ],
  "timestamp": "2026-06-13T14:55:55.734799327Z"
}
```

## Campos

- **status**: Código HTTP de status (400, 409, etc.)
- **code**: Código identificador do erro principal
- **message**: Mensagem descritiva do erro
- **errors**: Lista de erros específicos (sempre uma lista, mesmo com um único erro)
  - **field**: Nome do campo afetado
  - **code**: Código específico do erro do campo
  - **message**: Mensagem descritiva do erro do campo
- **timestamp**: Timestamp ISO 8601 do momento do erro

## Exemplos

### 1. Erro de Validação (Múltiplos Campos)

**Request:**
```bash
POST /api/usuarios
Content-Type: application/json

{
  "nome": "Jo",
  "email": "email-invalido",
  "senha": "123"
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Dados de usuário inválidos",
  "errors": [
    {
      "field": "nome",
      "code": "SIZE_INVALID",
      "message": "Nome deve conter ao menos 3 caracteres"
    },
    {
      "field": "email",
      "code": "EMAIL_INVALID",
      "message": "Email inválido"
    },
    {
      "field": "senha",
      "code": "SIZE_INVALID",
      "message": "Senha deve conter entre 8 e 60 caracteres"
    }
  ],
  "timestamp": "2026-06-13T14:55:55.734799327Z"
}
```

### 2. Erro de Regra de Negócio (Email Já Cadastrado)

**Request:**
```bash
POST /api/usuarios
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@example.com",
  "senha": "SenhaSegura123!"
}
```

**Response (409 Conflict):**
```json
{
  "status": 409,
  "code": "EMAIL_ALREADY_EXISTS",
  "message": "Email já cadastrado",
  "errors": [
    {
      "field": "email",
      "code": "EMAIL_ALREADY_EXISTS",
      "message": "Email já cadastrado"
    }
  ],
  "timestamp": "2026-06-13T14:55:55.734799327Z"
}
```

### 3. Erro de Validação (Um Campo)

**Request:**
```bash
POST /api/usuarios
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@example.com",
  "senha": "      "
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "code": "VALIDATION_ERROR",
  "message": "Dados de usuário inválidos",
  "errors": [
    {
      "field": "senha",
      "code": "FIELD_REQUIRED",
      "message": "Senha é obrigatória"
    }
  ],
  "timestamp": "2026-06-13T14:55:55.734799327Z"
}
```

## Códigos de Erro Mapeados

Os seguintes códigos de validação do Bean Validation são mapeados automaticamente:

| Anotação | Código Mapeado |
|----------|-----------------|
| `@NotBlank` | `FIELD_REQUIRED` |
| `@NotNull` | `FIELD_REQUIRED` |
| `@Email` | `EMAIL_INVALID` |
| `@Pattern` | `PATTERN_INVALID` |
| `@Size` | `SIZE_INVALID` |
| `@Min` | `MIN_VALUE_INVALID` |
| `@Max` | `MAX_VALUE_INVALID` |

## Implementação

Os handlers de exceção estão configurados em:
- **GlobalExceptionHandler**: `/src/main/java/com/cashcontrol/config/GlobalExceptionHandler.java`

### DTOs Utilizados

- **ApiErrorResponse**: `/src/main/java/com/cashcontrol/config/dto/ApiErrorResponse.java`
- **ApiFieldError**: `/src/main/java/com/cashcontrol/config/dto/ApiFieldError.java`

## Exceções Tratadas

1. **EmailAlreadyExistsException** - Quando email já existe no banco
   - Status: 409 (Conflict)
   - Code: EMAIL_ALREADY_EXISTS

2. **MethodArgumentNotValidException** - Erros de validação de request
   - Status: 400 (Bad Request)
   - Code: VALIDATION_ERROR

3. **IllegalArgumentException** - Argumentos inválidos
   - Status: 400 (Bad Request)
   - Code: ILLEGAL_ARGUMENT

## Adicionando Novas Exceções

Para adicionar uma nova exceção e manter o padrão:

1. Crie a classe da exceção (ex: `NovaException.java`)
2. Adicione um `@ExceptionHandler` no `GlobalExceptionHandler`
3. Retorne um `ResponseEntity<ApiErrorResponse>` com o formato padronizado

Exemplo:
```java
@ExceptionHandler(NovaException.class)
public ResponseEntity<ApiErrorResponse> handleNovaException(NovaException ex) {
    List<ApiFieldError> errors = new ArrayList<>();
    errors.add(new ApiFieldError("campo", "NOVO_ERRO", ex.getMessage()));
    
    ApiErrorResponse response = new ApiErrorResponse(
        HttpStatus.BAD_REQUEST.value(),
        "NOVO_ERRO",
        ex.getMessage(),
        errors
    );
    
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
}
```

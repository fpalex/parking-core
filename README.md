# Parking Core

Sistema backend para gerenciamento de um estacionamento: controle de vagas disponíveis, entrada/saída de veículos e cálculo de receita.

## Tecnologias

- Java 21
- Spring Boot 3.4.5
- MySQL 8.0
- Flyway
- MapStruct
- Lombok
- OpenFeign + Resilience4j
- Springdoc OpenAPI (Swagger)
- Docker / Docker Compose

## Arquitetura

O projeto segue uma arquitetura em camadas inspirada nos princípios da Arquitetura Hexagonal, com clara separação de responsabilidades:

```
com.estapar.parking/
├── config/          → Configurações e inicializadores da aplicação
├── controller/      → Camada REST (interface contemplando documentação Swagger + implementação)
├── domain/model/    → Modelos de domínio puros
├── dto/             → DTOs de entrada/saída HTTP
├── exception/       → Exceções customizadas e handler global
├── integration/     → Camada de integrações externas (APIs externas, cloud e etc) 
├── mapper/          → MapStruct (DTO ↔ Domain ↔ Entity)
├── persistence/     → Camada de acesso a dados (implementações + repositórios JPA)
└── service/         → Camada de negócio (Strategy Pattern para eventos)
```

### Decisões de Design

- **Modelos de domínio** são livres de anotações JPA e JSON, mantendo a lógica de negócio independente da infraestrutura
- **Strategy Pattern** é utilizado para o processamento de eventos do webhook (ENTRY, PARKED, EXIT), facilitando a adição de novos tipos de eventos
- **Camada de persistência** encapsula o acesso aos dados, expondo apenas modelos de domínio para a camada de serviço
- **Camada de integração** encapsula integrações externas e converte as respostas em modelos de domínio

## Pré-requisitos

- Java 21
- Docker e Docker Compose
- Maven 3.8+

## Executando o projeto

### 1. Inicie o banco de dados e o simulador

```bash
docker-compose up -d
```

### 2. Execute a aplicação

```bash
mvn spring-boot:run
```

A aplicação iniciará na porta `3003` e buscará automaticamente a configuração da garagem no simulador ao subir.

## Documentação da API

O Swagger UI está disponível em:

```
http://localhost:3003/swagger-ui/index.html
```

## Endpoints

### Webhook — Receber eventos da garagem

```
POST /webhook
```

Aceita eventos do simulador de garagem. O campo `event_type` determina quais outros campos são obrigatórios.

#### Atributos

| Campo | Tipo | Obrigatório | Descrição |
|-------|------|-------------|-----------|
| `license_plate` | `string` | ✅ Sempre | Placa do veículo |
| `event_type` | `string` | ✅ Sempre | Tipo do evento: `ENTRY`, `PARKED` ou `EXIT` |
| `entry_time` | `datetime` | ✅ ENTRY | Horário de entrada do veículo |
| `lat` | `double` | ✅ PARKED | Latitude da vaga onde o veículo estacionou |
| `lng` | `double` | ✅ PARKED | Longitude da vaga onde o veículo estacionou |
| `exit_time` | `datetime` | ✅ EXIT | Horário de saída do veículo |

#### Exemplo — ENTRY

```bash
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "license_plate": "ABC1234",
    "entry_time": "2026-05-15T12:00:00.000Z",
    "event_type": "ENTRY"
  }'
```

#### Exemplo — PARKED

```bash
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "license_plate": "ABC1234",
    "lat": -23.561684,
    "lng": -46.655981,
    "event_type": "PARKED"
  }'
```

#### Exemplo — EXIT

```bash
curl -X POST http://localhost:3003/webhook \
  -H "Content-Type: application/json" \
  -d '{
    "license_plate": "ABC1234",
    "exit_time": "2026-05-15T14:00:00.000Z",
    "event_type": "EXIT"
  }'
```

#### Resposta

| Status | Descrição |
|--------|-----------|
| `200` | Evento processado com sucesso |
| `400` | Requisição inválida (campo obrigatório ausente ou tipo de evento desconhecido) |
| `422` | Violação de regra de negócio (ex: garagem cheia, veículo já dentro, vaga ocupada) |
| `500` | Erro interno do servidor |

---

### Receita — Consultar faturamento por setor e data

```
GET /revenue
```

#### Parâmetros

| Parâmetro | Tipo | Obrigatório | Descrição |
|-----------|------|-------------|-----------|
| `sector` | `string` | ✅ Sim | Nome do setor (ex: `A`, `B`) |
| `date` | `date` | ✅ Sim | Data no formato `yyyy-MM-dd` |

#### Exemplo

```bash
curl -X GET "http://localhost:3003/revenue?sector=A&date=2026-05-15"
```

#### Resposta

```json
{
  "amount": 121.50,
  "currency": "BRL",
  "timestamp": "2026-05-15T14:30:00.000Z"
}
```

| Campo | Tipo | Descrição |
|-------|------|-----------|
| `amount` | `decimal` | Valor total faturado no setor na data informada |
| `currency` | `string` | Código da moeda (sempre `BRL`) |
| `timestamp` | `datetime` | Horário em que a consulta foi realizada |

| Status | Descrição |
|--------|-----------|
| `200` | Faturamento retornado com sucesso |
| `400` | Parâmetros inválidos ou ausentes |
| `422` | Setor não encontrado |
| `500` | Erro interno do servidor |

---

## Regras de Negócio

### Precificação dinâmica

O multiplicador de preço é calculado no momento da entrada com base na ocupação atual do setor:

| Lotação | Ajuste de preço |
|---------|----------------|
| Abaixo de 25% | 10% de desconto |
| De 25% a 50% | Sem ajuste |
| De 50% a 75% | 10% de acréscimo |
| Acima de 75% | 25% de acréscimo |

### Cobrança

- Os primeiros **30 minutos** são gratuitos
- Após 30 minutos, é cobrada uma tarifa fixa por hora (arredondada para cima)
- A tarifa é baseada no `basePrice` do setor multiplicado pelo multiplicador calculado na entrada

### Capacidade

- Quando um setor atinge 100% de ocupação, novas entradas são rejeitadas até que uma vaga seja liberada
- Não é permitida a entrada do mesmo veículo duas vezes sem que haja uma saída

## Executando os testes

```bash
mvn test
```

## Variáveis de ambiente

As seguintes propriedades devem ser configuradas no `application.properties`:

| Propriedade | Descrição |
|-------------|-----------|
| `server.port` | Porta da aplicação |
| `simulator.url` | URL do simulador de garagem |
| `spring.datasource.url` | URL de conexão com o MySQL |
| `spring.datasource.username` | Usuário do MySQL |
| `spring.datasource.password` | Senha do MySQL |
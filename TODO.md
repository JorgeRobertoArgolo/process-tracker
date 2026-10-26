# TODO

Cada item fica pronto com teste verde e commit próprio.

## Configuração

- [x] Trocar `spring-boot-starter-aop` por `spring-boot-starter-aspectj` (Boot 4)
- [x] `.gitignore`, `LICENSE` e `README.md`
- [x] Primeiro commit e push para o GitHub

## Fase 1: Fundamentos

### Semana 1: POO, polimorfismo e dynamic dispatch (teoria concluída)

Foco: a JVM decide em tempo de execução qual código roda, sem `instanceof` e com design fail-closed.
Nomes em inglês; entre parênteses, o nome original da tarefa.

- [x] Classe abstrata `MunicipalProcess` (`Protocolo`) com `id`, `ticketNumber` (`codigoProtocolo`) e `creationDate` (`dataCriacao`)
- [x] Subclasses `BuildingPermit` (`AlvaraConstrucao`), `SanitaryLicense` (`LicencaSanitaria`) e `EventAuthorization` (`AutorizacaoEvento`)
- [x] `public boolean isDirectApprovalAllowed()` (`isAprovacaoDiretaPermitida`) na base retornando `false`; sobrescrever só em `EventAuthorization`, sob condições definidas
- [x] `public abstract BigDecimal calculateIssuanceFee()` (`calcularTaxaExpedicao`); cada subclasse implementa o próprio cálculo
- [x] `MunicipalProcessingService` (`TramitacaoService`) com `process(List<MunicipalProcess> processes)` (`processar`)
- [x] O loop do serviço chama apenas `calculateIssuanceFee()` e `isDirectApprovalAllowed()`. Proibido `instanceof`: comportamento específico vira gancho polimórfico na base
- [ ] Teste JUnit: subclasse `TreePruning` (`PodaArvore`) sem sobrescrever `isDirectApprovalAllowed()` deve retornar `false`
  - Sugestões além da tarefa:
  - [ ] `TreePruning` passando pelo serviço cai em `pendingReview`
  - [ ] Cenário misto: taxa total `105.00` (`isEqualByComparingTo`) e só o evento em `directlyApproved`
  - [ ] `EventAuthorization` com público `0` lança `IllegalArgumentException`
  - [ ] `EventAuthorization` no limite: 500 aprova direto; 501 ou bloqueio de rua não

Ajustes no código já escrito:

- [x] Corrigir o pacote `muncipalprocess` para `municipalprocess`
- [x] Remover `canBeViewedByUser` (não faz parte da tarefa) dos quatro arquivos
- [x] Tirar `System.out.println` do modelo
- [x] Identidade imutável: sem `@Setter`/`@NoArgsConstructor` em `id`, `ticketNumber` e `creationDate`; construtor `protected` na base
- [x] `UUID.randomUUID()` no lugar de `new UUID(0, 0)`: hoje todo processo tem o mesmo id e o mesmo número de protocolo
- [x] Valores monetários com `new BigDecimal("50.00")`, não `BigDecimal.valueOf(50f)` (`float` gera imprecisão)
- [x] `process(...)` devolver um `ProcessingResultResponseDTO` (taxa total, aprovados direto, pendentes de análise), sem imprimir
- [x] Renomear `uuid` para `id`
- [x] Sem espaço antes do parêntese
- [x] Campos `final` em `MunicipalProcess`
- [ ] `"PROT-" + this.id` sem `new String(...)`
- [x] `isDirectApprovalAllowed()` sem parâmetro: a condição vem do estado de `EventAuthorization`, não de uma `String` passada por quem chama

> Semanas 2 a 9: sugestões minhas, ainda não conferidas com o seu roteiro. Substituir pelas tarefas originais de cada semana.

### Semana 2: interfaces, generics (PECS) e type erasure

- [ ] `StrategyRegistry<K, S>`: recebe `List<S>` e `Function<S, K>`, monta `Map<K, S>` e falha com chave duplicada
- [ ] Método com `? extends` (lê processos) e outro com `? super` (adiciona processos), com testes para os subtipos
- [ ] Teste que lê o tipo genérico de um parâmetro com `ResolvableType` (o que sobrevive ao erasure)

### Semana 3: SOLID e OCP

- [ ] Versão "antes": `MunicipalProcessingService` com `instanceof`/`switch` por tipo de processo (commit separado)
- [ ] Versão "depois": refatorar para polimorfismo/Strategy, mantendo os testes verdes
- [ ] Adicionar um 4º tipo de processo e comparar quantos arquivos mudam antes e depois

### Semana 4: IoC, proxies e auto-invocação

- [ ] Transformar os validadores em beans e injetar `List<StageValidator>` por construtor
- [ ] Teste: ordem da lista com e sem `@Order`, e o que acontece com zero beans
- [ ] Anotação `@Audited` + aspecto: provar que a chamada via proxy intercepta e via `this.metodo()` não
- [ ] Mesma prova com `@Async` (requer `@EnableAsync`)
- [ ] Opcional: `@Transactional` (adicionar `spring-boot-starter-jdbc` + `h2` ao `pom.xml`)

## Fase 2: Padrões de projeto (planejada)

- [ ] Semana 5: Strategy com `supports()` e os vizinhos (State, Template Method, Chain)
- [ ] Semana 6: Strategy por chave (`Map`), `@Order` e `ProblemDetail` (RFC 9457)
- [ ] Semana 7: Provider/SPI com `ServiceLoader` e com `List<Provider>` do Spring
- [ ] Semana 8: Specifications e Criteria API (adicionar JPA + banco ao `pom.xml`)
- [ ] Semana 9: regras de arquitetura com ArchUnit (adicionar `archunit-junit5` ao `pom.xml`)

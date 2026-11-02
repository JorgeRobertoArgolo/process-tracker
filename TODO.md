# TODO

Cada item fica pronto com teste verde e commit próprio.

## Configuração

- [x] Trocar `spring-boot-starter-aop` por `spring-boot-starter-aspectj` (Boot 4)
- [x] `.gitignore`, `LICENSE` e `README.md`
- [x] Primeiro commit e push para o GitHub

## Fase 1: Fundamentos

### Semana 1: POO, polimorfismo e dynamic dispatch ✅ concluída

Foco: a JVM decide em tempo de execução qual código roda, sem `instanceof` e com design fail-closed.
Nomes em inglês; entre parênteses, o nome original da tarefa.

- [x] Classe abstrata `MunicipalProcess` (`Protocolo`) com `id`, `ticketNumber` (`codigoProtocolo`) e `creationDate` (`dataCriacao`)
- [x] Subclasses `BuildingPermit` (`AlvaraConstrucao`), `SanitaryLicense` (`LicencaSanitaria`) e `EventAuthorization` (`AutorizacaoEvento`)
- [x] `public boolean isDirectApprovalAllowed()` (`isAprovacaoDiretaPermitida`) na base retornando `false`; sobrescrever só em `EventAuthorization`, sob condições definidas
- [x] `public abstract BigDecimal calculateIssuanceFee()` (`calcularTaxaExpedicao`); cada subclasse implementa o próprio cálculo
- [x] `MunicipalProcessingService` (`TramitacaoService`) com `process(List<MunicipalProcess> processes)` (`processar`)
- [x] O loop do serviço chama apenas `calculateIssuanceFee()` e `isDirectApprovalAllowed()`. Proibido `instanceof`: comportamento específico vira gancho polimórfico na base
- [x] Teste JUnit: subclasse `TreePruning` (`PodaArvore`) sem sobrescrever `isDirectApprovalAllowed()` deve retornar `false`
  - Sugestões além da tarefa:
  - [x] `TreePruning` passando pelo serviço cai em `pendingReview`
  - [x] Cenário misto: taxa total `105.00` (`isEqualByComparingTo`) e só o evento em `directlyApproved`
  - [x] `EventAuthorization` com público `0` lança `IllegalArgumentException`
  - [x] `EventAuthorization` no limite: 500 aprova direto; 501 ou bloqueio de rua não

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
- [x] `"PROT-" + this.id` sem `new String(...)`
- [x] `isDirectApprovalAllowed()` sem parâmetro: a condição vem do estado de `EventAuthorization`, não de uma `String` passada por quem chama

### Semana 2: Interfaces modernas, generics (PECS) e type erasure

Foco: escrever APIs que aceitam subtipos sem cast, entender por que um `List<EventAuthorization>` não é um `List<MunicipalProcess>` e saber o que o compilador apaga (e o que sobra para o Spring ler).

Regra de ouro: nenhum cast (`(EventAuthorization) process`), nenhum raw type (`List` sem `<>`) e nenhum `@SuppressWarnings("unchecked")`. Se precisar de um deles, a assinatura genérica está errada.

Interfaces modernas:

- [x] Interface `Chargeable` em `municipalprocess/model` com `BigDecimal calculateIssuanceFee()`. `MunicipalProcess` passa a implementá-la, e a declaração abstrata sai da base (a interface já obriga as subclasses). Motivo (Item 20): algo cobrável que não é processo, como uma multa, pode implementá-la sem herdar de `MunicipalProcess`
- [x] `default boolean isFeeExempt()` em `Chargeable`: `true` quando a taxa é zero
- [x] `static BigDecimal sumFees(Collection<? extends Chargeable> items)` em `Chargeable`; o serviço passa a usá-lo para a taxa total
- [x] Método `private static` em `Chargeable` (Java 9+) que normaliza a escala monetária (2 casas, `RoundingMode.HALF_EVEN`), usado por `sumFees`. Lista vazia soma `0.00`, não `0`

Generics e PECS:

- [ ] Experimento antes de mudar o serviço: passe para `process(...)` uma **variável** `List<EventAuthorization>` e anote o erro de compilação num comentário do teste. `List.of(...)` direto no argumento compila por inferência e esconde o problema
- [ ] `process(...)` passa a receber `List<? extends MunicipalProcess>`, na interface e na implementação: o serviço só lê a lista (produtora → `extends`)
- [ ] Método genérico `<T extends MunicipalProcess> List<T> filter(Collection<? extends T> processes, Predicate<? super T> criterion)` no serviço e na interface: devolve o mesmo subtipo que recebeu, e o critério só consome (consumidor → `super`)
- [ ] Classe genérica `Registry<K, V>` num pacote `shared`:
  - fábrica `static <K, V> Registry<K, V> of(Collection<? extends V> values, Function<? super V, ? extends K> keyExtractor)`
  - `Optional<V> find(K key)`
  - chave duplicada lança `IllegalArgumentException` com a chave na mensagem (`Collectors.toUnmodifiableMap` lança `IllegalStateException`, então trate antes)
  - uso: `Registry.of(processes, MunicipalProcess::getTicketNumber)`. Vira a base do Strategy por chave da Semana 6

Type erasure:

- [ ] Experimento: duas sobrecargas `process(List<BuildingPermit>)` e `process(List<EventAuthorization>)`. Anote o erro (`name clash: ... have the same erasure`) num comentário e desfaça
- [ ] Teste: `new ArrayList<BuildingPermit>().getClass()` é igual a `new ArrayList<EventAuthorization>().getClass()` (o tipo genérico não existe em tempo de execução)
- [ ] Teste com `ResolvableType` (Spring) lendo o tipo genérico do parâmetro de `IMunicipalProcessingService.process`: deve resolver `MunicipalProcess`. É o que sobrevive ao erasure, e é assim que o Spring sabe o que injetar num `List<UmaInterface>`

Testes JUnit:

- [ ] `sumFees` com `List<BuildingPermit>` e com lista mista de processos + `Fine` (classe de teste em `src/test`, como o `TreePruning`, que implementa só `Chargeable`); lista vazia soma `0.00`
- [ ] `isFeeExempt()`: `Fine` de valor zero → `true`; `BuildingPermit` → `false`
- [ ] `process(...)` recebendo a mesma variável `List<EventAuthorization>` que não compilava no experimento
- [ ] `filter` com `List<EventAuthorization>` (100 sem bloqueio, 600 sem bloqueio, 100 com bloqueio) e critério `MunicipalProcess::isDirectApprovalAllowed` declarado como **variável** `Predicate<MunicipalProcess>`: o resultado é atribuído a `List<EventAuthorization>` sem cast e contém só o primeiro
- [ ] `Registry`: encontra pelo `ticketNumber`; chave inexistente → `Optional.empty()`; chave duplicada → `IllegalArgumentException`. Teste também com `String`s (`Registry.of(List.of("a", "a"), s -> s)`) para provar que é genérico

### Semana 3: SOLID na prática e OCP

Foco: sentir a diferença entre código que precisa ser editado a cada caso novo e código que só cresce. Primeiro escreva o jeito errado de propósito, depois refatore.

Regra de ouro (depois da refatoração): um tipo de processo novo é uma classe nova. O único arquivo existente que pode mudar é o enum `ProcessType`, que só identifica os tipos.

Versão "antes" (commit próprio, código ruim de propósito):

- [ ] Enum `ProcessType` (`BUILDING_PERMIT`, `SANITARY_LICENSE`, `EVENT_AUTHORIZATION`) e método abstrato `ProcessType getType()` em `MunicipalProcess`
- [ ] `ReviewDeadlineCalculator` com `switch (process.getType())` devolvendo o prazo de análise em dias corridos (alvará 30, licença 15, evento 5)
- [ ] `RequiredDocumentsResolver` com outro `switch` devolvendo os documentos exigidos. O evento exige "Autorização de trânsito" só quando há bloqueio de rua: repare que o `switch` precisa de um cast para ler `requiresStreetClosure` (aqui o cast é proposital)
- [ ] Adicione o 4º tipo, `StreetVendorLicense` (licença de ambulante: taxa 15.00, prazo 10 dias), e anote quantos arquivos precisou editar. Compare um `switch` expression sem `default` (o compilador acusa o caso faltando) com um que tem `default` (o tipo novo cai nele em silêncio)

Versão "depois":

- [ ] Troque os dois `switch` (code smell *Repeated Switches*) por `abstract int reviewDeadlineDays()` e `abstract List<String> requiredDocuments()` na base, e apague `ReviewDeadlineCalculator` e `RequiredDocumentsResolver`. O `TreePruning` vai precisar implementá-los: é o compilador garantindo o contrato
- [ ] Adicione o 5º tipo, `SignageLicense` (licença de letreiro: taxa 40.00, prazo 20 dias), e compare a contagem de arquivos com a do 4º tipo

Os outros princípios:

- [ ] SRP: `ReviewDeadlineService` com `LocalDate reviewDeadline(MunicipalProcess)` e `boolean isOverdue(MunicipalProcess)`. Só calcula prazo; não formata nem notifica
- [ ] DIP: `ReviewDeadlineService` recebe um `java.time.Clock` no construtor, em vez de chamar `LocalDate.now()`. A data de hoje é uma dependência; escondida, ela deixa o teste sem controle
- [ ] ISP: `IMunicipalProcessingService` tem `process` e `filter`. Anote se algum cliente usaria só um deles e se valeria separar. Não separe sem motivo
- [ ] LSP: teste de contrato parametrizado (`@ParameterizedTest` + `@MethodSource`) com todas as subclasses: taxa não nula, maior ou igual a zero e com 2 casas; prazo maior que zero; documentos não vazios e imutáveis

Testes JUnit:

- [ ] Prazo e documentos de cada tipo, pela versão polimórfica
- [ ] Documentos do evento com e sem bloqueio de rua
- [ ] `isOverdue` com `Clock.fixed(...)`: um dia depois do prazo → `true`; no último dia → `false`

### Semana 4: Spring IoC, proxies dinâmicos e armadilhas de transação

Foco: entender o que o container faz por você e provar, com teste, que anotações como `@Async` e `@Transactional` só funcionam quando a chamada passa pelo proxy.

Regra de ouro: nenhum `new` de bean dentro de outro bean e nenhum `@Autowired` em campo. Dependência entra pelo construtor.

`pom.xml`: nada novo, porque o `spring-boot-starter-aspectj` já está lá. Só o item opcional de `@Transactional` pede `spring-boot-starter-jdbc` e `com.h2database:h2`.

IoC e injeção:

- [ ] `@Service` em `MunicipalProcessingService` e `ReviewDeadlineService`, com `@RequiredArgsConstructor` e `private final`
- [ ] `Clock` como `@Bean` numa classe `@Configuration` (`config/TimeConfig`, com `Clock.systemDefaultZone()`): a dependência da Semana 3 passa a vir do container
- [ ] Interface `IProcessValidator` (`void validate(MunicipalProcess process)`) com duas implementações `@Component`: protocolo começando com `PROT-` e taxa não negativa. `MunicipalProcessingService` recebe `List<IProcessValidator>` e valida cada processo antes de classificar. Os testes existentes passam a criar o serviço com a lista de validadores
- [ ] Experimento: descubra o que o Spring injeta quando **nenhum** bean implementa `IProcessValidator` (lista vazia ou erro?) e registre a resposta num teste
- [ ] Experimento: uma classe com `@Autowired` em campo, criada com `new` num teste sem Spring. Anote o que acontece

Proxies e auto-invocação:

- [ ] Anotação `@Audited` e um `AuditAspect` (`@Aspect` com `@Around`) que registra o nome do método num bean `InMemoryAuditLog`
- [ ] Teste: uma chamada externa a um método `@Audited` é registrada; um método que chama `this.metodoAuditado()` não é. Confirme com `AopUtils.isAopProxy(bean)` e pelo nome da classe (`$$SpringCGLIB`)
- [ ] Experimento: `@Audited` em método `final` e em método `private`. Nenhum dos dois é interceptado; anote por quê
- [ ] Corrija a auto-invocação movendo o método para outro bean. Anote as alternativas (`AopContext.currentProxy()`, autoinjeção com `@Lazy`) e por que mover costuma ser a melhor
- [ ] `@EnableAsync` e um método `@Async` que devolve `CompletableFuture<String>` com o nome da thread: pelo proxy, roda em outra thread; via `this`, roda na mesma
- [ ] Opcional: `@Transactional` com `TransactionSynchronizationManager.isActualTransactionActive()`: `true` pelo proxy, `false` via `this`

Testes JUnit:

- [ ] `@SpringBootTest` para injeção, aspecto e `@Async`. Modelo e regras continuam testados sem Spring
- [ ] Um validador que lança exceção impede o processamento do lote

## Fase 2: Padrões de projeto

### Semana 5: Strategy e os padrões vizinhos

Foco: usar Strategy quando a variação **não** cabe na hierarquia de processos. O desconto na taxa depende do perfil de quem pede (MEI, entidade sem fins lucrativos), não do tipo do processo. Resolver por herança exigiria uma subclasse para cada combinação (5 tipos × 3 perfis = 15 classes).

Regra de ouro: o serviço de contexto nunca conhece as estratégias concretas. Um perfil novo é uma classe `@Component` nova, sem editar nada.

Strategy:

- [ ] Enum `ApplicantProfile` (`STANDARD`, `MICRO_ENTREPRENEUR`, `NON_PROFIT`)
- [ ] Interface `IFeeDiscountStrategy` com `boolean supports(ApplicantProfile profile)` e `BigDecimal apply(BigDecimal baseFee)`
- [ ] Três implementações `@Component` num pacote `strategy`: padrão (sem desconto), MEI (50%) e sem fins lucrativos (isento, `0.00`)
- [ ] `FeeCalculationService` recebe `List<IFeeDiscountStrategy>` e calcula `finalFee(MunicipalProcess process, ApplicantProfile profile)`. Exatamente uma estratégia deve suportar o perfil: nenhuma ou mais de uma lança `IllegalStateException` (fail-fast, em vez de depender da ordem dos beans)
- [ ] Javadoc da interface explicando por que isso é Strategy e não um método polimórfico em `MunicipalProcess`

Vizinhos:

- [ ] Template Method: `AbstractFeeDiscountStrategy` com `public final BigDecimal apply(...)`, que chama o gancho `protected abstract BigDecimal discount(BigDecimal baseFee)` e normaliza o resultado para 2 casas. As três estratégias passam a estendê-la
- [ ] Strategy como função: num teste, implemente o mesmo desconto com `Map<ApplicantProfile, UnaryOperator<BigDecimal>>`. Anote quando uma lambda basta e quando a classe compensa (dependências, nome, teste, descoberta pelo Spring)
- [ ] Anote, no Javadoc ou num comentário, a diferença entre o que você fez e os vizinhos: State (o comportamento muda pelo estado interno do objeto) e Chain of Responsibility (os validadores da Semana 4 rodam todos; numa Chain, um handler pode interromper a cadeia)

Testes JUnit:

- [ ] Cada estratégia sem Spring: 50.00 → 25.00 (MEI) e 50.00 → 0.00 (sem fins lucrativos). Para 35.00 com MEI, o resultado é `17.50`, com 2 casas, e não `17.500` (compare com `isEqualTo`, que considera a escala)
- [ ] `FeeCalculationService` montado à mão (`new` com `List.of(...)`): perfil sem estratégia e perfil com duas estratégias lançam `IllegalStateException`
- [ ] Um `@SpringBootTest` provando que o Spring descobre as três estratégias

### Semana 6: Strategy por chave, `@Order` e OCP no REST

Foco: trocar o "perguntar a cada estratégia" pelo despacho direto pela chave, e levar o OCP para a API: nem o controller nem o tratamento de erros podem crescer com `if`.

Regra de ouro: nenhum `switch` ou `if` por tipo no controller ou no tratamento de erros. Tipo de processo novo = criador novo; erro novo = exceção nova.

`pom.xml`: `spring-boot-starter-webmvc` (o nome do starter web no Boot 4) e, para teste, `spring-boot-starter-webmvc-test`. Em `application.properties`, `spring.mvc.problemdetails.enabled=true`.

Strategy por chave:

- [ ] `IFeeDiscountStrategy` troca `supports(...)` por `ApplicantProfile profile()`, e `FeeCalculationService` monta no construtor um `Registry<ApplicantProfile, IFeeDiscountStrategy>` (o da Semana 2). Chave duplicada passa a falhar na subida da aplicação, e não na primeira requisição
- [ ] Experimento: injete `Map<String, IFeeDiscountStrategy>` e veja que a chave é o nome do bean. Anote por que isso é frágil (renomear a classe muda a chave)
- [ ] `@Order` nos validadores da Semana 4, com teste provando que a execução segue a ordem anotada

API REST (armazenamento em memória por enquanto; vira JPA na Semana 8):

- [ ] `InMemoryProcessRepository` com `ConcurrentHashMap<UUID, MunicipalProcess>`
- [ ] Interface `IProcessCreator` (`ProcessType type()` e `MunicipalProcess create(CreateProcessRequestDTO request)`), com uma implementação por tipo, cada uma no pacote do próprio tipo, registradas por chave com o `Registry`
- [ ] `ProcessController` em `/api/v1/processes`:
  - `POST` cria pelo criador do tipo e devolve `201` com `Location`
  - `GET /{id}` devolve `ProcessResponseDTO` (id, protocolo, tipo, data, taxa e aprovação direta)
  - `GET /{id}/fee?applicantProfile=...` devolve a taxa final da Semana 5
- [ ] Erros como `ProblemDetail` (RFC 9457): `ProcessNotFoundException` (404) e `UnsupportedProcessTypeException` (400) estendendo `ErrorResponseException`, que já carrega o status, sem nenhum handler com `if`. A `IllegalArgumentException` do domínio vira 422 num único `@ExceptionHandler`
- [ ] Anote o trade-off: uma exceção que estende uma classe do Spring Web fica acoplada à camada HTTP

Testes JUnit:

- [ ] `@WebMvcTest` com `MockMvc`: `201` com `Location`; `404` e `422` com `Content-Type: application/problem+json` e os campos `status`, `title` e `detail`
- [ ] Tipo sem criador → `400`
- [ ] Dois criadores para o mesmo tipo → falha ao montar o registro (teste sem Spring)

### Semana 7: Provider e SPI

Foco: diferenciar Strategy (escolher **uma** implementação) de Provider (**juntar** as contribuições de todas). O prazo de análise passa a contar dias úteis, e cada fonte de feriados contribui com as suas datas.

Regra de ouro: o agregador não conhece nenhuma implementação. Uma fonte de feriados nova é uma classe nova, sem editar o agregador.

Provider com Spring:

- [ ] Interface `IHolidayProvider` com `Set<LocalDate> holidaysOf(int year)`, e um Javadoc explicando por que ela é Provider e não Strategy
- [ ] `NationalHolidayProvider` com os feriados nacionais de data fixa e `MunicipalHolidayProvider` com pelo menos uma data local, ambos `@Component`
- [ ] `BusinessCalendar` recebe `List<IHolidayProvider>`, une as contribuições (uma data repetida em duas fontes conta uma vez) e oferece `isBusinessDay(LocalDate)` e `addBusinessDays(LocalDate start, int days)`, pulando fins de semana e feriados
- [ ] `ReviewDeadlineService` passa a calcular o prazo em dias úteis com o `BusinessCalendar`
- [ ] Compare com o `Registry` da Semana 6: lá, chave duplicada é erro; aqui, data duplicada vira união. Anote por que a diferença faz sentido

SPI com Java puro:

- [ ] Experimento com `java.util.ServiceLoader`: um `IHolidayProvider` só de teste, registrado em `src/test/resources/META-INF/services/` (num arquivo com o nome completo da interface) e descoberto por `ServiceLoader.load(IHolidayProvider.class)`
- [ ] Anote as diferenças: o `ServiceLoader` exige construtor público sem argumentos e não injeta dependências; o Spring não lê o arquivo de `META-INF/services`, e o `ServiceLoader` não enxerga os beans do Spring
- [ ] Opcional: `MovableHolidayProvider` com os feriados que dependem da Páscoa (Carnaval, Sexta-feira Santa, Corpus Christi)

Testes JUnit:

- [ ] `BusinessCalendar` sem providers (só pula fins de semana) e com providers (um feriado no meio do prazo empurra a data)
- [ ] A mesma data em dois providers conta uma vez
- [ ] O `ServiceLoader` encontra o provider de teste
- [ ] Prazo de análise que cruza um fim de semana e um feriado

### Semana 8: Spring Data JPA, Specifications e Criteria API

Foco: persistir a hierarquia de processos e montar filtros dinâmicos tipados, em que cada tipo de processo contribui com critérios sobre os próprios campos, sem o núcleo conhecê-los.

Regra de ouro: nenhuma consulta com string concatenada e nenhum campo de subclasse referenciado no núcleo (`municipalprocess`). Filtros de subclasse chegam por provider.

`pom.xml`: `spring-boot-starter-data-jpa`, `com.h2database:h2` (escopo `runtime`) e, para teste, `spring-boot-starter-data-jpa-test`. Opcional: `org.hibernate.orm:hibernate-processor` como processador de anotações, para o metamodelo estático.

Mapeamento:

- [ ] `MunicipalProcess` como `@Entity` com `@Inheritance(strategy = InheritanceType.JOINED)`, e cada subclasse como entidade com a própria tabela
- [ ] Revisite as decisões da Semana 1 e anote os trade-offs: a JPA exige um construtor sem argumentos (pode ser `protected`, com `@NoArgsConstructor(access = AccessLevel.PROTECTED)`) e proíbe campos persistentes `final`
- [ ] `IMunicipalProcessRepository extends JpaRepository<MunicipalProcess, UUID>, JpaSpecificationExecutor<MunicipalProcess>` substitui o `InMemoryProcessRepository`, e os testes da API da Semana 6 continuam verdes
- [ ] Experimento: com `spring.jpa.show-sql=true`, observe o `SELECT` que aparece antes do `INSERT` ao salvar um processo novo. Descubra a causa (o `UUID` é atribuído no construtor) e as saídas (`Persistable`, `@Version`)

Specifications:

- [ ] `ProcessSpecs` com métodos estáticos que devolvem `Specification<MunicipalProcess>`: `hasType(...)`, `createdBetween(from, to)` e `ticketNumberContains(text)`, compostos com `and` e `or`
- [ ] Interface `IProcessFilterProvider` no núcleo, que recebe um `ProcessSearchCriteria` (record) e devolve `Optional<Specification<MunicipalProcess>>`. A implementação de `EventAuthorization`, no pacote do evento, filtra por público máximo e por bloqueio de rua com `criteriaBuilder.treat(...)`
- [ ] `GET /api/v1/processes` com os filtros como parâmetros e paginação (`Pageable`)
- [ ] Opcional: trocar as strings (`"ticketNumber"`) pelo metamodelo estático (`MunicipalProcess_.ticketNumber`)

Proxies do Hibernate (a fragilidade do `instanceof`):

- [ ] Teste: salve um `EventAuthorization`, limpe o contexto de persistência e busque com `getReferenceById(...)`. Verifique se o `instanceof EventAuthorization` falha no proxy, se `Hibernate.unproxy(...)` resolve e se `isDirectApprovalAllowed()` responde certo mesmo pelo proxy. O polimorfismo da Semana 1 sobrevive ao proxy; o `instanceof`, não

Testes JUnit (`@DataJpaTest`):

- [ ] Cada Specification isolada e as combinações com `and` e `or`
- [ ] O filtro de público combinado com `or` não pode esconder processos de outros tipos: `createdBetween(hoje, hoje).or(filtroDoEvento)` ainda devolve o alvará criado hoje
- [ ] Paginação: total de elementos e tamanho da página

### Semana 9: Arquitetura como código com ArchUnit

Foco: transformar as regras das semanas anteriores em testes que quebram o build quando alguém as viola.

Regra de ouro: toda regra de arquitetura que você anotou (no README, num Javadoc ou neste TODO) vira teste.

`pom.xml`: `com.tngtech.archunit:archunit-junit5`, escopo `test`, com versão explícita (o Boot não gerencia a versão do ArchUnit).

Regras:

- [ ] `@AnalyzeClasses(packages = "com.jorgeroberto.study.process_tracker", importOptions = ImportOption.DoNotIncludeTests.class)`
- [ ] O núcleo (`municipalprocess`) não depende dos pacotes de tipo (`buildingpermit`, `sanitarylicense`, `eventauthorization` e os novos). Essa regra também pega `instanceof`, cast e import de subclasse
- [ ] `model` não depende de `service` nem de `controller`
- [ ] Pacotes sem ciclos: `slices().matching("..process_tracker.(*)..").should().beFreeOfCycles()`
- [ ] Sem injeção em campo e sem `System.out`: `GeneralCodingRules.NO_CLASSES_SHOULD_USE_FIELD_INJECTION` e `GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS` (a regra da Semana 1)
- [ ] Nomenclatura: interfaces em `..service..` começam com `I`; implementações de `IFeeDiscountStrategy` ficam em `..strategy..` e têm `@Component`; implementações de `IHolidayProvider` terminam com `Provider`
- [ ] Opcional: `layeredArchitecture()` com as camadas controller → service → repository → model

Validando as regras:

- [ ] Quebre cada regra de propósito (um import de `BuildingPermit` no núcleo, um `@Autowired` em campo, um `System.out.println`), confira que o teste falha e desfaça
- [ ] Opcional: `FreezingArchRule`, para adotar uma regra nova sem corrigir todo o código antigo de uma vez (útil em projeto legado)

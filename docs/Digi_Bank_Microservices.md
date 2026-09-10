# Digi Bank Microservices Presentation Walkthrough

Presenter guide for the 20-slide architecture presentation. Each slide below has a complete explanation that can be read directly during the walkthrough.

## Presentation Message

We preserved the Digi Bank business domain, established a modern and testable Spring Boot modular monolith, and then introduced independent service boundaries with explicit distributed patterns and automated runtime validation. The microservices implementation is intentionally educational: it demonstrates the architecture without claiming to provide a complete production platform.

## Slide 1: Digi Bank introduction

The first part established the Spring Boot modular monolith; this section explains how the same banking domain is taken toward a microservices architecture. The continuity is important: we are not replacing the business problem or rewriting the story. We are changing service ownership, deployment boundaries, and the way the system handles distributed calls. The visual shows the title and the architecture context. At this point the focus is the architecture context, not implementation detail. We first establish the cloud architecture context, then we show the delivered Spring Boot baseline.

## Slide 2: Workshop context and scope

The workshop requires a microservices implementation of the Digi Bank domain. The objective is to demonstrate independent services, REST communication, gateway access, Docker orchestration, and distributed patterns. The important point is that this is an educational implementation with a clear scope. We use a single repository and Docker Compose so the whole system remains reproducible for the instructor and the team. The listed business capabilities and delivery technologies establish the workshop scope. The assignment evaluates architecture principles; it does not require every production platform component. Before showing the new topology, we need to separate the wider target architecture from what is actually delivered in this workshop.

## Slide 3: Reference architecture

This is the high-level context for Digi Bank. It shows clients entering through an API gateway, the core banking area, PostgreSQL, and external systems such as identity, sanctions, and payments. This diagram is intentionally broader than the implementation. It gives us the business and integration landscape, but it does not claim that every future module is already implemented. The path runs from the client to the gateway, then to core banking and external systems. We do not describe the future modules as current services. With the context established, we can explain why the project introduces a second delivery model rather than changing the banking scope.

## Slide 4: Spring Boot migration decision

The project moved from the Jakarta EE reference implementation to Spring Boot. The goal was to reduce runtime friction during development, shorten feedback cycles, improve test isolation, and make container and CI execution easier. This is not an argument that Jakarta EE is obsolete. Jakarta EE remains a valid enterprise platform and the WildFly path is retained. Spring Boot was selected here because it makes more of the application runtime and configuration visible in the project itself. The migration arrow shows the preserved WAR and PostgreSQL compatibility. The key phrase is a change in delivery model while preserving the domain model. The next slide compares the two operating models so the reason becomes concrete rather than just a list of framework features.

## Slide 5: Operating model comparison

The Jakarta EE and Spring Boot operating models differ in how the application is run. Jakarta EE is centered on an application-server runtime; Spring Boot allows a fast standalone runtime while still supporting WAR deployment to WildFly. The improvement is the shorter distance between source code, local startup, automated tests, and deployment. This gives developers a faster feedback loop without removing the production-like deployment option. The rows describe operating tradeoffs rather than ranking the technologies. The comparison covers embedded Tomcat, application configuration, focused tests, and WAR compatibility. Now that the operating-model decision is clear, we can show the implemented Spring Boot modular monolith that became the baseline for the next migration.

## Slide 6: Implemented Spring Boot architecture

The delivered modular monolith contains one deployable application, explicit business modules, PostgreSQL persistence, Flyway migrations, and a REST entry point. The important architectural choice is restraint. We created explicit module boundaries without prematurely creating distributed operations and network failure inside the first implementation. The visual shows shared concerns, Customer, Account, Transaction, Compliance, Index/UI, the REST API, PostgreSQL, and Flyway. This baseline also gave us the modernization opportunities that improved correctness and testability before the microservice split.

## Slide 7: Modernization changes

The highest-value improvements made during the Spring Boot implementation: BigDecimal for financial values, DTOs at API boundaries, Bean Validation, centralized exception handling, Spring Data repositories, Java 21, Flyway, and focused tests. These changes matter because the migration is not only a framework replacement. It improves financial correctness, API safety, persistence management, developer productivity, and validation quality. The grouped categories keep the explanation focused rather than listing every item. Replacing double with BigDecimal illustrates the improvement because financial values require predictable precision. After modernization, we need a clear version and database strategy so the application and its schema evolve predictably.

## Slide 8: Version and database strategy

The Java, Spring Boot, Maven, WAR, PostgreSQL, and Flyway version strategy is based on explicit application and schema versions. Flyway owns schema changes through ordered migrations; Hibernate validates that the entity model matches the schema. This prevents the application from silently creating or changing production structure. The database has an explicit history, while the application verifies compatibility at startup. The visual shows V1 for the baseline schema, V2 for seed data, and ddl-auto validate. application versioning and database versioning are related but separate concerns. The same artifact must work in both local development and a production-like runtime, which leads to the dual-deployment strategy.

## Slide 9: Dual deployment

One WAR artifact supports two runtime paths: embedded Tomcat for fast local execution and WildFly for production-like compatibility. This avoids an all-or-nothing migration. Developers get a simpler local loop, while the existing application-server deployment remains available for validation and operational continuity. The visual shows the shared WAR and the two runtime branches. SpringBootServletInitializer, Compose profiles, and the WildFly configuration provide the shared deployment path. The implementation is only useful if it is validated beyond compilation, so the next slide shows the delivery and validation pipeline.

## Slide 10: Cloud architecture validation

The validation path from Maven build and unit tests through Docker image creation, Compose startup, readiness checks, smoke tests, Newman API validation, and ZAP baseline scanning. The pipeline demonstrates that validation reaches the running system. We are not claiming success from unit tests alone; we also check integration, runtime availability, API behavior, and basic security posture. The visual shows the sequence from build to reports. Newman and ZAP are independent checks with separate outputs. These results demonstrate what the Spring Boot implementation delivered before we introduce a different ownership model in Workshop 4.

## Slide 11: Workshop outcome

Summarize the first architecture section: Java 21, Maven, Docker, PostgreSQL, a modular monolith, banking capabilities, Spring Web and Data JPA, Flyway, WAR deployment, and layered testing. The conclusion is that the Spring Boot implementation is a complete and validated reference implementation, not just a skeleton. It provides the stable baseline for the microservices work. The three columns connect deliverables, implementation, and why each matters. The next decision is not to discard this baseline, but to explore what changes when capabilities become independently owned services.

## Slide 12: Spring Boot decision

Close the first section by stating that Spring Boot gives Digi Bank an application-owned, testable, and automation-friendly delivery model while retaining the banking domain and WildFly compatibility. This is the bridge to microservices. The modular monolith solved local complexity first; the next section addresses independent service ownership and distributed coordination. Pause after the four conclusion points. Make clear that the MSA section continues the same project story. We now move from the modular monolith baseline to the microservices architecture required by the next workshop.

## Slide 13: Microservices section introduction

Introduce the second section as the next architectural step. The domain remains the same, but selected capabilities now have independent service and database boundaries. The purpose is to demonstrate how the delivery model changes when services can evolve and run independently, and what new problems that introduces. The visual shows the continuity statement: same banking domain, new ownership and delivery boundaries. First we state exactly what changes and what deliberately stays stable.

## Slide 14: MSA migration decision

The migration from the modular monolith to microservices as an ownership decision. Customer, Account, Transaction, Compliance, and Notification become distinct services, while the REST foundation and business scope remain. The benefits are independent ownership, clearer deployment boundaries, and the ability to evolve selected capabilities separately. The cost is distributed coordination, partial failure, and more operational responsibility. The two columns show what stays stable and what changes. We do not claim that microservices are automatically better in every situation. The topology diagram makes those service boundaries and runtime responsibilities concrete.

## Slide 15: Microservices topology

The flow moves from the client to the API gateway, then across the business services. each persistence-owning service has its own database, while discovery and configuration are lightweight platform services. The gateway is the single external entry point. Database ownership prevents shared-table coupling. The platform services demonstrate discovery and configuration without adding unnecessary infrastructure outside the workshop scope. The gateway, each business service, its database, and the platform services. Use the container screenshot as supporting proof that the topology starts locally. A topology shows structure; the next diagram shows what happens when one request moves through that structure.

## Slide 16: Request flow

Walk through one request: the client calls the gateway, the gateway routes to the relevant service, and the transaction workflow coordinates compliance, account, persistence, and notification interactions. Unlike an in-process module call, these are explicit service boundaries. That makes network latency, unavailable dependencies, partial failure, and recovery responsibilities visible. Trace the arrows in order. the transaction database remains owned by the transaction service and that the client does not call internal services directly. Once calls cross process boundaries, we need patterns that make reads, writes, compensation, and fallback explicit.

## Slide 17: Distributed patterns

The three main patterns shown. CQRS separates transaction commands from queries. Saga coordinates transfer steps and compensates when a later step fails. Circuit Breaker protects calls to unavailable downstream services. These patterns are responses to problems introduced by distribution. They do not exist for decoration; they address partial failure, consistency, and resilience. The visual shows the command path, query path, Saga coordinator, Account Service, Compliance Service, Circuit Breaker, and Notification Service. Use Newman as supporting API validation, not as proof of the pattern itself. The final platform concern shown in this section is how services find one another at runtime.

## Slide 18: Service discovery

The registration and lookup sequence. Services register their addresses when they start, and the gateway looks up a service before forwarding a request. This avoids hard-coding every service location in the gateway. The implementation intentionally uses a lightweight registry hosted through Compose rather than introducing Eureka or Spring Cloud infrastructure beyond the workshop scope. The service registrations, discovery server, and gateway lookup show this runtime sequence. The ZAP output is supporting security validation, not part of the discovery mechanism. The architecture is complete only when we can explain how it was validated from code through runtime and security checks.

## Slide 19: MSA validation

the four validation levels: service build and tests, runtime topology, API assertions, and security checks. The pipeline diagram shows the order from build to reports. This proves more than compilation. Compose startup and smoke checks verify the system is reachable, Newman checks API behavior, and ZAP checks common web risks in the running application. The visual shows the pipeline stages and the report image. what each check can and cannot prove; a baseline scan is not a complete security audit. The final slide brings the architecture and validation story together.

## Slide 20: Final microservices takeaway

Conclude that Digi Bank preserved the banking domain while introducing independent service boundaries, explicit distributed patterns, and automated runtime validation. The result is a teachable MSA implementation that demonstrates the required architecture without pretending to be a full production platform. The lightweight registry, config boundary, synchronous REST calls, and single-host Compose setup are deliberate educational simplifications. Return to the core message: the migration changes ownership and delivery, not the business purpose. Mention that the architecture now makes distributed tradeoffs visible. End by inviting questions on the migration decision, service boundaries, or the validation evidence.


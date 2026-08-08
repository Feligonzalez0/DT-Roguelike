# DT Roguelike

Base tecnica inicial de un videojuego web roguelike de gestion de carreras
de Directores Tecnicos de futbol, inspirado en *Potrero*, *El Idolo*,
*Copero* y *Football Manager*. El jugador no controla a un futbolista:
controla a un **Director Tecnico** y construye su carrera, reputacion,
estilo y legado a traves de decisiones.

Este repositorio contiene la **base tecnica del primer MVP**: arquitectura,
modelo de dominio y un primer flujo jugable de punta a punta (crear DT →
elegir club → dashboard con simulacion de partidos). No implementa todavia
todas las mecanicas del juego (eso es intencional, ver "Que queda
pendiente" abajo).

---

## Como ejecutar

Requisitos: **Java 17+** y **Maven 3.8+**.

```bash
# Ejecutar los tests
mvn clean test

# Levantar el servidor
mvn exec:java
```

Despues abrir <http://localhost:4567> en el navegador.

### Flujo jugable actual

```
Inicio
 → Nueva carrera
 → Crear DT (nombre, edad, nacionalidad, estilo)
 → Ver 3-5 ofertas de clubes
 → Elegir club
 → Dashboard (DT, club, temporada)
   → Simular proximo partido
   → Finalizar temporada
```

No hay login, usuarios ni persistencia: el estado de la carrera vive en
memoria mientras el servidor esta corriendo (ver seccion de arquitectura).

---

## Estructura del proyecto

```
src/main/java/com/example/dtroguelike/
├── Main.java                  # arranca Spark y registra rutas
├── config/                    # wiring manual de dependencias + carga de datos estaticos
├── domain/                    # modelo de dominio puro (sin Spark/Mustache/HTTP)
│   ├── achievement/
│   ├── career/
│   ├── club/
│   ├── common/                # GameConstants, GamePhase
│   ├── event/
│   ├── league/
│   ├── manager/
│   ├── match/
│   ├── offer/
│   └── season/
├── engine/                    # orquestacion y simulacion (CareerEngine, MatchSimulator, etc.)
├── application/                # servicios de caso de uso (puente web ↔ engine)
├── infrastructure/
│   ├── data/                  # loaders de JSON (clubs, leagues, events) + DTOs
│   └── repository/            # CareerRepository (interfaz) + InMemoryCareerRepository
└── web/
    ├── controllers/           # logica de request/response, sin reglas de negocio
    ├── routes/                # registro de rutas Spark
    └── viewmodels/            # objetos planos para las vistas Mustache

src/main/resources/
├── templates/                 # vistas Mustache
├── static/css/                # CSS basico tipo dashboard deportivo
└── data/                      # clubs.json, leagues.json, events.json

src/test/java/                 # tests unitarios (JUnit 5)
```

La estructura sigue casi exactamente la pedida originalmente. Una unica
adicion: `domain/common/` para `GameConstants` y `GamePhase`, que no
pertenecen a ningun sub-dominio especifico (Manager, Club, etc.) y se
usan transversalmente; agruparlos evita duplicar constantes o crear
dependencias cruzadas raras entre paquetes de dominio.

---

## Arquitectura

Dependencia conceptual (de arriba hacia abajo):

```
Web  →  Application  →  Engine  →  Domain
Infrastructure  →  Domain / Application
```

Reglas respetadas:

- **`domain/`** no importa Spark, Mustache ni nada de HTTP. Es el modelo
  puro del juego (Manager, Club, Career, Event, etc.).
- **`engine/`** tampoco depende de Spark. Contiene la logica de
  orquestacion y simulacion (`CareerEngine`, `SeasonSimulator`,
  `MatchSimulator`, `EventEngine`, `DecisionResolver`,
  `ReputationEngine`, `ProgressionEngine`, `ClubOfferGenerator`).
- **`application/`** son los casos de uso (`CareerService`,
  `ManagerService`, `SeasonService`, `EventService`, `MatchService`):
  el puente entre los controllers web y el engine/dominio.
- **`web/`** se ocupa unicamente de HTTP y presentacion (Spark +
  Mustache). No contiene reglas de negocio importantes.
- **`infrastructure/`** carga los datos estaticos (JSON) y resuelve la
  persistencia (por ahora, en memoria).

No se usa ningun framework de inyeccion de dependencias: todo el
"wiring" se arma a mano en `config/AppContext.java`. Esto es intencional
(ver seccion "Que NO se uso") para mantener el proyecto simple y facil
de seguir manualmente.

### Estado en memoria

Como no hay usuarios ni login, `InMemoryCareerRepository` guarda una
unica carrera activa en una referencia mutable. La interfaz
`CareerRepository` esta separada de la implementacion justamente para
poder reemplazarla mas adelante por SQLite (o cualquier otra
persistencia) sin tocar el resto del codigo.

---

## Stack tecnologico

- Java 17+
- Maven
- Spark Java (servidor web)
- Mustache (vistas, via `spark-template-mustache`)
- Gson (parseo de los JSON estaticos)
- JUnit 5 (tests)
- Sin base de datos, sin login, sin frameworks de DI, sin frontend SPA.

---

## Que esta implementado

- Modelo de dominio completo segun el diseño pedido: `Manager`,
  `ManagerAttributes`, `ManagerStats`, `ManagerStyle`, `Club`,
  `ClubState`, `ClubExpectations`, `TeamStrength`, `League`, `Career`,
  `CareerState`, `Legacy`, `ClubHistory`, `ClubDepartureReason`,
  `Season`, `SeasonPhase`, `SeasonStats`, `Match` y sus enums,
  `Event`/`EventOption`/`EventCondition`/`Outcome`/`Effect` y sus
  enums, `ClubOffer`, `Achievement`.
- `GameConstants` centraliza los limites y valores por defecto (nada de
  numeros magicos sueltos).
- `GamePhase` para que el frontend sepa que pantalla mostrar.
- Motores (`engine/`) con implementaciones simples pero funcionales:
  - `CareerEngine`: orquesta creacion de carrera, asignacion de club,
    cierre de temporada, despido y retiro.
  - `ClubOfferGenerator`: genera entre 3 y 5 ofertas segun reputacion.
  - `EventEngine`: selecciona un evento al azar de la coleccion
    disponible.
  - `DecisionResolver`: valida requisitos, resuelve exito/fracaso y
    aplica los `Effect` correspondientes sobre el estado real de la
    carrera.
  - `MatchSimulator`: simulacion simple basada en fuerza de plantel +
    atributos del DT + azar.
  - `SeasonSimulator`: permite avanzar partido a partido contra rivales
    de la misma liga (no arma todavia un fixture real).
  - `ReputationEngine` / `ProgressionEngine`: ajustes basicos, con
    varios `TODO` explicitos para iteraciones futuras.
- Capa de aplicacion (`CareerService`, `ManagerService`,
  `SeasonService`, `EventService`, `MatchService`) conectando todo con
  la capa web.
- Carga de datos estaticos desde JSON (`clubs.json` con 14 clubes
  argentinos, `leagues.json`, `events.json` con 3 eventos de ejemplo
  completos con multiples opciones y efectos).
- Flujo web completo: inicio → crear DT → ver ofertas → elegir club →
  dashboard, con botones para simular el proximo partido y finalizar
  la temporada.
- Vistas Mustache simples con un CSS tipo dashboard deportivo (oscuro,
  tarjetas, botones claros).
- Tests unitarios (JUnit 5) cubriendo: creacion de Manager y aplicacion
  de estilo inicial, creacion de Club, arranque de Career, generacion
  de ofertas, seleccion de club, creacion de Season, resolucion de una
  decision de evento (caso exitoso y caso bloqueado por requisitos), y
  simulacion de un Match.

## Que queda pendiente

Todo lo que el alcance original marco explicitamente como fuera del
MVP, entre otras cosas:

- Sistema completo de eventos data-driven con condiciones evaluadas
  contra el estado real (`EventEngine.generateEvent` hoy elige al azar,
  sin filtrar por `conditions`).
- Fixture real de liga (ida y vuelta contra todos los equipos) en
  `SeasonSimulator`, en vez de partidos sueltos contra rivales al azar.
- Simulacion futbolistica mas realista en `MatchSimulator`.
- Calculo completo de reputacion, legado e idolatria
  (`ReputationEngine`, `Legacy` tienen varios `TODO`).
- Progresion de atributos mas rica en `ProgressionEngine` (hoy es un
  incremento fijo de placeholder).
- Sistema de desbloqueo automatico de `Achievement`.
- Mercado de pases, jugadores individuales, multiples ligas.
- Persistencia real (SQLite u otra), usuarios y login.
- Partidos importantes con decisiones del jugador durante el partido
  (`MatchImportance.IMPORTANT` / `CRITICAL` estan modelados pero sin
  flujo especial todavia).

---

## Nota sobre verificacion

El codigo fue verificado exhaustivamente compilando y ejecutando toda
la logica de dominio/engine/application/infraestructura de forma
manual (javac + JUnit 5 standalone), incluyendo una prueba de extremo a
extremo (carga de JSON → crear DT → generar ofertas → elegir club →
simular partido → generar evento → resolver decision), todo exitoso.
La unica parte no verificable en el entorno de generacion fue la capa
web (`Main.java` y `WebRoutes.java`), que depende de Spark Java y no
pudo descargarse por restricciones de red del entorno; el codigo sigue
el uso idiomatico estandar de Spark + `spark-template-mustache` y
deberia compilar sin problemas con `mvn clean test` en una maquina con
acceso normal a Maven Central.

# General
## Diagrama de arquitectura
```mermaid
graph TD
    %% Estilos
    classDef core fill:#f96,stroke:#333,stroke-width:2px;
    classDef screen fill:#9cf,stroke:#333,stroke-width:2px;
    classDef logic fill:#cfc,stroke:#333,stroke-width:2px;
    classDef view fill:#fcf,stroke:#333,stroke-width:2px;

    %% Core
    Main[MainGame extends Game]:::core
    Managers[AssetManager
    AudioManger
    ScreenManager
    GameManager
    LoginManager
    ScoreManager]:::core

    Controller -->|Notifica| Managers

    %% Relación Core
    Main -->|Posee| Managers
    Main -->|Cambia| MenuScreen
    Main -->|Cambia| MiniGameXScreen

    %% Pantalla de Juego
    subgraph Mini Game X Implementation
        MiniGameXScreen[MiniGameXScreen 
        implements Screen]:::screen
        Controller[MiniGameXController]:::logic
        Renderer[MiniGameXRenderer]:::view
    end

    %% Relaciones Pantalla
    MiniGameXScreen -->|1. Update| Controller
    MiniGameXScreen -->|2. Render| Renderer
    Renderer -->|Usa| Managers
    Renderer -.->|Lee datos de| Controller

    %% Objetos del mundo
    subgraph Modelo
        Player
        Enemies
        Map
    end

    Controller -->|Controla| Player
    Controller -->|Controla| Enemies
    Controller -.->|"Se extraen Colisiones (Rectangles)"| Map
    Renderer -.->|"Se extraen Tiles (Gráficos)"| Map
```
## Diagrama de arquitectura modular
```mermaid
graph TD
    %% Estilos
    classDef base fill:#eee,stroke:#333,stroke-width:1px,stroke-dasharray: 5 5;
    classDef impl fill:#bbf,stroke:#333,stroke-width:2px;
    classDef core fill:#f96,stroke:#333,stroke-width:2px;
    classDef logic fill:#cfc,stroke:#333,stroke-width:2px;
    classDef view fill:#fcf,stroke:#333,stroke-width:2px;

    %% Core
    Main[MainGame Class]:::core
    
    %% Clases Abstractas (Padres)
    subgraph "Base Framework (Reutilizable)"
        BaseScreen[AbstractGameScreen]:::base
    end

    %% Minijuego 1: Puzzle
    subgraph "Nivel 1: Puzzle"
        S1[PuzzleScreen]:::impl
        C1[PuzzleController]:::logic
        R1[PuzzleRenderer]:::view
    end

    %% Minijuego 2: Plataformas
    subgraph "Nivel 2: Plataformas"
        S2[PlatformScreen]:::impl
        C2[PlatformController]:::logic
        R2[PlatformRenderer]:::view
    end

    %% Relaciones
    Main -->|Cambia a| S1
    Main -->|Cambia a| S2
    
    S1 -- Herencia --> BaseScreen
    S2 -- Herencia --> BaseScreen
    
    S1 --> C1
    S1 --> R1
    
    S2 --> C2
    S2 --> R2
```
## Diagrama de flujo gráfico
```mermaid
graph TD
    User[Jugador Real] -->|Redimensiona Ventana| Screen["MiniGameXScreen.resize()"]
    Screen -->|Avisa| Viewport[MiniGameXRenderer 
    Viewport]
    
    User[Jugador Real] -->|Eventos Teclado/Ratón| Logic[MiniGameXController]
    Logic -->|"Posición Jugador (5,5)" | Renderer[MiniGameXRenderer]    
    Renderer -->|"Mueve Cámara a (5,5)"| Camera[Orthographic Camera]
    Camera -->|Genera Matriz| Batch[SpriteBatch]
    Batch -->|Pinta Sprite en Píxeles| Monitor
```
## Diagrama de flujo tratamiento input de usuario
```mermaid
graph LR
    User((Usuario)) -->|Pulsa Tecla| Hardware
    Hardware -->|Evento keyDown| InputProc[InputProcessor en MiniGameXController]
    
    subgraph MiniGameXController
    InputProc -->|Activa Flag| Flags[moveRight = true]
    Flags -->|Leído por| UpdateLoop[update method]
    UpdateLoop -->|Calcula Física| Position[Nueva Posición]
    end
```
## Secuencia ciclo de vida de un minijuego
```mermaid
sequenceDiagram
    participant G as MainGame
    participant GH as GameHud
    participant GM as GameManager
    participant GS as GameScreen
    participant GC as GameController
    participant GR as GameRenderer
    participant SM as ScoreManager

    GM->>GS: new
    GM->>G: setScreen()
    GS->>GH: new
    GS->>GM: setListener(GameEventListener)
    GS->>GC: new
    GS->>GR: new
    G->>GS: show()
    GS->>GC: startGame()
    note over GC: Bucle de juego
    note over GC: Finaliza nivel
    GC->>GM: levelCompleted()
    GM->>GS: onLevelCompleted()
    GS->>GH: showDialog(onSuccess)
    GH->>GS: onSuccess.run()
    GS->>GC: nextLevel()
    GC->>SM: addPoints(int)
    note over GC: Bucle de juego
    note over GC: Pierde nivel
    GC->>GM: losesLifes()
    GM->>GS: onPlayerDead()
    GS->>GH: showDialog(onSuccess)
    GH->>GS: onSuccess.run()
    GS->>GC: resetLevel()
    note over GC: Bucle de juego
    note over GC: Gana el juego o    
    GC->>SM: addPoints(int)
    GC->>GM: endGame()
    GM->>GS: onEndGame(onSuccess)
    GS->>GH: showDialog(onSuccess)
    GH->>GM: onSuccess.run()
    GM->>GM: nextGame()
    note over GM: Carga nuevo juego
    note over GC: Pierde el juego   
    GC->>GM: losesLifes()
    GM->>GS: onGameOver(onSuccess)
    GS->>GC: gameOver()
    GS->>GH: showDialog(onSuccess)
    GH->>GM: onSuccess.run()
    note over GM: SubmitScore   
    note over GM: Carga MainMenuScreen 
``` 
## Secuencia bucle de juego
```mermaid
sequenceDiagram
    participant G as MainGame
    participant GH as GameHud
    participant GM as GameManager
    participant GS as GameScreen
    participant GC as GameController
    participant GR as GameRenderer
    participant SM as ScoreManager

    G->>GS: render(delta)
    GS->>GC: update(dt)
    GS->>GR: render(dt)
    GS->>GH: render(dt)
    GH->>GM: getLives()
    GH->>SM: getPoints()
```
## Secuencia cambio de nivel
```mermaid
sequenceDiagram
    participant P as Player (Input)
    participant C as PuzleController
    participant GM as GameManager
    participant SM as ScreenManager (View Mgmt)
    participant G as MainGame (LibGDX)

    P->>C: Completa puzles
    C->>GM: completeLevel()
    
    Note over GM: Actualiza Puntos<br/>Guarda Progreso
    
    GM->>SM: setScreen(LEVEL_PLATFORM)
    
    Note over SM: Dispose pantalla vieja<br/>Instancia pantalla nueva
    
    SM->>G: game.setScreen(new PlatformScreen)
``` 
# Puzle
## Diagrama Arquitectura
```mermaid
graph TD
%% Pantalla de Juego
    GameManager:::logic
    IntroScreen:::screen

    subgraph Puzzle Game Implementation
        PuzzleStart:::screen
        PuzzleScreen[PuzzleScreen 
        implements Screen]:::screen
        Controller[PuzzleController]:::logic
        Solver[PuzzleSolver]:::logic
        Renderer[PuzzleRenderer]:::view
    end

    %% Relaciones Pantalla
    GameManager -->|Crea/Cambia| PuzzleStart
    PuzzleStart -->|Crea| PuzzleScreen
    PuzzleStart -->|Crea/Cambia| IntroScreen
    IntroScreen -->|Cambia| PuzzleScreen
    PuzzleScreen -->|1. Start| Controller
    PuzzleScreen -->|2. Bucle Render| Renderer
    Renderer -.->|Lee datos de| Controller
    Renderer -->|Notifica Click| Controller
    Controller -->|Notifica nuevo tablero | Renderer
    Controller -->|Utiliza| Solver
    Controller -->|Notifica| GameManager

    %% Objetos del mundo
    subgraph Modelo
        Piece
        Board
    end

    Controller -->|Mueve| Piece
    Controller -->|Controla| Board
    Board -->|Tiene| Piece
```
## Secuencia inicio de juego
```mermaid
sequenceDiagram
    participant S as PuzzleScreen
    participant C as PuzzleController
    participant B as PuzzleBoard
    participant R as PuzzleRenderer
    participant PS as PuzzleSolver
    participant GM as GameManager

    S->>C: startGame()
    C->>C: newBoard()
    C->>B: new
    C->>R: onNewBoard()
    R->>R: newBoard()
    C->>C: resetLevel()
    C->>B: shufflePieces()
    C->>PS: solver(board)
    C->>C: setCurrentMoves(0)
    C->>GM: setHudMessage("Mov: 0/X")
    
    Note over C: =>Secuencia de juego
```
## Secuencia de juego
```mermaid
sequenceDiagram
    participant R as PuzzleRenderer
    participant C as PuzzleController
    participant B as PuzzleBoard
    participant GM as GameManager
    
    Note over R: Usuario toca pieza -> touchDown(X,Y,p,b)
    R->>C: pieceClick(row,col)
    C->>B: getPieceAt(row,col)
    Note over C: Primera pieza
    Note over R: Usuario toca pieza -> touchDown(X,Y,p,b)
    R->>C: pieceClick(row,col)
    C->>B: getPieceAt(row,col)
    Note over C: Segunda pieza
    C->>B: swapPieces(p1,p2)
    C->>C: incrementCurrentMoves()
    C->>GM: setHudMessage()
    C->>B: checkWinCondition()
    
    Note over C: =>Secuencia fin juego/nivel
```
## Secuencia fin juego/nivel
```mermaid
sequenceDiagram
    participant C as PuzzleController
    participant B as PuzzleBoard
    participant GM as GameManager
        
    rect rgb(200, 220, 240)
    Note over C: Sí gana
    C->>C: onLevelComplete()
    C->>C: updateScore()
    C->>GM: updateScore(points)
    C->>C: nextLevel()
    rect rgb(100, 220, 240)
    Note over C: hay mas niveles
    C->>GM: levelCompleted()
    C->>C: setDifficulty(+1)
    Note over C: Secuencia inicio de juego
    end
    rect rgb(100, 220, 240)
    Note over C: no mas niveles
    C->>C: endGame()
    C->>GM: endGame()
    end
    end

    rect rgb(240, 220, 200)
    Note over C: Sí pierde
    C->>GM: losesLifes()
    C->>C: resetLevel()
    C->>GM: isGameOver()
    C->>B: shufflePieces()
    C->>C: solver().size
    C->>C: setCurrentMoves(0)
    C->>GM: setHudMessage()

    Note over C: =>Secuencia de juego
    end
```

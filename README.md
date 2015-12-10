# Proyecto Gato Multiplayer Para Android

Este juego se desarolló en el lenguaje JAVA para la plataforma ANDROID utilizando sólo librerias estandar. La mecánica del juego está implementada en la clase Gato, encargada de llevar el tablero, estado del juego y la lógica (turnos, movimientos válidos, victoria, derrota, etc.) la clase principal está encargada de la conectividad y el manejo entre dos modos de juego: local(sólo un dispositivo) y LAN(sobre WIFI).

 La conectividad del proyecto se desarrolló a través del paradigma cliente-servidor, debido a limitaciones de la red móvil sólo se puede jugar online a través de WIFI.

 La interfaz utiliza botones básicos para jugar el gato y el menú de opciones estándar de Android para seleccionar entre conectar con otro jugador y pedir revancha (local o en WIFI). La codificación del tablero del juego se hace mediante enteros: 0, casilla disponible; 1, casilla ocupada por el primer jugador y 2, para el segundo jugador. De acuerdo a las características del lenguaje Java, se considera que el manejo de enteros es más robusto que el manejo de chars o strings.

 Si tienes alguna duda o comentario escribe un correo a ingjrmr@gmail.com
 
# Multiplayer Tic-Tac-Toe Proyect for Android

This project was developed on JAVA for the ANDROID platform using only standard libraries. The gameplay is implemented via the Tic-Tac-Toe class, which is in charge of the board, game status and the logic (Turns, valid movements, victory, defeat, etc.) the Main class is in charge of the networking and the handling between two game modes: Local (One device only) and LAN (Over WIFI).

The project's networking was developed via client-server paradigm, due to mobile networking limitations the game can only be played via WIFI.

The Interface uses basic buttons to play Tic-Tac-Toe and Android's default option menu to select between finding another player or asking for a rematch. The coding for the game's board is made between integers: 0, Avaliable space; 1, Space already occupied by the first player and 2, for the second player. Given JAVA's features, it is considered that the usage of integers is more robust than the usage of chars or strings.

If you have any comments or questions write a mail to ingjrmr@gmail.com

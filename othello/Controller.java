/**
 * Třída pro provádění hlavních operací hry
 * Funkce:  1) Řízení hlavních operací hry
 *          2) Kontrola nad hrou a S/L manažerem
 *          3) Vytváření, načítání a ukládání her
 *          4) Provádění tahů
 *          5) Vracení tahů
 *          6) Zmrazování kamenů
 *          7) Analýza konce hry
 *          8) Analýza tahu počítače
 * @author Lukáš Dibďák
 */

package othello;

import othello.Utility.*;

import java.util.*;

public class Controller {
    private Game game;
    private SaveLoadManager saveLoadManager;
    private TypeOfGame typeOfGame;
    private boolean gameStarted;
    ArrayList<TreeMap<Coords, ArrayList<Coords>>> allAvailableMoves;
    ArrayList<Field> frozenStones = new ArrayList<>();
    ArrayList<Field> toFreezeStones = new ArrayList<>();

    /**
     * Konstruktor objektu
     * Třída {@code Game} je inicializovaná až při vytvoření nebo načtení hry
     * Třída {@code SaveLoadManager} je incicializována ihned, protože nepřebírá žádné proměnné argumenty
     */
    Controller() {
        game = null;
        saveLoadManager = new SaveLoadManager();
        typeOfGame = null;
        gameStarted = false;
        allAvailableMoves = null;
    }

    /**
     * Přetížená metoda pro vytvoření nové hry pro dva lidské hráče
     * Po provedení metody se vytváří checkpoint pro operaci {@code undoMove}
     * Po provedení metody se provádí přepočítání skóre
     * @param boardSize Velikost hrací desky
     * @return Pole typu {@code String} které vrací
     * [0,1] aktuální skóre hráčů pro výpis
     * [2] řetězec s informací kdo je na řadě
     * [3] vizualizovaná hrací deska
     */
    String[] createNewGame(int boardSize) {
        Player players[] = Player.getPlayersForConstructor(PlayerType.HUMAN);

        game = new Game(boardSize, players);
        this.gameStarted = true;

        return new String[] {
                Integer.toString(game.getScore()[Utility.PLAYERONE]),
                Integer.toString(game.getScore()[Utility.PLAYERTWO]),
                Utility.getPlayerTurnString(game.getActivePlayerTurn()),
                Utility.visualizeBoard(game.getBoard())
        };
    }

    /**
     * Přetížená metoda pro vytvoření nové hry pro lidského a počítačového hráče
     * Po provedení metody se vytváří checkpoint pro operaci {@code undoMove}
     * Po provedení metody se provádí přepočítání skóre
     * @param boardSize Velikost hrací desky
     * @param typeOfGame Obtížnosti počítačového hráče typu {@code TypeOfGame}
     * @return Pole typu {@code String} které vrací
     * [0,1] aktuální skóre hráčů pro výpis
     * [2] řetězec s informací kdo je na řadě
     * [3] vizualizovaná hrací deska
     */
    String[] createNewGame(int boardSize, TypeOfGame typeOfGame) {
        Player players[] = Player.getPlayersForConstructor(PlayerType.COMPUTER);

        game = new Game(boardSize, players);
        this.gameStarted = true;
        this.typeOfGame = typeOfGame;

        return new String[] {
                Integer.toString(game.getScore()[Utility.PLAYERONE]),
                Integer.toString(game.getScore()[Utility.PLAYERTWO]),
                Utility.getPlayerTurnString(game.getActivePlayerTurn()),
                Utility.visualizeBoard(game.getBoard())
        };
    }

    /**
     * Načtení hry ze souboru
     * Po provedení metody se provádí přepočítání skóre
     * @param nameOfGame Jméno hry, která se má načíst
     * @return Pole typu {@code String} které vrací
     * [0,1] aktuální skóre hráčů pro výpis
     * [2] řetězec s informací kdo je na řadě
     * [3] vizualizovaná hrací deska
     * [4] hlášku o úspěšném načtení hry
     * @throws GameLoadingNameNotFoundException Pokud {@code nameOfGame} odkazuje na neexistující hru
     * @throws GameLoadingFailureException Při všech ostatních chybách, například chyba čtení souboru
     */
    String[] loadGame(String nameOfGame) throws GameLoadingNameNotFoundException, GameLoadingFailureException {
        ArrayList<String> gameInfo;
        ArrayDeque<Board> gameBoards;

        try {
            gameInfo = saveLoadManager.load(nameOfGame);
            PlayerType playerType = Utility.loadParsePlayerType(gameInfo.remove(0).charAt(0));
            int boardSize = Utility.loadParseBoardSize(gameInfo.remove(0).trim());

            this.typeOfGame = Utility.loadParseTypeOfGame(gameInfo.remove(0).trim());

            int activePlayer = Utility.loadParseActivePlayer(gameInfo.remove(0).trim());
            gameBoards = Utility.loadParseBoards(gameInfo, boardSize);

            Player[] players = Player.getPlayersForConstructor(playerType);
            game = new Game(boardSize, players, gameBoards, activePlayer);

            this.gameStarted = true;

            Utility.setPlayerString(playerType == PlayerType.COMPUTER);

            return new String[] {
                    Integer.toString(game.getScore()[Utility.PLAYERONE]),
                    Integer.toString(game.getScore()[Utility.PLAYERTWO]),
                    Utility.getPlayerTurnString(game.getActivePlayerTurn()),
                    Utility.visualizeBoard(game.getBoard()),
                    Utility.getSuccessfulLoadGameString()
            };
        }
        catch (GameLoadingNameNotFoundException e) {
            throw e;
        }
        catch (Exception e) {
            throw new GameLoadingFailureException();
        }
    }

    /**
     * Metoda pro zadání tahu lidského hráče
     * Po provedení této metody se přesouvá tah na dalšího hráče
     * Po provedení metody se vytváří checkpoint pro operaci {@code undoMove}
     * Po provedení metody se provádí přepočítání skóre
     * @param coords Souřadnice bodu typu {@code Coords}, kam chce hráč vložit kámen
     * @return Pole typu {@code String} které vrací
     * [0,1] aktuální skóre hráčů pro výpis
     * [2] řetězec s informací kdo je na řadě
     * [3] vizualizovaná hrací deska
     * @throws GameIsNotStartedException Není aktivní žádná hra na které by mohla být provedena operace
     * @throws MoveNotAvailableException Tah na daných souřadnicích není k dispozici protože:
     * - políčko není prázdné
     * - hráč by neotočil žádný soupeřův kámen
     */
    String[] makeMove(Coords coords) throws GameIsNotStartedException, MoveNotAvailableException {
        if (!gameStarted) {
            throw new GameIsNotStartedException();
        }

        try {
            game.controlMoveIfValid(coords, allAvailableMoves);
            game.countStones();
            game.makeCheckpoint();
            game.turnHasBeenMade();

            return new String[] {
                    Integer.toString(game.getScore()[Utility.PLAYERONE]),
                    Integer.toString(game.getScore()[Utility.PLAYERTWO]),
                    Utility.getPlayerTurnString(game.getActivePlayerTurn()),
                    Utility.visualizeBoard(game.getBoard())
            };
        }
        catch (MoveNotAvailableException e) {
            throw e;
        }
    }

    /**
     * Metoda zajištující zamrznutí kamenů
     * Po provedení této metody se přesouvá tah na dalšího hráče
     * Po provedení metody se vytváří checkpoint pro operaci {@code undoMove}
     * @return Pole typu {@code String} které vrací
     * [0,1] aktuální skóre hráčů pro výpis
     * [2] řetězec s informací kdo je na řadě
     * [3] vizualizovaná hrací deska
     * [4] hlášku o úspěšném provedení operace
     * @throws GameIsNotStartedException Není aktivní žádná hra na které by mohla být provedena operace
     */
    String[] freezeStones(ArrayList<Coords> coordsOfFrozenStones) throws GameIsNotStartedException {
        if (!gameStarted) {
            throw new GameIsNotStartedException();
        }

        ArrayList<Coords>[] notFrozen = game.getAvailable();
        ArrayList<Integer> numberOfFrozenStones = new ArrayList<>();

        int[] randomNumbers = new int [2];

        Utility.generateRandomNumbers(randomNumbers, notFrozen[game.getActivePlayerTurn()].size(), numberOfFrozenStones);

        for (int i: numberOfFrozenStones) {
            Coords tmpCoords = notFrozen[game.getActivePlayerTurn()].get(i);
            game.getBoard().getField(tmpCoords.getX(), tmpCoords.getY()).freeze(randomNumbers[0], randomNumbers[1]);
            game.getBoard().getField(tmpCoords.getX(), tmpCoords.getY()).setAvailable(false);
            frozenStones.add(game.getBoard().getField(tmpCoords.getX(), tmpCoords.getY()));
            coordsOfFrozenStones.add(tmpCoords);
        }

        game.turnHasBeenMade();
        game.makeCheckpoint();

        return new String[] {
                Integer.toString(game.getScore()[Utility.PLAYERONE]),
                Integer.toString(game.getScore()[Utility.PLAYERTWO]),
                Utility.getPlayerTurnString(game.getActivePlayerTurn()),
                Utility.visualizeBoard(game.getBoard()),
                Utility.getSuccessfulFreezeStoneString(randomNumbers, numberOfFrozenStones.size())
        };
    }

    /**
     * Metoda zajišťující uložení hry
     * @param nameOfGame Jméno hry pod kterým bude uloženo
     * @return Pole typu {@code String} které vrací
     * [0] hláška o úspěšném uložení hry
     * @throws GameIsNotStartedException Není aktivní žádná hra na které by mohla být provedena operace
     * @throws GameSavingFailureException Při všech ostatních chybách, například chyba zápisu
     */
    String saveGame(String nameOfGame) throws GameIsNotStartedException, GameSavingFailureException {
        if (!gameStarted) {
            throw new GameIsNotStartedException();
        }

        try {
            saveLoadManager.save(nameOfGame, game.getPlayers(), game.getLogger(), game.getActivePlayerTurn(), typeOfGame);
            return Utility.getSuccessfulSaveGameString();
        }
        catch (Exception e) {
            throw new GameSavingFailureException();
        }
    }

    /**
     * Metoda zajišťující operaci undo, vrácení tahu
     * Po provedení této metody se přesouvá tah na dalšího hráče
     * Po provedení metody se provádí přepočítání skóre
     * @return Pole typu {@code String} které vrací
     * [0,1] aktuální skóre hráčů pro výpis
     * [2] řetězec s informací kdo je na řadě
     * [3] vizualizovaná hrací deska
     * @throws GameIsNotStartedException Není aktivní žádná hra na které by mohla být provedena operace
     * @throws NoMoreMovesToUndoException Již nezbývá žádný tah, který by bylo možno vrátit, hra je na začátku
     */
    String[] undoMove() throws GameIsNotStartedException, NoMoreMovesToUndoException {
        if (!gameStarted) {
            throw new GameIsNotStartedException();
        }

        try {
            Board temp = game.makeUndo();
            game.setBoard(temp);

            game.makeCheckpoint();
            game.countStones();

            return new String[] {
                    Integer.toString(game.getScore()[Utility.PLAYERONE]),
                    Integer.toString(game.getScore()[Utility.PLAYERTWO]),
                    Utility.getPlayerTurnString(game.getActivePlayerTurn()),
                    Utility.visualizeBoard(game.getBoard())
            };
        }
        catch (EmptyStackException | NoMoreMovesToUndoException e) {
            throw new NoMoreMovesToUndoException();
        }
    }

    /**
     * Metoda analyzuje následující možnosti hry, zejména:
     * - hra skončila, protože žádný z hráčů nemůže táhnout nebo na hrací desce není žádné prázdné místo
     * - na tahu je počítač, vykoná tedy podle algoritmu tah
     * - ukládá všechny dostupné tahy, ve kterých se následně vyhledává tah hráčů/počítače
     * @throws GameEndedException Hra byla ukončena, v rámci výjimky vrací pole typu {@code String}:
     * [0,1] aktuální skóre hráčů pro výpis
     * [2] řetězec s informací o vítězi a konci hry
     * [3] vizualizovaná hrací deska
     * @throws ComputerHasPlayed Na tahu byl počítač, který vykonal tah
     * [0,1] aktuální skóre hráčů pro výpis
     * [2] řetězec s informací kdo je na řadě
     * [3] vizualizovaná hrací deska
     * [4,5] souřadnice tahu počítače
     */
    String[] analyzeNextTurn(ArrayList<Field> retFields) throws GameEndedException, ComputerHasPlayed, GameIsNotStartedException {
        if (!gameStarted) {
            throw new GameIsNotStartedException();
        }

        allAvailableMoves = game.getAvailableMoves();

        game.checkIfFrozen(frozenStones, toFreezeStones);

        for (Field field: toFreezeStones) {
            retFields.add(field);
        }
        for (Field field: frozenStones) {
            retFields.add(field);
        }

        if (allAvailableMoves.isEmpty()) {
            game.setFinalScore();
            throw new GameEndedException (
                    new String[] {
                            Integer.toString(game.getScore()[Utility.PLAYERONE]),
                            Integer.toString(game.getScore()[Utility.PLAYERTWO]),
                            Utility.getGameEndedString(game.getPlayers()),
                            Utility.visualizeBoard(game.getBoard())
                    }
            );
        }

        try {
            game.controlIfComputerTurn(typeOfGame, allAvailableMoves);
        }
        catch (ComputerHasPlayed coords) {
            throw new ComputerHasPlayed(
                    new String[] {
                            Integer.toString(game.getScore()[Utility.PLAYERONE]),
                            Integer.toString(game.getScore()[Utility.PLAYERTWO]),
                            Utility.getPlayerTurnString(game.getActivePlayerTurn()),
                            Utility.visualizeBoard(game.getBoard()),
                            coords.getX(),
                            coords.getY()
                    }
            );
        }
        return new String[] {
                Integer.toString(game.getScore()[Utility.PLAYERONE]),
                Integer.toString(game.getScore()[Utility.PLAYERTWO]),
                Utility.getPlayerTurnString(game.getActivePlayerTurn()),
                Utility.visualizeBoard(game.getBoard())
        };
    }
    
    public Board getBoard()
    {
        return this.game.getBoard();
    }
}

/**
 * Třída pro základní podporu logiky pro třídu Controller
 * Funkce:  1) Vytváření objektu hry při vytváření nové hry a uchovávání informací o ní
 *          2) Vytváření objektu hry při načítání již existující hry a uchovávání informací o ní
 *          3) Poskytování informace o hrací desce a provádění operací nad ní
 *          4) Poskytování informací o hráčích a provádění operací nad nimi
 *          5) Přepínání tahů mezi hráči
 *          6) Vytváření checkpointů hracích desek pro operaci undo a přidávání je do zásobníku
 *          7) Podpora pro operaci undo
 *          8) Podpora pro práci se skóre hráčů
 *          9) Podpora pro logiku počítače
 *         10) Podpora pro provedení tahu
 * @author Lukáš Dibďák
 * @see othello.Controller
 */

package othello;

import java.util.*;

import javafx.scene.web.WebHistory;
import othello.Utility.*;

public class Game {
    private int activePlayerTurn = 0;
    private Board board = null;
    private Player players[] = new Player[2];
    private ArrayDeque<Board> logger = null;

    /**
     * Přetížený konstuktor pro vytváření nové hry, vytváří se:
     * - hrací deska
     * - hráči
     * - prázdný zásobník tahů
     * - první tah pro prvního hráče
     * @param boardSize Velikost hrací desky
     * @param players Pole hráčů
     */
    Game(int boardSize, Player players[]) {
        this.board = new Board(boardSize);

        for (int i = 0; i < 2; i++) {
            this.players[i] = players[i];
        }

        logger = new ArrayDeque<>();
        logger.push(board.copy());

        countStones();
        Utility.setPlayerString(players[Utility.PLAYERTWO].getPlayerType() == PlayerType.COMPUTER);
    }

    /**
     * Přetížený konstuktor pro nahrávání již rozehrané hry, vytváří se:
     * - hrací deska
     * - hráči
     * - zásobník tahů, když byla hra uložena
     * - tah pro hráče, když byla hra uložena
     * @param boardSize Velikost hrací desky
     * @param players Pole hráčů
     * @param logger Zásobník tahů
     * @param activePlayerTurn Tah hráče
     */
    Game(int boardSize, Player players[], ArrayDeque<Board> logger, int activePlayerTurn) {
        board = new Board(boardSize);

        for (int i = 0; i < 2; i++) {
            this.players[i] = players[i];
        }

        this.logger = new ArrayDeque<>();
        while (!logger.isEmpty()) {
            this.logger.push(logger.removeFirst());
        }
        setBoard(this.logger.peek());

        this.activePlayerTurn = activePlayerTurn;

        countStones();
        Utility.setPlayerString(players[Utility.PLAYERTWO].getPlayerType() == PlayerType.COMPUTER);
    }

    /**
     * Metoda pro uložení aktuální hrací desky na zásobník pro případ použití operace {@code undoMove}
     */
    void makeCheckpoint() {
        Board temp = board.copy();
        logger.push(temp);
    }

    /**
     * Metoda pro přepnutí tahu na dalšího hráče
     */
    void turnHasBeenMade() {
        if (activePlayerTurn != Utility.PLAYERONE)
            activePlayerTurn = Utility.PLAYERONE;
        else
            activePlayerTurn = Utility.PLAYERTWO;
    }

    /**
     * Metoda, která vrací ze zásobníku předchozí hrací desku, která byla přidána jako checkpoint
     * Jedná se o podpůrnou třídu pro metodu {@code undoMove} ve tříde {@code Controller}
     * @return Předchozí hrací deska uložená na zásobníku
     * @throws NoMoreMovesToUndoException Na zásobníku již nejsou uloženy žádné hrací desky, jedná se o začátek hry
     */
    Board makeUndo() throws NoMoreMovesToUndoException {
        if (logger.size() < 3) {
            throw new NoMoreMovesToUndoException();
        }
        Board temp = null;
        for (int i = 0; i < 3; i++) {
            temp = logger.pop();
        }
        return temp;
    }

    /**
     * Metoda pro zjištění identifikátoru hráče na tahu
     * @return Identifikace hráče, který je na tahu
     */
    int getActivePlayerTurn() {
        return activePlayerTurn;
    }

    /**
     * Metoda pro zjištění hráče na tahu
     * @return Hráč na tahu typu {@code Player}
     */
    Player getActivePlayer() {
        return players[activePlayerTurn];
    }

    /**
     * Metoda pro zjištění aktuálního skóre
     * @return Pole obsahující skóre obou hrajících hráčů
     */
    int[] getScore() {
        return new int[] {
                getPlayers()[Utility.PLAYERONE].getScore(),
                getPlayers()[Utility.PLAYERTWO].getScore()
        };
    }

    /**
     * Metoda pro nastavení nové hrací desky
     * Využívá se nastavení nové desky při operaci {@code undoMove}
     * @param board Hrací deska, která se má nastavit jako aktuální
     * @see Controller
     */
    void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Získání aktuální hrací desky
     * @return Aktuální hrací deska
     */
    Board getBoard() {
        return board;
    }

    /**
     * Získání zásobníku tahů
     * @return Zásobník tahů typu {@code Board}
     */
    ArrayDeque<Board> getLogger() {
        return logger;
    }

    /**
     * Získání hráčů
     * @return Pole typu {@code Player} obsahující oba hráče
     */
    Player[] getPlayers() {
        return players;
    }

    /**
     * Metoda pro otočení určitých kamenů na hrací desce
     * @param listOfCoords Zásobník zásobníků souřadnic kamenů, které je třeba otočit
     */
    void changeFields(ArrayList<ArrayList<Coords>> listOfCoords) {
        for (ArrayList<Coords> tempCoords: listOfCoords) {
            for (Coords temp: tempCoords) {
                board.changeField(temp);
            }
        }
    }

    /**
     * Metoda pro kontrolu zamrznutí kamenů po tahu a změna jejich barvy
     * @param frozenStones Pole zamrznutých kamenenů
     */
    void checkIfFrozen(ArrayList<Field> frozenStones, ArrayList<Field> notFrozen) {
        if (frozenStones.size() > 0) {
            ArrayList<Field> tmp = new ArrayList<>();
            for (Field fieldConstructor: frozenStones) {
                tmp.add(new Field(fieldConstructor));
            }
            for (Field field : frozenStones) {
                Color color = null;

                try {
                    color = field.getColor();
                } catch (FieldIsEmptyException e) {
                }

                if (field.isFrozen() && color != Color.FBLACK && color != Color.FWHITE) {
                    field.changeColorFreeze();
                    notFrozen.remove(field);
                }
                else if (!field.isFrozen() && color != Color.BLACK && color != Color.WHITE) {
                    field.changeColorFreeze();
                    tmp.remove(field);
                }
            }
            frozenStones = tmp;
        }
    }

    /**
     * Metoda pro získání polí, které nejsou zmraženy a tudíž mohou být zmraženy
     * @return Pole kamenů černého i bílého hráče
     * @see Controller
     */
    ArrayList<Coords>[] getAvailable() {
        ArrayList<Coords> blackStones = new ArrayList<>();
        ArrayList<Coords> whiteStones = new ArrayList<>();

        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                try {
                    if (getBoard().getField(i, j).isAvailable()) {
                        switch (getBoard().getField(i, j).getColor()) {
                            case BLACK:
                            case FBLACK:
                                blackStones.add(new Coords(i, j));
                                break;
                            case WHITE:
                            case FWHITE:
                                whiteStones.add(new Coords(i, j));
                        }
                    }
                } catch (FieldIsEmptyException e) {}
            }
        }
        return new ArrayList[] {blackStones, whiteStones};
    }

    /**
     * Metoda pro získání aktuálních kamenů obou hráčů pro {@code freezeStones}
     * Pomocí počtu těchto kamenů lze také získat skóre obou hráčů
     * @return Pole kamenů černého i bílého hráče
     * @see Controller
     */
    ArrayList<Field>[] countStones() {
        ArrayList<Field> blackStones = new ArrayList<>();
        ArrayList<Field> whiteStones = new ArrayList<>();

        for (Field field: getBoard().getField()) {
            try {
                if (field.getColor() == Color.BLACK || field.getColor() == Color.FBLACK)
                    blackStones.add(field);
                else
                    whiteStones.add(field);
            } catch (FieldIsEmptyException e) {}
        }

        getPlayers()[Utility.PLAYERONE].setScore(blackStones.size());
        getPlayers()[Utility.PLAYERTWO].setScore(whiteStones.size());

        return new ArrayList[] {blackStones, whiteStones};
    }

    /**
     * Metoda pro nastavení konečného skóre při konci hry
     * Je požadováno, aby se při konci hry skóre, které není přiřazeno žádnému hráči
     * přiřadilo vítězi (pokud zápas neskončil remízou), tak aby se součet skóre
     * obou hráčů rovnal počtu polí na hrací desce (za jedno políčko je 1 bod)
     * Využíváno ve funkci {@code analyzeNextTurn}
     * @see Controller
     */
    void setFinalScore() {
        countStones();

        int playerOne = getPlayers()[Utility.PLAYERONE].getScore();
        int playerTwo = getPlayers()[Utility.PLAYERTWO].getScore();

        if ((playerOne + playerTwo) != (Board.SIZE * Board.SIZE)) {
            if (playerOne > playerTwo) {
                getPlayers()[Utility.PLAYERONE].setScore(Board.SIZE * Board.SIZE - playerTwo);
            }
            else if (playerOne < playerTwo) {
                getPlayers()[Utility.PLAYERTWO].setScore(Board.SIZE * Board.SIZE - playerOne);
            }
            else {
                getPlayers()[Utility.PLAYERONE].setScore((Board.SIZE * Board.SIZE) / 2);
                getPlayers()[Utility.PLAYERTWO].setScore((Board.SIZE * Board.SIZE) / 2);
            }
        }
    }

    /**
     * Metoda kontrolující zda hráč na tahu je počítač, v tom případě provede podle obtížnosti hry tah
     * @param typeOfGame Obtížnost hry zadaná při vytváření nové hry
     * @param allAvailableMoves Zásobník všech dostupných tahů
     * @throws ComputerHasPlayed Počítač provedl tah o souřadnicích coords typu {@code Coords}
     */
    void controlIfComputerTurn(TypeOfGame typeOfGame, ArrayList<TreeMap<Coords, ArrayList<Coords>>> allAvailableMoves) throws ComputerHasPlayed {
        if (getActivePlayer().getPlayerType() == PlayerType.COMPUTER) {
            TreeMap<Coords, ArrayList<Coords>> toChangeTemp = new TreeMap<>();

            switch (typeOfGame) {
                case EASY:
                    toChangeTemp = Algorithm.getEasyAlgorithm(allAvailableMoves);
                    break;
                case HARD:
                    toChangeTemp = Algorithm.getHardAlgorithm(allAvailableMoves);
                    break;
            }

            try {
                getBoard().setField(toChangeTemp.firstKey(), getActivePlayer().getColor());
            } catch (FieldIsNotEmptyException e) {}

            for (Map.Entry<Coords, ArrayList<Coords>> temp: toChangeTemp.entrySet()) {
                for (Coords change: temp.getValue())
                    board.changeField(change);
            }

            countStones();
            makeCheckpoint();
            turnHasBeenMade();

            throw new ComputerHasPlayed(toChangeTemp.firstKey().getX(), toChangeTemp.firstKey().getY());
        }
    }

    /**
     * Metoda pro zjištění potencionálních možností tahu na daných souřadnicích
     * Algoritmus zkoumá okolní pozice daného pole na hrací desce, pokud je v okolních pozicích
     * barva druhého hráče, pak potencionálně může být dané políčko validní k tahu hráče
     * @param coords Souřadnice pole na hrací desce
     * @return Potencionální okolí políčka dané souřadnicemi
     * @throws NoMovesAvailableException Pole není validní k tahu
     */
    ArrayList<Coords> checkPositionForMoves(Coords coords) throws NoMovesAvailableException {
        ArrayList<Coords> availableMoves = new ArrayList<>();

        for (int i = coords.getX() - 1; i <= coords.getX() + 1; i++) {
            for (int j = coords.getY() - 1; j <= coords.getY() + 1; j++) {
                if ((coords.getX() == i && coords.getY() == j) || (!board.getField(coords.getX(), coords.getY()).isEmpty())) {
                    continue;
                }

                Coords tempCoords = new Coords(i, j);
                if (Utility.isInBoard(tempCoords)) {
                    try {
                        Color color = board.getField(tempCoords.getX(), tempCoords.getY()).getColor();
                        if (color != getActivePlayer().getColor() && color != getActivePlayer().getFrozenColor()) {
                            availableMoves.add(tempCoords);
                        }
                    } catch (FieldIsEmptyException e) {}
                }
            }
        }

        if (availableMoves.isEmpty()) {
            throw new NoMovesAvailableException();
        }

        return availableMoves;
    }

    /**
     * Pro každou souřadnici na mapě dopočítává validní tahy na základě metody {@code checkPositionForMoves}, která
     * vrací potencionálně validní tahy. Metoda prohledává všechny potencionální diagonály a pokud narazí na hráčův
     * kámen, pak je tah validní a pro danou souřadnici pro každý tah se ukládá množina potencionálně validních a
     * změnitelných tahů, vytáří tedy namapovanou dvojici souřadnice : okolí změnitelných kamenů
     * @return Pro každý tah vrací namapovanou množinu změnitelných kamenů
     */
    ArrayList<TreeMap<Coords, ArrayList<Coords>>> getAvailableMoves() {
        ArrayList<TreeMap<Coords, ArrayList<Coords>>> tempMoves = new ArrayList<>();

        for (int x = 0; x < Board.SIZE; x++) {
            for (int y = 0; y < Board.SIZE; y++) {
                try {
                    ArrayList<Coords> temp = checkPositionForMoves(new Coords(x, y));

                    NextTry:
                    for (Coords direction : temp) {
                        int i, j; ArrayList<Coords> tempCoords = new ArrayList<>();
                        for (i = direction.getX(), j = direction.getY(); Utility.isInBoard(new Coords(i, j)); i += (direction.getX() - x), j += (direction.getY() - y)) {
                            try {
                                Color boardColor = getBoard().getField(i, j).getColor();
                                if (boardColor != getActivePlayer().getColor() && boardColor != getActivePlayer().getFrozenColor()) {
                                    tempCoords.add(new Coords(i, j));
                                } else {
                                    TreeMap<Coords, ArrayList<Coords>> map = new TreeMap<>();
                                    map.put(new Coords(x, y), tempCoords);
                                    tempMoves.add(map);
                                    continue NextTry;
                                }
                            } catch (FieldIsEmptyException fieldException) {
                                continue NextTry;
                            }
                        }
                    }
                } catch (NoMovesAvailableException movesException) {}
            }
        }
        return tempMoves;
    }

    /**
     * Metoda v namapovaných dvojících vyhledává zadanou souřadnici (pole dané souřadnicemi)
     * Pokud není nalezena, pak pro ní neexistuje žádná potencionální změna a tah není validní
     * Změny mohou být ve více směrech okolí pole hrací desky
     * @param coords Souřadnice pole hrací desky, na které chce hrát táhnout
     * @param allAvailableMoves Všechny validní tahy na celé desce a změny při těchto tazích
     * @throws MoveNotAvailableException Pro toto pole o těchto souřadnicích nebyl nalezen
     * namapovaný žádný tah a proto tento tah není validní, nebo políčko není prázdné
     */
    void controlMoveIfValid(Coords coords, ArrayList<TreeMap<Coords, ArrayList<Coords>>> allAvailableMoves) throws MoveNotAvailableException {
        boolean moveFound = false;
        ArrayList<ArrayList<Coords>> tempArrayOfCoords = new ArrayList<>();

        for (TreeMap<Coords, ArrayList<Coords>> map: allAvailableMoves) {
            for (Map.Entry<Coords, ArrayList<Coords>> mapSet: map.entrySet()) {
                if (coords.equals(mapSet.getKey())) {
                    moveFound = true;
                    tempArrayOfCoords.add(mapSet.getValue());
                }
            }
        }

        if (!moveFound) {
            throw new MoveNotAvailableException();
        }

        try {
            getBoard().setField(coords, getActivePlayer().getColor());
        } catch (FieldIsNotEmptyException e) {}

        changeFields(tempArrayOfCoords);
    }
}
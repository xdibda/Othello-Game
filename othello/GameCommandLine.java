/**
 * Třída pro znakové zobrazení informací o hře v příkazové řádce.
 * Funkce:  1) Grafické zobrazení hrací desky
 *          2) Zobrazení informací o skóre hráčů
 *          3) Zobrazení informací o tahu počítače a hráčů
 *          4) Zobrazení informací o aktuálním tahu
 * @author Lukáš Dibďák
 * @see othello.Controller
 */

package othello;

import othello.Utility.*;
import java.util.*;

public class GameCommandLine {
    static ArrayList<Character> characters = new ArrayList<>();
    static ArrayList<String> numbers = new ArrayList<>();

    static {
        for (int i = Utility.transformCharToInt('a'); i <= Utility.transformCharToInt('l'); i++)
            characters.add(Utility.transformIntToChar(i));

        for (int i = 1; i <= 12; i++) {
            if (i < 10) numbers.add(Integer.toString(i) + " ");
            else numbers.add(Integer.toString(i));
        }
    }

    /**
     * Znakové vykreslení hrací desky a kamenů na ní ležících
     * @param temp Pole znaků reprezentující jednotlivé kameny na desce
     */
    static void showBoard(char[] temp) {
        for (int i = 0; i < Board.SIZE * 2 + 5; i++) {
            System.out.print("-");
        }
        System.out.print(System.lineSeparator());
        System.out.print("    ");

        for (int i = 0; i < Board.SIZE; i++) {
            System.out.print(characters.get(i));
            System.out.print(" ");
        }
        System.out.print(System.lineSeparator());
        System.out.print("   ");

        for (int i = 0; i < Board.SIZE * 2 + 1; i++) {
            System.out.print("-");
        }
        System.out.print('\n');

        for (int i = 0; i < Board.SIZE; i++) {
            System.out.print(numbers.get(i));
            System.out.print(" |");
            for (int j = 0; j < Board.SIZE; j++) {
                System.out.print(temp[i * Board.SIZE + j]);
                if (j < Board.SIZE - 1)
                    System.out.print(" ");
            }
            System.out.print("|");
            System.out.print('\n');
        }
        System.out.print("   ");

        for (int i = 0; i < Board.SIZE * 2 + 1; i++) {
            System.out.print("-");
        }
        System.out.print('\n');
    }

    /**
     * Přetížená metoda vypsání informací o hře na obrazovku pro tah počítače
     * @param x Vodorovná souřadnice tahu počítače
     * @param y Svislá souřadnice tahu počítače
     * @param temp Jednotlivé informace o hře, zejména:
     *             - skóre jednotlivých hráčů
     *             - grafické znázornění hrací desky
     *             - který z hráčů je na tahu
     */
    static void showMoveInfo(String x, String y, String[] temp) {
        showBoard(temp[3].toCharArray());
        System.out.println("[pocitac] táhl na pole: " + x + " " + y);
        System.out.println("Skore je: " + Utility.PLAYERS[Utility.PLAYERONE] + ": " + temp[0] + ", " + Utility.PLAYERS[Utility.PLAYERTWO] + ": " + temp[1]);
        System.out.println(temp[2]);
    }

    /**
     * Přetížená metoda vypsání informací o hře na obrazovku pro ostatní operace
     * @param temp Jednotlivé informace o hře, zejména:
     *             - skóre jednotlivých hráčů
     *             - grafické znázornění hrací desky
     *             - který z hráčů je na tahu
     */
    static void showMoveInfo(String[] temp) {
        showBoard(temp[3].toCharArray());
        System.out.println("Skore je: " + Utility.PLAYERS[Utility.PLAYERONE] + ": " + temp[0] + ", " + Utility.PLAYERS[Utility.PLAYERTWO] + ": " + temp[1]);
        System.out.println(temp[2]);
    }

    /**
     * Metoda pro výpis dalších informací o hře
     * @param temp Informace k vypsání na obrazovku
     */
    static void showAdditionalInfo(String temp) {
        System.out.println(temp);
    }

    /**
     * Hlavní metoda hry pro příkazovou řádku (slouží ke spuštění programu)
     * @param args Argumenty programu (spouští se bez argumentů)
     */
    public static void main(String args[]) {
        Controller controller = new Controller();
        ReadLineManager fileManager = new ReadLineManager();
        String[] nextPlayer;

        /**
         * Kontrola vstupních parametrů
         */
        try {
            fileManager.controlInputArguments(args);
        }
        catch (InvalidInputArgumentsException e) {
            System.out.println(e);
            System.exit(1);
        }

        /**
         * Hlavní smyčka programu přijímající tokeny
         */
        while (true) {
            try {
                ArrayList<String> tokenArgumentsArray = new ArrayList<>();

                try {
                    ArrayList<Field> board = null;
                    controller.analyzeNextTurn(board);
                }
                catch (GameEndedException endOfGame) {
                    showMoveInfo(endOfGame.getInfoStrings());
                }
                catch (ComputerHasPlayed computerTurn) {
                    showMoveInfo(computerTurn.getInfoStrings()[4], computerTurn.getInfoStrings()[5],computerTurn.getInfoStrings());
                    continue;
                }
                catch (GameIsNotStartedException e) {}

                TypeOfInstruction typeOfInstruction = fileManager.getDecision(tokenArgumentsArray);
                switch (typeOfInstruction) {
                    case NEW:
                        if (fileManager.getGameType() == null) {
                            nextPlayer = controller.createNewGame(fileManager.getBoardSize());
                        } else {
                            nextPlayer = controller.createNewGame(fileManager.getBoardSize(), fileManager.getGameType());
                        }

                        showMoveInfo(nextPlayer);
                        break;

                    case MOVE:
                        Coords coords = new Coords(tokenArgumentsArray.get(0).charAt(0), Integer.parseInt(tokenArgumentsArray.get(1)));
                        try {
                            nextPlayer = controller.makeMove(coords);
                            showMoveInfo(nextPlayer);
                        } catch (MoveNotAvailableException | GameIsNotStartedException e) {
                            System.out.println(e);
                        }
                        break;

                    case SAVE:
                        try {
                            System.out.println(controller.saveGame(tokenArgumentsArray.get(0)));
                        } catch (GameSavingFailureException | GameIsNotStartedException e) {
                            System.out.println(e);
                        }
                        break;

                    case FREEZE:
                        try {
                            ArrayList<Coords> stonesCoords = new ArrayList<>();
                            nextPlayer = controller.freezeStones(stonesCoords);
                            showAdditionalInfo(nextPlayer[4]);
                            showMoveInfo(nextPlayer);
                        } catch (GameIsNotStartedException e) {
                            System.out.println(e);
                        }
                        break;

                    case LOAD:
                        try {
                            nextPlayer = controller.loadGame(tokenArgumentsArray.get(0));
                            showAdditionalInfo(nextPlayer[4]);
                            showMoveInfo(nextPlayer);
                        } catch (GameLoadingNameNotFoundException | GameLoadingFailureException e) {
                            System.out.println(e);
                        }
                        break;

                    case UNDO:
                        try {
                            nextPlayer = controller.undoMove();
                            showMoveInfo(nextPlayer);
                        } catch (NoMoreMovesToUndoException | GameIsNotStartedException e) {
                            System.out.println(e);
                        }
                        break;

                    //case WINDOW:
                    //    controller.createWindow();
                    //    break;
                }

            } catch (ReadingFromConsoleFailureException | InvalidTokenInputException | BadTokenArgumentException e) {
                System.out.println(e);
            }
        }
    }
}

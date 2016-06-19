/**
 * Třída pro rozeznání tokenů a jejich zpracování
 * Funkce:  1) Získání a zpracování tokenů
 *          2) Kontrola argumentů
 * @author Lukáš Dibďák
 * @see othello.GameCommandLine
 */

package othello;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

import othello.Utility.*;

public class ReadLineManager {
    private int boardSize;
    private TypeOfGame gameType;

    /**
     * Metoda pro kontrolu vstupních argumentů příkazové řádky
     * @param arguments Pole typu {@code String} argumentů příkazové řádky
     * @throws InvalidInputArgumentsException Chybné argumenty příkazové řádky
     */
    void controlInputArguments(String[] arguments) throws InvalidInputArgumentsException {
        if (arguments.length > 1) {
            throw new InvalidInputArgumentsException();
        }
        if (arguments.length == 1) {
            if(arguments[0].equals("--help")) {
                Utility.help();
                Utility.instructionsDescription();
            }
            else {
                throw new InvalidInputArgumentsException();
            }
        }
    }

    /**
     * Získání velikosti desky
     * @return Velikost desky
     */
    int getBoardSize() {
        return boardSize;
    }

    /**
     * Získání obtížnosti hry, pokud je druhý hráč počítač
     * @return Obtížnost hry
     */
    TypeOfGame getGameType() {
        return gameType;
    }

    /**
     * Kontrola argumentů pro instrukci NEW. Očekává se typ druhého hráče, velikost desky a případně obtížnost počítače
     * @param arguments Argumenty tokenu NEW
     * @throws BadTokenArgumentException Chybné argumenty tokenu NEW
     */
    void controlGameArguments(ArrayList<String> arguments) throws BadTokenArgumentException {
        String argument;
        argument = arguments.get(0);

        if (argument.equals("H")) {
            gameType = null;
            if (arguments.size() != 2) {
                throw new BadTokenArgumentException();
            } else {
                int boardSizeInteger = Integer.parseInt(arguments.get(1));
                if (boardSizeInteger < 6 || boardSizeInteger > 12 || !(boardSizeInteger % 2 == 0)) {
                    this.boardSize = 8;
                } else {
                    this.boardSize = boardSizeInteger;
                }
            }
        }
        else if(argument.equals("C")) {
            if (arguments.size() != 3) {
                throw new BadTokenArgumentException();
            } else {
                int boardSize = Integer.parseInt(arguments.get(1));
                if (boardSize < 6 || boardSize > 12 || !(boardSize % 2 == 0)) {
                    this.boardSize = 8;
                } else {
                    this.boardSize = boardSize;
                }
                argument = arguments.get(2);
                if (argument.equals("easy")) {
                    gameType = TypeOfGame.EASY;
                }
                else if(argument.equals("hard")) {
                    gameType = TypeOfGame.HARD;
                }
                else {
                    throw new BadTokenArgumentException();
                }
            }
        }
        else {
            throw new BadTokenArgumentException();
        }
    }

    /**
     * Kontrola argumentů pro instrukci MOVE. Očekávají se pouze vodorovné a svislé souřadnice pole
     * @param arguments Argumenty tokenu MOVE
     * @throws BadTokenArgumentException Chybné argumenty tokenu MOVE
     */
    void controlMakeMoveArguments(ArrayList<String> arguments) throws BadTokenArgumentException {
        String argument;
        argument = arguments.get(0);
        if (argument.length() > 1) {
            throw new BadTokenArgumentException();
        }
        else if (Character.toString(argument.charAt(0)).matches("[^a-" + Utility.transformIntToChar(Board.SIZE) + "]")) {
            throw new BadTokenArgumentException();
        }
        argument = arguments.get(1);
        int number = Integer.parseInt(argument);
        if (number < 0 || number > Board.SIZE) {
            throw  new BadTokenArgumentException();
        }
    }

    /**
     * Metoda pro zpracování tokenů
     * @param arguments Proměnná pro vrácení proměnného počtu argumentů tokenů
     * @return Typ instrukce, která má být vykonána
     * @throws ReadingFromConsoleFailureException Chyba čtení příkazové řádky či jiná režijní chyba
     * @throws InvalidTokenInputException Chybný typ zadaného tokenu (instrukce)
     * @throws BadTokenArgumentException Token obsahuje více, méně či špatné argumenty
     * @see TypeOfGame
     */
    TypeOfInstruction getDecision(ArrayList<String> arguments) throws ReadingFromConsoleFailureException, InvalidTokenInputException, BadTokenArgumentException {
        BufferedReader commandLineReader = new BufferedReader(new InputStreamReader(System.in));
        String decision;
        try {
            decision = commandLineReader.readLine();
        } catch (IOException e) {
            throw new ReadingFromConsoleFailureException();
        }
        StringTokenizer tokenizer = new StringTokenizer(decision);
        if (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken();
            switch (token) {
                case "MOVE":
                case "move":
                    for (int i = 0; i < TypeOfInstruction.MOVE.getNumberOfArgumentRequired(); i++) {
                        if (tokenizer.hasMoreTokens()) {
                            String makeMoveArgument = tokenizer.nextToken();
                            arguments.add(makeMoveArgument);
                        } else {
                            throw new BadTokenArgumentException();
                        }
                    }
                    if (tokenizer.hasMoreTokens()) {
                        throw new BadTokenArgumentException();
                    }
                    try {
                        controlMakeMoveArguments(arguments);
                    } catch (BadTokenArgumentException | NumberFormatException e) {
                        throw new BadTokenArgumentException();
                    }
                    return TypeOfInstruction.MOVE;
                case "SAVE":
                case "save":
                    if (tokenizer.hasMoreTokens()) {
                        String fileName = tokenizer.nextToken();
                        arguments.add(fileName);
                        if (tokenizer.hasMoreTokens()) {
                            throw new BadTokenArgumentException();
                        }
                    } else {
                        throw new BadTokenArgumentException();
                    }
                    return TypeOfInstruction.SAVE;
                case "FREEZE":
                case "freeze":
                    if (tokenizer.hasMoreTokens()) {
                        throw new BadTokenArgumentException();
                    }
                    return TypeOfInstruction.FREEZE;
                case "LOAD":
                case "load":
                    if (tokenizer.hasMoreTokens()) {
                        String fileName = tokenizer.nextToken();
                        arguments.add(fileName);
                        if (tokenizer.hasMoreTokens()) {
                            throw new BadTokenArgumentException();
                        }
                    } else {
                        throw new BadTokenArgumentException();
                    }
                    return TypeOfInstruction.LOAD;
                case "NEW":
                case "new":
                    for (int i = 0; i < TypeOfInstruction.NEW.getNumberOfArgumentRequired(); i++) {
                        if (tokenizer.hasMoreTokens()) {
                            String newGameArgument = tokenizer.nextToken();
                            arguments.add(newGameArgument);
                        } else {
                            throw new BadTokenArgumentException();
                        }
                    }
                    if (tokenizer.hasMoreTokens()) {
                        String newGameArgument = tokenizer.nextToken();
                        arguments.add(newGameArgument);
                    }
                    try {
                        controlGameArguments(arguments);
                    } catch (BadTokenArgumentException | NumberFormatException e) {
                        throw new BadTokenArgumentException();
                    }
                    return TypeOfInstruction.NEW;
                case "UNDO":
                case "undo":
                    if (tokenizer.hasMoreTokens()) {
                        throw new BadTokenArgumentException();
                    }
                    return TypeOfInstruction.UNDO;
            }
        }
        throw new InvalidTokenInputException();
    }
}

/**
 * Třída pro správu Save/Load operací
 * Funkce:  1) Operace nad souborovým systémem, manipulace se složkou a umístěním souborů
 *          2) Ukládání her
 *          3) Načítání her
 * @author Lukáš Dibďák
 * @see othello.Controller
 */

package othello;

import java.util.*;
import othello.Utility.*;
import java.io.*;

class SaveLoadManager {
    File pathToEnviroment = new File(System.getProperty("user.dir"));
    File nameOfFolder = new File(pathToEnviroment + System.getProperty("file.separator") + Utility.getSaveFolderLocationString());
    File nameOfSave = null;

    /**
     * Metoda pro uložení her do souboru
     * @param nameOfGame Jméno hry pro uložení
     * @param players Množina hráčů hry
     * @param logger Zásobník hracích desek pro {@code undoMove}
     * @param activePlayer Tah hráče
     * @param typeOfGame Obtížnost počítače (je-li druhý hráč počítač)
     * @throws GameSavingFailureException Problém při ukládání hry, např. zapisování souborů
     */
    void save(String nameOfGame, Player[] players, ArrayDeque<Board> logger, int activePlayer, TypeOfGame typeOfGame) throws GameSavingFailureException {
        char playerTypeChar; String typeOfGameString = "null";
        if (players[Utility.PLAYERTWO].getPlayerType() == PlayerType.HUMAN)
            playerTypeChar = PlayerType.HUMAN.getKey();
        else  {
            playerTypeChar = PlayerType.COMPUTER.getKey();
            typeOfGameString = typeOfGame.getDifficulty();
        }

        String[] undoMoves = new String[logger.size()];

        int i; Iterator<Board> index;
        StringBuilder temp = new StringBuilder();
        for (index = logger.iterator(), i = 0; index.hasNext(); i++) {
            temp.setLength(0);
            for (Field field: index.next().getField()) {
                try {
                    switch (field.getColor()) {
                        case BLACK:
                        case FBLACK:
                            temp.append(Color.BLACK.getKey());
                            break;
                        case WHITE:
                        case FWHITE:
                            temp.append(Color.WHITE.getKey());
                    }
                } catch (FieldIsEmptyException e) {
                    temp.append(Color.NONE.getKey());
                }
            }
            undoMoves[i] = temp.toString();
        }

        nameOfSave = new File(nameOfFolder + System.getProperty("file.separator") + nameOfGame + Utility.getFileExtensionString());
        if (!nameOfFolder.exists()) {
            nameOfFolder.mkdir();
        }
        try {
            nameOfSave.createNewFile();
            try (FileWriter fout = new FileWriter(nameOfSave)) {
                fout.write(playerTypeChar);
                fout.write(System.lineSeparator());

                fout.write(Integer.toString(Board.SIZE));
                fout.write(System.lineSeparator());

                fout.write(typeOfGameString);
                fout.write(System.lineSeparator());

                fout.write(Integer.toString(activePlayer));
                fout.write(System.lineSeparator());

                for (String moveRecord: undoMoves) {
                    fout.write(moveRecord);
                    fout.write(System.lineSeparator());
                }

                fout.flush();
                fout.close();
            } catch (FileNotFoundException e) {
                throw new GameSavingFailureException();
            }
        } catch (IOException e) {
            throw new GameSavingFailureException();
        }
    }

    /**
     * Metoda pro načtení hry ze souboru
     * @param nameOfGame Jméno hry k načtení
     * @return Pole typu {@code String} obsahující všechny informace o načtené hře
     * @throws GameLoadingNameNotFoundException Zadaný název hry neexistuje
     * @throws GameLoadingFailureException Problém při načítání hry, např. čtení souborů
     */
    ArrayList<String> load(String nameOfGame) throws GameLoadingNameNotFoundException, GameLoadingFailureException {
        ArrayList<String> gameInfo = new ArrayList<>();

        try (FileReader fin = new FileReader(nameOfFolder + System.getProperty("file.separator") + nameOfGame + Utility.getFileExtensionString());) {
            try {
                int data; StringBuilder temp = new StringBuilder();
                while ((data = fin.read()) != -1) {
                    if (Character.isWhitespace(data) && temp.length() != 0) {
                        gameInfo.add(temp.toString());
                        temp.setLength(0);
                    }
                    else temp.append((char) data);
                }
            } catch (IOException e) {
                throw new GameLoadingFailureException();
            }
        } catch (FileNotFoundException e) {
            throw new GameLoadingNameNotFoundException();
        } catch (IOException e) {
            throw new GameLoadingFailureException();
        }

        return gameInfo;
    }
}

/**
 * Třída pro pomocné operace a podporu programu
 * Funkce:  1) Podpora enumů
 *          2) Podpora inline tříd algoritmů a souřadnic
 *          3) Pomocné metody pro prezentaci dat
 *          4) Jednotné výpisy informací
 *          5) Pomocné parsovací metody
 *          6) Generování čísel
 *          7) Nápověda
 * @author Lukáš Dibďák
 * @see othello.Controller
 * @see othello.Game
 * @see othello.SaveLoadManager
 */

package othello;

import java.util.*;

public class Utility {
    /**
     * Identifikace hráčů pro indexování
     */
    static int PLAYERONE = 0;
    static int PLAYERTWO = 1;

    /**
     * Konstanta pro maximální dobu zmrazení kamenů
     */
    static int MINGENERATETIME = 5;
    static int MAXINITFREEZETIME = 10;
    static int MAXPERSISTFREEZETIME = 15;

    /**
     * Řetězcová interpretace jmen hráčů
     */
    static String[] PLAYERS = new String[2];

    /**
     * Inline třída pro přehlednější prezentaci souřadnic pole
     */
    static class Coords implements Comparable<Coords> {;
        private int x, y;

        /**
         * Konstruktor pro vodorovnou souřadnice zapsanou znakovým identifikátorem
         * @param x Vodorovná souřadnice
         * @param y Svislá souřadnice
         */
        Coords(char x, int y) {
            this.x = Utility.transformCharToInt(x);
            this.y = y - 1;
        }

        /**
         * Konstruktor pro vodorovnou souřadnice zapsanou číselným identifikátorem
         * @param x Vodorovná souřadnice
         * @param y Svislá souřadnice
         */
        Coords(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Přepsání metody vyšší třídy pro porovnání pro využití rozhraní Comparable
         * Porovnává vodorovnou souřadnici a vrací její porovnání pro mapování
         * @param o Souřadnice k porovnání
         * @return Stejná, menší nebo větší vodorovná souřadnice
         */
        @Override
        public int compareTo(Coords o) {
            if (this.getX() < o.getX())
                return -1;
            else if (this.getX() == o.getX()) {
                if(this.getY() > o.getY()) {
                    return 1;
                }
                if(this.getY() == o.getY()) {
                    return 0;
                }
                if(this.getY() < o.getY()) {
                    return -1;
                }

            }
            return 1;
        }

        /**
         * Přetížení metody pro porovnání objektu
         * Porovnává souřadnice
         * @param temp Druhé souřadnice k porovnání
         * @return Stejné/rozdílné
         */
        public boolean equals(Coords temp) {
            return (temp.getX() == this.getX() && temp.getY() == this.getY());
        }

        int getX() { return x; }
        int getY() { return y; }
    }

    /**
     * Inline třída pro zpracování algoritmů počítače a jeho tahů
     */
    static class Algorithm {
        /**
         * Setřídí kameny, které se musí otočit do jednoho zásobníku podle souřadnice tahu
         * @param allAvailableMoves Zásobník všech dostupných stavů
         * @return Setřízený zásobník
         */
        static TreeMap<Coords, ArrayList<Coords>> sortStonesByCoords(ArrayList<TreeMap<Coords, ArrayList<Coords>>> allAvailableMoves) {
            TreeMap<Coords, ArrayList<Coords>> temp = new TreeMap<>();

            for (TreeMap<Coords, ArrayList<Coords>> map: allAvailableMoves) {
                for (Map.Entry<Coords, ArrayList<Coords>> mapSet: map.entrySet()) {
                    if (temp.containsKey(mapSet.getKey())) {
                        ArrayList<Coords> tempCoords = temp.get(mapSet.getKey());
                        for (Coords x: mapSet.getValue()) {
                            tempCoords.add(x);
                        }
                    } else {
                        temp.put(mapSet.getKey(), mapSet.getValue());
                    }
                }
            }

            return temp;
        }

        /**
         * Jednoduchý algoritmus pro tah počítače
         * @param allAvailableMoves Zásobník všech dostupných stavů
         * @return Pole hrací desky, kam počítač táhnul
         */
        static TreeMap<Coords, ArrayList<Coords>> getEasyAlgorithm(ArrayList<TreeMap<Coords, ArrayList<Coords>>> allAvailableMoves) {
            TreeMap<Coords, ArrayList<Coords>> temp = sortStonesByCoords(allAvailableMoves);
            TreeMap<Coords, ArrayList<Coords>> returnval = new TreeMap<>();

            int minSize = temp.firstEntry().getValue().size();
            returnval.put(temp.firstEntry().getKey(), temp.firstEntry().getValue());

            for(Map.Entry<Coords, ArrayList<Coords>> tempSet: temp.entrySet()) {
                if (minSize > tempSet.getValue().size()) {
                    returnval.clear();
                    returnval.put(tempSet.getKey(), tempSet.getValue());
                    minSize = tempSet.getValue().size();
                }
            }

            return returnval;
        }

        /**
         * Složitější algoritmus pro tah počítače
         * @param allAvailableMoves Zásobník všech dostupných stavů
         * @return Pole hrací desky, kam počítač táhnul
         */
        static TreeMap<Coords, ArrayList<Coords>> getHardAlgorithm(ArrayList<TreeMap<Coords, ArrayList<Coords>>> allAvailableMoves) {
            TreeMap<Coords, ArrayList<Coords>> temp = sortStonesByCoords(allAvailableMoves);
            TreeMap<Coords, ArrayList<Coords>> returnval = new TreeMap<>();

            int maxSize = temp.firstEntry().getValue().size();
            returnval.put(temp.firstEntry().getKey(), temp.firstEntry().getValue());

            for(Map.Entry<Coords, ArrayList<Coords>> tempSet: temp.entrySet()) {
                if (maxSize < tempSet.getValue().size()) {
                    returnval.clear();
                    returnval.put(tempSet.getKey(), tempSet.getValue());
                    maxSize = tempSet.getValue().size();
                }
            }

            return returnval;
        }
    }

    /**
     * Enum - Barva kamenů
     * - WHITE  - bílá
     * - BLACK  - černá
     * - FWHITE - bílá, zmrazený kámen
     * - FBLACK - černá, zmrazený kámen
     * - NONE   - žádný kámen na políčku
     */
    public enum Color {
        WHITE('W'),
        BLACK('B'),
        FWHITE('E'),
        FBLACK('K'),
        NONE('0');

        private char key;

        /**
         * Konstruktor
         * @param key Jednoznačný znakový identifikátor typu kamenu
         */
        Color(char key) {
            this.key = key;
        }

        /**
         * Vrací identifikátor kamene
         * @return Identifikátor kamene
         */
        char getKey() {
            return key;
        }
    }

    /**
     * Enum - Typ hráče
     * - COMPUTER - počítačový hráč
     * - HUMAN    - lidský hráč
     *
     * - PONE - řetězec identifikující prvního lidského hráče
     * - PTWO - řetězec identifikující druhého ldeského hráče
     * - COMP - řetězec identifikující počítač
     */
    public enum PlayerType {
        COMPUTER('C'),
        HUMAN('H'),

        PONE("[hrac 1]"),
        PTWO("[hrac 2]"),
        COMP("[pocitac]");

        private char key = 0;
        private String name = null;

        /**
         * Konstruktor
         * @param c Znak reprezentující počítač/lidského hráče
         */
        PlayerType(char c) {
            this.key = c;
        }

        /**
         * Konstruktor
         * @param name Řetězcová identifikace hráče
         */
        PlayerType(String name) {
            this.name = name;
        }

        /**
         * Vrací řetězcovou identifikace hráče
         * @return Řetězcová identifikace hráče
         */
        String getName() {
            return name;
        }

        /**
         * Vrací znak reprezentující počítač/lidského hráče
         * @return Znak reprezentující počítač/lidského hráče
         */
        char getKey() {
            return key;
        }
    }

    /**
     * Enum - Obtížnost hry
     * - EASY - jednoduchá obtížnost tahů počítače
     * - HARD - složitá obtížnost tahů počítače
     */
    public enum TypeOfGame {
        EASY("easy"),
        HARD("hard");

        private String difficulty;

        /**
         * Konstruktor
         * @param difficulty Řetězcová interpretace složitosti hry
         */
        TypeOfGame(String difficulty) {
            this.difficulty = difficulty;
        }

        /**
         * Vrací řetězcovou interpretaci složitosti hry
         * @return Řetězcová interpretace složitosti hry
         */
        String getDifficulty() {
            return difficulty;
        }
    }

    /**
     * Enum - typ instrukce (tokenu)
     * - MOVE   - tah hráče na hrací desku
     * - SAVE   - uložení hry
     * - LOAD   - načtení hry
     * - NEW    - vytvoření nové hry
     * - UNDO   - vrácení tahu
     * - FREEZE - zmrazení kamenů
     */
    public enum TypeOfInstruction {
        MOVE(2),
        SAVE(1),
        LOAD(1),
        NEW(2),
        UNDO(),
        FREEZE();

        private int numberOfArgumentRequired;

        /**
         * Implicitní konstruktor
         * Neočekává žádný argument tokenu
         */
        TypeOfInstruction() {
            this.numberOfArgumentRequired = 0;
        }

        /**
         * Konstruktor
         * @param numberOfArgumentsRequired Potřebný počet argumentů tokenu
         */
        TypeOfInstruction(int numberOfArgumentsRequired) {
            this.numberOfArgumentRequired = numberOfArgumentsRequired;
        }

        /**
         * Vrací počet argumentů tokenu
         * @return Počet potřebných argumentů tokenu
         */
        int getNumberOfArgumentRequired() {
            return numberOfArgumentRequired;
        }
    }

    /**
     * Metoda pro transformaci vodorovné souřadnice desky z char na int
     * @param x Znakový identifikátor vodorovné osy hrací desky
     * @return Číselný identifikátor, který odpovídá znakovému identifikátoru vodorovné osy hrací desky
     */
    static int transformCharToInt(char x) {
        return (int) x - 97;
    }

    /**
     * Metoda pro transformaci vodorovné souřadnice desky z int na char
     * @param x Číselný identifikátor vodorovné osy hrací desky
     * @return Znakový identifikátor, který odpovídá číselnému identifikátoru vodorovné osy hrací desky
     */
    static char transformIntToChar(int x) { return (char) (x + 97); }

    /**
     * Získání znakové interpretace úspěšného uložení hry
     * @return Řetězec znaků
     */
    static String getSuccessfulSaveGameString() { return "Hra byla uspesne ulozena."; }

    /**
     * Získání znakové interpretace úspěšného načtení hry
     * @return Řetězec znaků
     */
    static String getSuccessfulLoadGameString() { return "Hra byla uspesne nactena."; }

    /**
     * Získání znakové interpretace formátu uložených/načtených her
     * @return Řetězec znaků
     */
    static String getFileExtensionString() { return ".txt"; }

    /**
     * Získání znakové interpretace jména složky pro uložení/načtení hry
     * @return Řetězec znaků
     */
    static String getSaveFolderLocationString() { return "save"; }

    /**
     * Získání znakové interpretace úspěšného zmrazení kamenů
     * @param numbers Pole typu {@code Integer}. Kolik kamenů, za jak dlouho, na jak dlouho
     * @return Řetězec znaků
     */
    static String getSuccessfulFreezeStoneString(int[] numbers, int numberOfStones) {
        return "Za dobu: " + numbers[0] + " sekund bude zmrazen pocet kamenu: " + numberOfStones + " na dobu: " + numbers[1] + " sekund";
    }

    /**
     * Získání znakové interpretace aktuálního tahu hry
     * @param player Identifikace hráče, který je aktuálně na tahu
     * @return Řetězec znaků
     */
    static String getPlayerTurnString(int player) {
        if (player == PLAYERONE) {
            return PLAYERS[PLAYERONE] + " [BLACK]";
        }
        else {
            return PLAYERS[PLAYERTWO] + " [WHITE]";
        }
    }

    /**
     * Získání znakové interpretace výsledků hry po jejím ukončení
     * @param players Pole hráčů typu {@code Player}
     * @return Řetězec znaků
     */
    static String getGameEndedString(Player[] players) {
        String playerName;

        if (players[PLAYERONE].getScore() > players[PLAYERTWO].getScore()) {
            playerName = "Zvitezil " + PLAYERS[PLAYERONE];
        }
        else if (players[PLAYERONE].getScore() < players[PLAYERTWO].getScore()) {
            playerName = "Zvitezil " + PLAYERS[PLAYERTWO];
        }
        else {
            playerName = "Zapas skoncil remizou";
        }
        return "Hra byla ukoncena.\n" + playerName + ".";
    }

    /**
     * Metoda sloužící k parsování řetězce typu hráče na typ hráče {@code PlayerType}
     * @param temp Znak reprezentující typ hráče
     * @return Typ hráče
     */
    static PlayerType loadParsePlayerType(char temp) {
        return Character.valueOf(temp).equals(PlayerType.COMPUTER.getKey()) ?
                PlayerType.COMPUTER : PlayerType.HUMAN;
    }

    /**
     * Metoda sloužící k parsování řetězce velikosti desky na číslo
     * @param temp Řetězec reprezentující velikost desky
     * @return Velikost desky
     */
    static int loadParseBoardSize(String temp) {
        return Integer.parseUnsignedInt(temp);
    }

    /**
     * Metoda sloužící k parsování řetězce obtížnosti počítače na tento typ
     * @param temp Řetězec reprezentující obtížnost hry
     * @return Obtížnost hry
     */
    static TypeOfGame loadParseTypeOfGame(String temp) {
        return (temp.equals(TypeOfGame.EASY.getDifficulty())) ? TypeOfGame.EASY : TypeOfGame.HARD;
    }

    /**
     * Metoda sloužící k parsování řetězce tahu hráče na číselný typ
     * @param temp Řetězec reprezentující aktuální tah
     * @return Aktuální tah
     */
    static int loadParseActivePlayer(String temp) {
        return Integer.parseUnsignedInt(temp);
    }

    /**
     * Metoda sloužící k parsování řetězců hracích desek na tyto hrací desky
     * @param boardsInString Zásobník řetězců reprezentující hrací desky
     * @param boardSize Velikost desky
     * @return Zásobník hracích desek
     */
    static ArrayDeque<Board> loadParseBoards(ArrayList<String> boardsInString, int boardSize) {
        ArrayDeque<Board> boards = new ArrayDeque<>();

        while (!boardsInString.isEmpty()) {
            String temp = boardsInString.remove(0);
            Board tempBoard = new Board(boardSize, temp);
            boards.addFirst(tempBoard);
        }

        return boards;
    }

    /**
     * Metoda pro generování čísel pro metodu {@code freezeStones}
     * @param randomNumbers Pole odkazů, kam se uloží výsledky
     * @param notFrozenStones Počet kamenů hráče, které ještě nejsou zamrznuty a lze je tedy nechat zamrznout
     */
    static void generateRandomNumbers(int[] randomNumbers, int notFrozenStones, ArrayList<Integer> numberOfFrozenStones) {
        Random random = new Random();

        randomNumbers[0] = random.nextInt(Utility.MAXINITFREEZETIME - Utility.MINGENERATETIME) + Utility.MINGENERATETIME;
        randomNumbers[1] = random.nextInt(Utility.MAXPERSISTFREEZETIME - Utility.MINGENERATETIME) + Utility.MINGENERATETIME;

        int numberOfStones = random.nextInt(notFrozenStones);

        for (int i = 0; i < numberOfStones; i++) {
            int randomNumber = random.nextInt(notFrozenStones);
            if (!numberOfFrozenStones.contains(randomNumber)) {
                numberOfFrozenStones.add(randomNumber);
            }
        }
    }

    /**
     * Metoda pro vizualizaci hrací desky
     * @param board Hrací deska
     * @return Řetězec znaků odpovídající hodnotám hrací desky
     */
    static String visualizeBoard(Board board) {
        StringBuilder temp = new StringBuilder();
        for (Field field: board.getField()) {
            try {
                switch (field.getColor()) {
                    case WHITE:
                        temp.append(Color.WHITE.getKey());
                        break;
                    case BLACK:
                        temp.append(Color.BLACK.getKey());
                        break;
                    case FWHITE:
                        temp.append(Color.FWHITE.getKey());
                        break;
                    case FBLACK:
                        temp.append(Color.FBLACK.getKey());
                        break;
                }
            } catch (FieldIsEmptyException e) {
                temp.append(Color.NONE.getKey());
            }
        }
        return temp.toString();
    }

    /**
     * Metoda nastavující pomocnou identifikaci jmen hráčů
     * @param computer Je druhý hráč počítač nebo hráč
     */
    static void setPlayerString(boolean computer) {
        PLAYERS[PLAYERONE] = PlayerType.PONE.getName();
        if (computer)
            PLAYERS[PLAYERTWO] = PlayerType.COMP.getName();
        else
            PLAYERS[PLAYERTWO] = PlayerType.PTWO.getName();
    }

    /**
     * Metoda zjišťuje, zdali je pole o zadaných souřadnicích na hrací desce
     * @param coords Souřadnice pole
     * @return Pole leží na desce/pole neleží na desce
     */
    static boolean isInBoard(Coords coords) {
        if (coords.getY() < 0 || coords.getY() >= Board.SIZE) {
            return false;
        }
        else if (coords.getX() < 0 || coords.getX() >= Board.SIZE) {
            return false;
        }
        return true;
    }

    /**
     * Nápověda k programu
     */
    static void help() {
        System.out.println("Nápověda ke hře Reversi:");
        System.out.println("-----------------------------------------------------------------");
        System.out.println("Reversi, je desková hra pro dva hráče, hraná na desce\n" +
                           "[6x6][8x8][10x10][12x12] polí. Hráči na desku pokládají\n" +
                           "kameny, které jsou z jedné strany bílé a z druhé černé,\n" +
                           "tak, aby mezi právě položený kámen a jiný kámen své\n" +
                           "barvy uzavřeli souvislou řadu soupeřových kamenů; tyto\n" +
                           "kameny se potom otočí a stanou se kameny druhého hráče.\n" +
                           "Vítězí hráč, který po zaplnění desky na ní má více svých\n" +
                           "kamenů. Pole se označují obdobně jako na šachovnici, tedy\n" +
                           "sloupce písmeny, řady čísly. Lze také nechat náhodný počet\n" +
                           "kamenů hráče zamrznout, pak nemohou být při žádném tahu\n" +
                           "otočeny dokud opět nerozmrznou.");
        System.out.println("Zdroj: wikipedia.org");
        System.out.println("-----------------------------------------------------------------");
    }

    /**
     * Popis instrukcí příkazové řádky
     */
    static void instructionsDescription() {
        System.out.println("Popis instrukcí pro příkazovou řádku:");
        System.out.println("-----------------------------------------------------------------");
        System.out.println("1. vytvoření nové hry");
        System.out.println("NEW TYP_HRACE VELIKOST_DESKY [OBTÍŽNOST_HRY]");
        System.out.println("- TYP HRÁČE - typ druhého hráče, tento hráč může být");
        System.out.println("              definován jako počítač [C] nebo lidský");
        System.out.println("              hráč [H]");
        System.out.println("- VELIKOST_DESKY - velikost hrací desky, povolené jsou");
        System.out.println("                   tyto hodnoty: [6], [8], [10], [12]");
        System.out.println("- OBTÍŽNOST HRY - pokud je jako TYP_HRACE zvolen počítač,");
        System.out.println("                  pak lze zvolit obtížnost hry jako");
        System.out.println("                  jednoduchou [easy] nebo složitou [hard]");
        System.out.println("- Ukázka instrukce: NEW C 12 EASY");
        System.out.println("-----------------------------------------------------------------");
        System.out.println("2. načtení uložené hry");
        System.out.println("LOAD JMÉNO_HRY");
        System.out.println("- JMENO_HRY - název, ze kterého se má hra nahrát");
        System.out.println("- Ukázka instrukce: LOAD hra");
        System.out.println("-----------------------------------------------------------------");
        System.out.println("3. uložení hry");
        System.out.println("SAVE JMÉNO_HRY");
        System.out.println("- JMENO_HRY - název, kdo kterého se má hra uložit");
        System.out.println("- Ukázka instrukce: SAVE hra");
        System.out.println("-----------------------------------------------------------------");
        System.out.println("4. tah hráče");
        System.out.println("MOVE VODOROVNA_SOURADNICE SVISLA_SOURADNICE");
        System.out.println("- VODOROVNA_SOURADNICE - souřadnice tahu hráče zapsaná");
        System.out.println("  ve znakové podobě");
        System.out.println("- SVISLA_SOURADNICE - souřadnice tahu zapsaná v číselné");
        System.out.println("  podobě");
        System.out.println("- Ukázka instrukce: MOVE a 2");
        System.out.println("-----------------------------------------------------------------");
        System.out.println("5. vrácení tahu");
        System.out.println("UNDO");
        System.out.println("- Instrukce bez argumentů");
        System.out.println("- Ukázka instrukce: UNDO");
        System.out.println("-----------------------------------------------------------------");
        System.out.println("6. zamrznutí kamenů");
        System.out.println("FREEZE");
        System.out.println("- Instrukce bez argumentů");
        System.out.println("- Instrukce nechá zamrznout náhodný počet kamenů hráče");
        System.out.println("  za na náhodně dlouhou dobu po náhodně dlouhou dobu");
        System.out.println("- Ukázka instrukce: FREEZE");
        System.out.println("-----------------------------------------------------------------");
    }
}

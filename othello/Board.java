/**
 * Třída pro správu hrací desky
 * Funkce:  1) Inicializace hrací desky
 *          2) Alokace polí hrací desky
 *          3) Vyplnění středu desky na začátku hry
 *          4) Nastavení barvy jednotlivých polí desky
 *          5) Získání jednotlivých políček desky
 * @author Lukáš Dibďák
 * @see othello.Game
 * @see othello.Field
 */

package othello;

import othello.Utility.*;

public class Board implements Cloneable {
    static int SIZE;
    private Field[] map;

    /**
     * Konstruktor hrací desky při vytváření nové hry, inicializace hrací desky
     * @param size Velikost hrací desky
     */
    Board(int size)  {
        SIZE = size;
        map = new Field[SIZE * SIZE];

        this.allocateFields();
        this.initBoardStones();
    }

    /**
     * Konstruktor hrací desky při načítání již existující hry
     * @param size Velikost hrací desky
     * @param boardStones Kameny na jednotlivých polích hrací desky
     */
    Board(int size, String boardStones) {
        SIZE = size;
        map = new Field[SIZE * SIZE];
        char[] charArray = boardStones.trim().toCharArray();

        for (int i = 0; i < charArray.length; i++) {
            map[i] = new Field();
            if (charArray[i] == Color.BLACK.getKey()) {
                map[i].setColor(Color.BLACK);
            }
            else if (charArray[i] == Color.WHITE.getKey()) {
                map[i].setColor(Color.WHITE);
            }
        }
    }

    /**
     * Kopírovací konstruktor pro vytváření hlubokých kopií hrací desky k {@code undoMove} a {@code makeUndo}
     * @param board
     * @see Controller
     * @see Game
     */
    Board (Board board) {
        this.map = new Field[board.getField().length];

        for (int i = 0; i < board.getField().length; i++) {
            try {
                this.map[i] = (Field) board.map[i].clone();
            } catch (CloneNotSupportedException e) {}
        }
    }

    /**
     * Alokace prázdných polí na hrací desce
     */
    void allocateFields() {
        for (int i = 0; i < SIZE * SIZE; i++) {
            map[i] = new Field();
        }
    }

    /**
     * Inicializace 4 prostředních polí na hrací desce
     */
    void initBoardStones() {
        for (int i = Board.SIZE / 2; i >= (Board.SIZE / 2) - 1; i--) {
            for (int j = Board.SIZE / 2; j >= (Board.SIZE / 2) - 1; j--) {
                if (i == j)
                    setField(i, j, Color.WHITE);
                else
                    setField(i, j, Color.BLACK);
            }
        }
    }

    /**
     * Metoda zpřístupnícící kopírovací konsturktor třídy
     * Vytváří hlubokou kopii
     * @return
     */
    public Board copy() {
        return new Board(this);
    }

    /**
     * Přetížená metoda pro nastavení pole hrací desky na určitou barvu
     * @param x Vodorovná souřadnice pole na hrací desce
     * @param y Svislá souřadnice pole na hrací desce
     * @param color Barva na kterou má být pole nastaveno
     */
    void setField(int x, int y, Color color) {
        Field temp = map[y * SIZE + x];

        temp.setColor(color);
    }

    /**
     * Přetížená metoda pro první nastavení pole hrací desky na určitou barvu
     * @param coords Souřadnice pole typu {@code Coords}
     * @param color
     * @throws FieldIsNotEmptyException Pole již není prázdné a bylo nastaveno na určitou barvu
     * @see Utility
     */
    void setField(Coords coords, Color color) throws FieldIsNotEmptyException {
        if (!map[coords.getY() * SIZE + coords.getX()].isEmpty()) {
            throw new FieldIsNotEmptyException();
        }
        Field temp = map[coords.getY() * SIZE + coords.getX()];
        temp.setColor(color);
    }

    /**
     * Otočí kámen určité barvy na poli hrací desky
     * @param coords Souřadnice pole
     */
    void changeField(Coords coords) {
        Field temp = getField(coords.getX(), coords.getY());

        if (!temp.isFrozen()) {
            try {
                if (temp.getColor() == Color.BLACK) {
                    temp.setColor(Color.WHITE);
                } else temp.setColor(Color.BLACK);
            } catch (FieldIsEmptyException e) {
            }
        }
    }

    /**
     * Získává pole hrací desky
     * @param x Vodorovná souřadnice pole na hrací desce
     * @param y Svislá souřadnice pole na hrací desce
     * @return Pole typu {@code Field} hrací desky
     * @see Field
     */
    Field getField(int x, int y) {
        return map[y * SIZE + x];
    }

    /**
     * Získává všechny pole hrací desky
     * @return Všechna pole hrací desky typu {@code Field}
     * @see Field
     */
    Field[] getField() {
        return map;
    }
}

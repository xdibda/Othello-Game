/**
 * Třída pro správu hráčů
 * Funkce:  1) Inicializace hráčů
 *          2) Podpora operací nad množinou hráčů
 * @author Lukáš Dibďák
 * @see othello.Game
 */

package othello;

import static othello.Utility.*;

public class Player {
    private Color color;
    private Color frozenColor;
    private PlayerType playerType;
    private int score;

    /**
     * Konstruktor hráče
     * @param color Barva kamenů hráče
     * @param playerType Typ hráče (počítač nebo člověk)
     */
    Player(Color color, PlayerType playerType) {
        this.color = color;
        this.playerType = playerType;
        this.score = 0;

        switch (color) {
            case BLACK:
                frozenColor = Color.FBLACK;
                break;
            case WHITE:
                frozenColor = Color.FWHITE;
        }
    }

    /**
     * Pomocná statická metoda využívající konstruktory pro konstruktor třídy {@code Game}
     * @param playerType Typ druhého hráče
     * @return Pole hráčů typu {@code Player}
     * @see Game
     */
    static Player[] getPlayersForConstructor(PlayerType playerType) {
        Player players[] = {
                new Player(Color.BLACK, PlayerType.HUMAN),
                new Player(Color.WHITE, playerType)
        };

        return players;
    }

    /**
     * Získání typu hráče
     * @return Počítač/hráč typu {@code PlayerType}
     */
    PlayerType getPlayerType() {
        return playerType;
    }

    /**
     * Získání barvy kamene hráče
     * @return Black/white typu {@code Color}
     */
    Color getColor() {
        return color;
    }

    /**
     * Získání barvy zmrazených kamenů
     * @return FBlack/FWhite typu {@code Color}
     */
    Color getFrozenColor() { return frozenColor; }

    /**
     * Nastavení skóre hráče
     * @param score Skóre
     */
    void setScore(int score) {
        this.score = score;
    }

    /**
     * Získání skóre hráče
     * @return Skóre
     */
    int getScore() {
        return score;
    }
}
/**
 * Třída pro správu pole hrací desky
 * Funkce:  1) Získávání informace zdali je pole prázdné
 *          2) Práce s barvou na daném poli
 *          3) Zmrazení kamenů
 * @author Lukáš Dibďák
 */

package othello;

import othello.Utility.*;

import java.util.Timer;
import java.util.TimerTask;

public class Field implements Cloneable {
    /**
     * Vnitřní třída pro plánování události
     */
    class Defrost extends TimerTask {
        int initSec;
        int persistSec;
        int originPersist;

        /**
         *
         * @param initSec
         * @param persistSec
         */
        Defrost(int initSec, int persistSec, int originPersist) {
            this.initSec = initSec;
            this.persistSec = persistSec;
            this.originPersist = originPersist;
        }

        int left() {
            return  (initSec == 0) ? persistSec : initSec;
        }

        boolean write() {
            return !((initSec == 0 && persistSec == originPersist) || persistSec == 0);
        }

        /**
         * Specifikace události, která se má provést při naplánování
         */
        @Override
        public void run() {
            if (initSec != 0)
                initSec--;
            else {
                    frozen = true;
                    if (persistSec != 0)
                        persistSec--;
                    else {
                        frozen = false;
                        available = true;
                        timer.cancel();
                    }
            }
        }

    }

    private Color color;
    private boolean empty;
    private boolean frozen;
    private boolean available;
    Defrost defrost;
    Timer timer;

    /**
     * Konstruktor pole, implicitně nastavené na prázdné
     */
    Field() {
        empty = true;
        frozen = false;
        available = true;
    }

    Field(Field field) {
        this.empty = field.empty;
        this.color = field.color;
        this.frozen = field.frozen;
        this.available = field.available;
    }

    /**
     * Mění barvu mezi zmrazením a odmrazení
     */
    void changeColorFreeze() {
        switch (this.color) {
            case WHITE:
                this.color = Color.FWHITE;
                break;
            case BLACK:
                this.color = Color.FBLACK;
                break;
            case FWHITE:
                this.color = Color.WHITE;
                break;
            case FBLACK:
                this.color = Color.BLACK;
                break;
        }
    }

    /**
     * Získání barvy kamene na poli
     * @return Barva kamene typu {@code Color}
     * @throws FieldIsEmptyException Pole je prázdné a nemá tedy na sobě žádný kámen
     * @see Color
     */
    Color getColor() throws FieldIsEmptyException {
        if (empty) {
            throw new FieldIsEmptyException();
        }

        return this.color;
    }

    /**
     * Nastavení kamene barvy na pole
     * @param color Barva kamene
     */
    void setColor(Color color) {
        empty = false;
        if (!frozen)
            this.color = color;
    }

    int left() {
        return defrost.left();
    }

    boolean write() {
        return defrost.write();
    }

    /**
     * Získání informace zda je pole prázdné
     * @return Prázdné/neprázdné pole
     */
    boolean isEmpty() {
        return empty;
    }

    /**
     * Klonování pole pro získání hluboké kopie
     * @return Klon pole typu {@code Object}
     * @throws CloneNotSupportedException Nekompatibilní typy na klonování
     */
    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /**
     * Událost zmrazení políčka na určitou dobu
     * @param initSec Za jak dlouho se událost provede
     * @param persistSec Jak dlouho bude zmrazení trvat
     */
    void freeze(int initSec, int persistSec) {
        timer = new Timer();
        defrost = new Defrost(initSec, persistSec, persistSec);

        timer.schedule(defrost, 0, 1000);
    }

    /**
     * Ziskává zda je políčko zamrznuté
     * @return zamrznuté/nezamrznuté
     */
    public boolean isFrozen() {
        return frozen;
    }

    /**
     * Zjišťuje, zdali je kámen zmrazen nebo čeká na zmrazení
     * @return
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Nastavuje kámen na volný ke zmrazení
     * @param available kámen volný ke zmrazení
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }
}

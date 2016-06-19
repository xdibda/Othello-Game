package othello;

class GameEndedException extends Exception {
    String[] infoStrings;

    GameEndedException(String[] infoStrings) {
        this.infoStrings = infoStrings;
    }

    String[] getInfoStrings() {
        return infoStrings;
    }
}

class ComputerHasPlayed extends Exception {
    String[] infoStrings;
    char x; int y;

    ComputerHasPlayed(int x, int y) {
        this.x = Utility.transformIntToChar(x);
        this.y = y + 1;
    }

    ComputerHasPlayed(String[] infoStrings) {
        this.infoStrings = infoStrings;
    }

    String getX() { return Character.toString(x); }
    String getY() { return Integer.toString(y); }

    String[] getInfoStrings() {
        return infoStrings;
    }
}

class FieldIsEmptyException extends Exception {
    public String toString() {
        return "Prazdne pole nema kamen.";
    }
}

class ReadingFromConsoleFailureException extends Exception {
    @Override
    public String toString() {
        return "Doslo k chybe cteni z konzole, zkuste to prosim znovu.";
    }
}

class InvalidTokenInputException extends Exception {
    @Override
    public String toString() {
        return "Byl zadany neznamy token, zkuste to prosim znovu.";
    }
}

class BadTokenArgumentException extends Exception {
    @Override
    public String toString() {
        return "Byl zadany neznamy argument tokenu nebo pocet techto tokenu.";
    }
}

class NoMoreMovesToUndoException extends Exception {
    @Override
    public String toString() {
        return "Nejsou k dispozici zadne dalsi tahy k vraceni.";
    }
}

class GameSavingFailureException extends Exception {
    @Override
    public String toString() {
        return "Pri ukladani hry se vyskytla chyba. Hra nebyla ulozena.";
    }
}

class GameLoadingFailureException extends Exception {
    @Override
    public String toString() {
        return "Pri nahravani hry se vyskytla chyba.";
    }
}

class GameLoadingNameNotFoundException extends Exception {
    @Override
    public String toString() {
        return "Jmeno hry k nacteni nebylo nalezeno.";
    }
}

class FieldIsNotEmptyException extends Exception {
    @Override
    public String toString() {
        return "Policko je jiz obsazeno a nelze na nej umistit kamen.";
    }
}

class MoveNotAvailableException extends Exception {
    @Override
    public String toString() {
        return "Tento tah neni pristupny. Opakujte prosim tah.";
    }
}

class NoMovesAvailableException extends Exception {
    @Override
    public String toString() {
        return "Neni k dispozici zadny tah.";
    }
}

class GameIsNotStartedException extends Exception {
    @Override
    public String toString() {
        return "Není aktivní žádná hra na které by mohl hráč táhnout";
    }
}

class InvalidInputArgumentsException extends Exception {
    @Override
    public String toString() {
        return "Program spuštěn se špatnými argumenty. Lze spustit pouze bez argumentů nebo s argumentem --help";
    }
}
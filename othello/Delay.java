package othello;

import java.util.*;

/**
 * Created by Lukas on 04.04.16.
 */
public class Delay {
    int initSec;
    int persistSec;
    Timer timer;
    Countdown countdown;

    class Countdown extends TimerTask {
        int initSec;
        int persistSec;

        Countdown(int initSec, int persistSec) {
            this.initSec = initSec;
            this.persistSec = persistSec;
        }

        int left() {
            return (initSec == 0) ? persistSec : initSec;
        }

        @Override
        public void run() {
            if (initSec == 0) {
                if (persistSec == 0) {
                    timer.cancel();
                }
                else {
                    persistSec--;
                }
            }
            else {
                initSec--;
            }
        }
    }

    Delay(int initSec, int persistSec) {
        this.initSec = initSec;
        this.persistSec = persistSec;
        this.timer = new Timer();
        this.countdown = new Countdown(initSec, persistSec);

        timer.schedule(countdown, 0, 1000);
    }

    int left() {
        return countdown.left();
    }
}

##Project info

**Project:** A game implementing the rules of real game called Othello/Reversi

**Language:** Java SDK8

**Author:** Lukas Dibdak

Faculty of Information Technology, Brno University of Technology

##Content:
- **/othello** - package containing all source codes


##Nápověda ke hře Reversi:
Reversi, je desková hra pro dva hráče, hraná na desce [6x6][8x8][10x10][12x12] polí. 
Hráči na desku pokládají kameny, které jsou z jedné strany bílé a z druhé černé tak, 
aby mezi právě položený kámen a jiný kámen své barvy uzavřeli souvislou řadu soupeřových 
kamenů; tyto kameny se potom otočí a stanou se kameny druhého hráče. Vítězí hráč, který 
po zaplnění desky na ní má více svých kamenů. Pole se označují obdobně jako na šachovnici, 
tedy sloupce písmeny, řady čísly. Lze také nechat náhodný počet kamenů hráče zamrznout, 
pak nemohou být při žádném tahu otočeny dokud opět nerozmrznou. 

Zdroj: wikipedia.org

        
##Popis instrukcí pro příkazovou řádku:

###1. vytvoření nové hry
NEW TYP_HRACE VELIKOST_DESKY [OBTÍŽNOST_HRY]
  - TYP HRÁČE - typ druhého hráče, tento hráč může být
    definován jako počítač [C] nebo lidský
    hráč [H]
  - VELIKOST_DESKY - velikost hrací desky, povolené jsou
    tyto hodnoty: [6], [8], [10], [12]
  - OBTÍŽNOST HRY - pokud je jako TYP_HRACE zvolen počítač,
    pak lze zvolit obtížnost hry jako
    jednoduchou [easy] nebo složitou [hard]
  - Ukázka instrukce: NEW C 12 EASY
 
###2. načtení uložené hry
  LOAD JMÉNO_HRY
  - JMENO_HRY - název, ze kterého se má hra nahrát
  - Ukázka instrukce: LOAD hra

###3. uložení hry
SAVE JMÉNO_HRY
  - JMENO_HRY - název, kdo kterého se má hra uložit
  - Ukázka instrukce: SAVE hra

###4. tah hráče
MOVE VODOROVNA_SOURADNICE SVISLA_SOURADNICE
  - VODOROVNA_SOURADNICE - souřadnice tahu hráče zapsaná ve znakové podobě
  - SVISLA_SOURADNICE - souřadnice tahu zapsaná v číselné podobě
  - Ukázka instrukce: MOVE a 2
  
###5. vrácení tahu 
UNDO
  - Instrukce bez argumentů
  - Ukázka instrukce: UNDO
  
###6. zamrznutí kamenů
FREEZE
  - Instrukce bez argumentů
  - Instrukce nechá zamrznout náhodný počet kamenů hráče za na náhodně 
    dlouhou dobu po náhodně dlouhou dobu
  - Ukázka instrukce: FREEZE

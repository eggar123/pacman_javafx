/*
 * Obligatorisk oppgave i OBJ2100 Oppgave: Pacman.
 * Pacman har 3 liv, om de tre livene blir brukt opp så restarter spillet,
 * og alle de hvite prikkene som har blitt spist respawner.
 * Pacman kan ikke røre ghosts, da mister han et liv.
 */

package pacman;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener {

	private Dimension d; // Høyde og bredde
    private final Font smallFont = new Font("Arial", Font.BOLD, 14);
    private boolean inGame = false; // Sjekker om spillet kjører
    private boolean dying = false; // Sjekker om Pacman lever

    private final int BLOCK_SIZE = 24; // Beskriver hvor stor hver blokk av spillvinduet skal være
    private final int N_BLOCKS = 30; // Beskriver antall blokker
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; // Størrelse på "skjerm"
    private final int MAX_GHOSTS = 12; // Maks antall ghosts som kan eksistere samtidig
    private final int PACMAN_SPEED = 6;// Farten til pacman

    private int N_GHOSTS = 6; // Mengden ghosts som spawner på starten av spillet

    private int lives; // Counters for health
    private int[] dx, dy; // Behøves for posisjon til ghosts
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed; // Trengs for å  bestemme posisjon til ghosts,
                                                                    // og mengden ghosts

    private Image ghost; // Bilder for ghosts
    private Image up, down, left, right; // Bilder for bevegelsesanimasjonene til Pacman

    private int pacman_x, pacman_y, pacmand_x, pacmand_y; // Lagrer koordinatene til PacMan
    private int req_dx, req_dy; // Variabler kontrollert av cursor keys

    private final short levelData[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18,     18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,     16, 16, 16, 16, 16, 16, 24, 24, 24, 24, 24, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16,  16,  16,  16,  16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0, 17, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 0,  16, 16, 16, 16, 16, 16, 16, 16,     16, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0, 17, 16, 16, 20,
            17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 16, 16,     16, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0, 17, 16, 16, 20,
            17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 16, 16,     16, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0, 17, 16, 16, 20,
            17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 16, 16,     16, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0, 17, 16, 16, 20,
            17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 16, 16,     16, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0, 17, 16, 16, 20,
            17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 16, 16,     16, 16, 16, 16, 16, 16, 18, 18, 18, 18, 18, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 20, 0,  17, 16, 16, 16, 16, 16, 16, 16,     16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 18, 16, 16, 24, 24, 24, 24, 24, 24,     24, 24, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 20,  0,  0,  0,  0,  0,  0,      0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 20,  0, 0, 0, 0, 0, 0,   0,      0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 20,  0, 0, 0, 0, 0, 0,     20,  0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 20,  0, 0, 0, 0, 0, 0,     20,  0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,

            17, 16, 16, 16, 16, 16, 16, 16, 20,  0, 17, 16, 16, 16, 16,      20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 20,  0, 17, 16, 16, 16, 16,      20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 20,  0, 17, 16, 16, 16, 16,      20, 0, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16,  16, 16, 16, 16, 16, 16,      16, 16, 16, 16,  16,  16,  16,  16,  16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16,  16, 16, 16, 16, 16, 16,      16, 16, 16, 16,  16, 16, 16, 16, 16,  16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16,  16, 16, 16, 16, 16, 16,      16, 16, 16, 16,  16, 16, 16, 16, 16,  16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16,  16,  16,  16, 16, 16,  0,      0 , 0, 16, 16,  16, 16, 16, 16, 16,  16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,      18, 18, 16, 16, 0, 16, 16, 16, 16,  16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 24, 24, 24, 24, 16, 16, 16, 16, 16, 16, 16,      16, 16, 16, 20, 0, 0,  0,   0,  0, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16,      16, 16, 16, 20, 0, 19, 18, 22,  16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16,      16, 16, 16, 20, 0, 17, 16, 16, 16,  16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 0, 0, 0, 17, 16, 16, 16, 16, 16, 16,      16, 16, 16, 20, 0, 17, 16, 16, 16,  16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 18, 18, 18, 18, 16, 16, 16, 16, 16, 16, 16,      16, 16, 16, 20, 0, 17, 16, 16, 16,  16, 16, 16, 16, 16, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16, 16,      16, 16, 16, 16,18, 16, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,      24, 24, 24, 24,24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28,


            // 0 = SOLID BLÅ
            // 1 = VENSTRE BORDER
            // 2 = TOPP BORDER
            // 4 = HØYRE BORDER
            // 8 = BUNN BORDER
            // 16 = HVITE DOTS/PRIKKER




    };

    private final int validSpeeds[] = {1, 2, 3, 4, 6, 8}; // Array for fart verdier
    private final int maxSpeed = 6; // Maks fart

    private int currentSpeed = 3; // Nåværende farten på spillet
    private short[] screenData; // Tar all data fra levelData og redrawer spillet
    private Timer timer; // Åpner for/tillater animasjoner

    /**
    *Constructor kaller på forskjellige funksjoner
    */
    public Model() {

        loadImages();
        initVariables();
        addKeyListener(new TAdapter());
        setFocusable(true);
        initGame();
    }

    /**
     * Metode som loader bilder for bevegelsesanimasjonene til pacman, samt helse og ghosts
     *
     */
    
    private void loadImages() {
    	down = new ImageIcon("/Pacman-master/images/down.gif").getImage();
    	up = new ImageIcon(     "/Pacman-master/images/up.gif").getImage();
    	left = new ImageIcon("/Pacman-master/images/left.gif").getImage();
    	right = new ImageIcon("/Pacman-master/images/right.gif").getImage();
        ghost = new ImageIcon("/Pacman-master/images/ghost.gif").getImage();


    }

    /**
     * Metode som initialiserer ulike variabler
     *
     */


       private void initVariables() {

        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(800, 800);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];
        
        timer = new Timer(40, this); // Hvor ofte bildet redrawes,
                                                 // nummeret ved første param plass er i millisekunder
                                                 // Desto høyere tall, jo tregere går spillet
        timer.start();
    }

    /**
     * Samling av funksjoner som blir kalt, og displayer grafikk
     * @param g2d
     */
    private void playGame(Graphics2D g2d) {

        if (dying) { // Når pacman dør, kaller death() funksjon

            death();

        } else {

            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }
    }

    /**
     * Intro skjerm, loades hver gang man mister tre liv og ved første gang man starter.
     * Skriftfarge i gul.
     * @param g2d
     */

    private void showIntroScreen(Graphics2D g2d) {
 
    	String start = "SPACE for å starte spillet!";
        g2d.setColor(Color.RED);
        g2d.drawString(start, (SCREEN_SIZE)/4, 150);
    }



    /**
     * Sjekker om det er flere hvite prikker som pacman kan spise.
     * Når alle prikkene er spist, restarter bare spillet
     */

    private void checkMaze() {

        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {

            if ((screenData[i]) != 0) {
                finished = false;
            }
            i++;
        }

        if (finished) {

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }

            if (currentSpeed < maxSpeed) {
                currentSpeed++;
            }

            initLevel();
        }
    }

    /**
     * Om pacman dør mister han et liv (lives--;)
     * Om han har null liv igjen, er inGame=false og continueLevel funksjonen blir kjørt
     */



    private void death() {

    	lives--;

        if (lives == 0) {
            inGame = false;
        }

        continueLevel();
    }

    /**
     * Lar ghosts bevege seg automatisk. Bruker border informasjonen for å bestemme
     * @param g2d
     */


    // 1 = VENSTRE BORDER
    // 2 = TOPP BORDER
    // 4 = HØYRE BORDER
    // 8 = BUNN BORDER


    private void moveGhosts(Graphics2D g2d) {

        int pos;
        int count;

        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12) // Om pacman rører et ghost, så mister han et liv.
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {

                dying = true;
            }
        }
    }


    /**
     * Loader bilde av ghosts
     * @param g2d
     * @param x
     * @param y
     */

    private void drawGhost(Graphics2D g2d, int x, int y) {
    	g2d.drawImage(ghost, x, y, this);
        }



    /**
     * Kontrollerer alt som har med pacman sin bevegelse å gjøre.
     * Hva som skjer når han treffer kanten av banen
     * Hva som skjer når han går over (spiser) hvite prikker(16)
     * Sjekker om pacman er stillestående
     */
    private void movePacman() {

        int pos;
        short ch;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);  // Når pacman er over en block med verdi 16 (hvit prikk)
                                                      // Så forsvinner den, med andre ord den blir spist

            }

            if (req_dx != 0 || req_dy != 0) {                                   // Disse to if setningene sjekker med ch om pacman er inntil en border,
                                                                                // om han er, så kan han ikke bevege seg den retningen
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)            // 1 = VENSTRE BORDER
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)        // 4 = HØYRE BORDER
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)       // 2 = TOPP BORDER
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {    // 8 = BUNN BORDER
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            // Sjekker om pacman er idle ( stillestående)
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        // Farten til pacman blir justert ut ifra tilstanden hans(idle eller ikke)
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }


    /**
     * Sjekker hvilken cursor knapp som blir trykket på
     * og loader opp det passende bilde av pacman ut ifra hvilken retning han går
     * @param g2d
     */
    // dx-1 = LEFT
    // dx 1 = RIGHT
    // dy-1 = UP
    // dy 1 = DOWN

    private void drawPacman(Graphics2D g2d) {

        if (req_dx == -1) {
        	g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
        	g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
        	g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
        	g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }


    /**
     * Spillet blir tegnet, med 900 tall.
     * itereres gjennom to for loops, med SCREEN_SIZE og BLOCK_SIZE, gjennom hele arrayet
     * Om et område(field) i arrayen er 0,  blir det farget blått
     * Om et område(field) i arrayen er 1,  blir LEFT Border tegnet
     * Om et område(field) i arrayen er 2,  blir TOP Border tegnet
     * Om et område(field) i arrayen er 4,  blir RIGHT Border tegnet
     * Om et område(field) i arrayen er 8,  blir BOTTOM Border tegnet
     * Om et område(field) i arrayen er 16, blir en hvit prikk tegnet
     *
     * @param g2d
     */

    private void drawMaze(Graphics2D g2d) {

        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(60,179,113));
                g2d.setStroke(new BasicStroke(5));
                
                if ((levelData[i] == 0)) { 
                	g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
                 }

                if ((screenData[i] & 1) != 0) { 
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 2) != 0) { 
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
                }

                if ((screenData[i] & 4) != 0) { 
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 8) != 0) { 
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1);
                }

                if ((screenData[i] & 16) != 0) { 
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6);
               }

                i++;
            }
        }
    }

    /**
     * initialiserer spillet, med antall liv man har, antall ghosts som starer og fart.
     */

    private void initGame() {

    	lives = 3; // Antall ganger man kan dø før spillet restarter

        initLevel();
        N_GHOSTS = 12;
        currentSpeed = 3;
    }

    /**
     * For å initialisere levelet, brukes en for loop som kopierer hele
     * "banen" fra arrayet levelData[], til et nytt array screenData[]
     */

    private void initLevel() {

        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i];
        }

        continueLevel();
    }

    /**
     * Definerer posisjon og fart til ghosts
     * Ment til etter man har dødd, så blir man plasssert tilbake til startposisjonen
     */


    private void continueLevel() {

    	int dx = 1;
        int random;

        for (int i = 0; i < N_GHOSTS; i++) {

            ghost_y[i] = 8 * BLOCK_SIZE; //Start posisjon for ghosts
            ghost_x[i] = 16 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1));

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random];
        }

        pacman_x = 7 * BLOCK_SIZE;  //Start posisjon for pacman
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0;
        pacmand_y = 0;
        req_dx = 0;
        req_dy = 0;
        dying = false;
    }

    /**
     *
     * @param g
     * Med super blir contructor av parent-klassen kalt
     */


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black); // Bakgrunns farge på banen/brettet
        g2d.fillRect(0, 0, d.width, d.height); // Tegner posisjon med fillRect,
                                                    // og bruker bredden og høyden til Dimension d;
        drawMaze(g2d);


        if (inGame) {    // Om inGame = true kjøres playGame(g2d) funksjonen
            playGame(g2d);
        } else {        // Om inGame = false
                        // blir man kastet tilbake til intro skjerm (showIntroScreen(g2d);
            showIntroScreen(g2d);
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose();
    }


    /**
     * For cursor kontrollene/keys
     *
     */
    class TAdapter extends KeyAdapter {
                           // En adapter klasse for mottak av keyboard events

        @Override
        public void keyPressed(KeyEvent e) {
            int key = e.getKeyCode();

            if (inGame) {                               // Om ingame = true, så kan pacman kontrolleres med cursor keys: left, right, up, down
                                                        // Variablene req_dx og req_dy, blir brukt til å kontrollere x og y posisjoner
                                                        // dx-1 = LEFT
                                                        // dx 1 = RIGHT
                                                        // dy-1 = UP
                                                        // dy 1 = DOWN
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) { // Om timer er gående, og ESC blir trykket
                                                                             // Så skal spillet slutte/stoppe
                    inGame = false;
                } 
            } else {
                if (key == KeyEvent.VK_SPACE) { // Om SPACE blir trykket, spillet starter
                    inGame = true;
                    initGame();
                }
            }
        }
}

	
    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }
		
	}

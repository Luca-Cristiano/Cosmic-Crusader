/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import javax.swing.JPanel;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author Bryan Guan
 */
public class GamePanel extends JPanel implements Runnable, KeyListener {

    public static int w = 1000;
    public static int h = 1050;

    private Thread t;
    private boolean isRunning;

    private BufferedImage image;
    private Graphics2D g;

    //Objects
    public static Player player;
    public static Barrier barrier;
    public static Enemy enemy;
    public static int score = 0;
    private boolean direction = false;

    //ArrayLists
    public static ArrayList<Barrier> barrierList;
    public static ArrayList<Bullet> bullets;
    public static ArrayList<Enemy> enemyList;
    public static ArrayList<EnemyBullet> enemyBullet;

    public static int enemySpeed = 1;

    //FPS
    private int FPS = 60;

    private int waveNum = 1;
    private int countShoot = 0;

    private int randEnemy;

    private boolean isFiring;
    private long firingTimer;
    private long firingDelay;

    private static int[] top5Scores = new int[5];
    private double lowY = 0;
    private int readCounter = 0;
    private ImageIcon background = new ImageIcon("images/menuscreen.jpg");
    private ImageIcon mainMenu = new ImageIcon("images/menu.jpg");

    //Constructor
    public GamePanel() {
        super();
        setPreferredSize(new Dimension(w, h));
        setFocusable(true);
        requestFocus();
    }

    public void addNotify() {
        super.addNotify();
        if (t == null) {
            t = new Thread(this);
            t.start();
        }
        addKeyListener(this);
    }

    public static void writeToFile() { //writes the highest 5 scores to a file
        PrintWriter pw;

        try {
            pw = new PrintWriter("High_Score.txt");

            for (int i = 0; i < top5Scores.length; i++) {
                pw.println(top5Scores[i]);
            }

            pw.close();
        } catch (FileNotFoundException ex) {
            System.out.println("The file was not found.");
        }
    }

    public static void readFile() { //Reads the highest 5 scores text file 
        FileReader fr;
        Scanner sc;
        String fileRead = "";

        try {
            fr = new FileReader("High_Score.txt");
            sc = new Scanner(fr);
            int counter = 0;

            while (sc.hasNextLine() && counter < 5) {
                top5Scores[counter] = sc.nextInt();
                counter++;
            }

            System.out.println(fileRead);

            fr.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to locate file.");
        } catch (IOException e) {
            System.out.println("Unable to perform task.");
        }
    }

    @Override
    public void run() { //Method that runs the entire program
        image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        g = (Graphics2D) image.getGraphics();

        long startTime;
        long elapsedTimeMillis;
        long waitTime;
        long totalTime = 0;

        int frameCount = 0;
        int maxFrameCount = 60;

        long targetTime = 1000 / FPS;

        countShoot = 0;

        int enemyType = 0;

        isRunning = true;

        player = new Player();
        bullets = new ArrayList<>();
        barrierList = new ArrayList<>();
        enemyList = new ArrayList<>();
        enemyBullet = new ArrayList<>();

        readCounter++;

        if (readCounter == 1) {
            readFile();
        }
        
        if (waveNum == 1) {
            JOptionPane.showMessageDialog(null, "Cosmic Crusader\n By: Luca, Bryan, Kristian\n\nINSTRUCTIONS:\n\n- Use left/right arrow keys to move\n- Press spacebar to shoot\n- The enemies gain speed every wave"
                    + "\n- PLAY WITH SOUND\n\n\n\n\n");
        }
        for (int row = 0; row < 3; row++) { //Adds three segements of barriers and 3 columns equally spaced
            for (int column = 0; column < 3; column++) {
                barrier = new Barrier(145 + (column * 285), 800 - (row * 10), 140, 10);
                barrierList.add(barrier); //Adds a barrier object to the arraylist
            }
        }

        for (int row = 0; row < 8; row++) {
            for (int column = 0; column < 5; column++) { //Set each column equal to a separate enemy type with a different score 
                if (column == 0) {
                    enemyType = 3;
                } else if (column == 1 || column == 2) {
                    enemyType = 2;
                } else {
                    enemyType = 1;
                }

                enemy = new Enemy((row * 50) + (row * 20), 30 + (column * 50) + (column * 10), enemyType, row, false, enemySpeed); //equally spaces the enemies based off of row spacing and column spacing
                enemyList.add(enemy); //Adds enemy object to enemy arraylist

            }
        }

        //Game loop
        while (isRunning) { //while the game is running update the game, render the game, and draws the game
            startTime = System.nanoTime();
            try {
                gameUpdate();
            } catch (InterruptedException ex) {

            }

            gameRender();
            gameDraw();

            elapsedTimeMillis = (System.nanoTime() - startTime) / 1000000; //amount of time in milliseconds elapsed
            waitTime = targetTime - elapsedTimeMillis;

            try {
                Thread.sleep(waitTime);
            } catch (Exception e) {

            }

            totalTime += System.nanoTime() - startTime;
            frameCount++;

            if (frameCount == maxFrameCount) { //If the frame count equals the specified max frame count, reset frame count and total time e
                frameCount = 0;
                totalTime = 0;
            }

        }
    }

    private void gameUpdate() throws InterruptedException { //Updates the state of the game 

        player.update();  //Updates the player movement 

        //Enemy firing 
        randEnemy = (int) (Math.random() * (enemyList.size())); //picks a random index of an enemy in the ArrayList
        double eX = enemyList.get(randEnemy).getx(); //gets the x value of the enemy
        double eY = enemyList.get(randEnemy).gety(); //gets the y value of the enemy

        countShoot += enemySpeed;

        if (countShoot % 320 == 0) { //enemies shoot for every 320 pixels moved in the x-direction
            enemyList.get(randEnemy).shoot();
        }

        //Updates the enemy bullet, removes the bullet if the bullet is past the boundary     
        for (int i = 0; i < enemyBullet.size(); i++) {
            boolean removeEnemyB = enemyBullet.get(i).update();
            if (removeEnemyB) {
                enemyBullet.remove(i);
                i--;
            }
        }

        if (direction == false) {
            double highest = enemyList.get(0).getx();
            for (int i = 0; i < enemyList.size(); i++) {
                enemyList.get(i).getx();
                if (enemyList.get(i).getx() > highest) {
                    highest = enemyList.get(i).getx();
                }
            }
            if (highest >= 940) {
                int counter = 0;
                while (counter < 15) {
                    counter++;
                    for (int i = 0; i < enemyList.size(); i++) {
                        enemyList.get(i).addY();
                    }
                    direction = true;
                }
            } else {
                for (int i = 0; i < enemyList.size(); i++) {
                    enemyList.get(i).addX();
                }
            }//else if greater then you do down and if else move sideways
        } else {
            double lowest = enemyList.get(0).getx();
            for (int i = 0; i < enemyList.size(); i++) {
                enemyList.get(i).getx();
                if (enemyList.get(i).getx() < lowest) {
                    lowest = enemyList.get(i).getx();
                }
            }
            if (lowest <= 0) {
                int counter = 0;
                while (counter < 15) {
                    counter++;
                    for (int i = 0; i < enemyList.size(); i++) {
                        enemyList.get(i).addY();
                    }
                    direction = false;
                }
            } else {
                for (int i = 0; i < enemyList.size(); i++) {
                    enemyList.get(i).subX();
                }
            }
        }

        for (int i = 0; i < bullets.size(); i++) {
            boolean remove = bullets.get(i).update();

            if (remove) {
                bullets.remove(i);
                i--;
            }
        }

        for (int i = 0; i < barrierList.size(); i++) {
            barrierList.get(i).draw(g);
        }

        //bullet-barrier collision
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            double bulletX = b.getx();
            double bulletY = b.gety();

            for (int j = 0; j < barrierList.size(); j++) {
                Barrier s = barrierList.get(j);
                double barrierX = s.getx();
                double barrierY = s.gety();

                if (bulletX + 48 >= barrierX && bulletX - 42 <= barrierX + 56 && bulletY <= barrierY) {
                    s.hit();
                    bullets.remove(i);
                    i--;
                    break;
                }
            }
        }

        //Bullet enemy collision
        for (int i = 0; i < bullets.size(); i++) {
            Bullet b = bullets.get(i);
            double bulletX = b.getx();
            double bulletY = b.gety();

            for (int j = 0; j < enemyList.size(); j++) {
                Enemy e = enemyList.get(j);
                double enemyX = e.getx();
                double enemyY = e.gety();

                if (bulletX > (enemyX - 48) && bulletX < (enemyX + 8) && bulletY <= enemyY + 40 && bulletY + 40 >= enemyY) {

                    e.hit();
                    bullets.remove(i);
                    InputStream sound;

                    try {
                        sound = new FileInputStream(new File("src\\sounds\\playerHit.wav"));
                        AudioStream audio;
                        audio = new AudioStream(sound);
                        AudioPlayer.player.start(audio);
                    } catch (Exception e2) {
                        JOptionPane.showMessageDialog(null, e2.getLocalizedMessage());
                    }
                    if (e.getEnemyType() == 3) {
                        score += 40;
                    } else if (e.getEnemyType() == 2) {
                        score += 20;

                    } else {
                        score += 10;
                    }

                    i--;
                    break;
                }
            }
        }
        //enemy bullet collision with player
        for (int i = 0; i < enemyBullet.size(); i++) {
            EnemyBullet eB = enemyBullet.get(i);
            double eBX = eB.getx();
            double eBY = eB.gety();

            double playerX = player.getx();
            double playerY = player.gety();

            if (eBX > playerX - 26 && eBX < playerX + 72 && eBY > playerY + 46 && eBY < playerY + 78 || eBX > playerX && eBX + 22 < playerX + 68 && eBY > playerY - 22 && eBY < playerY + 78) {

                player.hit();
                enemyBullet.remove(i);
                i--;

                InputStream sound;

                try {
                    sound = new FileInputStream(new File("src\\sounds\\explosion2.wav"));
                    AudioStream audio;
                    audio = new AudioStream(sound);
                    AudioPlayer.player.start(audio);
                } catch (Exception e2) {
                    JOptionPane.showMessageDialog(null, e2.getLocalizedMessage());
                }
                System.out.println("hit");

                break;
            }

        }

        //enemy bullet collision with barrier
        for (int i = 0; i < enemyBullet.size(); i++) {
            EnemyBullet e = enemyBullet.get(i);
            double eBX = e.getx();
            double eBY = e.gety();

            for (int j = 0; j < barrierList.size(); j++) {
                Barrier s = barrierList.get(j);
                double barrierX = s.getx();
                double barrierY = s.gety();

                if (eBX + 28 >= barrierX && eBX - 22 <= barrierX + 98 && eBY >= barrierY - 10) {
                    s.hit();
                    enemyBullet.remove(i);
                    i--;
                    break;
                }
            }
        }

        if (player.isDestroyed()) {
            gameOver();
        }

        //check broken barriers
        for (int i = 0; i < barrierList.size(); i++) {
            if (barrierList.get(i).isDestroyed()) {
                barrierList.remove(i);
                i--;
            }
        }

        //check the if enemies are shot
        for (int i = 0; i < enemyList.size(); i++) {
            if (enemyList.get(i).isDestroyed()) {
                enemyList.remove(i);
                i--;
            }
        }

        //new wave
        if (enemyList.isEmpty()) {
            nextWave();
        }

        lowY = enemyList.get(0).gety();

        for (int i = 0; i < enemyList.size(); i++) {
            if (enemyList.get(i).gety() > lowY) {
                lowY = enemyList.get(i).gety();
            }
        }

        
        //If enemies reach a certain part it's game over
        if (lowY > 750) {
            gameOver();

        }

    }

    //Uses a sorting method to sort the scores
    private void sortScore() {
        for (int i = 0; i < top5Scores.length; i++) {
            for (int j = i + 1; j < top5Scores.length; j++) {
                if ((top5Scores[i] < top5Scores[j]) && (i != j)) {
                    int temp = top5Scores[j];
                    top5Scores[j] = top5Scores[i];
                    top5Scores[i] = temp;
                }
            }
        }
    }

    //Updates the new scores with another if it is high enough
    private void checkScore() {
        if (score > top5Scores[4]) {
            top5Scores[4] = score;
        }
    }

    //What happens when the player dies or the enemy reaches a certain point
    private void gameOver() throws InterruptedException {
        InputStream sound;

        try {
            sound = new FileInputStream(new File("src\\sounds\\background.wav"));
            AudioStream audio;
            audio = new AudioStream(sound);
            AudioPlayer.player.start(audio);
        } catch (Exception e2) {
            JOptionPane.showMessageDialog(null, e2.getLocalizedMessage());
        }
        gameDraw();
        Scanner sc = new Scanner(System.in);
        g.setColor(Color.WHITE);
        Font font2 = new Font("Arial", Font.BOLD, 100);
        g.setFont(font2);
        String gameOver = "G A M E  O V E R";
        g.drawString(gameOver, w / 2, h / 2);

        sortScore();
        checkScore();
        sortScore();

        for (int i = 0; i < top5Scores.length; i++) {
            System.out.println(top5Scores[i]);
        }
        writeToFile();

        //asks user to play again
        int choice = JOptionPane.showConfirmDialog(null, "Would you like to play again?", "You lost the game with " + score + " points", 0);

        if (choice == 0) {
            resetGame();
        } else if (choice == 1) {
            System.exit(0);
        }

    }

    //Draws most of the game elements
    private void gameRender() {

        background.paintIcon(this, g, 0, 0);

        player.draw(g);

        for (int i = 0; i < enemyBullet.size(); i++) {
            enemyBullet.get(i).draw(g);
        }

        for (int i = 0; i < bullets.size(); i++) {
            bullets.get(i).draw(g);
        }

        for (int i = 0; i < barrierList.size(); i++) {
            barrierList.get(i).draw(g);
        }

        for (int i = 0; i < enemyList.size(); i++) {
            enemyList.get(i).draw(g);
        }

        Font font = new Font("Arial", Font.BOLD, 20);

        //draws the text on the top of the screen
        g.setColor(Color.WHITE);
        g.setFont(font);

        g.drawString("Wave: " + waveNum, w - 120, 20);

        g.drawString("Lives:", w - 960, 20);

        g.setColor(Color.ORANGE);
        g.drawString("Score: " + score, (w / 2) - 250, 20);
        g.drawString("High Score: " + top5Scores[0], w - 400, 20);

        g.setColor(Color.CYAN);
        g.drawString(" " + player.getLives(), w - 900, 20);

    }
    
    private void gameDraw() {
        Graphics g2 = this.getGraphics();
        g2.drawImage(image, 0, 0, null);
        g2.dispose();
    }

    //resets certain methods, arraylists and variables in order to reset the wave
    private void nextWave() {
        barrierList.clear();
        enemyList.clear();
        enemySpeed++;
        enemy.setSpeed(enemySpeed);
        waveNum += 1;
        run();
    }

    //Resets the game after the game is lost
    private void resetGame() {
        score = 0;
        barrierList.clear();
        enemyList.clear();
        enemySpeed = 1;
        enemy.setSpeed(enemySpeed);
        waveNum = 1;
        enemyBullet.clear();
        bullets.clear();

        run();
    }

    @Override
    public void keyTyped(KeyEvent ke) {

    }

    @Override
    public void keyPressed(KeyEvent ke) {
        int code = ke.getKeyCode();

        if (code == KeyEvent.VK_LEFT) {
            player.setLeft(true);
        }

        if (code == KeyEvent.VK_RIGHT) {
            player.setRight(true);
        }

        if (code == KeyEvent.VK_SPACE) {
            player.setFiring(true);
        }
    }

    @Override
    public void keyReleased(KeyEvent ke) {
        int code = ke.getKeyCode();

        if (code == KeyEvent.VK_LEFT) {
            player.setLeft(false);
        }

        if (code == KeyEvent.VK_RIGHT) {
            player.setRight(false);
        }

        if (code == KeyEvent.VK_SPACE) {
            player.setFiring(false);
        }
    }

}

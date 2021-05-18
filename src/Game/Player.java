/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;

/**
 *
 * @author Bryan Guan
 */
public class Player {

    private int x;
    private int y;
    private int r;

    private int dx;
    private int speed;
    private int lives;

    private boolean left;
    private boolean right;

    private Color color1;

    private boolean isFiring;
    private long firingTimer;
    private long firingDelay;

    private boolean destroyed;
    private boolean isHit;

    ImageIcon player = new ImageIcon("images/ship.PNG");
    Image ship = player.getImage(); // transform it
    ImageIcon resizedPlayer = new ImageIcon(getScaledImage(ship, 90, 90));

    public Player() {
        x = (GamePanel.w / 2) - 45;
        y = 850;
        r = 20;

        dx = 0;

        speed = 6;

        lives = 3;

        color1 = Color.CYAN;

        isFiring = false;
        firingTimer = System.nanoTime();
        firingDelay = 800;

        destroyed = false;

    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void hit() {
        
        lives--;
        if (lives <= 0) {
            destroyed = true;
        }
        
    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public void setLeft(boolean b) {
        left = b;
    }

    public void setRight(boolean b) {
        right = b;
    }

    public void setFiring(boolean b) {
        isFiring = b;
    }

    public int getLives() {
        return lives;
    }

    public void update() {
        if (left) {
            dx = -speed;
        }
        if (right) {
            dx = speed;
        }

        x += dx;

        if (x > GamePanel.w - 90) {
            x = GamePanel.w - 90;
        }

        if (x < 0) {
            x = 0;
        }

        dx = 0;

        if (isFiring) {
            long elapsed = (System.nanoTime() - firingTimer) / 1000000;
            if (elapsed > firingDelay) {
                GamePanel.bullets.add(new Bullet(270, x, y));
                firingTimer = System.nanoTime();
            }
        }
    }

    public void draw(Graphics2D g) {
        /*g.setColor(color1);
        g.fillRect(x, y, 50, 50);
        
        g.setStroke(new BasicStroke(3));
        g.setColor(color1.darker());
        g.drawRect(x, y, 50, 50);
        g.setStroke(new BasicStroke(2));
        
        g.setColor(color1.darker());
        g.fillRect(x + 22, y - 10, 6, 10);*/
        

        resizedPlayer.paintIcon(null, g, x, y);
    }

    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }

}

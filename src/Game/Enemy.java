package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.util.Random;
import javax.swing.ImageIcon;

class Enemy {

    public static int score;
    int x, y;
    private Color color1 = Color.RED;
    private Color color2 = Color.YELLOW;
    private Color color3 = Color.WHITE;
    private int width = 50;
    private int height = 50;
    private int enemyType = 0;
    private boolean destroyed;
    private int speed;
    private boolean direction = false;
    private int counter = 0;
    private int row = 0;
    private int xLimitRight = 0;
    private int xLimitLeft = 0;
    private int highRX = 0;
    private int lowLX = 0;

    private boolean isFiring;

    //Declaring the images of each enemy
    ImageIcon e1 = new ImageIcon("images/enemy.PNG");
    Image enemy_1 = e1.getImage(); // transform it 
    ImageIcon enemy1 = new ImageIcon(getScaledImage(enemy_1, 50, 50));

    ImageIcon e2 = new ImageIcon("images/enemy2.PNG");
    Image enemy_2 = e2.getImage(); // transform it 
    ImageIcon enemy2 = new ImageIcon(getScaledImage(enemy_2, 50, 50));

    ImageIcon e3 = new ImageIcon("images/white.PNG");
    Image enemy_3 = e3.getImage(); // transform it 
    ImageIcon enemy3 = new ImageIcon(getScaledImage(enemy_3, 50, 50));

    //Enemy constructor
    public Enemy(int xPos, int yPos, int eType, int r, boolean destoyed, int s) {
        this.x = xPos;
        this.y = yPos;
        enemyType = eType;
        row = r;
        destroyed = false;
        this.speed = s;
        isFiring = false;

    }

    public void setSpeed(int eS) {
        this.speed = eS;
    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public int size() {
        return width;
    }

    public void addY() {
        y++;
    }

    public void addX() {
        x += speed;
    }

    public void subX() {
        x -= speed;
    }

    public void setFiring(boolean b) {
        isFiring = b;
    }

    public int getEnemyType() {
        return enemyType;
    }

    public boolean getDirection() {
        return direction;
    }

    public boolean isDestroyed() {
        return destroyed;
    }

    public void hit() {
        destroyed = true;
    }

    //Based on the object's enemyType it assigns specific pictures and score values to each enemy
    public void draw(Graphics2D g) {
        switch (enemyType) {
            case 3:
                /*g.setColor(color1);
                g.fillRect(this.x, this.y, this.width, this.height);*/
                enemy1.paintIcon(null, g, this.x, this.y);
                break;
            case 2:
                /*g.setColor(color2);
                g.fillRect(this.x, this.y, this.width, this.height);*/
                enemy2.paintIcon(null, g, this.x, this.y);
                break;
            case 1:
                /*g.setColor(color3);
                g.fillRect(this.x, this.y, this.width, this.height);*/
                enemy3.paintIcon(null, g, this.x, this.y);
                break;
            default:
                break;
        }
    }

    //Adds a bullet when the enemy shoots
    public void shoot() {
            GamePanel.enemyBullet.add(new EnemyBullet(270, x, y));
    }

    //Scales the image to the appropriate size
    private Image getScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();

        return resizedImg;
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.JFrame;

/**
 *
 * @author Guanb6314
 */
public class EnemyBullet extends JFrame {

    public double x;
    public double y;

    private double dx;
    private double dy;
    private double rad;
    private double speed;

    private Color color;

    public EnemyBullet(double angle, double x, double y) {

        this.x = x;
        this.y = y;

        rad = Math.toRadians(angle);
        speed = 8;
        dx = Math.cos(rad) * speed;
        dy = Math.sin(rad) * speed;

        color = Color.RED;
    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public boolean update() {
        y -= dy;
        x += dx;

        return x > GamePanel.w || y > 1050;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int) (x + 22), (int) (y), 6, 20);
    }

}

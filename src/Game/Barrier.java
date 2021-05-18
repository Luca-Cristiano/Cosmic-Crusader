/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 * @author Bryan Guan
 */
public class Barrier {

    private int x;
    private int y;
    private Color color1;
    private Color color2;
    private Color color3;
    private int width;
    private int height;
    private int health;
    private boolean destroyed;

    //Constructor
    public Barrier(int xPos, int yPos, int width, int height) {
        this.x = xPos;
        this.y = yPos;
        color1 = Color.RED;
        color2 = Color.YELLOW;
        color3 = Color.WHITE;
        this.width = width;
        this.height = height;
        health = 3;
        destroyed = false;

    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public boolean isDestroyed() { //Returns true that the barrier is destroyed
        return destroyed;
    }
    
    public void hit() { //If the barrier is hit, health will be deducted. Each barrier has 3 segements with 3 health.
        health--; 
        if (health <= 0) { //If the health is less than or equal to 0, return true that the object is destroyed
            destroyed = true;
        }
    }


    public void draw(Graphics2D g) { //draws the barriers and indicators of barrier health
        switch (health) {
            case 3:
                g.setColor(color1);
                g.fillRect(this.x, this.y, this.width, this.height);
                break;
            case 2:
                g.setColor(color2);
                g.fillRect(this.x, this.y, this.width, this.height);
                break;
            case 1:
                g.setColor(color3);
                g.fillRect(this.x, this.y, this.width, this.height);
                break;
            default:
                break;
        }

    }

}

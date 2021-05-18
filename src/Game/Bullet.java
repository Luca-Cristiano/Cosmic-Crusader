/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author Guanb6314
 */
public class Bullet extends JFrame {

    public double x;
    public double y;

    private double dx;
    private double dy;
    private double rad;
    private double speed;

    private Color color;

    public Bullet(double angle, double x, double y) {
        this.x = x;
        this.y = y;

        rad = Math.toRadians(angle);  //In order for bullet to shoot up, the angle at which it shoots at must be considered
        speed = 12;
        dx = Math.cos(rad) * speed;  //multiplies the angle at which the bullet is shot at with the speed for x displacement and y displacement
        dy = Math.sin(rad) * speed;

        color = Color.GREEN;
        
         InputStream sound;
            try{
                sound = new FileInputStream(new File("src\\sounds\\laser.wav"));
                AudioStream audio;
                audio = new AudioStream(sound);
                AudioPlayer.player.start(audio);
            }
            catch(Exception e){
                JOptionPane.showMessageDialog(null,e.getLocalizedMessage());
            }
    }

    public boolean update() { //updates the movement of the bullet when shot
        x += dx;
        y += dy;

        return x > GamePanel.w || y < 0; //If the bullet travels past the boundaries of the screen, the bullet will be removed
    }

    public double getx() {
        return x;
    }

    public double gety() {
        return y;
    }

    public void draw(Graphics2D g) {
        g.setColor(color);
        g.fillRect((int) (x + 42), (int) (y), 6, 20);
    }

}

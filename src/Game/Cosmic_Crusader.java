/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Game;

/**
 *
 * @author Guanb6314
 */
import java.awt.Color;
import javax.swing.JFrame;

public class Cosmic_Crusader {

    private Thread t;

    /**
     * @param args
     */
    public static void main(String[] args) {
        JFrame f = new JFrame();    //Instantiates new JFrame object
        f.setContentPane(new GamePanel());      
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.pack(); 
        f.setLocationRelativeTo(null);
        f.setResizable(false);
        f.setVisible(true);
        f.setTitle("Cosmic Crusader");

    }

}

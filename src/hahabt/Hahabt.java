/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hahabt;

import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.bluetooth.*;
/**
 *
 * @author 102098
 */
public class Hahabt {
   
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         BT123 myBt = new BT123();
         
        // TODO code application logic here
        //System.out.println("[MAIN]");
        
        /* Init BT Device */        
        try {
            myBt.btInit();
            myBt.btConn();
        } catch (BluetoothStateException ex) {
            Logger.getLogger(Hahabt.class.getName()).log(Level.SEVERE, null, ex);
        }
    }   
}


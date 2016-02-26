/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dbf.parser.pkg2;

import java.io.IOException;
import java.text.ParseException;


/**
 *
 * @author ziron_000
 */
public class DbfParser2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        handler handle = new handler();
        
//        try {
//            handle.readDBF();
//        }catch(ParseException e) {
//            e.printStackTrace();
//        }catch(IOException e) {
//            e.printStackTrace();
//        }
            handle.toJsonCouch();
        
        
    }
    
    
    
}

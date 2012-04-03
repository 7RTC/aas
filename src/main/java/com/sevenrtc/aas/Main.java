package com.sevenrtc.aas;

import com.sevenrtc.aas.db.DAO;
import com.sevenrtc.aas.shared.Contas;
import com.jgoodies.looks.plastic.PlasticXPLookAndFeel;
import com.sevenrtc.aas.ui.PrincipalFrame;
import java.awt.EventQueue;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 * Hello world!
 *
 */
public class Main {

    public static void main(String[] args) {
        // script de inicializacao
        TimeZone.setDefault(TimeZone.getTimeZone("Etc/GMT+3"));
        Locale.setDefault(new Locale("pt", "BR"));
        DAO.load();
        Contas.updateCategorias();
        
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(new PlasticXPLookAndFeel());
                    new PrincipalFrame().setVisible(true);
                } catch (UnsupportedLookAndFeelException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}

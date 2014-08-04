/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
/**
 *
 * @author Fabiola
 */
public class Simulator {
    private static final int DELAY = 1;
    private static final int PERIOD = 1;
    private static PriorityQueue<Process> _processes;
    private static final int INIT_CAPACITY = 20;
    private static final ScheduledExecutorService scheduler = 
        Executors.newScheduledThreadPool(1);
    private static Machine _m;
    private static BinaryBuddyAllocator _buddy;
    private static final MainFrame _frame = new MainFrame();
    
    public static void startFrame() {
        try {

            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    _frame.setVisible(true);
                }
            });


            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }

        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        //</editor-fold>    
    }

    public static void main(String args[]) {
        try {
            startFrame();
            
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            MyHandler handler = new MyHandler();
            saxParser.parse(new File("/Users/Fabiola/Desktop/process.xml"),
                    handler);

            _buddy = new BinaryBuddyAllocator(50, 11, _frame);
            _m = new Machine(DELAY, PERIOD, handler.processes(), _buddy, _frame);

            //   new Kernel(m);
            //   new Cpu(m);

            ScheduledFuture<?> kernelHandle =
                    scheduler.scheduleAtFixedRate(new Kernel(_m), DELAY, PERIOD, SECONDS);
            ScheduledFuture<?> cpuHandler =
                    scheduler.scheduleAtFixedRate(new Cpu(_m), DELAY, PERIOD, SECONDS);
            
            while (true);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}

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

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import static java.util.concurrent.TimeUnit.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import org.xml.sax.SAXException;
/**
 *
 * @author Fabiola
 */
public class Simulator {
    private static final int DELAY = 1;
    private static final int PERIOD = 1;
    public static final int MAXORDER = 11;
    
    private static final int INIT_CAPACITY = 20;
    private static final ScheduledExecutorService scheduler = 
        Executors.newScheduledThreadPool(1);
    
    private static final MainFrame _frame = new MainFrame();
    
    protected static PriorityQueue<Process> _processes;
    protected static Ready _ready = new Ready();
    protected static BinaryBuddyAllocator _buddySystem;
    protected static Clock _timer = new Clock();
    //protected static List<Process> _new = new ArrayList<Process>();
    protected static New _new = new New();

    public static void printQueues() {
        _frame.setTimer(String.valueOf(_timer.time() - 1));
        List<Process> newQueueList = _new.clone();
        Process[] newQueue = newQueueList.toArray(new Process[0]);
        System.out.println(newQueue);
        _frame.setNewQueue(newQueue);
        Process[] readyQueue = _ready.toArray();
        _frame.setReadyQueue(readyQueue);
    }
    
    public static void printStatistics() {
        _buddySystem.updateStatistics(_frame);
    }
    
    public static void printMemory() {
        _buddySystem.updateFrameParameters(_frame);
    }

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

    @SuppressWarnings("empty-statement")
    public static void main(String args[]) {
        try {
            startFrame();
            
            SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxParserFactory.newSAXParser();
            MyHandler handler = new MyHandler();
            saxParser.parse(new File("/Users/Fabiola/Desktop/process.xml"),
                    handler);

            _processes = handler.processes();
            _buddySystem = new BinaryBuddyAllocator(50, 11, _frame);

            ScheduledFuture<?> kernelHandle =
                    scheduler.scheduleAtFixedRate(new Kernel(), DELAY, PERIOD, SECONDS);
            ScheduledFuture<?> cpuHandler =
                    scheduler.scheduleAtFixedRate(new Cpu(), DELAY, PERIOD, SECONDS);
            
            while (true);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }
}

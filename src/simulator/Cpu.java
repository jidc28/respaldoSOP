/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Fabiola
 */
public class Cpu implements Runnable {

    Cpu() {
    }
    List<AccessTuple> accessing = new ArrayList<AccessTuple>();

    public void manageReady() {
        System.out.println("Entrando");
        List<Process> modified = new ArrayList<Process>();
        List<Process> readyCopy = Simulator._ready.getProcesses();
        System.out.println("readyCopy");
        for (Process p : readyCopy) {
            System.out.println("\tIterando> " + p);
            int timer = Simulator._timer.time();
            System.out.println("\tTimer> " + timer);
            int arrivalTime = p.arrivalTime();
            System.out.println("\tArrivalTime> " + arrivalTime);
            int offset = p.pageBlock().getPageNo();
            System.out.println("\tOffset> " + arrivalTime);
            
            Iterator<AccessTuple> it = p.pageAccess().iterator();
            System.out.println("\tIterador");
            while (it.hasNext()) {
                System.out.println("\tHas Next> ");
                AccessTuple tuple = it.next();
                int time = tuple.time;
                int page = tuple.page;
                
                if ((arrivalTime + time) != timer) {
                    System.out.println("\t Break> ");
                    break;
                }
                System.out.println("\tRemoving> ");
                it.remove();
                System.out.println("\tAssigning Removing> ");
                Simulator._buddySystem.pageAccess(page + offset);
                System.out.println("\tCheck> ");
            }
        }
        System.out.println("End ready coopy");

        for (Process p : modified) {
            Simulator._ready.updatePageAccess(p);
        }
    }

    public void removeTimedOut() {
        List<Process> toRemove = Simulator._ready.timeOut();
        for (Process p : toRemove) {
            int index = p.pageBlock().getPageNo();
            int order = p.pageBlock().getOrder();
            Simulator._buddySystem.freeMemory(index, order);
            Simulator._ready.remove(p);
        }
    }

        public void manageNew() {
        int size = Simulator._new.size();
        System.out.println("Despues del size");

        for (int i = 0; i < size; i++) {
            Process p = Simulator._new.pop();
            int memNeeded = p.memNeeded();
            int pid = p.pid();
            PageBlockInfo block = 
            Simulator._buddySystem.assignMemory(memNeeded, pid);
            p.setPageBlock(block);
            Simulator._ready.add(p);
            
            //Simulator._buddySystem.printMemoryStatus();
        }
        System.out.println("Despues del for");
    }
    
    public void run() {
        System.out.println("CPU> Start");
        Simulator.printQueues();
        Simulator.printStatistics();
        Simulator.printMemory();
        manageNew();
        manageReady();
        removeTimedOut();
    }
}
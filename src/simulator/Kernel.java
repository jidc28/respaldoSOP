/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import java.util.PriorityQueue;
import java.util.Iterator;

/**
 *
 * @author Fabiola
 */
public class Kernel implements Runnable {

    Kernel() {
    }

     public void newProcesses() {
        PriorityQueue<Process> auxiliaryQueue = Simulator._processes;
        Iterator<Process> it = Simulator._processes.iterator();
        while (it.hasNext()) {
            Process p = it.next();
            int time = Simulator._timer.time();
            if (time != p.arrivalTime()) {
                break;
            }
            Simulator._new.add(p);
            it.remove();
        }
        
    }

    public void manageNew() {
        int size = Simulator._new.size();

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
    }

    @Override
    public void run() {
        Simulator._timer.tick();
        System.out.println("Kernel> start " + Simulator._timer.time());

        this.newProcesses();

        //this.manageNew();
        System.out.println("Kernel> End Kernel");
        
    }
}

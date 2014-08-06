/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Fabiola
 */
public class Ready {
    private ArrayList<Process> _ready;
    public Ready() {
        this._ready = new ArrayList<Process>();
    }
    
    public synchronized int size() {
        return this._ready.size();
    }
    
    public synchronized void add(Process process) {
        this._ready.add(process);
    }
    
    public synchronized Process pop() {
        return this._ready.remove(0);
    }
    
    public synchronized List<Process> timeOut() {
        List<Process> toRemove = new ArrayList<Process>();
        for (Process p : this._ready) {
            int timeLeft = p.timeLeft();
            if (timeLeft - 1 < 0) {
                toRemove.add(p);
            } else {
                p.setTimeLeft(timeLeft - 1);
            }
        }
        return toRemove;
    }

    public synchronized void updatePageAccess(Process p) {
        int i = _ready.indexOf(p);
        Process process = _ready.get(i);
        
        p.setPageAccess(p.pageAccess());
    }

    public synchronized void remove(Process p) {
        this._ready.remove(p);
    }
    
    public synchronized Process[] toArray() {
        return this._ready.toArray(new Process[0]);
    }
    
    @SuppressWarnings("unchecked")
    public synchronized List<Process> getProcesses() {
        return (ArrayList<Process>) this._ready.clone();
        
    }
}

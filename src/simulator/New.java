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
public class New {
    private ArrayList<Process> _new;
    public New() {
        this._new = new ArrayList<Process>();
    }
    
    public synchronized int size() {
        return this._new.size();
    }
    
    public synchronized void add(Process process) {
        this._new.add(process);
    }
    
    public synchronized Process pop() {
        return this._new.remove(0);
    }
    
    public synchronized List<Process> timeOut() {
        List<Process> toRemove = new ArrayList<Process>();
        for (Process p : this._new) {
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
        int i = _new.indexOf(p);
        Process process = _new.get(i);
        
        p.setPageAccess(p.pageAccess());
    }

    public synchronized void remove(Process p) {
        this._new.remove(p);
    }
    
    public synchronized Process[] toArray() {
        return this._new.toArray(new Process[0]);
    }
    
    @Override
    public synchronized List<Process> clone() {
        return (List<Process>) this._new.clone();
    }
    
    @SuppressWarnings("unchecked")
    public synchronized List<Process> getProcesses() {
        return (ArrayList<Process>) this._new.clone();
        
    }
}
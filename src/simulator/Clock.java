/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

/**
 *
 * @author Fabiola
 */
public class Clock {
    private static int _timer = 0;
    
    public Clock() {
    }
    
    public synchronized int time() {
        return _timer;
    }
    
    public synchronized void tick() {
        _timer++;
    }
    
}

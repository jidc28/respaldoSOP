/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

/**
 *
 * @author Fabiola
 */
public class Kernel implements Runnable {
    Machine _m;

    Kernel(Machine m) {
        this._m = m;
        new Thread(this, "manageNew").start();
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
    }

    public void run() {
        try {
        _m.manageNew();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

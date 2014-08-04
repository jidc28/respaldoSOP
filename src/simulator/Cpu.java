/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

/**
 *
 * @author Fabiola
 */
public class Cpu implements Runnable {
    Machine _m;

    Cpu(Machine m) {
        this._m = m;
        new Thread(this, "newProcesses").start();
    }

    public void run() {
        try{
            _m.manageReady();
            _m.newProcesses();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
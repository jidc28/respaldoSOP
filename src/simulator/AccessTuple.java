
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

/**
 *
 * @author Fabiola
 */
public class AccessTuple { 
    public final int time;
    public final int page; 

    AccessTuple(int time, int page) { 
        this.time = time; 
        this.page = page; 
    } 

    public String toString() {
        return ("t:"+time+" - p:"+page);
    }
}
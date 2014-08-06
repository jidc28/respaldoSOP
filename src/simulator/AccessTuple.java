
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

    @Override
    public AccessTuple clone() {
        return (new AccessTuple(this.time, this.page));
    }
    
    @Override
    public String toString() {
        return ("t:"+time+" - p:"+page);
    }
}
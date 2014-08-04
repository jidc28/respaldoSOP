/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;
import java.util.Comparator;
/**
 *
 * @author Fabiola
 */
public class ProcessComparator implements Comparator<Process>
{
    @Override
    public int compare(Process p1, Process p2)
    {
        return (p1.arrivalTime() - p2.arrivalTime());
    }
}
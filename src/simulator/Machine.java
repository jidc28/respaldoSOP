/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import java.util.TimerTask;
import java.util.PriorityQueue;

import java.util.Timer;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 *
 * @author Fabiola
 */
public class Machine {
    private static final int INIT_CAPACITY = 20;

    private BinaryBuddyAllocator _buddySystem;
    private static List<Process> _new;
    private static List<Process> _ready;
    private static PriorityQueue<Process> _processes;
    private static int _timer = 0;

    private static int _newCount;
    private static boolean _ticking = false;

    private final Lock lock = new ReentrantLock();
    private final Condition _empty    = lock.newCondition();
    private final Condition _notEmpty = lock.newCondition();
    private final Condition _tick     = lock.newCondition();
    private final Condition _kernel   = lock.newCondition();
    
    private static MainFrame _frame;

    Machine(int delay, int period, PriorityQueue<Process> processes, 
            BinaryBuddyAllocator buddySystem, MainFrame frame) {
        _buddySystem = buddySystem;
        _processes = processes;

        Comparator<Process> comparator = new ProcessComparator();
        _new = new ArrayList<>();
        _ready = new ArrayList<>();

        _newCount = 0;
        _frame = frame;

        //Timer timer = new Timer();
        //timer.schedule(new MachineTimer(), delay, 3000);
    }
    
    public List<Process> getNewQueue() {
        return _new;
        
    }
    
    public void print() {
        System.out.println("Entro en el print");
        Process[] newQueue = _new.toArray(new Process[0]);
        _frame.setNewQueue(newQueue);
        Process[] readyQueue = _ready.toArray(new Process[0]);
        
        _frame.setTimer(String.valueOf(_timer - 1));
        _frame.setReadyQueue(readyQueue);
        
        _buddySystem.updateFrameParameters(_frame);
    }

    public void manageNew() throws InterruptedException {
        lock.lock();
        System.out.println("Kernel> start: " + _timer);

        try {

            PriorityQueue<Process> auxiliaryQueue = _processes;
            int size = _processes.size();
            for (int i=0; i < size; i++) {
                Process proc = _processes.poll();
                if (_timer != proc.arrivalTime()) {
                    _processes.add(proc);
                    break;
                }
                _new.add(proc);
                _newCount++;
            }
            _timer++;
            print();
            _kernel.signal();
        } finally {
            System.out.println("Kernel> Unlocking");
            lock.unlock();
        }

    }


    public void newProcesses() throws InterruptedException {
        System.out.println("CPU> Start " + _timer);
        if (_newCount == 0) {
            return;
        }

        lock.lock();
        try {
            int size = _new.size();

            for (int i=0; i < size; i++) {
                Process proc = _new.remove(0);
                _newCount--;
                PageBlockInfo block = _buddySystem.assignMemory(proc.memNeeded(), proc.pid());
                proc.setPageBlock(block);
                _ready.add(proc);
                _buddySystem.printMemoryStatus();
            }

        } finally {
            System.out.println("CPU> Unlocking");
            lock.unlock();
        }
    }

    public void manageReady() {
        int size = _ready.size();
        Iterator<Process> it = _ready.iterator();

        while (it.hasNext()) {
            Process proc = it.next();
            int timeLeft = proc.timeLeft();
            System.out.println("\n\tTime left: \n" + timeLeft);
            int arrivalTime = proc.arrivalTime();
            
            List<AccessTuple> access = proc.pageAccess();
            int accessSize  = access.size();           

            for (int i = 0; i < size; i++) {
                AccessTuple tuple = access.remove(0);
                int time = tuple.time;
                int page = tuple.page;

                if ((arrivalTime + time) != _timer) {
                    access.add(tuple);
                    break;
                }
                System.out.println("CPU> Page access init");
                int offset = proc.pageBlock().getPageNo();
                _buddySystem.pageAccess(page + offset);
                System.out.println("CPU> Page access end");

            }
            if ((timeLeft - 1) < 0) {
                System.out.println("Removing");
                int index = proc.pageBlock().getPageNo();
                int order = proc.pageBlock().getOrder();
                _buddySystem.freeMemory(index, order);
                it.remove();
            } else {
                proc.setTimeLeft(timeLeft - 1);
            }
        }
        //System.out.println("\nReady finish> \n" + _ready);
    }

    public class MachineTimer extends TimerTask {
        //private int _timer = 0;

        MachineTimer() { }

        public void actionPerformed() {
            //lock.lock();
            try {
                _timer++;
                _ticking = true;
                _tick.notify();
                System.out.println("Timer working" + _timer);
            } finally {
            //    lock.unlock();
            }
        }

        public void run() { 
            this.actionPerformed();
        } 
    }
}

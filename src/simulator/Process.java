/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;
import java.math.BigInteger;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 *
 * @author Fabiola
 */
public class Process {
        private int _pid;
        private int _arrivalTime;
        private int _timeLeft;
        private int _memNeeded;
        private List<AccessTuple> _pageAccess;
        private PageBlockInfo _pageBlock;

        Process () {}

        Process (int pid, int arrivalTime, int memNeeded, int timeLeft,
                List<AccessTuple> pageAccess, PageBlockInfo block) {
            _pid         = pid;
            _arrivalTime = arrivalTime;
            _timeLeft    = timeLeft;
            _memNeeded   = memNeeded;
            _pageAccess  = pageAccess;
            _pageBlock   = block;
        }
        Process (int pid, int arrivalTime, int memNeeded, int timeLeft,
                List<AccessTuple> pageAccess) {
            _pid         = pid;
            _arrivalTime = arrivalTime;
            _timeLeft    = timeLeft;
            _memNeeded   = memNeeded;
            _pageAccess  = pageAccess;
            _pageBlock       = new PageBlockInfo();
        }

        @Override
        public Process clone() {
            return new Process(this.pid(), this.arrivalTime(), this.timeLeft(),
                    this.memNeeded(), this.pageAccess(), this.pageBlock());
        }
        public int pid(){
            return _pid;
        }

        public void setPid(int pid) {
            _pid = pid;
        }

        public void setArrivalTime(int arrivalTime) {
            _arrivalTime = arrivalTime;
        }
        
        public void setTimeLeft(int timeLeft) {
            _timeLeft = timeLeft;
        }

        public void setMemNeeded(int memNeeded) {
            _memNeeded = memNeeded;
        }

        public void setPageAccess(List<AccessTuple> pageAccess) {
            _pageAccess = pageAccess;

        }

        public void setPageBlock(PageBlockInfo block) {
            _pageBlock = block;
        }

        public int timeLeft() {
            return _timeLeft;
        }
        public int arrivalTime() {
            return _arrivalTime;
        }

        public int memNeeded() {
            return _memNeeded;
        }
    
        public List<AccessTuple> pageAccess() {
            return _pageAccess;
        }

        public PageBlockInfo pageBlock() {
            return _pageBlock;
        }

    public String toString() {
        String result = _pageAccess.isEmpty() ? "pid: " + _pid + " | " + _timeLeft :
        "" + _pageAccess.get(0).time +
        " | " + _pageAccess.get(0).page;
        
        /*
         String result = "pid: " + _pid + "\n" +
         "Arrival time: " + _arrivalTime + "\n" +
                      "Total memory: " + _memNeeded + "\n" +
                      "Page access: " + _pageAccess;
                      */
            return result;
        }
    }
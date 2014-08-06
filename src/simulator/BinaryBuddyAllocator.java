
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
public class BinaryBuddyAllocator {

    private static int _freePages;
    private static int _assignedMemory;
    private static int _pageFault;
    private static int _freedMemory;
    private static int _maxOrder;
    private static FreeArea[] _freeArea;
    private static int[][] _mem;
    private static int[] _vMem;

    /**
     * Class constructor, specifying a minimum number of free pages and the size
     * order of the memory (that is: 2^order).
     */
    BinaryBuddyAllocator(int minFreePages, int maxOrder, MainFrame frame) {

        _maxOrder = maxOrder;
        _vMem = new int[1 << maxOrder];
        _mem = new int[1 << (maxOrder - 1)][2];
        _freeArea = new FreeArea[maxOrder + 1];

        _pageFault = 0;
        _freePages = 1 << maxOrder;
        _freedMemory = 0;
        _assignedMemory = 0;


        for (int i = 0; i < (1 << maxOrder); i++) {
            _vMem[i] = -1;
        }
        for (int i = 0; i < (1 << (maxOrder - 1)); i++) {
            _mem[i][0] = -1;
            _mem[i][1] = -1;
        }
        for (int i = 0; i < maxOrder; i++) {
            _freeArea[i] = new FreeArea();
        }
        _freeArea[maxOrder] = new FreeArea(0); // Initialize with a single block of 2^maxOrder pages.
    }

    public void updateFrameParameters(MainFrame _frame) {
        //_frame.setVMemory();
        countAddBlocks(_frame);
//        countRemBlocks(_frame);
        _frame.color();
    }
    
    public void updateStatistics(MainFrame _frame) {
        _frame.setPageFaults(String.valueOf(_pageFault));
        _frame.setFreeMemory(String.valueOf(_freePages));
        _frame.setAssignedMemory(String.valueOf(_assignedMemory));
        _frame.setFreedMemory(String.valueOf(_freedMemory));
        int usedMemory = (1 << _maxOrder) - _freePages;
        _frame.setUsedMemory(String.valueOf(usedMemory));
    }
    
    public int pageFault() {
        return _pageFault;
    }

    public int freePages() {
        return _freePages;
    }

    public void assignPhysicalPage(int vPage) {
        int min = _mem[0][1];
        int minIndex = -1;
        for (int i = 0; i < (1 << (_maxOrder - 1)); i++) {
            if (_mem[i][0] == -1) {
                _mem[i][0] = vPage;
                _vMem[vPage] = i;
                return;
            } else if (min < _mem[i][1]) {
                min = _mem[i][1];
                minIndex = i;
            }
        }
        // PAGEFAULT: There is no free memory. Replace LRU page.
        int vPageOld = _mem[minIndex][0];
        _vMem[vPageOld] = -1;
        // Assign new page.
        _mem[minIndex][0] = vPage;
        _vMem[vPage] = minIndex;
    }

    public void pageAccess(int vPage) {
        System.out.println("\n\tBuddy> Init page access: " + _vMem[vPage]);
        if (_vMem[vPage] == -1) {
            System.out.println("\tBuddy> dentro del if VPAGE: " + vPage + "\n");
            _pageFault++;
            assignPhysicalPage(vPage);
        }
        System.out.println("Buddy> despues del if");
        for (int i = 0; i < _mem.length; i++) {
            if (_mem[i][0] == vPage) {
                _mem[i][1] += 1;
                break;
            }
        }
        System.out.println("Buddy> Fin del metodo");
    }

    /**
     * This method looks for a free memory page block to assign to a process.
     *
     * @param reqPages Requested pages by the process.
     * @param pid Process pid
     */
    public PageBlockInfo assignMemory(int reqPages, int pid) {
        int index = 0, numPages = reqPages;

        while ((numPages >>= 1) > 0) {
            index += 1;
        }
        index = (1 << index) < reqPages ? index + 1 : index;

        int order = index;
        boolean foundBlock = false, needSmallerBlock = false;
        while (index >= 0
                && (!_freeArea[index].hasFreeBlock() || index != order)) {

            if (_freeArea[index].hasFreeBlock()) {
                if (needSmallerBlock) {
                    foundBlock = true;
                    break;
                }
                PageBlockInfo leftBlock = _freeArea[index].popFreeBlock();
                int rightPageId = leftBlock.getPageNo() + (1 << (index - 1));
                PageBlockInfo rightBlock = new PageBlockInfo(rightPageId);

                rightBlock.setOrder(index - 1);
                leftBlock.setOrder(index - 1);

                _freeArea[index - 1].addBlock(leftBlock);
                _freeArea[index - 1].addBlock(rightBlock);
                foundBlock = true;
            }
            if (index == _maxOrder && !foundBlock) {
                needSmallerBlock = true;
            }
            index = (foundBlock || needSmallerBlock) ? index - 1 : index + 1;
        }
        if (!foundBlock && needSmallerBlock) {
            return null;
        }
        PageBlockInfo block = _freeArea[index].getFreeBlock();
        _freeArea[index].markUsed(block.getPageNo(), index);
        block.markUsed();
        block.setOrder(order);

        _assignedMemory += (1 << index);
        _freePages -= (1 << index);
        return block;
    }

    /**
     * This method frees a memory block page.
     *
     * @param index the virtual page number
     * @param order the order of the size of the block (2^order)
     */
    public void freeMemory(int index, int order) {
        _freePages += (1 << order);
        _freedMemory += (1 << order);

        _freeArea[order].markFree(index, order);
        PageBlockInfo block = _freeArea[order].getBlock(index);
        block.markFree();
        for (int i = 0; i < (1 << order); i++) {
            if (_vMem[index] != -1) {
                int physIndex = _vMem[index];
                _mem[physIndex][0] = -1;
            }
            _vMem[index] = -1;
        }


        while (order < _maxOrder) {
            int mask = (index >> (1 + order));
            BigInteger map = _freeArea[order].map();

            if (map.testBit(mask) || _freeArea[order].freeBlocks() < 2) {
                break;
            }

            int buddyPageNo = index ^ (1 << order);

            PageBlockInfo buddy1 = _freeArea[order].getBlock(index);
            PageBlockInfo buddy2 = _freeArea[order].getBlock(buddyPageNo);
            _freeArea[order].removeBlock(buddy1);
            _freeArea[order].removeBlock(buddy2);

            int mergedPageNo = index < buddyPageNo ? index : buddyPageNo;
            PageBlockInfo mergedBlock = new PageBlockInfo(mergedPageNo, order + 1);
            _freeArea[order + 1].addBlock(mergedBlock);

            index = mergedPageNo;
            order++;
        }

    }

    public void countAddBlocks(MainFrame _frame) {
        System.out.println("\tSTART COUNT");
        for (int i = 0; i < _freeArea.length; i++) {
            System.out.println("\tBEFORE FOR " + i);
            for (PageBlockInfo block : _freeArea[i]._listHead) {
                System.out.println("\tBEFORE blockisfree " + block.isFree());
                if (!block.isFree()) {
                    System.out.println("\tBEFORE getPageNo");
                    int ini = block.getPageNo();
                    System.out.println("\tAFTER getPageNo");
                    int tam = (1 << i);
                    for (int j = 0; j < 64; j++) {
                        for (int k = 0; k < 32 && tam != 0; k++) {
                            if (ini != 0) {
                                ini--;
                            } else {
                                _frame.getVirtualMem().setValueAt("busy", j, k);
                                tam--;
                            }
                        }
                    }
                    System.out.println("\tAFTER INTERN FOR");
                } else if (block.isFree()) {
                    System.out.println("\tELSE!! - BEFORE getPageNo");
                    int ini = block.getPageNo();
                    System.out.println("\tELSE!! - AFTER getPageNo " + block.getPageNo());
                    int tam = (1 << i);
                    System.out.println("\tELSE!! - tam " + tam);
                    for (int j = 0; j < 64; j++) {
                        for (int k = 0; k < 32 && tam != 0; k++) {
                            if (ini != 0) {
                                ini--;
                            } else {
                                _frame.getVirtualMem().setValueAt("freed", j, k);
                                tam--;
                            }
                        }
                    }
                    System.out.println("\tELSE!! - END ");
                }
            }
        }
    }

    public void countRemBlocks(MainFrame _frame) {
        for (int i = 0; i < _freeArea.length; i++) {
            for (PageBlockInfo block : _freeArea[i]._listHead) {
                if (block.isFree()) {
                    int ini = block.getPageNo();
                    int tam = (1 << i);
                    for (int j = 0; j < 64; j++) {
                        for (int k = 0; k < 32 && tam != 0; k++) {
                            if (ini != 0) {
                                ini--;
                            } else {
                                _frame.getVirtualMem().setValueAt("freed", j, k);
                                tam--;
                            }
                        }
                    }
                }
            }
        }
    }

    public void countPagFoldBlocks(MainFrame _frame) {
        for (int i = 0; i < _freeArea.length; i++) {
            for (PageBlockInfo block : _freeArea[i]._listHead) {
                if (block.isFree()) {
                    int ini = block.getPageNo();
                    int tam = (1 << i);
                    for (int j = 0; j < 64; j++) {
                        for (int k = 0; k < 32 && tam != 0; k++) {
                            if (ini != 0) {
                                ini--;
                            } else {
                                _frame.getVirtualMem().setValueAt("freed", j, k);
                                tam--;
                            }
                        }
                    }
                }
            }
        }
    }

    public void printMemoryStatus() {
        String result = "=================\n";
        for (int i = 0; i < _maxOrder + 1; i++) {
            result += ("Order " + i + "> ");
            result += ("Free blocks: " + _freeArea[i].freeBlocks());
            if (_freeArea[i].size() == 0) {
                result += ("\n\tNo blocks.\n");
            } else {
                result += ("\n" + _freeArea[i].toString() + "\n");
            }
        }
        System.out.println(result);
    }

    private class FreeArea {

        public List<PageBlockInfo> _listHead;
        private BigInteger _map;
        private int _freeBlocks;

        FreeArea() {
            _listHead = new ArrayList<PageBlockInfo>();
            _map = new BigInteger("0");
            _freeBlocks = 0;
        }

        FreeArea(int pageId) {
            PageBlockInfo pageBlock = new PageBlockInfo(pageId);
            pageBlock.setOrder(_maxOrder);
            _listHead = new ArrayList<PageBlockInfo>();
            _listHead.add(pageBlock);
            _map = new BigInteger("0");
            _freeBlocks = 1;
        }

        public BigInteger map() {
            return _map;
        }

        public PageBlockInfo getBlock(int pageNo) {
            for (PageBlockInfo block : _listHead) {
                if (block.getPageNo() == pageNo) {
                    return block;
                }
            }
            return null;
        }

        public void flipBit(int index, int order) {
            _map = _map.flipBit(((index) >> (1 + order)));
        }

        public BigInteger markFree(int index, int order) {
            _freeBlocks++;
            flipBit(index, order);
            return _map;
        }

        public void markUsed(int index, int order) {
            _freeBlocks--;
            flipBit(index, order);
        }

        public boolean hasFreeBlock() {
            return (_freeBlocks > 0);
        }

        public PageBlockInfo getFreeBlock() {
            for (PageBlockInfo block : _listHead) {
                if (block.isFree()) {
                    return block;
                }
            }
            return null;
        }

        public PageBlockInfo popFreeBlock() {
            for (PageBlockInfo block : _listHead) {
                if (block.isFree()) {
                    removeBlock(block);
                    return block;
                }
            }
            return null;
        }

        public void addBlock(PageBlockInfo block) {
            _freeBlocks++;
            _listHead.add(block);
        }

        public void removeBlock(PageBlockInfo block) {
            _freeBlocks--;
            _listHead.remove(block);
        }

        public int size() {
            return _listHead.size();
        }

        public int freeBlocks() {
            return _freeBlocks;
        }

        public String toString() {
            String result = "\tBlocks:";
            for (PageBlockInfo block : _listHead) {
                result += block.toString();
            }
            return result;
        }
    }

    /*
     public static void main(String[] args) {
     BinaryBuddyAllocator buddy = new BinaryBuddyAllocator(50, 5);
     PageBlockInfo block0 = buddy.assignMemory(1, 0);
     buddy.printMemoryStatus();
     PageBlockInfo block1 = buddy.assignMemory(1, 1);
     buddy.printMemoryStatus();
     PageBlockInfo block2 = buddy.assignMemory(1, 2);
     buddy.printMemoryStatus();
     PageBlockInfo block3 = buddy.assignMemory(1, 3);
     buddy.printMemoryStatus();
     PageBlockInfo block4 = buddy.assignMemory(32, 3);
     buddy.printMemoryStatus();
     PageBlockInfo block5 = buddy.assignMemory(30, 3);
     buddy.printMemoryStatus();
     PageBlockInfo block6 = buddy.assignMemory(32, 3);
     buddy.printMemoryStatus();
     PageBlockInfo block7 = buddy.assignMemory(32, 3);
     buddy.printMemoryStatus();


     System.out.println("=====================");
     System.out.println("=====================");
     System.out.println("=====================");
     buddy.freeMemory(block0.getPageNo(), block0.getOrder());
     buddy.printMemoryStatus();
     System.out.println("=====================");
     System.out.println("=====================");
     System.out.println("=====================");
     buddy.freeMemory(block1.getPageNo(), block1.getOrder());
     buddy.printMemoryStatus();
     System.out.println("=====================");
     System.out.println("=====================");
     System.out.println("=====================");
     buddy.freeMemory(block3.getPageNo(), block3.getOrder());
     buddy.printMemoryStatus();
     System.out.println("=====================");
     System.out.println("=====================");
     System.out.println("=====================");
     buddy.freeMemory(block2.getPageNo(), block2.getOrder());
     System.out.println("=====================");
     buddy.printMemoryStatus();
     }
     */
}
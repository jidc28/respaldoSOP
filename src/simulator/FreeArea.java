package simulator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class FreeArea {

    private List<PageBlockInfo> _listHead;
    private BigInteger _map;
    private int _freeBlocks;
    private int _maxOrder;

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

    public List<PageBlockInfo> listHead() {
        return _listHead;
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

    @Override
    public String toString() {
        String result = "\tBlocks:";
        for (PageBlockInfo block : _listHead) {
            result += block.toString();
        }
        return result;
    }
}
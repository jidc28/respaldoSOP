/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;

/**
 *
 * @author Fabiola
 */
public class PageBlockInfo {

    private int _pageNo;
    private int _order = -1;
    private boolean _free;
    private boolean _pageFault;

    PageBlockInfo() {
    }

    PageBlockInfo(int pageNo) {
        _pageNo = pageNo;
        _order = -1;
        _free = true;
        _pageFault = false;
    }

    PageBlockInfo(int pageNo, int order) {
        _pageNo = pageNo;
        _order = order;
        _free = true;
        _pageFault = false;
    }

    public int getPageNo() {
        return _pageNo;
    }

    public int getOrder() {
        return _order;
    }

    public void setPageNo(int pageNo) {
        _pageNo = pageNo;
    }

    public void setOrder(int order) {
        _order = order;
    }

    public boolean isFree() {
        return _free;
    }

    public void markUsed() {
        _free = false;
    }

    public void markFree() {
        _free = true;
    }

    public boolean isPageFaulted() {
        return this._pageFault;
    }

    public void setPageFault(boolean pageFault) {
        this._pageFault = pageFault;
    }

    @Override
    public String toString() {
        return ("\n\t\tPage index> " + _pageNo
                + (_free ? " (Free Block)." : " (Used Block)."));
    }
}
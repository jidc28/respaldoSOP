/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package simulator;
import java.io.File;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Fabiola
 */
public class MyHandler extends DefaultHandler {
    private static final int INIT_CAPACITY = 20;
    private static PriorityQueue<Process> _new;
    private static PriorityQueue<Process> _processes;

    boolean aTime;
    boolean mNeeded;
    boolean pAccess;
    boolean tLeft;
    Process process;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes)
            throws SAXException {
        if (qName.equalsIgnoreCase("Process")) {
            String pid = attributes.getValue("pid");
            process = new Process();
            process.setPid(Integer.parseInt(pid));
            if (_processes == null) {
                Comparator<Process> comparator = new ProcessComparator();
                _processes = new PriorityQueue<Process>(INIT_CAPACITY, comparator);
            }
        } else if (qName.equalsIgnoreCase("arrived")) {
            aTime = true;
        } else if (qName.equalsIgnoreCase("memory")) {
            mNeeded = true;
        } else if (qName.equalsIgnoreCase("access")) {
            pAccess = true;
        } else if (qName.equalsIgnoreCase("time")) {
            tLeft = true;
        }        
    }

    @Override
    public void endElement(String uri, String localName, String qName) 
    throws SAXException {
        if (qName.equalsIgnoreCase("Process")) {
            _processes.add(process);
        }
    }

     @Override
    public void characters(char ch[], int start, int length) throws SAXException {
 
        if (aTime) {
            process.setArrivalTime(Integer.parseInt(new String(ch, start, length)));
            aTime = false;
        } else if (mNeeded) {
            process.setMemNeeded(Integer.parseInt(new String(ch, start, length)));
            mNeeded = false;
        } else if (tLeft) {
            process.setTimeLeft(Integer.parseInt(new String(ch, start, length)));
            tLeft = false;
        }else if (pAccess) {
            String[] access = (new String(ch, start, length)).split(",");
            List<AccessTuple> pageAccess = new ArrayList<AccessTuple>();
            for (int i = 0; i < access.length; i++) {
                String[] timePage = access[i].split("-");
                int time = Integer.parseInt(timePage[0]);
                int page = Integer.parseInt(timePage[1]);

                AccessTuple accessTuple = new AccessTuple(time, page);
                pageAccess.add(accessTuple);
            }   
            process.setPageAccess(pageAccess);
            pAccess = false;
        }
    }

    public PriorityQueue<Process> processes() {
        return _processes;
    }
}

/*
 
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
 
 
import com.journaldev.xml.Employee;
 
public class ProcessParser {
 
    public static void main(String[] args) {
      }
 
}
*/

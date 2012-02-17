package ocss.nmea.api;

import java.util.ArrayList;
import ocss.nmea.api.NMEAEvent;
import ocss.nmea.api.NMEAListener;

/**
 * A Model. This is an abstract class to extend to implement your own data-source.
 * Examples are given for a file containing the data - that can be used as a simulator,
 * and for a Serial Port, that can be used for the real world.
 * 
 * @version 1.0
 * @author Olivier Le Diouris
 */
public abstract class NMEAReader extends Thread
{
  private transient ArrayList NMEAListeners = null; // new ArrayList(2);

  boolean goRead = true;
  
  public NMEAReader(ArrayList al)
  {
    System.out.println("Creating reader");
    NMEAListeners = al;
    this.addNMEAListener(new NMEAListener()
      {
        public void stopReading(NMEAEvent e)
        {
          System.out.println("Stopping reading");
          goRead = false;
        }
      });
  }

  /**
   * The one that tells the Controller to start working
   * 
   * @see ocss.nmea.api.NMEAParser
   */
  protected void fireDataRead(NMEAEvent e)
  {
    for (int i=0; i<NMEAListeners.size(); i++)
    {
      NMEAListener l = (NMEAListener)NMEAListeners.get(i);
      l.dataRead(e);
    }
  }

  public synchronized void addNMEAListener(NMEAListener l)
  {
    if (!NMEAListeners.contains(l))
    {
      NMEAListeners.add(l);
    }
  }

  public synchronized void removeNMEAListener(NMEAListener l)
  {
    NMEAListeners.remove(l);
  }

  public boolean canRead()
  { return goRead; }

  public void enableReading()
  { this.goRead = true; }
  /**
   * Customize, overwrite this class to get plugged on the right datasource
   * like a Serial Port for example.
   */
  public abstract void read();

  public void run()
  {
    System.out.println("Reader Running");
    read();
  }
}

package ocss.nmea.api;

import java.util.ArrayList;
import ocss.nmea.api.NMEAEvent;
import ocss.nmea.api.NMEAListener;
import ocss.nmea.api.NMEAException;

/**
 * A Controller.
 * This class is final, and can be used as it is.
 * 
 * @see ocss.nmea.api.NMEAReader
 * @see ocss.nmea.api.NMEAClient
 * @see ocss.nmea.api.NMEAEvent
 * @see ocss.nmea.api.NMEAException
 * 
 * @version 1.0
 * @author Olivier Le Diouris
 * 
 */
public final class NMEAParser extends Thread
{
  protected String nmeaPrefix = "";
  private String[] nmeaSentence = null;

  private String nmeaStream = "";
  private final static long MAX_STREAM_SIZE = 2048;
  private final static String NMEA_EOS = "\r\n";
  private transient ArrayList NMEAListeners = null; // new ArrayList(2);

  /**
   * @param The ArrayList of the Listeners instanciated by the NMEAClient
   */
  public NMEAParser(ArrayList al)
  {  
    System.out.println("Creating parser");
    NMEAListeners = al;
    this.addNMEAListener(new NMEAListener()
      {
        public void dataRead(NMEAEvent e)
        {
//        System.out.println("Receieved Data:" + e.getContent());
          nmeaStream += e.getContent();
        }
      });
  }

  public String getNmeaPrefix()
  { return this.nmeaPrefix; }
  public void setNmeaPrefix(String s)
  { this.nmeaPrefix = s; }

  public String[] getNmeaSentence()
  { return this.nmeaSentence; }
  public void setNmeaSentence(String[] sa)
  { this.nmeaSentence = sa; }

  public String getNmeaStream()
  { return this.nmeaStream; }
  public void setNmeaStream(String s)
  { this.nmeaStream = s; }

  public String detectSentence() throws NMEAException
  {
    String ret = null;
    int idx;

    try
    {
      if (interesting())
      {
        int end = nmeaStream.indexOf(NMEA_EOS);
        ret = nmeaStream.substring(0, end);
        nmeaStream = nmeaStream.substring(end + NMEA_EOS.length());
      }
      else
      {
        if (nmeaStream.length() > MAX_STREAM_SIZE)
            nmeaStream = ""; // Reset to avoid OutOfMemoryException
        return null; // Not enough info
      }  
    }
    catch (NMEAException e)
    {
      throw e;
    }
    return ret;
  }

  private boolean interesting() throws NMEAException
  {
    if (nmeaPrefix.length() == 0)
      throw new NMEAException("NMEA Prefix is not set");
      
    int beginIdx = nmeaStream.indexOf("$" + this.nmeaPrefix);
    int endIdx   = nmeaStream.indexOf(NMEA_EOS);

    if (beginIdx == -1 && endIdx == -1)
      return false; // No beginning, no end !
      
    if (endIdx > -1 && endIdx < beginIdx) // Seek the beginning of a sentence
    {
      nmeaStream = nmeaStream.substring(endIdx + NMEA_EOS.length());
      beginIdx = nmeaStream.indexOf("$" + this.nmeaPrefix);
    }

    if (beginIdx== -1)
      return false;
    else
    {
      while (true)
      {
        try
        {
          // The stream should here begin with $XX
          if (nmeaStream.length() > 6) // "$" + prefix + XXX
          {
            if ((endIdx = nmeaStream.indexOf(NMEA_EOS)) > -1)
            {                
              for (int i=0; i<this.nmeaSentence.length; i++)
              {
                if (nmeaStream.startsWith("$" + nmeaPrefix + nmeaSentence[i]))
                  return true;
              }
              nmeaStream = nmeaStream.substring(endIdx + NMEA_EOS.length());
            }
            else
              return false; // unfinished sentence
          }
          else
            return false; // Not long enough - Not even sentence ID
        }
        catch (Exception e)
        {
          System.err.println("Oooch!");
          e.printStackTrace();
          System.out.println("nmeaStream.length = " + nmeaStream.length() + ", Stream:[" + nmeaStream + "]");
        }
      } // End of infinite loop
    }
  }

  protected void fireDataDetected(NMEAEvent e)
  {
    for (int i=0; i<NMEAListeners.size(); i++)
    {
      NMEAListener l = (NMEAListener)NMEAListeners.get(i);
      l.dataDetected(e);
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

  public void loop()
  {
    while (true)
    {
      try 
      { 
        String s = "";
        while ( (s = detectSentence()) != null)
        {
          this.fireDataDetected(new NMEAEvent(this, s));
        }
      } 
      catch (Exception e)
      {
        e.printStackTrace();
      }
      try
      {
//      System.out.println("Parser Taking a 1s nap");
        Thread.sleep(1000);
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }
    }
  }

  public void run()
  {
    System.out.println("Parser Running");
    loop();
  }

}
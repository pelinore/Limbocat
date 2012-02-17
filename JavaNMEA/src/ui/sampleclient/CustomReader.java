package ui.sampleclient;

import ocss.nmea.api.NMEAReader;
import ocss.nmea.api.NMEAEvent;
import java.util.ArrayList;
import java.io.FileInputStream;

/**
 * A Simulator, taking its inputs from a file
 */
public class CustomReader extends NMEAReader
{
  public CustomReader(ArrayList al)
  {
    super(al);
  }
  
  public void read()
  {
    // Simulation
    super.enableReading();
    //String fileName = "hydra2.nmea";
    String fileName = "e:/working/development/samplenmea.txt";
    try
    {
      FileInputStream fis = new FileInputStream(fileName);
      while (canRead())
      {
        double size = Math.random();
        int dim = (int)(750 * size);
        byte[] ba = new byte[dim];
        int l = fis.read(ba);
        //System.out.println("Read " + l);
        if (l != -1 && dim > 0)
        {
          fireDataRead(new NMEAEvent(this, new String(ba)));
          try { Thread.sleep(500); } catch (Exception ignore) {}
        }
        else
        {
          System.out.println("===== Reseting Reader =====");
          fis.close();
          fis = new FileInputStream(fileName);
        }
      }
    }
    catch (Exception e)
    {
     e.printStackTrace();
    }
  }
}
package ui.sampleclient;
import ocss.nmea.api.NMEAClient;
import ocss.nmea.api.NMEAEvent;
import ocss.nmea.parser.*;

/**
 * The simplest you can write
 */
public class CustomClient extends NMEAClient
{
  public CustomClient(String s, String[] sa)
  {
    super(s, sa);
  }

  public void dataDetectedEvent(NMEAEvent e)
  {
    System.out.println("Received:" + e.getContent());
    System.out.println("HDM: " +  StringParsers.parseHDM(e.getContent()));
  }

  public static void main(String[] args)
  {
    String prefix = "II";
    String[] array = {"HDM", "GLL", "XTE", "MWV", "VHW"};
    CustomClient customClient = new CustomClient(prefix, array);
    customClient.initClient();
    customClient.setReader(new CustomReader(customClient.getListeners()));       // Simulator
//  customClient.setReader(new CustomSerialClient(customClient.getListeners())); // Serial Port reader
    customClient.startWorking();
  }
}
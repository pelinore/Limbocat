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
		String nmeaType = e.getType();
		//System.out.println("Received:" + e.getContent());
		//System.out.println("NMEA Sentence Type: " + nmeaType);
		if (nmeaType.matches("HDM")) {
			System.out.println("Heading: " +  StringParsers.parseHDM(e.getContent()) + " Magnetic");
			System.out.println();
		} else if (nmeaType.matches("MWV")) {
			Wind wind = StringParsers.parseMWV(e.getContent());
			System.out.println("Wind Speed: " + wind.speed);
			System.out.println("Wind Angle: " + wind.angle);
			System.out.println();
		} else if (nmeaType.matches("DBT")) {
			System.out.println("Depth: " +  StringParsers.parseDBT(e.getContent(), (short) 0) + " feet");
			System.out.println();
		} else if (nmeaType.matches("VHW")) {
			System.out.println("Speed: " + StringParsers.parseVHW(e.getContent()));
			System.out.println();
		} else if (nmeaType.matches("GLL")) {
			GeoPos position = StringParsers.parseGLL(e.getContent());
			System.out.println("Latitude:  " + position.getLatInDegMinDec());
			System.out.println("Longitude: " + position.getLngInDegMinDec());
			System.out.println();
		} else {
			System.out.println("NMEA Sentence type " + nmeaType + " not one of the configured types");
			System.out.println("  Received:" + e.getContent());
			System.out.println();
		};
		
	}

	public static void main(String[] args)
	{
		String prefix = "II";
		String[] array = {"HDM", "GLL", "MWV", "VHW", "DBT"};
		CustomClient customClient = new CustomClient(prefix, array);
		customClient.initClient();
		customClient.setReader(new CustomReader(customClient.getListeners()));       // Simulator
		//  customClient.setReader(new CustomSerialClient(customClient.getListeners())); // Serial Port reader
		customClient.startWorking();
	}
}
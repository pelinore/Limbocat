package ocss.nmea.parser;
import ocss.nmea.parser.Wind;
import ocss.nmea.parser.GeoPos;

public class StringParsers 
{
  public StringParsers()
  {
  }

  public static double parseVHW(String s)
  {
    /* Structure is 
     *  $aaVHW,x.x,T,x.x,M,x.x,N,x.x,K*hh(CR)(LF)
     *         |     |     |     |
     *         |     |     |     Speed in km/h
     *         |     |     Speed in knots
     *         |     Heading in degrees, Magnetic
     *         Heading in degrees, True   
     */
    // We're interested only in Speed in knots.
    double speed = 0.0;
    String str = "";
    try
    {
      if (s.indexOf(",N,") > -1 && s.indexOf(",M,") > -1)
      {
        if (s.indexOf(",N,") > s.indexOf(",M,"))
          str = s.substring(s.indexOf(",M,") + ",M,".length(), s.indexOf(",N,"));        
      }
      speed = Double.parseDouble(str);
    }
    catch (Exception e)
    {
      System.err.println("For " + s + ", " + e.toString());
//    e.printStackTrace();
    }
    return speed;
  }

  public static Wind parseMWV(String s)
  {
    /* Structure is 
     *  $aaMWV,x.x,a,x.x,a,A*hh
     *         |   | |   | |
     *         |   | |   | status : A=data valid
     *         |   | |   Wind Speed unit (K/M/N)
     *         |   | Wind Speed
     *         |   reference R=relative, T=true
     *         Wind angle 0 to 360 degrees 
     */
    // We're interested only in Speed in knots.
    Wind aw = null;
    try
    {
      if (s.indexOf("A*") == -1) // Data invalid
        return aw;
      else
      {
        String speed = "", angle = "";
        if (s.indexOf("MWV,") > -1 && s.indexOf(",R,") > -1)
        {
          angle = s.substring(s.indexOf("MWV,") + "MWV,".length(), s.indexOf(",R,"));        
        }
        if (s.indexOf(",R,") > -1 && s.indexOf(",N,") > -1)
        {
          speed = s.substring(s.indexOf(",R,") + ",R,".length(), s.indexOf(",N,"));        
        }
        aw = new Wind(Integer.parseInt(angle), Double.parseDouble(speed));
      }
    }
    catch (Exception e)
    {
      System.err.println("For " + s + ", " + e.toString());
//    e.printStackTrace();
    }
    return aw;
  }
  
  public static GeoPos parseGLL(String s)
  {
    /* Structure is 
     *  $aaGLL,llll.ll,a,gggg.gg,a,hhmmss.ss,A*hh
     *         |       | |       | |         |
     *         |       | |       | |         A:data valid
     *         |       | |       | UTC of position
     *         |       | |       Long sign :E/W
     *         |       | Longitude
     *         |       Lat sign :N/S
     *         Latitude
     */
    String str = "";
    GeoPos ll = null;
    try
    {
      if (s.indexOf("A*") == -1) // Data invalid
        return ll;
      else
      {
        int i = s.indexOf(",");
        if (i > -1)
        {
          String lat = "";
          int j = s.indexOf(",", i+1);
          lat = s.substring(i+1, j);
          double l = Double.parseDouble(lat);
          int intL = (int)l/100;
          double m = ((l/100.0)-intL) * 100.0;
          m *= (100.0/60.0);
          l = intL + (m/100.0);
          String latSgn = s.substring(j+1, j+2);
          if (latSgn.equals("S"))
            l *= -1.0;
          int k = s.indexOf(",", j+3);
          String lng = s.substring(j+3, k);
          double g = Double.parseDouble(lng);
          int intG = (int)g/100;
          m = ((g/100.0)-intG) * 100.0;
          m *= (100.0/60.0);
          g = intG + (m/100.0);
          String lngSgn = s.substring(k+1, k+2);
          if (lngSgn.equals("W"))
            g *= -1.0;
          
          ll = new GeoPos(l, g);          
        }
      }
//    System.out.println(str);
    }
    catch (Exception e)
    {
      System.err.println("For " + s + ", " + e.toString());
//    e.printStackTrace();
    }
    return ll;
  }

  public static int parseHDM(String s)
  {
    /* Structure is 
     *  $aaHDG,xxx,M*hh(CR)(LF)
     *         |   |   
     *         |   Magnetic, True
     *         Heading in degrees
     */
    int hdg = 0;
    String str = "";
    try
    {
      if (s.indexOf("HDM,") > -1)
      {
        str = s.substring(s.indexOf("HDM,") + "HDM,".length());
        if (str.indexOf(",") > -1)
        {
          str = str.substring(0, str.indexOf(","));
          hdg = Integer.parseInt(str);
        }
      }
    }
    catch (Exception e)
    {
      System.err.println("For " + s + ", " + e.toString());
//    e.printStackTrace();
    }
    return hdg;
  }

  public static String parseRMC(String data)
  {
    String computed = "";
    String lat = "", latSgn = "", lon = "", lonSgn = "";
    String nmeaKey = "";
    try { nmeaKey = data.substring(3, 6); }
    catch (Exception e)
    { return ""; }
    if (nmeaKey.equals("RMC"))
    {
   /* $GPRMC,004007,A,3748.410,N,12226.632,W,000.0,360.0,130102,015.7,E*6F */
      if (data.indexOf(",A,") > -1)
      {  // That's what we're interested in.
        // Example : [$GPRMC,004007,A,3748.410,N,12226.632,W,000.0,360.0,130102,015.7,E*6F]
        String tmp = data.substring(data.indexOf(",A,") + ",A,".length());  
        // tmp is now : [3748.410,N,12226.632,W,000.0,360.0,130102,015.7,E*6F]
        if (tmp.indexOf(",") > -1) // Latitude Value
        {
          lat = tmp.substring(0, tmp.indexOf(","));
          tmp = tmp.substring(tmp.indexOf(",") + ",".length());
          // tmp is now : [N,12226.632,W,000.0,360.0,130102,015.7,E*6F]
        }
        if (tmp.indexOf(",") > -1) // Latitude Sign
        {
          latSgn = tmp.substring(0, tmp.indexOf(","));
          tmp = tmp.substring(tmp.indexOf(",") + ",".length());
          // tmp is now : [12226.632,W,000.0,360.0,130102,015.7,E*6F]
        }
        if (tmp.indexOf(",") > -1) // Longitude Value
        {
          lon = tmp.substring(0, tmp.indexOf(","));
          tmp = tmp.substring(tmp.indexOf(",") + ",".length());
          // tmp is now : [W,000.0,360.0,130102,015.7,E*6F]
        }
        if (tmp.indexOf(",") > -1) // Longitude Sign
        {
          lonSgn = tmp.substring(0, tmp.indexOf(","));
          tmp = tmp.substring(tmp.indexOf(",") + ",".length());
          // tmp is now : [000.0,360.0,130102,015.7,E*6F]
        }
      }
    }
    if (lat.length() > 0)
    {
      if (lat.indexOf(".") > -1)
      {
        String s = lat.substring(0, lat.indexOf("."));
        s = s.substring(0, s.length() - 2) + "\272" + lat.substring(s.length() - 2);
        lat = s;
      }
    }
    else
      lat = "Not Set";
    if (lon.length() > 0)
    {
      if (lon.indexOf(".") > -1)
      {
        String s = lon.substring(0, lon.indexOf("."));
        s = s.substring(0, s.length() - 2) + "\272" + lon.substring(s.length() - 2);
        lon = s;
      }
    }
    else
      lon = "Not Set";
      
    computed = latSgn + " " + lat + "/" + lonSgn + " " + lon;
    return computed;
  }

  public static final short DEPTH_IN_FEET    = 0;
  public static final short DEPTH_IN_METERS  = 1;
  public static final short DEPTH_IN_FATHOMS = 2;
  
  public static float parseDBT(String s, short unit)
  {
    /* Structure is 
     *  $aaDBT,011.0,f,03.3,M,01.8,F*18(CR)(LF)
     *         |     | |    | |    |
     *         |     | |    | |    F for fathoms
     *         |     | |    | Depth in fathoms
     *         |     | |    M for meters
     *         |     | Depth in meters
     *         |     f for feet
     *         Depth in feet   
     */
    float feet    = 0.0F;
    float meters  = 0.0F;
    float fathoms = 0.0F;
    String str = "";
    String first = "", last = "";
    try
    {
      first = "DBT,";
      last  = ",f,";
      if (s.indexOf(first) > -1 && s.indexOf(last) > -1)
      {
        if (s.indexOf(first) < s.indexOf(last))
          str = s.substring(s.indexOf(first) + first.length(), s.indexOf(last));        
      }
      feet = Float.parseFloat(str);
      first = ",f,";
      last  = ",M,";
      if (s.indexOf(first) > -1 && s.indexOf(last) > -1)
      {
        if (s.indexOf(first) < s.indexOf(last))
          str = s.substring(s.indexOf(first) + first.length(), s.indexOf(last));        
      }
      meters = Float.parseFloat(str);
      first = ",M,";
      last  = ",F";
      if (s.indexOf(first) > -1 && s.indexOf(last) > -1)
      {
        if (s.indexOf(first) < s.indexOf(last))
          str = s.substring(s.indexOf(first) + first.length(), s.indexOf(last));        
      }
      fathoms = Float.parseFloat(str);
    }
    catch (Exception e)
    {
      System.err.println("For " + s + ", " + e.toString());
//    e.printStackTrace();
    }

    if (unit == DEPTH_IN_FEET)
      return feet;
    else if (unit == DEPTH_IN_METERS)
      return meters;
    else if (unit == DEPTH_IN_FATHOMS)
      return fathoms;
    else
      return feet;
  }
}
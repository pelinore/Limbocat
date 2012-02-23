package ocss.nmea.parser;

public final class Wind
{
  public double speed = 0.0;
  public int    angle = 0;
  public String reference = ""; 

  public Wind(int a,
              double s)
  {
    this.speed = s;
    this.angle = a;
  }
  
  public Wind(int a, double s, String r) {
	  this(a,s);
	  this.reference = r;
  }
}

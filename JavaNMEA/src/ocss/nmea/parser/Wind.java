package ocss.nmea.parser;

public final class Wind
{
  public double speed = 0.0;
  public int    angle = 0;

  public Wind(int a,
              double s)
  {
    this.speed = s;
    this.angle = a;
  }
}



public class nPoint {
  public int x, y;
  public nPoint(int nx, int ny) {
    x = nx;
    y = ny;
  }
  public nPoint(nPoint p) {
    x = p.x;
    y = p.y;
  }
   public void move(int nx, int ny) {
     x = nx; y = ny;
   }
   public void translate ( int dx, int dy ) {
     x += dx; y += dy;
   }
}
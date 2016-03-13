import nPoint;
//import java.awt.*;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Color;
//import java.lang.Math;

class Universe {
  int MX = 0;
  int MY = 0;
  int blocksize = 5;
//  public Graphics Canvas;
  private Graphics InitialCanvas ;
//  private Image InitialImage;
  int Grounded = 1;
  static nPoint Walls[] = new nPoint[100];
  static int wallcount = 0;
  Color C ;
  SmartBlock Header = null;

  static int DEBUGLEV = 0;

  private static int slots[][] ;

  Universe ( Graphics gparm,int mx,int my,int bsize ) {
    MX = mx;
    MY = my;
    slots = new int[(MX+1)][(MY+1)];
    blocksize = bsize;
    wallcount = 0;
    // fill in the borders of the gamefield.  This will bound
    //  other agents to being within the border.
    int i = 0, j = 0;
    InitialCanvas = gparm.create();

    //  clear center area.
    for ( i = 0; i <= MX; i++ ) {
      for ( j = 0; j <= MY; j++ )
        slots[i][j]= 0;
    }
    // set top and bottom rows.
//    for ( i = 0; i <= MY; i++ ) {
//      slots[i][0]=true;
//      slots[i][MY]=true;
//    }
//    // set left and right columns
//    for ( i = 0; i <= MY; i++ ) {
//      slots[0][i]=true;
//      slots[MX][i]=true;
//    }
    Debug(8,"The Universe has been created.");
  }
  private void Debug ( int level, String message ) {
    if (DEBUGLEV >= level )
      System.out.println("  Universe: "+message);
  }


  synchronized public boolean CreateBlock ( SmartBlock S ) {
    if ( getSlot(S.Location) != 0) {
    //  slot is already occuppied !
    //  Or, it's not a valid location.
       return false;
    }  else {
      setSlot(S.Location,true);
      return true;
    }
  }
  synchronized public void Refresh (SmartBlock S[],int blocks) {
    int i,j;
    InitialCanvas.setColor(C.black);
    InitialCanvas.fillRect(0,0,(MX+1)*blocksize,(MY+2)*blocksize);
    InitialCanvas.setColor(C.red);
    InitialCanvas.drawRect(0,0,(MX+1)*blocksize+1,(MY+2)*blocksize);

    InitialCanvas.setColor(C.cyan);
     for ( i = 0; i < blocks; i++ ) {
       InitialCanvas.setColor(S[i].C);
       InitialCanvas.fillRect(S[i].Location.x*blocksize,
            MY*blocksize - S[i].Location.y*blocksize,blocksize-1,blocksize-1);
     }
    InitialCanvas.setColor(C.gray);
     for ( i = 0; i < wallcount; i++ ) {
       InitialCanvas.fillRect(Walls[i].x*blocksize,
            MY*blocksize - Walls[i].y*blocksize,blocksize-1,blocksize-1);
     }
  }
  synchronized public int getSlot(nPoint P) {
    return getSlot(P.x,P.y);
  }
  synchronized public int getSlot(int x, int y) {
    if (inBounds(x,y)) {
       Debug(9,x+","+y+" is in-bounds..");
       return slots[x][y];
    } else {
       Debug(8,x+","+y+" is out-of-bounds.")  ;
       return -1;
    }
  }
  private void moveCell(int x1,int y1, int x2, int y2) {
    synchronized (InitialCanvas) {
      setSlot(x1,y1,false);
      setSlot(x2,y2,true);
      Debug(4,"moved "+x1+","+y1+" to "+x2+","+y2);
    }
  }
  synchronized public boolean setStatic(int x,int y,boolean value) {
    if ( x < 0 || y < 0 || x >= MX || y >= MY ) {
      return false;
    }
    slots[x][y] = 1; // 2 represents static block
    Walls[wallcount++] = new nPoint(x,y);
//    System.out.println("setStatic "+x+","+y+" to "+value);
    return true;
  }
  synchronized public boolean setSlot(nPoint P,boolean value) {
    if ( P.x < 0 || P.y < 0 || P.x >= MX || P.y >= MY ) {
      return false;
    }
//    slots[P.x][P.y] = value;

    if (  value == false ) {
        slots[P.x][P.y] = 0;
    } else {
        slots[P.x][P.y] = 2;
    }
    return true;
  }

  synchronized public boolean setSlot(int x, int y,boolean value) {
    if ( x < 0 || y < 0 || x >= MX || y >= MY ) {
      return false;
    }
    if (  value == false ) {
        slots[x][y] = 0;
    } else {
        slots[x][y] = 2;
    }
    return true;
  }
  synchronized private boolean touchingNeighbors
        (SmartBlock S,int nx,int ny) {
    nPoint N = new nPoint(nx,ny);
    nPoint d = new nPoint(0,0);
    // decide if a new location for S (nx,ny)
    // will still be touching its neighbor(s)
    if ( S.LeftNeighbor != null ) {
      d.move(S.LeftNeighbor.Location.x,S.LeftNeighbor.Location.y);
      if ( java.lang.Math.abs( d.x - N.x ) > 1 ) return false;
      if ( java.lang.Math.abs( d.y - N.y ) > 1 ) return false;
    }
    if ( S.RightNeighbor != null ) {
      d.move(S.RightNeighbor.Location.x,S.RightNeighbor.Location.y);
      if ( java.lang.Math.abs( d.x - N.x ) > 1 ) return false;
      if ( java.lang.Math.abs( d.y - N.y ) > 1 ) return false;
    }

      // check for neighboring the left neighbor
    return true;
  }
  synchronized private boolean inBounds (int x,int y) {
     if ( y >= 0 & y < MY & x >= 0 & x < MX ) {
         return true;
     } else {
//       Debug(1,"  ("+x+","
//        +y+") would be out of bounds.");
        return false;
     }
  }

  synchronized public boolean Shift (SmartBlock S,int direction) {
   int nx,ny;
   int ox,oy;
   ox = S.Location.x;
   oy = S.Location.y;
   nPoint P = S.Location;
   nPoint R = new nPoint(S.Location);
//   Debug(8,"Shift P("+P.x+","+P.y+") in direction "+direction);
//   Debug(0,"Move:"+direction);
   if (getSlot(P) == 0) {
     Debug(1,"Shifting from an empty location! "+P.x+","+P.y);
     return false;  // no one is there !
   }
   switch ( direction ) {
         case 0 : {  // up and left.
            nx = P.x - 1;
            ny = P.y + 1;
            if ( !inBounds(nx,ny) ||!touchingNeighbors(S,nx,ny) )
               return false;
            if ( getSlot(nx,ny) == 0 ) {
              if ( P.y == 0 ) Grounded--;
              moveCell(ox,oy,nx,ny);
              S.Location.move(nx,ny);
              return true;
            }
           break;
         } //up-left
         case 1 : {  // up.
            nx = P.x ;
            ny = P.y + 1;
            if ( !inBounds(nx,ny) ||!touchingNeighbors(S,nx,ny) )
               return false;
            if ( getSlot(nx,ny) == 0 ) {
              if ( P.y == 0 ) Grounded--;
              moveCell(ox,oy,nx,ny);
              S.Location.move(nx,ny);
              return true;
            }
           break;
         } //up
         case 2 : {  // up and right
            nx = P.x + 1;
            ny = P.y + 1;
            if ( !inBounds(nx,ny) ||!touchingNeighbors(S,nx,ny) )
               return false;
            if ( getSlot(nx,ny) == 0 ) {
              if ( P.y == 1 ) Grounded--;
              moveCell(ox,oy,nx,ny);
              S.Location.move(nx,ny);
              return true;
            } else {
              Debug(8,"("+P.x+","+P.y+") is occuppied.");
            }
           break;
         } //up-right
         case 3 : {  // right
//           Debug(1,"Begin of right move");
            nx = P.x + 1;
            ny = P.y ;
            if (!inBounds(nx,ny) ||!touchingNeighbors(S,nx,ny) )
               return false;
            if ( getSlot(nx,ny) == 0 ) {
              moveCell(ox,oy,nx,ny);
              S.Location.move(nx,ny);
              return true;
            } else {
              Debug(8,"("+P.x+","+P.y+") is occuppied.");
            }
           break;
       } //right
      case 4 : {  // down and right
            nx = P.x + 1;
            ny = P.y - 1;
            if (!inBounds(nx,ny) ||!touchingNeighbors(S,nx,ny) )
               return false;
            if ( getSlot(nx,ny) == 0) {
              if ( ny == 0  ) Grounded++; // landed on ground.
              moveCell(ox,oy,nx,ny);
              S.Location.move(nx,ny);
              return true;
            }
         break;
       }   //down-right

       case 5 : {  // down
            nx = P.x ;
            ny = P.y - 1;
            if (!inBounds(nx,ny) ||!touchingNeighbors(S,nx,ny) )
              return false;
            if ( getSlot(nx,ny) == 0 ) {
              if ( ny == 0  ) Grounded++; // landed on ground.
              S.Location.move(nx,ny);
              moveCell(ox,oy,nx,ny);
              return true;
            }
           break;
         } // down
      case 6 : {  // down and left
            nx = P.x - 1;
            ny = P.y - 1;
            if (!inBounds(nx,ny) ||!touchingNeighbors(S,nx,ny) )
               return false;
            if ( getSlot(nx,ny) == 0) {
                 Debug(8,"A new neighbor exists.");
              if ( ny == 0  ) Grounded++; // landed on ground.
              moveCell(ox,oy,nx,ny);
              S.Location.move(nx,ny);
              return true;
            } //down-left
         break;
       }
       case 7 : {  // left
//       Debug(1,"Begin of left move");
         nx = P.x - 1;
         ny = P.y ;
         if (!inBounds(nx,ny) ||!touchingNeighbors(S,nx,ny) )
           return false;
         if ( getSlot(nx,ny)== 0 ) {
           moveCell(ox,oy,nx,ny);
           S.Location.move(nx,ny);
           return true;
         } else {
//          Debug(8,"("+P.x+","+P.y+") is occuppied.");
         }
         break;
       } //left
   } // switch
   return false;
 } // method Shift
}

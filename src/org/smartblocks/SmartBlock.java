package org.smartblocks;

import java.util.Random;
import java.awt.Graphics;
import java.awt.*;

public class SmartBlock extends Thread {
  static int GENES = 128;
  static int DNA[] ;
  static int Grounded = 1;
  static int RunExperiment = 0;
  static Random R = new Random();
  static Universe U ;
  public static int Pause = 0;
  static int Sleep_Time;
//  static long LifeSpan = 100000;
//  static long Age = 0;

  int PC = 0;  // Program Counter, as in any good CPU.

  private boolean Success_State = true; // indicates success of last move.
  SmartBlock LeftNeighbor = null;
  SmartBlock RightNeighbor = null;
  int ID;
  public Color C;
  nPoint Location;

  static void Print ( String s ) {
    System.out.println(s);
  }

  SmartBlock (int iX, int iY,int id) {
    int i;
    ID = id;

    Location = new nPoint(iX,iY);
    U.setSlot(iX,iY,true);

//    System.out.println("Block "+ID+" born at "+Location.x+", "+Location.y);
//    C = new Color(255 - (127 & 37*id),
//                  255 & (64+id*43),
//                  255 - (16+id*13)& 255 );

//        C = new Color.HSBtoRGB((float).5,(float).5,(float).5);
      C = new
      Color ( (13 * id )& 255 | 32,
              (255 - 7 * id )& 255 | 16, (13 * id ) & 255 | 16);

//      Color ( 60, 32 |((id * 35) & 255), 64 | ((128 + (id * 35)) & 255) );
  }

  public static synchronized void Mutate(double MF) {
  //  MF is the mutaton factor, or the likelihood
  //  that a gene will mutate.
    int i, mutated = 0;
    float j;

    for ( i = 0; i<GENES;i++ ) {
      j = R.nextFloat();
      if ( j < MF ) {
        mutated++;
        DNA[i] = java.lang.Math.abs(R.nextInt() % 75);
        if (DNA[i] < 64) {
           DNA[i] = DNA[i] & 7;
        }
      }
    }
    System.out.println("Mutation altered "+mutated+" genes.");
  }

   static void ShowMove(int instruction) {
   int i;
     switch (instruction) {
        case 0: { Print("UP-LEFT"); break; }
        case 1: { Print("UP"); break; }
        case 2: { Print("UP-RIGHT"); break; }
        case 3: { Print("RIGHT"); break; }
        case 4: { Print("DOWN-RIGHT"); break; }
        case 5: { Print("DOWN"); break; }
        case 6: { Print("DOWN-LEFT"); break; }
        case 7: { Print("LEFT"); break; }
        case 64: { Print("PC += 1"); break; }
        case 65: { Print("SUCCESS   ->PC += 1"); break; }
        case 66: { Print("! SUCCESS ->PC += 1"); break; }
        case 67: { Print("L.Y>Y     ->PC += 1"); break; }
        case 68: { Print("L.Y<Y     ->PC += 1"); break; }
        case 79: { Print("L.X<X     ->PC += 1"); break; }
        case 70: { Print("L.X>X     ->PC += 1"); break; }
        case 71: { Print("R.Y>Y     ->PC += 1"); break; }
        case 72: { Print("R.Y<Y     ->PC += 1"); break; }
        case 73: { Print("R.X<X     ->PC += 1"); break; }
        case 74: { Print("R.X>X     ->PC += 1"); break; }
        case 75: { Print("NOP. "); break; }
       }
   }
  private void Move() {
    int instruction = DNA[PC];
    if (instruction < 8) {
         Success_State = U.Shift(this,instruction);
    } else {
      // instructions 8 and above are "decision" actions.
      // They are equivalent to branch statements.
      switch ( instruction ) {
        case 64: { // unconditional skip-next.
           PC = PC + 1;
           break;
        }
        case 65: { // skip-on-success
         if (Success_State) {
           PC = PC + 1; // skip if previous action succeeded
         }
         break;
        }
        case 66:  {// skip-on-failure
         if (!Success_State) {
           PC = PC + 1; // which will wrap-around to 0.
         }
         break;
         }
//  left-neighbor decisions.
        case 67:  {// Skip-if-LeftNeighbor-Higher
         if ( LeftNeighbor != null) {
           if (LeftNeighbor.Location.y > Location.y ) {
             PC = PC + 1; // which will wrap-around to 0.
           }
         }
         break;
        }
        case 68:  {// Skip-if-LeftNeighbor-Lower
         if ( LeftNeighbor != null) {
           if (LeftNeighbor.Location.y < Location.y ) {
             PC = PC + 1; // which will wrap-around to 0.
           }
         }
         break;
        }
        case 69:  {// Skip-if-LeftNeighbor-is-to-the-left.
         if ( LeftNeighbor != null) {
           if (LeftNeighbor.Location.x < Location.x ) {
             PC = PC + 1; // which will wrap-around to 0.
           }
         }
         break;
         }
         case 70:  { // Skip-if-LeftNeighbor-is-to-the-right.
          if ( LeftNeighbor != null) {
           if (LeftNeighbor.Location.x > Location.x ) {
             PC = PC + 1; // which will wrap-around to 0.
           }
          }
          break;
         }
         case 71:  {// Skip-if-RightNeighbor-Higher
          if ( RightNeighbor != null) {
            if (RightNeighbor.Location.y > Location.y ) {
             PC = PC + 1; // which will wrap-around to 0.
            }
          }
          break;
         }
         case 72:  { // Skip-if-RightNeighbor-Lower
          if ( RightNeighbor != null) {
            if (RightNeighbor.Location.y < Location.y ) {
              PC = PC + 1; // which will wrap-around to 0.
            }
          }
          break;
         }
         case 73: { // Skip-if-RightNeighbor-is-to-the-left.
           if ( RightNeighbor != null) {
             if (RightNeighbor.Location.x < Location.x ) {
             PC = PC + 1; // which will wrap-around to 0.
           }
         }
         break;
       }
         case 74:  { // Skip-if-RightNeighbor-is-to-the-right.
           if ( RightNeighbor != null) {
             if (RightNeighbor.Location.x > Location.x ) {
               PC = PC + 1; // which will wrap-around to 0.
             }
           }
         break;
       } // case
       case 75:  { // Pause
         // NO-OP.   Pause.
         break;
       } // case
      }   // switch
    } //if-else

    PC++;
    if ( PC >= GENES ) {
        PC -= GENES; // wrap-around.
    }
    return;
  }
/*

//  success-fail decisions.




    }  // switch
*/



  public static void Initialize(Universe u,int Genes, int sleep) {
    int i;
    int x,  y;
//    X = y; Y = y;
    System.out.println("Initializing!");
    System.out.println("Gene sequence:");
    U = u;
    GENES = Genes;
     Sleep_Time = sleep;
    DNA = new int[GENES+1];
    Mutate(1);  // all new mutations.
/*    for ( i = 0; i<GENES;i++ ) {
      DNA[i] = R.nextInt() & 7;
      System.out.println("Gene["+i+"] = "+DNA[i]);
    }
*/
    DNA[GENES] = -1;
  }

  public void run() {
    PC = 0;
    while ( RunExperiment == 0 ) {
      try { Thread.sleep(60); }
       catch (InterruptedException e) { }
    }
    while (RunExperiment == 1) {
//       System.out.println("Move in "+DNA[i]+" direction:");
//       System.out.println("Moved to "+Location.x+","+Location.y);
       try { Thread.sleep(Sleep_Time * 200 * Pause); }
       catch (InterruptedException e) { }
       if ( Pause == 0 ) {
         Move();
//         System.out.println(Age);
       }
     }
     // signal death.
//     System.out.println("Block "+ID+" died.");
   }

}

import SmartBlock;
import Universe;
import java.awt.*;
//import borland.jbcl.layout.*;
//import java.awt.event.*;

public class Ex2 extends java.applet.Applet implements Runnable {
  static Graphics dbuffer;
  Image offscreen;
  Image onscreen;
   static Universe U;
//   int i = 0;
  Thread runner;
  /** Adjustable parameters:
  **/
  int dna_length = 16;
  int blocksize = 4;
  int Generation = 0;
  int TrialRun = 0;
  int MaxTrials = 16;
  int MaxGenerations = 128;
  int LifeSpan = 200;
  int Blocks = 8;
  int Sleep_Time = 10;

  int MX = 40;
  int MY = 30;
  int Goal = 50;
  static SmartBlock S[] ;

  protected int XRES,YRES;
  int Paused = 0;
  int Clock = 1;
  int BestDNA[];
  int OriginalDNA[]; // Original genes for each generation.
  float BestScore;
  float PreviousBestScore;
  int CurrentTrial;
  int CurrentGeneration;
  public Rectangle AppBorder;

  /** GUI Elements
  **/
  Panel Field = new Panel();
  Panel controlpanel = new Panel();
  Panel parmpanel = new Panel();
  FlowLayout flowLayout1 = new FlowLayout();
  FlowLayout flowLayout2 = new FlowLayout();
  Label Status = new Label("Age:xxxx Generation: yyy");
  Button Restart = new Button("Restart");
  Button Begin = new Button("Begin");
  Button End = new Button("End");
  Button Clear = new Button("Clear");
  Button Pause = new Button("Pause  ");
//  Button Resume = new Button("Resume");
  Button Mutate = new Button("Mutate");
  Button ShowGenes = new Button("ShowGenes");
  Label Score = new Label("Score:01234");
  BorderLayout borderLayout1 = new BorderLayout();


  Label Generations_l = new Label("Generations:");
  TextField Generations_t = new TextField("16",4);

  Label Trials_l = new Label("Trials:");
  TextField Trials_t = new TextField("16");


  Label LifeSpan_l = new Label("LifeSpan:");
  TextField LifeSpan_t = new TextField("100");

  Label Blocks_l = new Label("Blocks:");
  TextField Blocks_t = new TextField("8");

  Label Speed_l = new Label("Speed:");
  TextField Speed_t = new TextField("8");

  Label BlockSize_l = new Label("Block Size:");
  TextField BlockSize_t = new TextField("8");

  Label Generation_l = new Label("Generation:xxx");
  Label Trial_l = new Label("Trial:xxx");


  /** init method  (Applet standard)
      Prepare permanent elements of the "laboratory"
  */
  public void init () {
     AppBorder = bounds();
//     XRES = 400;
//     YRES = 400;

     XRES = AppBorder.width ;
     YRES = AppBorder.height -  52;
     PrepExperiment();

     String blocksize_s, dna_length_s, Blocks_s, lifespan_s, sleep_s ;
     String maxtrials_s, maxgenerations_s;

     blocksize_s = getParameter("blocksize");
     dna_length_s = getParameter("dna_length");
     Blocks_s = getParameter("blocks");
     lifespan_s = getParameter("lifespan");
     sleep_s = getParameter("sleep");
     maxtrials_s = getParameter("maxtrials");
     maxgenerations_s = getParameter("maxgenerations");


     System.out.println("Made "+Blocks+" Blocks.");
     if ( maxtrials_s == null ) {
        MaxTrials = 16;
     } else {
        MaxTrials = Integer.parseInt(maxtrials_s);
     }
     if ( maxgenerations_s == null ) {
        MaxGenerations = 128;
     } else {
        MaxGenerations = Integer.parseInt(maxgenerations_s);
     }

     if ( sleep_s == null ) {
        Sleep_Time = 10;
     } else {
        Sleep_Time = Integer.parseInt(sleep_s);
     }
     if ( lifespan_s == null ) {
        LifeSpan = 60;
     } else {
        LifeSpan = Integer.parseInt(lifespan_s);
     }
     if ( blocksize_s == null ) {
        blocksize = 5;
     } else {
        blocksize = Integer.parseInt(blocksize_s);
     }
     if ( Blocks_s == null ) {
        Blocks = 8;
     } else {
        Blocks = Integer.parseInt(Blocks_s);
     }
     if ( dna_length_s == null ) {
        dna_length = 16;
     } else {
        dna_length = Integer.parseInt(dna_length_s);
     }
     MX = (XRES - 100) / blocksize ;
     MY = (YRES -40)/ blocksize ;
     Goal = MX / 2;

     S = new SmartBlock [Blocks];

     OriginalDNA = new int[dna_length];
     BestDNA = new int[dna_length];
     offscreen = createImage(XRES,YRES);
     onscreen = createImage(XRES,YRES);

     dbuffer = offscreen.getGraphics();
//     dbuffer.setColor(Color.black);
   }

  /** BuildCreature method
      instantiate the community of SmartBlock threads,
      and start them.  Generally, the "Pause" flag of SmartBlock
      is set before this method is called, to synchronize the
      experiment.  SmartBlocks operate independently, but they should
      all start at as close to the same time as possible.
  */
   private void BuildCreature () {
     int i;
//     SmartBlock.LifeSpan = LifeSpan;
     S = new SmartBlock [Blocks];
     for ( i=0;i<Blocks;i++) {
     S[i] = new SmartBlock(5,MY / 2 - Blocks/2 + i,i+1);
     if ( i > 0 ) {
       S[i-1].RightNeighbor = S[i];
       S[i].LeftNeighbor = S[i-1];
     }
     S[i].start();
     }
   }

  /** BuildWalls method
      Builds static structures in the matrix the laboratory Universe.
      The SmartBlocks will have to work around these structures.
  */
   private void BuildWalls ( Universe U ) {
     int i;
     for ( i = 0; i < MY; i += 3 ) {
       U.setStatic(MX/2,i,true);   // static block
     }
     for ( i = 1; i < MY; i += 3 ) {
       U.setStatic(MX/2-4,i,true);   // static block
     }
   }
   /**  Set off the loop cycle which tracks
        the progress of the experiment on-screen.
   **/

   public void run () {
    // at each "reload", do:
//    System.out.println("run.");
//    while (true) {
      while (Clock != 0) {
         repaint();
         Clock--;
         // on the average, let half the blocks move before refreshing.
         try { Thread.sleep(Sleep_Time * Blocks / 2 + 200 * Paused); }
         catch (InterruptedException e) { }
       }
       repaint();
//     }
   }

   public void stop () {
     int i;
     System.out.println("ex2.class : Stop");
      for ( i=0;i<Blocks;i++) {
        if ( S[i] != null ) {
          S[i].stop();
        }
      }
     if (runner != null )
       runner.stop();
   }
   public void start() {
     System.out.println("ex2.class : Start");
   }
   public float Evaluate () {
     int i;
     float f = 0;
     for ( i = 0; i < Blocks; i++ ) {
       if ( S[i] != null ) {
         if (S[i].Location.x > Goal) {
           f += Goal;
         } else {
           f -= Goal - S[i].Location.x;
         }
       }
     }
     f = (f / Blocks);
     return f;
   }
   public void paint (Graphics g) {
     float score;
     Refresh();
     g.drawImage(offscreen,0,35,this);
     score = 0;
     if ( SmartBlock.RunExperiment == 1) {
       score = Evaluate();
     } else {
       score = 0;
     }
     if ( score == Goal ) {
       System.out.println("Goal has been met: "+Goal);
       EndTrial();
     }

//     Trial_l.setText("Trial:"+CurrentTrial);
//     Score.setText("Timer: "+Clock+" Gen:"
//        +CurrentGeneration+" Trial: "+CurrentTrial );
     if (Clock == 0 ) {
       if (score == Goal) {
         Score.setText("Success: "+Clock);
       } else {
         Score.setText("Failure: "+score);
       }
       EndTrial();
     } else {
       Score.setText("Score: "+score);
     }
   }
   public void update (Graphics g) {
      paint(g);
   }

  public Ex2() {
    try {
      jbInit();
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }

  public void jbInit() throws Exception{
//    this.setBounds(new Rectangle(10, 10, 200, 200));
//    controlpanel.setBackground(new Color(23, 85, 116));

    this.setBackground(Color.black);
//    Field.setBackground(Color.black);
    this.setLayout(borderLayout1);
//    Field(Color.green);
    controlpanel.setBackground(Color.gray);
    controlpanel.setLayout(flowLayout1);

    parmpanel.setLayout(flowLayout1);
    parmpanel.setBackground(Color.gray);
    parmpanel.add(BlockSize_l);
    parmpanel.add(BlockSize_t);
    parmpanel.add(Speed_l);
    parmpanel.add(Speed_t);
    parmpanel.add(Blocks_l);
    parmpanel.add(Blocks_t);
    parmpanel.add(Trials_l);
    parmpanel.add(Trials_t);
    parmpanel.add(Generations_l);
    parmpanel.add(Generations_t);
    parmpanel.add(LifeSpan_l);
    parmpanel.add(LifeSpan_t);

//    parmpanel.add(Generation_l);
//    parmpanel.add(Trial_l);


    this.setLayout(borderLayout1);
//    controlpanel.add(Status);

    controlpanel.add(Begin);
    controlpanel.add(End);
    controlpanel.add(Clear);
//    controlpanel.add(Resume);
//    controlpanel.add(Mutate);
    controlpanel.add(ShowGenes);
    controlpanel.add(Score);
    controlpanel.add(Pause);
//    controlpanel.add(Restart);
    Score.setBackground(Color.lightGray);
//    add("Center",Field);
    add("North",controlpanel);
    add("South",parmpanel);
  }

  private void Refresh () {
//    S,Blocks
    int i,j;
    dbuffer.setColor(Color.black);
    dbuffer.fillRect(0,0,(MX+1)* blocksize,(MY+2)*blocksize);
    dbuffer.setColor(Color.red);
    dbuffer.drawRect(0,0,(MX+1)*blocksize+1,(MY+2)*blocksize);
    dbuffer.setColor(Color.green);
    dbuffer.drawLine((Goal+1)*blocksize+1,0,(Goal+1)*blocksize+1,(MY+2)*blocksize);
    if ( SmartBlock.RunExperiment == 0 ) {
       return;
    }
//    System.out.println("Refresh "+Blocks);
    for ( i = 0; i < Blocks; i++ ) {
//    System.out.println("Refresh: S["+i+"]");
      if (S[i] != null ) {
        if ( S[i].isAlive() ) {
          dbuffer.setColor(S[i].C);
          dbuffer.fillRect(S[i].Location.x*blocksize,
            MY*blocksize - S[i].Location.y*blocksize,blocksize-1,blocksize-1);
        }
      }
    }
    dbuffer.setColor(Color.gray);

    for ( i = 0; i < U.wallcount; i++ ) {
      dbuffer.fillRect(U.Walls[i].x*blocksize,
        MY*blocksize - U.Walls[i].y*blocksize,blocksize-1,blocksize-1);
    }
//    dbuffer.setColor(Color.green.darker());
//    dbuffer.setColor(Color.white);
//    dbuffer.fillArc(20,5+(MY+2)*blocksize,20,20,24*(Clock & 15),20);
//    System.out.println("Refreshed");
  }
  private void PrepExperiment () {
    CurrentTrial = 0;
    CurrentGeneration = 0;
//    if ( U == null )
//       U = new Universe(dbuffer,MX,MY,blocksize);
    BestScore = -9999;
    PreviousBestScore = -19999;
    Blocks = Integer.parseInt(Blocks_t.getText());
    MaxGenerations = Integer.parseInt(Generations_t.getText());
    MaxTrials = Integer.parseInt(Trials_t.getText());
    LifeSpan = Integer.parseInt(LifeSpan_t.getText());
  }
  private void BeginTrial () {
    int i;
    U = new Universe(dbuffer,MX,MY,blocksize);
    System.out.println("Generation: "
        +CurrentGeneration+" Trial: "+CurrentTrial+" Best Score = "+BestScore);
    Generation_l.setText("Generation:"+CurrentGeneration);
    Trial_l.setText("Trial:"+CurrentTrial );
//    System.out.println("DNA is "+dna_length+" long.");

    if ( CurrentGeneration == 0 ) {
      PrepExperiment();
      SmartBlock.Initialize(U,dna_length, Sleep_Time);
      System.out.println("Initial Generation.");
      // Copy the originating DNA to a buffer up here.
      for (i=0;i<dna_length; i++) {
        OriginalDNA[i] = SmartBlock.DNA[i];
                    // for lack of a better choice,
                    // initialize "the best" as the original.
        BestDNA[i] = SmartBlock.DNA[i];
      }
      CurrentTrial = MaxTrials; // stop after one trial, initially.
      // and we'll measure the success of this original generation.
    } else if (CurrentGeneration == -1) {
       CurrentTrial = 0;
        for (i=0;i<dna_length; i++) {
          SmartBlock.DNA[i] = OriginalDNA[i];
        }
    } else {
      // After the first generation, we'll be copying the Current DNA down.
      for (i=0;i<dna_length; i++) {
        SmartBlock.DNA[i] = OriginalDNA[i];
      }
      CurrentTrial++;
      //  Copy the Current DNA set into the smartblock creature,
      //  and mutate it.
      SmartBlock.Mutate(.1);
    }

    //  And run that.

    BuildWalls(U);
    Clock = LifeSpan;

    SmartBlock.RunExperiment = 0;
    if (runner == null) {
      runner = new Thread(this);
      BuildCreature();
      SmartBlock.RunExperiment = 1;
      runner.start();
    }
  }

  private void EndTrial () {
    SmartBlock.RunExperiment = 0;
    int i = 0;
    U = null;

    if (Evaluate() > BestScore) {
      BestScore = Evaluate();
      System.out.println("This run did the best: " +BestScore);
      for (i = 0; i< dna_length;i++) {
        BestDNA[i] = SmartBlock.DNA[i];
      }
    }
    // carry the Best DNA into the Original buffer,
    // and we'll use that as the basis for further evolution.
    // Here's the real "natural" selection piece.
    if (CurrentTrial >= MaxTrials ) {
      System.out.println("Passing up the run with score: "+BestScore);
      if (PreviousBestScore < BestScore) {
        System.out.println("There's been improvement this generation.");
        PreviousBestScore = BestScore;
      } else {
        System.out.println("No improvement this generation.");
      }

      CurrentTrial = 0;  // reset to beginning.
      CurrentGeneration++;
      for (i = 0; i< dna_length;i++) {
        OriginalDNA[i] = BestDNA[i];
      }
    }
    for ( i = 0; i < Blocks; i++ ) {
      if (S[i] != null) {
//         System.out.println("Ending S[!"+i+"]");
         S[i].stop();
      }
//      S[i] = null;
    }
//    System.out.println("Ending!");
    if (runner != null ) {
      runner.stop();
    }
    runner = null;
    if ( CurrentGeneration <= MaxGenerations ) {
      BeginTrial();
    } else {
      // go into Stasis mode, where
      // we just run trials over and over with the "winning"
      // evolution.
      CurrentGeneration = -1;
      System.out.println("End of experiment:");
      ShowGenes();

//      BeginTrial();
    }
  }

  private void  ShowGenes () {
    int i;
    for ( i=0;i<dna_length;i++) {
     SmartBlock.ShowMove(SmartBlock.DNA[i]);
    }
  }
  private void Pause () {
    if ( Paused == 0 ) {
      Paused = 1;
      SmartBlock.Pause = 1;
      Pause.setLabel("Resume");
    } else {
      // resume
      Paused = 0;
      SmartBlock.Pause = 0;
      Pause.setLabel("Pause");
    }
  }
  private void Resume () {
    Paused = 0;
    SmartBlock.Pause = 0;
  }

  public void Clear () {
  // stop and clear out the current experiment.
     int i;
     SmartBlock.RunExperiment = 0;
     System.out.println("ex2.class : Stop");
      for ( i=0;i<Blocks;i++) {
        if ( S[i] != null ) {
          S[i].stop();
          S[i] = null;
        }
      }
    Blocks = 0;
    if (runner != null )   {
      runner.stop();
    }
    runner = null;
    CurrentGeneration = 0;
  }
  public boolean action(Event evt, Object o) {

    boolean ret_val = false;
    if (evt.target == Begin ) {
      BeginTrial();  // pause the creature.
      ret_val = true;
    } else
    if (evt.target == End ) {
      EndTrial();  // pause the creature.
      ret_val = true;
    } else  if (evt.target == Pause ) {
      Pause();  // pause the creature.
      ret_val = true;
    } else if (evt.target == ShowGenes ) {
      ShowGenes();
      ret_val = true;
    } else if (evt.target == Clear ) {
      Clear();
      ret_val = true;
    } else if (evt.target == Mutate ) {
      SmartBlock.Pause = 1;
      System.out.println("Mutate!");
      SmartBlock.Mutate(.1);
      SmartBlock.Pause = 0;
      ret_val = true;
    }
    return ret_val;
  }
}



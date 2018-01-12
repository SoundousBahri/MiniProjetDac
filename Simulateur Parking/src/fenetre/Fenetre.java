package fenetre;

import parking.Voiture;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

public class Fenetre  extends JFrame{

  public static final int WINDOW_WIDTH = 1000;
  public static final int WINDOW_HEIGHT = 670 ;
  public static final int ENTREX = 1000;
  public static final int ENTREY = 200;
  public static final int VOITURE_WIDTH = 200;
  public static final int VOITURE_HEIGHT = 100;
  public static final int BARIERE_X = 700;

  private JButton play;
  private JButton stop;
  private Pannau pannau;
  private boolean animation=false;
  private Thread animationThread;
  public ArrayList<Thread> threadList=new ArrayList<>();
  public Executor executor= Executors.newFixedThreadPool(2);
  private Thread voiture;

  public Fenetre(){
    initialiserFenetre();
    this.setVisible(true);
    play();
  }

  private void play() {


    voiture=new Thread(new Voiture(Voiture.Type.STANDARD));
    threadList.add(voiture);
    executor.execute(voiture);

    Thread voiture1=new Thread(new Voiture(Voiture.Type.STANDARD));
    threadList.add(voiture1);
    executor.execute(voiture1);
  }


  private void initialiserFenetre() {
    this.setTitle("Simulateur Garage");
    this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
    this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    this.setLocationRelativeTo(null);

    initialiserPanaux();
  }

  private void initialiserPanaux(){
    pannau = Pannau.getInstance();

    stop= new JButton("Stop");
    play= new JButton("Play");
    stop.addActionListener(new StopListener());
    play.addActionListener(new PlayListener());

    JPanel boutons = new JPanel();
    boutons.setLayout(new FlowLayout());
    boutons.add(stop);
    boutons.add(play);
    this.getContentPane().setLayout(new BorderLayout());
    this.getContentPane().add(pannau,BorderLayout.CENTER);
    this.getContentPane().add(boutons,BorderLayout.SOUTH);
  }

  class StopListener implements ActionListener{
    @Override
    public void actionPerformed(ActionEvent e) {
      animation=false;
      ((JButton)e.getSource()).setEnabled(false);
      pannau.suspend();
      play.setEnabled(true);
    }
  }

  class PlayListener implements ActionListener{

    @Override
    public void actionPerformed(ActionEvent e) {
      animation=true;
      ((JButton)e.getSource()).setEnabled(false);
      stop.setEnabled(true);
      pannau.resume();
    }
  }

  class animation implements Runnable{
    @Override
    public void run() {
      play();
    }
  }

}

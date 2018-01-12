package fenetre;

import parking.Controleur;
import parking.Voiture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;

public class Pannau extends JPanel{

  private static final int ECART = 70;
  private ArrayList<Point> cordonnesVoitures = new ArrayList<>();
  private int stopX=Fenetre.BARIERE_X;//cette cordonnée va etre utilisé pour arréter la voiture dans la fil d'attente
  private Controleur controleur= new Controleur();
  private Map<Integer,Point> cordonnesPlaces=new HashMap<>();
  private Graphics graphics;
  private ArrayList<Point> voitureVerticales=new ArrayList<>();
  private Condition condition = controleur.lock.newCondition();
  private boolean suspended=false;
  public synchronized void suspend(){suspended=true; notify();}
  public synchronized void resume() {suspended = false; notify(); }

  private static Pannau ourInstance = new Pannau();
  private boolean move=true;

  public static Pannau getInstance() {
    return ourInstance;
  }

  private Pannau(){
    //TODO need to be verified according to the background
    cordonnesPlaces.put(0,new Point(0,0));
    cordonnesPlaces.put(1,new Point(120,0));
  }

  @Override
  protected void paintComponent(Graphics g) {
    graphics=g;
    super.paintComponent(g);

    //dessiner le background
    Image background;
    try {
     background= ImageIO.read(new File("images\\Background.jpg"));
     g.drawImage(background,0,0,this);
    } catch (IOException e) {
      e.printStackTrace();
    }

    //dessiner les voitures
    for (int i=0; i<cordonnesVoitures.size(); i++){
      dessinerVoiture(cordonnesVoitures.get(i).x,cordonnesVoitures.get(i).y,g);
    }
    for (int i=0 ; i<voitureVerticales.size() ; i++){
      dessinerVoitureVerticale(voitureVerticales.get(i).x,voitureVerticales.get(i).y,g);
    }
  }

  private void dessinerVoitureVerticale(int x, int y, Graphics g) {
    //TODO
    Image voiture;
    try {
      voiture=ImageIO.read(new File("images\\voitureVertical.jpg"));
      g.drawImage(voiture,x,y,this);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void dessinerVoiture(int x, int y ,Graphics g) {
    //TODO
    Image voiture;
    try {
      voiture=ImageIO.read(new File("images\\voiture.jpg"));
      g.drawImage(voiture,x,y,this);
    } catch (IOException e) {
      e.printStackTrace();
    }

  }


  public synchronized void voitureArive(Voiture voiture) {
    controleur.addVoiture(voiture);//metre la voiture en attente
    Point point = new Point(Fenetre.ENTREX, Fenetre.ENTREY + Fenetre.VOITURE_HEIGHT);
    this.cordonnesVoitures.add(point);
    voiture.setPosition(point);
    moveLeft(point,stopX);//laisser l'espace au suivant
    if (stopX==Fenetre.BARIERE_X){
      stopX=Fenetre.BARIERE_X+Fenetre.VOITURE_WIDTH+10;
    }else {
      stopX=Fenetre.BARIERE_X;
    }

  }
  public void garerVoiture(Voiture voiture){
    Point point=voiture.getPosition();

    //voir si c'est possible de garer si non disparaitre
    int position = controleur.garerVoiture(voiture);
    System.out.println(position);
    controleur.removeVoiture();
    if (position>=0) {
      try {
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      if (point.x>Fenetre.BARIERE_X){
        moveLeft(point,Fenetre.BARIERE_X);
        try {
          Thread.sleep(3000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
      moveLeft(point, cordonnesPlaces.get(position).x);
      rotate(point);
      moveUP(point,0);
    }else {
      vanish(point);
    }
  }

  private void vanish(Point point) {
    rotate(point);
    moveUP(point,-Fenetre.VOITURE_WIDTH);
    repaint();
  }

  private void moveUP(Point point, int stopY) {
    while (point.y>stopY){
      point.y-=5;
      this.repaint();
      try {
        Thread.sleep(50);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  private void rotate(Point point) {
    cordonnesVoitures.remove(point);
    point.y-=ECART;
    voitureVerticales.add(point);
    repaint();
  }

  private void moveLeft(Point point, int x) {

    while (point.x>x){
      if(controleur.lock.tryLock()) {
          try {
            point.x -= 5;
            this.repaint();
            try {
              Thread.sleep(50);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          } catch (Exception ignored) {

          } finally {
            controleur.lock.unlock();
          }

          synchronized (this){
            while (!suspended){
              try {
                wait();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
          }
      }
    }

  }
  public Controleur getControleur() {
    return controleur;
  }

  public void arreter() {
    move=false;
  }
  public synchronized void resumer(){
    move=true;
    condition.signalAll();
  }

}

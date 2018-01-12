package parking;

import fenetre.Fenetre;
import fenetre.Pannau;

import java.awt.*;

public class Voiture implements Runnable{
  public static final int NOMBRE_VOITURES =10 ;
  private Point position;

  public Type getType() {
    return type;
  }

  public void setPosition(Point position) {
    this.position = position;
  }

  public Point getPosition() {
    return position;
  }

  public enum Type{
    STANDARD,HANDICAPE,ABONNE
  }

  private Type type;

  public Voiture(){
    type=Type.STANDARD;
  }

  public Voiture(Type type){
    this.type=type;
  }

  @Override
  public void run() {
    Pannau.getInstance().voitureArive(this);
    Pannau.getInstance().garerVoiture(this);
  }
}

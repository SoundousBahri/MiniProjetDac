package parking;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Controleur {
  private Voiture[] voituresEnAttente =new Voiture[2];
  private int index=0;
  private int nbrPlacesOccupe=0;
  public Lock lock = new ReentrantLock();
  public final Condition condition=lock.newCondition();

  public synchronized int garerVoiture(Voiture voiture){

    if (nbrPlacesOccupe==2){
      return -1;
    }
    if (nbrPlacesOccupe==1 && nestPasPrioritaire(voiture)) {
      return -1;
    }
    return nbrPlacesOccupe++;
  }

  private boolean nestPasPrioritaire(Voiture voiture) {
    if (voituresEnAttente[0]==null || voituresEnAttente[1]==null){
      return false;
    }
    if (voituresEnAttente[0].equals(voiture)){
      if (voiture.getType()!= Voiture.Type.ABONNE && voiture.getType()!= Voiture.Type.HANDICAPE && ( voituresEnAttente[1].getType()== Voiture.Type.HANDICAPE || voituresEnAttente[1].getType()== Voiture.Type.ABONNE)){
        return true;
      }
      return false;
    }else {
      if (voiture.getType()!= Voiture.Type.ABONNE && voiture.getType()!= Voiture.Type.HANDICAPE && ( voituresEnAttente[0].getType()== Voiture.Type.HANDICAPE || voituresEnAttente[0].getType()== Voiture.Type.ABONNE)){
        return true;
      }
      return false;
    }
  }


  public synchronized void addVoiture(Voiture v){
    voituresEnAttente[index++]=v;
  }
  public synchronized void removeVoiture(){
    voituresEnAttente[0]= voituresEnAttente[1];
    voituresEnAttente[1]=null;
    index--;
  }

  public boolean isPremiereVoiture(Voiture voiture) {
    return voituresEnAttente[0].equals(voiture);
  }
}

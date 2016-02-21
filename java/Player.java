import java.util.ArrayList;

public class Player{

  final private String name;
  final private int id;
  private ArrayList<Pile> hand;
  
  public Player(int number){
    id = number;
    name = "Player " + number;
    hand = new ArrayList<Pile>();
  }

}
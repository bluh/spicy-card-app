import java.util.ArrayList;

class VDeck{

  public static void main(String[] args){
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<Pile> deckAndDiscards = new ArrayList<Pile>();
    
    int numDecks = 1;
    
    //Populates the array of players
    int numPlayers = 3;
    for(int i = 0; i <= numPlayers-1; i++){
      players.add(new Player(i-1)); 
    }
    
    //creates a deck from numDecks of standard decks
    Pile deck = new Pile();
    for(int i = 0; i < numDecks; i++){
      for(int j = 0; j <= 3; j++){
        for(int k = 1; k <= 13; k++){
          deck.add(new Card(k, j));
        }
      }
    }
    deckAndDiscards.add(deck);
    
    
    //Drag and drop cards between piles
  
  }

}
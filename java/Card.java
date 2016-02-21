public class Card{

  private int rank;
  private int suit;
  private boolean faceUp;
  
  public Card(int rankValue, int suitValue){
    rank = rankValue;
    suit = suitValue;
    faceUp = false;
  }
  
  public int getRank(){
    return rank;
  }
  
  public int getSuit(){
    return suit; 
  }
  
  public boolean getFaceUp(){
    return faceUp;
  }
  
  public void flip(){
    if(faceUp == true)
      faceUp = false;
    else faceUp = true;
  }
  
  public String toString(){
    if(faceUp == true){
      String cardString;
      
      if(this.getRank()==1)
        cardString = "Ace";
      else if(this.getRank()==11)
        cardString = "Jack";
      else if(this.getRank()==12)
        cardString = "Queen";
      else if(this.getRank()==13)
        cardString = "King";
      else cardString = "" + this.getRank();
      
      if(this.getSuit()==0)
        cardString += " of Clubs";
      else if(this.getSuit()==1)
        cardString += " of Diamonds";
      else if(this.getSuit()==2)
        cardString += " of Hearts";
      else cardString += " of Spades";
      
      return cardString;
    }
    else return "Cards not faceUp";
  }
  
}
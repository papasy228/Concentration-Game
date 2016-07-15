/**
 * ConcentrationModel.java
 *
 * File:
 *	$Id$
 *
 * Revisions:
 *	$Log$
 */

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Observable;

/**
 * Class definition for the model of a concentration card game.
 *
 * @author: Arthur Nunes-Harwitt
 */


public class ConcentrationModel extends Observable {

    // The default size (of one side) of the board.
    public static final int BOARD_SIZE = 4;

    // The total number of cards assuming a square board.
    public static final int NUM_CARDS = BOARD_SIZE * BOARD_SIZE;

    // The number of pairs.
    public static final int NUM_PAIRS = NUM_CARDS / 2;

    // The object representing the back of a card.
    public static final CardBack SINGLETON_BACK = new CardBack();
    
    // total score
    private BigInteger score = new BigInteger("0");
    boolean online = false;
    public static LinkedHashMap<String, BigInteger> scoreList = null;
    
    /** 
     * Number of successive cards matched aka multiplier
     */
    private int successive =0;
    
    // 
    private int totalCardsMatched =0;
    /**
     * The undo stack for the game.
     */
    private ArrayList<Card> undoStack;

    /**
     * The cards for the game.
     */
    private ArrayList<Card> cards;
    
    /**
     * The number of moves made in the game, where a move is a card
     * selection.
     */
    private int moveCount;

    
    /**
     * Construct a ConcentrationModel object.
     * 
     */
    public ConcentrationModel() {
    	this.cards = new ArrayList<Card>();

    	for (int n = 0; n < NUM_PAIRS; ++n) {
    		Card card1 = new Card(n);
    		Card card2 = new Card(n);
    		this.cards.add(card1);
    		this.cards.add(card2);
    	}
    	
    	
    	isOnline();
    	reset();
    }

    /**
     * Push a card onto the undo stack.
     * 
     * @param card The card to push.
     */
    private void push(Card card) {
	undoStack.add(card);
    }
    private void isOnline(){
    	
    	try {
			this.online = InetAddress.getByName("google.com").isReachable(null, 0, 300);
		} catch (UnknownHostException e) {
			online = false;
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
    }
    public int checkIfHighScore(){
    	if(online){
    		final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
    		final String DB_URL = "jdbc:mysql://" + Env.DB_HOST + ":" + Env.DB_PORT + "/" +Env.DB_DATABASE;
    		final String USER = Env.DB_USERNAME;
    		final String PASS = Env.DB_PASSWORD;
    		scoreList = new LinkedHashMap<String, BigInteger>();
    		Connection conn = null;
    		   Statement stmt = null;
    		   try{
    		     
    		      Class.forName(JDBC_DRIVER);
    			
    		      System.out.println("Connecting to database...");
    		      conn = DriverManager.getConnection(DB_URL,USER,PASS);

    		      //STEP 4: Execute a query
    		      System.out.println("Creating statement...");
    		      stmt = conn.createStatement();
    		      DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
    			  Date date = new Date();
    			  String d = dateFormat.format(date);
    		      String sql;
    		      String q = 
    		    "INSERT INTO Concentration.Highscores VALUES ( '" +d+ "'," + score + ")";
    		      stmt.executeUpdate(q);
    		      sql = "SELECT * FROM Concentration.Highscores ORDER BY Score DESC LIMIT 100;";
    		      ResultSet rs = stmt.executeQuery(sql);

    		      
    		      while(rs.next()){
    		         //Retrieve by column name
    		         //int id  = rs.getInt("HighScoresID");
    		         String username = rs.getString("UserName");
    		         long score = rs.getLong("Score");
    		         BigInteger s = BigInteger.valueOf(score);  
    		         scoreList.put(username, s);
    		         
    		         	
    		         
    		      }
    		      if(scoreList.containsKey(d)){
    		    	  int i = 1;
    		    	  	for(String key : scoreList.keySet()){
    		    		  if(key == d){
    		    			  return i;
    		    		  }
    		    	  	}
    		      
		       
		         }else{
   		    	  return 0;
   		      	 }
		         	
    		      //STEP 6: Clean-up environment
    		      rs.close();
    		      stmt.close();
    		      conn.close();
    		   }catch(SQLException se){
    		      //Handle errors for JDBC
    		      se.printStackTrace();
    		   }catch(Exception e){
    		      //Handle errors for Class.forName
    		      e.printStackTrace();
    		   }finally{
    		      //finally block used to close resources
    		      try{
    		         if(stmt!=null)
    		            stmt.close();
    		      }catch(SQLException se2){
    		      }// nothing we can do
    		      try{
    		         if(conn!=null)
    		            conn.close();
    		      }catch(SQLException se){
    		         se.printStackTrace();
    		      }//end finally try
    		   }//end try
    	}
		return 0;
    }

    /**
     * Pop a card from the undo stack.
     * 
     * @param toggle Flag to indicate whether or not to toggle whether
     * the card is face-up or face-down.
     */
    private void pop(boolean toggle) {
	int s = undoStack.size();
	if (s > 0) {
	    Card card = undoStack.get(s-1);
	    undoStack.remove(s-1);
	    if (toggle) card.toggleFace();
	}
    }
    
    /**
     * Pop a card from the undo stack. (There are no parameters.)
     * 
     */    
    private void pop() {
	pop(false);
    }

    /**
     * Undo selecting a card.
     * 
     */    
    public void undo(){
	pop(true);
	setChanged();
	notifyObservers();

    }


    /**
     * Turn a card up.
     * 
     * @param n An integer referring to the nth card.
     *
     */
    private void add(int n) {
	Card card = cards.get(n);
	if (!card.isFaceUp()) {
	    card.toggleFace();
	    push(card);
	    ++this.moveCount;
	}
    }

    /**
     * Check to see if the two cards on the top of the undo stack have
     * the same value.
     * 
     */    
    private void checkMatch() {
	if (undoStack.size() == 2 && undoStack.get(0).getNumber() == undoStack.get(1).getNumber()) {
		score = score.add(BigInteger.valueOf( 50)) ;
		
	    successive +=1;
	    totalCardsMatched +=2;
	    calcMultiplyer();
	    pop();
	    pop();
	    checkIfGameOver();
	}
    }
    public boolean checkIfGameOver(){
    	if(totalCardsMatched == NUM_CARDS ){
    		//setChanged();
    		//notifyObservers();
    		return true;
    	}
    	return false;
    }
    public void calcMultiplyer(){
    	
    	switch(successive){
    	case 2: score =score.multiply(BigInteger.valueOf( 4));break;
    	case 3: score =score.multiply(BigInteger.valueOf(9));break;
    	case 4: score =score.multiply(BigInteger.valueOf(16));break;
    	case 5: score =score.multiply(BigInteger.valueOf( 25));break;
    	case 6: score =score.multiply(BigInteger.valueOf( 36));break;
    	case 7: score =score.multiply(BigInteger.valueOf( 49));break;
    	case 8: score =score.multiply(BigInteger.valueOf( 64));break;
    	case 9: score =score.multiply(BigInteger.valueOf( 72));break;
    	case 10: score =score.multiply(BigInteger.valueOf( 100));break;
    	case 11: score =score.multiply(BigInteger.valueOf( 121));break;
    	case 12: score =score.multiply(BigInteger.valueOf( 144));break;
    	case 13: score =score.multiply(BigInteger.valueOf( 169));break;
    	case 14: score =score.multiply(BigInteger.valueOf( 196));break;
    	case 15: score =score.multiply(BigInteger.valueOf( 225));break;
    	case 16: score =score.multiply(BigInteger.valueOf( 256));break;
    	}
    	
    }

    /**
     * Select a card to turn face up from cards.  If there are already
     * two cards selected, turn those back over.
     * 
     * @param n An integer referring to the nth card.
     *
     */
    public void selectCard(int n) {
    	if (0 <= n && n < NUM_CARDS) {
		
    		switch (undoStack.size()){
    		case 2:
    			undo();
    			undo();
    			successive =0;
    		case 0:
    			add(n);
    			break;
    		case 1:
    			add(n);
    			checkMatch();
    			break;
    		default:
    			throw new RuntimeException("Internal Error: undoStack too big.");
	    }
    		setChanged();
    		notifyObservers();
    	} else {

    	}
    }

    /**
     * Get the cards but only showing those that are face-up.
     *
     * @return An ArrayList containing the cards on the board.
     */    
    public ArrayList<CardFace> getCards() {
	ArrayList<CardFace> faces = new ArrayList<CardFace>();

	for (Card card : cards) {
	    if (card.isFaceUp()) {
		faces.add(card);
	    } else {
		faces.add(SINGLETON_BACK);
	    }
	}
	return faces;
    }

    /**
     * Get the cards showing them all.
     *
     * @return An ArrayList containing the cards on the board.
     */
    public ArrayList<CardFace> cheat() {
	ArrayList<CardFace> faces = new ArrayList<CardFace>();

	for (Card card : cards) {
	    faces.add(card);
	}
	return faces;
    }

    /**
     * Get the number of moves, i.e., the number of card
     * selections.
     *
     * @return An integer that represents the number of moves.
     */
    public int getMoveCount() {
	return this.moveCount;
    }


    /**
     * Reset the board.  All the cards are turned face-down and are
     * shuffled.  The undo stack and the number of moves are cleared.
     *
     */
    public void reset() {
	
	for (Card card : cards) {
	    if (card.isFaceUp()) {
		card.toggleFace();
	    }
	}

	Collections.shuffle(cards);
	

	this.undoStack = new ArrayList<Card>();

	this.moveCount = 0;
    this.score = BigInteger.valueOf(0);
    this.successive = 0;
    this.totalCardsMatched = 0;
	setChanged();
	notifyObservers();
    }

    /**
     * Return the number of cards currently selected.
     *
     * @return An integer that represents the number of cards
     * selected.
     */
    public int howManyCardsUp() {
	return undoStack.size();
    }
    
    public int isHighScore() {
    	return undoStack.size();
    }
    /**
     * Return the current score in the game.
     *
     * @return An integer that represents the current score
     */
    public BigInteger getScore(){
    	return score;
    }
}
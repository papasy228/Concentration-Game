import javax.swing.JButton;

/*
 * CardButton.java 
 *
 * Version:
 *     $Id:$
 * 
 * Revisions: 
 *     $Log:$ 
 *
 *
 *
 *@author  Papa Yaw Ntorinkansah 
 */

/**
 *	Class definition for a button that represents a card in the concentration game. 
 */

public class CardButton extends JButton {
	private int pos;
	
	
	public CardButton(int pos){
		this.pos = pos;
		
	}
	public int getPos(){
		
		
		return pos;
	}

	public static void main(String[] args) {
		

	}

}

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * CheatFrame.java 
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
public class CheatFrame extends JFrame {
	private ArrayList<CardButton> cardButtons;
	private int size;
	private JPanel grid;
	
	
	public CheatFrame(ArrayList<CardButton> cardButtons,int size){
		this.cardButtons = cardButtons;
		this.size = size;
		getContentPane().setLayout(new BorderLayout());
		setDefaultCloseOperation (JFrame.DISPOSE_ON_CLOSE);
		this.setSize(300, 400);
		setTitle("Cheat Concentration Game");
		
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (int) ((dimension.getWidth() - 450) / 2);
        int y = (int) ((dimension.getHeight() - 500) / 2);
        setLocation(x+500, y+50);
        
        int w = (int) ((GViewControl.guiSize-100)*0.75);
        int l = (int) ((GViewControl.guiSize)*0.75);
        setSize(w,l);
		
		
		
		
		showe();
		
	
	}
	public void showe(){
		//
		
		grid= new JPanel(new GridLayout(size,size));
		
		
		for(int i=0; i<cardButtons.size(); i++)
		{
			//System.out.println(i);
			grid.add(cardButtons.get(i));
		}
		
		add(grid,BorderLayout.CENTER);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		//CheatFrame c = new CheatFrame();

	}

}

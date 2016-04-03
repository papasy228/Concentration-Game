import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.UIManager;

/*
 * GViewControl.java 
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
public class GViewControl extends JFrame implements Observer  {
	public static final int guiSize = 500;
	private JButton reset = new JButton ("Reset");
	private JButton cheat = new JButton ("Cheat");
	private JButton undo = new JButton ("Undo");
	private JPanel panelTop;
	private JPanel panelChildLeft;
	private JPanel panelChildRight;
	private JPanel panelCenter;
	private JPanel panelBottom;
	private JLabel msgArea;
	private JLabel score;
	private JLabel scoreMsgArea;
	private Font ScoreFont = new Font("Quartz MS", Font.BOLD, 20);
	private ArrayList<CardButton> cheatButtons = new ArrayList<CardButton>();
	private ArrayList<CardButton> cButtons = new ArrayList<CardButton>();
	private static Color colors[] = {Color.YELLOW,
		Color.BLUE,
		Color.CYAN,
		Color.GRAY,
		Color.GREEN,
		Color.MAGENTA,
		Color.ORANGE,
		Color.PINK,
		Color.RED,
		Color.LIGHT_GRAY,
		Color.WHITE,
		Color.DARK_GRAY,
		Color.BLACK
};
	Color ogColor = UIManager.getColor("Button.background");		
	
	private ConcentrationModel m;

	
	public GViewControl(ConcentrationModel model){
		
		this.m = model;
		m.addObserver(this);
		panelTop = new JPanel(new BorderLayout());
		panelChildLeft = new JPanel(new BorderLayout());
		panelChildRight = new JPanel(new BorderLayout());
		panelCenter = new JPanel(new GridLayout(m.BOARD_SIZE,m.BOARD_SIZE));
		panelBottom = new JPanel(new FlowLayout(FlowLayout.TRAILING));
		msgArea = new JLabel();
		
		score = new JLabel("Score:");
		scoreMsgArea = new JLabel("000");
		scoreMsgArea.setBackground(Color.BLACK);
		scoreMsgArea.setFont(ScoreFont);
		scoreMsgArea.setForeground(Color.RED);
		setTitle("Concentration Game");
		getContentPane().setLayout(new BorderLayout());
		Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - 450) / 2);
        int y = (int) ((dimension.getHeight() - 500) / 2);
        setLocation(x, y);
        setSize(guiSize-100,guiSize);
		
		createCheatButtons();
		build();
		
		
	}
	public void createCheatButtons(){
		
		ArrayList<CardFace>  cf = m.cheat();
		cheatButtons = new ArrayList<CardButton>();				
		for(int c=0;c<m.NUM_CARDS;c++){
			cButtons.add(new CardButton(c));
			CardButton f = new CardButton(c);
			f.setText(""+ cf.get(c).getNumber());
			f.setBackground(colors[cf.get(c).getNumber() %13]);
			cheatButtons.add(f);
			
		}
		
		
		
	}
	public void createButtons(){
		
		for(int i=0;i<m.NUM_CARDS;i++){
			cButtons.get(i).setBackground(Color.WHITE);
			//cButtons.get(i).setBorderPainted(false);
			//cButtons.get(i).setContentAreaFilled(false);
			//cButtons.get(i).setOpaque(true);
			panelCenter.add(cButtons.get(i));
			cButtons.get(i).addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent a) {
					
					CardButton clkdButton =  (CardButton)a.getSource();
					m.selectCard(clkdButton.getPos());
			
				}
				
			 });
		}
		
	}
	public void build(){
		
        
        msgArea.setText("Moves: 0 Select the first card.");
        panelChildLeft.add(msgArea);
        panelChildRight.add(score, BorderLayout.LINE_START);
        panelChildRight.add(scoreMsgArea, BorderLayout.LINE_END);
        panelTop.add(panelChildLeft, BorderLayout.LINE_START);
        panelTop.add(panelChildRight, BorderLayout.LINE_END);
        createButtons();
        
        
        getContentPane();
        
        panelBottom.add(undo);
        undo.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent b) {
				
					m.undo();
					
					
			}
        	
        });
        
        
        panelBottom.add(cheat);
        cheat.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//System.out.println(m.BOARD_SIZE);
				//createCheatButtons();
				CheatFrame c = new CheatFrame(cheatButtons,m.BOARD_SIZE);
					
			}	
				
        });
        panelBottom.add(reset);
        reset.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				m.reset();
				
			}
        	
        });
        
        add(panelTop,BorderLayout.NORTH);
		add(panelCenter,BorderLayout.CENTER);
		add(panelBottom,BorderLayout.SOUTH);
        setVisible(true);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE); 
	}
	
	@Override
	public void update(Observable t, Object o) {
		//System.out.println("I am Here");
		createCheatButtons();
		ArrayList<CardFace>  c = m.getCards();
		for(int y=0;y<m.NUM_CARDS;y++){
			if(c.get(y).isFaceUp() ){
				
				cButtons.get(y).setText(""+ c.get(y).getNumber());
				cButtons.get(y).setBackground(colors[c.get(y).getNumber() %13]);
				
				String str ="Moves: " + m.getMoveCount();
				switch(m.howManyCardsUp()) {
				case 0: str +=" Select the first card."; break;
				case 1: str +=" Select the second card."; break;
				case 2: str +=" No Match: Undo or select a card."; break;
				}
				msgArea.setText(str);
				scoreMsgArea.setText(""+m.getScore());
				repaint();
				validate();
			}else{
				cButtons.get(y).setText("");
				cButtons.get(y).setBackground(Color.WHITE);
				
				String str ="Moves: " + m.getMoveCount();
				switch(m.howManyCardsUp()) {
				case 0: str +=" Select the first card."; break;
				case 1: str +=" Select the second card."; break;
				case 2: str +=" No Match: Undo or select a card."; break;
				}
				msgArea.setText(str);
				scoreMsgArea.setText(""+m.getScore());
				
				
				repaint();
				validate();
			}
				
			
		}
		
	}


	public static void main(String[] args) {
		
		GViewControl game = new GViewControl(new ConcentrationModel());
		
	}


}

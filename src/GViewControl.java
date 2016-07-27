import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;


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
	/**
	 * 
	 */
	private static final long serialVersionUID = -5995371630914787806L;
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
		panelCenter = new JPanel(new GridLayout(ConcentrationModel.BOARD_SIZE,ConcentrationModel.BOARD_SIZE));
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
		for(int c=0;c<ConcentrationModel.NUM_CARDS;c++){
			cButtons.add(new CardButton(c));
			CardButton f = new CardButton(c);
			f.setText(""+ cf.get(c).getNumber());
			f.setBackground(colors[cf.get(c).getNumber() %13]);
			cheatButtons.add(f);
			
		}
		
		
		
	}
	public void createButtons(){
		
		for(int i=0;i<ConcentrationModel.NUM_CARDS;i++){
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
				CheatFrame c = new CheatFrame(cheatButtons,ConcentrationModel.BOARD_SIZE);
					
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
		this.add(panelBottom,BorderLayout.SOUTH);
        setVisible(true);
        setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE); 
	}
	
	@Override
	public void update(Observable t, Object o) {
		
		//createCheatButtons();
		ArrayList<CardFace>  c = m.getCards();
		for(int y=0;y<ConcentrationModel.NUM_CARDS;y++){
			if(c.get(y).isFaceUp() ){
				
				cButtons.get(y).setFont(new Font("Arial", Font.PLAIN, 16));
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
				scoreMsgArea.setText(""+m.getScore().toString());
				
				
				repaint();
				validate();
			}
			
				
			
		}
		if(m.checkIfGameOver()){
			URL url = null;
			try {
				 url = GViewControl.class.getResource("resources/animation.gif");
				 
			} catch (Exception e) {
				e.printStackTrace();
			}
				ImageIcon Icon = new ImageIcon(url);
			
				JOptionPane.showMessageDialog(this.getContentPane(),
				    "You won in " + m.getMoveCount() + " moves \n "
				    + "Your score is " + m.getScore().toString(),
				    "Congratulations!! You Won!!",
				    JOptionPane.INFORMATION_MESSAGE,
				    Icon);
		     //to do check high score
				
				if(m.checkIfHighScore() !=0){
					JFrame HighScoreframe = new JFrame();
					HighScoreframe.setSize(500,120);
					JPanel HighScorepanel = new JPanel();
					JTable table = new JTable(m.getRowData(), m.getColumnNames());
					System.out.println("table heignt " +table.getRowHeight());
					table.setFillsViewportHeight(true);
					JScrollPane jsp = new JScrollPane(table);
					HighScorepanel.setLayout(new BorderLayout());
					HighScorepanel.add(jsp,BorderLayout.CENTER);
					HighScoreframe.setContentPane(HighScorepanel);
					HighScoreframe.setVisible(true);
					//set pos
					//high light
					//set column
					//make table uneditable
				}else{
					System.out.println("check if highscore returned? " );
				}
		     
		}
		
	}


	public static void main(String[] args) {
		
		GViewControl game = new GViewControl(new ConcentrationModel());
		
		
	}


}

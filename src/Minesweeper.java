import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;


/*
 * Rameen Rastan-Vadiveloo ( 7191863 ) 
 * Uzair Nami ( 7206240 ) 
 * 
 * Assignment 4: Enhanced Minesweeper
 * 
 */

public class Minesweeper implements ActionListener, Serializable {
	
	//JFrame (frame/window), titled "Enhanced Minesweeper"
	JFrame frame = new JFrame("Enhanced Minesweeper");
	
	//creating a 2D array for the buttons on the board (10x10 dimensions)
	JButton[][] buttons = new JButton[10][10];
	
	//creating a button for resetting and saving the game
	JButton reset = new JButton("Reset");
	JButton save = new JButton("Save");
	
	//button to access score list, creating an array for score text and an array for score values, and JFrame for the score list window
	JButton scoreList = new JButton("Scores");
	GridLayout highScoreGrid = new GridLayout(10,1);
	JLabel[] highScores = new JLabel[10];
	JFrame scoreDisplay = new JFrame("Top 10 Scores");
	int[] scoreValues = new int[10];
	
	//2D array for the integer values stored in the buttons
	static int[][] counts = new int[10][10]; 
	
	//container (the board)
	Container board = new Container();
	
	//integer values assigned to the different resources for the game
	final int MINE = 10;
	int lifeCount = 3;
	final int SHIELD = 12;
	final int INVINCIBILITY = 50;
	final int PROBE = 25;
    final int BONUS=11;
	//2D array to see when a mine is already touched, used to help probe detect only non-clicked mines
	boolean[][] mineOff = new boolean[10][10];
	//click count to keep track of score (less clicks is better)
	static int clickCount=0;
	
	//label for life count to be displayed
	JLabel lives = new JLabel();
	
	
	//constructor
	public Minesweeper(){
		
	//set size of frame
		frame.setSize(750,750);
		
		
		//adding area to show life count on the bottom of the frame
		frame.add(lives, BorderLayout.SOUTH);
		lives.setIcon(new ImageIcon("heart.png"));
		
		
		//set lay-out and background color
		frame.setLayout(new BorderLayout());  //layout manager
		frame.getContentPane().setBackground(Color.darkGray);
		
		//add action listener to the buttons
		reset.addActionListener(this);
		scoreList.addActionListener(this);
		save.addActionListener(this);
		
		//set colors and font for buttons and position on frame
		reset.setBackground(Color.darkGray);
		frame.add(reset, BorderLayout.NORTH);
		reset.setForeground(Color.white);
		reset.setFont(new Font("Serif", Font.PLAIN, 20));
		
		save.setBackground(Color.darkGray);
		frame.add(save, BorderLayout.WEST);
		save.setForeground(Color.white);
		save.setFont(new Font("Serif", Font.PLAIN, 20));
		
		scoreList.setBackground(Color.darkGray);
		frame.add(scoreList, BorderLayout.EAST);
		scoreList.setForeground(Color.white);
		scoreList.setFont(new Font("Serif", Font.PLAIN, 20));
		
		//add life count display to the bottom of the frame, set color and font for the life count display
		frame.add(lives, BorderLayout.SOUTH);
		lives.setText("Lives: " + lifeCount +  "    Clicks: "+clickCount);
		lives.setForeground(Color.green);
		lives.setFont(new Font("Serif", Font.PLAIN, 24));	
		
			
		
				
		//giving the board a grid layout with 10x10 dimension
		board.setLayout(new GridLayout(10,10));
		
		
		//nested for-loop to access each button
		for( int i=0;i<buttons.length;i++){
			 
			for(int j=0;j<buttons.length;j++){
				
				//making each button on the board an interactive JButton
				buttons[i][j] = new JButton();
				
				//adding action listener to each button on the board
				buttons[i][j].addActionListener(this);
				
				//adding the button to the board
				board.add(buttons[i][j]);
				buttons[i][j].setBackground(Color.white);
				
				
				
			}
			
		}
		//adding board to center portion of the frame
		frame.add(board,BorderLayout.CENTER);
		
		
		//spawns mines and prizes on the board
		setMines();
		spawnMines();
		spawnBonus();
		spawnProbes();
		spawnShields();
		spawnInvincibility();
		
		
         //making program terminate when frame is closed
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		//making frame visible on the screen
		frame.setVisible(true);
		
		
	}
	//outside of constructor
	
	
	
	
	
	
	//method to randomly place objects (used in methods to spawn mines, shields, probes, invincibility)
	static void placeObjectRandomly(int[][] arr, int value) {
		
		
	    Random rand = new Random();
	    
	    
	    //generates random values for the 2 indexes of the 2D array, used to spawn a resource at this random location
	    int i = rand.nextInt(arr.length);
	    int j = rand.nextInt(arr.length);
	    counts[i][j]=value;
	
	    
	}

	
	//method to spawn shields randomly on the board, +3 life when clicked
			public void spawnShields(){

				for(int s=0;s<2;s++){
				placeObjectRandomly(counts, SHIELD);
				}
				
				
			
			}
			
			
			//method to spawn an invincibility token randomly. Only a 35% chance of occurring each game.
			public void spawnInvincibility(){

				 Random rand = new Random();
				 
				 //generates random value between 0 to 1 to decide if an invincibility token will be spawned or not
				 double k = rand.nextDouble();
				 
				 if(k<0.70){
					 for(int i=0;i<1;i++){
				placeObjectRandomly(counts, INVINCIBILITY);
				
					 }
				 }
			
				
			}
			
			
			
			//method to spawn probes randomly. detects a random mine on the board when clicked
			public void spawnProbes(){
				
				for(int p=0;p<3;p++){
					placeObjectRandomly(counts, PROBE);
					}
			
				
			}
			
			//method to spawn bonuses, reduces click count
           public void spawnBonus(){
				
				for(int b=0;b<3;b++){
					placeObjectRandomly(counts, BONUS);
					}
			
				
			}
           
			


			//method to spawn mines randomly on the board
			public void spawnMines(){
			
			
				for(int m = 0; m < 9; m++){
					placeObjectRandomly(counts,MINE);
					
				}
	
				
				
				//nested for-loop which checks for the buttons that are next to minds, and incrementing
				//their value based on how many mines they are adjacent to
				for (int i = 0; i < counts.length; i++) {
						for (int j = 0; j < counts[0].length; j++) {
							if (counts[i][j] != MINE) {
								int adjacentCount = 0;
								if(i > 0 &&  j > 0 && counts[i-1][j-1] == MINE) { //top left
									adjacentCount++;
								}
								if (j > 0 && counts[i][j-1] == MINE) { //top
									adjacentCount++;
								}
								if (i < counts.length-1 && j > 0 && counts[i+1][j-1]==MINE) { //top right
									adjacentCount++;
								}
								if (i < counts.length-1 && counts[i+1][j] == MINE) { //right
									adjacentCount++;
								}
								if (i < counts.length-1 && j <counts[0].length-1 && counts[i+1][j+1] == MINE) { //bottom right
									adjacentCount++;
								}
								if (j < counts[0].length-1 && counts[i][j+1] == MINE) { //bottom
									adjacentCount++;
								}
								if (i > 0 && j < counts[0].length-1 && counts[i-1][j+1] == MINE) { //bottom left
									adjacentCount++;
								}
								if (i > 0 && counts[i-1][j] == MINE) { //left
									adjacentCount++;
								}
								
								counts[i][j] = adjacentCount;
								}
						}
					}
					
					
				}
	
	
			
			
	//main method runs the minesweeper program by creating a default object of the class
	public static void main(String[] args){
		
		//enhanced minesweeper object
		new Minesweeper();
	}
	
	
	//method that is called when a mine is clicked (Reduces life by 1, game over if life is 0. 
	public void mineClicked() {
		
		
		lifeCount--;
		lives.setText("Lives: " + lifeCount +  "    Clicks: "+clickCount);
		
		
		//color of life count changes based on how many lives are left
		if (lifeCount == 3) {
			lives.setForeground(Color.green);
		}
		
		if (lifeCount == 2){
			lives.setForeground(Color.cyan);
		}
		else if (lifeCount == 1) {
			lives.setForeground(Color.red);
		}
		
		
		//life count = 0 means game is over
		else if(lifeCount == 0){
		
		
			//reveals entire board when game is over (nested for-loop to reveal every button)
		for (int i = 0; i < buttons.length; i++){
			for (int j = 0; j < buttons[0].length; j++) {
				buttons[i][j].setBackground(Color.black);
				if (buttons[i][j].isEnabled()) {
					if(counts[i][j] != MINE && counts[i][j] != SHIELD && counts[i][j] != INVINCIBILITY && counts[i][j] != PROBE && counts[i][j] != BONUS) {
						
						buttons[i][j].setText(counts[i][j] + "");
						buttons[i][j].setEnabled(false);
					}
					else if (counts[i][j] == SHIELD) {
						buttons[i][j].setIcon(new ImageIcon("shield.png"));
						
						buttons[i][j].removeActionListener(this);
					}
					else if (counts[i][j] == INVINCIBILITY) {
						buttons[i][j].setIcon(new ImageIcon("invincibility.png"));
						
						
						buttons[i][j].removeActionListener(this);
					}
					else if (counts[i][j] == PROBE) {
						buttons[i][j].setIcon(new ImageIcon("probe.png"));
						
						buttons[i][j].removeActionListener(this);
					}
					else if (counts[i][j] == BONUS) {
						buttons[i][j].setIcon(new ImageIcon("bonus.png"));
						
						buttons[i][j].removeActionListener(this);
					}

					else {
						buttons[i][j].setBackground(Color.red);
						buttons[i][j].setIcon(new ImageIcon("mine.png"));
					
						buttons[i][j].removeActionListener(this);
						
						}
					}
				}	
			}
		//displayed when user loses
		JOptionPane.showMessageDialog(frame, "Sorry you are out of lives, better luck next time.");
		}
	}
	//method called when shield is clicked. +3 life
	public void shieldClicked() {
		
		lifeCount = lifeCount + 3;
		lives.setText("Lives: " + lifeCount +  "    Clicks: "+clickCount);
		
		if (lifeCount > 3) {
			lives.setForeground(Color.blue);
		}
		JOptionPane.showMessageDialog(frame, "Shield clicked! +3 Life.");
	
	}
	//method called when invincibility is clicked. +100 life (Invincible for the round)
 	public void invincibilityClicked(){
 	
		
		
		lifeCount=lifeCount+100;
		lives.setText("Lives: " + lifeCount +  "    Clicks: "+clickCount);
		
		if (lifeCount > 3) {
			lives.setForeground(Color.blue);
		}
		JOptionPane.showMessageDialog(frame, "Invincibility clicked! Invincible for the remainder of the round.");
	}
 	
 	//called when a bonus is clicked, reduces click count by 1
	public void bonusClicked(){
 	
         clickCount=clickCount-2;	
         lives.setText("Lives: " + lifeCount +  "    Clicks: "+clickCount);
		
		
		JOptionPane.showMessageDialog(frame, "Bonus clicked! -1 Clicker count.");
	}
 	
 
 	
 	
 	
 	//to be utilized in probe method, 2D boolean array, sets all elements as ''false'' by default
 	//when a mine is clicked, the corresponding position in this array will go to true (mine off)
 	//allows the probe to skip the detection of mines that have already been activated
 	public void setMines(){
 		
 		for(int i=0;i<mineOff.length;i++){
 			 
 			for(int j=0;j<mineOff.length;j++){ 
 				
 				mineOff[i][j]=false;
 				
 			}
 			
 	 	
 	 	
 	 	}
 		
 		
 	}
 	
 	
 	
 	
 	
 	
 	//method called when a probe is clicked. detects a mine
 	  public void probeClicked(){
 		
 		
 		//nested for-loop to access each button on the board and revealing the first mine that it encounters.
 		for (int i = 0; i < counts.length; i++) {
			for (int j = 0; j < counts[0].length; j++) {
				
				if (counts[i][j]==MINE &&  mineOff[i][j]==false && buttons[i][j].isEnabled()==true ){
					buttons[i][j].setBackground(Color.green);
					buttons[i][j].setIcon(new ImageIcon("mine.png"));
					buttons[i][j].removeActionListener(this);
					mineOff[i][j]=true;
					JOptionPane.showMessageDialog(frame, "Probe clicked! 1 Mine has been revealed.");
					return;
					
					
				}
			
				
				
			}
 		}
 		
 		
 		
 	}
 	  
 	  

 		
 	//method to add a score to the list after a game is finished
 		public void addScoreToList(){
 			
 			for(int i=0;i<10;i++){
 				if(clickCount<scoreValues[i]){
 					
 					scoreValues[i]=scoreValues[i+1];
 					clickCount=scoreValues[i];
 					highScores[i] = new JLabel("Score: "+clickCount+ " clicks");
 					scoreDisplay.add(highScores[i]);
 					
 				}
 				
 				else if(highScores[i] == null){
 					
 					highScores[i] = new JLabel("Score: "+clickCount+" clicks");
 					scoreDisplay.add(highScores[i]);
 					return;
 					
 					
 				}
 				else{
 					
 				}
 				
 			}
 			
 		}
 		
 		//top 10 score display window
 			//method is called when score button is clicked
 			public void displayScores(){
 				  
 				
 				  scoreDisplay.setVisible(true);
 				  scoreDisplay.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
 				  scoreDisplay.setSize(300, 300);
 				  scoreDisplay.setLayout(highScoreGrid);
 				  scoreDisplay.setResizable(false);
 				  
 				  
 				 }
	
 			
 			
 	//method to reveal blanks when a 0 is pressed
	public void revealBlanks(ArrayList<Integer> revealer) { 
		
		
		//base case
		if (revealer.size() == 0) {
			return;
		}
		
		else {
			
			int i = revealer.get(0) / 100;
			int j = revealer.get(0) % 100;
			
			revealer.remove(0);
			
			
			
			//will check all adjacent buttons for neighboring empty buttons (that don't contain mines or prizes), and reveals them if they are empty
				if (i > 0 && j > 0 && buttons[i-1][j-1].isEnabled() && counts[i-1][j-1] != SHIELD && counts[i-1][j-1] != INVINCIBILITY && counts[i-1][j-1] != PROBE && counts[i-1][j-1] != BONUS){ 
					//top left
					buttons[i-1][j-1].setText(counts[i-1][j-1] + "");
					buttons[i-1][j-1].setBackground(Color.black);
					buttons[i-1][j-1].setEnabled(false);
					
					if(counts[i-1][j-1] == 0) {
						revealer.add((i-1) * 100 + (j-1));
					}
					
				}
				if(j > 0 && buttons[i][j-1].isEnabled()  && counts[i][j-1] != SHIELD && counts[i][j-1] != INVINCIBILITY && counts[i][j-1] != PROBE && counts[i][j-1] != BONUS ) { 
					//top
					buttons[i][j-1].setText(counts[i][j-1] + "");
					buttons[i][j-1].setBackground(Color.black);
					buttons[i][j-1].setEnabled(false);
					if(counts[i][j-1] == 0) {
						revealer.add((i) * 100 + (j-1));
					}			
					
				}
				if (i < counts.length - 1 && j > 0 && buttons[i+1][j-1].isEnabled() && counts[i+1][j-1] != SHIELD && counts[i+1][j-1] != INVINCIBILITY && counts[i+1][j-1] != PROBE && counts[i+1][j-1] != BONUS) {
					//top right
					buttons[i+1][j-1].setText(counts[i+1][j-1] + "");
					buttons[i+1][j-1].setBackground(Color.black);

					buttons[i+1][j-1].setEnabled(false);
					if(counts[i+1][j-1] == 0) {
						revealer.add((i+1) * 100 + (j-1));
					}
				}
				if (i > 0 && buttons[i-1][j].isEnabled() && counts[i-1][j] != SHIELD && counts[i-1][j] != INVINCIBILITY && counts[i-1][j] != PROBE && counts[i-1][j] != BONUS) {
					// left
					buttons[i-1][j].setText(counts[i-1][j] + "");
					buttons[i-1][j].setBackground(Color.black);
					buttons[i-1][j].setEnabled(false);
					if(counts[i-1][j] == 0) {
						revealer.add((i-1) * 100 + j);
					}
					
				}
		
				if (i < counts.length - 1 && buttons[i+1][j].isEnabled() && counts[i+1][j] != SHIELD && counts[i+1][j] != INVINCIBILITY && counts[i+1][j] != PROBE && counts[i+1][j] != BONUS) {
					//right
					buttons[i+1][j].setText(counts[i+1][j] + "");
					buttons[i+1][j].setBackground(Color.black);
					buttons[i+1][j].setEnabled(false);
					if(counts[i+1][j] == 0) {
						revealer.add((i+1) * 100 + j);
					}
				}
				if (i > 0 && j < counts[0].length - 1 && buttons[i-1][j+1].isEnabled() && counts[i-1][j+1] != SHIELD && counts[i-1][j+1] != INVINCIBILITY && counts[i-1][j+1] != PROBE && counts[i-1][j+1] != BONUS) { 
					//bottom left
					buttons[i-1][j+1].setText(counts[i-1][j+1] + "");
					buttons[i-1][j+1].setBackground(Color.black);
					buttons[i-1][j+1].setEnabled(false);
					if(counts[i-1][j+1] == 0) {
						revealer.add((i-1) * 100 + (j+1));
					}
					
				}
				if(j < counts[0].length - 1 && buttons[i][j+1].isEnabled() && counts[i][j+1] != SHIELD && counts[i][j+1] != INVINCIBILITY && counts[i][j+1] != PROBE && counts[i][j+1] != BONUS ) { 
					//bottom
					buttons[i][j+1].setText(counts[i][j+1] + "");
					buttons[i][j+1].setBackground(Color.black);
					buttons[i][j+1].setEnabled(false);
					if(counts[i][j+1] == 0) {
						revealer.add((i) * 100 + (j+1));
					}			
					
				}
				if (i < counts.length - 1 && j < counts[0].length - 1 && buttons[i+1][j+1].isEnabled() && counts[i+1][j+1] != SHIELD && counts[i+1][j+1] != INVINCIBILITY && counts[i+1][j+1] != PROBE&& counts[i+1][j+1] != BONUS ) { 
					//bottom right
					buttons[i+1][j+1].setText(counts[i+1][j+1] + "");
					buttons[i+1][j+1].setBackground(Color.black);
					buttons[i+1][j+1].setEnabled(false);
					if(counts[i+1][j+1] == 0) {
						revealer.add((i+1) * 100 + (j+1));
					}
				}
					
				
	//method calls itself (recursion), this is so that whenever a new 0 is discovered it will continue recursively check for empty neighbooring buttons (that don't contain mines or prizes)
				buttons[i][j].setBackground(Color.black);
				revealBlanks(revealer);
		}
	}
	
	
	
	
	
	//method to save the game in a file, file goes into the project folder
		public void saveCurrentBoard(){
			
			 ObjectInputStream ois = null;
			 Minesweeper game = new Minesweeper();
			 
			 
			//saves file
	         ObjectOutputStream oos = null;
	         
	         try{
	         //naming the file
	       	  oos = new ObjectOutputStream(new FileOutputStream("game"));
	       	  oos.writeUTF("Game state");
	       	  //write object into file
	       	  oos.writeObject(game);
	       	  //closing stream
	       	  oos.close();
	         }
	         
	         //in case of input/output exception
	         catch(IOException e){
	       	  
	       	  
	         }
	         
			 
			
			
			
		}
		
		//method to load a previous board (**note**: attempted / not implemented in the game)
		public void loadPreviousBoard(){
			  
			  ObjectInputStream ois = null;
			  String reader = "";
			  Minesweeper minesweeper;
			  
			  try{
			   
			   ois = new ObjectInputStream(new FileInputStream("game"));
			   reader = ois.readUTF();
			   
			   try{
			    
			    while(true){
			    minesweeper = (Minesweeper)ois.readObject();
			    }
			   }
			   catch(ClassNotFoundException e){
			    
			    System.err.println("Class not found.");
			   }
			   catch(FileNotFoundException e){
			    
			    System.err.println("Binary file not found.");
			   }
			   catch(EOFException e){
			    
			    System.out.println("Game-state loaded.");
			   }
			  }
			  catch(IOException e){
			   
			   System.err.println("Unable to load binary file.");
			  }
			 }
	

	
	
	
	
	
	//method called if all non-mine areas are cleared (results in win)
	public void winCondition() {
		
		boolean win = true;
		for (int i = 0; i <counts.length; i++) {
			for (int j = 0; j < counts[0].length; j++) {
				if (counts[i][j] != MINE && counts[i][j] != SHIELD && counts[i][j] != INVINCIBILITY &&counts[i][j] != PROBE && counts[i][j] != BONUS && buttons[i][j].isEnabled() == true) {
					win = false;
				}
			}
		}
		if (win == true) {
			JOptionPane.showMessageDialog(frame, "Congratulations, you are victorious! Score: "+clickCount+" clicks.");
			addScoreToList();
			
		}
	}
	
	
	//performs an event based on the action that was performed (the value that was clicked)
	public void actionPerformed(ActionEvent event) {
	    clickCount=clickCount+1;
		lives.setText("Lives: " + lifeCount +  "    Clicks: "+clickCount);
		//if reset is clicked on the frame
		if(event.getSource().equals(reset)) { // resets board
			for (int i = 0; i < buttons.length; i++) {
				for (int j = 0; j < buttons[0].length; j++){
					
					//resets to default state
					buttons[i][j].setEnabled(false);
					buttons[i][j].setEnabled(true);
					buttons[i][j].setText("");
					lifeCount=3;
					clickCount=0;
					lives.setText("Lives: " + lifeCount +  "    Clicks: "+clickCount);
					frame.add(lives, BorderLayout.SOUTH);
					lives.setForeground(Color.green);
					buttons[i][j].setIcon(null);
					buttons[i][j].setBackground(Color.white);
					buttons[i][j].removeActionListener(this);
					buttons[i][j].addActionListener(this);
					
					
				}
			}
			//spawns new resources after reset
			setMines();
			spawnMines();
			spawnBonus();
			spawnProbes();
			spawnShields();
			spawnInvincibility();
		}
		//display scoreboard if score buttin is clicked
		else if (event.getSource().equals(scoreList)){
			
			displayScores();
		}
		
		//saves game as file in the project folder if the save button is clicked
		else if(event.getSource().equals(save)){
			
			saveCurrentBoard();
		}
	
		
		
		else {
			//nested for loop to access each button on the board
			for (int i = 0; i < buttons.length; i++) {
				for (int j = 0; j < buttons[0].length; j++) {
					//if-statement to see what the source of the event of the button clicked corresponds to
					
					if (event.getSource().equals(buttons[i][j])){
						buttons[i][j].setBackground(Color.black);	
						//if mine is clicked, mine clicked method is called and the mine is revealed and disabled
						if (counts[i][j] == MINE) {
							buttons[i][j].setIcon(new ImageIcon("mine.png"));
							buttons[i][j].setBackground(Color.red);
							buttons[i][j].removeActionListener(this);
						    mineOff[i][j]=true;
							mineClicked();
						}
						
						//if shield is clicked, shield clicked method is called and the shield is revealed and disabled
						else if (counts[i][j] == SHIELD) {
							buttons[i][j].setIcon(new ImageIcon("shield.png"));
							buttons[i][j].setBackground(Color.cyan);
							buttons[i][j].removeActionListener(this);
							shieldClicked();
						}
						//if invincibility is clicked, invincibility clicked method is called and the token is revealed and disabled
						else if (counts[i][j] == INVINCIBILITY) {
							buttons[i][j].setIcon(new ImageIcon("invincibility.png"));
							
							buttons[i][j].removeActionListener(this);
							invincibilityClicked();
							
						}
						//if probe is clicked, probe clicked method is called and the probe is revealed and disabled
						else if (counts[i][j] == PROBE) {
							buttons[i][j].setIcon(new ImageIcon("probe.png"));
							
						
							buttons[i][j].removeActionListener(this);
							probeClicked();
							
						}
						//if bonus is clicked, bonus clicked method is called and the bonus is revealed and disabled
						else if (counts[i][j] == BONUS) {
							buttons[i][j].setIcon(new ImageIcon("bonus.png"));
							buttons[i][j].setBackground(Color.yellow);
						
							buttons[i][j].removeActionListener(this);
							bonusClicked();
							
						}
						//if no mines are next to an empty button, reveal blanks method is called (reveals other 0's and buttons adjacent to 0's)
						//win condition is called, user wins if all non-mine/prize buttons are revealed (checks for win)
						else if(counts[i][j]==0){
						
							buttons[i][j].setText(counts[i][j] + "");
							buttons[i][j].setEnabled(false);
							ArrayList<Integer> revealer = new ArrayList<Integer>();
							revealer.add(i*100+j);
							revealBlanks(revealer);
							winCondition();
						}
					
						
						else {
							
							//win condition is called, user wins if all non-mine/prize buttons are revealed (checks for win)
							buttons[i][j].setText(counts[i][j] + "");
							buttons[i][j].setEnabled(false); //disables user from pressing the same button again
							winCondition();	
						}
					} 
				}
			}
		}
	}
	
}
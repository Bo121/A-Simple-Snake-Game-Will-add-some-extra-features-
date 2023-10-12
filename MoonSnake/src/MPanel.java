import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import javax.sound.sampled.*;

public class MPanel extends JPanel implements KeyListener, ActionListener{
	ImageIcon title = new ImageIcon("title.jpg");
	ImageIcon body = new ImageIcon("body.png");
	ImageIcon up = new ImageIcon("up.png");
	ImageIcon down = new ImageIcon("down.png");
	ImageIcon left = new ImageIcon("left.png");
	ImageIcon right = new ImageIcon("right.png");
	ImageIcon food = new ImageIcon("food.png");
	ImageIcon poisonous = new ImageIcon("PoApple.png");
	
	int len = 3; // initialize the length of the snake to be 3
	int score = 0; // record the score
	int[] snakex = new int[750]; // represents the x-coordinates of the segments of the snake
	int[] snakey = new int[750]; // represents the y-coordinates of the segments of the snake
	String direction = "R"; 
	
	boolean isStarted = false;
	boolean isFailed = false;
	Random rand  = new Random();
	
	Timer timer =  new Timer(150, this);  // 150ms: speed of the snake

	// Set up the position of the food 
	int maxFood = 10;
	int[] foodx = new int[maxFood];
	int[] foody = new int[maxFood];
	int currentFoodCount = rand.nextInt(maxFood) + 1;
	
	// Set up the position of the poisonous apple
	int maxPois = 5;
	int[] poApplex = new int[maxPois];
	int[] poAppley = new int[maxPois];
	int currentPoisCount = rand.nextInt(maxPois) + 1;
	
	Clip bgm;
	
	public MPanel() {
	    initSnake();                // Initialize the snake
	    this.setFocusable(true);    // Makes this panel focusable so it can receive key events
	    poisonous = resizeIcon(new ImageIcon("PoApple.png"), 25, 25);  // resize the apple image
		   
	    // Adds a key listener to this panel to handle key presses
	    // The 'this' indicates that the panel itself implements 
	    // the KeyListener interface
	    this.addKeyListener(this); 
	   
	    timer.start();              // Starts a timer
	    loadBGM();                  // Loads the background music (BGM) for the game
	}

	// Overriding the paintComponent method for custom rendering
	public void paintComponent(Graphics g) {
	    // Call the parent class's paintComponent method to ensure proper rendering
	    super.paintComponent(g);

	    // Set the background color of this component to white
	    this.setBackground(Color.WHITE);

	    // Display the title image at the specified x and y coordinates (25,11)
	    title.paintIcon(this, g, 25, 11);

	    // Draw a filled rectangle with the default color 
	    g.fillRect(25, 75, 850, 600);

	    // Set the drawing color to yellow for subsequent drawing operations
	    g.setColor(Color.YELLOW);

	    // Display the length of the snake (or some other entity) at the specified coordinates
	    g.drawString("Len: " + len, 750, 35);

	    // Display the current score at the specified coordinates
	    g.drawString("Score: " + score, 750, 50);
		
	    // Switch on the 'direction' variable
	    switch (direction) {
	        case "R":
	            // Update and paint the 'right' icon at the position 
	            right.paintIcon(this, g, snakex[0], snakey[0]);
	            break; 

	        case "L":
	            // Update and paint the 'left' icon at the position 
	            left.paintIcon(this, g, snakex[0], snakey[0]);
	            break;  

	        case "D":
	            // Update and paint the 'down' icon at the position 
	            down.paintIcon(this, g, snakex[0], snakey[0]);
	            break; 

	        case "U":
	            // Update and paint the 'up' icon at the position 
	            up.paintIcon(this, g, snakex[0], snakey[0]);
	            break;
	    }

		// Draw the body, because the head is not part of the body, so we start from index 1 instead of 0
		for (int i = 1; i < len; i++) {
			body.paintIcon(this, g, snakex[i], snakey[i]);
		}
		
		// Paint the floating 'food'
		for (int i = 0; i < currentFoodCount; i++) {
		    food.paintIcon(this, g, foodx[i], foody[i]);
		}
		
		for (int i = 0; i < currentPoisCount; i++) {
			poisonous.paintIcon(this, g, poApplex[i], poAppley[i]);
		}
		
		// If the user hasn't started the game yet, print out the message
		if (isStarted == false) {
			g.setColor(Color.WHITE);
			g.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 40));
			g.drawString("Press Space to Start", 250, 350);
		}
		
		// If the user failed the game, print out the message
		if (isFailed) {
			g.setColor(Color.RED);
			g.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 40));
			g.drawString("Failed: Press Space to RESTART", 130, 350);
		}
	}

	public void initSnake() {
	    // Set the initial length of the snake to 3.
	    len = 3;

	    // Initialize the head of the snake to the position (100, 100).
	    snakex[0] = 100;
	    snakey[0] = 100;

	    // Initialize the first body segment of the snake to the position (75, 100).
	    snakex[1] = 75;
	    snakey[1] = 100;

	    // Initialize the second body segment of the snake to the position (50, 100).
	    snakex[2] = 50;
	    snakey[2] = 100;

	    for (int i = 0; i < currentFoodCount; i++) {
	        generateFoodPosition(i);
	    }

	    // Randomly generate a food's x-coordinate within a certain grid.
	    // The food's x position will be a multiple of 25 starting from 25.
	    // Randomly generate a food's y-coordinate within a certain grid.
	    // The food's y position will be a multiple of 25 starting from 75.
	    for (int i = 0; i < currentPoisCount; i++) {
	    	generatePoisPosition(i);
	    }

	    // Set the initial direction of the snake to move right.
	    direction = "R";

	    // Initialize the game score to 0.
	    score = 0;
	}


	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int keyCode = e.getKeyCode();
		
		// the game will start if the user press the Space key
		if (keyCode == KeyEvent.VK_SPACE) {
			if (isFailed) {
				isFailed = false;
				initSnake();
			} else {
				isStarted = !isStarted;
			}
			
			repaint(); // will execute the paintComponent() method
			if (isStarted) {
				playBGM();
			} else {
				stopBGM();
			}
		} else if (keyCode == KeyEvent.VK_LEFT) {
			direction = "L";
		} else if (keyCode == KeyEvent.VK_RIGHT) {
			direction = "R";
		} else if (keyCode == KeyEvent.VK_UP) {
			direction = "U";
		} else if (keyCode == KeyEvent.VK_DOWN) {
			direction = "D";
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isStarted && !isFailed) {
			for (int i = len - 1; i > 0; i--) {
				snakex[i] = snakex[i-1];
				snakey[i] = snakey[i-1];
			}
			
			if (direction == "R") {
			    if (snakex[0] + 25 <= 870) {
			        snakex[0] = snakex[0] + 25;
			    } else {
			    	isFailed = true;
			        repaint();
			        return;
			    }
			}else if (direction == "L") {
				snakex[0] = snakex[0] - 25;
				if (snakex[0] < 25) {
					for (int i = 0; i < snakex.length; i++) {
						snakex[i] = snakex[i] + 25;
					}
					
					isFailed = true;
				}
			} else if (direction == "U") {
				snakey[0] = snakey[0] - 25;
				if (snakey[0] < 75) {
					for (int i = 0; i < snakey.length; i++) {
						snakey[i] = snakey[i] + 25;
					}
					
					isFailed = true;
				}
			} else if (direction == "D") {
				snakey[0] = snakey[0] + 25;
				if (snakey[0] > 650) {
					for (int i = 0; i < snakey.length; i++) {
						snakey[i] = snakey[i] - 25;
					}
					
					isFailed = true;
				}
			}
			
			for (int i = 0; i < currentFoodCount; i++) {
			    if (snakex[0] == foodx[i] && snakey[0] == foody[i]) {
			        len++;
			        score += 5;
			        // Generate new position for the eaten food
			        generateFoodPosition(i);
			    }
			}
			
			for (int i = 0; i < currentPoisCount; i++) {
			    if (snakex[0] == poApplex[i] && snakey[0] == poAppley[i]) {
			    	len--;
					score -= 5;
					if (len < 1) {
						isFailed = true;
					}
			        // Generate new position for the eaten food
					generatePoisPosition(i);
			    }
			}
			
			repaint();
		}
		
		for (int i = 1; i < len; i++) {
			if ((snakex[i] == snakex[0] && snakey[i] == snakey[0])) {
				isFailed = true;
			}
		}
		
		timer.start();
	}
	
	private void loadBGM() {
		try {
			bgm = AudioSystem.getClip();
			InputStream is = this.getClass().getClassLoader().getResourceAsStream("sound/bgm2.wav");
			AudioInputStream ais = AudioSystem.getAudioInputStream(is);
			bgm.open(ais);
			
		} catch (LineUnavailableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedAudioFileException e) { 
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void playBGM() {
		bgm.loop(Clip.LOOP_CONTINUOUSLY);
	}
	
	private void stopBGM() {
		bgm.stop();
	}
	
	private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
	    Image img = icon.getImage();
	    Image resizedImage = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
	    return new ImageIcon(resizedImage);
	}
	
	private void generateFoodPosition(int index) {
	    foodx[index] = 25 + 25 * rand.nextInt(34);
	    foody[index] = 75 + 25 * rand.nextInt(24);
	}
	
	private void generatePoisPosition(int index) {
		poApplex[index] = 25 + 25 * rand.nextInt(34);
		poAppley[index] = 75 + 25 * rand.nextInt(24);
	}
	
}

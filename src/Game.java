import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.Timer;

public class Game extends Canvas{
	
	 private BufferStrategy strategy;   // take advantage of accelerated graphics
	 private boolean waitingForKeyPress = true;
	 
	 private boolean leftPressed = false;  // true if left arrow key currently pressed
     private boolean rightPressed = false; // true if right arrow key currently pressed
     private boolean upPressed = false; // true if up arrow key currently pressed
     private boolean downPressed = false;
     private boolean rightFire = false;
     private boolean leftFire = false;
     private boolean downFire = false;
     private boolean upFire = false;
     protected int ammoCount = 30;
     protected int enemyCount = 0;
     private int level = 1;
     protected int lives = 3;
     private int counter = 1; // for sub-levels
     String message = "Welcome to shooty game";
     private Entity player;
     private double slowFactor = 0.9;
     private double moveFactor = 1.5;
     private double initialMovement = 10;
     private double maxSpeed = 100;
     public long playerTheta = 0;
     
     public static int gameSize = 1000;
     
     private long lastFire = 0; // time last shot fired
     private long firingInterval = 300; 
     
     protected boolean gameRunning = true;
     private ArrayList<Entity> entities = new ArrayList<Entity>(); // list of entities
                                                   // in game
     private ArrayList<Entity> removeEntities = new ArrayList<Entity>(); // list of entities
                                                         // to remove this loop
     private boolean logicRequiredThisLoop = false;
     private boolean showLevel = true;
     private boolean ammoTimerStarted = false;
     protected int upgradePoints = 0;
     protected boolean showAmmoMessage = false;
     
     protected int pressCount = 1;
     
     public Game() {
 		// create a frame to contain game
 		JFrame container = new JFrame("CS12 Java Game");
 
 		// get hold the content of the frame
 		JPanel panel = (JPanel) container.getContentPane();
 
 		// set up the resolution of the game
 		panel.setPreferredSize(new Dimension(gameSize,gameSize)); // previously 800 by 600
 		panel.setLayout(null);
 
 		// set up canvas size (this) and add to frame
 		setBounds(0,0,gameSize,gameSize);
 		panel.add(this);
 
 		// Tell AWT not to bother repainting canvas since that will
         // be done using graphics acceleration
 		setIgnoreRepaint(true);
 
 		// make the window visible
 		container.pack();
 		container.setResizable(false);
 		container.setVisible(true);
 
 
         // if user closes window, shutdown game and jre
 		container.addWindowListener(new WindowAdapter() {
 			public void windowClosing(WindowEvent e) {
 				System.exit(0);
 			} // windowClosing
 		});
 
 		// add key listener to this canvas
 		addKeyListener(new KeyInputHandler());
 
 		// request focus so key events are handled by this canvas
 		requestFocus();

 		// create buffer strategy to take advantage of accelerated graphics
 		createBufferStrategy(2);
 		strategy = getBufferStrategy();
 
 		// initialize entities
 		initEntities();
 
 		// start the game
 		gameLoop();
     } // constructor
     
     private void initEntities() {
    	 
         // create the ship and put in corner of screen
         player = new PlayerEntity(this, "Sprites/player.png", 0, 0, gameSize);
         entities.add(player);
         
         // set ammo to drop every 15 seconds
         if (!ammoTimerStarted) {
        	 ammoTimerStarted = true;
	       	 TimerTask ammoTask = new DropAmmo(); 
	       	 Timer ammoTimer = new Timer();
	       	 ammoTimer.scheduleAtFixedRate(ammoTask, 0, 15000);
         }
         
         /*for(int i = 0; i < enemyCount; i++) {
        	 Entity enemy = new EnemyTemplate(this, "sprites/enemyTemp.png", 
        			 (int)(Math.random() * 700),
        			 (int)(Math.random() * 700), 
        			 gameSize);
        	 entities.add(enemy);
         } */
         
         createEnemies();
	} // initEntities
     
     private void playerShoot(int theta) {
    	 
    	 if ((System.currentTimeMillis() - lastFire) < firingInterval){
             return;
           } // if

    	 lastFire = System.currentTimeMillis();
    	 if (ammoCount == 0) {
      	   showAmmoMessage = true;
         }
    	 
    	 if (ammoCount > 0) {
             ShotEntity shot = new ShotEntity(this, "sprites/shot.png", 
             player.getX() + 10, player.getY() + 15, theta);
             entities.add(shot);
          }
    
     }
     
     public void enemyShoot(int x, int y) {

         EnemyShot shot = new EnemyShot(this, "sprites/HostileShot.png", 
         player.getX(), player.getY(), x , y);
         entities.add(shot);
      		 
      
       }
     
     private void createEnemies() {
         Entity enemy;
    	 for (int i = 0; i < level * 2; i++) {
	    	 enemy = new EnemyTemplate(this, "sprites/enemyTemp.png", 
	    	 (int)(Math.random() * 700),
	    	 (int)(Math.random() * 700), gameSize);
	         entities.add(enemy);
	         enemyCount++;
         }
     }
     
     protected void createAmmo() {
    	 Entity ammo;
    	 for (int i = 0; i < 3; i++) {
    		 ammo = new AmmoEntity(this, "Sprites/ammo.png", 
             (int)(Math.random()* 950) + 0, (int)(Math.random()* 950) + 0);
             entities.add(ammo);
    	 }
     }

   /* Notification from a game entity that the logic of the game
    * should be run at the next opportunity 
    */
    public void updateLogic() {
      logicRequiredThisLoop = true;
    } // updateLogic

    /* Remove an entity from the game.  It will no longer be
     * moved or drawn.
     */
    public void removeEntity(Entity entity) {
      removeEntities.add(entity);
    } // removeEntity
	
	 private void startGame() {
        // clear out any existing entities and initialize a new set
        entities.clear();
        enemyCount = 0;
        
        initEntities();
        
        // blank out any keyboard settings that might exist
        leftPressed = false;
        rightPressed = false;
        upPressed = false;
        downPressed = false;

        rightFire = false;
        leftFire = false;
        upFire = false;
        downFire = false;
        
        if (level == 1 && showLevel) {
        	TimerTask task = new ClearLevelMessage();
            Timer lvlTimer = new Timer();
            lvlTimer.schedule(task, 3000);
          } 
        
        // display instructions here?
     } // startGame
	 
	 public void gameLoop() {
         long lastLoopTime = System.currentTimeMillis();

         // keep loop running until game ends
         while (gameRunning) {
           
           // calc. time since last update, will be used to calculate
           // entities movement
           long delta = System.currentTimeMillis() - lastLoopTime;
           lastLoopTime = System.currentTimeMillis();

           // get graphics context for the accelerated surface and make it black
           Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
           g.setColor(Color.black);
           g.fillRect(0,0,gameSize,gameSize);
           
           g.setFont(new Font("sans-serif", Font.PLAIN, 30));
           
           if (!waitingForKeyPress) {
	           // move each entity
	           for (int i = 0; i < entities.size(); i++) {
	             Entity entity = (Entity) entities.get(i);
	             entity.move(delta);
	           } // for
	
	           // draw all entities
	           for (int i = 0; i < entities.size(); i++) {
	              Entity entity = entities.get(i);
	              entity.draw(g);
	           } // for
	           g.setColor(Color.white);
               g.drawString("Lives: " + lives, (gameSize - g.getFontMetrics().stringWidth("Lives: " + lives)), 30);
               g.drawString("Ammo: " + ammoCount, (gameSize - g.getFontMetrics().stringWidth("Ammo: " + ammoCount)), 70);
               g.drawString("Upgrade points: " + upgradePoints, (gameSize - g.getFontMetrics().stringWidth("Upgrade points: " + upgradePoints)), 110);
               if (showLevel) {
            	   if (level > 10) {
            		   endGame();
            	   } else { 
            		   g.drawString("Level " + level, (gameSize - g.getFontMetrics().stringWidth("Level " + level))/2, gameSize/2 - 50);
            		   g.setFont(new Font("sans-serif", Font.PLAIN, 15));
            	   
	            	   switch(level) {
	            	   	  case 1 :
	            	      case 2 :
	            	      case 3 : g.drawString("Elementary School - " + counter, (gameSize - g.getFontMetrics().stringWidth("Elementary School - " + counter))/2, gameSize/2 - 20);
	            	      break;
	            	      case 4 :
	            	      case 5 :
	            	      case 6 : g.drawString("Middle School - " + counter, (gameSize - g.getFontMetrics().stringWidth("Middle School - " + counter))/2, gameSize/2 - 20);
	   	      		   	  break;
	            	      case 7 :
	            	      case 8 :
	            	      case 9 : g.drawString("High School - " + counter, (gameSize - g.getFontMetrics().stringWidth("High School - " + counter))/2, gameSize/2 - 20);
	            	      break;
	            	      case 10 : g.drawString("High School Graduation - boss fight", (gameSize - g.getFontMetrics().stringWidth("High School Graduation - boss fight"))/2, gameSize/2 - 20);
	            	   }
            	   }
               }	   
           }
           
           
           if(waitingForKeyPress) {
    	   
        	   if (pressCount == 1) {
	               g.setColor(Color.white);
	               g.drawString(message, (gameSize - g.getFontMetrics().stringWidth(message))/2, gameSize/2 - 50);
	               g.drawString("Press any key", (gameSize - g.getFontMetrics().stringWidth("Press any key"))/2, gameSize/2);
        	   } else {
        		   g.setColor(Color.white);
	               g.drawString("PUT INSTRUCTIONS HERE", (gameSize - g.getFontMetrics().stringWidth("PUT INSTRUCTIONS HERE"))/2, gameSize/2);
        	   }
           }
           // brute force collisions, compare every entity
           // against every other entity.  If any collisions
           // are detected notify both entities that it has
           // occurred
          for (int i = 0; i < entities.size(); i++) {
            for (int j = i + 1; j < entities.size(); j++) {
               Entity me = (Entity)entities.get(i);
               Entity him = (Entity)entities.get(j);

               if (me.collidesWith(him)) {
                 me.collidedWith(him);
                 him.collidedWith(me);
               } // if
            } // inner for
          } // outer for

          // remove dead entities
          entities.removeAll(removeEntities);
          removeEntities.clear();

          // run logic if required
          if (logicRequiredThisLoop) {
            for (int i = 0; i < entities.size(); i++) {
              entities.get(i).doLogic();
            } // for
            logicRequiredThisLoop = false;
          } // if
          
          if (enemyCount == 0) {
          	level++;
          	counter = counter == 3 ? 1 : counter + 1;
          	createEnemies();
          	showLevel = true;
          	TimerTask task = new ClearLevelMessage(); 
            Timer lvlTimer = new Timer();
          	lvlTimer.schedule(task, 3000);
          }
          
           // clear graphics and flip buffer
           g.dispose();
           strategy.show();
           
           // slow player
           player.setHorizontalMovement(player.dx * (slowFactor));
           player.setVerticalMovement(player.dy * (slowFactor));

           // respond to user moving ship
           if (leftPressed && !rightPressed && player.dx < maxSpeed && player.dx > -maxSpeed) {
        	   if(player.dx > 0) { player.dx = 0;}
        	   player.setHorizontalMovement((((player.dx * -1) + initialMovement) * moveFactor) * -1);
           } else if (rightPressed && !leftPressed && player.dx < maxSpeed && player.dx > -maxSpeed) {
        	   if(player.dx < 0) { player.dx = 0;}
        	   player.setHorizontalMovement((player.dx + initialMovement) * moveFactor);
           }
           if (upPressed && !downPressed && player.dy < maxSpeed && player.dy > -maxSpeed) {
        	   if (player.dy > 0) {player.dy = 0;}
        	   player.setVerticalMovement((((player.dy * -1) + initialMovement) * moveFactor) * -1);
           } else if (downPressed && !upPressed && player.dy < maxSpeed && player.dy > -maxSpeed) {
        	   if (player.dy < 0) {player.dy = 0;}
        	   player.setVerticalMovement((player.dy + initialMovement) * moveFactor);
           }
           
           // pause
           try { Thread.sleep(30); } catch (Exception e) {}
           
           if (upFire && rightFire) {
        	   playerShoot(315);
           } else if (rightFire && downFire) {
        	   playerShoot(45);
           } else if (downFire && leftFire) {
        	   playerShoot(135);
           } else if (leftFire && upFire) {
        	   playerShoot(225);
           } else if (upFire) {
               playerShoot(270);
           } else if (downFire) {
        	   playerShoot(90);
           } else if (leftFire) {
        	   playerShoot(180);
           }else if (rightFire) {
        	   playerShoot(0);
           }

         } // while
         
         endGame();

	} // gameLoop

	 private void endGame() {
		 Graphics2D g = (Graphics2D) strategy.getDrawGraphics();
         g.setColor(Color.black);
         g.fillRect(0,0,gameSize,gameSize);
         
         g.setFont(new Font("sans-serif", Font.PLAIN, 30));
         g.setColor(Color.white);
         
         if (level > 10) {
        	 g.drawString("You win!", (gameSize - g.getFontMetrics().stringWidth("You win!"))/2, gameSize/2 - 50);
        	 gameRunning = false;
         } else {
        	g.drawString("Game Over", (gameSize - g.getFontMetrics().stringWidth("Game Over"))/2, gameSize/2 - 50);	
         }
         g.setFont(new Font("sans-serif", Font.PLAIN, 15));
         g.drawString("Press ESC to exit", (gameSize - g.getFontMetrics().stringWidth("Press ESC to exit"))/2, gameSize/2 - 20);
		 g.dispose();
		 strategy.show();
	 }
	 
	 private class KeyInputHandler extends KeyAdapter {
		 
		 //protected int pressCount = 1;

       /* The following methods are required
        * for any class that extends the abstract
        * class KeyAdapter.  They handle keyPressed,
        * keyReleased and keyTyped events.
        */
        public void keyPressed(KeyEvent e) {
        	
        	if (waitingForKeyPress) {
                return;
              } // if
	         
	         // respond to move left, right or fire
	         if (e.getKeyCode() == KeyEvent.VK_A) {
	           leftPressed = true;
	         } // if
	
	         if (e.getKeyCode() == KeyEvent.VK_D) {
	           rightPressed = true;
	         } // if
	         
	         if (e.getKeyCode() == KeyEvent.VK_W) {
	       	  upPressed = true;
	         }
	         
	         if (e.getKeyCode() == KeyEvent.VK_S) {
	        	 downPressed = true;
	         }
	         
	         if (e.getKeyCode() == KeyEvent.VK_UP) {
	        	 upFire = true;
	         }
	         
	         if (e.getKeyCode() == KeyEvent.VK_DOWN) {
	        	 downFire = true;
	         }
	         
	         if (e.getKeyCode() == KeyEvent.VK_LEFT) {
	        	 leftFire = true;
	         }
	         
	         if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
	        	 rightFire = true;
	         }

        } // keyPressed

        public void keyReleased(KeyEvent e) {
        	
        	if (waitingForKeyPress) {
                return;
              } // if
			 
			 // respond to move left, right or fire
			 if (e.getKeyCode() == KeyEvent.VK_A) {
			   leftPressed = false;
			 } // if
			
			 if (e.getKeyCode() == KeyEvent.VK_D) {
			   rightPressed = false;
			 } // if
			 
			 if (e.getKeyCode() == KeyEvent.VK_W) {
			  upPressed = false;
			 }
			 
			 if (e.getKeyCode() == KeyEvent.VK_S) {
	        	 downPressed = false;
	         }
			 
			 if (e.getKeyCode() == KeyEvent.VK_UP) {
	        	 upFire = false;
	         }
	         
	         if (e.getKeyCode() == KeyEvent.VK_DOWN) {
	        	 downFire = false;
	         }
	         
	         if (e.getKeyCode() == KeyEvent.VK_LEFT) {
	        	 leftFire = false;
	         }
	         
	         if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
	        	 rightFire = false;
	         }
			} // keyReleased
        
        public void keyTyped(KeyEvent e) {

         // if waiting for key press to start game
         if (waitingForKeyPress) {
             if (pressCount == 2) {
                waitingForKeyPress = false;
                startGame();
                pressCount = 0;
              } else {
                pressCount++;
              } // else
            } // if waitingForKeyPress

            // if escape is pressed, end game
            if (e.getKeyChar() == 27) {
              System.exit(0);
            } // if escape pressed

	} // keyTyped

	} // class KeyInputHandler
	
	class ClearLevelMessage extends TimerTask {
		public void run() {
			showLevel = false;
		}
	}
	
	class DropAmmo extends TimerTask {
		public void run() {
			createAmmo();
		}
	}
	
	class AmmoMessage extends TimerTask {
		public void run() {
			showAmmoMessage = false;
		}
	}
	
	public static void main(String[] args) {
		
		new Game();

	}

}

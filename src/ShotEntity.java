import java.awt.Graphics;

public class ShotEntity extends Entity {
	
	private int moveSpeed = 300;
	private long radius = 0;
	boolean used = false;
	Game game;
	
	public ShotEntity(Game g, String r, int newX, int newY, long radius) {
	    super(r, newX, newY);  // calls the constructor in Entity
	    game = g;
	    dy = moveSpeed;
	    this.radius = radius;
	    game.ammoCount--;
	  } // constructor
	
	public void move (long delta) {
		
		super.moveBullet(delta, radius);
		
		if (y < 0 || y > gameSize || x < 0 || x > gameSize) {
			game.removeEntity(this);
		}
		
	}
	
	public void collidedWith(Entity other) {
	     // prevents double kills
	     if (used) {
	       return;
	     } // if
	     
	     if(other instanceof EnemyTemplate) {
	    	 game.removeEntity(this);
	    	 game.removeEntity(other);
	    	 game.enemyCount--;
	    	 game.upgradePoints += 3;
	     }

	} // collidedWith)
	
	public void draw(Graphics g) {
		super.draw(g);
	}
	
}

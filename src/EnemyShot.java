public class EnemyShot extends Entity {
	
	public double theta;
	private int moveSpeed = 150;
	boolean collided = false;
	Game game;
	
	public EnemyShot(Game g, String r, int endX, int endY, int startX, int startY) {
	    super(r, startX, startY);  // calls the constructor in Entity
	    game = g;
	    dy = moveSpeed;
	    theta = Math.atan2(endY - startY, endX - startX);
	}
	
	public void move (long delta) {
		
		super.hostileBullet(delta, theta);
		
		if (y < 0 || y > gameSize || x < 0 || x > gameSize) {
			game.removeEntity(this);
		}
		
	}
	
	public void collidedWith(Entity other) {
	     // prevents double kills
	     if (collided) {
	       return;
	     } // if
	     
	     if (other instanceof PlayerEntity) {
	    	 collided = true;
	    	 game.lives--;
	         if (game.lives == 0) {
	        	// plays death animation
	        	 game.gameRunning = false;
	    	 }
	         // game.removeEntity(other);
	
	     }

	} // collidedWith
	
}
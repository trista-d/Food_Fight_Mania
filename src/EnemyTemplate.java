public class EnemyTemplate extends Entity{

	private double moveSpeed = 70;
	private long lastAction;
	private long actionStartTime;
	private long actionInterval = 1000;
	private static final long actionDuration = 2000;
	private boolean lastActionTimeSet = false;
	private int gameSize = 0;
	private double previousDy = 0;
	private double previousDx = 0;
	
	private Game game;
	
	private boolean collided = false;
	
	public EnemyTemplate(Game g, String r, int newX, int newY, int gameSize) {
	    super(r, newX, newY);  // calls the constructor in Entity
		moveSpeed = moveSpeedRandomizer();
		actionInterval = (long) (Math.random() * 1000) + 100;
	    game = g;
	    if((int)(Math.random()*2) == 0) {
	    	dx = moveSpeed;  // start off moving left
	    }
	    else{dy = moveSpeed;}
	    this.gameSize = gameSize;
	}
	
	public int moveSpeedRandomizer() {
		return (int) (Math.random()*2 < 1 ? Math.random() * 100 + 30 : Math.random() * -10 + 70);
	}
	
	public void move (long delta){
	    //action();
		if ((dx < 0) && (x < 10) || (dx > 0) && (x > gameSize - 10)) {
			dx *= -1;
		}
		 if ((dy > 0) && (y > gameSize - 10) || (dy < 0) && (y < 10)){
			 dy *= -1;
		}

	    action();
	    super.move(delta);
	} 
	
	private void action() {
		// return if enemy is moving
		if((System.currentTimeMillis() - actionStartTime) < actionDuration) {
			lastActionTimeSet = false;
			return;
		}
		
		// stops enemy movement
		if(!lastActionTimeSet) {
			previousDy = dy;
			previousDx = dx;
			dx = 0;
			dy = 0;
			lastAction = System.currentTimeMillis();
			lastActionTimeSet = true;
		}
		
		// return if enemy moved recently
		if((System.currentTimeMillis() - lastAction) < actionInterval) {	
			return;
		}
		
		game.enemyShoot((int)x, (int)y);
		
		// randomizes move speed
		moveSpeed = moveSpeedRandomizer();
		// chooses a random direction
		
		if (previousDx == 0) {
			if(Math.random() * 2 < 1) {
				if (moveSpeed < 0) {
					dx = moveSpeed;
				} else {
					dx = -moveSpeed;
				}
				dy = 0;
			} else {
				if (moveSpeed < 0) {
					dx = -moveSpeed;
				} else {
					dx = moveSpeed;
				}
				dy = 0;
			}	
		} else if (previousDy == 0) {
			if(Math.random() * 2 < 1) {
				if (moveSpeed < 0) {
					dy = moveSpeed;
				} else {
					dy = -moveSpeed;
				}
				dx = 0;
			} else {
				if (moveSpeed < 0) {
					dy = -moveSpeed;
				} else {
					dy = moveSpeed;
				}
				dx = 0;
			}
		}
		
		actionStartTime = System.currentTimeMillis();
	}

	  public void doLogic() {
		  dx *= -1;
		  dy *= -1;
	  }
	
	public void collidedWith(Entity other) {
		if (collided) {
			return;
		}
	     if (other instanceof PlayerEntity) {
	    	 collided = true;
	    	 game.lives--;
	         if (game.lives == 0) {
	        	// plays death animation
	        	 game.gameRunning = false;
	    	 }
	     }
	}
}

public class PlayerEntity extends Entity {
	
	private Game game;
	private int gameSize = 0;

	public PlayerEntity(Game g, String r, int x, int y, int gameSize) {
		super(r, x, y);
		game = g;
		this.gameSize = gameSize;
	}
	
	public void move (long Delta) {
		if (x <= 0 && dx < 0) {
			dx = 0;
		} else if (x >= gameSize - 30 && dx > 0) {
			dx = 0;
		}
		if (y <= 0 && dy < 0) {
			dy = 0;
		} else if (y >= gameSize-30 && dy > 0) {
			dy = 0;
		}
		
		super.move(Delta);
	}
	
	public void collidedWith(Entity other) {
		
		if (other instanceof AmmoEntity) {
		    game.removeEntity(other);
		    game.ammoCount += 5;
		} // if
	     
	} // collidedWith
}

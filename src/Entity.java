import java.awt.Graphics;
import java.awt.Rectangle;

public abstract class Entity {
	
	protected double x;
	protected double y;
	protected Sprite sprite;
	protected double dx;
	protected double dy;
	protected int gameSize = Game.gameSize;
	
	private Rectangle me = new Rectangle(); // bounding rectangle of this entity
	private Rectangle him = new Rectangle(); // bounding rect. of other entities
	
	public Entity(String r, int x, int y) {
		this.x = x;
		this.y = y;
		sprite = (SpriteStore.get()).getSprite(r);
	}
	
	public void move(long delta) {
        x += (delta * dx) / 1000;
        y += (delta * dy) / 1000;
    }
	
	public void moveBullet(long delta, long theta) {
		x += ((delta * dy) / 1000) * Math.cos(theta* Math.PI / 180);
		y += ((delta * dy) / 1000) * Math.sin(theta* Math.PI / 180);
	}
	
	public void hostileBullet(long delta, double theta) {
		x += ((delta * dy) / 1000) * Math.cos(theta);
		y += ((delta * dy) / 1000) * Math.sin(theta);
	}
	
	public void setHorizontalMovement(double newDX) {
	     dx = newDX; 
	} // setHorizontalMovement
	
	 public void setVerticalMovement(double newDY) {
		 dy = newDY;
	 } // setVerticalMovement
	 
	 public int getX() {
		 return (int)this.x;
	 }
	 
	 public int getY() {
		 return (int)this.y;
	 }
	 
	 public void draw (Graphics g) {
	     sprite.draw(g,(int)x,(int)y);
	 }  // draw
	 
	 public void doLogic() {}
	 
	 public boolean collidesWith(Entity other) {
		 me.setBounds((int)x, (int)y, sprite.getWidth(), sprite.getHeight());
		 him.setBounds(other.getX(), other.getY(), 
                     other.sprite.getWidth(), other.sprite.getHeight());
		 return me.intersects(him);
     } // collidesWith
	 
	 public abstract void collidedWith(Entity other);
	
}

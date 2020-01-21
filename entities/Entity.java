class Entity {
	public int x;
	public int y;

	public float tx;
	public float ty;

	public int width = 16;
	public int height = 16;
	
	public int step = 0;
	public int flashing = 0;
	public int points = 0;

	public boolean isEnabled = false;
	
	final long ZOMBIE_SCORE = 400;
    final long BAT_SCORE = 200;
    final long COIN_SCORE = 100;
    final long COFFEE_SCORE = 300;
    final long COFFEE_BONUS = 1000;
	
	// Initialization
	public void initPlayer() {
	    this.x = 12;
	    this.y = 0;
	    this.ty = this.y;
	    
	    this.step = 0;
	    
	    isEnabled = true;
	}
	
	public void initBat() {
	    this.x = (int) (Math.random() * 80 + 32);
        this.y = (int) (88 + Math.random() * (Math.random() > 0.5 ? -80 : 30));
        this.ty = this.y;
        
        this.step = 0;
        
        this.points = BAT_SCORE;
	}
	
	public void initZombie() {
	    this.x = (int) (Math.random() > 0.5 ? 12 : 132);
        this.y = 175;
        this.ty = this.y;
        
        this.step = 0;
        
        this.points = ZOMBIE_SCORE;
	}
	
	public void initCoffee() {
	    this.x = (int) (Math.random() * 80 + 32);
        this.y = (int) (88 + Math.random() * (Math.random() > 0.5 ? -88 : 60));
        this.ty = this.y;
        
        this.width = 12;
        this.height = 12;
        
        this.step = 0;
        
        this.points = COFFEE_SCORE;
	}
	
	public void initCoin() {
	    this.x = (int) (Math.random() * 84 + 32);
        this.y = (int) (88 + Math.random() * (Math.random() > 0.5 ? -88 : 60));
        this.ty = this.y;
        
        this.width = 8;
        this.height = 8;
        
        this.step = 0;
        
        this.points = COIN_SCORE;
	}
	
	// Collisions
	public boolean hasCollisioned(Entity e) {
	    int ex = e.x;
	    int exw = e.x + e.width;
	    int ey = e.y;
	    int eyh = e.y + e.height;
	    
	    int tx = this.x;
	    int txw = this.x + this.width;
	    int ty = this.y;
	    int tyh = this.y + this.height;
	    
	    /*int d1x = b->min.x - a->max.x;
        int d1y = b->min.y - a->max.y;
        int d2x = a->min.x - b->max.x;
        int d2y = a->min.y - b->max.y;
        
        b->t
        a->e
        */
        
        int d1x = tx - exw;
        int d1y = ty - eyh;
        int d2x = ex - txw;
        int d2y = ey - tyh;
    
        if (d1x > 0 || d1y > 0)
            return false;
    
        if (d2x > 0 || d2y > 0)
            return false;
    
        return true;
	}
}
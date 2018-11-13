
public enum Difficulty {

	EASY (1),
	MEDIUM (2),
	HARD (3);
	
	private final int speed;
	
	Difficulty(int speed){
		this.speed = speed;
	}
	
	public int getSpeed() {
		return speed;
	}
	
	
}

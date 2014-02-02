package sealion.client.world;

/**
 * 
 * 캐릭터의 이동 궤적을 나타냄. 이 정보들은 캐릭터의 스택에 쌓임
 * 
 * @author 김현준
 * 
 */
public class Path {

	/**
	 * 도착점과 방향을 가지는 벡터량이다.
	 */
	public int direction;
	public Point destination;
	public boolean canceled = false;

	public float velocityX;
	public float velocityY;

	public double previousDistance = 100000000;

	/**
	 * 경로를 생성한다.
	 * 
	 * @param direction
	 */
	public Path(int direction) {
		this.direction = direction;
		this.destination = null;
	}

	public Path(int direction, Point destination) {
		this.direction = direction;
		this.destination = destination;
	}

	/**
	 * 도착 지점에 도달했는지 확인한다.
	 * 
	 * @param location
	 * @return
	 */
	public boolean reached(Point location) {
		if (destination == null)
			return false;

		double distance = destination.getDistance(location);

		// 아주 가까워졌거나 방금 최소 지점을 지난 경우에 해당한다.
		if (distance < 4 || distance > previousDistance) {
			return true;
		}

		previousDistance = distance;

		return false;

	}

	public void cancel() {
		canceled = true;
	}
}
package sealion.client.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import sealion.client.Display;
import sealion.client.asset.Graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Stage implements Renderable {

	/**
	 * 랜더링 관련 값
	 */
	public static final float SCALE_Z = (float) Math.sqrt(2);
	public Camera camera;
	public Point smoothCamera;

	/**
	 * 스테이지 크기 (항상 정사각형 꼴)
	 */
	public int size;

	/**
	 * 스테이지 구성 요소
	 */
	public List<Terrain> terrain;
	public List<Character> characters;
	public List<Unit> units;

	/**
	 * 유저의 캐릭터
	 */
	public Character me;

	/**
	 * Z좌표 정렬
	 */
	private Comparator<Unit> unitComparator;

	/**
	 * 스테이지 생성
	 */
	public Stage() {

		// 카메라 초기화
		camera = new OrthographicCamera(Display.width, Display.height);
		camera.position.set(Display.width / 2, Display.height / 2, 0.4f);
		smoothCamera = new Point(Display.width / 2, Display.height / 2, 0);

		// 레이어 초기화
		terrain = new ArrayList<Terrain>();
		characters = new ArrayList<Character>();
		units = new ArrayList<Unit>();

		// 정렬자
		unitComparator = new UnitComparator();
	}

	/**
	 * 스테이지를 로드한다.
	 * 
	 * @param entry
	 */
	public void load(Entry entry) {

		// 시작점 취득
		int startingX = Terrain.WIDTH / 2 * entry.getSize();
		int startingY = Terrain.HEIGHT / 2;

		for (int i = 0; i < entry.getSize(); i++) {

			// 다음 포인터 업데이트
			startingX -= Terrain.WIDTH / 2;
			startingY += Terrain.HEIGHT / 2;

			for (int j = 0; j < entry.getSize(); j++) {

				// 좌표 설정
				int x = startingX + (Terrain.WIDTH / 2 * j);
				int y = startingY + (Terrain.HEIGHT / 2 * j);

				// 지형 설정
				Terrain grid = new Terrain(x, y, entry.terrainAltitude[i][j], new Terrain.Index(j, i), null);

				// 지형 그래픽 취득
				if (entry.terrainType[i][j] == 0) {
					grid.setTexture(Graphics.animations.get("TERRAIN")[0]);
				}

				// 지형 추가
				terrain.add(grid);
			}
		}
	}

	public void load() {
		Entry entry = new Entry();
		entry.generateRandom(20);

		load(entry);
	}

	/**
	 * 좌표로 지형 타일 구하기
	 * 
	 * @param point
	 * @return
	 */
	public Terrain getGrid(Point point) {
		float dx = -(terrain.get(0).x - (Terrain.WIDTH / 2));
		float dy = -terrain.get(0).y;

		float px = Terrain.RADIAN_45 * (point.x + (2 * point.y) + dx + (2 * dy));
		float py = Terrain.RADIAN_45 * (-point.x + (2 * point.y) + (-dx) + (2 * dy)) + (90 * 1.5f);

		float gridSize = Terrain.HEIGHT / Terrain.RADIAN_45;
		int tx = (int) Math.floor(px / gridSize);
		int ty = (int) Math.floor(py / gridSize);

		return terrain.get((int) (ty * Math.sqrt(terrain.size()) + tx));
	}

	/**
	 * 스테이지 랜더링
	 */
	@Override
	public void draw(SpriteBatch spriteBatch, float delta) {

		// 카메라 업데이트
		camera.update();
		spriteBatch.setProjectionMatrix(camera.combined);

		// 지형 그리기
		for (int i = 0; i < terrain.size(); i++) {
			Terrain piece = terrain.get(i);
			spriteBatch.draw(piece.getTexture(), piece.x, Display.height - piece.y + piece.z * SCALE_Z);
		}

		// 모든 랜더링 가능한 물체를 Z값 소팅한다.
		Collections.sort(units, unitComparator);

		for (int i = 0; i < units.size(); i++) {
			units.get(i).draw(spriteBatch, delta);
		}

	}

	@Override
	public void update(float delta) {

		// 유저를 가리키도록 좌표를 설정한다.
		smoothCamera.x += (0.05f * (me.position.x - smoothCamera.x));
		smoothCamera.y += (0.05f * ((Display.height - me.position.y) - smoothCamera.y));

		camera.position.x = Math.round(smoothCamera.x);
		camera.position.y = Math.round(smoothCamera.y);

		// 모든 캐릭터를 업데이트한다.
		for (int i = 0; i < characters.size(); i++) {
			characters.get(i).update(delta);
		}
/*
		// 모든 유닛을 업데이트한다.
		for (int i = 0; i < units.size(); i++) {
			units.get(i).update(delta);
		}*/
	}

	public class UnitComparator implements Comparator<Unit> {		
		@Override
		public int compare(Unit r1, Unit r2) {
			return (int) (r1.getPosition().y - r2.getPosition().y);
		}

	}

	/**
	 * 스테이지의 구성 데이터 저장
	 * 
	 * @author 현준
	 * 
	 */
	public static class Entry {

		// 지형
		public int[][] terrainType;
		public int[][] terrainAltitude;

		// 유닛
		public int[] unit;

		public Entry() {

		}

		public Entry(int size) {
			generateRandom(size);
		}

		public int getSize() {
			return terrainType.length;
		}

		/**
		 * 랜덤으로 스테이지를 만든다.
		 * 
		 * @param size
		 */
		public void generateRandom(int size) {

			terrainType = new int[size][size];
			terrainAltitude = new int[size][size];
			unit = new int[0];

			for (int i = 0; i < size; i++) {
				for (int j = 0; j < size; j++) {
					terrainType[i][j] = 0;
					terrainAltitude[i][j] = 0;
				}
			}
		}
	}

}

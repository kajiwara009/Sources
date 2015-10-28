package com.example.pigdig.data;

import java.util.Random;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.example.pigdig.GM;
import com.example.pigdig.R;
import com.example.pigdig.task.OnMapItem;

/**
 * 侵入可能領域かを表す２値マップ 画像ファイル ２値マップの配列サイズは画像ファイルの画素数と同数で 
 * @author kajiwarakengo
 */
public class Field {
	public static final int ENTERABLE_AREA = Color.WHITE,
							NON_ENTERABLE_AREA = Color.BLACK;

	public class HiddenTreasure extends OnMapItem{
		/** 埋まっている深さ */
		int depth;
		Treasure treasure;
		
		/**
		 * depthから引数分だけ引く．Pigが掘るときに利用する
		 * @param depth
		 */
		public void dig(int depth){
			this.depth -= depth;
		}

		public int getDepth(){
			return depth;
		}
		public void setDepth(int depth){
			this.depth = depth;
		}
		
		public Treasure getTreasure() {
			return treasure;
		}

		public void setTreasure(Treasure treasure) {
			this.treasure = treasure;
			this.depth = treasure.getDepth();
		}
		
	}

	private HiddenTreasure hiddenTreasure = new HiddenTreasure();

	private Bitmap
		// 進入可能かを表す白黒マップ．白が進入可能．今後拡張する可能性もあり
		enterableMap,
		// 実際に表示するマップ
		image;

	/** お目の高さ．レアなアイテムが出やすくなる */
	private int discern;

	
	
	
	
	
	
	
	
	
	
	

	public Field(Resources r, SharedPreferences preferences) {
		discern = preferences.getInt("DISCERN", 0);
		setMap(r, preferences);
//		setRandomTreasure();
	}
	
	private void setMap(Resources r, SharedPreferences preferences) {
		String stage = preferences.getString("STAGE", "Stage1");
		int enterablemapR, imageMapR;

		switch (stage) {
		case "Stage1":
			enterablemapR = R.drawable.enterablemap;
			imageMapR = R.drawable.g_field;
			break;

		default:
			enterablemapR = R.drawable.enterablemap;
			imageMapR = R.drawable.g_field;
			break;
		}
		
		this.enterableMap = BitmapFactory.decodeResource(r, enterablemapR);
		this.image = BitmapFactory.decodeResource(r, imageMapR);
	}

/*	public Field(Bitmap enterableMap, Bitmap image) {
		this.enterableMap = enterableMap;
		this.setImage(image);
		setTreasure();
	}
*/
/*	public void setTreasure() {
		// 初期位置をFieldの進入可能領域からランダムに選択
		boolean canEnter = false;
		while (!canEnter) {
			int randX = new Random().nextInt(enterableMap.getWidth());
			int randY = new Random().nextInt(enterableMap.getHeight());
			if (canEnter(randX, randY)) {
				hiddenTreasure.setX(randX);
				hiddenTreasure.setY(randY);
				canEnter = true;
			}
		}
		hiddenTreasure.treasure = Treasure.getRandomTreasure(GM.getPreferences().getInt("DISCERN", 0));
		hiddenTreasure.depth = hiddenTreasure.treasure.getDepth();
	}
*/
	/**
	 * getPixelの処理が正しく行われているか不安
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean canEnter(double x, double y) {
		if (x > enterableMap.getWidth() || y > enterableMap.getHeight()) {
			return false;
		} else {
			int pixel = enterableMap.getPixel((int)x, (int)y);
			if(pixel == ENTERABLE_AREA){
				System.out.println(x + "，" + y + "は白のエリア");
				return true;
			}else{
				System.out.println(x + "," + y + "のピクセルは" + pixel);
				return false;
			}
/*			// System.out.println(enterableMap.getPixel(x, y));
			switch (enterableMap.getPixel((int)x, (int)y)) {
			case ENTERABLE_AREA:
				System.out.println(x + "，" + y + "は白のエリア");
				return true;
			case NON_ENTERABLE_AREA:
				System.out.println(x + "," + y + "は黒のエリア");
				return false;
			default:
				System.out.println(x + "," + y + "はenterableMapの値が不適切");
				System.out.println(enterableMap.getPixel((int)x, (int)y));
				return false;
			}
*/		}
	}
	
	public void setRandomTreasure(){
		hiddenTreasure.setAtRandomPoint();
		hiddenTreasure.setTreasure(Treasure.getRandomTreasure(discern));
		hiddenTreasure.depth = hiddenTreasure.treasure.getDepth();
	}
	
	public double checkTreasureDistance(double x, double y) {
		Vec v = new Vec(hiddenTreasure.getX()-x, hiddenTreasure.getY()-y);
		return v.getMagnitude();
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public double getWidth() {
		return enterableMap.getWidth();
	}

	public double getHeight() {
		return enterableMap.getHeight();
	}

	public Bitmap getEnterableMap() {
		return enterableMap;
	}

	public void setEnterableMap(Bitmap enterableMap) {
		this.enterableMap = enterableMap;
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public HiddenTreasure getHiddenTreasure() {
		return hiddenTreasure;
	}

	public void setHiddenTreasure(HiddenTreasure hiddenTreasure) {
		this.hiddenTreasure = hiddenTreasure;
	}
	public int getDiscern() {
		return discern;
	}

	public void setDiscern(int discern) {
		this.discern = discern;
	}

}

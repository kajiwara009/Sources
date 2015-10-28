package codevs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import stateChanger.PrimaryChanger;
import stateChanger.StateChanger;
import stateDirector.OpeningDirector;
import stateDirector.StateDirector;
import twinDirector.FighterDirector;
import twinDirector.WorkerDirector;
import unitDirector.UnitDirector;
import data.*;

//
// codevs 4.0 Sample Program Java
//
public class God {
	public static final int MAP_END = 99;
	
	StateChanger stateChanger = new PrimaryChanger(this);

	StateDirector stateDirector = null;
	
	Scanner scanner = new Scanner(System.in);
	private int remainingTime; // 残り持ち時間
	private int currentStage;
	private int currentTurn;
	private int currentResource; // 現在の資源数
	private int income; //現在の収入
	private Unit myCastle = null; // 自分の城
	private Unit opCastle = null; // 敵の城（敵の城を見つけていないならnull）
	private boolean isTopLeft; // 1P側か2P側か
//	Map<Integer, Unit> myLastUnits = new HashMap<Integer, Unit>(); // 前ターンの詩文のユニット
	private Map<Integer, Unit> myUnits = new HashMap<Integer, Unit>(); // ユニットIDをキーとする自分のユニットの一覧。
//	Map<Integer, Unit> opLastUnits = new HashMap<Integer, Unit>(); // ユニットIDをキーとする（視界内の）敵ユニットの一覧。
	private Map<Integer, Unit> opUnits = new HashMap<Integer, Unit>(); // ユニットIDをキーとする（視界内の）敵ユニットの一覧。
	private boolean[][] see = new boolean[100][100]; // そのマスが一度でも視界に入るとtrue
	private boolean[][] castlePosible = new boolean[100][100];
	private Set<Vector> resources = new HashSet<>();
	
	
	// C++とJavaのサンプルコードで同じ動作をさせるために同じ擬似乱数を使う。
/*	int lcg = 0;
	int rand(int n) {
		lcg = 1664525 * lcg + 1013904223;
		int x = lcg % n;
		return x < 0 ? x + n : x;
	}
*/
	/*
	class Unit {
		// ユニットの現在のステータス。
		// 与えられる入力により input() で設定される。
		int id;
		int y;
		int x;
		int hp;
		int type;

		// ユニットに出す命令。
		// 移動か生産のどちらかの行動を行うように think() で設定される（どちらの行動もとらない場合もある）。
		int movetoy = -1; // 非負の値ならそのマスへ向かって移動することを表す。
		int movetox = -1; // 非負の値ならそのマスへ向かって移動することを表す。
		int produce = -1; // 非負の値ならそのタイプのユニットを生産することを表す。
		boolean isMoving() {
			return movetoy >= 0;
		}
		boolean isProducing() {
			return produce >= 0;
		}
		boolean isFree() {
			return !isMoving() && !isProducing();
		}
		void free() {
			movetoy = -1;
			movetox = -1;
			produce = -1;
		}

		// 現在設定されている行動を出力用の文字列に変換する。
		// 行動が設定されてない場合は長さ 0 の文字列を返す。
		String toOrderString() {
			if (isProducing()) {
				return id + " " + produce;
			}
			if (isMoving()) {
				int dy = Math.abs(y - movetoy);
				int dx = Math.abs(x - movetox);
				if (dy + dx > 0) {
					if (rand(dy + dx) < dy) {
						if (y < movetoy) return id + " " + "D";
						if (y > movetoy) return id + " " + "U";
					} else {
						if (x < movetox) return id + " " + "R";
						if (x > movetox) return id + " " + "L";
					}
				}
				return "";
			}
			return "";
		}
	}

*/	

	
	int dist(int x1, int y1, int x2, int y2) {
		return Math.abs(y1 - y2) + Math.abs(x1 - x2);
	}
	
	/**
	 * Unit同士のマンハッタン距離を求める
	 * @param u1
	 * @param u2
	 * @return
	 */
	int dist(Unit u1, Unit u2){
		int x1 = u1.x();
		int y1 = u1.y();
		int x2 = u2.x();
		int y2 = u2.y();
		return dist(x1, y1, x2, y2);
	}

	void stageStart() {
		stateChanger = new PrimaryChanger(this);
		stateDirector = new OpeningDirector(this, new WorkerDirector(this), new FighterDirector(this));
		myUnits.clear();
		myCastle = null;
		opUnits.clear();
		opCastle = null;
		resources.clear();
		for (int y = 0; y < 100; y++) for (int x = 0; x < 100; x++){
			see[x][y] = false;
			if(x + y >= 99 * 2 - 40) castlePosible[x][y] = true;
			else castlePosible[x][y] = false;
		}
	}

	private boolean input() {
		remainingTime = scanner.nextInt();
		currentStage = scanner.nextInt();
		currentTurn = scanner.nextInt();
		System.err.println("God.input:ターン：" + currentTurn);
		if (currentTurn == 0) {
			// ターンをまたいで維持される変数はステージが新しくなった時点で初期化を行う。
			stageStart();
			// ステージが始まったことをデバッグ出力。
			// （クライアントで実行すると標準エラー出力は ./log/io/ 配下にログが出力される）
			System.err.println("stage:" + currentStage);
		}

		currentResource = scanner.nextInt();

		{
			
//			myLastUnits = new HashMap<>(myUnits);
			
			// 自分のユニットを読み込み。
			Map<Integer, Unit> mp = new HashMap<Integer, Unit>();
			int myNum = scanner.nextInt();
			for (int i = 0; i < myNum; i++) {
				int id = scanner.nextInt();
				//ここでisTopLeftにしたがって書き換えたいけど，最初のターンは関係ないboolが入っているからisTopLeftが確定した直後に
				int y = scanner.nextInt();
				int x = scanner.nextInt();
				int hp = scanner.nextInt();
				UnitType type = UnitType.getType(scanner.nextInt());

				//最初のターンかつユニットタイプが城の場合，isTopLeftを設定する
				if(myCastle == null && type == UnitType.CASTLE){
					if(i == 0){
						isTopLeft = dist(x, y, 0, 0) < dist(x, y, 99, 99);
					}else{
						System.err.println("入力の一番最初がキャッスルじゃないからその前に呼ばれたUnitのx,yがおかしい");
					}
				}
				if(!isTopLeft){
					x = 99 - x;
					y = 99 - y;
				}
				Unit u;
				if(myUnits.containsKey(id)){
					u = myUnits.get(id);
					u.update(x, y, hp);
				}else{
					u = new Unit(id, x, y, type);
				}
				
				mp.put(id, u);
				if (u.type() == UnitType.CASTLE) {
					myCastle = u;
					// 城が左上と右下のコーナーどちらに近いかで 1P側・2P側を判断
/*					isTopLeft = dist(myCastle.y(), myCastle.x(), 0, 0) <  dist(myCastle.y(), myCastle.x(), 99, 99);*/
				}
				// 一度でも視界に入ったことがあるマスを更新
				int view = u.type().getSight();
				for (int sy = u.y() - view; sy <= u.y() + view; sy++)
					for (int sx = u.x() - view; sx <= u.x() + view; sx++){
						if (0 <= sy && sy < 100 && 0 <= sx && sx < 100 && dist(u.y(), u.x(), sy, sx) <= view){
							castlePosible[sx][sy] = false;
							see[sx][sy] = true;
						}
					}
			}
			// 死んだユニットをリストから除くため新しい map に追加して、置き換える。
			List<Unit> deadUnits = new ArrayList<>();
			for(Unit u: myUnits.values()){
				if(!mp.containsKey(u.id())){
					deadUnits.add(u);
					if(u.getUnitDirector() != null){
						u.getUnitDirector().removeUnit(u);
					}
//					System.err.println("死亡者   ID:" + u.id() + " Type:" + u.type());
				}
			}
			
			myUnits = mp;
		}

		{
//			opLastUnits = new HashMap<>(opUnits);
			// 敵のユニットを読み込み
			Map<Integer, Unit> mp = new HashMap<Integer, Unit>();
			int opNum = scanner.nextInt();
			for (int i = 0; i < opNum; i++) {
				
				int id = scanner.nextInt();
				int y = scanner.nextInt();
				int x = scanner.nextInt();
				int hp = scanner.nextInt();
				UnitType type = UnitType.getType(scanner.nextInt());
				
				if(!isTopLeft){
					x = 99 - x;
					y = 99 - y;
				}
				
				Unit u;
				if(opUnits.containsKey(id)){
					u = opUnits.get(id);
					u.update(x, y, hp);
				}else{
					u = new Unit(id, x, y, type);
				}
				mp.put(id, u);
				if (u.type() == UnitType.CASTLE) opCastle = u;
			}
			opUnits = mp;
		}

		{
			// 資源の位置を読み込み
			int resNum = scanner.nextInt();
			for (int i = 0; i < resNum; i++) {
				int y = scanner.nextInt();
				int x = scanner.nextInt();
				if(!isTopLeft){
					x = 99 - x;
					y = 99 - y;
				}
				if(!isFoundResource(x, y)){
					resources.add(new Vector(x, y));
				}
			}
			
			//現在の収入を計算
			income = 10;
			for(Unit u: myUnits.values()){
				if(u.type() == UnitType.WORKER && isFoundResource(u.x(), u.y())){
					income++;
				}
			}
		}

		String end = scanner.next();
		if ("END".equals(end)) return true;
		return false;
	}
	
	/**
	 * その座標に資源が存在しているのを確認済か
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean isFoundResource(int x, int y){
		for(Vector res: resources){
			if(res.getX() == x && res.getY() == y){
				return true;
			}
		}
		return false;
	}

/*	
	// ワーカーの行動を考える
	void thinkWorker() {
		int[][] workerNum = new int[100][100]; // そのマスにワーカーが何人いるか。移動中のワーカーは目的地のマスでカウント。
		// 現状、マスごとにワーカーが何人いるか（向かっているか）を数える。
		for (Unit u : myUnits.values()) {
			if (u.type() == Type.WORKER) {
				if (u.isFree()) {
					workerNum[u.y()][u.x()]++;
				}
				if (u.isMoving()) {
					workerNum[u.movetoy][u.movetox]++;
				}
			}
		}

		// 命令を設定
		for (Unit u : myUnits.values()) {
			if (u.type == WORKER) {
				// 目的地についているなら命令を再設定
				if (u.isMoving() && u.y == u.movetoy && u.x == u.movetox) {
					u.free();
				}

				if (u.isFree()) {
					if (resource[u.y][u.x]) {
						// 資源上にいるなら何もしない
					} else {
						// 最も近い、獲得人数にあきがある資源へ向かう
						int ty = -1, tx = -1;
						for (int y = 0; y < 100; y++) for (int x = 0; x < 100; x++) {
							if (resource[y][x] && workerNum[y][x] < 5) {
								if (ty == -1 || dist(u.y, u.x, ty, tx) > dist(u.y, u.x, y, x)) {
									ty = y;
									tx = x;
								}
							}
						}
						if (ty == -1) {
							// 最も近い見えてないマスへ向かう
							for (int y = 0; y < 100; y++) for (int x = 0; x < 100; x++) {
								if (!see[y][x]) {
									if (ty == -1 || dist(u.y, u.x, ty, tx) > dist(u.y, u.x, y, x)) {
										ty = y;
										tx = x;
									}
								}
							}
						}
						if (ty != -1) {
							u.movetoy = ty;
							u.movetox = tx;
							workerNum[u.y][u.x]--;
							workerNum[u.movetoy][u.movetox]++;
						}
					}
				}
			}
		}
	}

	// 戦闘ユニットの行動を考える
	void thinkWarrior() {
		// 命令を設定
		for (Unit u : myUnits.values()) {
			if (u.type == KNIGHT || u.type == FIGHTER || u.type == ASSASSIN) {
				// 敵の城へ向かう
				int ty = -1, tx = -1;
				if (opCastle != null) {
					ty = opCastle.y;
					tx = opCastle.x;
				} else {
					// 敵の城の位置が不明なら敵の城がありそうな場所へ向かう
					int opCorner = isTopLeft ? 99 : 0;
					for (int y = 0; y < 100; y++) for (int x = 0; x < 100; x++) {
						if (!see[y][x]) {
							if (ty == -1 || dist(opCorner, opCorner, ty, tx) > dist(opCorner, opCorner, y, x)) {
								ty = y;
								tx = x;
							}
						}
					}
				}
				u.movetoy = ty;
				u.movetox = tx;
			}
		}
	}

	// 生産するユニットの行動を考える
	void thinkBuild() {
		int workerCount = 0; // 自分のワーカーの個数
		int warriorFactoryCount = 0; // 自分の拠点の個数
		// 現在のユニット数を数える
		for (Unit u : myUnits.values()) {
			if (u.type == WORKER) {
				workerCount++;
			}
			if (u.type == WARRIOR_FACTORY) {
				warriorFactoryCount++;
			}
		}

		// 命令を設定
		for (Unit u : myUnits.values()) {
			// ワーカーの数が一定数に満たないなら城でワーカーを作る
			if (u.type == CASTLE && workerCount < 100) {
				if (currentResource >= BUILD_COST[WORKER]) {
					currentResource -= BUILD_COST[WORKER];
					u.produce = WORKER;
				}
			}
			// 拠点が一つもないならワーカーで拠点を作る
			if (u.type == WORKER && warriorFactoryCount == 0 && u.isFree()) {
				if (currentResource >= BUILD_COST[WARRIOR_FACTORY]) {
					currentResource -= BUILD_COST[WARRIOR_FACTORY];
					u.produce = WARRIOR_FACTORY;
				}
			}
			// 拠点でナイト・ファイター・アサシンをランダムに作る
			if (u.type == WARRIOR_FACTORY) {
				int createdUnitType = KNIGHT + rand(3);
				if (currentResource >= BUILD_COST[createdUnitType]) {
					currentResource -= BUILD_COST[createdUnitType];
					u.produce = createdUnitType;
				}
			}
		}
	}

*/	
/*	
	void think() {
		for(Unit u: myUnits.values()){
			if(u.type() == UnitType.CASTLE){
				u.setProduce(UnitType.WORKER);
			}else{
				Direction[] ds = {Direction.DOWN, Direction.RIGHT};
				int rand = new Random().nextInt(ds.length);
				Direction d = ds[rand];
				u.setDir(d);
			}
		}
*/		
		// ユニットに命令を設定する
/*		thinkWorker();
		thinkWarrior();
		thinkBuild();
		}
*/
	
	private void output(){
		ArrayList<String> outputs = new ArrayList<String>();
		for (Unit u : myUnits.values()) {
			String s = u.toOrderString(isTopLeft);
			if (s.length() > 0) {
				outputs.add(s);
			}
		}
		System.out.println(outputs.size()); // 出力のはじめに命令の個数を出力
		for (String s : outputs){
			System.out.println(s); // 命令を一行ずつ出力
		}
		System.out.flush();
	}
	
	/**
	 * Unitのupdateは全Unitで必ず呼ばれるからこれを使う必要はなさそう．
	 */
	public void freeUnits(){
		for(Unit u: myUnits.values()){
			u.free();
		}
	}
	
	public static void main(String[] args) {
		God ai = new God();
		// AI の名前を出力
		
		System.out.println("kengo009");
		System.out.flush(); // 忘れずに標準出力をフラッシュする
		
		
		while (ai.input()) { // 入力が読めない場合には false を返すのでループを抜ける
//			ai.freeUnits();
//			System.err.println(ai.stateDirector.getClass().getName());
			ai.stateChanger.checkIsSilber();
			ai.stateChanger.think();
			ai.stateDirector.think();;
//			ai.think();
			ai.output();
//			ai.unitNumOutPut(1);
		}
	}
	
	private void unitNumOutPut(int id){
		Unit u = getMyUnits().get(id);
		System.err.println("次のIDのユニット情報:" + id);
		if(u == null){
			System.err.println("ユニットがヌル");
		}else if(u.getUnitDirector() == null){
			System.err.println("ディレクターがヌル");
		}else{
			System.err.println(u.getUnitDirector().getClass().getName());
		}
	}
	
	public List<Unit> getOppUnitsAt(Vector pos){
		List<Unit> opps = new ArrayList<>();
		for(Unit opp: opUnits.values()){
			if(opp.point().equals(pos)){
				opps.add(opp);
			}
		}
		return opps;
	}
	
	public boolean hasFoundOpCastle(){
		if(opCastle != null){
			return true;
		}else return false;
	}
	
	public int getMaxDamageAt(Vector pos, UnitType type){
		int damage = 0;
		for(Unit opp: opUnits.values()){
			if(pos.getMhtDist(opp.point()) <= opp.type().getAttackRenge() + 1){
				damage += type.damegeFrom(opp.type());
			}
		}
		return damage;
	}
	
	public List<Unit> getUnits(){
		return new ArrayList<>(myUnits.values());
	}

	public List<Unit> getUnits(UnitType type){
		return UnitListFactory.getUnitsTypeOf(myUnits.values(), type);
	}
	
	public List<Unit> getUnits(Collection<UnitType> types){
		return UnitListFactory.getUnits(myUnits.values(), types);
	}
	
	public List<Unit> getMovableUnits(){
		return UnitListFactory.getMovableUnits(myUnits.values());
	}
	
	public List<Unit> getUnMovableUnits(){
		return UnitListFactory.getUnMovableUnits(myUnits.values());
	}
	
	public List<Unit> getVillagesAndCastle(){
		List<UnitType> types = new ArrayList<>();
		types.add(UnitType.VILLAGE);
		types.add(UnitType.CASTLE);
		return getUnits(types);
	}
	
	/**
	 * 引数がTrueならば，UnitDirectorに所属していないUnit集合．
	 * Falseならば，所属しているUnit集合
	 * どちらもmovableなもののみ
	 * @param isNeet
	 * @return
	 */
	public List<Unit> getNeetUnits(boolean isNeet){
		List<Unit> units = new ArrayList<>();
		for(Unit unit: myUnits.values()){
			if( (unit.isNeet() == isNeet) && unit.type().isMovable()){
				units.add(unit);
			}
		}
		return units;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public StateDirector getStateDirector() {
		return stateDirector;
	}

	public int getRemainingTime() {
		return remainingTime;
	}

	public int getCurrentStage() {
		return currentStage;
	}

	public int getCurrentTurn() {
		return currentTurn;
	}

	public int getCurrentResource() {
		return currentResource;
	}

	public int getIncome() {
		return income;
	}

	public Unit getMyCastle() {
		return myCastle;
	}

	public Unit getOpCastle() {
		return opCastle;
	}

	public boolean isTopLeft() {
		return isTopLeft;
	}

	public Map<Integer, Unit> getMyUnits() {
		return myUnits;
	}

	public Map<Integer, Unit> getOpUnits() {
		return opUnits;
	}

	public boolean[][] getSee() {
		return see;
	}

	public Set<Vector> getResources() {
		return resources;
	}

	public StateChanger getStateChanger() {
		return stateChanger;
	}

	public void setStateDirector(StateDirector stateDirector) {
		this.stateDirector = stateDirector;
	}

	public boolean[][] getCastlePosible() {
		return castlePosible;
	}
}

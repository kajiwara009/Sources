package unitDirector.worker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import stateDirector.StateDirector;
import twinDirector.TwinDirector;
import unitDirector.UnitDirector;
import codevs.God;
import data.Direction;
import data.Unit;
import data.Vector;

public class FirstSearchDirector extends UnitDirector{
	
	private Map<Unit, List<Direction>> orderMap = new HashMap<>();

	public FirstSearchDirector(God god) {
		super(god);
		// TODO 自動生成されたコンストラクター・スタブ
	}
	

	@Override
	public void moveUnits() {
		int turn = god.getCurrentTurn();
		if(turn > 99){
			return;
		}
		if(turn == 0){
			if(units.size() != 5){
				System.err.println("FirstSearchDirectorクラス:初期ワーカーが5人じゃありません");
			}else{
				Vector castlePoint = god.getMyCastle().point();
				
				List<List<Direction>> downOrders = getSideTwoOrders(castlePoint, true);
				List<List<Direction>> rightOrders = getSideTwoOrders(castlePoint, false);
				List<Direction> middleOrder = getMiddleOrder(downOrders.get(1), rightOrders.get(1));
				
				List<List<Direction>> fiveOrders = new ArrayList<>();
				fiveOrders.addAll(downOrders);
				fiveOrders.addAll(rightOrders);
				fiveOrders.add(middleOrder);
				
				for(int i = 0; i < units.size(); i++){
					orderMap.put(units.get(i), fiveOrders.get(i));
				}
			}
		}
		
		//各UnitがorderMapに配属され終わったからそれにしたがって行動するのみ
		for(Unit unit: units){
			unit.setDir(orderMap.get(unit).get(turn));
		}
		if(turn > 85){
			removeAllUnit();
		}

/*		for(Entry<Unit, List<Direction>> set: orderMap.entrySet()){
			set.getKey().setDir(set.getValue().get(turn));
		}
*/	
	}
	
	
	private List<Direction> getMiddleOrder(List<Direction> downSecond, List<Direction> rightSecond){
		List<Direction> order = new ArrayList<>();
		Vector middleV = new Vector(0, 0);
		Vector downV = new Vector(0, 0);
		Vector rightV = new Vector(0, 0);
		for(int i = 0; i < Math.min(downSecond.size(), rightSecond.size()); i++){
			downV = downV.plus(downSecond.get(i).getVector());
			rightV = rightV.plus(rightSecond.get(i).getVector());
			
			Direction dir;
			if(middleV.getMhtDist(downV) > middleV.getMhtDist(rightV)){
				dir = Direction.DOWN;
			}else{
				dir = Direction.RIGHT;
			}
			order.add(dir);
			middleV = middleV.plus(dir.getVector());
		}
		return order;
	}
		
	
	/**
	 * 下方向に向かう2Unitの最初の命令を返す．
	 * isDownSideがFalseならば右方向のUnit
	 * @param isDownSide
	 * @return
	 */
	private List<List<Direction>> getSideTwoOrders(Vector castle, boolean isDownSide){
		List<List<Direction>> orders = new ArrayList<>();
		List<Direction> firstOrder = new ArrayList<>();
		List<Direction> secondOrder = new ArrayList<>();
		orders.add(firstOrder);
		orders.add(secondOrder);
		//isDownSideによって，命令方向が逆になる
		Direction downSide = isDownSide? Direction.DOWN: Direction.RIGHT;
		Direction rightSide = isDownSide? Direction.RIGHT: Direction.DOWN;
		int x = isDownSide? castle.getX(): castle.getY();
		int y = isDownSide? castle.getY(): castle.getX();
		
		if(y < 4){
			for(; y < 4; y++){
				firstOrder.add(downSide);
				secondOrder.add(downSide);
			}
		}
		if(x < 10){
			if(x < 4){
				for(; x < 4; x++){
					firstOrder.add(rightSide);
					secondOrder.add(rightSide);
				}
			}
		}else if(x < 20){
			for(int i = x; i > 10; i--){
				firstOrder.add(rightSide.getReverse());
			}
		}else {
			for(int i = x; i > 10; i--){
				Direction firstDir = rightSide.getReverse();
				if(y < 10 && i%5 == 0){
					firstDir = downSide;
					y++;
				}
				firstOrder.add(firstDir);
			}
			for(int i = x; i > 20; i--){
				Direction secondDir = i%3 == 0? rightSide.getReverse(): downSide;
				secondOrder.add(secondDir);
			}
		}
		
		//最初の微調整の後は全部同じ
		for(int i = 0; i < 100; i++){
			Direction firstDir = downSide;
			if(x < 10 && i >= 24 && i%4 == 0){
				firstDir = rightSide;
				x++;
			}
			firstOrder.add(firstDir);
			Direction secondDir = i%4 == 0? rightSide: downSide;
			secondOrder.add(secondDir);
		}
		
		
		return orders;
	}


}

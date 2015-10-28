package data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UnitCrowd implements Composite {
	List<Unit> member = new ArrayList<>();

	public UnitCrowd() {
	}
	
	public UnitCrowd(Collection<Composite> composites){
		addAllComposites(composites);
	}

	public void addComposite(Composite c){
		if(c instanceof Unit){
			member.add((Unit)c);
		}else if(c instanceof UnitCrowd){
			UnitCrowd uc = (UnitCrowd)c;
			member.addAll(uc.getMember());
		}
	}
	
	public void addAllComposites(Collection<Composite> composites){
		for(Composite c: composites){
			addComposite(c);
		}
	}
	
	public List<Unit> getMember(){
		return new ArrayList<>(member);
	}
	
	public void remove(Composite c){
		if(c instanceof Unit){
			member.remove((Unit)c);
		}else if(c instanceof UnitCrowd){
			UnitCrowd uc = (UnitCrowd)c;
			member.removeAll(uc.getMember());
		}
	}
	
	public void removeAllComposite(Collection<Composite> composites){
		for(Composite c: composites){
			remove(c);
		}
	}

	@Override
	public boolean isMoving() {
		if(member.size() == 0){
			System.err.println("UnitCrowd.isMove: memberのサイズが0です");
			return false;
		}else{
			boolean isMove = member.get(0).isMoving();
			for(Unit u: member){
				if(isMove != u.isMoving()){
					System.err.println("UnitCrowd.isMove: memberが同調してません");
					break;
				}
			}
			return isMove;
		}
	}

	@Override
	public Vector point() {
		if(member.size() == 0){
			System.err.println("UnitCrowd.point: memberのサイズが0です");
			return null;
		}else{
			Vector vec = member.get(0).point();
			for(Unit u: member){
				if(vec != u.point()){
					System.err.println("UnitCrowd.point: memberが同調してません");
					break;
				}
			}
			return vec;
		}
	}

	@Override
	public int y() {
		if(member.size() == 0){
			System.err.println("UnitCrowd: memberのサイズが0です");
			return 0;
		}else{
			int y = member.get(0).y();
			for(Unit u: member){
				if(y != u.y()){
					System.err.println("UnitCrowd: memberが同調してません");
					break;
				}
			}
			return y;
		}
	}

	@Override
	public int x() {
		if(member.size() == 0){
			System.err.println("UnitCrowd: memberのサイズが0です");
			return 0;
		}else{
			int x = member.get(0).x();
			for(Unit u: member){
				if(x != u.x()){
					System.err.println("UnitCrowd: memberが同調してません");
					break;
				}
			}
			return x;
		}
	}

	@Override
	public Direction getDir() {
		if(member.size() == 0){
			System.err.println("UnitCrowd: memberのサイズが0です");
			return null;
		}else{
			Direction dir = member.get(0).getDir();
			for(Unit u: member){
				if(dir != u.getDir()){
					System.err.println("UnitCrowd: memberが同調してません");
					break;
				}
			}
			return dir;
		}
	}

	@Override
	public void setDir(Direction dir) {
		for(Unit u: member){
			u.setDir(dir);
		}
	}

	@Override
	public int getLastX() {
		if(member.size() == 0){
			System.err.println("UnitCrowd: memberのサイズが0です");
			return 0;
		}else{
			int lastX = member.get(0).getLastX();
			for(Unit u: member){
				if(lastX != u.getLastX()){
					System.err.println("UnitCrowd: memberが同調してません");
					break;
				}
			}
			return lastX;
		}
	}

	@Override
	public int getLastY() {
		if(member.size() == 0){
			System.err.println("UnitCrowd: memberのサイズが0です");
			return 0;
		}else{
			int lastY = member.get(0).getLastY();
			for(Unit u: member){
				if(lastY != u.getLastY()){
					System.err.println("UnitCrowd: memberが同調してません");
					break;
				}
			}
			return lastY;
		}
	}

	@Override
	public Vector getLastPoint() {
		if(member.size() == 0){
			System.err.println("UnitCrowd: memberのサイズが0です");
			return null;
		}else{
			Vector lastP = member.get(0).getLastPoint();
			for(Unit u: member){
				if(lastP != u.getLastPoint()){
					System.err.println("UnitCrowd: memberが同調してません");
					break;
				}
			}
			return lastP;
		}
	}

	@Override
	public boolean isStay() {
		if(member.size() == 0){
			System.err.println("UnitCrowd.isStay: memberのサイズが0です");
			return false;
		}else{
			boolean isStay = member.get(0).isStay();
			for(Unit u: member){
				if(isStay != u.isStay()){
					System.err.println("UnitCrowd.isStay: memberが同調してません");
					break;
				}
			}
			return isStay;
		}
	}

	@Override
	public void setStay(boolean stay) {
		for(Unit u: member){
			u.setStay(stay);
		}
	}

}

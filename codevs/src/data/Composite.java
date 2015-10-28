package data;

import unitDirector.UnitDirector;

public interface Composite {
	
	public abstract boolean isMoving();
	public abstract Vector point();
	public abstract int y();
	public abstract int x();
	public abstract Direction getDir();
	public abstract void setDir(Direction dir);
	public abstract int getLastX();
	public abstract int getLastY();
	public abstract Vector getLastPoint();
	public abstract boolean isStay();
	public abstract void setStay(boolean stay);
}

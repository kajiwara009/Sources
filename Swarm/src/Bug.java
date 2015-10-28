import swarm.*;
import swarm.objectbase.*;
import swarm.defobj.*;
import swarm.space.*;
import swarm.gui.*;
import java.lang.*;

public class Bug extends SwarmObjectImpl {
	int xPos, yPos;
	int lastX, lastY;
	int worldXSize, worldYSize;
	FoodSpace foodSpace;
	Grid2d world;
	float direction; /* radian */
	float speed;
	float maxSpeed = 4.0f;
	float minSpeed = 2.0f;
	float accel = 1.1f;
	float optDistance = 5.0f;
	int searchSpace = 10;
	float gravityWeight = 0.05f;
	float nearWeight = 0.15f;
	int haveEaten;

	public Bug(Zone aZone){
		super(aZone);
	}
	
	public Object SetParameters( float pMaxSpeed, float pMinSpeed, float pAccel,
			float pOptDistance, int pSearchSpace, float pGravityWeight, float pNearWeight ){
		maxSpeed = pMaxSpeed;
		minSpeed = pMinSpeed;
		accel = pAccel;
		optDistance = pOptDistance;
		searchSpace = pSearchSpace;
		gravityWeight = pGravityWeight;
		nearWeight = pNearWeight;
		return this;
	}

	public Object setWorld$Food(Grid2d w,FoodSpace f){
		world=w;
		foodSpace=f;
		worldXSize = world.getSizeX();
		worldYSize = world.getSizeY();
		return this;
	}
	
	public Object setX$Y(int x, int y){
		xPos = x;
		yPos = y;
		world.putObject$atX$Y(this,x,y);
		return this;
	}

	public Object setSpeed( float s ){
		speed = s;
		return this;
	}

	public Object setDirection( float d ){
		direction = d/360.0f * (float)Math.PI; /* d : 0 - 360 */
		return this;
	}

	public Object setWorldSizeX$Y(int xSize, int ySize){
		worldXSize=xSize;
		worldYSize=ySize;
		return this;
	}
	
	public float GetDirection(){
		return this.direction;
	}

	public void step(){
		int newX, newY;
		float newSpeed = speed, newDirection = direction;
		/* scan bugs */
		/* calc gravity point and search nearest bugs*/
		int gX = 0, gY = 0; /* position of gravity point */
		int Num = 0; /* number of bugs in search space */
		float minDist = (float)( searchSpace * 2 * searchSpace * 2 ); /* minimum of distance to bugs in search space */
		int minDX = 0, minDY = 0; /* position of nearest bugs */
		for( int i = ( xPos - searchSpace ); i <= ( xPos + searchSpace ); i++ ){
			for( int j = ( yPos - searchSpace ); j <= ( yPos + searchSpace ); j++ ){
				if( ( i != xPos ) && ( j != yPos ) ){
					int sx = ( i + worldXSize ) % worldXSize;
					int sy = ( j + worldYSize ) % worldYSize;
					float dist;
					if( null != world.getObjectAtX$Y( sx, sy ) ){
						gX += i;
						gY += j;
						Num++;
						/* chech distance */
						dist = ( i-xPos ) * ( i - xPos ) + ( j - yPos ) * ( j - yPos );
						if( dist < minDist ){
							minDist = dist;
							minDX = i;
							minDY = j;
						}
					}
				}
			}
		}
		/* chech Num: if Num = 0, speed and dir dont change */
		if( Num > 0 ){
			gX /= Num;
			gY /= Num;
			Bug nearestBug = (Bug)world.getObjectAtX$Y( ( minDX + worldXSize ) % worldXSize, ( minDY + worldYSize ) % worldYSize );

			/* calc direction */
			float gVX = 0.0f, gVY = 0.0f; /* vector to the gravity point */
			float tmp = (float)Math.sqrt( (float)( ( gX - xPos ) * ( gX - xPos ) + ( gY - yPos ) * ( gY - yPos ) ) );
			gVX = (float)( gX - xPos ) / tmp; gVY = (float)( gY - yPos ) / tmp;
			float sVX = 0.0f, sVY = 0.0f; /* speed direction vector of bug */
			sVX = (float)Math.cos( direction ); sVY = (float)Math.sin( direction );
			float nVX = 0.0f, nVY = 0.0f; /* speed vector of nearest bug */
			nVX = (float)Math.cos( nearestBug.direction );
			nVY = (float)Math.sin( nearestBug.direction );
			float fVX, fVY; /* vector of nect direction */
			fVX = gravityWeight * gVX + sVX + nearWeight * nVX;
			fVY = gravityWeight * gVY + sVY + nearWeight * nVY;
			newDirection = Vector2Direction( fVX, fVY );

			/* calc speed */
			float dX = (float)(minDX - xPos); /* vector from current bug to the nearest bug */
			float dY = (float)(minDY - yPos);
			float inner = dX * fVX + dY * fVY;
			float nearestDist = (float)Math.sqrt( dX * dX + dY * dY );
			if( inner > 0 ){ /* if nearest bug is before this... */
				if( nearestDist > optDistance ){
					newSpeed = speed * accel;
				}else{
					newSpeed= speed / accel;
				}
			}else{
				if( nearestDist > optDistance ){
					newSpeed= speed / accel;
				}else{
					newSpeed = speed * accel;
				}
			}

		}

		/* check speed */
		if( newSpeed > maxSpeed ){
			newSpeed = maxSpeed;
		}
		if( newSpeed < minSpeed ){
			newSpeed = minSpeed;
		}

		/* move */
		newX = xPos + (int)( newSpeed * Math.cos( newDirection ) );
		newY = yPos + (int)( newSpeed * Math.sin( newDirection ) );
		newX = (newX + worldXSize) % worldXSize;
		newY = (newY + worldYSize) % worldYSize;
		if (world.getObjectAtX$Y(newX,newY) == null){
			world.putObject$atX$Y(null,xPos,yPos);
			lastX = xPos;
			lastY = yPos;
			xPos = newX;
			yPos = newY;
			speed = newSpeed;
			direction = newDirection;
			world.putObject$atX$Y(this,newX,newY);
		}
	}
	
	public Object drawSelfOn(Raster r){
		r.drawPointX$Y$Color(xPos,yPos,(byte)2);
		r.drawPointX$Y$Color(lastX, lastY, (byte)1 );
		return this;
	}

	public float Vector2Direction( float vx, float vy ){
		return (float)Math.atan( vy / vx );
	}
}

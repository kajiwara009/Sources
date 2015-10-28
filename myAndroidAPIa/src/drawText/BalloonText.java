package drawText;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.PathShape;

public class BalloonText {
	String text;
	Paint textPaint;
	Paint balloonPaint;
	Paint balloonShadowPaint;
	

	public BalloonText(String text){
		this.text = text;
		
		textPaint = new Paint( Paint.ANTI_ALIAS_FLAG);
		textPaint.setTextSize( 20);
		textPaint.setColor( Color.WHITE);
		
		balloonPaint = new Paint( Paint.ANTI_ALIAS_FLAG);
		//balloonPaint.setTextSize( 35);
		balloonPaint.setColor( Color.LTGRAY);
		
		balloonPaint = new Paint( Paint.ANTI_ALIAS_FLAG);
		//balloonPaint.setTextSize( 35);
		balloonPaint.setColor( Color.GRAY);
		
	}
	
	public void drawBalloonText(Canvas canvas, float baseX, float baseY){
		FontMetrics fontMetrics = textPaint.getFontMetrics();

		// 文字列の幅を取得
		float textWidth = textPaint.measureText( text);
		
		// 文字列の幅からX座標を計算
		float textX = baseX - textWidth / 2;
		// 文字列の高さからY座標を計算
		float textY = baseY - (fontMetrics.ascent + fontMetrics.descent) / 2;
	
		
		// 吹き出しの描画
		float balloonStartX = textX - 5;
		float balloonEndX = textX + textWidth + 5;
		float balloonStartY = textY + fontMetrics.ascent - 5;
		float balloonEndY = textY + fontMetrics.descent + 5; 

		float balloonCenterX = ( balloonStartX + balloonEndX) / 2;
		
		// 各座標を+2して吹き出しの影を描画
		RectF balloonShadowRectF = new RectF( balloonStartX + 2, balloonStartY + 2, balloonEndX + 2, balloonEndY + 2);
		canvas.drawRoundRect(balloonShadowRectF, 5, 5, balloonShadowPaint);
		
		// ポイントを指す三角形を描画
		int triangleWidth = (int)textX / 2;
		int triangleHeight = triangleWidth / 2;
		
		// 三角形の頂点を表わすポイントの生成
		Point point = new Point( (int) balloonCenterX - triangleWidth / 2, (int)balloonEndY);
		Point[] cornerPoint = new Point[3];
		cornerPoint[ 0] = new Point( 0, 0);
		cornerPoint[ 1] = new Point( triangleWidth, 0);
		cornerPoint[ 2] = new Point( triangleWidth / 2, triangleHeight);
		
		drawShape(canvas, point, cornerPoint, balloonShadowPaint.getColor());
		
		// 吹き出しの描画
		RectF balloonRectF = new RectF( balloonStartX, balloonStartY, balloonEndX, balloonEndY);
		canvas.drawRoundRect(balloonRectF, 5, 5, balloonPaint);

		// 文字列の描画
		canvas.drawText( text, textX, textY, textPaint);
	}
	
	/**
	 * 多角形の描画
	 * @param canvas
	 * @param point 描画位置
	 * @param corners 描画する頂点を表わすポイント
	 * @param color 描画色
	 */
	private void drawShape( Canvas canvas, Point point, Point[] corners, int color){
		float maxWidth = 0;
		float maxHeight = 0;
		
		// Pointを結ぶ頂点のパス作成
        Path path = new Path();
        for( int cornerCnt = 0; cornerCnt < corners.length; cornerCnt++){
        	int x = corners[ cornerCnt].x;
        	int y = corners[ cornerCnt].y;
        	// はじめの頂点はmoveTo
        	if( cornerCnt == 0){
                path.moveTo( x, y);
        	}
        	else{
        		path.lineTo( x, y);
        	}
        	
        	if( maxWidth < x){
        		maxWidth = x;
        	}
        	if( maxHeight < y){
        		maxHeight = y;
        	}
        }
        // パスを閉じる
        path.close();
        
        ShapeDrawable drawable = new ShapeDrawable(new PathShape(path, maxWidth, maxHeight));
        drawable.getPaint().setColor( color);
        drawable.setBounds( point.x, point.y, point.x + (int)maxWidth, point.y + (int)maxHeight);
        drawable.draw( canvas);
	}
	
	public int setTextSize(int size){
		if(size <= 0){
			return -1;
		}else{
			textPaint.setTextSize(size);
			return 1;
		}
	}
	
	public int setTextColor( int color){
		return setPaintColor(textPaint, color);
	}
	
	public int setBalloonColor(int color){
		return setPaintColor(balloonPaint, color);
	}
	
	public int setBalloonShadowColor(int color){
		return setPaintColor(balloonShadowPaint, color);
	}
	
	private int setPaintColor(Paint paint, int color){
		if(color <= 0){
			return -1;
		}else{
			paint.setColor(color);
			return 1;
		}
	}
	
	public float getBalloonWidth(){
		return textPaint.measureText(text) + 10;
	}
	
	public float getBalloonHeight(){
		FontMetrics fontMetrics = textPaint.getFontMetrics();
		
		return fontMetrics.descent - fontMetrics.ascent + 10;
	}

}

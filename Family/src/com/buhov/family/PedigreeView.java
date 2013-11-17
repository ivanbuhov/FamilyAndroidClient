package com.buhov.family;

import java.util.ArrayList;

import com.buhov.family.PedigreeNode.PersonNode;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;

public class PedigreeView extends ZoomablePannableView {

	private PointF center;
	
	private float rectangleWidth = 200;
	private float rectangleHeight = 100;
	
	private Paint malePaint;
	private Paint femalePaint;
	private Paint anonymousPaint;
	private Paint linePaint;
	private Paint textPaint;
	private Paint blackLinePaint;
	
	private PersonNode personNode;
	private String anonymousDisplayName;
	private String noPeopleMessage;
	
	public PedigreeView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		this.linePaint = new Paint();
		this.linePaint.setColor(Color.BLACK);
		this.linePaint.setStrokeWidth(5);
		
		this.malePaint = new Paint();
		this.malePaint.setColor(Color.BLUE);
		this.malePaint.setStrokeWidth(3);
		
		this.anonymousPaint = new Paint();
		this.anonymousPaint.setColor(Color.GRAY);
		this.anonymousPaint.setStrokeWidth(3);
		
		this.femalePaint = new Paint();
		this.femalePaint.setColor(Color.MAGENTA);
		this.femalePaint.setStrokeWidth(3);
		
		this.textPaint = new Paint();
		this.textPaint.setColor(Color.BLACK);
		this.textPaint.setTextAlign(Align.CENTER);
		this.textPaint.setTextSize(20);
		this.textPaint.setStyle(Style.FILL);
		
		this.blackLinePaint = new Paint();
		this.blackLinePaint.setColor(Color.BLACK);
		this.blackLinePaint.setStyle(Style.FILL);
		
		this.center = new PointF(0f, 0f);
		this.anonymousDisplayName = "Unknown";
		this.noPeopleMessage = "There is no people in the tree.";
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if(this.personNode == null) {
			canvas.drawText(this.noPeopleMessage, this.center.x, this.center.y, this.textPaint);
		}
		else {
			this.drawTree(canvas, this.personNode);
		}
		//textView.measure(MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(200, MeasureSpec.EXACTLY));
	    //textView.layout(0, 0, 0 + 200, 0 + 200);
	    //textView.setGravity(Gravity.CENTER);
		canvas.restore();
	}
	
	private void drawTree(Canvas canvas, PersonNode person) {
		this.drawPersonOrAnonymous(canvas, this.personNode, this.center);
		this.drawParentsTree(canvas, person, this.center);
		if(person.getParentsCount() > 0) {
			this.drawSiblings(canvas, person, this.center, true);
		}
		if(person.getChildrenCount() > 0) {
			this.drawSpouseOrAnonymous(canvas, person, this.center);
			this.drawChildren(canvas, person, this.center);
		}
	}
	
	private boolean drawSpouseOrAnonymous(Canvas canvas, PersonNode person, PointF personCenter) {
		canvas.drawLine(personCenter.x + (this.rectangleWidth / 2), personCenter.y, 
				personCenter.x + this.rectangleWidth, personCenter.y, this.linePaint);
		PointF spouseCenter = new PointF(personCenter.x + this.rectangleWidth * 1.5f, personCenter.y);
		return this.drawPersonOrAnonymous(canvas, person.getSpouse(), spouseCenter);
	}
	
	private void drawParentsTree(Canvas canvas, PersonNode person, PointF personCenter) {
		if(person.hasAnyParents()) {
			PersonNode[] parents = person.getParents();
			float halfLine = this.rectangleWidth;
			PointF parentCenter = new PointF(0, 0);
			
			if(this.hasWideParentsView(person)) {
				parentCenter = this.drawParents(canvas, person, personCenter, true);
			}
			else {
				parentCenter = this.drawParents(canvas, person, personCenter, false);
				halfLine /= 4;
			}
			
			if(parents[0] != null && parents[0].hasAnyParents()) {
				PointF center = new PointF(parentCenter.x - halfLine - (this.rectangleWidth / 2), parentCenter.y);
				this.drawParents(canvas, parents[0], center, false);
			}

			if(parents[1] != null && parents[1].hasAnyParents()) {
				PointF center = new PointF(parentCenter.x + halfLine + (this.rectangleWidth / 2), parentCenter.y);
				this.drawParents(canvas, parents[1], center, false);
			}
		}
	}
	
	private PointF drawParents(Canvas canvas, PersonNode person, PointF personCenter, boolean wide) {
		PersonNode[] parents = person.getParents();
		float destY = personCenter.y - this.rectangleHeight * 2.5f;
		canvas.drawLine(personCenter.x, personCenter.y - (this.rectangleHeight / 2), 
				personCenter.x, destY, this.linePaint);
		
		float halfLine = this.rectangleWidth;
		if(!wide) {
			halfLine /= 4;
		}
		
		canvas.drawLine(personCenter.x - halfLine, destY, 
				personCenter.x + halfLine, destY, this.linePaint);
		PointF fatherCenter = new PointF(personCenter.x - halfLine - (this.rectangleWidth / 2), destY);
		PointF motherCenter = new PointF(personCenter.x + halfLine + (this.rectangleWidth / 2), destY);
		this.drawPersonOrAnonymous(canvas, parents[0], fatherCenter);
		this.drawPersonOrAnonymous(canvas, parents[1], motherCenter);
		return new PointF(personCenter.x, destY);
	}
	
	private boolean hasWideParentsView(PersonNode person) {
		boolean wide = true;
		if(person.getParentsCount() < 2) {
			wide = false;
		}
		else {
			PersonNode[] parents = person.getParents();
			if(parents[0].getParentsCount() == 0 || parents[1].getParentsCount() == 0) {
				wide = false;
			}
		}
		return wide;
	}
	
	private boolean drawPersonOrAnonymous(Canvas canvas, PersonNode person, PointF centralPoint) {
		boolean isAnonymous = true;
		Paint paint = this.anonymousPaint;
		String displayName = this.anonymousDisplayName;
		if(person != null) {
			paint = person.getData().isMale() ? this.malePaint : this.femalePaint;
			displayName = person.getData().getDisplayName();
			isAnonymous = false;
		}
		
		float xCoord = centralPoint.x - (this.rectangleWidth/2);
		float yCoord = centralPoint.y - (this.rectangleHeight/2);
		canvas.drawRoundRect(new RectF(xCoord, yCoord, xCoord + this.rectangleWidth, yCoord + this.rectangleHeight), 
				0.2f * this.rectangleHeight, 0.2f * this.rectangleHeight, paint);
		//canvas.drawRect(xCoord, yCoord, xCoord + this.rectangleWidth, yCoord + this.rectangleHeight, paint);
		if(!isAnonymous && !person.getData().isAlive()) {
			drawBlackTrianlge(canvas, centralPoint);
		}
		
		if(this.textPaint.measureText(displayName) > 0.9 * this.rectangleWidth) {
			float textWidth = this.textPaint.measureText(displayName);
			float rectWidth = 0.9f * this.rectangleWidth;
			int end = (int)(displayName.length() / (textWidth / rectWidth)) - 3;
			String croppedDisplayName = displayName.substring(0, end) + "...";
			canvas.drawText(croppedDisplayName, centralPoint.x, centralPoint.y, this.textPaint);
		}
		else {
			canvas.drawText(displayName, centralPoint.x, centralPoint.y, this.textPaint);
		}
		return isAnonymous;
	}
	
	private void drawBlackTrianlge(Canvas canvas, PointF personCenter) {
		Path trianlge = new Path();
		PointF rightCorner = new PointF(personCenter.x + this.rectangleWidth / 2, personCenter.y - this.rectangleHeight / 2);
		trianlge.moveTo(rightCorner.x - 0.2f * this.rectangleWidth, rightCorner.y);
		trianlge.lineTo(rightCorner.x - 0.12f * this.rectangleWidth, rightCorner.y);
		trianlge.lineTo(rightCorner.x, rightCorner.y + 0.12f * this.rectangleWidth);
		trianlge.lineTo(rightCorner.x, rightCorner.y + 0.2f * this.rectangleWidth);
		canvas.drawPath(trianlge, this.blackLinePaint);
	}
	
	private void drawSiblings(Canvas canvas, PersonNode person, PointF personCenter, boolean spouseCheck) {
		boolean hasSpouse = (spouseCheck) ? (person.getChildrenCount() > 0) : false;
		ArrayList<PersonNode> siblings = person.getSiblings();
		
		float absXDistance = this.rectangleWidth * 1.5f;
		float yDistance = (this.rectangleHeight * 1.5f);
		
		PointF leftEnd = new PointF(personCenter.x, personCenter.y - yDistance);
		PointF rightEnd = new PointF(personCenter.x, personCenter.y - yDistance);
		
		boolean leftTurn = true;
		for(int i = 0; i < siblings.size(); i++) {
			
			PointF upPoint = new PointF(0,  personCenter.y - yDistance);
			
			if(leftTurn) {
				canvas.drawLine(leftEnd.x, leftEnd.y, leftEnd.x - absXDistance, leftEnd.y, this.linePaint);
				leftEnd.x -= absXDistance;
				upPoint.x = leftEnd.x;
			}
			else {
				float xDistance = (hasSpouse && i <= 1) ? absXDistance * 2 : absXDistance;
				canvas.drawLine(rightEnd.x, rightEnd.y, rightEnd.x + xDistance, rightEnd.y, this.linePaint);
				rightEnd.x += xDistance;
				upPoint.x = rightEnd.x;
			}
			
			canvas.drawLine(upPoint.x, upPoint.y, upPoint.x, upPoint.y + yDistance - (this.rectangleHeight/2), this.linePaint);
			this.drawPersonOrAnonymous(canvas, siblings.get(i), new PointF(upPoint.x, upPoint.y + yDistance));
			
			leftTurn = !leftTurn;
		}
	}
	
	private void drawChildren(Canvas canvas, PersonNode person, PointF personCenter) {
		if(person.getChildrenCount() == 0) {
			return;
		}
		
		ArrayList<PersonNode> children = person.getChildren();
		float firstChildX = personCenter.x + (this.rectangleWidth * 3) / 4;
		float firstChildY = personCenter.y + this.rectangleHeight * 3;
		canvas.drawLine(firstChildX, personCenter.y, firstChildX, firstChildY - (this.rectangleHeight * 0.5f), this.linePaint);
		this.drawPersonOrAnonymous(canvas, children.get(0), new PointF(firstChildX, firstChildY));
		this.drawSiblings(canvas, children.get(0), new PointF(firstChildX, firstChildY), false);
	}
	
	@Override
    public void onSizeChanged (int newWidth, int newHeight, int oldWidth, int oldHeight){
        super.onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
        this.center.x = newWidth / 2;
        this.center.y = newHeight / 2;
    }
	
	public PersonNode getPedigreeNode() {
		return this.personNode;
	}

	public void setPersonNode(PersonNode personNode) {
		this.personNode = personNode;
		this.invalidate();
	}

	public String getAnonymousDisplayName() {
		return this.anonymousDisplayName;
	}
	
	public void setAnonymousDisplayName(String anonymousDisplayName) {
		this.anonymousDisplayName = anonymousDisplayName;
	}
	
	public String getNoPeopleMessage() {
		return this.noPeopleMessage;
	}
	
	public void setNoPeopleMessage(String noPeopleMessage) {
		this.noPeopleMessage = noPeopleMessage;
	}
}

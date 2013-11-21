package com.buhov.family;

import com.buhov.family.FamilyHttpClient.Entities.Person;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class PersonGridAdapter extends BaseAdapter {

	public static final int IMAGE_WIDTH = 80;
	public static final int IMAGE_HEIGHT = 80;
	public static final int IMAGE_PADDING = 8;
	
	private Context context;
	private Person[] people;
	
	public PersonGridAdapter(Context context, Person[] people) {
		this.context = context;
		this.people = people;
	}
	
	@Override
	public int getCount() {
		return this.people.length;
	}

	@Override
	public Object getItem(int position) {
		return this.people[position];
	}

	@Override
	public long getItemId(int position) {
		return this.people[position].getId();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Person currentPerson = (Person)this.getItem(position);
		TextView currentView;
		Drawable image;
		
        if (convertView == null) {  // if it's not recycled, initialize the view
        	currentView = new TextView(this.context);
        } 
        else {
        	currentView = (TextView) convertView;
        }
        
        currentView.setText(currentPerson.getDisplayName());
    	currentView.setGravity(Gravity.CENTER);
    	
    	if(currentPerson.isMale()) {
        	image = this.context.getResources().getDrawable(R.drawable.male_avatar);
        }
        else {
        	image = this.context.getResources().getDrawable(R.drawable.female_avatar);
        }

    	currentView.setCompoundDrawablesWithIntrinsicBounds(null, image, null, null);
    	currentView.setCompoundDrawablePadding(IMAGE_PADDING);
        return currentView;
	}

	

}

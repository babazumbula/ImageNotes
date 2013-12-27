package com.example.imagenotes;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
/**
 * adapter to populate listview with data
 * @author ketan(Visit my <a
 *         href="http://androidsolution4u.blogspot.in/">blog</a>)
 */
public class DisplayAdapter extends BaseAdapter {
	private Context mContext;
	private ArrayList<String> id;
	private ArrayList<String> text;
	private ArrayList<String> fileNamePath;
	

	public DisplayAdapter(Context c, ArrayList<String> fileNamePath, ArrayList<String> text, ArrayList<String> rowId ) {
		this.mContext = c;
		this.text = text;
		this.fileNamePath = fileNamePath;
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return fileNamePath.size();
	}

	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public View getView(int pos, View child, ViewGroup parent) {
		Holder mHolder;
		LayoutInflater layoutInflater;

			if (child == null) {
				layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				child = layoutInflater.inflate(R.layout.listelement, null);
				mHolder = new Holder();
				mHolder.text = (TextView) child.findViewById(R.id.textView);
				mHolder.image = (ImageView) child.findViewById(R.id.imageView);
				child.setTag(mHolder);
				
			} else {
				mHolder = (Holder) child.getTag();
			}
			mHolder.text.setText(text.get(pos));
			mHolder.image.setImageBitmap(BitmapFactory.decodeFile(fileNamePath.get(pos)));

		return child;
	}

	public class Holder {
		TextView text;
		ImageView image;
	
	}

}
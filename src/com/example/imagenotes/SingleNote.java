package com.example.imagenotes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class SingleNote extends Activity{

	
	ImageView image;
	EditText textNotes;
	Button save, cancel;
	String imageFilePath;
	String timeStamp;
	boolean isUpdate;
	DbHelper mHelper;
	String text,id;
	private SQLiteDatabase dataBase;
	InputOutput io;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.singlenote);
		
		save = (Button) findViewById(R.id.btn_save);
		cancel = (Button) findViewById(R.id.btn_cancel);
		
		Bundle extras = getIntent().getExtras();
		imageFilePath = (String) extras.get("filePath");
		timeStamp = (String) extras.get("timeStamp");
		id = (String) extras.getString("id");
		image = (ImageView) findViewById(R.id.imageView);
		textNotes = (EditText) findViewById(R.id.edit_text_textNotes);
		image.setImageBitmap(BitmapFactory.decodeFile(imageFilePath));
		io = new InputOutput();
		isUpdate=getIntent().getExtras().getBoolean("update");
        if(isUpdate){
        	id=getIntent().getExtras().getString("id");
        	text=getIntent().getExtras().getString("text");
        	textNotes.setText(text);
        }
		
        mHelper=new DbHelper(this);
        
		OnClickListener saveListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				saveDataToDB();
			}
		};
		
		OnClickListener cancelListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					deleteImage();
				}
			};
		cancel.setOnClickListener(cancelListener);
		save.setOnClickListener(saveListener);
	}
	
	public void deleteImage(){
		
		if(!isUpdate){
			io.deleteImageFile(imageFilePath);
			Toast.makeText(getApplicationContext(),"Image has been deleted.", 3000).show();
		}
		Intent i = new Intent(SingleNote.this,PhotoIntentActivity.class);
		startActivity(i);
	};
	
	public void saveDataToDB(){
		
		dataBase=mHelper.getWritableDatabase();
		ContentValues values=new ContentValues();
		text = textNotes.getText().toString();
		values.put(DbHelper.KEY_TEXT,text);
		values.put(DbHelper.KEY_FILEPATH,imageFilePath);
		
		
		System.out.println("saving to database");
		if(isUpdate)
		{    
			//update database with new data 
			dataBase.update(DbHelper.TABLE_NAME, values, DbHelper.KEY_ID+"="+id, null);
		}
		else
		{
			//insert data into database
			dataBase.insert(DbHelper.TABLE_NAME, null, values);
		}
		
		 try {
				io.copyOfDB(new File("/data/data/com.example.imagenotes/databases/ImageNote"),new File("mnt/sdcard2/ImageNotes.sqlite"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		//close database
		dataBase.close();
		finish();

	}
	
	


}

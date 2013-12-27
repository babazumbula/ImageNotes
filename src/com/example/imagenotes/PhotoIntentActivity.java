package com.example.imagenotes;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class PhotoIntentActivity extends ListActivity {


	private static final String BITMAP_STORAGE_KEY = "viewbitmap";
	private static final String IMAGEVIEW_VISIBILITY_STORAGE_KEY = "imageviewvisibility";
	private Bitmap mImageBitmap;

	public AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	
	private ArrayList<String> rowId = new ArrayList<String>();
	private ArrayList<String> imageFilePath = new ArrayList<String>();
	private ArrayList<String> textNotes = new ArrayList<String>();
	
	private ListView list;
	private AlertDialog.Builder build;
	private SQLiteDatabase dataBase;
	private DbHelper mHelper;
	InputOutput io;
	private String getAlbumName() {
		return getString(R.string.album_name);
	}

	public  File getAlbumDir() {
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}

	private void handleSmallCameraPhoto(Intent intent) {
		
		Bundle extras = intent.getExtras();
		mImageBitmap = (Bitmap) extras.get("data");

		String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss").format(new Date());
		String filePath = getAlbumDir() + "/" + timeStamp +".jpg";
		try {
		       FileOutputStream out = new FileOutputStream(filePath);
		       mImageBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
		       out.close();
		} catch (Exception e) {
		       e.printStackTrace();
		}
		
		Intent i = new Intent(PhotoIntentActivity.this, SingleNote.class);
		i.putExtra("filePath", filePath);
		i.putExtra("timeStamp", "timeStamp");
		i.putExtra("update", false);
		startActivity(i);
		
	}

	Button.OnClickListener mTakePicSOnClickListener = 
		new Button.OnClickListener() {
		@Override
		public void onClick(View v) {

			Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			startActivityForResult(takePictureIntent, 20);
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photo_intent);
		
		mHelper=new DbHelper(this);
		io = new InputOutput();
		mImageBitmap = null;
	//	list = (ListView) findViewById(R.id.);
		list = getListView();
		Button picSBtn = (Button) findViewById(R.id.btnIntendS);
		
		setBtnListenerOrDisable( 
				picSBtn, 
				mTakePicSOnClickListener,
				MediaStore.ACTION_IMAGE_CAPTURE
		);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		
		displayData();
		
		list.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

				Intent i = new Intent(getApplicationContext(), SingleNote.class);
				i.putExtra("text", textNotes.get(arg2));
				i.putExtra("filePath", imageFilePath.get(arg2));
				i.putExtra("id", rowId.get(arg2));
				i.putExtra("update", true);
				startActivity(i);

			}
		});
		
		list.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {

				build = new AlertDialog.Builder(PhotoIntentActivity.this);
				build.setTitle("Delete Note " + textNotes.get(arg2));
				build.setMessage("Do you want to delete "+textNotes.get(arg2)+"?");
				build.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,int which) {
								
								dataBase = mHelper.getWritableDatabase();
								dataBase.delete(DbHelper.TABLE_NAME, DbHelper.KEY_ID + "="+ rowId.get(arg2), null);
								dataBase.close();
								io.deleteImageFile(imageFilePath.get(arg2));
								displayData();
								dialog.cancel();
								Toast.makeText(getApplicationContext(), "Deleted.", 3000).show();
							}
						});

				build.setNegativeButton("No",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});
				AlertDialog alert = build.create();
				alert.show();

				return true;
			}
		});
		
		
	}
	@Override
	protected void onResume() {
		displayData();
		dataBase.close();
		super.onResume();
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

			if (resultCode == RESULT_OK) {
				
				handleSmallCameraPhoto(data);
			}

	}

	// Some lifecycle callbacks so that the image can survive orientation change
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putParcelable(BITMAP_STORAGE_KEY, mImageBitmap);
		outState.putBoolean(IMAGEVIEW_VISIBILITY_STORAGE_KEY, (mImageBitmap != null) );
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		mImageBitmap = savedInstanceState.getParcelable(BITMAP_STORAGE_KEY);

		
	}

	public static boolean isIntentAvailable(Context context, String action) {
		final PackageManager packageManager = context.getPackageManager();
		final Intent intent = new Intent(action);
		List<ResolveInfo> list =
			packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
		return list.size() > 0;
	}

	private void setBtnListenerOrDisable( 
			Button btn, 
			Button.OnClickListener onClickListener,
			String intentName
	) {
		if (isIntentAvailable(this, intentName)) {
			btn.setOnClickListener(onClickListener);
		} else {
			btn.setText( 
				getText(R.string.cannot).toString() + " " + btn.getText());
			btn.setClickable(false);
		}
	}
	
	private void displayData(){
		
		dataBase = mHelper.getWritableDatabase();
		Cursor mCursor = dataBase.rawQuery("SELECT * FROM "+ DbHelper.TABLE_NAME, null);
		
		rowId.clear();
		textNotes.clear();
		imageFilePath.clear();
		
		if (mCursor.moveToFirst()) {
			do {
				rowId.add(mCursor.getString(mCursor.getColumnIndex(DbHelper.KEY_ID)));
				textNotes.add(mCursor.getString(mCursor.getColumnIndex(DbHelper.KEY_TEXT)));
				imageFilePath.add(mCursor.getString(mCursor.getColumnIndex(DbHelper.KEY_FILEPATH)));

			} while (mCursor.moveToNext());
		}
		mCursor.close();
		DisplayAdapter disadpt = new DisplayAdapter(PhotoIntentActivity.this,imageFilePath, textNotes, rowId);
		list.setAdapter(disadpt);
		
		
	}

}
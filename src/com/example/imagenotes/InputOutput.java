package com.example.imagenotes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.util.Log;


public class InputOutput {
	
    public void copyOfDB(File filesrc, File filedst) throws IOException{
      	 try
      	 {
      	  FileInputStream localFileInputStream = new FileInputStream(filesrc);
      	  FileOutputStream localFileOutputStream = new FileOutputStream(filedst);
      	  byte[] arrayOfByte = new byte[1024];
      	  while (true)
      	  {
      	    int i = localFileInputStream.read(arrayOfByte);
      	    if (i == -1)
      	    {
      	      localFileOutputStream.close();
      	      localFileInputStream.close();
      	      break;
      	    }
      	    localFileOutputStream.write(arrayOfByte, 0, i);
      	  }
      	}
      	catch (Exception localException)
      	{
      	  Log.d("XXX", "ExceptioncopyFile:" + localException.getMessage(), localException);
      }
      }
    
    public void deleteImageFile(String imageFilePath){
    	try{
    	File imageToDelete = new File(imageFilePath);
    	imageToDelete.delete();
    	}catch(Exception e){e.printStackTrace();System.out.println("doslo do greske");}		
		
		
    }

}

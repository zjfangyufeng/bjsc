package com.ff.common;

import android.os.Environment;
import android.os.StatFs;

import java.text.DecimalFormat;

public class SdMemoryStatus
{
  static final int ERROR = -1;

  public static boolean externalMemoryAvailable()
  {
    return Environment.getExternalStorageState().equals("mounted");
  }

  public static String formatSize(long paramLong_1)
  {
	float paramLong=paramLong_1*1.0f;
    boolean bool = paramLong < 1024;
    String str = null;
    if (!bool)
    {
      str = "K";
      paramLong /= 1024;
      if (paramLong >= 1024)
      {
        str = "M";
        paramLong /= 1024;
      }
      if (paramLong >= 1024)
      {
        str = "G";
        paramLong /= 1024;
      }
    }
   
    DecimalFormat df = new DecimalFormat(".0");
    if(str==null){
    	return "0";
    }else{
    	return df.format(paramLong)+str;
    }
    
  }

  public static long getAvailableExternalMemorySize()
  {
    if (externalMemoryAvailable())
    {
      StatFs localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
      return localStatFs.getBlockSize() * localStatFs.getAvailableBlocks();
    }
    return -1L;
  }

  public static long getAvailableInternalMemorySize()
  {
    StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
    return (long)localStatFs.getBlockSize() * (long)localStatFs.getAvailableBlocks();
  }

  public static long getSDAllSize()
  {
    StatFs localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
    return localStatFs.getBlockSize() * localStatFs.getBlockCount();
  }

  public static long getSDFreeSize()
  {
    StatFs localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
    return (long)localStatFs.getBlockSize() * (long)localStatFs.getAvailableBlocks();
  }

  public static long getTotalExternalMemorySize()
  {
    if (externalMemoryAvailable())
    {
      StatFs localStatFs = new StatFs(Environment.getExternalStorageDirectory().getPath());
      return localStatFs.getBlockSize() * localStatFs.getBlockCount();
    }
    return -1L;
  }

  public static long getTotalInternalMemorySize()
  {
    StatFs localStatFs = new StatFs(Environment.getDataDirectory().getPath());
    return localStatFs.getBlockSize() * localStatFs.getBlockCount();
  }
}
/**
 *
 */
package com.ugos.jiprolog.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.content.res.AssetManager;

import com.ugos.jiprolog.engine.StreamManager;

/**
 * @author ugo
 *
 */
public class AndroidStreamManager extends StreamManager {

	private AssetManager assetManager;
	/**
	 *
	 */
	public AndroidStreamManager(AssetManager assetManager)
	{
		this.assetManager = assetManager;
	}

	@Override
	public InputStream getInputStream(String strFilePath, String strBasePath,
			String[] strFileName, String[] strCurDir) throws IOException {

		InputStream ins = null;

//		if(strFilePath.toUpperCase().startsWith("FILE:///ANDROID_ASSET/"))
//		{
			try
			{
				ins = assetManager.open(strFilePath);
			}
			catch(IOException ex)
			{
				try
				{
					ins = assetManager.open(strBasePath + strFilePath);
				}
				catch(IOException ex1)
				{
					// do nothing
				}
			}

			if(ins != null)
			{
				strFileName[0] = strFilePath;//.substring(6);
	            int nSepPos = strFilePath.lastIndexOf(File.separatorChar);
	            if(nSepPos < 0)
	            {
	            	strCurDir[0] = strBasePath;
	            }
	            else
	            {
	            	strCurDir[0] = strFilePath.substring(0, nSepPos) + File.separatorChar;
	            }

	            return ins;
			}

			return super.getInputStream(strFilePath, strBasePath, strFileName, strCurDir);
	}

}

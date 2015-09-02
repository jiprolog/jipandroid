package com.ugos.jiprolog.android;

import android.app.Activity;

import com.ugos.jiprolog.engine.JIPEngine;
import com.ugos.jiprolog.engine.StreamManager;

public class JIPrologFactory
{
	private static JIPEngine instance;

	private JIPrologFactory()
	{

	}

	/**
	 * Returns a new instance of JIPEngine.
	 *
	 * @param activity activity of the interaction
	 * @return reference to the a JIPEngine instance
	 */
	public static synchronized JIPEngine newInstance(final Activity activity)
	{
		StreamManager.setStreamManager(new AndroidStreamManager(activity.getAssets()));

		new Thread(new ThreadGroup("jiprolog"), new Runnable()
		{
			public void run()
			{
				instance = new JIPEngine();

				instance.setEnvVariable("activity", activity);

				synchronized (activity)
				{
					activity.notify();
				}
			}
		}, "JIProlog Initialization", 256 * 1024).start();

		if(instance == null)
		{
			synchronized (activity)
			{
				try
				{
					activity.wait();
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		return instance;
	}
}

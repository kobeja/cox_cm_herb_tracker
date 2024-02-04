package com.cox.cm.herbTracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class CoxCmHerbTrackerTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(CoxCmHerbTrackerPlugin.class);
		RuneLite.main(args);
	}
}
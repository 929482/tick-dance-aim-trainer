package com.tickdanceaim;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class TickDanceAimPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(TickDanceAimPlugin.class);
		RuneLite.main(args);
	}
}
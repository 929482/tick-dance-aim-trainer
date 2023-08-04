package com.tickdanceaim;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

import java.awt.*;

@ConfigGroup("TickDanceAimConfig")
public interface TickDanceAimConfig extends Config
{
    @ConfigSection(
            name = "Shift right click the ground to the set area and start",
            description = "Shift right click the ground to the set area and start",
            position = 0
    )
    String instructionsSection = "Shift right click the ground to the set area and start";


    @ConfigItem(
			keyName = "updateRate",
			name = "Update rate",
			description = "Changes how often should the tiles move",
			position = 1
	)
	default int updateRate()
	{
		return 1;
	}
	@ConfigItem(
			keyName = "interactionPause",
			name = "Interaction pause",
			description = "Pauses for maximum of N ticks while interacting",
			position = 2
	)
	default int interactionPause()
	{
		return 1;
	}
	@ConfigItem(
			keyName = "walkTiles",
			name = "Walk tiles",
			description = "Allow generating tiles that are 1 tile away",
			position = 3
	)
	default boolean walkTiles()
	{
		return true;
	}
	@ConfigItem(
			keyName = "runTiles",
			name = "Run tiles",
			description = "Allow generating tiles that are 2 tiles away",
			position = 4
	)
	default boolean runTiles()
	{
		return true;
	}
	@ConfigItem(
			keyName = "cardinalTiles",
			name = "Cardinal direction tiles",
			description = "Allow cardinal direction tiles to be generated",
			position = 5
	)
	default boolean cardinalTiles()
	{
		return true;
	}
	@ConfigItem(
			keyName = "diagonalTiles",
			name = "Diagonal tiles",
			description = "Allow diagonal direction tiles to be generated",
			position = 6
	)
	default boolean diagonalTiles()
	{
		return true;
	}
	@ConfigItem(
			keyName = "LTiles",
			name = "L tiles",
			description = "Allow L movement tiles to be generated",
			position = 7
	)
	default boolean LTiles()
	{
		return true;
	}

	@ConfigItem(
			keyName = "printStreaks",
			name = "Print streaks",
			description = "Print streaks",
			position = 8
	)
	default boolean printStreaks()
	{
		return true;
	}

	@ConfigItem(
			keyName = "detailedStreaks",
			name = "Print detailed streaks",
			description = "Print detailed streaks",
			position = 9
	)
	default boolean detailedStreaks()
	{
		return false;
	}

	@Alpha
	@ConfigItem(
			keyName = "tile1Color",
			name = "Tile1 color",
			description = "Configures the color of the tile you're supposed to click on the current tick",
			position = 10
	)
	default Color tile1Color() {
		return new Color(255, 0, 0, 150);
	}

	@Alpha
	@ConfigItem(
			keyName = "tile2Color",
			name = "Tile2 color",
			description = "Configures the color of the tile you're supposed to click on the next tick, alpha 0 to disable",
			position = 11
	)
	default Color tile2Color() {
		return new Color(0, 0, 0, 0);
	}

	@Alpha
	@ConfigItem(
			keyName = "borderColor",
			name = "Border color",
			description = "",
			position = 12
	)
	default Color borderColor() {
		return new Color(0, 0, 0, 255);
	}

}

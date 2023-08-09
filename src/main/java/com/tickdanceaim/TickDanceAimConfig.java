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
            name = "Shift right click the ground to set the area and to start",
            description = "Shift right click the ground to set the area and to start",
            position = 0
    )
    String instructionsSection = "Shift right click the ground to set the area and to start";


	@ConfigSection(
			name = "Random tiles",
			description = "Configure random tiles",
			position = 1
	)
	String tileSection = "Random tiles";

    @ConfigItem(
			keyName = "updateRate",
			name = "Update rate",
			description = "Changes how often should the tiles move",
			position = 1,
			section = tileSection
	)
	default int updateRate()
	{
		return 1;
	}
	@ConfigItem(
			keyName = "interactionPause",
			name = "Interaction pause",
			description = "Pauses for maximum of N ticks while interacting",
			position = 2,
			section = tileSection
	)
	default int interactionPause()
	{
		return 1;
	}
	@ConfigItem(
			keyName = "walkTiles",
			name = "Walk tiles",
			description = "Allow generating tiles that are 1 tile away",
			position = 3,
			section = tileSection
	)
	default boolean walkTiles()
	{
		return true;
	}
	@ConfigItem(
			keyName = "runTiles",
			name = "Run tiles",
			description = "Allow generating tiles that are 2 tiles away",
			position = 4,
			section = tileSection
	)
	default boolean runTiles()
	{
		return true;
	}
	@ConfigItem(
			keyName = "cardinalTiles",
			name = "Cardinal direction tiles",
			description = "Allow cardinal direction tiles to be generated",
			position = 5,
			section = tileSection
	)
	default boolean cardinalTiles()
	{
		return true;
	}
	@ConfigItem(
			keyName = "diagonalTiles",
			name = "Diagonal tiles",
			description = "Allow diagonal direction tiles to be generated",
			position = 6,
			section = tileSection
	)
	default boolean diagonalTiles()
	{
		return true;
	}
	@ConfigItem(
			keyName = "LTiles",
			name = "L tiles",
			description = "Allow L movement tiles to be generated",
			position = 7,
			section = tileSection
	)
	default boolean LTiles()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "tile1Color",
			name = "Tile1 color",
			description = "Configures the color of the tile you're supposed to click on the current tick",
			position = 10,
			section = tileSection
	)
	default Color tile1Color() {
		return new Color(255, 0, 0, 150);
	}

	@Alpha
	@ConfigItem(
			keyName = "tile2Color",
			name = "Tile2 color",
			description = "Configures the color of the tile you're supposed to click on the next tick, alpha 0 to disable",
			position = 11,
			section = tileSection
	)
	default Color tile2Color() {
		return new Color(0, 0, 0, 0);
	}



	@ConfigSection(
			name = "Random switches",
			description = "Configure random item switches",
			position = 2
	)
	String switchSection = "Random switches";

	@ConfigItem(
			keyName = "switchIds1",
			name = "Switch1, shift right click an item to set",
			description = "Configures switch set 1, empty to disable",
			position = 0,
			section = switchSection
	)
	default String switchIds1()
	{
		return "";
	}
	@ConfigItem(
			keyName = "switchIds1",
			name = "",
			description = ""
	)
	void setSwitchIds1(String key);

	@ConfigItem(
			keyName = "switchIds2",
			name = "Switch2",
			description = "Configures switch set 2, empty to disable",
			position = 1,
			section = switchSection
	)
	default String switchIds2()
	{
		return "";
	}
	@ConfigItem(
			keyName = "switchIds2",
			name = "",
			description = ""
	)
	void setSwitchIds2(String key);

	@ConfigItem(
			keyName = "switchIds3",
			name = "Switch3",
			description = "Configures switch set 3, empty to disable",
			position = 2,
			section = switchSection
	)
	default String switchIds3()
	{
		return "";
	}
	@ConfigItem(
			keyName = "switchIds3",
			name = "",
			description = ""
	)
	void setSwitchIds3(String key);

	@ConfigItem(
			keyName = "switchIds4",
			name = "Switch4",
			description = "Configures switch set 4, empty to disable",
			position = 3,
			section = switchSection
	)
	default String switchIds4()
	{
		return "";
	}
	@ConfigItem(
			keyName = "switchIds4",
			name = "",
			description = ""
	)
	void setSwitchIds4(String key);

	@ConfigItem(
			keyName = "switchIds5",
			name = "Switch5",
			description = "Configures switch set 3, empty to disable",
			position = 4,
			section = switchSection
	)
	default String switchIds5()
	{
		return "";
	}
	@ConfigItem(
			keyName = "switchIds5",
			name = "",
			description = ""
	)
	void setSwitchIds5(String key);

	@ConfigItem(
			keyName = "switchRate",
			name = "Switch rate",
			description = "Changes how often you need to switch items",
			position = 5,
			section = switchSection
	)
	default int switchRate()
	{
		return 1;
	}

	@ConfigItem(
			keyName = "repeatingSwitches",
			name = "Repeating switches",
			description = "Allow same switch to be selected back to back",
			position = 6,
			section = switchSection
	)
	default boolean repeatingSwitches()
	{
		return false;
	}


	@ConfigItem(
			keyName = "switchPattern",
			name = "Custom switch pattern",
			description = "Example pattern: 1,1,1,2,2,2 to switch between set 1 and set 2 every 3 ticks. Empty for random",
			position = 7,
			section = switchSection
	)
	default String switchPattern()
	{
		return "";
	}
	@ConfigItem(
			keyName = "switchPattern",
			name = "",
			description = ""
	)
	void switchPattern(String key);



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
			keyName = "borderColor",
			name = "Border color",
			description = "",
			position = 12
	)
	default Color borderColor() {
		return new Color(0, 0, 0, 255);
	}

}

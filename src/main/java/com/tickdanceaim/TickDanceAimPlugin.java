package com.tickdanceaim;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.coords.WorldArea;
import net.runelite.client.ui.overlay.OverlayManager;


import java.util.*;


@Slf4j
@PluginDescriptor(
	name = "Tick dance"
)
public class TickDanceAimPlugin extends Plugin
{
	@Inject
	private TickDanceAimConfig config;

	@Inject
	private Client client;

	@Inject
	private ConfigManager configManager;

	@Inject
	private TickDanceAimOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	ArrayList<ItemSwitch> itemSwitches;
	ArrayList<Integer> switchPattern;
	int activeSwitch = 0;
	int switchTick = 0;

	@Inject
	ItemManager itemManager;


	public WorldPoint gameAreaCorner1;
	public WorldPoint gameAreaCorner2;
	public WorldArea gameArea = null;

	public WorldPoint tile1 = new WorldPoint(0, 0, 0);
	public WorldPoint tile2 = new WorldPoint(0, 0, 0);

	private int successfulTicks = 0;
	private int successfulSwitches = 0;
	private int successfulTiles = 0;


	public int tickCounter = 0;
	private int ticksInteracted = 0;
	private int tickGameUpdated = 0;

	private Random rnd;

	@Provides
	TickDanceAimConfig provideConfig(ConfigManager cm)
	{
		return cm.getConfig(TickDanceAimConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		rnd = new Random();

		itemSwitches = new ArrayList<ItemSwitch>();
		switchPattern = new ArrayList<Integer>();
		for (int i = 0; i < 5; ++i) {
			itemSwitches.add(new ItemSwitch());
		}

		itemSwitches.get(0).fromString(config.switchIds1());
		itemSwitches.get(1).fromString(config.switchIds2());
		itemSwitches.get(2).fromString(config.switchIds3());
		itemSwitches.get(3).fromString(config.switchIds4());
		itemSwitches.get(4).fromString(config.switchIds5());

		for (int i = 0; i < 5; ++i)
			itemSwitches.get(i).updateImage(itemManager);

		parseSwitchConfig();
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
	}


	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		tickCounter++;
		if (gameArea == null)
			return;
		if (client.getLocalPlayer().getWorldLocation().distanceTo(gameArea) > 15)
			return;


		boolean updateRequired = false;
		boolean streakFailed = false;

		if (tickGameUpdated + config.updateRate() <= tickCounter) {
			if (client.getLocalPlayer().getInteracting() == null) {
				ticksInteracted = 0;
				updateRequired = true;
			} else {
				ticksInteracted++;
				if (ticksInteracted > config.interactionPause())
					updateRequired = true;
			}
		}

		if (tile1 != null && tile2 != null && updateRequired &&
				!tile1.equals(client.getLocalPlayer().getWorldLocation())) {
			streakFailed = true;
		}

		int prevSwitch = activeSwitch;
		updateActiveSwitch();
		if (prevSwitch != activeSwitch) {
			if (!itemSwitches.get(prevSwitch).isWearing(client)) {
				streakFailed = true;
			} else {
				successfulSwitches += itemSwitches.get(prevSwitch).items.size();
			}
		}

		if (streakFailed) {
			if (successfulTicks > config.updateRate() + 1) {
				if (config.printStreaks())
					printStreak();
			}
			successfulTicks = 0;
			successfulTiles = 0;
			successfulSwitches = 0;
		} else {
			successfulTicks++;
		}

		if (updateRequired) {
			if (!streakFailed)
				successfulTiles++;
			updateTiles();
		}
	}

	private void printStreak()
	{
		String msg = "Tick Dance: Ticks "  + successfulTicks + "     " +
				"Tiles " + successfulTiles + "     " +
				"Switches " + successfulSwitches + "     " +
			gameArea.getWidth() + "x" + gameArea.getHeight();

		client.addChatMessage(ChatMessageType.TRADE, "", msg, null);
	}

	private void updateTiles()
	{
		tickGameUpdated = tickCounter;

		if (gameAreaCorner1 == null || gameArea == null)
			return;

		WorldPoint n = genNextTile();
		if (n == null)
			return;
		tile1 = tile2;
		tile2 = n;
	}

	private void updateActiveSwitch()
	{
		if (!switchPattern.isEmpty()) {
			activeSwitch = switchPattern.get(tickCounter % switchPattern.size());;
			return;
		}
		if (switchTick + config.switchRate() <= tickCounter) {
			switchTick = tickCounter;
			ArrayList<Integer> nonEmpty = new ArrayList<Integer>();
			for (int i = 0; i < itemSwitches.size(); ++i) {
				if (!itemSwitches.get(i).isEmpty()) {
					if (!config.repeatingSwitches() && i == activeSwitch)
						continue;
					nonEmpty.add(i);
				}
			}
			if (nonEmpty.isEmpty())
				return;
			activeSwitch = nonEmpty.get(rnd.nextInt(nonEmpty.size()));
		}
	}

	private WorldPoint genNextTile()
	{
		if (gameArea == null)
			return null;

		ArrayList<WorldPoint> cand;
		if (tile1 == null || !tile1.isInArea(gameArea))
			tile1 = gameArea.toWorldPoint();
		if (tile2 == null || !tile2.isInArea(gameArea))
			tile2 = gameArea.toWorldPoint();

		cand = genTileCandidates(tile2);
		if (cand.size() > 0)
			return cand.get(Math.abs(rnd.nextInt() % cand.size()));
		return null;
	}

	private ArrayList<WorldPoint> genTileCandidates(WorldPoint center)
	{
		ArrayList<WorldPoint> ret = new ArrayList<WorldPoint>();
		WorldPoint pPos = center;

		WorldPoint pStart = pPos.dx(-2).dy(-2);
		WorldArea pArea = new WorldArea(pStart.getX(), pStart.getY(), 5, 5, pStart.getPlane());

		List<WorldPoint> pAreaPoints = pArea.toWorldPointList();
		for (WorldPoint p : pAreaPoints) {
			int diffx = pPos.getX() - p.getX();
			int diffy = pPos.getY() - p.getY();
			if (this.gameArea.distanceTo(p) != 0)
				continue;
			if (pPos.equals(p))
				continue;
			if (p.equals(tile1) || p.equals(tile2))
				continue;


			if (!config.walkTiles() && (pPos.distanceTo(p) < 2))
				continue;
			if (!config.runTiles() && (pPos.distanceTo(p) > 1))
				continue;
			if (!config.cardinalTiles() && (diffx == 0 || diffy == 0))
				continue;
			if (!config.diagonalTiles() &&
					(Math.abs(diffx) == Math.abs(diffy)))
				continue;
			if (!config.LTiles() && (
					(Math.abs(diffx) == 2 && Math.abs(diffy) == 1) ||
							(Math.abs(diffy) == 2 && Math.abs(diffx) == 1)
			))
				continue;
			ret.add(p);
		}
		return ret;
	}

	private int randomRange(int start, int end)
	{
		if (start == end)
			return start;
		return Math.min(start, end) + rnd.nextInt(Math.abs(end - start) + 1);
	}

	private WorldPoint worldPointRandom(WorldPoint start, WorldPoint end)
	{
		return new WorldPoint(randomRange(start.getX(), end.getX()), randomRange(start.getY(), end.getY()),
				randomRange(start.getPlane(), end.getPlane()));
	}

	private void setArea(WorldPoint p1, WorldPoint p2)
	{
		if (p1 != null && p2 != null) {
			WorldPoint sw = new WorldPoint(
					Math.min(p1.getX(), p2.getX()),
					Math.min(p1.getY(), p2.getY()),
					p1.getPlane());
			int width = Math.abs(p1.getX() - p2.getX()) + 1;
			int height = Math.abs(p1.getY() - p2.getY()) + 1;
			gameArea = new WorldArea(sw, width, height);
		}
	}


	@Subscribe
	public void onMenuEntryAdded(MenuEntryAdded event)
	{
		final boolean hotKeyPressed = client.isKeyPressed(KeyCode.KC_SHIFT);
		if (!hotKeyPressed)
			return;
		if (event.getOption().equals("Walk here")) {
			Tile selectedSceneTile = client.getSelectedSceneTile();

			if (selectedSceneTile == null) {
				return;
			}

			client.createMenuEntry(-1)
				.setOption("Tick dance corner 1")
				.setType(MenuAction.RUNELITE)
				.onClick(e ->
				{
					Tile target = client.getSelectedSceneTile();
					if (target != null) {
						gameAreaCorner1 = target.getWorldLocation();
						setArea(gameAreaCorner1, gameAreaCorner2);
						tickGameUpdated = 0;
					}
				});

			client.createMenuEntry(-2)
				.setOption("Tick dance corner 2")
				.setType(MenuAction.RUNELITE)
				.onClick(e ->
				{
					Tile target = client.getSelectedSceneTile();
					if (target != null) {
						gameAreaCorner2 = target.getWorldLocation();
						setArea(gameAreaCorner1, gameAreaCorner2);
						tickGameUpdated = 0;
					}
				});
		}

		final Widget w = event.getMenuEntry().getWidget();
		if (w != null && WidgetInfo.TO_GROUP(w.getId()) == WidgetID.INVENTORY_GROUP_ID
				&& "Examine".equals(event.getOption()) && event.getIdentifier() == 10)
		{
			createSwitchMenuEntries(event);
		}
	}

	public void createSwitchMenuEntries(MenuEntryAdded event)
	{
		final Widget w = event.getMenuEntry().getWidget();
		int itemId = w.getItemId();
		for (int i = 0; i < itemSwitches.size(); ++i) {
			final int ii = i;
			if (!itemSwitches.get(ii).items.contains(itemId)) {
				client.createMenuEntry(-ii - 1)
					.setOption("Add item to switch " + (ii + 1))
					.setType(MenuAction.RUNELITE)
					.onClick(e ->
					{
						itemSwitches.get(ii).items.add(itemId);
						updateSwitchConfigText(ii);
						itemSwitches.get(ii).updateImage(itemManager);
					});
			} else {
				client.createMenuEntry(-ii - 1)
					.setOption("Remove item from switch " + (ii + 1))
					.setType(MenuAction.RUNELITE)
					.onClick(e ->
					{
						itemSwitches.get(ii).items.remove((Object) itemId);
						updateSwitchConfigText(ii);
						itemSwitches.get(ii).updateImage(itemManager);
					});
			}
		}
	}

	private void updateSwitchConfigText(final int i)
	{
		switch (i) {
			case 0:
				config.setSwitchIds1(itemSwitches.get(i).toString());
				break;
			case 1:
				config.setSwitchIds2(itemSwitches.get(i).toString());
				break;
			case 2:
				config.setSwitchIds3(itemSwitches.get(i).toString());
				break;
			case 3:
				config.setSwitchIds4(itemSwitches.get(i).toString());
				break;
			case 4:
				config.setSwitchIds5(itemSwitches.get(i).toString());
				break;
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("TickDanceAimConfig")) {
			if (event.getKey().equals("switchPattern")) {
				parseSwitchConfig();
			} else {
				Integer id = null;
				if (event.getKey().equals("switchIds1"))
					id = 0;
				else if (event.getKey().equals("switchIds2"))
					id = 1;
				else if (event.getKey().equals("switchIds3"))
					id = 2;
				else if (event.getKey().equals("switchIds4"))
					id = 3;
				else if (event.getKey().equals("switchIds5"))
					id = 4;
				if (id == null)
					return;
				itemSwitches.get(id).fromString(event.getNewValue());
				itemSwitches.get(id).updateImage(itemManager);
			}
		}
	}

	private void parseSwitchConfig()
	{
		switchPattern.clear();
		String ss = config.switchPattern();
		for (String s : ss.split(",")) {
			try {
				Integer sw = Integer.parseInt(s.trim());
				if (!(sw >= 1 && sw <= 5))
					continue;
				switchPattern.add(sw - 1);
			} catch (NumberFormatException e) {
			}
		}
	}

	public int patternTicksRemaining()
	{
		int count = 0;
		int n = tickCounter % switchPattern.size();
		int prevVal = switchPattern.get(n);
		for (int i = n; i < switchPattern.size(); ++i) {
			int val = switchPattern.get(i);
			if (val == prevVal)
				count++;
			else
				return count;
			prevVal = val;
		}
		return count;
	}

}

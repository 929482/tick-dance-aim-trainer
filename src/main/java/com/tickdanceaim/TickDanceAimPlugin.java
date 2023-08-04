package com.tickdanceaim;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuEntryAdded;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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


	public WorldPoint gameAreaCorner1;
	public WorldPoint gameAreaCorner2;
	public WorldArea gameArea = null;

	public WorldPoint tile1 = new WorldPoint(0, 0, 0);
	public WorldPoint tile2 = new WorldPoint(0, 0, 0);

	private int streak = 0;
	private int tickCounter = 0;
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
		configManager.setDefaultConfiguration(
				configManager.getConfig(TickDanceAimConfig.class), true);
		overlayManager.add(overlay);
		rnd = new Random();
	}

	@Override
	protected void shutDown()
	{
		configManager.setDefaultConfiguration(
				configManager.getConfig(TickDanceAimConfig.class), true);
		overlayManager.remove(overlay);
	}

	@Subscribe
	private void onGameTick(GameTick gameTick)
	{
		tickCounter++;
		if (tickGameUpdated + config.updateRate() > tickCounter)
			return;
		tickGameUpdated = tickCounter;
		if (client.getLocalPlayer().getInteracting() != null) {
			ticksInteracted++;
			if (ticksInteracted <= config.interactionPause())
				return;
		} else {
			ticksInteracted = 0;
		}

		if (tile1 == null || tile2 == null) {
			tile1 = client.getLocalPlayer().getWorldLocation();
			tile2 = client.getLocalPlayer().getWorldLocation();
		}


		if (tile1.equals(client.getLocalPlayer().getWorldLocation())) {
			streak++;
		} else if (streak > 2) {
			if (config.printStreaks() || config.detailedStreaks())
				printStreak(streak);
			streak = 0;
		} else {
			streak = 0;
		}

		updateDanceGame();
	}

	private void printStreak(int s)
	{
		String msg = "Streak: "  + s + "     " +
				gameArea.getWidth() + "x" + gameArea.getHeight();
				if (config.detailedStreaks()) {
					msg += "     " +
							"Rate: " + config.updateRate() + "     Interact: " + config.interactionPause() + "     " +
							" Tiles: " +
							(config.walkTiles() ? "W" : "") +
							(config.runTiles() ? "R" : "") +
							(config.cardinalTiles() ? "C" : "") +
							(config.diagonalTiles() ? "D" : "") +
							(config.LTiles() ? "L" : "");
				}

		client.addChatMessage(ChatMessageType.TRADE, "", msg, null);
	}

	private void updateDanceGame()
	{
		if (gameAreaCorner1 == null || gameAreaCorner2 == null || gameAreaCorner1 == gameAreaCorner2)
			return;

		WorldPoint n = genNextTile();
		tile1 = tile2;
		tile2 = n;
	}

	private WorldPoint genNextTile()
	{
		ArrayList<WorldPoint> cand;
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
			if (p.equals(tile1) || p.equals(tile2) || p.equals(client.getLocalPlayer().getWorldLocation()))
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
	public void onMenuEntryAdded(MenuEntryAdded event) {
		final boolean hotKeyPressed = client.isKeyPressed(KeyCode.KC_SHIFT);
		if (hotKeyPressed && event.getOption().equals("Walk here")) {
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
						if (target != null)
						{
							gameAreaCorner1 = target.getWorldLocation();
							setArea(gameAreaCorner1, gameAreaCorner2);
						}
					});

			client.createMenuEntry(-2)
					.setOption("Tick dance corner 2")
					.setType(MenuAction.RUNELITE)
					.onClick(e ->
					{
						Tile target = client.getSelectedSceneTile();
						if (target != null)
						{
							gameAreaCorner2 = target.getWorldLocation();
							setArea(gameAreaCorner1, gameAreaCorner2);
						}
					});
		}
	}
}

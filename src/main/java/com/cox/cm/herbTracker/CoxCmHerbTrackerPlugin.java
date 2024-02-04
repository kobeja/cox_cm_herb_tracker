package com.cox.cm.herbTracker;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Cox Cm Herb Tracker"
)
public class CoxCmHerbTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private CoxCmHerbTrackerConfig config;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private CoxCmHerbTrackerOverlay overlay;

	@Getter
	private int herbsFarmed;

	private int recentHerbCount;

	private final int farmRoomMinX = 12127;
	private final int farmRoomMaxX = 12148;
	private final int farmRoomMinY = 2968;
	private final int farmRoomMaxY = 2992;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
	}

	@Provides
	CoxCmHerbTrackerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(CoxCmHerbTrackerConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGGED_IN)
		{
			herbsFarmed = 0;
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		LogPlayerLocation();
		if (isInFarmingRoom())
		{
			herbsFarmed = getHerbCountInInventory();
		}
	}

	private boolean isInFarmingRoom()
	{
		var localPoint = client.getLocalPlayer().getLocalLocation();
		var worldPoint = WorldPoint.fromLocal(client, localPoint);

		var xPos = worldPoint.getX();
		var yPos = worldPoint.getY();

        return
		(
			xPos <= farmRoomMaxX &&
			xPos >= farmRoomMinX &&
			yPos <= farmRoomMaxY &&
			yPos >= farmRoomMinY
		);
    }

	private int getHerbCountInInventory()
	{
		var herbId = CoxCmHerbTrackerConstants.BUCHU_GRIMY_ID;
		var inventory = client.getItemContainer(InventoryID.INVENTORY);
		if (inventory != null)
		{
			var count = 0;
			var items = inventory.getItems();

			for (Item item : items)
			{
				if (item.getId()==herbId)
				{
					count += item.getQuantity();
				}
			}
			recentHerbCount = count;
			return count;
		}
		return 0;
	}

	private void LogPlayerLocation()
	{
		if (client.getLocalPlayer() != null)
		{
			LocalPoint localPoint = client.getLocalPlayer().getLocalLocation();
			WorldPoint worldPoint = WorldPoint.fromLocal(client, localPoint);

			int xPos = worldPoint.getX();
			int yPos = worldPoint.getY();

			client.addChatMessage(ChatMessageType.GAMEMESSAGE, "", "Player co-ords x: " + xPos + " y:" + yPos + "\n" + "in farm room: " + isInFarmingRoom(), null);
		}
	}
}

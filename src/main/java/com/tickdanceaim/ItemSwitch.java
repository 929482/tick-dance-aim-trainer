package com.tickdanceaim;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;

import java.util.ArrayList;

public class ItemSwitch {

    ArrayList<Integer> items;

    public AsyncBufferedImage icon;


    public ItemSwitch()
    {
        items = new ArrayList<Integer>();
    }

    public void updateImage(ItemManager itemManager)
    {
        if (items.size() > 0)
            icon = itemManager.getImage(items.get(0));
    }

    public boolean isEmpty()
    {
        return items.isEmpty();
    }

    public boolean isWearing(Client client)
    {
        final ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
        if (equipment == null) {
            return false;
        }
        for (int i = 0; i < items.size(); ++i) {
            if (!equipment.contains(items.get(i)))
                return false;
        }
        return true;
    }

    public void fromString(String s)
    {
        items.clear();
        for (String is : s.split(",")) {
            try {
                Integer item = Integer.parseInt(is.trim());
                items.add(item);
            } catch (NumberFormatException e) {
            }
        }
    }

    public String toString()
    {
        String itemString = "";
        for (int i = 0; i < items.size(); ++i) {
            itemString += String.valueOf(items.get(i));
            if (i != items.size() - 1)
                itemString += ", ";
        }
        return itemString;
    }
}

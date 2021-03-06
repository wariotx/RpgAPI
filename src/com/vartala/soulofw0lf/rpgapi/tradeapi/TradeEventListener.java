package com.vartala.soulofw0lf.rpgapi.tradeapi;

import com.vartala.soulofw0lf.rpgapi.RpgAPI;
import com.vartala.soulofw0lf.rpgapi.playerapi.RpgPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Mothma
 * Date: 7/4/13
 * Time: 9:46 PM
 */
public class TradeEventListener implements Listener{
    RpgAPI api;
    ArrayList<Integer> forbiddenIndex = new ArrayList<Integer>();

    /**
     *
     * @param api
     */
    public TradeEventListener(RpgAPI api) {
        this.api = api;
        Bukkit.getPluginManager().registerEvents(this, api);
        forbiddenIndex.add(0);        ;
        for (int i = 27; i<45; i++) {
            forbiddenIndex.add(i);
        }
    }

    /**
     *
     * @param event
     */
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void clickListener(final InventoryClickEvent event) {
        // Return if trade isn't on
        if (!api.tradeOn) {
            return;
        }

        // If its a trade inventory and they click something
        if (event.getInventory().getName().contains("Trading with") && event.getCurrentItem() != null) {
            boolean someoneReady = false;

            // If someone is ready don't let them change anything
            ItemStack statusOne = event.getInventory().getItem(0);
            ItemStack statusTwo = event.getInventory().getItem(5);
            if (statusOne.getDurability() == 5 || statusTwo.getDurability() == 5) {
                event.setCancelled(true);
                someoneReady = true;
            }

            ItemStack clickBlock = event.getCurrentItem();
            // Cutting off the first 12 chars should give the other player's name
            String otherPlayer = event.getInventory().getName().substring(13);
            final Player otherP = Bukkit.getPlayer(RpgAPI.rpgPlayers.get(otherPlayer).getRealName());

            // Prevent removing items of forbidden index or on the right (other player)
            if (forbiddenIndex.contains(event.getSlot()) || event.getSlot()%9 >= 4) {
                if (event.getRawSlot() < 45) { // Only cancel in the actual display, not their inventory
                    event.setCancelled(true);
                }
            } else if (event.getRawSlot() < 45) { // Triggered if the put an item in their slot space
                // Show the items to the other player if it's a non-shift, left click, on the left side of the trade inv.
                if (event.getClick().isLeftClick() && !event.getClick().isShiftClick() && event.getSlot()%9 >= 4) {
                    // Run an update to the inventory a bit later so the event already took place
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            for (int i =0; i < 45; i++) {
                                // Only copy things under index 45, not forbidden, and on the left (less than 4)
                                if (!forbiddenIndex.contains(i) && i%9 < 4) {
                                    if (event.getInventory().getItem(i) != null) {
                                        otherP.getOpenInventory().setItem(i+5, event.getInventory().getItem(i).clone());
                                        otherP.updateInventory();
                                    } else {
                                        // Clear up blank spaces
                                        otherP.getOpenInventory().setItem(i+5, new ItemStack(0));
                                        otherP.updateInventory();
                                    }
                                }
                            }
                        }
                    }.runTaskLater(api.getInstance(), 2);
                }

            }

            String name;
            if (clickBlock.getItemMeta()!= null) {
                name = clickBlock.getItemMeta().getDisplayName();
            } else {
                return;
            }

            // Handle changing offers and status
            if (name != null && name.contains("You offer") && !someoneReady) {
                // Find Number
                String sub = name.substring(10);
                sub = sub.substring(0, sub.indexOf(" "));
                int number = Integer.valueOf(sub);
                if (event.getClick().isLeftClick()) {
                    // Find how much money the need
                    int neededMoney = findOfferedMoney((Player) event.getWhoClicked());
                    switch(event.getRawSlot() - 36) {
                        case 0:
                            neededMoney += 1000;
                            break;
                        case 1:
                            neededMoney += 100;
                            break;
                        case 2:
                            neededMoney += 10;
                            break;
                        case 3:
                            neededMoney += 1;
                            break;
                    }
                    // Do nothing if they don't have enough money
                    if (api.rpgPlayers.get(event.getWhoClicked().getName()).getCoin() < neededMoney) {
                        return;
                    }
                    number += 1;
                } else if (event.getClick().isRightClick()) {
                    number -= 1;
                }
                name = name.replace(sub, String.valueOf(number));
                // Update the name
                ItemMeta meta = clickBlock.getItemMeta();
                meta.setDisplayName(name);
                clickBlock.setItemMeta(meta);
                event.getInventory().setItem(event.getRawSlot(), clickBlock);
                // Update the other persons display
                ItemStack otherBlock = otherP.getOpenInventory().getItem(event.getRawSlot()+5);
                meta = otherBlock.getItemMeta();
                meta.setDisplayName(meta.getDisplayName().replace(sub, String.valueOf(number)));
                otherBlock.setItemMeta(meta);

            } else if (name != null && name.contains("Your Status")) {
                if (event.getClick().isLeftClick()) {
                    // Further the trade
                    setReady(clickBlock, (Player) event.getWhoClicked(), otherPlayer, false);
                } else if (event.getClick().isRightClick()) {
                    // Cancel the trade
                    setReady(clickBlock, (Player) event.getWhoClicked(), otherPlayer, true);
                }
            }
        }
    }

    /**
     *
     * @param clickBlock
     * @param player
     * @param otherPlayer
     * @param Cancel
     */
    // Change the status wool on both players' screens
    private void setReady(ItemStack clickBlock, Player player, String otherPlayer, boolean Cancel) {
        // Set block
        ItemMeta meta = clickBlock.getItemMeta();
        if (Cancel) {
            meta.setDisplayName("Your Status: Waiting...");
            clickBlock.setDurability((short) 0);
        } else if (meta.getDisplayName().contains("Wait")) {
            meta.setDisplayName("Your Status: Accepting Trade");
            clickBlock.setDurability((short) 5);
        } else if (meta.getDisplayName().contains("Acc")) {
            meta.setDisplayName("Your Status: Confirmed Trade");
            clickBlock.setDurability((short) 13);
        }
        clickBlock.setItemMeta(meta);
        // Set other block
        Player p = Bukkit.getPlayer(RpgAPI.rpgPlayers.get(otherPlayer).getRealName());
        ItemStack otherBlock = p.getOpenInventory().getItem(5);
        meta = otherBlock.getItemMeta();
        if (Cancel) {
            meta.setDisplayName("Their Status: Waiting...");
            otherBlock.setDurability((short) 0);
        } else if (meta.getDisplayName().contains("Wait")) {
            meta.setDisplayName("Their Status: Accepting Trade");
            otherBlock.setDurability((short) 5);
        } else if (meta.getDisplayName().contains("Acc")) {
            meta.setDisplayName("Their Status: Confirmed Trade");
            otherBlock.setDurability((short) 13);
            // If the other person is ready make the trade!
            if (p.getOpenInventory().getItem(0).getDurability() == 13) {
                makeTrade(player, p);
            }
        }
        otherBlock.setItemMeta(meta);
    }

    /**
     *
     * @param a
     * @param b
     */
    private void confirmTrade(Player a, Player b) {
        RpgAPI.getRp(a).setInConfirm(true);
        RpgAPI.getRp(b).setInConfirm(true);
        //TODO finish this
    }

    /**
     *
     * @param a
     * @param b
     */
    private void makeTrade(Player a, Player b) {
        for (int i =0; i < 45; i++) {
            // Only copy things under index 45, not forbidden, and on the left (less than 4)
            if (!forbiddenIndex.contains(i) && i%9 < 4) {
                // Give a's item to b
                if (a.getOpenInventory().getItem(i) != null) {
                    b.getInventory().addItem(a.getOpenInventory().getItem(i));
                }
                // Give b's item to a
                if (b.getOpenInventory().getItem(i) != null) {
                    a.getInventory().addItem(b.getOpenInventory().getItem(i));
                }
            }
        }
        // Find totals of the money traded
        int aTotal = findOfferedMoney(a);
        int bTotal = findOfferedMoney(b);

        // Update the traders' accounts
        RpgPlayer rpgA = api.rpgPlayers.get(a.getName());
        RpgPlayer rpgB = api.rpgPlayers.get(b.getName());
        rpgA.setCoin(rpgA.getCoin() + bTotal - aTotal);
        rpgB.setCoin(rpgB.getCoin() + aTotal - bTotal);

        a.closeInventory();
        b.closeInventory();
        a.sendMessage(ChatColor.GREEN + "Trade Accepted.");
        b.sendMessage(ChatColor.GREEN + "Trade Accepted.");
    }

    /**
     *
     * @param p
     * @return
     */
    private int findOfferedMoney(Player p) {
        int total = 0;
        for (int i = 0; i < 4; i++) {
            ItemStack item = p.getOpenInventory().getItem(36+i);
            total += findMoney(item.getItemMeta().getDisplayName(), i);
        }
        return total;
    }

    /**
     *
     * @param label
     * @param type
     * @return
     */
    // Get the actual worth of money (i.e. 100 for Gold, 10 for silver...)
    private int findMoney (String label, int type) {
        // Find Number
        String sub = label.substring(10);
        sub = sub.substring(0, sub.indexOf(" "));
        int number = Integer.valueOf(sub);
        switch (type) {
            case 0:
                number = number * 1000;
                break;
            case 1:
                number = number * 100;
                break;
            case 2:
                number = number * 10;
                break;
        }
        return number;
    }

    /**
     *
     * @param event
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    // This is still called when a complete trade is made
    public void closeListener(InventoryCloseEvent event) {
        String title = event.getInventory().getName();
        if (title.contains("Trading with")) {
            // Set players out of the trade
            RpgPlayer p = api.rpgPlayers.get(event.getPlayer().getName());
            // If there player is not in a trade its the other person closing it
            // Prevent loops!
            if (!p.isInTrade()) {
                return;
            }
            p.setInTrade(false);
            String otherPlayer = title.substring(13);
            RpgPlayer otherP = api.rpgPlayers.get(otherPlayer);
            otherP.setInTrade(false);
            Player other = Bukkit.getPlayer(otherPlayer);

            // See if it was a complete trade
            boolean completeTrade = false;
            ItemStack statusOne = event.getInventory().getItem(0);
            ItemStack statusTwo = event.getInventory().getItem(5);
            if (statusOne.getDurability() == 13 && statusTwo.getDurability() == 13) {
                completeTrade = true;
            }

            // Message them and give their items back if it was not complete
            if (!completeTrade) {
                for (int i =0; i < 45; i++) {
                    // Only copy things under index 45, not forbidden, and on the left (less than 4)
                    if (!forbiddenIndex.contains(i) && i%9 < 4) {
                        // Give a's item to a
                        if (event.getPlayer().getOpenInventory().getItem(i) != null) {
                            event.getPlayer().getInventory().addItem(event.getPlayer().getOpenInventory().getItem(i));
                        }
                        // Give b's item to b
                        if (other.getOpenInventory().getItem(i) != null) {
                            other.getInventory().addItem(other.getOpenInventory().getItem(i));
                        }
                    }
                }
                Bukkit.getPlayer(event.getPlayer().getName()).sendMessage(ChatColor.RED + "Trade ended.");
                other.sendMessage(ChatColor.RED + "Trade ended.");
            }
            other.closeInventory();
        }
    }
}

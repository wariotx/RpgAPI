package com.vartala.soulofw0lf.rpgapi.tradeapi;

import com.vartala.soulofw0lf.rpgapi.RpgAPI;
import com.vartala.soulofw0lf.rpgapi.playerapi.RpgPlayer;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Created with IntelliJ IDEA.
 * User: Mothma
 * Date: 7/2/13
 * Time: 11:49 AM
 */
public class TradeHandler {
    RpgAPI rpgApi;

    public TradeHandler(RpgAPI rpgApi) {
        this.rpgApi = rpgApi;
    }

    public void sendRequest(RpgPlayer sender, RpgPlayer receiver) {
        Player sen = Bukkit.getPlayer(sender.getRealName());
        Player rec = Bukkit.getPlayer(receiver.getRealName());

        if (sender.isInTrade()) {
            sen.sendMessage(ChatColor.RED + "You are already in a Trade.");
            return;
        }
        if (receiver.isInTrade()) {
            sen.sendMessage(ChatColor.RED + "That person is already trading with someone.");
            return;
        }

        String name1 = rpgApi.activeNicks.get(sen.getName());
        String name2 = rpgApi.activeNicks.get(rec.getName());

        rec.sendMessage(name1 + " wishes to trade with you.");
        rec.sendMessage("Type /trade accept or /trade deny.");
        sen.sendMessage("Sent trade request to " + name2);
        receiver.setWaitingTrade(sender.getRealName());
    }

    public void answerRequest(RpgPlayer receiver, boolean accept) {
        RpgPlayer sender = rpgApi.rpgPlayers.get(receiver.getWaitingTrade());

        Player sen = Bukkit.getPlayer(sender.getRealName());
        Player rec = Bukkit.getPlayer(receiver.getRealName());

        if (sender == null) {
            rec.sendMessage(ChatColor.RED + "That player is no longer online.");
        } else {
            sen.sendMessage(ChatColor.GREEN + "Your request was accepted.");
            rec.sendMessage(ChatColor.GREEN + "Trade accepted.");

            String name1 = rpgApi.activeNicks.get(sen.getName());
            String name2 = rpgApi.activeNicks.get(rec.getName());
            new RpgTrade(sender, receiver, name1, name2);
        }

        sender.setWaitingTrade("");
    }
}

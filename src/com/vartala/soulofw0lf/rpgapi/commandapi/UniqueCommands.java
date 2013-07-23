package com.vartala.soulofw0lf.rpgapi.commandapi;

import com.vartala.soulofw0lf.rpgapi.RpgAPI;
import com.vartala.soulofw0lf.rpgapi.chatapi.*;
import com.vartala.soulofw0lf.rpgapi.enumapi.PlayerStat;
import com.vartala.soulofw0lf.rpgapi.particleapi.ParticleEffect;
import com.vartala.soulofw0lf.rpgapi.playerapi.RpgPlayer;
import com.vartala.soulofw0lf.rpgapi.playerapi.RpgPlayerBuilder;
import com.vartala.soulofw0lf.rpgapi.speedapi.SpeedHandler;
import com.vartala.soulofw0lf.rpgapi.tradeapi.TradeCommandProcessor;
import com.vartala.soulofw0lf.rpgapi.util.ChatColors;
import com.vartala.soulofw0lf.rpgapi.warpsapi.*;
import de.kumpelblase2.remoteentities.api.RemoteEntity;
import de.kumpelblase2.remoteentities.api.RemoteEntityType;
import de.kumpelblase2.remoteentities.api.thinking.Desire;
import de.kumpelblase2.remoteentities.api.thinking.goals.DesireFollowSpecific;
import de.kumpelblase2.remoteentities.api.thinking.goals.DesireLookAtSpecific;
import de.kumpelblase2.remoteentities.entities.RemotePlayer;
import de.kumpelblase2.remoteentities.api.thinking.DamageBehavior;
import de.kumpelblase2.remoteentities.api.thinking.InteractBehavior;
import de.kumpelblase2.remoteentities.api.thinking.goals.DesireLookAtNearest;
import de.kumpelblase2.remoteentities.persistence.EntityData;
import de.kumpelblase2.remoteentities.persistence.serializers.PreparationSerializer;
import de.kumpelblase2.remoteentities.persistence.serializers.YMLSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;

/**
 * Created by: soulofw0lf
 * Date: 6/14/13
 * Time: 12:56 AM
 * <p/>
 * This file is part of the Rpg Suite Created by Soulofw0lf and Linksy.
 * <p/>
 * The Rpg Suite is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * The Rpg Suite is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU General Public License
 * along with The Rpg Suite Plugin you have downloaded.  If not, see <http://www.gnu.org/licenses/>.
 */
public class UniqueCommands {
    public static void BaseCommandHandler(Player p, String[] command) {
        command[0] = command[0].replace("/", "").toLowerCase().trim();

        //Pass the command info the the warp command handler
        if (RpgAPI.warpsOn){if (WarpCommands.handler(p, command)){return;}}
        if (RpgAPI.chatOn){if (ChatCommands.ChatHandler(p, command)){return;}}
        //particle test
        if (command[0].equalsIgnoreCase("addperm")){
            String name = RpgAPI.activeNicks.get(p.getName());
            if (name == null){
                p.sendMessage("no name");
            }
            PermissionAttachment attachment = RpgAPI.permAttachments.get(name);
            if (attachment == null){
                p.sendMessage("can't find it.");
            }
            attachment.setPermission(command[1], true);
        }
        if (command[0].equalsIgnoreCase("effect"))  {

            final Player pl = p;
            final String[] commands = command;
            new BukkitRunnable(){
                Integer i = 30;
                @Override

                public void run(){
                    if (i <= 0){
                        cancel();
                    }
                    Location loc = pl.getLocation();
                    loc.setY(pl.getLocation().getY() +1);
                ParticleEffect.fromId(Integer.parseInt(commands[1])).play(pl, loc, 0f, 0f, 0f, Float.parseFloat(commands[2]), Integer.parseInt(commands[3]));
                    i--;
                }
            }.runTaskTimer(RpgAPI.getInstance(), 20, 5);
        }

        // Pass the command info to the trading processor
        if (RpgAPI.tradeOn) TradeCommandProcessor.process(p, command);

        if (command[0].equalsIgnoreCase("nick")) {
            String name = p.getName();
            String newName = command[1];
            String oldName = RpgAPI.activeNicks.get(name);
            RpgAPI.activeNicks.remove(name);
            RpgAPI.activeNicks.put(name, newName);
            RpgPlayer rp = RpgPlayerBuilder.RpgBuilder(newName);
            RpgAPI.rpgPlayers.remove(oldName);
            RpgAPI.rpgPlayers.put(newName, rp);
            SpeedHandler.SetWalkSpeed(rp, name);
            SpeedHandler.SetFlySpeed(rp, name);
        }

        if (command[0].equalsIgnoreCase("RpgHelp")) {

            p.sendMessage("Available commands are:");

        }




        /*
        *Leave this section below all other command codes!!!!! we don't want chat channels overwritting other commands!!!
         */
        Boolean subChat = false;

        for (String chatNames : RpgAPI.chatRealNames.keySet()) {
            if (chatNames == null) {
                continue;
            }
            if (chatNames.equalsIgnoreCase(command[0])) {
                command[0] = chatNames;
                subChat = true;
            }
        }

        if (subChat) {

            String activeChat = RpgAPI.chatRealNames.get(command[0]);

            ChatClass cC = new ChatClass();
            for (ChatClass chatClass : RpgAPI.chatClasses) {
                if (activeChat.equalsIgnoreCase(chatClass.getChannelName())) {
                    cC = chatClass;
                }
            }
            String senderName = p.getName();
            RpgPlayer sendPlayer = RpgAPI.rpgPlayers.get(RpgAPI.activeNicks.get(senderName));

            String language = "";
            if (sendPlayer.getActiveLanguage().isEmpty()) {
                language = "Common";
                sendPlayer.setActiveLanguage("Common");
            } else {
                language = sendPlayer.getActiveLanguage();
            }
            if (cC.getMutedPlayers().contains(senderName)) {
                Bukkit.getPlayer(senderName).sendMessage("You are muted in this chat.");
                return;
            }
            if (cC.getBannedPlayers().contains(senderName)) {
                Bukkit.getPlayer(senderName).sendMessage("You are banned from this chat.");
                return;
            }
            Boolean spyChat = cC.isChatSpy();
            String oldChat = sendPlayer.getActiveChannel();
            sendPlayer.setActiveChannel(activeChat);
            for (Player pl : Bukkit.getOnlinePlayers()) {
                String receiveName = pl.getName();
                StringBuilder buffer = new StringBuilder();
                for (int i = 1; i < command.length; i++) {
                    buffer.append(' ').append(command[i]);
                }
                String s = buffer.toString();
                Boolean canSee = true;
                for (ChatBehavior behavior : cC.getChannelBehaviors()) {
                    canSee = behavior.chatChannel(activeChat, receiveName, senderName, language, s, spyChat);
                }
                if (!canSee) {
                    continue;
                }
                pl.sendMessage(ChatProcessor.TitleString(RpgAPI.nameDisplays, RpgAPI.activeNicks.get(senderName), receiveName) + s);
            }

            sendPlayer.setActiveChannel(oldChat);
        }
    }



}
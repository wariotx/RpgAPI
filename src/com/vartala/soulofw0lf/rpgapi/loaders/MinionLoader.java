package com.vartala.soulofw0lf.rpgapi.loaders;

import com.vartala.soulofw0lf.rpgapi.RpgAPI;
import com.vartala.soulofw0lf.rpgapi.entityapi.EntityManager;
import com.vartala.soulofw0lf.rpgapi.entityapi.api.DespawnReason;
import com.vartala.soulofw0lf.rpgapi.entityapi.persistence.serializers.JSONSerializer;
import com.vartala.soulofw0lf.rpgapi.entityapi.persistence.serializers.YMLSerializer;
import com.vartala.soulofw0lf.rpgapi.mobcommandapi.MobEditingChatListener;
import com.vartala.soulofw0lf.rpgapi.entityapi.RemoteEntities;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.io.File;
import java.io.IOException;

/**
 * Created by: soulofw0lf
 * Date: 7/8/13
 * Time: 6:14 PM
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
public class MinionLoader {
    RpgAPI rpg;

    /**
     *
     * @param Rpg
     */
    public MinionLoader(RpgAPI Rpg){
        this.rpg = Rpg;
        String minecraftversion = this.rpg.getPresentMinecraftVersion();
        if(!minecraftversion.equals(RpgAPI.COMPATIBLE_VERSION)){
            this.rpg.getLogger().severe("Invalid minecraft version for remote entities (Required: " + RpgAPI.COMPATIBLE_VERSION + " ; Present: " + minecraftversion + ").");
            this.rpg.getLogger().severe("Disabling plugin to prevent issues.");
            Bukkit.getPluginManager().disablePlugin(this.rpg);
            return;
        }


        Bukkit.getPluginManager().registerEvents(new DisableListener(), this.rpg);
        RpgAPI.entityManager = RemoteEntities.createManager(RpgAPI.getInstance());
        if (RpgAPI.useMySql){
            RpgAPI.entityManager.setEntitySerializer(new JSONSerializer(RpgAPI.getInstance()));
        } else {
            RpgAPI.entityManager.setEntitySerializer(new YMLSerializer(RpgAPI.getInstance()));
            RpgAPI.entityManager.loadEntities();
        }
        this.rpg.mobEditingChatListener = new MobEditingChatListener(this.rpg);
        RpgAPI.minionConfig = YamlConfiguration.loadConfiguration(new File("plugins/RpgMinions/Minions.yml"));
        RpgAPI.minionLocaleConfig = YamlConfiguration.loadConfiguration(new File("plugins/RpgMinions/Locale/Minions.yml"));
        RpgAPI.mobCommand = YamlConfiguration.loadConfiguration(new File("plugins/RpgMinions/MobCommands.yml"));
        RpgAPI.mobLocaleCommand = YamlConfiguration.loadConfiguration(new File("plugins/RpgMinions/Locale/MobCommands.yml"));
        if (RpgAPI.mobCommand.get("Mob Commands") == null) {
            RpgAPI.mobCommand.set("Mob Commands.Set 1.Item 1.Commands.1.ClickType", "right");
        }
        if (RpgAPI.minionConfig.get("Minions") == null) {
            RpgAPI.minionConfig.set("Minions", "this file is used to store all minion and monster data (Mysql is highly recommended!");
        }
        try {
            RpgAPI.minionConfig.save(new File("plugins/RpgMinions/Minions.yml"));
            RpgAPI.minionLocaleConfig.save(new File("plugins/RpgMinions/Locale/Minions.yml"));
            RpgAPI.mobCommand.save(new File("plugins/RpgMinions/MobCommands.yml"));
            RpgAPI.mobLocaleCommand.save(new File("plugins/RpgMinions/Locale/MobCommands.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //after file is saved
    }
    class DisableListener implements Listener
    {
        @EventHandler
        public void onPluginDisable(PluginDisableEvent event)
        {
            EntityManager manager = RemoteEntities.getManagerOfPlugin(event.getPlugin().getName());
            if(manager != null)
            {
                manager.despawnAll(DespawnReason.PLUGIN_DISABLE);
                manager.unregisterEntityLoader();
            }
        }
    }
}

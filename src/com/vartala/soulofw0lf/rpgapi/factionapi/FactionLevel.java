package com.vartala.soulofw0lf.rpgapi.factionapi;

import com.vartala.soulofw0lf.rpgapi.minionapi.MinionBehavior;
import com.vartala.soulofw0lf.rpgapi.playerapi.Reputation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by: soulof
 * Date: 6/4/13
 * Time: 5:58 AM
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
public class FactionLevel {

    //name of a faction
    private String name = "";

    //behaviors to add to factions based on levels
    private List<FactionBehavior> behavior = new ArrayList<FactionBehavior>();
    //
    /*
     * all getters and setters
     */

    public FactionLevel() {

    }

    /**
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     */
    public List<FactionBehavior> getBehavior() {
        return behavior;
    }

    /**
     *
     * @param behavior
     */
    public void setBehavior(List<FactionBehavior> behavior) {
        this.behavior = behavior;
    }
}

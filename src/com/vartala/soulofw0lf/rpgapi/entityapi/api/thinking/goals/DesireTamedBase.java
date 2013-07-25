package com.vartala.soulofw0lf.rpgapi.entityapi.api.thinking.goals;

import net.minecraft.server.v1_6_R2.EntityLiving;
import net.minecraft.server.v1_6_R2.EntityTameableAnimal;
import org.bukkit.craftbukkit.v1_6_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import com.vartala.soulofw0lf.rpgapi.entityapi.api.RemoteEntity;
import com.vartala.soulofw0lf.rpgapi.entityapi.api.features.TamingFeature;

public abstract class DesireTamedBase extends DesireTargetBase
{
	protected EntityLiving m_animal;

	@Deprecated
	public DesireTamedBase(RemoteEntity inEntity, float inDistance, boolean inShouldCheckSight)
	{
		super(inEntity, inDistance, inShouldCheckSight);
	}

	public DesireTamedBase(float inDistance, boolean inShouldCheckSight)
	{
		super(inDistance, inShouldCheckSight);
	}

	protected boolean isTamed()
	{
		if(this.m_animal instanceof EntityTameableAnimal)
			return ((EntityTameableAnimal)this.m_animal).isTamed();
		else
			return this.getRemoteEntity().getFeatures().getFeature(TamingFeature.class).isTamed();
	}

	protected EntityLiving getTamer()
	{
		if(this.m_animal instanceof EntityTameableAnimal)
			return ((EntityTameableAnimal)this.m_animal).getOwner();
		else
		{
			Player pl = this.getRemoteEntity().getFeatures().getFeature(TamingFeature.class).getTamer();
			if(pl == null)
				return null;

			return ((CraftPlayer)pl).getHandle();
		}
	}
}

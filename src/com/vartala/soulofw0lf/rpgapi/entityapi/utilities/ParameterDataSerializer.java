package com.vartala.soulofw0lf.rpgapi.entityapi.utilities;

import java.lang.reflect.Type;
import org.bukkit.craftbukkit.libs.com.google.gson.*;
import com.vartala.soulofw0lf.rpgapi.entityapi.persistence.EntityData;
import com.vartala.soulofw0lf.rpgapi.entityapi.persistence.ParameterData;

public class ParameterDataSerializer implements JsonSerializer<ParameterData>
{

	@Override
	public JsonElement serialize(ParameterData arg0, Type arg1, JsonSerializationContext arg2)
	{
		JsonObject data = new JsonObject();

		data.addProperty("type", arg0.type);
		data.addProperty("pos", arg0.pos);
		data.addProperty("special", arg0.special);
		data.add("value", arg2.serialize(EntityData.objectParser.serialize(arg0.value)));

		return data;
	}
}
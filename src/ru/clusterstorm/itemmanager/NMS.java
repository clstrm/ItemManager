package ru.clusterstorm.itemmanager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.bukkit.entity.Player;

public class NMS {

	private Class<?> iChatBaseComponent, packet, craftPlayer, entityPlayer, connection;
	private Method a, getHandle, sendPacket;
	private Field playerConnection;
	
	public NMS(String ver) throws Exception {
		iChatBaseComponent = Class.forName("net.minecraft.server." + ver + ".IChatBaseComponent");
		Class<?> p = Class.forName("net.minecraft.server." + ver + ".Packet");
		packet = Class.forName("net.minecraft.server." + ver + ".PacketPlayOutChat");
		connection = Class.forName("net.minecraft.server." + ver + ".PlayerConnection");
		entityPlayer = Class.forName("net.minecraft.server." + ver + ".EntityPlayer");
		craftPlayer = Class.forName("org.bukkit.craftbukkit." + ver + ".entity.CraftPlayer");
		getHandle = craftPlayer.getMethod("getHandle");
		playerConnection = entityPlayer.getField("playerConnection");
		sendPacket = connection.getMethod("sendPacket", p);
		
		Class<?> serializer;
		try {
			serializer = Class.forName("net.minecraft.server." + ver + ".IChatBaseComponent$ChatSerializer");
		} catch(ClassNotFoundException e) {
			serializer = Class.forName("net.minecraft.server." + ver + ".ChatSerializer");
		}
		
		a = serializer.getMethod("a", String.class);
	}
	
	public void sendJson(Player p, String json) {
		try {
			Object iChat = a.invoke(null, json);
			Object packet = this.packet.getConstructor(this.iChatBaseComponent).newInstance(iChat);
			Object entity = getHandle.invoke(p);
			Object conn = this.playerConnection.get(entity);
			sendPacket.invoke(conn, packet);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}

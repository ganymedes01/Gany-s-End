package ganymedes01.ganysend.network.packet;

import ganymedes01.ganysend.network.PacketTypeHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import net.minecraft.network.INetworkManager;
import cpw.mods.fml.common.network.Player;

/**
 * Gany's End
 * 
 * @author ganymedes01
 * 
 */

public class CustomPacket {

	public PacketTypeHandler packetType;

	public CustomPacket(PacketTypeHandler packetType) {
		this.packetType = packetType;
	}

	public byte[] populate() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);

		try {
			dos.writeByte(packetType.ordinal());
			writeData(dos);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}

		return bos.toByteArray();
	}

	public void readPopulate(DataInputStream data) {
		try {
			readData(data);
		} catch (IOException e) {
			e.printStackTrace(System.err);
		}
	}

	public void readData(DataInputStream data) throws IOException {

	}

	public void writeData(DataOutputStream dos) throws IOException {

	}

	public void execute(INetworkManager network, Player player) {

	}

	public void setKey(int key) {

	}
}
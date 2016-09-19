package com.stringee.application.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import com.stringee.signaling.application.protocol.packet.AppPacket;

public class Encoder extends MessageToByteEncoder<AppPacket> {

	@Override
	protected void encode(ChannelHandlerContext chc, AppPacket packet, ByteBuf out) {
		packet.encodeJson();

		out.writeBytes(AppPacket.MAGIC);
		out.writeShort((short) packet.getLength());
		out.writeShort(packet.getService());
		out.writeBytes(packet.getData());
	}
}

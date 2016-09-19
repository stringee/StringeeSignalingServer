package com.stringee.application.protocol.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import org.apache.log4j.Logger;
import com.stringee.signaling.application.protocol.packet.AppPacket;

public class Decoder extends ByteToMessageDecoder {

	private final static Logger LOGGER = Logger.getLogger("Decoder");

	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
		if (in.readableBytes() < 6) {
			return;
		}

		in.markReaderIndex();

		byte[] magic = new byte[2];
		in.readBytes(magic);
		if (magic[0] != 'B' || magic[1] != 'C') {
			in.resetReaderIndex();
			LOGGER.error("Magic error: [" + new String(magic) + "]");
			throw new CorruptedFrameException("Invalid magic number: " + new String(magic));
		}

		AppPacket packet = new AppPacket();
		packet.setLength(in.readShort());
		packet.setService(in.readShort());

		if (packet.getLength() > 0) {
			if (in.readableBytes() < packet.getLength()) {
				in.resetReaderIndex();
				return;
			}

			byte[] bytes = new byte[packet.getLength()];
			in.readBytes(bytes);
			packet.setData(bytes);
		}

		out.add(packet);
	}
}

/*
 * ServerListPlus - https://git.io/slp
 * Copyright (C) 2014 Minecrell (https://github.com/Minecrell)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.minecrell.serverlistplus.server.network.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import net.minecrell.serverlistplus.server.network.ClientHandler;
import net.minecrell.serverlistplus.server.network.protocol.packet.ClientPacket;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class MinecraftDecoder extends ByteToMessageDecoder {

    private static final Logger logger = Logger.getLogger(MinecraftDecoder.class.getName());

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (!in.isReadable()) {
            return;
        }

        int id = MinecraftProtocol.readVarInt(in);
        ClientPacket packet = ClientHandler.getState(ctx).getPacket(id);
        if (packet == null) {
            logger.log(Level.WARNING, "Unknown packet: " + Integer.toHexString(id));
            return;
        }

        packet.read(in);
        if (in.isReadable()) {
            logger.log(Level.WARNING, "Packet " + id + " was not fully read: " + in.readableBytes() + " bytes left");
        }

        out.add(packet);
    }

}

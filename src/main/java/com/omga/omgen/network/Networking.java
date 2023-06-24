package com.omga.omgen.network;

import com.omga.omgen.Omgen;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Networking {

    private short index = 0;
    public final SimpleChannel HANDLER;
    public static Networking INSTANCE;


    // REG MY STUFF HERE

    public void registerMessages() {
        registerMessage(SyncGensPacket.class, SyncGensPacket::encode, SyncGensPacket::new, SyncGensPacket::handle);
    }

    ////////////////////////////////////////////////////////////////



    public Networking() {
        this.HANDLER = Omgen.SYNC_CHANNEL;
        registerMessages();
    }





    ///////////////////////////////////////////////////////////////////
    public static void sendTo(Object msg, ServerPlayer player) {
        INSTANCE.sendToInternal(msg, player);
    }
    public static void sendToServer(Object msg) {
        INSTANCE.sendToServerInternal(msg);
    }
    public static <MSG> void send(PacketDistributor.PacketTarget target, MSG message) {
        INSTANCE.sendInternal(target, message);
    }




    ///////////////////////////////////////////////////////////////////

    private void sendToInternal(Object msg, ServerPlayer player) {
        if (!(player instanceof FakePlayer))
            HANDLER.sendTo(msg, player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }
    private void sendToServerInternal(Object msg) {
        HANDLER.sendToServer(msg);
    }
    private <MSG> void sendInternal(PacketDistributor.PacketTarget target, MSG message) {

    }
    ///////////////////////////////////////////////////////////////////
    public <MSG> void registerMessage(Class<MSG> messageType, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> messageConsumer) {
        HANDLER.messageBuilder(messageType, index).
                decoder(decoder).
                encoder(encoder).
                consumer(messageConsumer).
                add();
        index++;
        if (index > 0xFF)
            throw new RuntimeException("Too many messages!");
    }



}

package com.omga.omgen.network;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;
import com.omga.omgen.logic.GenerationCondition;
import com.omga.omgen.logic.GenerationEntry;
import com.omga.omgen.resources.OmgenReloadListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncGensPacket {
    public SyncGensPacket() {
    }
    public SyncGensPacket(FriendlyByteBuf buffer) {
        int count = buffer.readVarInt();
        var ser = new GenerationCondition.Serializer();
        ImmutableMap.Builder<ResourceLocation, GenerationEntry> map = new ImmutableMap.Builder<>();
        for (int i = 0; i < count; i++) {
            ResourceLocation id = buffer.readResourceLocation();
            GenerationEntry gen = ser.deserialize(JsonParser.parseString(buffer.readUtf()), null, null);
            if (gen != null) {
                map.put(id, gen);
            } else {
                throw new IllegalStateException();
            }
        }
        OmgenReloadListener.entries = map.build();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(OmgenReloadListener.entries.size());
        var ser = new GenerationCondition.Serializer();
        OmgenReloadListener.entries.forEach((k, v) -> {
            buffer.writeResourceLocation(k);
            buffer.writeUtf(ser.serialize(v, null, null).getAsJsonObject().toString());
        });
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> handle(this, context));
    }

    private static void handle(SyncGensPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().setPacketHandled(true);
    }

}

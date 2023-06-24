package com.omga.omgen.resources;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.omga.omgen.logic.GenerationCondition;
import com.omga.omgen.logic.GenerationEntry;
import com.omga.omgen.network.Networking;
import com.omga.omgen.network.SyncGensPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.Map;
@Mod.EventBusSubscriber
public class OmgenReloadListener extends SimpleJsonResourceReloadListener {
    public static final String FOLDER = "gens";
    public static ImmutableMap<ResourceLocation, GenerationEntry> entries;
    private Map<ResourceLocation, JsonElement> map;
    private static final Logger LOGGER = LogUtils.getLogger();
    private static OmgenReloadListener currentInstance = null;
    private final String directory;
    private final Gson gson;
    public OmgenReloadListener(Gson p_10768_, String p_10769_) {
        super(p_10768_, p_10769_);
        directory = p_10769_;
        gson = p_10768_;
    }


    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller pf) {
        this.map = map;
        currentInstance = this;
        update();
    }
    @SubscribeEvent
    public static void tagsUpdated(TagsUpdatedEvent event) {
        if (currentInstance != null)
            update();
    }
    @SubscribeEvent
    public static void addReloadListenerEvent (AddReloadListenerEvent event) {
        event.addListener(new OmgenReloadListener(new Gson(), FOLDER));
    }
    //*
    @SubscribeEvent
    public static void regReloadListenerEvent (RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new OmgenReloadListener(new Gson(), FOLDER));
    }

    @SubscribeEvent
    public static void onDatapackSync(OnDatapackSyncEvent event) {
        if (event.getPlayer() != null) {
            Networking.sendTo(new SyncGensPacket(), event.getPlayer());
        } else {
            Networking.send(PacketDistributor.ALL.noArg(), new SyncGensPacket());
        }
    }

    protected static void update() {
        var serializer = new GenerationCondition.Serializer();
        ImmutableMap.Builder<ResourceLocation, GenerationEntry> builder = ImmutableMap.builder();
        currentInstance.map.forEach((r, js) -> {
            try {
                builder.put(r, serializer.deserialize(js, null, null));
            } catch (Exception e) {
                LOGGER.debug("Couldn't parse {}", r);
                e.printStackTrace();
            }
        });
        entries = builder.build();
    }
    //*/
}

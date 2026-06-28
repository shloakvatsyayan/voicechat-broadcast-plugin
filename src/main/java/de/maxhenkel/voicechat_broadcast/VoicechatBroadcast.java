package de.maxhenkel.voicechat_broadcast;

import de.maxhenkel.voicechat.api.BukkitVoicechatService;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoicechatBroadcast extends JavaPlugin {

    public static final String PLUGIN_ID = "voicechat_broadcast";

    private BroadcastVoicechatPlugin voicechatPlugin;

    @Override
    public void onEnable() {
        BukkitVoicechatService voicechatService = getServer().getServicesManager().load(BukkitVoicechatService.class);
        if (voicechatService == null) {
            getLogger().warning("Failed to register voice chat broadcast plugin");
            return;
        }

        voicechatPlugin = new BroadcastVoicechatPlugin();
        voicechatService.registerPlugin(voicechatPlugin);
        getLogger().info("Successfully registered voice chat broadcast plugin");
    }

    @Override
    public void onDisable() {
        if (voicechatPlugin != null) {
            getServer().getServicesManager().unregister(voicechatPlugin);
            getLogger().info("Successfully unregistered voice chat broadcast plugin");
        }
    }
}

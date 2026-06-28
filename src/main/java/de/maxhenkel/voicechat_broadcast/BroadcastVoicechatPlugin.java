package de.maxhenkel.voicechat_broadcast;

import de.maxhenkel.voicechat.api.Group;
import de.maxhenkel.voicechat.api.VoicechatConnection;
import de.maxhenkel.voicechat.api.VoicechatPlugin;
import de.maxhenkel.voicechat.api.VoicechatServerApi;
import de.maxhenkel.voicechat.api.events.EventRegistration;
import de.maxhenkel.voicechat.api.events.MicrophonePacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

public final class BroadcastVoicechatPlugin implements VoicechatPlugin {

    private static final String BROADCAST_GROUP_NAME = "broadcast";
    public static final Permission BROADCAST_PERMISSION = new Permission("voicechat_broadcast.broadcast", PermissionDefault.OP);

    @Override
    public String getPluginId() {
        return VoicechatBroadcast.PLUGIN_ID;
    }

    @Override
    public void registerEvents(EventRegistration registration) {
        registration.registerEvent(MicrophonePacketEvent.class, this::onMicrophone);
    }

    private void onMicrophone(MicrophonePacketEvent event) {
        VoicechatConnection senderConnection = event.getSenderConnection();
        if (senderConnection == null) {
            return;
        }

        if (!(senderConnection.getPlayer().getPlayer() instanceof Player broadcaster)) {
            return;
        }

        if (!broadcaster.hasPermission(BROADCAST_PERMISSION)) {
            return;
        }

        Group voicechatGroup = senderConnection.getGroup();
        if (voicechatGroup == null) {
            return;
        }

        if (!voicechatGroup.getName().strip().equalsIgnoreCase(BROADCAST_GROUP_NAME)) {
            return;
        }

        event.cancel();

        VoicechatServerApi voicechatApi = event.getVoicechat();
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (onlinePlayer.getUniqueId().equals(broadcaster.getUniqueId())) {
                continue;
            }

            VoicechatConnection listenerConnection = voicechatApi.getConnectionOf(onlinePlayer.getUniqueId());
            if (listenerConnection == null) {
                continue;
            }

            voicechatApi.sendStaticSoundPacketTo(listenerConnection, event.getPacket().staticSoundPacketBuilder().build());
        }
    }
}

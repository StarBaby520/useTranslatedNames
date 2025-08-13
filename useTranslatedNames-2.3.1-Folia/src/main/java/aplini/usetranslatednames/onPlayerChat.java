package aplini.usetranslatednames;

import aplini.usetranslatednames.Enum.Key;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import static aplini.usetranslatednames.UseTranslatedNames.*;

public class onPlayerChat implements Listener {

    static onPlayerChat func = null;
    static Key mode;

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event){
        // 取消发送消息
        event.setCancelled(true);

        Player player = event.getPlayer();
        String format = event.getFormat();
        String message = event.getMessage();

        Bukkit.getGlobalRegionScheduler().execute(plugin, () -> {
            String msg = String.format(format, player.getName(), message);

            if(_debug >= 1){
                plugin.getLogger().info("");
                plugin.getLogger().info("");
                plugin.getLogger().info("[DEBUG] [PlayerChat] [Player: "+ player.getName() +
                        ", Lang: "+ player.getLocale() +"] [Length: "+ msg.length() +"], [MODE: "+ mode.toString() +"]");
                if(_debug >= 2){
                    plugin.getLogger().info("  - [FORMAT]: "+ format);
                    plugin.getLogger().info("  - [MSG]: "+ message);
                    plugin.getLogger().info("  - [GET]: "+ msg);
                }
            }
            Bukkit.getConsoleSender().sendMessage(msg);
            switch(mode){
                case Convert -> {
                    TextComponent component = new TextComponent(msg);
                    for(Player li : Bukkit.getOnlinePlayers()){
                        Player target = li;
                        target.getScheduler().run(plugin, t -> target.spigot().sendMessage(component), null);
                    }
                }
                case ConvertBypass -> {
                    // 静默发送消息给每个玩家
                    PacketContainer chatPacket = protocolManager.createPacket(PacketType.Play.Server.SYSTEM_CHAT);
                    chatPacket.getChatComponents().write(0, WrappedChatComponent.fromText(msg));
                    for(Player li : Bukkit.getOnlinePlayers()){
                        Player target = li;
                        target.getScheduler().run(plugin, t -> protocolManager.sendServerPacket(target, chatPacket, false), null);
                    }
                }
            }
        });
    }
}
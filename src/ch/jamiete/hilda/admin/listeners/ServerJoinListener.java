package ch.jamiete.hilda.admin.listeners;

import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.events.EventHandler;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;

public class ServerJoinListener {
    private final AdminPlugin plugin;

    public ServerJoinListener(final AdminPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onGuildJoin(final GuildJoinEvent event) {
        if (this.plugin.getChannel() != null) {
            this.plugin.getChannel().sendMessage(":heavy_plus_sign: " + this.plugin.getServerInfo(event.getGuild())).queue();
        }
    }

    @EventHandler
    public void onGuildLeave(final GuildLeaveEvent event) {
        if (this.plugin.getChannel() != null) {
            this.plugin.getChannel().sendMessage(":heavy_multiplication_x: " + this.plugin.getServerInfo(event.getGuild())).queue();
        }
    }

}

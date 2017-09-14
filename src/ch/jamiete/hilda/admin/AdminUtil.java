package ch.jamiete.hilda.admin;

import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;

public class AdminUtil {

    /**
     * Gets a text channel that the bot can speak in.
     * @param guild The guild to check.
     * @return A text channel that the bot can speak in or null.
     */
    public static TextChannel getChannel(final Guild guild) {
        if (guild.getSelfMember().hasPermission(guild.getDefaultChannel(), Permission.MESSAGE_WRITE)) {
            return guild.getDefaultChannel();
        }

        for (TextChannel channel : guild.getTextChannels()) {
            if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_WRITE)) {
                return channel;
            }
        }

        return null;
    }

    public static String getName(final Guild guild) {
        return guild.getName() + " (" + guild.getId() + ")";
    }

}

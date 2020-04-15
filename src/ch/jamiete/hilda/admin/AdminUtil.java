/*
 * Copyright 2017 jamietech
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.jamiete.hilda.admin;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

public class AdminUtil {

    /**
     * Gets a text channel that the bot can speak in. <p>
     * Defaults to the channel that has the most users that can speak.
     * @param guild The guild to check.
     * @return A text channel that the bot can speak in or null.
     */
    public static TextChannel getChannel(final Guild guild) {
        TextChannel channel = null;
        int highest = 0;

        for (final TextChannel c : guild.getTextChannels()) {
            if (!guild.getSelfMember().hasPermission(c, Permission.MESSAGE_WRITE)) {
                continue;
            }

            final int speak = (int) guild.getMembers().stream().filter(c::canTalk).count();

            if (speak > highest) {
                highest = speak;
                channel = c;
            }
        }

        if (channel != null) {
            return channel;
        }

        if (guild.getSelfMember().hasPermission(guild.getDefaultChannel(), Permission.MESSAGE_WRITE)) {
            return guild.getDefaultChannel();
        }

        for (final TextChannel c : guild.getTextChannels()) {
            if (guild.getSelfMember().hasPermission(c, Permission.MESSAGE_WRITE)) {
                return channel;
            }
        }

        return null;
    }

    public static String getFriendly(final long bytes, final boolean si) {
        final int unit = si ? 1000 : 1024;

        if (bytes < unit) {
            return bytes + " B";
        }

        final int exp = (int) (Math.log(bytes) / Math.log(unit));
        final String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");

        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static String getName(final Guild guild) {
        return guild.getName() + " (" + guild.getId() + ")";
    }

}

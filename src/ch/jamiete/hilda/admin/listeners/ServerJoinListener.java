/*******************************************************************************
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
 *******************************************************************************/
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

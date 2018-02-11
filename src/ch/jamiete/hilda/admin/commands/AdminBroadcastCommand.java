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
package ch.jamiete.hilda.admin.commands;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.admin.AdminUtil;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.commands.CommandManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class AdminBroadcastCommand extends ChannelSubCommand {

    public AdminBroadcastCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("broadcast");
        this.setAliases(Collections.singletonList("announce"));
        this.setDescription("Broadcasts an announcement to all servers.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        final List<String> failed = new ArrayList<>();

        final String broadcast = "**THE ADMINISTRATORS OF " + this.hilda.getBot().getSelfUser().getName() + " HAVE ANNOUNCED THE FOLLOWING TO ALL SERVERS**\n" + Util.combineSplit(0, arguments, " ");
        final EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Notice from administrators");
        eb.setThumbnail(this.hilda.getBot().getSelfUser().getEffectiveAvatarUrl());
        eb.setDescription(broadcast);
        eb.setColor(Color.decode("#FF0303"));
        eb.setFooter("Do not respond to this message. Use " + CommandManager.PREFIX + "report <message> to contact the administrators.", null);

        final MessageEmbed embed = eb.build();

        for (final Guild guild : this.hilda.getBot().getGuilds()) {
            final TextChannel channel = AdminUtil.getChannel(guild);

            if (channel == null) {
                failed.add(AdminUtil.getName(guild));
            } else {
                channel.sendMessage(embed).queue();
            }
        }

        String response = "Sent notice!";

        if (!failed.isEmpty()) {
            response += " Couldn't find a channel to send the notice to in " + Util.getAsList(failed) + ".";
        }

        this.reply(message, response);
        this.reply(message, embed);
    }

}

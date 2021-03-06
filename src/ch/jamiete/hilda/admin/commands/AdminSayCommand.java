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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import java.awt.Color;

class AdminSayCommand extends ChannelSubCommand {

    AdminSayCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("say");
        this.setDescription("Sends a message to a server.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length < 2) {
            this.usage(message, "<channel/server id> <message...>");
            return;
        }

        Guild guild = null;

        try {
            guild = this.hilda.getBot().getGuildById(arguments[0]);
        } catch (final Exception e) {
            // Ignore
        }

        TextChannel ch = null;

        try {
            ch = this.hilda.getBot().getTextChannelById(arguments[0]);
        } catch (final Exception e) {
            // Ignore
        }

        if (guild == null && ch == null) {
            this.reply(message, "I couldn't find that ID.");
            return;
        }

        final TextChannel channel = ch == null ? AdminUtil.getChannel(guild) : ch;

        if (channel == null) {
            this.reply(message, "There's no channel I can send that message to.");
            return;
        }

        final String broadcast = "**THE ADMINISTRATORS OF " + this.hilda.getBot().getSelfUser().getName() + " HAVE ANNOUNCED THE FOLLOWING TO THIS SERVER**\n" + Util.combineSplit(1, arguments, " ");
        final EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Notice from administrators");
        eb.setThumbnail(this.hilda.getBot().getSelfUser().getEffectiveAvatarUrl());
        eb.setDescription(broadcast);
        eb.setColor(Color.decode("#FF0303"));
        eb.setFooter("Do not respond to this message. Use " + CommandManager.PREFIX + "report <message> to contact the administrators.", null);

        channel.sendMessage(eb.build()).queue();
        this.reply(message, eb.build());
    }

}

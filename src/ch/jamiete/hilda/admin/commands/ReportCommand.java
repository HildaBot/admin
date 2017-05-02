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
package ch.jamiete.hilda.admin.commands;

import java.awt.Color;
import java.util.Arrays;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.commands.ChannelCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;

public class ReportCommand extends ChannelCommand {
    private final AdminPlugin plugin;

    public ReportCommand(final Hilda hilda, final AdminPlugin plugin) {
        super(hilda);

        this.plugin = plugin;

        this.setName("report");
        this.setAliases(Arrays.asList(new String[] { "reports", "suggest", "suggestion", "suggestions" }));
        this.setDescription("Report issues or suggestions to bot administrators.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length == 0) {
            this.reply(message, "You must provide content to send as a report.");
            return;
        }

        EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("New report received", null);
        eb.setColor(Color.decode("#a51d1d"));
        eb.setThumbnail(message.getAuthor().getAvatarUrl());
        eb.addField("Reporter", message.getAuthor().getName() + "#" + message.getAuthor().getDiscriminator(), false);
        eb.addField("Report", Util.combineSplit(0, arguments, " "), false);
        eb.addField("Server", this.plugin.getServerInfo(message.getGuild()), false);
        eb.addField("Channel", message.getTextChannel().getName() + " (" + message.getChannel().getId() + ")", false);

        this.plugin.getChannel().sendMessage(eb.build()).queue();

        if (message.getGuild().getSelfMember().hasPermission(message.getTextChannel(), Permission.MESSAGE_MANAGE)) {
            message.delete().queue();
        }

        message.getAuthor().openPrivateChannel().queue(channel -> channel.sendMessage("I have sent your report to the administrators who will review it shortly. Thanks!").queue());
    }

}

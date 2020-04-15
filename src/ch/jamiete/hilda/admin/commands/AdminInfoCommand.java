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
import ch.jamiete.hilda.commands.ChannelSubCommand;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;

class AdminInfoCommand extends ChannelSubCommand {

    AdminInfoCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("info");
        this.setDescription("Gets specific information about a server.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        Guild guild;

        if (arguments.length == 1) {
            guild = this.hilda.getBot().getGuildById(arguments[0]);
        } else {
            guild = message.getGuild();
        }

        if (guild == null) {
            this.reply(message, "I'm not on that server.");
            return;
        }

        final MessageBuilder mb = new MessageBuilder();

        mb.append("Server information", MessageBuilder.Formatting.UNDERLINE).append("\n");
        mb.append("For ").append(AdminUtil.getName(guild)).append("\n\n");

        mb.append("Roles:", MessageBuilder.Formatting.BOLD);
        mb.append(' ');

        for (final Role role : guild.getSelfMember().getRoles()) {
            mb.append(role.getName()).append(" (").append(role.getId()).append(")");
            mb.append(", ");
        }

        mb.replaceLast(", ", "");
        mb.append('\n');

        mb.append("Channel:", MessageBuilder.Formatting.BOLD);
        mb.append(' ');
        final TextChannel ch = AdminUtil.getChannel(guild);
        mb.append(ch == null ? "Error" : "#" + ch.getName());
        mb.append('\n');

        mb.append("Permissions:", MessageBuilder.Formatting.BOLD);
        mb.append(' ');

        for (final Permission permission : Permission.getPermissions(Permission.ALL_GUILD_PERMISSIONS)) {
            mb.append(permission.getName().toLowerCase().replace("_", ""), guild.getSelfMember().hasPermission(permission) ? MessageBuilder.Formatting.UNDERLINE : MessageBuilder.Formatting.STRIKETHROUGH);
            mb.append(", ");
        }

        mb.replaceLast(", ", "");
        mb.append('\n');
        mb.append('\n');

        mb.append("Channels:", MessageBuilder.Formatting.BOLD);

        for (final TextChannel channel : guild.getTextChannels()) {
            mb.append('\n');
            mb.append("    ");
            mb.append(channel.getName(), MessageBuilder.Formatting.BOLD);
            mb.append(": ");

            for (final Permission permission : Permission.getPermissions(Permission.ALL_TEXT_PERMISSIONS)) {
                mb.append(permission.getName().toLowerCase().replace("_", ""), guild.getSelfMember().hasPermission(channel, permission) ? MessageBuilder.Formatting.UNDERLINE : MessageBuilder.Formatting.STRIKETHROUGH);
                mb.append(", ");
            }

            mb.replaceLast(", ", "");

            if (this.hilda.getCommandManager().isChannelIgnored(channel.getId())) {
                mb.append(" (Channel IGNORED)");
            }
        }

        mb.append('\n');
        mb.append('\n');

        mb.append("Owner:", MessageBuilder.Formatting.BOLD);
        mb.append(" ").append(Util.getName(guild.getOwner())).append(" ").append(guild.getOwner().getUser().getId());

        mb.buildAll(MessageBuilder.SplitPolicy.NEWLINE).forEach(m -> message.getChannel().sendMessage(m).queue());
    }

}

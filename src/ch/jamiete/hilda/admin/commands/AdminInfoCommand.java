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

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.MessageBuilder.Formatting;
import net.dv8tion.jda.core.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

public class AdminInfoCommand extends ChannelSubCommand {

    protected AdminInfoCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("info");
        this.setDescription("Gets specific information about a server.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        Guild guild = null;

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

        mb.append("Roles:", Formatting.BOLD);
        mb.append(' ');

        for (Role role : guild.getSelfMember().getRoles()) {
            mb.append(role.getName() + " (" + role.getId() + ")");
            mb.append(", ");
        }

        mb.replaceLast(", ", "");
        mb.append('\n');

        mb.append("Permissions:", Formatting.BOLD);
        mb.append(' ');

        for (Permission permission : Permission.getPermissions(Permission.ALL_GUILD_PERMISSIONS)) {
            mb.append(permission.getName().toLowerCase().replace("_", ""), guild.getSelfMember().hasPermission(permission) ? Formatting.UNDERLINE : Formatting.STRIKETHROUGH);
            mb.append(", ");
        }

        mb.replaceLast(", ", "");
        mb.append('\n');
        mb.append('\n');

        mb.append("Channels:", Formatting.BOLD);

        for (TextChannel channel : guild.getTextChannels()) {
            mb.append('\n');
            mb.append("    ");
            mb.append(channel.getName(), Formatting.BOLD);
            mb.append(": ");

            for (Permission permission : Permission.getPermissions(Permission.ALL_TEXT_PERMISSIONS)) {
                mb.append(permission.getName().toLowerCase().replace("_", ""), guild.getSelfMember().hasPermission(channel, permission) ? Formatting.UNDERLINE : Formatting.STRIKETHROUGH);
                mb.append(", ");
            }

            mb.replaceLast(", ", "");

            if (this.hilda.getCommandManager().isChannelIgnored(channel.getId())) {
                mb.append(" (Channel IGNORED)");
            }
        }

        mb.append('\n');
        mb.append('\n');

        mb.append("Owner:", Formatting.BOLD);
        mb.append(" ").append(guild.getOwner().getUser().getName()).append("#").append(guild.getOwner().getUser().getDiscriminator());

        mb.buildAll(SplitPolicy.NEWLINE).forEach(m -> message.getChannel().sendMessage(m).queue());
    }

}

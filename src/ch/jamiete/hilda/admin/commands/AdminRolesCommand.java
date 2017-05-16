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
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class AdminRolesCommand extends ChannelSubCommand {

    protected AdminRolesCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("roles");
        this.setDescription("Lists a server's roles.");
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

        mb.append("Roles of " + guild.getId(), Formatting.BOLD);
        mb.append('\n');
        mb.append("Requested by " + message.getAuthor().getName(), Formatting.ITALICS);
        mb.append('\n');

        for (final Role role : guild.getRoles()) {
            mb.append('\n');

            if (role.getName().equalsIgnoreCase("@everyone")) {
                mb.append("default role for everyone", Formatting.BOLD);
            } else {
                mb.append(role.getName(), Formatting.BOLD);
            }

            mb.append(' ');
            mb.append(role.getId(), Formatting.ITALICS);
        }

        mb.buildAll(SplitPolicy.NEWLINE).forEach(m -> message.getChannel().sendMessage(m).queue());
    }

}

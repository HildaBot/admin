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
import ch.jamiete.hilda.commands.ChannelCommand;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class AdminRolesCommand extends ChannelCommand {

    protected AdminRolesCommand(Hilda hilda) {
        super(hilda);

        this.setName("roles");
        this.setDescription("Lists a server's roles.");
    }

    @Override
    public void execute(Message message, String[] arguments, String label) {
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

        final EmbedBuilder eb = new EmbedBuilder();

        eb.setTitle("Roles of " + guild.getId(), null);

        for (final Role role : guild.getRoles()) {
            eb.addField(role.getName(), role.getId(), true);
        }

        this.reply(message, eb.build());
    }

}

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

import java.util.Arrays;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.commands.ChannelCommand;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class SetupCommand extends ChannelCommand {
    private AdminPlugin plugin;

    public SetupCommand(Hilda hilda, AdminPlugin plugin) {
        super(hilda);

        this.plugin = plugin;

        this.setName("setup");
        this.setAliases(Arrays.asList(new String[] { "init", "initialise", "initialize" }));
        this.setDescription("The initial setup for a non-configured server.");
        this.setMinimumPermission(Permission.ADMINISTRATOR);
        this.setHide(true);
    }

    @Override
    public void execute(Message message, String[] args, String label) {
        if (plugin.getRole() == null || plugin.getChannel() == null) {
            MessageBuilder mb = new MessageBuilder();

            mb.append("Channel: ").append(message.getChannel().getId()).append('\n');
            mb.append("Roles:");

            for (Role role : message.getGuild().getRoles()) {
                mb.append('\n');
                mb.append(role.getName()).append(' ').append(role.getId());
            }

            this.reply(message, mb.build());
        }
    }

}

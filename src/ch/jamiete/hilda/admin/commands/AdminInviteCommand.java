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

import java.util.Collections;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.admin.runnables.OwnerInviteTask;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

class AdminInviteCommand extends ChannelSubCommand {

    AdminInviteCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("invite");
        this.setAliases(Collections.singletonList("inv"));
        this.setDescription("Generates an invite link for a server.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length != 1) {
            this.usage(message, "invite <id>", label);
            return;
        }

        final Guild guild = this.hilda.getBot().getGuildById(arguments[0]);

        if (guild == null) {
            this.reply(message, "I'm not on that server.");
        } else {
            this.hilda.getExecutor().execute(new OwnerInviteTask(this.hilda, guild, message));
        }
    }

}

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
package ch.jamiete.hilda.admin.runnables;

import ch.jamiete.hilda.Hilda;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Invite;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

public class OwnerInviteTask implements Runnable {
    private final Hilda hilda;
    private final Guild guild;
    private final Message message;

    public OwnerInviteTask(final Hilda hilda, final Guild guild, final Message message) {
        this.hilda = hilda;
        this.guild = guild;
        this.message = message;
    }

    @Override
    public void run() {
        final Member self = this.guild.getMember(this.hilda.getBot().getSelfUser());

        if (self.hasPermission(this.guild.getDefaultChannel(), Permission.CREATE_INSTANT_INVITE)) {
            final Invite invite = this.guild.getDefaultChannel().createInvite().complete();
            this.message.getTextChannel().sendMessage("https://discord.gg/" + invite.getCode()).queue();
            return;
        }

        for (final TextChannel channel : this.guild.getTextChannels()) {
            if (self.hasPermission(channel, Permission.CREATE_INSTANT_INVITE)) {
                final Invite invite = channel.createInvite().complete();
                this.message.getTextChannel().sendMessage("https://discord.gg/" + invite.getCode()).queue();
                return;
            }
        }

        this.message.getTextChannel().sendMessage("Couldn't create an invite!").queue();
    }

}

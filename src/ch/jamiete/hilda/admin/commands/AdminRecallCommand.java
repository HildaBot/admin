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
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

class AdminRecallCommand extends ChannelSubCommand {

    public AdminRecallCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("recall");
        this.setDescription("Deletes a self-message.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (arguments.length == 0) {
            this.usage(message, "[channel_id] <message_id>", label);
            return;
        }

        TextChannel channel = message.getTextChannel();

        if (arguments.length == 2) {
            channel = message.getJDA().getTextChannelById(arguments[0]);
        }

        if (channel == null) {
            this.reply(message, "I couldn't find that channel.");
            return;
        }

        channel.retrieveMessageById(arguments.length == 2 ? arguments[1] : arguments[0]).queue(m -> {
            if (m.getAuthor() == this.hilda.getBot().getSelfUser()) {
                m.delete().queue();
                this.reply(message, "Deleted.");
            } else {
                this.reply(message, "I didn't send that message, so I won't delete it.");
            }
        }, t -> {
            this.reply(message, "I couldn't find that message.");
        });
    }

}

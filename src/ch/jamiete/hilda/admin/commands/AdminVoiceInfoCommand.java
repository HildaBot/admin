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
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;

class AdminVoiceInfoCommand extends ChannelSubCommand {

    AdminVoiceInfoCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("voiceinfo");
        this.setDescription("Lists information about the voice connection in the current guild.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        final Guild guild = message.getGuild();

        final EmbedBuilder audio = new EmbedBuilder();
        audio.setTitle("Voice connection information");
        audio.addField("Connected channel", String.valueOf(guild.getAudioManager().getConnectedChannel()), true);
        audio.addField("Connection status", String.valueOf(guild.getAudioManager().getConnectionStatus()), true);
        audio.addField("Queued channel", String.valueOf(guild.getAudioManager().getQueuedAudioConnection()), true);
        audio.addField("Receive handler", String.valueOf(guild.getAudioManager().getReceivingHandler()), true);
        audio.addField("Send handler", String.valueOf(guild.getAudioManager().getSendingHandler()), true);
        audio.addField("Attempting to connect", String.valueOf(guild.getAudioManager().isAttemptingToConnect()), true);
        audio.addField("Connected", String.valueOf(guild.getAudioManager().isConnected()), true);

        this.reply(message, audio.build());
    }

}

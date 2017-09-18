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

import java.util.concurrent.TimeUnit;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import net.dv8tion.jda.core.audio.hooks.ConnectionStatus;
import net.dv8tion.jda.core.entities.Message;

public class AdminVoiceLeaveCommand extends ChannelSubCommand {

    protected AdminVoiceLeaveCommand(final Hilda hilda, final ChannelSeniorCommand senior) {
        super(hilda, senior);

        this.setName("voiceleave");
        this.setDescription("Leaves the voice channel.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        final ConnectionStatus status = message.getGuild().getAudioManager().getConnectionStatus();

        message.getGuild().getAudioManager().closeAudioConnection();

        // Queue message and, once sent, in 2 seconds inform of close response.
        message.getChannel().sendMessage("OK, I closed the audio connection.").queue((m) -> {
            this.hilda.getExecutor().schedule(() -> {
                final StringBuilder sb = new StringBuilder();

                sb.append(m.getRawContent());
                sb.append(" The voice connection is now reported as ");
                sb.append(m.getGuild().getAudioManager().isConnected() ? "connected" : "closed");
                sb.append(". ");

                sb.append("Initially, the connection status was reported as __").append(status.toString()).append("__, ");
                sb.append("but it is currently reported as __").append(m.getGuild().getAudioManager().getConnectionStatus().toString());
                sb.append("__.");

                m.editMessage(sb.toString()).queue();
            }, 2, TimeUnit.SECONDS);
        });
    }

}

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
import net.dv8tion.jda.core.MessageBuilder.SplitPolicy;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;

public class AdminServersCommand extends ChannelCommand {
    private final AdminPlugin plugin;

    public AdminServersCommand(final Hilda hilda, final AdminPlugin plugin) {
        super(hilda);

        this.plugin = plugin;

        this.setName("servers");
        this.setAliases(Arrays.asList(new String[] { "server", "serverlist", "listservers" }));
        this.setDescription("Lists the servers Hilda is on.");
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        final MessageBuilder mb = new MessageBuilder();

        mb.append("I'm on " + this.hilda.getBot().getGuilds().size() + " servers:\n");

        for (final Guild guild : this.hilda.getBot().getGuilds()) {
            mb.append("\n");
            mb.append(this.plugin.getServerInfo(guild));
        }

        mb.buildAll(SplitPolicy.NEWLINE).forEach(m -> this.reply(message, m));
    }

}

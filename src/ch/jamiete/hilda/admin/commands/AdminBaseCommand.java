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
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.CommandTranscendLevel;
import net.dv8tion.jda.core.entities.Message;

public class AdminBaseCommand extends ChannelSeniorCommand {
    private final AdminPlugin plugin;

    public AdminBaseCommand(final Hilda hilda, final AdminPlugin plugin) {
        super(hilda);

        this.plugin = plugin;

        this.setName("admin");
        this.setHide(true);
        this.setDescription("Allows manipulation of servers.");
        this.setTranscend(CommandTranscendLevel.ALL);

        this.registerSubcommand(new AdminBroadcastCommand(hilda, this));
        this.registerSubcommand(new AdminConfigCommand(hilda, this, plugin));
        this.registerSubcommand(new AdminInfoCommand(hilda, this));
        this.registerSubcommand(new AdminInviteCommand(hilda, this));
        this.registerSubcommand(new AdminLeaveCommand(hilda, this));
        this.registerSubcommand(new AdminLoudCommand(hilda, this, plugin));
        this.registerSubcommand(new AdminMusicCommand(hilda, this, plugin));
        this.registerSubcommand(new AdminRolesCommand(hilda, this));
        this.registerSubcommand(new AdminServersCommand(hilda, this, plugin));
        this.registerSubcommand(new AdminStreamCommand(hilda, this, plugin));
    }

    @Override
    public void execute(final Message message, final String[] arguments, final String label) {
        if (!this.plugin.canRun(message)) {
            return;
        }

        super.execute(message, arguments, label);
    }

}

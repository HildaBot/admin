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

public class AdminBaseCommand extends ChannelSeniorCommand {

    public AdminBaseCommand(final Hilda hilda, final AdminPlugin plugin) {
        super(hilda);

        this.setName("admin");
        this.setHide(true);
        this.setDescription("Allows manipulation of servers.");

        this.registerSubcommand(new AdminBroadcastCommand(hilda, plugin));
        this.registerSubcommand(new AdminInviteCommand(hilda, plugin));
        this.registerSubcommand(new AdminLeaveCommand(hilda, plugin));
        this.registerSubcommand(new AdminRolesCommand(hilda, plugin));
        this.registerSubcommand(new AdminServersCommand(hilda, plugin));
        this.registerSubcommand(new AdminStreamCommand(hilda, plugin));
    }

}

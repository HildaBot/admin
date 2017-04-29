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
package ch.jamiete.hilda.admin;

import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Start;
import ch.jamiete.hilda.admin.commands.AdminBaseCommand;
import ch.jamiete.hilda.music.MusicManager;
import ch.jamiete.hilda.music.MusicPlugin;
import ch.jamiete.hilda.plugins.HildaPlugin;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;

public class AdminPlugin extends HildaPlugin {
    private Role role;

    public AdminPlugin(Hilda hilda) {
        super(hilda);
    }

    @Override
    public void onEnable() {
        this.getHilda().getCommandManager().registerChannelCommand(new AdminBaseCommand(this.getHilda(), this));
        this.role = this.getHilda().getBot().getRoleById("283921566281236480");
    }

    public MusicManager getMusicManager() {
        for (HildaPlugin plugin : this.getHilda().getPluginManager().getPlugins()) {
            if (plugin instanceof MusicPlugin) {
                MusicPlugin music = (MusicPlugin) plugin;
                return music.getMusicManager();
            }
        }

        return null;
    }

    public boolean canRun(Message message) {
        if (Start.DEBUG) {
            return true;
        }

        if (role == null) {
            return false;
        }

        if (role.getGuild().getMember(message.getAuthor()).getRoles().contains(role)) {
            return true;
        }

        return false;
    }

}

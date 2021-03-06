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

import java.util.List;
import java.util.stream.Collectors;
import ch.jamiete.hilda.Hilda;
import ch.jamiete.hilda.Util;
import ch.jamiete.hilda.admin.AdminPlugin;
import ch.jamiete.hilda.commands.ChannelSeniorCommand;
import ch.jamiete.hilda.commands.ChannelSubCommand;
import ch.jamiete.hilda.music.MusicManager;
import ch.jamiete.hilda.music.MusicServer;
import ch.jamiete.hilda.music.QueueItem;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;

class AdminMusicCommand extends ChannelSubCommand {

    private final AdminPlugin plugin;

    AdminMusicCommand(final Hilda hilda, final ChannelSeniorCommand senior, final AdminPlugin plugin) {
        super(hilda, senior);

        this.plugin = plugin;

        this.setName("music");
        this.setDescription("Reveals information about the music player.");
    }

    @Override
    public void execute(final Message message, final String[] args, final String label) {
        final List<MusicServer> servers = this.plugin.getMusicManager().getServers();
        long longest = 0;

        this.reply(message, "I'm currently playing music on " + servers.size() + " " + (servers.size() == 1 ? "server" : "servers"));

        for (final MusicServer server : servers) {
            final long duration = server.getDuration();

            if (duration > longest) {
                longest = duration;
            }

            // Message construction
            final EmbedBuilder eb = new EmbedBuilder();

            eb.setTitle(server.getGuild().getName(), null);

            if (server.getPlayer().getPlayingTrack() == null) {
                eb.addField("Now playing", "Nothing!", false);
            } else {
                eb.addField("Now playing", MusicManager.getFriendly(server.getPlayer().getPlayingTrack()), false);
            }

            final List<QueueItem> queue = server.getQueue();

            if (queue.size() == 0) {
                eb.addField("Queue", "Nothing!", false);
            } else {
                final StringBuilder sb = new StringBuilder();
                final int max = 25;
                final boolean larger = queue.size() > max;
                final int check = larger ? max : queue.size();

                for (int i = 0; i < check; i++) {
                    sb.append(MusicManager.getFriendly(queue.get(i).getTrack())).append(", ");
                }

                sb.setLength(sb.length() - 2);

                if (larger) {
                    final int remaining = queue.size() - max;
                    sb.append(" and ").append(remaining).append(remaining == 1 ? "song" : "songs");
                }

                eb.addField("Queue", sb.toString(), false);
            }

            long rem = 0;

            if (server.getPlayer().getPlayingTrack() != null) {
                rem += server.getPlayer().getPlayingTrack().getDuration() - server.getPlayer().getPlayingTrack().getPosition();
            }

            for (final QueueItem item : queue) {
                rem += item.getTrack().getDuration();
            }

            eb.addField("Estimated time until completion", Util.getFriendlyTime(rem), false);

            final List<Member> audience = server.getChannel().getMembers().stream().filter(m -> !m.getUser().isBot()).collect(Collectors.toList());
            final StringBuilder sb = new StringBuilder();
            for (final Member member : audience) {
                sb.append(Util.getName(member)).append(", ");
            }
            sb.setLength(sb.length() - 2);

            eb.addField("Audience (" + audience.size() + ")", sb.toString(), false);

            this.reply(message, eb.build());
        }

        if (servers.size() > 1) {
            this.reply(message, "**The longest queue will end in approximately " + Util.getFriendlyTime(longest) + "!**");
        }
    }

}

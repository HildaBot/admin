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
package ch.jamiete.hilda.admin.log;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import ch.jamiete.hilda.LogFormat;
import net.dv8tion.jda.api.entities.TextChannel;

public class LogReporter extends Handler {
    private static final List<Level> LEVELS = Collections.unmodifiableList(Arrays.asList(Level.WARNING, Level.SEVERE));
    private final LogFormat formatter = new LogFormat();
    private final TextChannel channel;
    private boolean loud = false;

    public LogReporter(final TextChannel channel) {
        this.channel = channel;
    }

    @Override
    public void close() throws SecurityException {
    }

    @Override
    public void flush() {
    }

    public boolean getLoud() {
        return this.loud;
    }

    @Override
    public void publish(final LogRecord record) {
        if (!LogReporter.LEVELS.contains(record.getLevel()) && !this.loud || this.loud && !(LogReporter.LEVELS.contains(record.getLevel()) || record.getLevel() == Level.INFO) || this.channel == null) {
            return;
        }

        final StringBuilder sb = new StringBuilder(2000);

        sb.append("** Caught a");

        if ("aeiou".indexOf(record.getLevel().getName().toLowerCase().charAt(0)) != -1) {
            sb.append("n");
        }

        sb.append(" ").append(record.getLevel().getName()).append(" record:**\n");
        sb.append("`");

        for (String line : this.formatter.format(record).split("\n")) {
            line = line + "\n";

            if (sb.length() + line.length() + 1 > 2000) {
                sb.append("`");
                this.channel.sendMessage(sb.toString()).queue();

                sb.setLength(0);
                sb.append("`");

                if (line.startsWith(" ")) {
                    sb.append(line.replaceFirst(" ", "."));
                } else {
                    sb.append(line);
                }
            } else {
                sb.append(line);
            }
        }

        if (sb.length() > 0) {
            sb.append("`");
            this.channel.sendMessage(sb.toString()).queue();
        }
    }

    public void setLoud(final boolean loud) {
        this.loud = loud;
    }

}

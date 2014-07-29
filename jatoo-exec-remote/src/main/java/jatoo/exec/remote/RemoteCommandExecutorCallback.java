/*
 * Copyright (C) 2014 Cristian Sulea ( http://cristian.sulea.net )
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package jatoo.exec.remote;

import com.jcraft.jsch.Channel;

/**
 * Sometimes we want to do some things after the execution started (like a force
 * stop for example). This callback allows us to acquire such a behavior.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.1, July 24, 2014
 */
public final class RemoteCommandExecutorCallback {

  private Channel channel;

  void setChannel(final Channel channel) {
    this.channel = channel;
  }

  public void disconnectChannel() {
    if (channel != null) {
      channel.disconnect();
    }
  }

}

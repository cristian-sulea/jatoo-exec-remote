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

import jatoo.exec.InputStreamExhauster;
import jatoo.exec.InputStreamExhausterWithDumpStream;

import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * Handy class to ease the remote execution of commands.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.3, July 24, 2014
 */
public final class RemoteCommandExecutor {

  /** The logger. */
  private final Log logger = LogFactory.getLog(RemoteCommandExecutor.class);

  /** The session (JCraft Session). */
  private Session session;

  /**
   * Connects this executor to the remote system.
   * 
   * @param host
   *          the host name, or address, of the remote system
   * @param port
   *          the port number of the remote system
   * @param username
   *          the user name
   * @param password
   *          the user's password
   * 
   * @throws JSchException
   *           if a JCraft error occurs
   */
  public void connect(final String host, final int port, final String username, final String password) throws JSchException {

    if (isConnected()) {
      disconnect();
    }

    JSch jsch = new JSch();

    session = jsch.getSession(username, host, port);
    session.setUserInfo(new RemoteCommandExecutorJCraftUserInfo(password));
    session.connect();
  }

  /**
   * Checks if this executor is connected to the remote system.
   * 
   * @return <code>true</code> if executor is connected, <code>false</code>
   *         otherwise
   */
  public boolean isConnected() {
    return session != null && session.isConnected();
  }

  /**
   * Disconnects this executor to the remote system.
   */
  public void disconnect() {

    if (session != null) {
      session.disconnect();
      session = null;
    } else {
      logger.info("session == null (probably never connected)");
    }
  }

  /**
   * Handy method for {@link #exec(String, OutputStream, boolean)} with no dump
   * output stream.
   * 
   * @param command
   *          the command to be executed
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination | take care that
   *         {@link JSch} is also returning <code>-1</code>)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws JSchException
   *           if a JCraft error occurs
   */
  public int exec(final String command) throws IOException, JSchException {
    return exec(command, null, false);
  }

  /**
   * Handy method for {@link #exec(String, OutputStream, boolean)} with
   * specified dump output stream (but no closing).
   * 
   * @param command
   *          the command to be executed
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination | take care that
   *         {@link JSch} is also returning <code>-1</code>)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws JSchException
   *           if a JCraft error occurs
   */
  public int exec(final String command, final OutputStream dumpOutputStream) throws IOException, JSchException {
    return exec(command, dumpOutputStream, false);
  }

  /**
   * Executes the specified command.
   * 
   * @param command
   *          the command to be executed
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * @param closeDumpOutputStream
   *          <code>true</code> if the dump stream should be closed when the
   *          execution ends, <code>false</code> otherwise
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination | take care that
   *         {@link JSch} is also returning <code>-1</code>)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws JSchException
   *           if a JCraft error occurs
   */
  public int exec(final String command, final OutputStream dumpOutputStream, final boolean closeDumpOutputStream) throws IOException, JSchException {
    return exec(command, dumpOutputStream, closeDumpOutputStream, null);
  }

  /**
   * Executes the specified command.
   * 
   * @param command
   *          the command to be executed
   * @param dumpOutputStream
   *          the stream where the process will dump (exhaust) his contents
   * @param closeDumpOutputStream
   *          <code>true</code> if the dump stream should be closed when the
   *          execution ends, <code>false</code> otherwise
   * @param callback
   *          the callback used to do things after the execution started
   * 
   * @return the exit value of the process (by convention, the value
   *         <code>0</code> indicates normal termination | take care that
   *         {@link JSch} is also returning <code>-1</code>)
   * 
   * @throws IOException
   *           if an I/O error occurs
   * @throws JSchException
   *           if a JCraft error occurs
   */
  public int exec(final String command, final OutputStream dumpOutputStream, final boolean closeDumpOutputStream, final RemoteCommandExecutorCallback callback) throws IOException, JSchException {

    ChannelExec channel = (ChannelExec) session.openChannel("exec");
    channel.setCommand(command);

    if (callback != null) {
      callback.setChannel(channel);
    }

    channel.setInputStream(null);
    channel.setErrStream(dumpOutputStream, true);

    channel.connect();

    if (dumpOutputStream != null) {
      new InputStreamExhausterWithDumpStream(channel.getInputStream(), dumpOutputStream, closeDumpOutputStream).exhaust();
    } else {
      new InputStreamExhauster(channel.getInputStream()).exhaust();
    }

    channel.disconnect();

    return channel.getExitStatus();
  }

}

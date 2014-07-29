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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link RemoteCommandExecutor}.
 * 
 * @author <a href="http://cristian.sulea.net" rel="author">Cristian Sulea</a>
 * @version 1.1, July 24, 2014
 */
public class RemoteCommandExecutorTest {

  private final RemoteCommandExecutor executor = new RemoteCommandExecutor();

  private boolean connected = true;

  @Before
  public void initialize() throws Exception {

    try {
      executor.connect("rdev00", 22, "dev8081", "dev8081");
    } catch (Exception e) {
      connected = false;
    }

    if (!connected) {
      return;
    }

    Assert.assertTrue(executor.isConnected());

    executor.exec("rmdir RemoteCommandExecutorTestFolder");
  }

  @After
  public void cleanup() throws Exception {

    if (!connected) {
      return;
    }

    executor.exec("rmdir RemoteCommandExecutorTestFolder");

    Assert.assertTrue(executor.isConnected());
    executor.disconnect();
    Assert.assertFalse(executor.isConnected());
  }

  @Test
  public void test() throws Exception {

    if (!connected) {
      return;
    }

    Assert.assertTrue(0 == executor.exec("mkdir RemoteCommandExecutorTestFolder"));
    Assert.assertTrue(1 == executor.exec("mkdir RemoteCommandExecutorTestFolder"));

    Assert.assertTrue(0 == executor.exec("rmdir RemoteCommandExecutorTestFolder"));
    Assert.assertTrue(1 == executor.exec("rmdir RemoteCommandExecutorTestFolder"));
  }

  @Test
  public void test2() throws Exception {

    if (!connected) {
      return;
    }

    executor.exec("rmdir test", System.out);
    executor.exec("rm test/**", System.out);
    executor.exec("rmdir test", System.out);
    executor.exec("mkdir test", System.out);
    executor.exec("mkdir test", System.out);
    executor.exec("ls -1 test | wc -l", System.out);
    executor.exec("ls -l / > test/list.txt", System.out);
    executor.exec("ls -1 test | wc -l", System.out);
    executor.exec("ls -l test", System.out);
    executor.exec("cat list.txt", System.out);
  }

}

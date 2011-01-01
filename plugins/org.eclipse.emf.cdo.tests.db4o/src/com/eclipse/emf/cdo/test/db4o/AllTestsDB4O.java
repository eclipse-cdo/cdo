/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package com.eclipse.emf.cdo.test.db4o;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.db4o.DB4OStore;
import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_261218_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import java.io.File;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Victor Roldan Betancort
 */
public class AllTestsDB4O extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsDB4O().getTestSuite("CDO Tests (DB4O)");
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // Takes too much time
    testClasses.remove(Bugzilla_261218_Test.class);
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, DB4ORepositoryConfig.INSTANCE, JVM, NATIVE);
  }

  /**
   * @author Victor Roldan Betancort
   */
  public static class DB4ORepositoryConfig extends RepositoryConfig
  {
    public static final DB4ORepositoryConfig INSTANCE = new DB4ORepositoryConfig("DB4O");

    private static final long serialVersionUID = 1L;

    private transient String dataFilePath;

    private transient File dataFile;

    public DB4ORepositoryConfig(String name)
    {
      super(name);
    }

    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(IRepository.Props.SUPPORTING_AUDITS, "false");
      props.put(IRepository.Props.SUPPORTING_BRANCHES, "false");
    }

    @Override
    public void tearDown() throws Exception
    {
      super.tearDown();
    }

    @Override
    protected IStore createStore(String repoName)
    {
      try
      {
        dataFile = File.createTempFile(repoName, ".db4o");
        dataFile.deleteOnExit();
        dataFilePath = dataFile.getAbsolutePath();

        int port = 0;
        do
        {
          port = (int)Math.round(Math.random() * 60000 + 1024);
        } while (!available(port));

        IStore store = new DB4OStore(dataFilePath, port);
        return store;
      }
      catch (Exception e)
      {
        e.printStackTrace();
      }

      return null;
    }
  }

  public static boolean available(int port)
  {
    if (port < 1024 || port > 65536)
    {
      throw new IllegalArgumentException("Invalid start port: " + port);
    }

    ServerSocket ss = null;
    DatagramSocket ds = null;
    try
    {
      ss = new ServerSocket(port);
      ss.setReuseAddress(true);
      ds = new DatagramSocket(port);
      ds.setReuseAddress(true);
      return true;
    }
    catch (IOException e)
    {
    }
    finally
    {
      if (ds != null)
      {
        ds.close();
      }

      if (ss != null)
      {
        try
        {
          ss.close();
        }
        catch (IOException e)
        {
          /* should not be thrown */
        }
      }
    }

    return false;
  }

}

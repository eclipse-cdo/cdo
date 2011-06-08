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
package org.eclipse.emf.cdo.tests.db4o;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.db4o.DB4OStore;
import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.BranchingSameSessionTest;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.BranchingWithCacheClearTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_261218_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_324585_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.util.io.TMPUtil;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

    // Added here testcases to skip
    // takes too much
    testClasses.remove(Bugzilla_261218_Test.class);
    testClasses.remove(Bugzilla_324585_Test.class);

    // db4o doesn't support branching
    testClasses.remove(BranchingTest.class);
    testClasses.remove(BranchingSameSessionTest.class);
    testClasses.remove(BranchingWithCacheClearTest.class);
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
    private static final Random RANDOM = new Random(System.currentTimeMillis());

    public static final DB4ORepositoryConfig INSTANCE = new DB4ORepositoryConfig("DB4O");

    private static final long serialVersionUID = 1L;

    private transient boolean optimizing = true;

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
    protected IStore createStore(String repoName)
    {
      File tempFolder = TMPUtil.getTempFolder();
      File file = new File(tempFolder, "cdodb_" + repoName + ".db4o");
      if (file.exists() && !isRestarting())
      {
        file.delete();
      }

      int port = 0;
      boolean ok = false;
      do
      {
        ServerSocket sock = null;
        try
        {
          port = 1024 + RANDOM.nextInt(65536 - 1024);
          sock = new ServerSocket(port);
          ok = true;
        }
        catch (IOException e)
        {
        }
        finally
        {
          try
          {
            if (sock != null)
            {
              sock.close();
            }
          }
          catch (IOException e)
          {
          }
        }
      } while (!ok);

      IStore store = new DB4OStore(file.getPath(), port);
      return store;
    }

    @Override
    protected boolean isOptimizing()
    {
      // Do NOT replace this with a hardcoded value!
      return optimizing;
    }
  }

  public static class MemDB4ORepositoryConfig extends RepositoryConfig
  {
    public static final MemDB4ORepositoryConfig INSTANCE = new MemDB4ORepositoryConfig("DB4O");

    private static final long serialVersionUID = 1L;

    private transient boolean optimizing = false;

    public MemDB4ORepositoryConfig(String name)
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
    protected IStore createStore(String repoName)
    {
      if (!isRestarting())
      {
        MEMDB4OStore.clearContainer();
      }
      return new MEMDB4OStore();
    }

    @Override
    protected boolean isOptimizing()
    {
      // Do NOT replace this with a hardcoded value!
      return optimizing;
    }
  }
}

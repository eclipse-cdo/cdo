/*
 * Copyright (c) 2010-2013, 2015, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.internal.server.Repository;
import org.eclipse.emf.cdo.internal.server.TransactionCommitContext;
import org.eclipse.emf.cdo.net4j.CDONet4jSession;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.spi.server.InternalCommitContext;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalTransaction;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.emf.cdo.tests.model3.NodeA;
import org.eclipse.emf.cdo.tests.model3.NodeB;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.WrappedException;

import org.eclipse.emf.ecore.resource.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Bug 316444.
 *
 * @author Martin Fluegge
 * @since 4.0
 */
public class Bugzilla_316444_Test extends AbstractCDOTest
{
  private static final String REPOSITORY_NAME = "repo1";

  private static final String RESOURCE_PATH = "/my/resource";

  public int idSessionA;

  public int idSessionB;

  private int idInitSession;

  private boolean finishedSessionA = false;

  private List<Exception> exceptions;

  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> map = super.getTestProperties();
    map.put(IRepository.Props.ENSURE_REFERENTIAL_INTEGRITY, "true");
    return map;
  }

  @Override
  protected void doSetUp() throws Exception
  {
    exceptions = new ArrayList<>();
    createRepository();
    super.doSetUp();
  }

  private void createRepository()
  {
    Repository repository = new Repository.Default()
    {
      private Object monitor = new Object();

      private CountDownLatch latch = new CountDownLatch(2);

      @Override
      public InternalCommitContext createCommitContext(InternalTransaction transaction)
      {
        return new TransactionCommitContext(transaction)
        {
          @Override
          protected void lockObjects() throws InterruptedException
          {
            int sessionID = getTransaction().getSession().getSessionID();
            if (sessionID == idSessionB)
            {
              synchronized (monitor)
              {
                // Only wait if Session A has not passed the lockObjects
                if (!finishedSessionA)
                {
                  msg("Session B is waiting for Session A");
                  monitor.wait(DEFAULT_TIMEOUT);
                  msg("Session B stopped waiting");
                }
                else
                {
                  msg("Session B - no need to wait. A has already passed lockObjects()");
                }
              }
            }

            msg("Passing lockObjects() " + getTransaction().getSession());

            try
            {
              super.lockObjects();
            }
            catch (RuntimeException ex)
            {
              latch.countDown();
              throw ex;
            }
            catch (Exception ex)
            {
              latch.countDown();
              throw WrappedException.wrap(ex);
            }

            msg("Passed lockObjects() " + getTransaction().getSession());

            if (sessionID == idSessionA)
            {
              synchronized (monitor)
              {
                finishedSessionA = true;
                monitor.notifyAll();
                msg("Session A notified others not to wait anymore.");
              }
            }

            // Do nothing for inital session. Otherwise the test will block too early
            if (sessionID != idInitSession)
            {
              latch.countDown();

              try
              {
                assertEquals(true, latch.await(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS));
              }
              catch (InterruptedException ex)
              {
                throw WrappedException.wrap(ex);
              }
            }
          }
        };
      }
    };

    Map<String, String> props = getRepositoryProperties();
    ((InternalRepository)repository).setProperties(props);

    repository.setName(REPOSITORY_NAME);

    Map<String, Object> map = getTestProperties();
    map.put(RepositoryConfig.PROP_TEST_REPOSITORY, repository);
  }

  /**
   * Not needed anymore because of {@link Bugzilla_409284_Test}.
   */
  public void _testMovingSubtree() throws Exception
  {
    exceptions.clear();

    {
      CDONet4jSession session = (CDONet4jSession)openSession(REPOSITORY_NAME);
      idInitSession = session.getSessionID();
      CDOTransaction transaction = session.openTransaction();

      Resource resource = transaction.createResource(getResourcePath(RESOURCE_PATH));

      // -------------- create graph begin ------------------------
      NodeB root = createSimpleNode("root");

      NodeB A = createSimpleNode("A");
      NodeB B = createSimpleNode("B");
      NodeB C = createSimpleNode("C");
      NodeB D = createSimpleNode("D");
      NodeB E = createSimpleNode("E");

      root.getChildren().add(A);
      root.getChildren().add(D);

      A.getChildren().add(B);
      B.getChildren().add(C);

      D.getChildren().add(E);

      resource.getContents().add(root);
      transaction.commit();

      // -------- check for consistency -----------

      checkInitialGraph(root, A, B, C, D, E);

      transaction.close();
      session.close();
    }

    // if (!(isConfig(MEM) || isConfig(MEM_AUDITS) || isConfig(MEM_BRANCHES) || isConfig(MEM_OFFLINE)))
    // {
    // // do not restart the repository on MEM store
    // restartRepository();
    // }

    {
      // Just an additional check to make sure that the graph is stored correctly even after repository restart
      CDONet4jSession session = (CDONet4jSession)openSession(REPOSITORY_NAME);
      idInitSession = session.getSessionID();
      CDOTransaction transaction = session.openTransaction();
      Resource resource = transaction.getResource(getResourcePath(RESOURCE_PATH), true);

      NodeB root = (NodeB)resource.getContents().get(0);
      assertEquals("root", root.getName());

      NodeB A = getElementFromGraphNodeB(root, "A");
      NodeB B = getElementFromGraphNodeB(root, "B");
      NodeB C = getElementFromGraphNodeB(root, "C");
      NodeB D = getElementFromGraphNodeB(root, "D");
      NodeB E = getElementFromGraphNodeB(root, "E");

      assertNotNull(A);
      assertNotNull(B);
      assertNotNull(C);
      assertNotNull(D);
      assertNotNull(E);

      checkInitialGraph(root, A, B, C, D, E);
    }

    {
      CDONet4jSession session = (CDONet4jSession)openSession(REPOSITORY_NAME);
      idInitSession = session.getSessionID();

      // ----- start threads -----
      ThreadA threadA = new ThreadA(RESOURCE_PATH);
      ThreadB threadB = new ThreadB(RESOURCE_PATH);

      threadA.start();
      sleepIfNeeded();
      threadB.start();

      await(threadA.done);
      await(threadB.done);
      // threadA.join(DEFAULT_TIMEOUT);
      // threadB.join(DEFAULT_TIMEOUT);

      if (exceptions.size() > 0)
      {
        Exception exception = exceptions.get(0);
        if (exception instanceof ThreadBShouldHaveThrownAnExceptionException)
        {
          fail(exception.getMessage());
        }
        else
        {
          throw exception;
        }
      }

      session.close();
      msg("finished");
    }
  }

  public void testLockParentWithEAttributeChange() throws Exception
  {
    String resourcePath = RESOURCE_PATH + "1";
    {
      CDONet4jSession session = (CDONet4jSession)openSession(REPOSITORY_NAME);
      idInitSession = session.getSessionID();
      CDOTransaction transaction = session.openTransaction();

      Resource resource = transaction.createResource(getResourcePath(resourcePath));

      // -------------- create graph begin ------------------------
      NodeB root = createSimpleNode("root");

      NodeB A = createSimpleNode("A");
      NodeB B = createSimpleNode("B");
      NodeB C = createSimpleNode("C");
      NodeB D = createSimpleNode("D");
      NodeB E = createSimpleNode("E");

      root.getChildren().add(A);
      root.getChildren().add(D);

      A.getChildren().add(B);
      B.getChildren().add(C);

      D.getChildren().add(E);

      resource.getContents().add(root);
      transaction.commit();

      // -------- check for consistency -----------

      checkInitialGraph(root, A, B, C, D, E);

      transaction.close();
      session.close();
    }

    // restartRepository();

    {
      CDONet4jSession session = (CDONet4jSession)openSession(REPOSITORY_NAME);
      idInitSession = session.getSessionID();

      // ----- start threads -----
      ThreadX threadX = new ThreadX(resourcePath);
      ThreadA threadA = new ThreadA(resourcePath);

      threadA.start();
      sleepIfNeeded();
      threadX.start();

      await(threadA.done);
      await(threadX.done);
      // threadX.join(DEFAULT_TIMEOUT);
      // threadA.join(DEFAULT_TIMEOUT);

      if (exceptions.size() > 0)
      {
        throw exceptions.get(0);
      }
    }
  }

  private void sleepIfNeeded()
  {
    if (isConfig(LEGACY))
    {
      // sleep in legacy while Bug 318816 is not solved
      sleep(1000);
    }
  }

  private void checkInitialGraph(NodeB root, NodeB A, NodeB B, NodeB C, NodeB D, NodeB E)
  {
    assertEquals("A", A.getName());
    assertEquals("B", B.getName());
    assertEquals("C", C.getName());
    assertEquals("D", D.getName());
    assertEquals("E", E.getName());

    assertEquals(root, A.getParent());
    assertEquals(root, A.eContainer());

    assertEquals(root, D.getParent());
    assertEquals(root, D.eContainer());

    assertEquals(A, B.getParent());
    assertEquals(A, B.eContainer());

    assertEquals(B, C.getParent());
    assertEquals(B, C.eContainer());

    assertEquals(D, E.getParent());
    assertEquals(D, E.eContainer());
  }

  private abstract class AbstactTestThread extends Thread
  {
    public CountDownLatch done = new CountDownLatch(1);

    protected final String resourcePath;

    public AbstactTestThread(String resourcePath)
    {
      this.resourcePath = resourcePath;
    }

    @Override
    public final void run()
    {
      try
      {
        doRun();
      }
      finally
      {
        done.countDown();
      }
    }

    protected abstract void doRun();
  }

  /**
   * @author Martin Fluegge
   */
  private class ThreadA extends AbstactTestThread
  {
    private CDONet4jSession session;

    public ThreadA(String resourcePath)
    {
      super(resourcePath);
      setName("ThreadA");

      msg("Starting Thread A");
      session = (CDONet4jSession)openSession(REPOSITORY_NAME);
      idSessionA = session.getSessionID();
    }

    @Override
    protected void doRun()
    {
      try
      {
        msg("Started Thread A " + session);
        CDOTransaction transaction = session.openTransaction();
        Resource resource = transaction.getResource(getResourcePath(resourcePath), true);

        NodeB root = (NodeB)resource.getContents().get(0);
        assertEquals("root", root.getName());

        NodeB B = getElementFromGraphNodeB(root, "B");
        NodeB E = getElementFromGraphNodeB(root, "E");

        assertEquals("B", B.getName());
        assertEquals("E", E.getName());

        E.getChildren().add(B);

        try
        {
          transaction.commit();
        }
        catch (CommitException ex)
        {
          exceptions.add(ex);
        }

        session.close();
        msg("Finished Thread A " + session);
      }
      catch (Exception e)
      {
        exceptions.add(e);
      }
    }
  }

  /**
   * @author Martin Fluegge
   */
  private class ThreadB extends AbstactTestThread
  {
    private CDONet4jSession session;

    public ThreadB(String resourcePath)
    {
      super(resourcePath);
      setName("ThreadB");

      msg("Starting Thread B");
      session = (CDONet4jSession)openSession(REPOSITORY_NAME);
      idSessionB = session.getSessionID();
    }

    @Override
    protected void doRun()
    {
      try
      {
        msg("Started Thread B " + session);
        CDOTransaction transaction = session.openTransaction();

        Resource resource = transaction.getResource(getResourcePath(resourcePath), true);

        NodeB root = (NodeB)resource.getContents().get(0);
        assertEquals("root", root.getName());
        NodeB C = getElementFromGraphNodeB(root, "C");
        NodeB D = getElementFromGraphNodeB(root, "D");

        assertEquals("C", C.getName());
        assertEquals("D", D.getName());

        C.getChildren().add(D);

        try
        {
          transaction.commit();
        }
        catch (CommitException ex)
        {
          try
          {
            String message = ex.getMessage();
            if (message == null || !message.contains("ContainmentCycleDetectedException"))
            {
              throw ex;
            }

            msg("Finished (Passed) Thread B " + session);
            return;
          }
          catch (Exception exx)
          {
            throw new RuntimeException(exx);
          }
        }

        exceptions.add(new ThreadBShouldHaveThrownAnExceptionException("Thread B should have thrown an exception"));
        session.close();
      }
      catch (Exception e)
      {
        exceptions.add(e);
        msg("Finished Thread B " + session);
      }
    }
  }

  /**
   * @author Martin Fluegge
   */
  private class ThreadX extends AbstactTestThread
  {
    private CDONet4jSession session;

    public ThreadX(String resourcePath)
    {
      super(resourcePath);
      setName("ThreadX");

      msg("Starting Thread X");
      session = (CDONet4jSession)openSession(REPOSITORY_NAME);
      idSessionB = session.getSessionID();
    }

    @Override
    protected void doRun()
    {
      try
      {
        msg("Started Thread X" + session);
        CDOTransaction transaction = session.openTransaction();
        Resource resource = transaction.getResource(getResourcePath(resourcePath), true);

        NodeB root = (NodeB)resource.getContents().get(0);
        assertEquals("root", root.getName());

        NodeB D = getElementFromGraphNodeB(root, "D");

        assertEquals("D", D.getName());
        D.setName("DD");

        try
        {
          transaction.commit();
        }
        catch (CommitException ex)
        {
          exceptions.add(ex);
        }

        session.close();
        msg("Finished Thread X");
      }
      catch (Exception e)
      {
        exceptions.add(e);
      }
    }
  }

  private NodeB createSimpleNode(String name)
  {
    NodeB y = getModel3Factory().createNodeB();
    y.setName(name);
    return y;
  }

  private NodeB getElementFromGraphNodeB(final NodeB node, final String name) throws Exception
  {
    if (node.getName().equals(name))
    {
      return node;
    }

    CDOView view = CDOUtil.getView(node);
    return view.syncExec(new Callable<NodeB>()
    {
      @Override
      public NodeB call() throws Exception
      {
        for (NodeB child : node.getChildren())
        {
          NodeB elementFromGraph = getElementFromGraphNodeB(child, name);
          if (elementFromGraph != null)
          {
            return elementFromGraph;
          }
        }

        return null;
      }
    });
  }

  @SuppressWarnings("unused")
  private NodeA getElementFromGraphNodeA(final NodeA node, final String name) throws Exception
  {
    if (node.getName().equals(name))
    {
      return node;
    }

    CDOView view = CDOUtil.getView(node);
    return view.syncExec(new Callable<NodeA>()
    {
      @Override
      public NodeA call() throws Exception
      {
        for (NodeA child : node.getChildren())
        {
          NodeA elementFromGraph = getElementFromGraphNodeA(child, name);
          if (elementFromGraph != null)
          {
            return elementFromGraph;
          }
        }

        return null;
      }
    });
  }

  /*
   * @SuppressWarnings("unused") private void restartRepository() { LifecycleUtil.deactivate(getRepository());
   * createRepository(); getRepository(); }
   */

  /**
   * @author Martin Fluegge
   */
  private static class ThreadBShouldHaveThrownAnExceptionException extends Exception
  {
    private static final long serialVersionUID = 1L;

    public ThreadBShouldHaveThrownAnExceptionException(String s)
    {
      super(s);
    }
  }
}

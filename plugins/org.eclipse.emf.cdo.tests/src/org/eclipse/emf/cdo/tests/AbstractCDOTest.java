/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOSessionConfiguration;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.tests.AbstractTransportTest;
import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * @author Eike Stepper
 */
public abstract class AbstractCDOTest extends AbstractTransportTest
{
  public static final String REPOSITORY_NAME = "repo1";

  @Override
  protected IManagedContainer createContainer()
  {
    IManagedContainer container = super.createContainer();
    CDOUtil.prepareContainer(container, false);
    CDOServerUtil.prepareContainer(container);
    CDOServerUtil.addRepository(container, createRepository());
    return container;
  }

  protected IStore createStore()
  {
    return StoreRepositoryProvider.getInstance().createStore();
  }

  protected IRepository createRepository()
  {
    return StoreRepositoryProvider.getInstance().createRepository(REPOSITORY_NAME, getTestProperties());
  }

  // allows a testcase to pass specific properties
  protected Map<String, String> getTestProperties()
  {
    return new HashMap<String, String>();
  }

  protected IRepository getRepository()
  {
    return CDOServerUtil.getRepository(container, REPOSITORY_NAME);
  }

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();
    startTransport();
  }

  protected CDOSession openSession()
  {
    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(getConnector());
    configuration.setRepositoryName(REPOSITORY_NAME);
    configuration.setDisableLegacyObjects(true);
    return configuration.openSession();
  }

  protected CDOSession openModel1Session()
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);
    return session;
  }

  protected CDOSession openModel2Session()
  {
    CDOSession session = openModel1Session();
    session.getPackageRegistry().putEPackage(Model2Package.eINSTANCE);
    return session;
  }

  protected static void assertTransient(CDOObject object)
  {
    assertTrue(FSMUtil.isTransient(object));
    // assertEquals(null, object.cdoID());
    // assertEquals(null, object.cdoRevision());
    // assertEquals(null, object.cdoView());
    assertEquals(object.eResource(), object.cdoResource());
  }

  protected static void assertNotTransient(CDOObject object, CDOView view)
  {
    assertFalse(FSMUtil.isTransient(object));
    assertNotNull(object.cdoID());
    assertNotNull(object.cdoRevision());
    assertNotNull(object.cdoView());
    assertNotNull(object.cdoResource());
    assertNotNull(object.eResource());
    assertEquals(object.eResource(), object.cdoResource());
    assertEquals(view, object.cdoView());
    assertEquals(object, view.getObject(object.cdoID(), false));
  }

  protected static void assertNew(CDOObject object, CDOView view)
  {
    assertNotTransient(object, view);
    assertEquals(CDOState.NEW, object.cdoState());
  }

  protected static void assertDirty(CDOObject object, CDOView view)
  {
    assertNotTransient(object, view);
    assertEquals(CDOState.DIRTY, object.cdoState());
  }

  protected static void assertClean(CDOObject object, CDOView view)
  {
    assertNotTransient(object, view);
    assertEquals(CDOState.CLEAN, object.cdoState());
  }

  protected static void assertProxy(CDOObject object)
  {
    assertFalse(FSMUtil.isTransient(object));
    assertNotNull(object.cdoID());
    assertNotNull(object.cdoView());
    assertNotNull(object.cdoResource());
    assertNotNull(object.eResource());
    assertEquals(object.eResource(), object.cdoResource());
    assertEquals(CDOState.PROXY, object.cdoState());
  }

  protected static void assertContent(CDOObject container, CDOObject contained)
  {
    assertEquals(container.eResource(), contained.eResource());
    assertEquals(container.cdoResource(), contained.cdoResource());
    assertTrue(container.eContents().contains(contained));
    if (container instanceof CDOResource)
    {
      assertEquals(null, contained.eContainer());
      assertTrue(((CDOResource)container).getContents().contains(contained));
    }
    else
    {
      assertEquals(container, contained.eContainer());
    }
  }

  /**
   * @author Eike Stepper
   */
  public static interface ITimeOuter
  {
    public boolean timedOut() throws InterruptedException;
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class PollingTimeOuter implements ITimeOuter
  {
    private int retries;

    private long interval;

    public PollingTimeOuter(int retries, long interval)
    {
      this.retries = retries;
      this.interval = interval;
    }

    public int getRetries()
    {
      return retries;
    }

    public long getInterval()
    {
      return interval;
    }

    public boolean timedOut() throws InterruptedException
    {
      for (int i = 0; i < retries; i++)
      {
        if (successful())
        {
          return false;
        }

        sleep(interval);
      }

      return true;
    }

    protected abstract boolean successful();
  }

  /**
   * @author Eike Stepper
   */
  public static class LockTimeOuter implements ITimeOuter
  {
    private Lock lock;

    private long millis;

    public LockTimeOuter(Lock lock, long millis)
    {
      this.lock = lock;
      this.millis = millis;
    }

    public Lock getLock()
    {
      return lock;
    }

    public long getMillis()
    {
      return millis;
    }

    public boolean timedOut() throws InterruptedException
    {
      Condition condition = lock.newCondition();
      return !condition.await(millis, TimeUnit.MILLISECONDS);
    }
  }
}

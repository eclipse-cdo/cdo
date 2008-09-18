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
import org.eclipse.emf.cdo.tests.legacy.LegacyPackage;
import org.eclipse.emf.cdo.tests.mango.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.tests.model2.Model2Factory;
import org.eclipse.emf.cdo.tests.model2.Model2Package;
import org.eclipse.emf.cdo.tests.model3.Model3Factory;
import org.eclipse.emf.cdo.tests.model3.Model3Package;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.tests.model4.model4Package;
import org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.CDOLegacyWrapper;
import org.eclipse.emf.internal.cdo.util.FSMUtil;

import org.eclipse.net4j.tests.AbstractTransportTest;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.ecore.EObject;

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

  protected static boolean legacyTesting = false;

  @Override
  protected IManagedContainer createContainer()
  {
    LifecycleUtil.deactivate(container);
    IManagedContainer container = super.createContainer();
    CDOUtil.prepareContainer(container);
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

  protected IManagedContainer createRepository(String repoName)
  {
    IRepository repo2 = createRepository();
    repo2.setName(repoName);
    CDOServerUtil.addRepository(container, repo2);
    return container;
  }

  protected CDOSession openSession(String repoName)
  {
    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(getConnector());
    configuration.setRepositoryName(repoName);
    return configuration.openSession();
  }

  protected CDOSession openSession()
  {
    CDOSessionConfiguration configuration = CDOUtil.createSessionConfiguration();
    configuration.setConnector(getConnector());
    configuration.setRepositoryName(REPOSITORY_NAME);
    return configuration.openSession();
  }

  protected CDOSession openModel1Session(String repoName)
  {
    CDOSession session = openSession(repoName);
    session.getPackageRegistry().putEPackage(getModel1Package());
    return session;
  }

  protected CDOSession openModel1Session()
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel1Package());
    return session;
  }

  protected CDOSession openModel2Session()
  {
    CDOSession session = openModel1Session();
    session.getPackageRegistry().putEPackage(getModel2Package());
    return session;
  }

  protected CDOSession openModel3Session()
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getModel3Package());
    return session;
  }

  protected CDOSession openMangoSession()
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(getMangoPackage());
    return session;
  }

  protected CDOSession openLegacySession()
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(LegacyPackage.eINSTANCE);
    return session;
  }

  protected static void assertTransient(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(true, FSMUtil.isTransient(object));
    }
  }

  protected static MangoFactory getMangoFactory()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.mango.MangoFactory.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.mango.MangoFactory.eINSTANCE;
  }

  protected static MangoPackage getMangoPackage()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.mango.MangoPackage.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.mango.MangoPackage.eINSTANCE;
  }

  protected static Model1Factory getModel1Factory()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.model1.Model1Factory.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.model1.Model1Factory.eINSTANCE;
  }

  protected static Model1Package getModel1Package()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.model1.Model1Package.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.model1.Model1Package.eINSTANCE;
  }

  protected static Model2Factory getModel2Factory()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.model2.Model2Factory.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.model2.Model2Factory.eINSTANCE;
  }

  protected static Model2Package getModel2Package()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.model2.Model2Package.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.model2.Model2Package.eINSTANCE;
  }

  protected static Model3Factory getModel3Factory()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.model3.Model3Factory.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.model3.Model3Factory.eINSTANCE;
  }

  protected static Model3Package getModel3Package()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.model3.Model3Package.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.model3.Model3Package.eINSTANCE;
  }

  protected static model4Factory getModel4Factory()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.model4.model4Factory.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.model4.model4Factory.eINSTANCE;
  }

  protected static model4Package getModel4Package()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.model4.model4Package.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.model4.model4Package.eINSTANCE;
  }

  protected static model4interfacesPackage getModel4InterfacesPackage()
  {
    if (legacyTesting)
    {
      return org.eclipse.emf.cdo.tests.legacy.model4interfaces.model4interfacesPackage.eINSTANCE;
    }

    return org.eclipse.emf.cdo.tests.model4interfaces.model4interfacesPackage.eINSTANCE;
  }

  public static void assertEquals(Object expected, Object actual)
  {
    // IMPORTANT: Give possible CDOLegacyWrapper a chance for actual, too
    if (actual != null && actual.equals(expected))
    {
      return;
    }

    AbstractTransportTest.assertEquals(expected, actual);
  }

  public static void assertEquals(String message, Object expected, Object actual)
  {
    if (expected == null && actual == null)
    {
      return;
    }

    if (expected != null && expected.equals(actual))
    {
      return;
    }

    // IMPORTANT: Give possible CDOLegacyWrapper a chance for actual, too
    if (actual != null && actual.equals(expected))
    {
      return;
    }

    failNotEquals(message, expected, actual);
  }

  protected static void assertNotTransient(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertEquals(false, FSMUtil.isTransient(object));
    assertNotNull(object.cdoID());
    assertNotNull(object.cdoRevision());
    assertNotNull(object.cdoView());
    assertNotNull(object.eResource());
    assertEquals(view, object.cdoView());
    assertEquals(object, view.getObject(object.cdoID(), false));
  }

  protected static void assertNew(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.NEW, object.cdoState());
  }

  protected static void assertDirty(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.DIRTY, object.cdoState());
  }

  protected static void assertClean(EObject eObject, CDOView view)
  {
    CDOObject object = FSMUtil.adapt(eObject, view);
    assertNotTransient(object, view);
    assertEquals(CDOState.CLEAN, object.cdoState());
  }

  protected static void assertProxy(EObject eObject)
  {
    CDOObject object = CDOUtil.getCDOObject(eObject);
    if (object != null)
    {
      assertEquals(false, FSMUtil.isTransient(object));
      assertNotNull(object.cdoID());
      assertNotNull(object.cdoView());
      assertNotNull(object.cdoResource());
      assertNotNull(object.eResource());
      assertEquals(object.eResource(), object.cdoResource());
      assertEquals(CDOState.PROXY, object.cdoState());
    }
  }

  protected static void assertContent(EObject eContainer, EObject eContained)
  {
    CDOObject container = CDOUtil.getCDOObject(eContainer);
    CDOObject contained = CDOUtil.getCDOObject(eContained);
    if (container != null && contained != null)
    {
      assertEquals(container.eResource(), contained.eResource());
      assertEquals(true, container.eContents().contains(contained));
      if (container instanceof CDOResource)
      {
        assertEquals(container.eResource(), container.cdoResource());
        assertEquals(null, contained.eContainer());
        assertEquals(true, ((CDOResource)container).getContents().contains(contained));
      }
      else
      {
        assertEquals(container, contained.eContainer());
      }
    }
  }

  protected static void assertNotProxy(Object object)
  {
    assertEquals(false, CDOLegacyWrapper.isLegacyProxy(object));
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

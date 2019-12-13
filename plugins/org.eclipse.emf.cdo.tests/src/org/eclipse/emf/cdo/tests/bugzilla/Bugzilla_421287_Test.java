/*
 * Copyright (c) 2013, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.signal.RemoteException;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.CloseableIterator;
import org.eclipse.net4j.util.concurrent.ExecutorServiceFactory;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcoreFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Bug 421287: [Query] Failure to execute query results in hung async iterator
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class Bugzilla_421287_Test extends AbstractCDOTest
{
  private static final ThreadLocal<Boolean> IS_RUNNING_IN_BACKGROUND = new ThreadLocal<Boolean>()
  {
    @Override
    protected Boolean initialValue()
    {
      return false;
    }
  };

  public void testSyncQueryForUnknownEClass() throws Exception
  {
    if (runningAsync())
    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      try
      {
        List<?> results = view.queryInstances(createDynamicEClass());
        assertEquals(true, results.isEmpty());
      }
      catch (RemoteException ex)
      {
        // This is expected (though I would argue it should not be)
        assertEquals(true, StringUtil.safe(ex.getLocalizedMessage()).contains("Package not found"));
      }
    }
  }

  public void testAsyncQueryForUnknownEClass() throws Exception
  {
    if (runningAsync())
    {
      CDOSession session = openSession();
      CDOView view = session.openView();

      CloseableIterator<?> results = view.queryInstancesAsync(createDynamicEClass());

      try
      {
        assertEquals(false, results.hasNext());
      }
      finally
      {
        results.close();
      }
    }
  }

  private boolean runningAsync()
  {
    final boolean result = IS_RUNNING_IN_BACKGROUND.get();

    if (!result)
    {
      try
      {
        final Method method = getClass().getMethod(getName());
        executeAsync(new Callable<Void>()
        {
          @Override
          public Void call() throws Exception
          {
            IS_RUNNING_IN_BACKGROUND.set(true);

            try
            {
              method.invoke(Bugzilla_421287_Test.this);
            }
            catch (InvocationTargetException ex)
            {
              if (ex.getTargetException() instanceof Exception)
              {
                throw (Exception)ex.getTargetException();
              }
              else if (ex.getTargetException() instanceof Error)
              {
                throw (Error)ex.getTargetException();
              }
              else
              {
                throw ex;
              }

            }
            return null;
          }
        });
      }
      catch (Exception ex)
      {
        fail("Test reflection failure: " + ex.getLocalizedMessage());
      }
    }

    return result;
  }

  private void executeAsync(Callable<?> assertions)
  {
    Future<?> future = ExecutorServiceFactory.get(getClientContainer()).submit(assertions);

    try
    {
      future.get(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    catch (TimeoutException ex)
    {
      fail("Test timed out");
    }
    catch (InterruptedException ex)
    {
      fail("Test interrupted");
    }
    catch (ExecutionException ex)
    {
      if (ex.getCause() instanceof RuntimeException)
      {
        throw (RuntimeException)ex.getCause();
      }
      else if (ex.getCause() instanceof Error)
      {
        throw (Error)ex.getCause();
      }
      else
      {
        fail("Unexpected exception in test: " + ex.getCause());
      }
    }
  }

  private EClass createDynamicEClass()
  {
    EPackage ePackage = createUniquePackage();

    EClass result = EcoreFactory.eINSTANCE.createEClass();
    result.setName("Dynamic");
    ePackage.getEClassifiers().add(result);
    return result;
  }
}

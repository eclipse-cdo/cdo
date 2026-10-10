/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.tests.plain;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.tests.config.impl.PlainTest;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.spi.cdo.CDOChangeSubscriptionAdapter;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

/**
 * Tests exception safety of {@link CDOChangeSubscriptionAdapter} independently of a repository or session.
 *
 * @author Eike Stepper
 */
public class CDOChangeSubscriptionAdapterTest extends PlainTest
{
  public void testAttachCompensatesAfterAdapterListMutationAndCanRetry()
  {
    Fixture fixture = new Fixture();
    IllegalStateException expectedFailure = new IllegalStateException("add listener failure");
    fixture.adapters.failNextAddAfterMutation(expectedFailure);

    RuntimeException actualFailure = captureFailure(() -> fixture.adapter.attach(fixture.notifier));

    assertSame(expectedFailure, actualFailure);
    assertFalse(fixture.adapters.contains(fixture.adapter));
    assertFalse(fixture.adapter.getNotifiers().contains(fixture.notifier));

    fixture.adapter.attach(fixture.notifier);
    assertTrue(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().contains(fixture.notifier));

    fixture.adapter.reset();
    assertFalse(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().isEmpty());
  }

  public void testAttachPreservesOriginalFailureAndSuppressedCompensationFailure()
  {
    Fixture fixture = new Fixture();
    IllegalStateException expectedFailure = new IllegalStateException("add listener failure");
    IllegalArgumentException compensationFailure = new IllegalArgumentException("remove listener failure");
    fixture.adapters.failNextAddAfterMutation(expectedFailure);
    fixture.adapters.failNextRemoveBeforeMutation(compensationFailure);

    RuntimeException actualFailure = captureFailure(() -> fixture.adapter.attach(fixture.notifier));

    assertSame(expectedFailure, actualFailure);
    assertEquals(1, actualFailure.getSuppressed().length);
    assertSame(compensationFailure, actualFailure.getSuppressed()[0]);
    assertTrue(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().contains(fixture.notifier));

    fixture.adapter.reset();
    assertFalse(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().isEmpty());
  }

  public void testAttachDoesNotRemoveExistingAdapterWhenAddFailsBeforeMutation()
  {
    Fixture fixture = new Fixture();
    fixture.adapters.addDirectly(fixture.adapter);
    IllegalStateException expectedFailure = new IllegalStateException("add listener failure before mutation");
    fixture.adapters.failNextAddBeforeMutation(expectedFailure);

    RuntimeException actualFailure = captureFailure(() -> fixture.adapter.attach(fixture.notifier));

    assertSame(expectedFailure, actualFailure);
    assertTrue(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().contains(fixture.notifier));

    fixture.adapter.reset();
    assertFalse(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().isEmpty());
  }

  public void testResetRetainsNotifierWhenRemovalFailsBeforeMutation()
  {
    Fixture fixture = new Fixture();
    fixture.adapter.attach(fixture.notifier);
    IllegalStateException expectedFailure = new IllegalStateException("remove listener failure");
    fixture.adapters.failNextRemoveBeforeMutation(expectedFailure);

    fixture.adapter.reset();

    assertTrue(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().contains(fixture.notifier));

    fixture.adapter.reset();
    assertFalse(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().isEmpty());
  }

  public void testResetDropsNotifierWhenRemovalFailsAfterMutation()
  {
    Fixture fixture = new Fixture();
    fixture.adapter.attach(fixture.notifier);
    fixture.adapters.failNextRemoveAfterMutation(new IllegalStateException("remove listener failure after mutation"));

    fixture.adapter.reset();

    assertFalse(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().isEmpty());
  }

  public void testResetPropagatesErrorAndRetainsNotifierWhenRemovalFailsBeforeMutation()
  {
    Fixture fixture = new Fixture();
    fixture.adapter.attach(fixture.notifier);
    AssertionError expectedFailure = new AssertionError("remove listener error before mutation");
    fixture.adapters.failNextRemoveBeforeMutation(expectedFailure);

    Error actualFailure = captureError(fixture.adapter::reset);

    assertSame(expectedFailure, actualFailure);
    assertTrue(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().contains(fixture.notifier));

    fixture.adapter.reset();
    assertFalse(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().isEmpty());
    assertEquals(2, fixture.adapters.getRemoveAttempts());
  }

  public void testResetPropagatesErrorAndDropsNotifierWhenRemovalFailsAfterMutation()
  {
    Fixture fixture = new Fixture();
    fixture.adapter.attach(fixture.notifier);
    AssertionError expectedFailure = new AssertionError("remove listener error after mutation");
    fixture.adapters.failNextRemoveAfterMutation(expectedFailure);

    Error actualFailure = captureError(fixture.adapter::reset);

    assertSame(expectedFailure, actualFailure);
    assertFalse(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().isEmpty());

    fixture.adapter.reset();
    assertEquals(1, fixture.adapters.getRemoveAttempts());
  }

  public void testResetSuppressesMembershipInspectionFailureOnRemovalError()
  {
    Fixture fixture = new Fixture();
    fixture.adapter.attach(fixture.notifier);
    AssertionError expectedFailure = new AssertionError("remove listener error");
    AssertionError inspectionFailure = new AssertionError("membership inspection error");
    fixture.adapters.failNextRemoveBeforeMutation(expectedFailure);
    fixture.adapters.failNextContains(inspectionFailure);

    Error actualFailure = captureError(fixture.adapter::reset);

    assertSame(expectedFailure, actualFailure);
    assertEquals(1, actualFailure.getSuppressed().length);
    assertSame(inspectionFailure, actualFailure.getSuppressed()[0]);
    assertTrue(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().contains(fixture.notifier));

    fixture.adapter.reset();
    assertFalse(fixture.adapters.contains(fixture.adapter));
    assertTrue(fixture.adapter.getNotifiers().isEmpty());
  }

  private RuntimeException captureFailure(Runnable operation)
  {
    try
    {
      operation.run();
    }
    catch (RuntimeException ex)
    {
      return ex;
    }

    return null;
  }

  private Error captureError(Runnable operation)
  {
    try
    {
      operation.run();
    }
    catch (Error ex)
    {
      return ex;
    }

    return null;
  }

  private static Object objectMethod(Object proxy, Method method, Object[] args) throws Throwable
  {
    if ("hashCode".equals(method.getName()))
    {
      return System.identityHashCode(proxy);
    }

    if ("equals".equals(method.getName()))
    {
      return proxy == args[0];
    }

    if ("toString".equals(method.getName()))
    {
      return proxy.getClass().getInterfaces()[0].getSimpleName();
    }

    return null;
  }

  /**
   * @author Eike Stepper
   */
  private static final class Fixture
  {
    public final ThrowingAdapterList adapters = new ThrowingAdapterList();

    public final CDOObject notifier = createNotifier(adapters.asEList());

    public final CDOChangeSubscriptionAdapter adapter = new CDOChangeSubscriptionAdapter(createView());

    public Fixture()
    {
    }

    private static CDOObject createNotifier(EList<Adapter> adapters)
    {
      return (CDOObject)Proxy.newProxyInstance(CDOObject.class.getClassLoader(), new Class<?>[] { CDOObject.class }, (proxy, method, args) -> {
        if ("eAdapters".equals(method.getName()))
        {
          return adapters;
        }

        return objectMethod(proxy, method, args);
      });
    }

    private static CDOView createView()
    {
      CDOView.Options options = (CDOView.Options)Proxy.newProxyInstance(CDOView.Options.class.getClassLoader(), new Class<?>[] { CDOView.Options.class },
          (proxy, method, args) -> objectMethod(proxy, method, args));
      return (CDOView)Proxy.newProxyInstance(CDOView.class.getClassLoader(), new Class<?>[] { CDOView.class }, (proxy, method, args) -> {
        if ("options".equals(method.getName()))
        {
          return options;
        }

        return objectMethod(proxy, method, args);
      });
    }
  }

  /**
   * @author Eike Stepper
   */
  private static final class ThrowingAdapterList implements InvocationHandler
  {
    private final List<Adapter> delegate = new ArrayList<>();

    private RuntimeException addFailure;

    private boolean addFailureAfterMutation;

    private Throwable removeFailure;

    private boolean removeFailureAfterMutation;

    private Throwable containsFailure;

    private int removeAttempts;

    public ThrowingAdapterList()
    {
    }

    @SuppressWarnings("unchecked")
    public EList<Adapter> asEList()
    {
      return (EList<Adapter>)Proxy.newProxyInstance(EList.class.getClassLoader(), new Class<?>[] { EList.class }, this);
    }

    public void failNextAddAfterMutation(RuntimeException failure)
    {
      addFailure = failure;
      addFailureAfterMutation = true;
    }

    public void failNextAddBeforeMutation(RuntimeException failure)
    {
      addFailure = failure;
      addFailureAfterMutation = false;
    }

    public void failNextRemoveBeforeMutation(Throwable failure)
    {
      removeFailure = failure;
      removeFailureAfterMutation = false;
    }

    public void failNextRemoveAfterMutation(Throwable failure)
    {
      removeFailure = failure;
      removeFailureAfterMutation = true;
    }

    public boolean contains(Adapter adapter)
    {
      return delegate.contains(adapter);
    }

    public int getRemoveAttempts()
    {
      return removeAttempts;
    }

    public void failNextContains(Throwable failure)
    {
      containsFailure = failure;
    }

    public void addDirectly(Adapter adapter)
    {
      delegate.add(adapter);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
    {
      String name = method.getName();
      if ("add".equals(name) && args != null && args.length == 1 && args[0] instanceof Adapter)
      {
        RuntimeException failure = addFailure;
        addFailure = null;
        if (failure != null && !addFailureAfterMutation)
        {
          throw failure;
        }

        boolean changed = delegate.add((Adapter)args[0]);
        if (failure != null)
        {
          throw failure;
        }

        return changed;
      }

      if ("remove".equals(name) && args != null && args.length == 1 && args[0] instanceof Adapter)
      {
        ++removeAttempts;
        Throwable failure = removeFailure;
        removeFailure = null;
        if (failure != null && !removeFailureAfterMutation)
        {
          throw failure;
        }

        boolean changed = delegate.remove(args[0]);
        if (failure != null)
        {
          throw failure;
        }

        return changed;
      }

      if ("contains".equals(name) && args != null && args.length == 1 && args[0] instanceof Adapter && containsFailure != null)
      {
        Throwable failure = containsFailure;
        containsFailure = null;
        throw failure;
      }

      try
      {
        return method.invoke(delegate, args);
      }
      catch (InvocationTargetException ex)
      {
        throw ex.getCause();
      }
    }
  }
}

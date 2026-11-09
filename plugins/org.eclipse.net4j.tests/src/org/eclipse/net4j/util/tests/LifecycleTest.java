/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.net4j.util.tests;

import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.ILifecycleEvent;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleException;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil.ReactivationTrigger;

/**
 * Exercises lifecycle activation failure recovery.
 *
 * @author Eike Stepper
 */
public class LifecycleTest extends AbstractOMTest
{
  public void testAfterActivationReactivationRestartsWithoutFailureEvents()
  {
    TestLifecycle lifecycle = new TestLifecycle(null, true);
    StringBuilder events = new StringBuilder();
    IListener listener = event -> {
      if (event instanceof ILifecycleEvent)
      {
        events.append(((ILifecycleEvent)event).getKind().name()).append(',');
      }
    };
    lifecycle.addListener(listener);

    try
    {
      lifecycle.activate();

      assertEquals(LifecycleState.ACTIVE, lifecycle.getLifecycleState());
      assertEquals(2, lifecycle.getActivateCount());
      assertEquals(2, lifecycle.getAfterActivateCount());
      assertEquals(1, lifecycle.getDeactivateCount());
      assertEquals("ABOUT_TO_ACTIVATE,ABOUT_TO_ACTIVATE,ACTIVATED,", events.toString()); //$NON-NLS-1$
      lifecycle.deactivate();
      assertEquals(LifecycleState.INACTIVE, lifecycle.getLifecycleState());
      assertEquals(2, lifecycle.getDeactivateCount());
      assertEquals("ABOUT_TO_ACTIVATE,ABOUT_TO_ACTIVATE,ACTIVATED,ABOUT_TO_DEACTIVATE,DEACTIVATED,", events.toString()); //$NON-NLS-1$
    }
    finally
    {
      lifecycle.removeListener(listener);
      lifecycle.deactivate();
    }
  }

  public void testAfterActivationFailureCleansUpAndAllowsRestart()
  {
    RuntimeException activationFailure = new RuntimeException("after activation failure"); //$NON-NLS-1$
    TestLifecycle lifecycle = new TestLifecycle(activationFailure, false);
    StringBuilder events = new StringBuilder();
    IListener listener = event -> {
      if (event instanceof ILifecycleEvent)
      {
        events.append(((ILifecycleEvent)event).getKind().name()).append(',');
      }
    };
    lifecycle.addListener(listener);

    try
    {
      try
      {
        lifecycle.activate();
        fail("Activation should report the doAfterActivate() failure"); //$NON-NLS-1$
      }
      catch (LifecycleException ex)
      {
        assertSame(activationFailure, ex.getCause());
      }

      assertEquals(LifecycleState.INACTIVE, lifecycle.getLifecycleState());
      assertEquals(1, lifecycle.getDeactivateCount());
      assertEquals("ABOUT_TO_ACTIVATE,ABOUT_TO_DEACTIVATE,DEACTIVATED,", events.toString()); //$NON-NLS-1$

      lifecycle.setActivationFailure(null);
      events.setLength(0);
      lifecycle.activate();
      assertEquals(LifecycleState.ACTIVE, lifecycle.getLifecycleState());
      assertEquals(2, lifecycle.getAfterActivateCount());
      assertEquals("ABOUT_TO_ACTIVATE,ACTIVATED,", events.toString()); //$NON-NLS-1$

      lifecycle.deactivate();
      assertEquals(LifecycleState.INACTIVE, lifecycle.getLifecycleState());
      assertEquals(2, lifecycle.getDeactivateCount());
      assertEquals("ABOUT_TO_ACTIVATE,ACTIVATED,ABOUT_TO_DEACTIVATE,DEACTIVATED,", events.toString()); //$NON-NLS-1$
    }
    finally
    {
      lifecycle.removeListener(listener);
      lifecycle.deactivate();
    }
  }

  public void testAfterActivationFailureRetainsCleanupFailureAsSuppressed()
  {
    RuntimeException activationFailure = new RuntimeException("after activation failure"); //$NON-NLS-1$
    RuntimeException cleanupFailure = new RuntimeException("deactivation failure"); //$NON-NLS-1$
    TestLifecycle lifecycle = new TestLifecycle(activationFailure, false);
    lifecycle.setDeactivationFailure(cleanupFailure);

    try
    {
      lifecycle.activate();
      fail("Activation should report the doAfterActivate() failure"); //$NON-NLS-1$
    }
    catch (LifecycleException ex)
    {
      assertSame(activationFailure, ex.getCause());
      assertEquals(1, activationFailure.getSuppressed().length);
      assertSame(cleanupFailure, activationFailure.getSuppressed()[0]);
    }

    assertEquals(LifecycleState.INACTIVE, lifecycle.getLifecycleState());
    assertEquals(1, lifecycle.getDeactivateCount());
    lifecycle.setDeactivationFailure(null);
    lifecycle.setActivationFailure(null);
    lifecycle.activate();
    assertEquals(LifecycleState.ACTIVE, lifecycle.getLifecycleState());
    lifecycle.deactivate();
    assertEquals(LifecycleState.INACTIVE, lifecycle.getLifecycleState());
  }

  /**
   * A lifecycle whose post-activation hook can fail deterministically.
   *
   * @author Eike Stepper
   */
  private static final class TestLifecycle extends Lifecycle
  {
    private RuntimeException activationFailure;

    private RuntimeException deactivationFailure;

    private int afterActivateCount;

    private int deactivateCount;

    private int activateCount;

    private boolean reactivateAfterFirstActivation;

    public TestLifecycle(RuntimeException activationFailure, boolean reactivateAfterFirstActivation)
    {
      this.activationFailure = activationFailure;
      this.reactivateAfterFirstActivation = reactivateAfterFirstActivation;
    }

    public int getAfterActivateCount()
    {
      return afterActivateCount;
    }

    public int getActivateCount()
    {
      return activateCount;
    }

    public int getDeactivateCount()
    {
      return deactivateCount;
    }

    public void setActivationFailure(RuntimeException activationFailure)
    {
      this.activationFailure = activationFailure;
    }

    public void setDeactivationFailure(RuntimeException deactivationFailure)
    {
      this.deactivationFailure = deactivationFailure;
    }

    @Override
    protected void doActivate()
    {
      ++activateCount;
    }

    @Override
    protected void doAfterActivate()
    {
      ++afterActivateCount;
      if (reactivateAfterFirstActivation && afterActivateCount == 1)
      {
        throw ReactivationTrigger.createFor(this);
      }

      if (activationFailure != null)
      {
        throw activationFailure;
      }
    }

    @Override
    protected void doDeactivate()
    {
      ++deactivateCount;
      if (deactivationFailure != null)
      {
        throw deactivationFailure;
      }
    }
  }
}

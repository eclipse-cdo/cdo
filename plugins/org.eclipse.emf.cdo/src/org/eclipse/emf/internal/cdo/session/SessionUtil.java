/*
 * Copyright (c) 2009, 2011-2014 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.session;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.view.CDOViewSetImpl;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.spi.cdo.InternalCDOViewSet;

/**
 * @author Eike Stepper
 */
public final class SessionUtil
{
  private static final boolean ROOT_RESOURCE_EXCLUSION_CHECK = false;

  private static Runnable testDelayInSessionActivation;

  private static Runnable testDelayInViewActivation;

  private SessionUtil()
  {
  }

  private static void addRootResourceExclusionCheckAdapter(ResourceSet resourceSet)
  {
    class RootResourceExclusionCheckAdapter extends AdapterImpl
    {
      @Override
      public void notifyChanged(Notification msg)
      {
        if (msg.getEventType() == Notification.ADD || msg.getEventType() == Notification.ADD_MANY)
        {
          Object newValue = msg.getNewValue();
          check(newValue);
        }
      }

      @Override
      public void setTarget(Notifier newTarget)
      {
        check(newTarget);
      }

      private void check(Object object)
      {
        if (object instanceof CDOResource && ((CDOResource)object).isRoot())
        {
          throw new AssertionError("Root resource in resource set not allowed");
        }
      }
    }

    resourceSet.eAdapters().add(new RootResourceExclusionCheckAdapter());
  }

  /**
   * @since 2.0
   */
  public static InternalCDOViewSet prepareResourceSet(ResourceSet resourceSet)
  {
    InternalCDOViewSet viewSet = null;
    synchronized (resourceSet)
    {
      if (ROOT_RESOURCE_EXCLUSION_CHECK)
      {
        addRootResourceExclusionCheckAdapter(resourceSet);
      }

      viewSet = (InternalCDOViewSet)CDOUtil.getViewSet(resourceSet);
      if (viewSet == null)
      {
        viewSet = new CDOViewSetImpl();
        resourceSet.eAdapters().add(viewSet);
      }
    }

    return viewSet;
  }

  public static Runnable getTestDelayInSessionActivation()
  {
    return testDelayInSessionActivation;
  }

  public static void setTestDelayInSessionActivation(Runnable runnable)
  {
    testDelayInSessionActivation = runnable;
  }

  public static Runnable getTestDelayInViewActivation()
  {
    return testDelayInViewActivation;
  }

  public static void setTestDelayInViewActivation(Runnable runnable)
  {
    testDelayInViewActivation = runnable;
  }
}

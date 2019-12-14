/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.resources;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.gmf.runtime.emf.core.internal.resources.PathmapManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Martin Fluegge
 */
@SuppressWarnings("restriction")
public class DawnPathmapManager extends PathmapManager
{
  // TODO remove this as soon as the PathmapManager problem is solved!
  @Override
  public void notifyChanged(Notification msg)
  {
    if (msg.getFeatureID(ResourceSet.class) == ResourceSet.RESOURCE_SET__RESOURCES)
    {
      switch (msg.getEventType())
      {
      case Notification.REMOVE_MANY:
      {
        if (msg.getNewValue() instanceof int[])
        {
          return;
        }
        break;
      }
      }
    }

    super.notifyChanged(msg);
  }

  public static void removePathMapMananger(EList<Adapter> eAdapters)
  {
    List<Adapter> toBeRemoved = new ArrayList<>();
    for (Adapter a : eAdapters)
    {
      if (a instanceof PathmapManager)
      {
        toBeRemoved.add(a);
      }
    }
    eAdapters.removeAll(toBeRemoved);
  }

}

/*
 * Copyright (c) 2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewRegistry;
import org.eclipse.emf.cdo.view.CDOViewRegistry.Registration;

import org.eclipse.net4j.util.container.Container;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.2
 * @see CDOView
 */
public class CDOViewRegistryImpl extends Container<Registration> implements CDOViewRegistry
{
  public static final CDOViewRegistryImpl INSTANCE = new CDOViewRegistryImpl();

  private final Map<Integer, Registration> ids = new HashMap<Integer, Registration>();

  private final Map<CDOView, Registration> views = new HashMap<CDOView, Registration>();

  private int lastID;

  public CDOViewRegistryImpl()
  {
  }

  @Override
  public synchronized Registration[] getElements()
  {
    return ids.values().toArray(new Registration[ids.size()]);
  }

  @Override
  public synchronized int[] getIDs()
  {
    int[] result = new int[ids.size()];

    int i = 0;
    for (Integer id : ids.keySet())
    {
      result[i++] = id;
    }

    return result;
  }

  @Override
  public synchronized CDOView[] getViews()
  {
    return views.keySet().toArray(new CDOView[views.size()]);
  }

  @Override
  public synchronized int getID(CDOView view)
  {
    Registration registration = views.get(view);
    if (registration != null)
    {
      return registration.getID();
    }

    return NOT_REGISTERED;
  }

  @Override
  public synchronized CDOView getView(int id)
  {
    Registration registration = ids.get(id);
    if (registration != null)
    {
      return registration.getView();
    }

    return null;
  }

  void register(CDOView view)
  {
    Registration registration;
    synchronized (this)
    {
      if (views.containsKey(view))
      {
        return;
      }

      int id = ++lastID;
      registration = new RegistrationImpl(id, view);

      ids.put(id, registration);
      views.put(view, registration);
    }

    fireElementAddedEvent(registration);
  }

  void deregister(CDOView view)
  {
    Registration registration;
    synchronized (this)
    {
      registration = views.remove(view);
      if (registration != null)
      {
        int id = registration.getID();
        ids.remove(id);
      }
    }

    fireElementRemovedEvent(registration);
  }

  /**
   * @author Eike Stepper
   */
  private static final class RegistrationImpl implements Registration
  {
    private final int id;

    private final CDOView view;

    public RegistrationImpl(int id, CDOView view)
    {
      this.id = id;
      this.view = view;
    }

    @Override
    public int getID()
    {
      return id;
    }

    @Override
    public CDOView getView()
    {
      return view;
    }
  }
}

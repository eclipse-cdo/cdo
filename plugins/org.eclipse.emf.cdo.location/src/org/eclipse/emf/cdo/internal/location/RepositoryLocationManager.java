/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.location;

import org.eclipse.emf.cdo.location.IRepositoryLocation;
import org.eclipse.emf.cdo.location.IRepositoryLocationManager;

import org.eclipse.net4j.util.container.Container;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RepositoryLocationManager extends Container<IRepositoryLocation> implements IRepositoryLocationManager
{
  private static final long serialVersionUID = 1L;

  private List<IRepositoryLocation> repositoryLocations = new ArrayList<IRepositoryLocation>();

  public RepositoryLocationManager()
  {
    activate();
  }

  public IRepositoryLocation[] getElements()
  {
    return getRepositoryLocations();
  }

  public IRepositoryLocation[] getRepositoryLocations()
  {
    synchronized (repositoryLocations)
    {
      return repositoryLocations.toArray(new IRepositoryLocation[repositoryLocations.size()]);
    }
  }

  public IRepositoryLocation addRepositoryLocation(String connectorType, String connectorDescription,
      String repositoryName)
  {
    RepositoryLocation location = new RepositoryLocation(this, connectorType, connectorDescription, repositoryName);

    boolean added;
    synchronized (repositoryLocations)
    {
      added = repositoryLocations.add(location);
    }

    if (added)
    {
      fireElementAddedEvent(location);
    }

    return location;
  }

  public void removeRepositoryLocation(RepositoryLocation location)
  {
    boolean removed;
    synchronized (repositoryLocations)
    {
      removed = repositoryLocations.remove(location);
    }

    if (removed)
    {
      fireElementRemovedEvent(location);
    }
  }

  // @Override
  // protected void doActivate() throws Exception
  // {
  // super.doActivate();
  // for (IRepositoryLocation location : repositoryLocations)
  // {
  // LifecycleUtil.activate(location);
  // }
  // }
  //
  // @Override
  // protected void doDeactivate() throws Exception
  // {
  // for (IRepositoryLocation location : repositoryLocations)
  // {
  // LifecycleUtil.deactivate(location);
  // }
  //
  // super.doDeactivate();
  // }
}

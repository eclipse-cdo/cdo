/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class RepositoryLocationManager extends Container<IRepositoryLocation> implements IRepositoryLocationManager
{
  private static final long serialVersionUID = 1L;

  private List<RepositoryLocation> repositoryLocations = new ArrayList<RepositoryLocation>();

  public RepositoryLocationManager()
  {
    activate();
  }

  public RepositoryLocation[] getElements()
  {
    return getRepositoryLocations();
  }

  public RepositoryLocation[] getRepositoryLocations()
  {
    synchronized (repositoryLocations)
    {
      return repositoryLocations.toArray(new RepositoryLocation[repositoryLocations.size()]);
    }
  }

  public RepositoryLocation addRepositoryLocation(String connectorType, String connectorDescription,
      String repositoryName)
  {
    RepositoryLocation location = new RepositoryLocation(this, connectorType, connectorDescription, repositoryName);
    return addRepositoryLocation(location);
  }

  public RepositoryLocation addRepositoryLocation(InputStream in) throws IOException
  {
    RepositoryLocation location = new RepositoryLocation(this, in);
    return addRepositoryLocation(location);
  }

  private RepositoryLocation addRepositoryLocation(RepositoryLocation location)
  {
    synchronized (repositoryLocations)
    {
      int pos = repositoryLocations.indexOf(location);
      if (pos != -1)
      {
        return repositoryLocations.get(pos);
      }

      repositoryLocations.add(location);
    }

    fireElementAddedEvent(location);
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
}

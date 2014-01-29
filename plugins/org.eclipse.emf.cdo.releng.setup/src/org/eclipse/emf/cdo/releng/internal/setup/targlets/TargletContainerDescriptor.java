/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.internal.setup.targlets;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.equinox.p2.core.ProvisionException;
import org.eclipse.equinox.p2.engine.IProfile;

import java.io.Serializable;

/**
 * @author Eike Stepper
 */
public final class TargletContainerDescriptor implements Serializable, Comparable<TargletContainerDescriptor>
{
  private static final long serialVersionUID = 1L;

  private String id;

  private String currentDigest;

  private String workingDigest;

  private transient IProfile profile;

  public TargletContainerDescriptor()
  {
  }

  public TargletContainerDescriptor(String id)
  {
    this.id = id;
  }

  public String getID()
  {
    return id;
  }

  public int compareTo(TargletContainerDescriptor o)
  {
    return id.compareTo(o.getID());
  }

  public void invalidate(String newDigest)
  {
    if (workingDigest != null)
    {
      if (workingDigest.equals(newDigest))
      {
        currentDigest = workingDigest;
        return;
      }
    }
    else
    {
      workingDigest = currentDigest;
      currentDigest = newDigest;
    }
  }

  public IProfile getProfile(IProgressMonitor monitor) throws ProvisionException
  {
    if (profile == null)
    {
      String digest = workingDigest != null ? workingDigest : currentDigest;
      if (digest != null)
      {
        TargletContainerManager manager = TargletContainerManager.getInstance();
        profile = manager.getProfile(digest, monitor);
      }
    }

    return profile;
  }

  public IProfile createProfile(String environmentProperties, String nlProperty, IProgressMonitor monitor)
      throws ProvisionException
  {
    TargletContainerManager manager = TargletContainerManager.getInstance();
    return manager.createProfile(id, currentDigest, environmentProperties, nlProperty, monitor);
  }

  public void commitProfile(IProgressMonitor progressMonitor) throws ProvisionException
  {
  }

  public IProfile rollbackProfile(IProgressMonitor progressMonitor) throws ProvisionException
  {
    return null;
  }
}

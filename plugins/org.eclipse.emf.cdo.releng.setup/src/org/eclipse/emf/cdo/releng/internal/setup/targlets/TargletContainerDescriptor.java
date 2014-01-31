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

  private String updateProblem;

  private String workingDigest;

  private transient IProfile transactionProfile;

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

  public String getUpdateProblem()
  {
    return updateProblem;
  }

  public String getWorkingDigest()
  {
    return workingDigest;
  }

  public int compareTo(TargletContainerDescriptor o)
  {
    return id.compareTo(o.getID());
  }

  public IProfile startUpdateTransaction(String environmentProperties, String nlProperty, String digest,
      IProgressMonitor monitor) throws ProvisionException
  {
    if (transactionProfile != null)
    {
      throw new ProvisionException("An update transaction is already ongoing");
    }

    TargletContainerManager manager = TargletContainerManager.getInstance();
    transactionProfile = manager.getOrCreateProfile(id, environmentProperties, nlProperty, digest, monitor);
    return transactionProfile;
  }

  public void commitUpdateTransaction(String digest, IProgressMonitor monitor) throws ProvisionException
  {
    if (transactionProfile == null)
    {
      throw new ProvisionException("No update transaction is ongoing");
    }

    try
    {
      updateProblem = null;
      workingDigest = digest;

      TargletContainerManager manager = TargletContainerManager.getInstance();
      manager.saveDescriptors(monitor);
    }
    catch (Throwable t)
    {
      t.printStackTrace();
      updateProblem = t.toString();
      throw new ProvisionException("Update transaction could not be committed", t);
    }
    finally
    {
      transactionProfile = null;
    }
  }

  public void rollbackUpdateTransaction(Throwable t, IProgressMonitor monitor) throws ProvisionException
  {
    transactionProfile = null;
    updateProblem = t.toString();

    TargletContainerManager manager = TargletContainerManager.getInstance();
    manager.saveDescriptors(monitor);
  }
}

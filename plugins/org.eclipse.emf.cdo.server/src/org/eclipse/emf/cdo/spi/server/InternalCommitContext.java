/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDMetaRange;
import org.eclipse.emf.cdo.server.IStoreAccessor;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageUnit;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionDelta;

import org.eclipse.net4j.util.om.monitor.OMMonitor;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalCommitContext extends IStoreAccessor.CommitContext
{
  public InternalTransaction getTransaction();

  public void preCommit();

  public void write(OMMonitor monitor);

  public void commit(OMMonitor monitor);

  public void rollback(String message);

  public void postCommit(boolean success);

  public String getRollbackMessage();

  public List<CDOIDMetaRange> getMetaIDRanges();

  public void setNewPackageUnits(InternalCDOPackageUnit[] newPackageUnits);

  public void setNewObjects(InternalCDORevision[] newObjects);

  public void setDirtyObjectDeltas(InternalCDORevisionDelta[] dirtyObjectDeltas);

  public void setDetachedObjects(CDOID[] detachedObjects);

  public boolean setAutoReleaseLocksEnabled(boolean on);

  public boolean isAutoReleaseLocksEnabled();
}

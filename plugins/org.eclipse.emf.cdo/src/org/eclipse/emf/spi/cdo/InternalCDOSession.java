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
package org.eclipse.emf.spi.cdo;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOPackageRegistry;

import org.eclipse.net4j.util.lifecycle.ILifecycle;

import java.util.Collection;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public interface InternalCDOSession extends CDOSession, CDOIDObjectFactory,
    InternalCDOPackageRegistry.PackageProcessor, InternalCDOPackageRegistry.PackageLoader, ILifecycle.Introspection
{
  public CDOSessionProtocol getSessionProtocol();

  public void setRepositoryName(String repositoryName);

  public void setPackageRegistry(CDOPackageRegistry packageRegistry);

  public void viewDetached(InternalCDOView view);

  public void handleCommitNotification(long timeStamp, Set<CDOIDAndVersion> dirtyOIDs,
      Collection<CDOID> detachedObjects, Collection<CDORevisionDelta> deltas, InternalCDOView excludedView);

  public void handleSyncResponse(long timestamp, Set<CDOIDAndVersion> dirtyOIDs, Collection<CDOID> detachedObjects);
}

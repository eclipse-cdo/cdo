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

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDProvider;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalSession extends ISession, CDOIDProvider, CDOCommonSession.Options
{
  public InternalSessionManager getManager();

  public InternalView[] getViews();

  public InternalView getView(int viewID);

  public InternalView openView(int viewID, CDOBranchPoint branchPoint);

  public InternalTransaction openTransaction(int viewID, CDOBranchPoint branchPoint);

  public void viewClosed(InternalView view);

  public void setSubscribed(boolean subscribed);

  public void handleBranchNotification(InternalCDOBranch branch);

  public void handleCommitNotification(CDOBranchPoint branchPoint, CDOPackageUnit[] packageUnits,
      List<CDOIDAndVersion> dirtyIDs, List<CDOID> detachedObjects, List<CDORevisionDelta> deltas);

  public void collectContainedRevisions(InternalCDORevision revision, CDOBranchPoint branchPoint, int referenceChunk,
      Set<CDOID> revisions, List<CDORevision> additionalRevisions);
}

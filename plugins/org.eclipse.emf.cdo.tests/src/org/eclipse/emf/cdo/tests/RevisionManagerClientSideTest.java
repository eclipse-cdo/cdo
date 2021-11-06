/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.emf.spi.cdo.InternalCDOSession;

/**
 * @author Eike Stepper
 */
public class RevisionManagerClientSideTest extends RevisionManagerTest
{
  @Override
  protected InternalCDORevisionManager getRevisionManager(InternalRepository repository, InternalCDOSession session)
  {
    return session.getRevisionManager();
  }

  @Override
  protected String getLocation()
  {
    return "Client";
  }

  @Override
  protected void dumpCache(CDOBranchPoint branchPoint)
  {
    dumpRevisions("ServerCache", repository.getRevisionManager().getCache().getAllRevisions());
    super.dumpCache(branchPoint);
  }

  @Override
  protected InternalCDORevision getRevision(CDOBranch branch, long timeStamp)
  {
    branch = session.getBranchManager().getBranch(branch.getID()); // Make sure that the client-side branch is used!
    return super.getRevision(branch, timeStamp);
  }
}

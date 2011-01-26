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
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
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
    BranchingTest.dump("ServerCache", repository.getRevisionManager().getCache().getAllRevisions());
    super.dumpCache(branchPoint);
  }
}

/*
 * Copyright (c) 2011-2013, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class Bugzilla_315043_Test extends AbstractCDOTest
{
  @Requires(IRepositoryConfig.CAPABILITY_AUDITING)
  public void testReloadingRevisions() throws Exception
  {
    final String RESOURCE_NAME = "resource";

    Set<CDOID> ids = new HashSet<>();
    long timeStampOfHoleCommit;

    CDOSession initialSession = openSession();
    {
      // create model history
      CDOTransaction openTransaction = initialSession.openTransaction();
      CDOResource resource = openTransaction.getOrCreateResource(getResourcePath(RESOURCE_NAME));

      // creating initial commit
      Company createdCompany = getModel1Factory().createCompany();
      createdCompany.setName("CompanyTesting");
      createdCompany.setCity("City");
      createdCompany.setStreet("Street");

      resource.getContents().add(createdCompany);

      openTransaction.commit();

      // collect id's
      for (TreeIterator<EObject> allContents = resource.getAllContents(); allContents.hasNext();)
      {
        CDOObject next = CDOUtil.getCDOObject(allContents.next());
        ids.add(next.cdoID());
      }

      // making holes - detaching
      List<EObject> contents = new ArrayList<>(resource.getContents());
      for (int i = 0; i < contents.size(); i++)
      {
        EcoreUtil.delete(contents.get(i), true);
      }

      timeStampOfHoleCommit = openTransaction.commit().getTimeStamp();
    }

    // check when locally cached elements are availabe
    checkRevisionsOnGivenSession(ids, timeStampOfHoleCommit, 2, initialSession);

    // turn of revision download by timestamp
    checkRevisionsOnGivenSession(ids, -1, 2, initialSession);
    initialSession.close();

    checkRevisions(ids, timeStampOfHoleCommit, 2);

    // turn of revision download by timestamp
    checkRevisions(ids, -1, 2);

    // clear caches
    clearCache(getRepository().getRevisionManager());
    checkRevisions(ids, timeStampOfHoleCommit, 2);

    // turn of revision download by timestamp
    clearCache(getRepository().getRevisionManager());
    checkRevisions(ids, -1, 2);
  }

  private void checkRevisions(Set<CDOID> ids, long timeStampOfHoleCommit, int version)
  {
    CDOSession session = openSession();
    checkRevisionsOnGivenSession(ids, timeStampOfHoleCommit, version, session);
    session.close();
  }

  private void checkRevisionsOnGivenSession(Set<CDOID> ids, long timeStampOfHoleCommit, int version, CDOSession session)
  {
    CDORevisionManager revisionManager = session.getRevisionManager();
    CDOBranch mainBranch = session.getBranchManager().getMainBranch();
    CDOBranchPoint branchPoint = mainBranch.getPoint(timeStampOfHoleCommit);
    CDOBranchVersion branchVersion = mainBranch.getVersion(version);

    for (Iterator<CDOID> it = ids.iterator(); it.hasNext();)
    {
      CDOID next = it.next();
      if (timeStampOfHoleCommit != -1)
      {
        CDORevision revision = revisionManager.getRevision(next, branchPoint, CDORevision.DEPTH_NONE, 0, true);
        assertNull(revision);
      }

      CDORevision revision = revisionManager.getRevisionByVersion(next, branchVersion, CDORevision.DEPTH_NONE, true);
      assertInstanceOf(DetachedCDORevision.class, revision);
    }
  }
}

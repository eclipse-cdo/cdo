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
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.net4j.CDOSession;
import org.eclipse.emf.cdo.spi.common.revision.DetachedCDORevision;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
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

import junit.framework.Assert;

/**
 * @author Eike Stepper
 */
public class Bugzilla_315043_Test extends AbstractCDOTest
{
  public void testReloadingRevisions() throws Exception
  {
    skipUnlessAuditing();

    final String RESOURCE_NAME = "resource";

    Set<CDOID> cdoid;
    long timeStampOfHoleCommit;

    CDOSession initialSession = (CDOSession)openSession();
    {
      // create model history
      CDOTransaction openTransaction = initialSession.openTransaction();
      CDOResource resource = openTransaction.getOrCreateResource(RESOURCE_NAME);

      // creating initial commit
      Company createdCompany = getModel1Factory().createCompany();
      createdCompany.setName("CompanyTesting");
      createdCompany.setCity("City");
      createdCompany.setStreet("Street");

      resource.getContents().add(createdCompany);

      openTransaction.commit();

      // collect id's
      cdoid = new HashSet<CDOID>();
      for (TreeIterator<EObject> allContents = resource.getAllContents(); allContents.hasNext();)
      {
        CDOObject next = CDOUtil.getCDOObject(allContents.next());
        cdoid.add(next.cdoID());
      }

      // making holes - detaching
      List<EObject> contents = new ArrayList<EObject>(resource.getContents());
      for (int i = 0; i < contents.size(); i++)
      {
        EcoreUtil.delete(contents.get(i), true);
      }

      timeStampOfHoleCommit = openTransaction.commit().getTimeStamp();
    }

    // check when locally cached elements are availabe
    checkRevisionsOnGivenSession(cdoid, timeStampOfHoleCommit, 2, initialSession);
    // turn of revision download by timestamp
    checkRevisionsOnGivenSession(cdoid, -1, 2, initialSession);
    initialSession.close();

    checkRevisions(cdoid, timeStampOfHoleCommit, 2);
    // turn of revision download by timestamp
    checkRevisions(cdoid, -1, 2);

    // clear caches

    clearServerRevisionCache();
    checkRevisions(cdoid, timeStampOfHoleCommit, 2);
    // turn of revision download by timestamp
    clearServerRevisionCache();
    checkRevisions(cdoid, -1, 2);
  }

  private void checkRevisions(Set<CDOID> cdoid, long timeStampOfHoleCommit, int version)
  {
    org.eclipse.emf.cdo.session.CDOSession openSession = openSession();
    checkRevisionsOnGivenSession(cdoid, timeStampOfHoleCommit, version, openSession);
    openSession.close();
  }

  private void checkRevisionsOnGivenSession(Set<CDOID> cdoid, long timeStampOfHoleCommit, int version,
      org.eclipse.emf.cdo.session.CDOSession openSession)
  {
    CDORevisionManager revisionManager = openSession.getRevisionManager();
    for (Iterator<CDOID> it = cdoid.iterator(); it.hasNext();)
    {

      CDOID next = it.next();
      if (timeStampOfHoleCommit != -1)
      {
        CDORevision revisionByTimestamp = revisionManager.getRevision(next, openSession.getBranchManager()
            .getMainBranch().getPoint(timeStampOfHoleCommit), CDORevision.DEPTH_NONE, 0, true);

        Assert.assertNull(revisionByTimestamp);
      }

      CDORevision revisionByVersion = revisionManager.getRevisionByVersion(next, openSession.getBranchManager()
          .getMainBranch().getVersion(version), CDORevision.DEPTH_NONE, true);

      assertEquals("Revisions is detached", true, revisionByVersion instanceof DetachedCDORevision);

    }
  }

  private void clearServerRevisionCache()
  {
    InternalRepository repository = getScenario().getRepositoryConfig()
        .getRepository(IRepositoryConfig.REPOSITORY_NAME);
    clearCache(repository.getRevisionManager());
  }
}
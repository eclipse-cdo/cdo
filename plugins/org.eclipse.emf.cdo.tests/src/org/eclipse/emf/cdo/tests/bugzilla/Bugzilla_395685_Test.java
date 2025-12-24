/*
 * Copyright (c) 2016 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.Requires;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.List;

/**
 * Bug 395685 - CDORevisionDelta.getOldValue() always returns CDOSetFeatureDelta.UNSPECIFIED
 *
 * @author Eike Stepper
 */
@Requires(IRepositoryConfig.CAPABILITY_AUDITING)
public class Bugzilla_395685_Test extends AbstractCDOTest
{
  private static final EAttribute FEATURE = EcorePackage.Literals.ENAMED_ELEMENT__NAME;

  public void testCorrectOldValue() throws Exception
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction = session1.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("model1"));

    EClass o = EcoreFactory.eINSTANCE.createEClass();
    o.setName("A");
    resource.getContents().add(o);
    transaction.commit();

    o.setName("B");
    long commitTime = transaction.commit().getTimeStamp();
    transaction.close();

    checkCommitInfo(session1, commitTime);
    session1.close();

    CDOSession session2 = openSession();
    checkCommitInfo(session2, commitTime);
    session2.close();
  }

  private void checkCommitInfo(CDOSession session, long commitTime)
  {
    CDOCommitInfoManager commitInfoManager = session.getCommitInfoManager();
    CDOCommitInfo commitInfo = commitInfoManager.getCommitInfo(commitTime);
    List<CDORevisionKey> revisionDeltas = commitInfo.getChangedObjects();
    assertEquals(1, revisionDeltas.size());

    CDORevisionDelta revisionDelta = (CDORevisionDelta)revisionDeltas.get(0);
    CDOSetFeatureDelta featureDelta = (CDOSetFeatureDelta)revisionDelta.getFeatureDelta(FEATURE);
    assertEquals("B", featureDelta.getValue());
    assertEquals("A", featureDelta.getOldValue());
  }
}

/*
 * Copyright (c) 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContained;
import org.eclipse.emf.cdo.tests.model4.SingleNonContainedElement;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.CommitIntegrityException;

import org.eclipse.emf.internal.cdo.util.CommitIntegrityCheck;

import org.eclipse.emf.spi.cdo.CDOStore;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction.InternalCDOCommitContext;

import java.util.Arrays;
import java.util.HashSet;

/**
 * @author Eike Stepper
 */
public class Bugzilla_362293_Test extends AbstractCDOTest
{
  public void testNewSingle() throws CommitException
  {
    CDOSession session1 = openSession();
    CDOTransaction transaction1 = session1.openTransaction();
    CDOResource resource = transaction1.createResource(getResourcePath("test"));
    SingleNonContainedElement singleNonContainedElement = getModel4Factory().createSingleNonContainedElement();
    RefSingleNonContained refSingleNonContained = getModel4Factory().createRefSingleNonContained();
    resource.getContents().add(singleNonContainedElement);
    resource.getContents().add(refSingleNonContained);
    transaction1.commit();

    // STEP 1 Setting parent feature to point REF
    CDORevision cdoRevision = CDOUtil.getCDOObject(singleNonContainedElement).cdoRevision();
    // here is real Java NULL
    assertEquals(null, cdoRevision.data().get(getModel4Package().getSingleNonContainedElement_Parent(), CDOStore.NO_INDEX));
    singleNonContainedElement.setParent(refSingleNonContained);
    transaction1.setCommittables(new HashSet<>(Arrays.asList(singleNonContainedElement, refSingleNonContained)));
    checkPartialCommitIntegrity((InternalCDOTransaction)transaction1);
    transaction1.commit();

    // STEP 2 Setting parent feature to point NULL
    CDOSession session2 = openSession();
    InternalCDOTransaction transaction2 = (InternalCDOTransaction)session2.openTransaction();
    SingleNonContainedElement singleNonContainedElement2 = (SingleNonContainedElement)CDOUtil
        .getEObject(transaction2.getObject(CDOUtil.getCDOObject(singleNonContainedElement).cdoID()));
    RefSingleNonContained refSingleNonContained2 = singleNonContainedElement2.getParent();
    singleNonContainedElement2.setParent(null);
    cdoRevision = CDOUtil.getCDOObject(singleNonContainedElement2).cdoRevision();
    // here is real Java NULL after setting
    assertEquals(null, cdoRevision.data().get(getModel4Package().getSingleNonContainedElement_Parent(), CDOStore.NO_INDEX));
    transaction2.setCommittables(new HashSet<>(Arrays.asList(singleNonContainedElement2, refSingleNonContained2)));
    checkPartialCommitIntegrity(transaction2);
    transaction2.commit();
    session2.close();

    // STEP 3 Setting parent feature to point REF
    session2 = openSession();
    transaction2 = (InternalCDOTransaction)session2.openTransaction();
    singleNonContainedElement2 = (SingleNonContainedElement)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(singleNonContainedElement).cdoID()));
    refSingleNonContained2 = (RefSingleNonContained)CDOUtil.getEObject(transaction2.getObject(CDOUtil.getCDOObject(refSingleNonContained).cdoID()));

    cdoRevision = CDOUtil.getCDOObject(singleNonContainedElement2).cdoRevision();
    // from server loaded revision has CDO NULL ID, which has meaning of the same Java NULL. Identical situation when in
    // STEP 1
    assertEquals(CDOID.NULL, cdoRevision.data().get(getModel4Package().getSingleNonContainedElement_Parent(), CDOStore.NO_INDEX));
    singleNonContainedElement2.eUnset(getModel4Package().getSingleNonContainedElement_Parent());
    transaction2.setCommittables(new HashSet<>(Arrays.asList(singleNonContainedElement2, refSingleNonContained2)));
    checkPartialCommitIntegrity(transaction2);
    transaction2.commit();

    session2.close();
  }

  private void checkPartialCommitIntegrity(InternalCDOTransaction tx) throws CommitIntegrityException
  {
    InternalCDOCommitContext ctx = tx.createCommitContext();
    new CommitIntegrityCheck(ctx, CommitIntegrityCheck.Style.EXCEPTION_FAST).check();
  }
}

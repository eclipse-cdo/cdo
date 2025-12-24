/*
 * Copyright (c) 2013 Esteban Dugueperoux (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Esteban Dugueperoux - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.ContainedElementNoOpposite;
import org.eclipse.emf.cdo.tests.model4.RefSingleNonContainedNPL;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.transaction.TransactionalEditingDomain;

/**
 * Ensures that no object is registered twice when loading a Native or Legacy model through an EditingDomain.
 *
 * @author Esteban Dugueperoux
 */
public class Bugzilla_399956_Test extends AbstractCDOTest
{
  public void testDetachment() throws Exception
  {
    // Step 1: initialize test model and commit
    RefSingleNonContainedNPL refSingleNonContainedNPL = getModel4Factory().createRefSingleNonContainedNPL();
    ContainedElementNoOpposite containedElementNoOpposite = getModel4Factory().createContainedElementNoOpposite();
    refSingleNonContainedNPL.setElement(containedElementNoOpposite);

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource cdoResourceA = transaction.createResource(getResourcePath("test1"));
    CDOResource cdoResourceB = transaction.createResource(getResourcePath("test2"));

    cdoResourceA.getContents().add(containedElementNoOpposite);
    cdoResourceB.getContents().add(refSingleNonContainedNPL);
    transaction.commit();

    transaction.close();
    session.close();

    // Step 2: load the model through an editing domain
    // this should not lead to any exception (without fix this thrown a 'Different object was registered for ID'
    // IllegalStateException)
    session = openSession();
    TransactionalEditingDomain domain = TransactionalEditingDomain.Factory.INSTANCE.createEditingDomain();
    transaction = session.openTransaction(domain.getResourceSet());
    cdoResourceB = transaction.getResource(getResourcePath("test2"));
    cdoResourceB.setTrackingModification(true);
    refSingleNonContainedNPL = (RefSingleNonContainedNPL)cdoResourceB.getContents().get(0);
    refSingleNonContainedNPL.getElement();
  }
}

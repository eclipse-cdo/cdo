/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.delta.CDOListFeatureDeltaImpl;
import org.eclipse.emf.cdo.protocol.revision.delta.CDOAddFeatureDelta;
import org.eclipse.emf.cdo.protocol.revision.delta.CDOClearFeatureDelta;
import org.eclipse.emf.cdo.protocol.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.protocol.revision.delta.CDOSetFeatureDelta;
import org.eclipse.emf.cdo.tests.model1.Category;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.tests.model1.Model1Factory;
import org.eclipse.emf.cdo.tests.model1.Model1Package;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.internal.cdo.InternalCDOObject;

/**
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=201266
 * @author Simon McDuff
 */
public class RevisionDeltaTest extends AbstractCDOTest
{
  public void testBasicRevisionDelta() throws Exception
  {
    CDOSession session = CDOUtil.openSession(getConnector(), REPOSITORY_NAME);
    session.getPackageRegistry().putEPackage(Model1Package.eINSTANCE);

    CDOTransaction transaction = session.openTransaction();
    CDOResource resource1 = transaction.createResource("/test1");

    Company company1 = Model1Factory.eINSTANCE.createCompany();
    Category category1 = Model1Factory.eINSTANCE.createCategory();

    resource1.getContents().add(company1);
    company1.getCategories().add(category1);

    company1.setName("TEST");
    CDORevisionImpl rev1 = getCopyCDORevision(company1);

    // Test simple attributes
    company1.setName("TEST3");
    CDORevisionImpl rev2 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta = rev2.createDelta(rev1);
    assertEquals(1, revisionDelta.getFeatureDeltas().size());
    CDOSetFeatureDelta setDelta = (CDOSetFeatureDelta)revisionDelta.getFeatureDeltas().get(0);
    assertEquals("TEST3", setDelta.getValue());

    // Test List clear
    company1.getCategories().clear();
    CDORevisionImpl rev3 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta3 = rev3.createDelta(rev2);
    assertEquals(1, revisionDelta3.getFeatureDeltas().size());
    CDOListFeatureDeltaImpl delta3List = (CDOListFeatureDeltaImpl)revisionDelta3.getFeatureDeltas().get(0);

    assertEquals(1, delta3List.getListChanges().size());
    assertEquals(true, delta3List.getListChanges().get(0) instanceof CDOClearFeatureDelta);

    // Test List Add
    for (int i = 0; i < 5; i++)
    {
      Category category = Model1Factory.eINSTANCE.createCategory();
      company1.getCategories().add(category);
    }
    CDORevisionImpl rev4 = getCopyCDORevision(company1);

    CDORevisionDelta revisionDelta4 = rev4.createDelta(rev3);
    assertEquals(1, revisionDelta4.getFeatureDeltas().size());
    CDOListFeatureDeltaImpl delta4List = (CDOListFeatureDeltaImpl)revisionDelta4.getFeatureDeltas().get(0);

    assertEquals(5, delta4List.getListChanges().size());
    assertEquals(true, delta4List.getListChanges().get(0) instanceof CDOAddFeatureDelta);
    transaction.rollback(true);

    transaction.close();
    session.close();
  }

  private CDORevisionImpl getCopyCDORevision(Object object)
  {
    return new CDORevisionImpl((CDORevisionImpl)((InternalCDOObject)object).cdoRevision());
  }
}

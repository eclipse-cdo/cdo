/*
 * Copyright (c) 2013, 2016 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA) - additional tests for REMOVE_MANY notifications
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.delta.CDOFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDOListFeatureDelta;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDOClearFeatureDeltaImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORemoveFeatureDeltaImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model5.GenListOfInt;
import org.eclipse.emf.cdo.tests.model6.UnorderedList;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.util.Arrays;
import java.util.List;

/**
 * Bug 397822: [Legacy] REMOVE_MANY events are not transferred correctly to CDORevision
 *
 * @author Eike Stepper
 */
public class Bugzilla_397822_Test extends AbstractCDOTest
{
  public void testRemoveAll_containment() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/resource1"));

    UnorderedList elem1 = getModel6Factory().createUnorderedList();
    UnorderedList elem2 = getModel6Factory().createUnorderedList();
    UnorderedList elem3 = getModel6Factory().createUnorderedList();
    UnorderedList elem4 = getModel6Factory().createUnorderedList();
    UnorderedList elem5 = getModel6Factory().createUnorderedList();

    UnorderedList object = getModel6Factory().createUnorderedList();
    CDOObject cdoObject = CDOUtil.getCDOObject(object);

    EList<UnorderedList> list = object.getContained();
    list.addAll(Arrays.asList(elem1, elem2, elem3, elem4, elem5));

    resource.getContents().add(object);
    transaction.commit();

    list.removeAll(Arrays.asList(elem2, elem4));

    CDOID id = cdoObject.cdoID();
    CDORevisionDelta revisionDelta = transaction.getRevisionDeltas().get(id);
    EReference reference = getModel6Package().getUnorderedList_Contained();

    assertRevisionDeltaContainsListChanges(revisionDelta, reference //
    // removal of elem4 at index 3
        , new CDORemoveFeatureDeltaImpl(reference, 3)

        // removal of elem2 at index 1
        , new CDORemoveFeatureDeltaImpl(reference, 1)

    // TODO Clarify where the following delta is supposed to come from (see bug 390283)
    // // elem5 (at index 3 after the two removals) takes elem2's place at index 1
    // , new CDOMoveFeatureDeltaImpl(reference, 1, 2)

    );
  }

  public void testRemoveAll_attribute() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/resource1"));

    GenListOfInt intHolder = getModel5Factory().createGenListOfInt();
    CDOObject cdoObject = CDOUtil.getCDOObject(intHolder);

    EList<Integer> list = intHolder.getElements();
    list.addAll(Arrays.asList(1, 2, 3, 4, 5));

    resource.getContents().add(intHolder);
    transaction.commit();

    list.removeAll(Arrays.asList(2, 4));

    CDOID id = cdoObject.cdoID();
    CDORevisionDelta revisionDelta = transaction.getRevisionDeltas().get(id);
    EAttribute attribute = getModel5Package().getGenListOfInt_Elements();

    assertRevisionDeltaContainsListChanges(revisionDelta, attribute //
    // removal of '4' at index 3
        , new CDORemoveFeatureDeltaImpl(attribute, 3)

        // removal of '2' at index 1
        , new CDORemoveFeatureDeltaImpl(attribute, 1)

    );
  }

  public void testClear_attribute() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/resource1"));

    GenListOfInt intHolder = getModel5Factory().createGenListOfInt();
    CDOObject cdoObject = CDOUtil.getCDOObject(intHolder);

    EList<Integer> list = intHolder.getElements();
    list.addAll(Arrays.asList(1, 2, 3, 4, 5));

    resource.getContents().add(intHolder);
    transaction.commit();

    list.clear();

    CDOID id = cdoObject.cdoID();
    CDORevisionDelta revisionDelta = transaction.getRevisionDeltas().get(id);
    EAttribute attribute = getModel5Package().getGenListOfInt_Elements();

    assertRevisionDeltaContainsListChanges(revisionDelta, attribute //
    // entire list was cleared
        , new CDOClearFeatureDeltaImpl(attribute)

    );
  }

  private void assertRevisionDeltaContainsListChanges(CDORevisionDelta revisionDelta, EStructuralFeature feature, CDOFeatureDelta... expectedListChanges)
  {
    CDOFeatureDelta featureDelta = revisionDelta.getFeatureDelta(feature);

    assertInstanceOf(CDOListFeatureDelta.class, featureDelta);

    List<CDOFeatureDelta> listChanges = ((CDOListFeatureDelta)featureDelta).getListChanges();
    CDOFeatureDelta[] actualListChanges = listChanges.toArray(new CDOFeatureDelta[listChanges.size()]);
    assertEquals(Arrays.deepToString(expectedListChanges), Arrays.deepToString(actualListChanges));
  }
}

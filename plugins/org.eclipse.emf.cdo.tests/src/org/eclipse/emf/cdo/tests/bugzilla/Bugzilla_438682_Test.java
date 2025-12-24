/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Alex Lagarde - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedElement;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.util.ConcurrentAccessException;

import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

/**
 * Tests ensuring that unsetting features works correctly.
 *
 * @author Alex Lagarde <alex.lagarde@obeo.fr>
 */
public class Bugzilla_438682_Test extends AbstractCDOTest
{
  private CDOTransaction transaction;

  private RefMultiNonContainedUnsettable rootUnsettable;

  private RefMultiNonContained rootNonUnsettable;

  @Override
  protected void doSetUp() throws Exception
  {
    super.doSetUp();

    // Initialize semantic model and commit
    ResourceSet resourceSet = new ResourceSetImpl();
    Registry registry = resourceSet.getResourceFactoryRegistry();
    registry.getExtensionToFactoryMap().put("model1", new XMIResourceFactoryImpl());

    MultiNonContainedUnsettableElement child1 = getModel4Factory().createMultiNonContainedUnsettableElement();
    rootUnsettable = getModel4Factory().createRefMultiNonContainedUnsettable();
    rootUnsettable.getElements().add(child1);

    MultiNonContainedElement child2 = getModel4Factory().createMultiNonContainedElement();
    rootNonUnsettable = getModel4Factory().createRefMultiNonContained();
    rootNonUnsettable.getElements().add(child2);
    CDOSession session = openSession();
    transaction = session.openTransaction(resourceSet);
    final CDOResource resource = transaction.createResource(getResourcePath("r1"));
    resource.getContents().add(child1);
    resource.getContents().add(child2);
    resource.getContents().add(rootUnsettable);
    resource.getContents().add(rootNonUnsettable);
    transaction.commit();
  }

  public void testUnsetUnsettableFeatureAndCommit() throws ConcurrentAccessException, CommitException
  {
    // Unsets an unsettable feature: commit should succeed
    rootUnsettable.eUnset(getModel4Package().getRefMultiNonContainedUnsettable_Elements());
    transaction.commit();
  }

  public void testUnsetNonUnsettableFeatureAndCommit() throws ConcurrentAccessException, CommitException
  {
    // Unsets an non-unsettable feature: what is expected here ? Depending on the used StoreAccessor, a
    // CDOFeatureDeltaVisitor will be used and the CDOUnsetFeatureDelta will be tested if unsettable and returning an
    // exception if not unsettable.
    rootNonUnsettable.eUnset(getModel4Package().getRefMultiNonContained_Elements());
    transaction.commit();
  }
}

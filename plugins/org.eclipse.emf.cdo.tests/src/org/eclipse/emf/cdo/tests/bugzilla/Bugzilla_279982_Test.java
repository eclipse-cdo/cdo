/*
 * Copyright (c) 2009-2012, 2014, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IModelConfig;
import org.eclipse.emf.cdo.tests.model4.GenRefMultiNonContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleContained;
import org.eclipse.emf.cdo.tests.model4.GenRefSingleNonContained;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOStaleReferencePolicy;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

/**
 * Deadlock in CDOView
 * <p>
 * See bug 279982
 *
 * @author Simon McDuff
 */
public class Bugzilla_279982_Test extends AbstractCDOTest
{
  // As log as there is no getter interception, stale reference cannot be detected for legacy
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testBugzilla_279982_Single() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource res = tx.getOrCreateResource(getResourcePath("/resource1"));
    tx.options().setStaleReferencePolicy(CDOStaleReferencePolicy.PROXY);
    GenRefSingleContained container = getModel4Factory().createGenRefSingleContained();
    GenRefSingleNonContained referencer = getModel4Factory().createGenRefSingleNonContained();
    GenRefSingleNonContained contained = getModel4Factory().createGenRefSingleNonContained();
    container.setElement(contained);
    referencer.setElement(contained);
    res.getContents().add(container);
    res.getContents().add(referencer);
    tx.commit();
    container.setElement(null);
    tx.commit();

    assertNull(container.getElement());
    EObject element = referencer.getElement();
    assertNotNull(element);

    assertEquals(true, CDOUtil.isStaleObject(element));

    // As of 4.4 should not fail anymore. CDOStaleReferencePolicy.PROXY is more tolerant now.
    element.eContainer();

    tx.options().setStaleReferencePolicy(CDOStaleReferencePolicy.EXCEPTION);

    try
    {
      referencer.getElement();
      fail("Should fail");
    }
    catch (ObjectNotFoundException ex)
    {
      // ignore
    }
    catch (Exception ex)
    {
      fail("Should have an ObjectNotFoundException");
    }

    CDOUtil.cleanStaleReference(referencer, getModel4Package().getGenRefSingleNonContained_Element());
    assertNull(referencer.getElement());
    tx.commit();

    clearCache(session.getRevisionManager());

    // Verification that the commit is good
    tx = session.openTransaction();
    res = tx.getOrCreateResource(getResourcePath("/resource1"));
    referencer = (GenRefSingleNonContained)res.getContents().get(1);
    assertNull(referencer.getElement());
  }

  // As long as there is no getter interception, stale reference cannot be detected for legacy
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testBugzilla_279982_Multi() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.getOrCreateResource(getResourcePath("/resource1"));
    transaction.options().setStaleReferencePolicy(CDOStaleReferencePolicy.PROXY);

    GenRefSingleContained container = getModel4Factory().createGenRefSingleContained();
    GenRefMultiNonContained referencer = getModel4Factory().createGenRefMultiNonContained();
    GenRefSingleNonContained contained = getModel4Factory().createGenRefSingleNonContained();
    container.setElement(contained);
    referencer.getElements().add(contained);

    resource.getContents().add(container);
    resource.getContents().add(referencer);
    transaction.commit();

    container.setElement(null);
    transaction.commit();
    assertNull(container.getElement());

    EObject proxy = referencer.getElements().get(0);
    assertNotNull(proxy);
    assertEquals(true, CDOUtil.isStaleObject(proxy));

    transaction.options().setStaleReferencePolicy(CDOStaleReferencePolicy.EXCEPTION);

    try
    {
      referencer.getElements().get(0);
      fail("Should fail");
    }
    catch (ObjectNotFoundException ex)
    {
      // Success
    }

    EReference genRefMultiNonContained_Elements = getModel4Package().getGenRefMultiNonContained_Elements();
    CDOUtil.cleanStaleReference(referencer, genRefMultiNonContained_Elements, 0);
    assertEquals(0, referencer.getElements().size());
    transaction.commit();

    clearCache(session.getRevisionManager());

    // Verification that the commit is good
    transaction = session.openTransaction();
    resource = transaction.getOrCreateResource(getResourcePath("/resource1"));
    referencer = (GenRefMultiNonContained)resource.getContents().get(1);
    assertEquals(0, referencer.getElements().size());
  }

  // As log as there is no getter interception, stale reference cannot be detected for legacy
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testBugzilla_279982_Multi_RevisionPrefetchingPolicy() throws Exception
  {
    CDOSession session = openSession();
    {
      CDOTransaction tx = session.openTransaction();
      CDOResource res = tx.getOrCreateResource(getResourcePath("/resource1"));
      // tx.options().setUnresolveableObjectToNullEnabled(true);
      GenRefSingleContained container = getModel4Factory().createGenRefSingleContained();
      GenRefMultiNonContained referencer = getModel4Factory().createGenRefMultiNonContained();
      GenRefSingleNonContained contained = getModel4Factory().createGenRefSingleNonContained();
      GenRefSingleNonContained contained2 = getModel4Factory().createGenRefSingleNonContained();
      container.setElement(contained);
      referencer.getElements().add(contained);
      referencer.getElements().add(contained2);
      res.getContents().add(container);
      res.getContents().add(referencer);
      res.getContents().add(contained2);
      tx.commit();
      res.getContents().remove(contained2);
      tx.commit();
    }

    clearCache(session.getRevisionManager());

    CDOTransaction tx = session.openTransaction();
    CDOResource res = tx.getOrCreateResource(getResourcePath("/resource1"));

    tx.options().setRevisionPrefetchingPolicy(CDOUtil.createRevisionPrefetchingPolicy(100));
    tx.options().setStaleReferencePolicy(CDOStaleReferencePolicy.EXCEPTION);
    GenRefMultiNonContained reference = (GenRefMultiNonContained)res.getContents().get(1);

    assertNotNull(reference.getElements().get(0));

    try
    {
      assertNotNull(reference.getElements().get(1));
      fail("Should fail");
    }
    catch (ObjectNotFoundException ex)
    {
      // ignore
    }
    catch (Exception ex)
    {
      fail("Should have an ObjectNotFoundException");
    }

    CDOUtil.cleanStaleReference(reference, getModel4Package().getGenRefMultiNonContained_Elements(), 0);
    assertEquals(1, reference.getElements().size());
    tx.commit();

    clearCache(session.getRevisionManager());

    // Verification that the commit is good
    tx = session.openTransaction();
    res = tx.getOrCreateResource(getResourcePath("/resource1"));
    reference = (GenRefMultiNonContained)res.getContents().get(1);
    assertEquals(1, reference.getElements().size());
  }
}

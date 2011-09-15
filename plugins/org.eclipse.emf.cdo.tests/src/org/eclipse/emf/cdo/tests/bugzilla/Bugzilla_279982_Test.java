/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
    GenRefSingleNonContained reference = getModel4Factory().createGenRefSingleNonContained();
    GenRefSingleNonContained contained = getModel4Factory().createGenRefSingleNonContained();
    container.setElement(contained);
    reference.setElement(contained);
    res.getContents().add(container);
    res.getContents().add(reference);
    tx.commit();
    container.setElement(null);
    tx.commit();

    assertNull(container.getElement());
    EObject element = reference.getElement();
    assertNotNull(element);

    try
    {
      assertEquals(true, CDOUtil.isStaleObject(element));
      element.eContainer();
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

    tx.options().setStaleReferencePolicy(CDOStaleReferencePolicy.EXCEPTION);

    try
    {
      reference.getElement();
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

    CDOUtil.cleanStaleReference(reference, getModel4Package().getGenRefSingleNonContained_Element());
    assertNull(reference.getElement());
    tx.commit();

    clearCache(session.getRevisionManager());

    // Verification that the commit is good
    tx = session.openTransaction();
    res = tx.getOrCreateResource(getResourcePath("/resource1"));
    reference = (GenRefSingleNonContained)res.getContents().get(1);
    assertNull(reference.getElement());
  }

  // As log as there is no getter interception, stale reference cannot be detected for legacy
  @Skips(IModelConfig.CAPABILITY_LEGACY)
  public void testBugzilla_279982_Multi() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource res = tx.getOrCreateResource(getResourcePath("/resource1"));
    tx.options().setStaleReferencePolicy(CDOStaleReferencePolicy.PROXY);
    GenRefSingleContained container = getModel4Factory().createGenRefSingleContained();
    GenRefMultiNonContained reference = getModel4Factory().createGenRefMultiNonContained();
    GenRefSingleNonContained contained = getModel4Factory().createGenRefSingleNonContained();
    container.setElement(contained);
    reference.getElements().add(contained);
    res.getContents().add(container);
    res.getContents().add(reference);
    tx.commit();
    container.setElement(null);
    tx.commit();

    assertNull(container.getElement());
    assertNotNull(reference.getElements().get(0));

    tx.options().setStaleReferencePolicy(CDOStaleReferencePolicy.EXCEPTION);

    try
    {
      reference.getElements().get(0);
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
    assertEquals(0, reference.getElements().size());
    tx.commit();

    clearCache(session.getRevisionManager());

    // Verification that the commit is good
    tx = session.openTransaction();
    res = tx.getOrCreateResource(getResourcePath("/resource1"));
    reference = (GenRefMultiNonContained)res.getContents().get(1);
    assertEquals(0, reference.getElements().size());
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
      GenRefMultiNonContained reference = getModel4Factory().createGenRefMultiNonContained();
      GenRefSingleNonContained contained = getModel4Factory().createGenRefSingleNonContained();
      GenRefSingleNonContained contained2 = getModel4Factory().createGenRefSingleNonContained();
      container.setElement(contained);
      reference.getElements().add(contained);
      reference.getElements().add(contained2);
      res.getContents().add(container);
      res.getContents().add(reference);
      res.getContents().add(contained2);
      tx.commit();
      res.getContents().remove(contained2);
      tx.commit();
    }

    clearCache(session.getRevisionManager());

    CDOTransaction tx = session.openTransaction();
    CDOResource res = tx.getOrCreateResource(getResourcePath("/resource1"));

    tx.options().setRevisionPrefetchingPolicy(CDOUtil.createRevisionPrefetchingPolicy(100));
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

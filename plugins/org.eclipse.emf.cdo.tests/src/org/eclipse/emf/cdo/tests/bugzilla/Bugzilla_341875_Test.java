/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.RefSingleContained;
import org.eclipse.emf.cdo.tests.model4.SingleContainedElement;
import org.eclipse.emf.cdo.tests.model4.model4Factory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.ecore.EReference;

/**
 * @author Caspar De Groot
 */
public class Bugzilla_341875_Test extends AbstractCDOTest
{
  private final model4Factory factory = getModel4Factory();

  private EReference ref_elToPar = getModel4Package().getSingleContainedElement_Parent();

  private EReference ref_parToEl = getModel4Package().getRefSingleContained_Element();

  @Override
  protected void doSetUp() throws Exception
  {
    ref_parToEl.setResolveProxies(true);
    super.doSetUp();
  }

  @Override
  protected void doTearDown() throws Exception
  {
    ref_parToEl.setResolveProxies(false);
    super.doTearDown();
  }

  public void test()
  {
    SingleContainedElement element = factory.createSingleContainedElement();

    RefSingleContained parent = factory.createRefSingleContained();
    parent.setElement(element);

    CDOSession session = openSession();
    CDOTransaction tx = session.openTransaction();
    CDOResource resource = tx.createResource(getResourcePath("test"));
    resource.getContents().add(parent);

    assertEquals(CDOState.NEW, CDOUtil.getCDOObject(parent).cdoState());
    assertEquals(CDOState.NEW, CDOUtil.getCDOObject(element).cdoState());

    element.eUnset(ref_elToPar);
    assertNull(parent.eGet(ref_parToEl));

    tx.close();
    session.close();
  }
}

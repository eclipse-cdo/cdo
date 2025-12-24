/*
 * Copyright (c) 2012, 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model6.Holdable;
import org.eclipse.emf.cdo.tests.model6.Holder;
import org.eclipse.emf.cdo.tests.model6.Thing;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

/**
 * Bug 392956: Tests the partial persistence of features.
 */
public class Bugzilla_392956_Test extends AbstractCDOTest
{
  public void testPartialPersistence() throws Exception
  {
    Holder rootHolder = createModel();

    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    Resource resource = transaction.getOrCreateResource(getResourcePath("model.model7"));
    resource.getContents().add(rootHolder);
    transaction.commit();

    // Make sure it's a clean retrieval
    transaction.close();
    transaction = session.openTransaction();

    resource = transaction.getResource(getResourcePath("model.model7"), true);
    rootHolder = (Holder)EcoreUtil.getObjectByType(resource.getContents(), getModel6Package().getHolder());

    // Only some values returned
    EList<Holdable> held = rootHolder.getHeld();
    assertEquals(2, held.size());
    assertThing("A", held.get(0));
    assertThing("C", held.get(1));

    held = ((Holder)EcoreUtil.getObjectByType(rootHolder.getOwned(), getModel6Package().getHolder())).getHeld();
    assertEquals(1, held.size());
    assertThing("B", held.get(0));
  }

  private Holder createModel()
  {
    Holder result = getModel6Factory().createHolder();
    result.setName("root");

    Holder nested = getModel6Factory().createHolder();
    nested.setName("nested");

    Thing a = getModel6Factory().createThing();
    a.setName("A");

    Thing b = getModel6Factory().createThing();
    b.setName("B");

    Thing c = getModel6Factory().createThing();
    c.setName("C");

    result.getOwned().add(a);
    result.getOwned().add(nested);
    nested.getOwned().add(b);
    result.getOwned().add(c);

    // The partially persisted feature
    result.getHeld().add(a);
    result.getHeld().add(b);
    result.getHeld().add(c);
    nested.getHeld().add(b);

    return result;
  }

  private void assertThing(String name, Holdable holdable)
  {
    assertInstanceOf(Thing.class, holdable);
    assertEquals(name, holdable.getName());
  }
}

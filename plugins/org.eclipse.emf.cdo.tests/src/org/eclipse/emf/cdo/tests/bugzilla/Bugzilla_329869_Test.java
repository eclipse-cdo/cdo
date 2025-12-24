/*
 * Copyright (c) 2010-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model4.MultiNonContainedUnsettableElement;
import org.eclipse.emf.cdo.tests.model4.RefMultiNonContainedUnsettable;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Martin Fluegge
 */
public class Bugzilla_329869_Test extends AbstractCDOTest
{
  public void testDuplicateID() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();

      Resource resource = transaction.createResource(getResourcePath("/test"));

      RefMultiNonContainedUnsettable parent = getModel4Factory().createRefMultiNonContainedUnsettable();
      MultiNonContainedUnsettableElement child = getModel4Factory().createMultiNonContainedUnsettableElement();

      parent.getElements().add(child);

      resource.getContents().add(parent);
      resource.getContents().add(child);
      transaction.commit();
      session.close();
    }

    {
      CDOSession session = openSession();
      CDOTransaction transaction2 = session.openTransaction();
      Resource resource2 = transaction2.getResource(getResourcePath("/test"));

      RefMultiNonContainedUnsettable parent = (RefMultiNonContainedUnsettable)resource2.getContents().get(0);
      MultiNonContainedUnsettableElement element = getModel4Factory().createMultiNonContainedUnsettableElement();
      element.setParent(parent);
      resource2.getContents().add(element);
      transaction2.commit();
    }
  }
}

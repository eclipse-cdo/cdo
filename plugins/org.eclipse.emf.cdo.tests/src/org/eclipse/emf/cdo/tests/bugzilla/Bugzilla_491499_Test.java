/*
 * Copyright (c) 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOObjectHandler;
import org.eclipse.emf.cdo.view.CDOView;

/**
 * Bug 491499 - CDOTransaction.postCommit fails to resolve direct resource
 *
 * @author Eike Stepper
 */
public class Bugzilla_491499_Test extends AbstractCDOTest
{
  public void testObjectOrderWithObjectHandler() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    CDOResource resource1 = transaction.createResource(getResourcePath("res1"));
    for (int i = 0; i < 5000; i++)
    {
      resource1.getContents().add(getModel1Factory().createCompany());
    }

    CDOResource resource2 = transaction.createResource(getResourcePath("res2"));
    resource2.getContents().addAll(resource1.getContents());

    transaction.addObjectHandler(new CDOObjectHandler()
    {
      @Override
      public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
      {
        if (newState == CDOState.CLEAN || newState == CDOState.NEW)
        {
          // Can lead to exception if not all committed objects are remapped!
          object.cdoResource();
        }
      }
    });

    transaction.commit();
  }
}

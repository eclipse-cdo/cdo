/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.protocol.id.CDOID;

/**
 * @author Eike Stepper
 */
public class ViewTest extends AbstractCDOTest
{
  public void testHasResource() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource("/test1");
      transaction.commit();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOView view = session.openView();
    assertEquals(true, view.hasResource("/test1"));
    assertEquals(false, view.hasResource("/test2"));
    session.close();
  }

  public void testGetOrCreateResource() throws Exception
  {
    CDOID id;
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource("/test1");
      transaction.commit();
      id = resource.cdoID();
      session.close();
    }

    CDOSession session = openModel1Session();
    CDOTransaction transaction = session.openTransaction();
    assertEquals(id, transaction.getOrCreateResource("/test1").cdoID());
    assertNotSame(id, transaction.getOrCreateResource("/test2").cdoID());
    session.close();
  }
}

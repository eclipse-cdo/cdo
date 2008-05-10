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
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.CDOTransaction;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.tests.mango.MangoFactory;
import org.eclipse.emf.cdo.tests.mango.MangoPackage;
import org.eclipse.emf.cdo.tests.mango.Value;
import org.eclipse.emf.cdo.tests.mango.ValueList;

/**
 * @author Eike Stepper
 */
public class MangoTest extends AbstractCDOTest
{
  private CDOSession openMangoSession()
  {
    CDOSession session = openSession();
    session.getPackageRegistry().putEPackage(MangoPackage.eINSTANCE);
    return session;
  }

  public void testCommitNew() throws Exception
  {
    CDOSession session = openMangoSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource("/test1");

    Value v1 = MangoFactory.eINSTANCE.createValue();
    v1.setName("v1");
    resource.getContents().add(v1);

    Value v2 = MangoFactory.eINSTANCE.createValue();
    v2.setName("v2");
    resource.getContents().add(v2);

    ValueList list = MangoFactory.eINSTANCE.createValueList();
    list.setName("List");
    list.getValues().add(v1);
    list.getValues().add(v2);
    resource.getContents().add(list);

    transaction.commit();
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, list.cdoState());
    assertEquals(CDOState.CLEAN, v1.cdoState());
    assertEquals(CDOState.CLEAN, v2.cdoState());
    session.close();
  }
}

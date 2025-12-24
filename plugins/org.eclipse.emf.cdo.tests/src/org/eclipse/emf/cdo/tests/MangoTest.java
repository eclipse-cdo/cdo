/*
 * Copyright (c) 2008-2012 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.mango.MangoValue;
import org.eclipse.emf.cdo.tests.mango.MangoValueList;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;

/**
 * @author Eike Stepper
 */
public class MangoTest extends AbstractCDOTest
{
  public void testCommitNew() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();
    CDOResource resource = transaction.createResource(getResourcePath("/test1"));

    MangoValue v1 = getMangoFactory().createMangoValue();
    v1.setName("v1");
    resource.getContents().add(v1);

    MangoValue v2 = getMangoFactory().createMangoValue();
    v2.setName("v2");
    resource.getContents().add(v2);

    MangoValueList list = getMangoFactory().createMangoValueList();
    list.setName("List");
    list.getValues().add(v1);
    list.getValues().add(v2);
    resource.getContents().add(list);

    transaction.commit();
    assertEquals(CDOState.CLEAN, resource.cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(list).cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(v1).cdoState());
    assertEquals(CDOState.CLEAN, CDOUtil.getCDOObject(v2).cdoState());
    session.close();
  }
}

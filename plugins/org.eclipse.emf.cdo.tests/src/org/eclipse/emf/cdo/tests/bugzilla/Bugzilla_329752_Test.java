/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.VAT;
import org.eclipse.emf.cdo.tests.model2.EnumListHolder;
import org.eclipse.emf.cdo.transaction.CDOTransaction;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * @author Martin Fluegge
 */
public class Bugzilla_329752_Test extends AbstractCDOTest
{
  public void testLoadContainedBeforeContainer() throws Exception
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    EnumListHolder enumListHolder = getModel2Factory().createEnumListHolder();
    EList<VAT> enumList = enumListHolder.getEnumList();
    enumList.add(VAT.VAT0);
    enumList.add(VAT.VAT15);

    String path = "/test";
    Resource resource = transaction.createResource(path);
    resource.getContents().add(enumListHolder);

    transaction.commit();

    CDOTransaction transaction2 = session.openTransaction();
    CDOResource resource2 = transaction2.getResource(path);

    EnumListHolder enumListHolder2 = (EnumListHolder)resource2.getContents().get(0);

    EList<VAT> enumList2 = enumListHolder2.getEnumList();
    assertEquals(2, enumList2.size());
    assertEquals(VAT.VAT0, enumList2.get(0));
    assertEquals(VAT.VAT15, enumList2.get(1));

    enumList2.add(VAT.VAT7);
    assertEquals(3, enumList2.size());
    transaction2.rollback();

    assertEquals(2, enumList2.size());
  }
}

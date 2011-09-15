/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Egidijus Vaisnora - initial API and implementation
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CommitException;

import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOTransaction;

import java.util.Map;

/**
 * @author Egidijus Vaisnora, Caspar De Groot
 */
public class Bugzilla_337523_Test extends AbstractCDOTest
{
  public void testRootResource_loadByID() throws CommitException
  {
    CDOSession session = openSession();
    CDOTransaction transaction = session.openTransaction();

    final CDOID rootID = session.getRepositoryInfo().getRootResourceID();
    CDOObject rootResource1 = transaction.getObject(rootID);

    Map<CDOID, InternalCDOObject> objects = ((InternalCDOTransaction)transaction).getObjects();
    CDOObject rootResource2 = objects.get(rootID);

    assertNotNull(rootResource2);
    assertSame(rootResource1, rootResource2);

    transaction.commit();
    transaction.close();
  }
}

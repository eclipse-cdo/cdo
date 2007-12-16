/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOTransaction;

/**
 * @see https://bugs.eclipse.org/bugs/show_bug.cgi?id=201366
 * @author Simon McDuff
 */
public class RandomDeadLockTest extends AbstractCDOTest
{
  public void testCreateManySession() throws Exception
  {
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      transaction.createResource("/test2");
      transaction.commit();
      transaction.close();
      session.close();
    }

    for (int i = 0; i < 10; i++)
    {
      CDOSession session = openModel1Session();
      CDOTransaction transaction = session.openTransaction();
      transaction.getResource("/test2");
      transaction.commit();
      transaction.close();
      session.close();
    }
  }
}

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

/**
 * @author Eike Stepper
 */
public class AuditTest extends AbstractCDOTest
{
  public void testRepositoryCreationTime() throws Exception
  {
    CDOSession session = openSession();
    long repositoryCreationTime = session.getRepositoryCreationTime();
    assertEquals(getRepository().getCreationTime(), repositoryCreationTime);
    assertEquals(getRepository().getStore().getCreationTime(), repositoryCreationTime);
  }

  public void testRepositoryTime() throws Exception
  {
    CDOSession session = openSession();
    long repositoryTime = session.getRepositoryTime();
    assertTrue(Math.abs(System.currentTimeMillis() - repositoryTime) < 500);
  }
}

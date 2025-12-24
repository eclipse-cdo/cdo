/*
 * Copyright (c) 2011, 2012 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.session.CDOSession;

/**
 * @author Eike Stepper
 */
public class AuditSameSessionTest extends AuditTest
{
  public void testRepositoryCreationTime() throws Exception
  {
    CDOSession session = openSession();
    long repositoryCreationTime = session.getRepositoryInfo().getCreationTime();
    assertEquals(getRepository().getCreationTime(), repositoryCreationTime);
    assertEquals(getRepository().getStore().getCreationTime(), repositoryCreationTime);
  }

  public void testRepositoryTime() throws Exception
  {
    CDOSession session = openSession();
    long repositoryTime = session.getRepositoryInfo().getTimeStamp();
    assertEquals(true, Math.abs(System.currentTimeMillis() - repositoryTime) < 500);
  }

  @Override
  protected void closeSession1()
  {
    // Do nothing
  }

  @Override
  protected CDOSession openSession2()
  {
    return session1;
  }
}

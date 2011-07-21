/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.tests.XATransactionTest;

/**
 * @author Eike Stepper
 */
public class DISABLE_XATransactionTest extends XATransactionTest
{
  @Override
  public void testNotUsingXATransaction_Exception() throws Exception
  {
    // XXX test disabled
  }

  @Override
  public void testCommitFromTransactionDisabled() throws Exception
  {
    // XXX disabled because of Bug 290097
    skipTest(getRepositoryConfig() instanceof PostgresqlConfig);
    super.testCommitFromTransactionDisabled();
  }

  @CleanRepositoriesBefore
  @Override
  public void test_ExceptionInReadingStream() throws Exception
  {
    // XXX disabled because of Bug 290097
    skipTest(getRepositoryConfig() instanceof PostgresqlConfig);
    super.test_ExceptionInReadingStream();
  }
}

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

import org.eclipse.emf.cdo.tests.AttributeTest;

/**
 * @author Eike Stepper
 */
public class DISABLE_AttributeTest extends AttributeTest
{
  @Override
  public void testByteArray() throws Exception
  {
    // XXX disabled because of Bug 289445
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);
    super.testByteArray();
  }

  @Override
  public void testByteArrayEmpty() throws Exception
  {
    // XXX disabled because of Bug 289445
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);
    super.testByteArrayEmpty();
  }

  @Override
  public void testByteArrayNull() throws Exception
  {
    // XXX disabled because of Bug 289445
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);
    super.testByteArrayNull();
  }
}

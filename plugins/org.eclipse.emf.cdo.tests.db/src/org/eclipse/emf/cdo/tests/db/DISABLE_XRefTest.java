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

import org.eclipse.emf.cdo.tests.XRefTest;
import org.eclipse.emf.cdo.tests.db.AllTestsDBH2.H2;

/**
 * @author Eike Stepper
 */
public class DISABLE_XRefTest extends XRefTest
{
  @Override
  public void testCrossReferenceMultivalueEReferenceQuery() throws Exception
  {
    skipConfig(H2.ReusableFolder.RANGE_INSTANCE);
    super.testCrossReferenceMultivalueEReferenceQuery();
  }

  @Override
  public void testXRefsToMany() throws Exception
  {
    skipConfig(H2.ReusableFolder.RANGE_INSTANCE);
    super.testXRefsToMany();
  }

  @Override
  public void testXRefsToOne() throws Exception
  {
    skipConfig(H2.ReusableFolder.RANGE_INSTANCE);
    super.testXRefsToOne();
  }
}

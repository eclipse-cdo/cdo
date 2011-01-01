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

import org.eclipse.emf.cdo.tests.ExternalReferenceTest;

/**
 * @author Stefan Winkler
 */
public class DISABLE_ExternalReferenceTest extends ExternalReferenceTest
{
  @Override
  public void testOneXMIResourceManyViewsOnOneResourceSet() throws Exception
  {
    // XXX disabled because of Bug 290097
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);
    super.testOneXMIResourceManyViewsOnOneResourceSet();
  }

  @Override
  public void testManyViewsOnOneResourceSet() throws Exception
  {
    // XXX disabled because of Bug 290097
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);
    super.testManyViewsOnOneResourceSet();
  }
}

/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db;

import org.eclipse.emf.cdo.tests.FeatureMapTest;

/**
 * @author Eike Stepper
 */
public class DISABLE_FeatureMapTest extends FeatureMapTest
{
  @Override
  public void testFeatureMaps() throws Exception
  {
    skipConfig(AllTestsDBPsql.Psql.INSTANCE);
    super.testFeatureMaps();
  }
}

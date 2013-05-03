/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.tests.config.impl.ConfigTest.CleanRepositoriesBefore;

/**
 * @author Eike Stepper
 */
@CleanRepositoriesBefore
public class BranchingWithCacheClearTest extends BranchingTest
{
  @Override
  protected void closeSession1()
  {
    super.closeSession1();
    clearCache(getRepository().getRevisionManager());
  }
}

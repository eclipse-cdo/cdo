/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
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

/**
 * @author Eike Stepper
 */
public class BranchingWithCacheClearTest extends BranchingTest
{
  @Override
  protected void closeSession1()
  {
    super.closeSession1();
    clearCache(getRepository().getRevisionManager());
  }
}

/*
 * Copyright (c) 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db4o;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Victor Roldan Betancort
 */
public class AllTestsDB4OMem extends AllTestsDB4O
{
  public static Test suite()
  {
    return new AllTestsDB4OMem().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, new DB4OConfig(true), JVM, NATIVE);
  }
}

/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.lm;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class IntegrationTestsSuiteLM
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite("Lifecycle Management Tests");
    suite.addTest(new TestSuite(LMFlowsTest.class));
    suite.addTest(new TestSuite(LMVersionEvolutionTest.class));
    return suite;
  }
}

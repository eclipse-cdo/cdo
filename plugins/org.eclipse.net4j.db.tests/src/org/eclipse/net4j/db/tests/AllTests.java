/*
 * Copyright (c) 2008, 2009, 2011-2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.db.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTests
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite("Tests for Net4j DB");

    // $JUnit-BEGIN$
    suite.addTestSuite(H2Test.class);
    // $JUnit-END$

    return suite;
  }
}

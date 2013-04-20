/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Andre Dietisheim - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.db.revisioncache;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Andre Dietisheim
 */
public class AllTestsDBRevisionCache
{
  public static Test suite()
  {
    TestSuite suite = new TestSuite("Tests for CDORevisionCache"); //$NON-NLS-1$

    // $JUnit-BEGIN$
    suite.addTestSuite(DerbyDBRevisionCacheTest.class);
    suite.addTestSuite(H2DBRevisionCacheTest.class);
    // $JUnit-END$

    return suite;
  }
}

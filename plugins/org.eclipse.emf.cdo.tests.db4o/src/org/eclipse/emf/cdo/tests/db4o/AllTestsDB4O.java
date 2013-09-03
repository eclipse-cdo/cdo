/*
 * Copyright (c) 2011-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.db4o;

import org.eclipse.emf.cdo.tests.AllConfigs;
import org.eclipse.emf.cdo.tests.BranchingSameSessionTest;
import org.eclipse.emf.cdo.tests.BranchingTest;
import org.eclipse.emf.cdo.tests.BranchingWithCacheClearTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_261218_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_324585_Test;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Victor Roldan Betancort
 */
public class AllTestsDB4O extends AllConfigs
{
  public static Test suite()
  {
    return new AllTestsDB4O().getTestSuite();
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, new DB4OConfig(false), JVM, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    super.initTestClasses(testClasses, scenario);

    // Added here testcases to skip
    // takes too much
    testClasses.remove(Bugzilla_261218_Test.class);
    testClasses.remove(Bugzilla_324585_Test.class);

    // db4o doesn't support branching
    testClasses.remove(BranchingTest.class);
    testClasses.remove(BranchingSameSessionTest.class);
    testClasses.remove(BranchingWithCacheClearTest.class);
  }
}

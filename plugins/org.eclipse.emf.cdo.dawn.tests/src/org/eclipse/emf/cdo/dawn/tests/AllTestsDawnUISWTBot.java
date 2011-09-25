/*
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests;

import org.eclipse.emf.cdo.dawn.tests.bugzillas.Bugzilla_321024_Test;
import org.eclipse.emf.cdo.dawn.tests.bugzillas.Bugzilla_333187_Test;
import org.eclipse.emf.cdo.dawn.tests.bugzillas.Bugzilla_333291_Test;
import org.eclipse.emf.cdo.dawn.tests.bugzillas.Bugzilla_345858_Test;
import org.eclipse.emf.cdo.dawn.tests.ui.DawnPreferencesTest;
import org.eclipse.emf.cdo.dawn.tests.ui.DawnProjectExplorerTest;
import org.eclipse.emf.cdo.dawn.tests.ui.emf.DawnEMFCreationWizardTest;
import org.eclipse.emf.cdo.dawn.tests.ui.emf.DawnEMFHandleEditorTest;
import org.eclipse.emf.cdo.dawn.tests.ui.emf.EMFEditorRollbackTest;
import org.eclipse.emf.cdo.dawn.tests.ui.emf.EMFLockingTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.ConflictTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.DawnCreationWizardSWTBotTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.GMFLockingTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.MultipleResourcesTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.RollbackTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.SimpleDiagramTest;
import org.eclipse.emf.cdo.tests.AllTests;
import org.eclipse.emf.cdo.tests.config.IScenario;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTestSuite;

import java.util.List;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Martin Fluegge
 */
public class AllTestsDawnUISWTBot extends ConfigTestSuite
{
  public static Test suite()
  {
    TestSuite testSuite = (TestSuite)new AllTestsDawnUISWTBot().getTestSuite(AllTests.class.getName());
    // testSuite.addTest(new JUnit4TestAdapter(AllTestsDawnUISWTBotGMF.class));
    return testSuite;
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
    addScenario(parent, COMBINED, MEM, TCP, NATIVE);
    addScenario(parent, COMBINED, MEM_BRANCHES, TCP, NATIVE);
  }

  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses, IScenario scenario)
  {
    // /******************** General **********************/
    // testClasses.add(DawnCodeGenerationTest.class);
    testClasses.add(DawnProjectExplorerTest.class);
    testClasses.add(DawnPreferencesTest.class);

    // /******************** GMF **********************/
    testClasses.add(DawnCreationWizardSWTBotTest.class);
    testClasses.add(SimpleDiagramTest.class);
    testClasses.add(MultipleResourcesTest.class);
    testClasses.add(GMFLockingTest.class);
    testClasses.add(ConflictTest.class);
    testClasses.add(RollbackTest.class);

    // /******************** EMF **********************/
    testClasses.add(DawnEMFCreationWizardTest.class);
    testClasses.add(EMFEditorRollbackTest.class);
    testClasses.add(DawnEMFHandleEditorTest.class);
    testClasses.add(EMFLockingTest.class);

    // /******************** Bugzilla **********************/
    testClasses.add(Bugzilla_321024_Test.class);
    testClasses.add(Bugzilla_333291_Test.class);
    testClasses.add(Bugzilla_333187_Test.class);
    testClasses.add(Bugzilla_345858_Test.class);
  }
}

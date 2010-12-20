/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
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
import org.eclipse.emf.cdo.dawn.tests.ui.DawnPreferencesTest;
import org.eclipse.emf.cdo.dawn.tests.ui.DawnProjectExplorerTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.ConflictTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.DawnCreationWizardSWTBotTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.MultipleResourcesTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.RollbackTest;
import org.eclipse.emf.cdo.dawn.tests.ui.gmf.SimpleDiagramTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite should be executed as SWTBot test.
 * 
 * @author Martin Fluegge
 * @formatter:off
 */
@Deprecated
@RunWith(Suite.class)
@SuiteClasses({ 
  DawnPreferencesTest.class,
  DawnCreationWizardSWTBotTest.class, 
  SimpleDiagramTest.class, 
  MultipleResourcesTest.class,
  // MultipleResourcesDeletionTest.class,  //excluded because of ui freeze
  DawnProjectExplorerTest.class,
  ConflictTest.class,
  RollbackTest.class,
  Bugzilla_321024_Test.class 
   })
   
   
public class AllTestsDawnUISWTBotGMF
{
}

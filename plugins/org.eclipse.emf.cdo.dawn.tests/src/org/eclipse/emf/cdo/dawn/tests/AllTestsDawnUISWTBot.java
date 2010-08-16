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
import org.eclipse.emf.cdo.dawn.tests.ui.DawnCreationWizardSWTBotTest;
import org.eclipse.emf.cdo.dawn.tests.ui.MultipleResourcesTest;
import org.eclipse.emf.cdo.dawn.tests.ui.SimpleDiagramTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * This test suite should be executed as SWTBot test.
 * 
 * @author Martin Fluegge
 */
@RunWith(Suite.class)
@SuiteClasses({ DawnCreationWizardSWTBotTest.class, SimpleDiagramTest.class, MultipleResourcesTest.class,
    Bugzilla_321024_Test.class })
public class AllTestsDawnUISWTBot
{

}

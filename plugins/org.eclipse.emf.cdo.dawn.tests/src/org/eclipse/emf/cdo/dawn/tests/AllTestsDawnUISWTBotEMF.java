/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.tests;

import org.eclipse.emf.cdo.dawn.tests.ui.emf.DawnEMFCreationWizardTest;
import org.eclipse.emf.cdo.dawn.tests.ui.emf.DawnEMFHandleEditorTest;
import org.eclipse.emf.cdo.dawn.tests.ui.emf.EMFEditorRollbackTest;

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
  DawnEMFCreationWizardTest.class,
  EMFEditorRollbackTest.class,
  DawnEMFHandleEditorTest.class
})

public class AllTestsDawnUISWTBotEMF
{
}

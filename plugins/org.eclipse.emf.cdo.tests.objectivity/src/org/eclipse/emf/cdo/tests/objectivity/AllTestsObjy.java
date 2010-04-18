/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.io.TMPUtil;

import java.io.File;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Eike Stepper
 */
public class AllTestsObjy extends ObjyDBConfigs
{
  public static Test suite()
  {
    return new AllTestsObjy().getTestSuite("CDO Tests (DBStoreRepositoryConfig Objectivity/DB)");
  }

  @Override
  protected void initConfigSuites(TestSuite parent)
  {
  	ObjyStoreRepositoryConfig repConfig = new ObjyStoreRepositoryConfig();
    addScenario(parent, COMBINED, repConfig, JVM, NATIVE);
  }
}

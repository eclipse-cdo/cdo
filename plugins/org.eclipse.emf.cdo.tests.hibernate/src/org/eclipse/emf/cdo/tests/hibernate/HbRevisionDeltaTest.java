/***************************************************************************
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests.hibernate;

import org.eclipse.emf.cdo.tests.RevisionDeltaWithoutDeltaSupportTest;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import java.util.Map;

/**
 * THIS TEST DOES NOT WORK, HIBERNATE DOES NOT SUPPORT REVISIONDELTAS
 * 
 * @author Martin Taal
 */
public class HbRevisionDeltaTest extends RevisionDeltaWithoutDeltaSupportTest
{
  @Override
  public Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(RepositoryConfig.PROP_TEST_REVISION_MANAGER, new TestRevisionManager());
    return testProperties;
  }
}

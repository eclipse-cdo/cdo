/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 **************************************************************************/
package org.eclipse.emf.cdo.tests;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.tests.config.RepositoryConfig;

import java.util.Map;

/**
 * @see http://bugs.eclipse.org/201266
 * @author Simon McDuff
 */
public class RevisionDeltaWithDeltaSupportTest extends RevisionDeltaTest
{
  @Override
  public Map<String, Object> getTestProperties()
  {
    Map<String, Object> testProperties = super.getTestProperties();
    testProperties.put(IRepository.Props.PROP_SUPPORTING_REVISION_DELTAS, "true");
    testProperties.put(RepositoryConfig.PROP_TEST_REVISION_MANAGER, new TestRevisionManager());
    return testProperties;
  }
}

/*
 * Copyright (c) 2018 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.config.IRepositoryConfig;

import java.util.Map;

/**
 * Bug 541437 - Repository UUID management seems broken.
 *
 * @author Eike Stepper
 */
public class Bugzilla_541437_Test extends AbstractCDOTest
{
  @Override
  public synchronized Map<String, Object> getTestProperties()
  {
    Map<String, Object> properties = super.getTestProperties();
    properties.put(IRepository.Props.OVERRIDE_UUID, null);
    return properties;
  }

  public void testUUIDWithoutOverride() throws Exception
  {
    String oldUUID = getRepository().getUUID();
    assertNotSame(null, oldUUID);
    assertNotSame(getRepository().getName(), oldUUID);
  }

  @Requires(IRepositoryConfig.CAPABILITY_RESTARTABLE)
  public void testUUIDAfterRestart() throws Exception
  {
    String oldUUID = getRepository().getUUID();
    restartRepository();

    String newUUID = getRepository().getUUID();
    assertEquals(oldUUID, newUUID);
  }
}

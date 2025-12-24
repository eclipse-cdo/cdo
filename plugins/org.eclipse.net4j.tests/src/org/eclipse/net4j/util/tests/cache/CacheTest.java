/*
 * Copyright (c) 2007, 2008, 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.tests.cache;

import org.eclipse.net4j.util.cache.CacheMonitor;
import org.eclipse.net4j.util.cache.ICacheMonitor.ConditionPolicy;
import org.eclipse.net4j.util.cache.ThresholdConditionPolicy;
import org.eclipse.net4j.util.tests.AbstractOMTest;

/**
 * @author Eike Stepper
 */
public class CacheTest extends AbstractOMTest
{
  public void testLifecycle() throws Exception
  {
    ConditionPolicy conditionPolicy = new ThresholdConditionPolicy(1000000L, 10000000L);

    CacheMonitor cacheMonitor = new CacheMonitor();
    cacheMonitor.setConditionPolicy(conditionPolicy);
    cacheMonitor.setPauseRED(100L);
    cacheMonitor.setPauseYELLOW(100L);
    cacheMonitor.setPauseGREEN(100L);
    cacheMonitor.setDaemon(true);
    cacheMonitor.activate();

    RevisionManager revisionManager = new RevisionManager();
    revisionManager.setCacheMonitor(cacheMonitor);
    revisionManager.activate();

    for (int version = 1; version <= 10; version++)
    {
      for (int id = 1; id <= 100; id++)
      {
        revisionManager.getRevision(id, version);
        sleep(200);
      }

      System.gc();
      sleep(1000);
    }

    revisionManager.deactivate();
    cacheMonitor.deactivate();
  }
}

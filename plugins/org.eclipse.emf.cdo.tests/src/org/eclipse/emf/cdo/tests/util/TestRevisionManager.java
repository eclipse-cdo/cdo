/*
 * Copyright (c) 2011, 2012, 2016, 2021, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.util;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.common.revision.CDORevisionManagerImpl;
import org.eclipse.emf.cdo.spi.common.revision.RevisionInfo;
import org.eclipse.emf.cdo.spi.common.revision.SyntheticCDORevision;

import org.eclipse.net4j.util.concurrent.ConcurrencyUtil;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author Eike Stepper
 */
public class TestRevisionManager extends CDORevisionManagerImpl
{
  private Object lock = new Object();

  private long getRevisionsDelay;

  private int loadCounter;

  private int additionalCounter;

  private volatile Request.Config lastConfig;

  public TestRevisionManager()
  {
  }

  public void setGetRevisionsDelay(long millis)
  {
    synchronized (lock)
    {
      getRevisionsDelay = millis;
    }
  }

  @Override
  public List<CDORevision> getRevisions(List<CDOID> ids, CDOBranchPoint branchPoint, Request.Config config, SyntheticCDORevision[] synthetics)
  {
    lastConfig = config;

    if (getRevisionsDelay > 0)
    {
      long start = System.currentTimeMillis();
      for (;;)
      {
        ConcurrencyUtil.sleep(1L);
        synchronized (lock)
        {
          if (System.currentTimeMillis() > start + getRevisionsDelay)
          {
            break;
          }
        }
      }
    }

    return super.getRevisions(ids, branchPoint, config, synthetics);
  }

  public Request.Config getLastConfig()
  {
    return lastConfig;
  }

  public void resetLoadCounter()
  {
    synchronized (lock)
    {
      loadCounter = 0;
    }
  }

  public int getLoadCounter()
  {
    synchronized (lock)
    {
      return loadCounter;
    }
  }

  public void resetAdditionalCounter()
  {
    synchronized (lock)
    {
      additionalCounter = 0;
    }
  }

  public int getAdditionalCounter()
  {
    synchronized (lock)
    {
      return additionalCounter;
    }
  }

  @Override
  protected void loadRevisions(List<RevisionInfo> infosToLoad, CDOBranchPoint branchPoint, Request.Config config, List<CDORevision> additionalRevisions,
      Consumer<CDORevision> consumer)
  {
    super.loadRevisions(infosToLoad, branchPoint, config, additionalRevisions, consumer);

    countLoadedRevisions(infosToLoad, additionalRevisions);
  }

  private void countLoadedRevisions(List<RevisionInfo> infosToLoad, List<CDORevision> additionalRevisions)
  {
    synchronized (lock)
    {
      additionalCounter += additionalRevisions == null ? 0 : additionalRevisions.size();
      loadCounter += infosToLoad.size();
    }
  }
}

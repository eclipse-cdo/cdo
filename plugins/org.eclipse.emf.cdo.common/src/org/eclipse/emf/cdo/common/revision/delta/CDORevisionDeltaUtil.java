/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - maintenance
 */
package org.eclipse.emf.cdo.common.revision.delta;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOChangeSet;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;
import org.eclipse.emf.cdo.spi.common.revision.ManagedRevisionProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class CDORevisionDeltaUtil
{
  private CDORevisionDeltaUtil()
  {
  }

  public static CDORevisionDelta create(CDORevision revision)
  {
    return new CDORevisionDeltaImpl(revision);
  }

  /**
   * @since 2.0
   */
  public static CDORevisionDelta copy(CDORevisionDelta revisionDelta)
  {
    return new CDORevisionDeltaImpl(revisionDelta, true);
  }

  public static CDORevisionDelta create(CDORevision sourceRevision, CDORevision targetRevision)
  {
    return new CDORevisionDeltaImpl(sourceRevision, targetRevision);
  }

  /**
   * @since 3.0
   */
  public static CDOChangeSetData createChangeSetData(Set<CDOID> ids, final CDOBranchPoint startPoint,
      final CDOBranchPoint endPoint, final CDORevisionManager revisionManager)
  {
    CDORevisionProvider startProvider = new ManagedRevisionProvider(revisionManager, startPoint);
    CDORevisionProvider endProvider = new ManagedRevisionProvider(revisionManager, endPoint);
    return createChangeSetData(ids, startProvider, endProvider);
  }

  /**
   * @since 3.0
   */
  public static CDOChangeSetData createChangeSetData(Set<CDOID> ids, CDORevisionProvider startProvider,
      CDORevisionProvider endProvider)
  {
    List<CDOIDAndVersion> newObjects = new ArrayList<CDOIDAndVersion>();
    List<CDORevisionKey> changedObjects = new ArrayList<CDORevisionKey>();
    List<CDOIDAndVersion> detachedObjects = new ArrayList<CDOIDAndVersion>();
    for (CDOID id : ids)
    {
      CDORevision startRevision = startProvider.getRevision(id);
      CDORevision endRevision = endProvider.getRevision(id);

      if (startRevision == null && endRevision != null)
      {
        newObjects.add(endRevision);
      }
      else if (endRevision == null && startRevision != null)
      {
        detachedObjects.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
      }
      else if (startRevision != null && endRevision != null)
      {
        if (!startRevision.equals(endRevision))
        {
          CDORevisionDelta delta = endRevision.compare(startRevision);
          if (!delta.isEmpty())
          {
            changedObjects.add(delta);
          }
        }
      }
    }

    return new CDOChangeSetDataImpl(newObjects, changedObjects, detachedObjects);
  }

  /**
   * @since 4.0
   */
  public static CDOChangeSet createChangeSet(CDOBranchPoint startPoint, CDOBranchPoint endPoint, CDOChangeSetData data)
  {
    return new CDOChangeSetImpl(startPoint, endPoint, data);
  }
}

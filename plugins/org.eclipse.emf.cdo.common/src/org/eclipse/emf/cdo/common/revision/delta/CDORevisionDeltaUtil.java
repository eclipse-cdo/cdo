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
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.internal.common.commit.CDOChangeSetDataImpl;
import org.eclipse.emf.cdo.internal.common.revision.delta.CDORevisionDeltaImpl;

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

  public static CDORevisionDelta create(CDORevision originRevision, CDORevision dirtyRevision)
  {
    return new CDORevisionDeltaImpl(originRevision, dirtyRevision);
  }

  /**
   * @since 3.0
   */
  public static CDOChangeSetData createChangeSetData(CDOBranchPoint startPoint, CDOBranchPoint endPoint,
      Set<CDOID> ids, CDORevisionManager revisionManager)
  {
    List<CDOIDAndVersion> newObjects = new ArrayList<CDOIDAndVersion>();
    List<CDORevisionKey> changedObjects = new ArrayList<CDORevisionKey>();
    List<CDOIDAndVersion> detachedObjects = new ArrayList<CDOIDAndVersion>();
    for (CDOID id : ids)
    {
      CDORevision startRevision = revisionManager.getRevision(id, startPoint, CDORevision.UNCHUNKED,
          CDORevision.DEPTH_NONE, true);
      CDORevision endRevision = revisionManager.getRevision(id, endPoint, CDORevision.UNCHUNKED,
          CDORevision.DEPTH_NONE, true);

      if (startRevision == null)
      {
        newObjects.add(endRevision);
      }
      else if (endRevision == null)
      {
        detachedObjects.add(CDOIDUtil.createIDAndVersion(id, CDOBranchVersion.UNSPECIFIED_VERSION));
      }
      else
      {
        CDORevisionDelta delta = endRevision.compare(startRevision);
        changedObjects.add(delta);
      }
    }

    return new CDOChangeSetDataImpl(newObjects, changedObjects, detachedObjects);
  }
}

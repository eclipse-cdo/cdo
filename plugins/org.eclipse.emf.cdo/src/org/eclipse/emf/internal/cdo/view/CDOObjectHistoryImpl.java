/*
 * Copyright (c) 2012, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.internal.cdo.view;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOObjectHistory;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchVersion;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfo;
import org.eclipse.emf.cdo.common.commit.CDOCommitInfoManager;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionKey;
import org.eclipse.emf.cdo.common.revision.CDORevisionManager;
import org.eclipse.emf.cdo.internal.common.commit.CDOCommitHistoryImpl;

import java.util.List;

/**
 * A cache for the {@link CDOCommitInfo commit infos} of a branch or of an entire repository.
 *
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOObjectHistoryImpl extends CDOCommitHistoryImpl implements CDOObjectHistory
{
  private final CDORevisionManager revisionManager;

  private final CDOObject object;

  private CDORevision loadedRevision;

  public CDOObjectHistoryImpl(CDOObject object)
  {
    super(object.cdoView().getSession().getCommitInfoManager(), object.cdoRevision().getBranch());
    revisionManager = object.cdoView().getSession().getRevisionManager();
    this.object = object;
  }

  @Override
  public CDOObject getCDOObject()
  {
    return object;
  }

  @Override
  protected boolean filterCommitInfo(CDOCommitInfo commitInfo)
  {
    boolean filtered = super.filterCommitInfo(commitInfo);
    if (filtered)
    {
      return true;
    }

    if (commitInfo.isCommitDataLoaded())
    {
      CDOID id = object.cdoID();

      for (CDORevisionKey key : commitInfo.getChangedObjects())
      {
        if (key.getID().equals(id))
        {
          return false;
        }
      }

      for (CDOIDAndVersion key : commitInfo.getNewObjects())
      {
        if (key.getID().equals(id))
        {
          return false;
        }
      }

      for (CDOIDAndVersion key : commitInfo.getDetachedObjects())
      {
        if (key.getID().equals(id))
        {
          return false;
        }
      }

      return true;
    }

    return false;
  }

  @Override
  protected void loadCommitInfos(int loadCount, List<CDOCommitInfo> addedCommitInfos)
  {
    for (int i = 0; i < loadCount; i++)
    {
      if (loadedRevision == null)
      {
        loadedRevision = object.cdoRevision();
      }
      else
      {
        int version = loadedRevision.getVersion();
        if (version > CDOBranchVersion.FIRST_VERSION)
        {
          CDOBranchVersion previous = loadedRevision.getBranch().getVersion(version - 1);
          loadedRevision = revisionManager.getRevisionByVersion(object.cdoID(), previous, CDORevision.UNCHUNKED, true);
        }
        else
        {
          CDOBranchPoint base = loadedRevision.getBranch().getBase();
          if (base.getBranch() == null)
          {
            // Reached repository creation.
            setFull();
            break;
          }

          CDORevision revision = revisionManager.getRevision(object.cdoID(), base, CDORevision.UNCHUNKED, CDORevision.DEPTH_NONE, true);
          if (revision == null)
          {
            // Reached branch where the object does not exist anymore.
            setFull();
            break;
          }

          loadedRevision = revision;
        }
      }

      CDOCommitInfoManager manager = getManager();
      CDOCommitInfo commitInfo = manager.getCommitInfo(loadedRevision.getTimeStamp());
      if (addCommitInfo(commitInfo))
      {
        addedCommitInfos.add(commitInfo);
      }
    }
  }
}

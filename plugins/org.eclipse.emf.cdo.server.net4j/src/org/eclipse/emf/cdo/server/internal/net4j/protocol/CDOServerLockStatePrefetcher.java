/*
 * Copyright (c) 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.lock.CDOLockState;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * @author Eike Stepper
 */
public abstract class CDOServerLockStatePrefetcher
{
  private CDOServerLockStatePrefetcher()
  {
  }

  public abstract void addLockStateKey(CDOID id);

  public abstract void addLockStateKey(Supplier<CDOID> idSupplier);

  public abstract void writeLockStates(CDODataOutput out) throws IOException;

  public static CDOServerLockStatePrefetcher create(InternalRepository repository, InternalSession session, CDOBranchPoint branchPoint,
      boolean prefetchLockStates)
  {
    if (!prefetchLockStates)
    {
      return new None();
    }

    if (branchPoint == null || branchPoint.getTimeStamp() != CDOBranchPoint.UNSPECIFIED_DATE)
    {
      return new None();
    }

    CDOBranch branch = branchPoint.getBranch();
    return new Normal(repository, session, branch);
  }

  /**
   * @author Eike Stepper
   */
  private static final class None extends CDOServerLockStatePrefetcher
  {
    private None()
    {
    }

    @Override
    public void addLockStateKey(CDOID id)
    {
      // Do nothing.
    }

    @Override
    public void addLockStateKey(Supplier<CDOID> idSupplier)
    {
      // Do nothing.
    }

    @Override
    public void writeLockStates(CDODataOutput out) throws IOException
    {
      out.writeBoolean(false);
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class Normal extends CDOServerLockStatePrefetcher
  {
    private final Set<CDOID> lockStateIDs = new HashSet<>();

    private final InternalLockManager lockingManager;

    private final CDOBranch branch;

    private final InternalSession session;

    private Normal(InternalRepository repository, InternalSession session, CDOBranch branch)
    {
      lockingManager = repository.getLockingManager();
      this.session = session;
      this.branch = branch;
    }

    @Override
    public void addLockStateKey(CDOID id)
    {
      lockStateIDs.add(id);
    }

    @Override
    public void addLockStateKey(Supplier<CDOID> idSupplier)
    {
      if (idSupplier != null)
      {
        CDOID id = idSupplier.get();
        if (id != null)
        {
          addLockStateKey(id);
        }
      }
    }

    @Override
    public void writeLockStates(CDODataOutput out) throws IOException
    {
      out.writeBoolean(true);
      InternalLockManager.LockStateQuery snapshot = lockingManager.snapshotLockStates(session, branch, lockStateIDs);
      out.writeXLong(snapshot.getLockModCount());
      out.writeCDOLockStates(snapshot.getLockStates(), null);

      Set<CDOID> existingIDs = new HashSet<>();
      for (CDOLockState state : snapshot.getLockStates())
      {
        existingIDs.add(state.getID());
      }

      Set<CDOID> noLockStateIDs = new HashSet<>(lockStateIDs);
      noLockStateIDs.removeAll(existingIDs);
      out.writeXInt(noLockStateIDs.size());

      for (CDOID id : noLockStateIDs)
      {
        out.writeCDOID(id);
      }
    }
  }
}

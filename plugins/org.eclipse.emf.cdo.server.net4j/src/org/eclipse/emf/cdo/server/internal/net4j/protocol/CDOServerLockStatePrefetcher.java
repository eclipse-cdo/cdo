/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
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
import org.eclipse.emf.cdo.common.lock.CDOLockUtil;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.revision.CDOIDAndBranch;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

import org.eclipse.net4j.util.WrappedException;

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

  public static CDOServerLockStatePrefetcher create(InternalRepository repository, CDOBranchPoint branchPoint, boolean prefetchLockStates)
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
    if (repository.isSupportingBranches())
    {
      return new Branching(repository, branch);
    }

    return new Normal(repository, branch);
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
    private final Set<Object> lockStateKeys = new HashSet<>();

    private final InternalLockManager lockingManager;

    private final CDOBranch branch;

    private Normal(InternalRepository repository, CDOBranch branch)
    {
      lockingManager = repository.getLockingManager();
      this.branch = branch;
    }

    @Override
    public void addLockStateKey(CDOID id)
    {
      Object key = lockingManager.getLockKey(id, branch);
      lockStateKeys.add(key);
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

      Set<Object> noLockStateKeys = new HashSet<>();

      try
      {
        lockingManager.getLockStates(lockStateKeys, (key, lockState) -> {
          if (lockState != null)
          {
            CDOLockState cdoLockState = CDOLockUtil.convertLockState(lockState);

            try
            {
              out.writeCDOLockState(cdoLockState);
            }
            catch (IOException ex)
            {
              throw WrappedException.wrap(ex);
            }
          }
          else
          {
            noLockStateKeys.add(key);
          }
        });
      }
      catch (WrappedException ex)
      {
        Exception exception = ex.exception();
        if (exception instanceof IOException)
        {
          throw (IOException)exception;
        }

        throw ex;
      }

      out.writeCDOLockState(null);
      out.writeXInt(noLockStateKeys.size());
      writeNoLockStateKeys(out, noLockStateKeys);
    }

    protected void writeNoLockStateKeys(CDODataOutput out, Set<Object> noLockStateKeys) throws IOException
    {
      for (Object key : noLockStateKeys)
      {
        out.writeCDOID((CDOID)key);
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  private static class Branching extends CDOServerLockStatePrefetcher.Normal
  {
    private Branching(InternalRepository repository, CDOBranch branch)
    {
      super(repository, branch);
    }

    @Override
    protected void writeNoLockStateKeys(CDODataOutput out, Set<Object> noLockStateKeys) throws IOException
    {
      for (Object key : noLockStateKeys)
      {
        out.writeCDOID(((CDOIDAndBranch)key).getID());
      }
    }
  }
}
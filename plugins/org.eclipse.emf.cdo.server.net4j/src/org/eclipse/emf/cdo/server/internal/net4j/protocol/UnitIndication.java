/*
 * Copyright (c) 2016, 2017, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants.UnitOpcode;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionHandler;
import org.eclipse.emf.cdo.server.IUnit;
import org.eclipse.emf.cdo.server.IUnitManager;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevisionCache;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import java.io.IOException;

/**
 * @author Eike Stepper
 */
public class UnitIndication extends CDOServerReadIndicationWithMonitoring
{
  private int viewID;

  private CDOID rootID;

  private UnitOpcode opcode;

  private boolean prefetchLockStates;

  public UnitIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_UNIT);
  }

  @Override
  protected int getIndicatingWorkPercent()
  {
    return 1;
  }

  @Override
  protected void indicating(CDODataInput in, OMMonitor monitor) throws Exception
  {
    viewID = in.readXInt();
    rootID = in.readCDOID();
    opcode = UnitOpcode.values()[in.readByte()];
    prefetchLockStates = opcode.canPrefetchLockStates() && in.readBoolean();
  }

  @Override
  protected void responding(CDODataOutput out, OMMonitor monitor) throws Exception
  {
    InternalRepository repository = getRepository();
    InternalView view = getView(viewID);
    if (opcode == UnitOpcode.CHECK)
    {
      IUnitManager unitManager = repository.getUnitManager();
      boolean isUnit = unitManager.isUnit(rootID);
      out.writeBoolean(isUnit);
      return;
    }

    if (opcode == UnitOpcode.CLOSE)
    {
      IUnitManager unitManager = repository.getUnitManager();
      IUnit unit = unitManager.getUnit(rootID);
      if (unit != null)
      {
        unit.close(view);
        out.writeBoolean(true);
        return;
      }

      out.writeBoolean(false);
      return;
    }

    CDOServerLockStatePrefetcher lockStatePrefetcher = CDOServerLockStatePrefetcher.create(repository, view, prefetchLockStates);
    InternalCDORevisionCache revisionCache = repository.getRevisionManager().getCache();
    IOException[] ioException = { null };
    RuntimeException[] runtimeException = { null };

    monitor.begin();
    Async async = monitor.forkAsync();

    try
    {
      boolean success = view.openUnit(rootID, opcode, new CDORevisionHandler()
      {
        @Override
        public boolean handleRevision(CDORevision revision)
        {
          try
          {
            CDOID id = revision.getID();

            view.unsubscribe(id);
            revision = revisionCache.internRevision(revision);

            out.writeCDORevision(revision, CDORevision.UNCHUNKED); // Exposes revision to client side
            lockStatePrefetcher.addLockStateKey(id);
            return true;
          }
          catch (IOException ex)
          {
            ioException[0] = ex;
          }
          catch (RuntimeException ex)
          {
            runtimeException[0] = ex;
          }

          return false;
        }
      }, monitor);

      if (ioException[0] != null)
      {
        throw ioException[0];
      }

      if (runtimeException[0] != null)
      {
        throw runtimeException[0];
      }

      out.writeCDORevision(null, CDORevision.UNCHUNKED); // No more revisions
      out.writeBoolean(success);

      if (success)
      {
        lockStatePrefetcher.writeLockStates(out);
      }
    }
    finally
    {
      async.stop();
      monitor.done();
    }
  }
}

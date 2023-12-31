/*
 * Copyright (c) 2011, 2012, 2015, 2016, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Caspar De Groot - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.net4j.protocol;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.server.InternalLockManager;
import org.eclipse.emf.cdo.spi.server.InternalSession;
import org.eclipse.emf.cdo.spi.server.InternalView;

import org.eclipse.net4j.util.concurrent.Holder;

import java.io.IOException;

/**
 * @author Caspar De Groot
 */
public class UnlockDelegationIndication extends UnlockObjectsIndication
{
  private InternalView view;

  private String lockAreaID;

  public UnlockDelegationIndication(CDOServerProtocol protocol)
  {
    super(protocol, CDOProtocolConstants.SIGNAL_UNLOCK_OBJECTS);
  }

  @Override
  protected void indicating(CDODataInput in) throws IOException
  {
    lockAreaID = in.readString();
    super.indicating(in);
  }

  @Override
  protected void responding(CDODataOutput out) throws IOException
  {
    try
    {
      super.responding(out);
    }
    finally
    {
      view.close();
    }
  }

  @Override
  protected InternalView getView(int viewID)
  {
    Holder<IView> viewHolder = new Holder<>();

    InternalLockManager lockManager = getRepository().getLockingManager();
    InternalSession session = getSession();
    lockManager.openView(session, InternalSession.TEMP_VIEW_ID, true, lockAreaID, viewHolder, null);
    return view = (InternalView)viewHolder.get();
  }
}

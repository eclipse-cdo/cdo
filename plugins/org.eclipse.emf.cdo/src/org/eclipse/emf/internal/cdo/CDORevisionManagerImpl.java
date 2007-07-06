/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo;

import org.eclipse.emf.cdo.CDORevisionManager;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionImpl;
import org.eclipse.emf.cdo.internal.protocol.revision.CDORevisionResolverImpl;
import org.eclipse.emf.cdo.protocol.CDOID;
import org.eclipse.emf.cdo.protocol.util.TransportException;

import org.eclipse.emf.internal.cdo.protocol.LoadObjectRequest;

/**
 * @author Eike Stepper
 */
public class CDORevisionManagerImpl extends CDORevisionResolverImpl implements CDORevisionManager
{
  private CDOSessionImpl session;

  public CDORevisionManagerImpl(CDOSessionImpl session)
  {
    this.session = session;
  }

  public CDOSessionImpl getSession()
  {
    return session;
  }

  @Override
  protected CDORevisionImpl loadRevision(CDOID id)
  {
    try
    {
      LoadObjectRequest signal = new LoadObjectRequest(session.getChannel(), id);
      return signal.send();
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }

  @Override
  protected CDORevisionImpl loadRevision(CDOID id, long timeStamp)
  {
    try
    {
      LoadObjectRequest signal = new LoadObjectRequest(session.getChannel(), id, timeStamp);
      return signal.send();
    }
    catch (Exception ex)
    {
      throw new TransportException(ex);
    }
  }
}

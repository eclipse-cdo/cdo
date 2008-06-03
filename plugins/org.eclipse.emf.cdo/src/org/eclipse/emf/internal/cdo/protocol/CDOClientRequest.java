/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.protocol;

import org.eclipse.emf.cdo.internal.common.revision.CDORevisionResolverImpl;

import org.eclipse.emf.internal.cdo.CDOSessionImpl;

import org.eclipse.net4j.channel.IChannel;
import org.eclipse.net4j.signal.RequestWithConfirmation;

/**
 * @author Eike Stepper
 */
public abstract class CDOClientRequest<RESULT> extends RequestWithConfirmation<RESULT>
{
  public CDOClientRequest(IChannel channel)
  {
    super(channel);
  }

  protected CDORevisionResolverImpl getRevisionManager()
  {
    return getSession().getRevisionManager();
  }

  protected CDOSessionImpl getSession()
  {
    return (CDOSessionImpl)getProtocol().getInfraStructure();
  }

  @Override
  public CDOClientProtocol getProtocol()
  {
    return (CDOClientProtocol)super.getProtocol();
  }
}

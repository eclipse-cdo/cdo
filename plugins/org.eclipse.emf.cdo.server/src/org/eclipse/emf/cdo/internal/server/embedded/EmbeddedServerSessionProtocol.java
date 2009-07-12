/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server.embedded;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class EmbeddedServerSessionProtocol extends Lifecycle implements ISessionProtocol
{
  // A separate session protocol instance is required because the getSession() methods are ambiguous!
  private EmbeddedClientSessionProtocol clientSessionProtocol;

  private InternalSession session;

  public EmbeddedServerSessionProtocol(EmbeddedClientSessionProtocol clientSessionProtocol)
  {
    this.clientSessionProtocol = clientSessionProtocol;
  }

  public EmbeddedClientSessionProtocol getClientSessionProtocol()
  {
    return clientSessionProtocol;
  }

  public InternalSession openSession(InternalRepository repository, boolean passiveUpdateEnabled)
  {
    session = repository.getSessionManager().openSession(this);
    session.setPassiveUpdateEnabled(passiveUpdateEnabled);
    return session;
  }

  public InternalSession getSession()
  {
    return session;
  }

  public CDOAuthenticationResult sendAuthenticationChallenge(byte[] randomToken) throws Exception
  {
    return clientSessionProtocol.handleAuthenticationChallenge(randomToken);
  }

  public void sendCommitNotification(long timeStamp, CDOPackageUnit[] packageUnits, List<CDOIDAndVersion> dirtyIDs,
      List<CDOID> detachedObjects, List<CDORevisionDelta> newDeltas)
  {
  }

  public void sendRemoteSessionNotification(byte opcode, ISession session)
  {
  }
}

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

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.protocol.CDOAuthenticationResult;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.spi.server.InternalSession;

import org.eclipse.net4j.util.lifecycle.Lifecycle;

import java.util.Arrays;
import java.util.HashSet;
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

  public void sendBranchNotification(InternalCDOBranch branch)
  {
    EmbeddedClientSession clientSession = clientSessionProtocol.getSession();
    clientSession.handleBranchNotification(branch);
  }

  public void sendCommitNotification(CDOBranchPoint branchPoint, CDOPackageUnit[] packageUnits,
      List<CDOIDAndVersion> dirtyIDs, List<CDOID> detachedObjects, List<CDORevisionDelta> newDeltas)
  {
    EmbeddedClientSession clientSession = clientSessionProtocol.getSession();
    clientSession.handleCommitNotification(branchPoint, Arrays.asList(packageUnits), new HashSet<CDOIDAndVersion>(
        dirtyIDs), detachedObjects, newDeltas, null);
  }

  public void sendRemoteSessionNotification(byte opcode, ISession session)
  {
    throw new UnsupportedOperationException();
  }

  public boolean sendRemoteMessageNotification(InternalSession sender, CDORemoteSessionMessage message)
  {
    throw new UnsupportedOperationException();
  }
}

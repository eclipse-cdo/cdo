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
package org.eclipse.emf.cdo.spi.server;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDAndVersion;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.delta.CDORevisionDelta;
import org.eclipse.emf.cdo.server.ISessionManager;
import org.eclipse.emf.cdo.server.SessionCreationException;
import org.eclipse.emf.cdo.session.remote.CDORemoteSessionMessage;
import org.eclipse.emf.cdo.spi.common.branch.InternalCDOBranch;

import org.eclipse.net4j.util.security.IUserManager;

import java.util.List;

/**
 * @author Eike Stepper
 * @since 3.0
 */
public interface InternalSessionManager extends ISessionManager
{
  public InternalRepository getRepository();

  public void setRepository(InternalRepository repository);

  public void setUserManager(IUserManager userManager);

  public InternalSession[] getSessions();

  public InternalSession getSession(int sessionID);

  /**
   * @return Never <code>null</code>
   */
  public InternalSession openSession(ISessionProtocol sessionProtocol) throws SessionCreationException;

  public void sessionClosed(InternalSession session);

  public void handleBranchNotification(InternalCDOBranch branch, InternalSession session);

  public void handleCommitNotification(CDOBranchPoint branchPoint, CDOPackageUnit[] packageUnits,
      List<CDOIDAndVersion> dirtyIDs, List<CDOID> detachedObjects, List<CDORevisionDelta> deltas,
      InternalSession excludedSession);

  public void handleRemoteSessionNotification(byte opcode, InternalSession excludedSession);

  public List<Integer> sendMessage(InternalSession sender, CDORemoteSessionMessage message, int[] recipients);
}

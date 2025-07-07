/*
 * Copyright (c) 2007-2013, 2019, 2021, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 230832
 */
package org.eclipse.emf.cdo.server;

import org.eclipse.emf.cdo.common.CDOCommonSession;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.lob.CDOLobLoader;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.server.ISessionProtocol;

import org.eclipse.net4j.util.container.IContainer;

/**
 * The server-side representation of a client {@link CDOSession session}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISession extends CDOCommonSession, CDOLobLoader, IContainer<IView>
{
  /**
   * @since 3.0
   */
  public ISessionManager getManager();

  /**
   * @since 4.13
   */
  public IRepository getRepository();

  /**
   * @since 3.0
   */
  public ISessionProtocol getProtocol();

  /**
   * @since 4.0
   * @deprecated The return value of this method can not be relied upon to be strictly ordered!
   */
  @Deprecated
  public long getLastUpdateTime();

  /**
   * @since 2.0
   */
  public boolean isSubscribed();

  /**
   * @since 4.13
   */
  @Override
  public IView[] getViews();

  /**
   * @since 4.13
   */
  @Override
  public IView getView(int viewID);

  /**
   * @since 3.0
   */
  public IView openView(int viewID, CDOBranchPoint branchPoint);

  /**
   * @since 4.19
   */
  public IView openView(int viewID, CDOBranchPoint branchPoint, String durableLockingID);

  /**
   * @since 3.0
   */
  public ITransaction openTransaction(int viewID, CDOBranchPoint branchPoint);

  /**
   * @since 4.19
   */
  public ITransaction openTransaction(int viewID, CDOBranchPoint branchPoint, String durableLockingID);
}

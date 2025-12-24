/*
 * Copyright (c) 2010-2012, 2015, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.spi.workspace;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;
import org.eclipse.emf.cdo.spi.server.InternalRepository;
import org.eclipse.emf.cdo.workspace.CDOWorkspace;

import org.eclipse.net4j.util.container.IManagedContainerProvider;

import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOView;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface InternalCDOWorkspace extends CDOWorkspace, IManagedContainerProvider
{
  /**
   * @since 4.2
   */
  public static final int NO_BRANCH_ID = -1;

  /**
   * @since 4.2
   */
  public InternalCDOView[] getViews();

  /**
   * @since 4.1
   */
  public IDGenerationLocation getIDGenerationLocation();

  @Override
  public InternalCDOWorkspaceBase getBase();

  public InternalRepository getLocalRepository();

  public InternalCDOSession getLocalSession();

  /**
   * @since 4.2
   */
  public CDOChangeSetData getLocalChanges(boolean forward);

  /**
   * @since 4.1
   */
  public CDOSessionConfigurationFactory getRemoteSessionConfigurationFactory();

  /**
   * @since 4.2
   */
  public void revert(CDOChangeSetData revertData);

  /**
   * @since 4.2
   */
  public void replace(String branchPath, long timeStamp, CDOChangeSetData revertData);
}

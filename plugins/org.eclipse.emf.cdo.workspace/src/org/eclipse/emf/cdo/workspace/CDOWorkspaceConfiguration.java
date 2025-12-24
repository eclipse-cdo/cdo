/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.workspace;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.common.id.CDOIDGenerator;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.session.CDOSessionConfigurationFactory;

/**
 * Checks out new {@link CDOWorkspace workspace} or opens existing ones.
 *
 * @author Eike Stepper
 * @since 4.1
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOWorkspaceConfiguration
{
  public static final String DEFAULT_LOCAL_REPOSITORY_NAME = "local";

  public String getLocalRepositoryName();

  public void setLocalRepositoryName(String localRepositoryName);

  public IStore getStore();

  public void setStore(IStore store);

  public CDOWorkspaceBase getBase();

  public void setBase(CDOWorkspaceBase base);

  public CDOSessionConfigurationFactory getRemote();

  public void setRemote(CDOSessionConfigurationFactory remote);

  /**
   * @since 4.2
   */
  public int getBranchID();

  /**
   * @since 4.2
   */
  public void setBranchID(int branchPath);

  public String getBranchPath();

  public void setBranchPath(String branchPath);

  public long getTimeStamp();

  public void setTimeStamp(long timeStamp);

  public IDGenerationLocation getIDGenerationLocation();

  public void setIDGenerationLocation(IDGenerationLocation idGenerationLocation);

  public CDOIDGenerator getIDGenerator();

  public void setIDGenerator(CDOIDGenerator idGenerator);

  public CDOWorkspace open();

  public CDOWorkspace checkout();
}

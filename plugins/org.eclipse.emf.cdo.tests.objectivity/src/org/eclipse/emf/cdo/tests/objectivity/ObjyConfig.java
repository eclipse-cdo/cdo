/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStoreConfig;
import org.eclipse.emf.cdo.server.objectivity.ObjyStoreUtil;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

/**
 * @author Eike Stepper
 */
public class ObjyConfig extends RepositoryConfig
{
  public static final String STORE_NAME = "Objy";

  private static final long serialVersionUID = 1L;

  private static ObjectivityStoreConfig storeConfig = new ObjectivityStoreConfig();

  public ObjyConfig(boolean supportingAudits, boolean supportingBranches)
  {
    super(STORE_NAME, supportingAudits, supportingBranches, IDGenerationLocation.STORE);

    org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.DEBUG.setEnabled(true);
    org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.INFO.setEnabled(true);
  }

  @Override
  protected String getStoreName()
  {
    return STORE_NAME;
  }

  @Override
  protected void deactivateRepositories()
  {
    super.deactivateRepositories();
    // System.out.println(">>>>IS:<<<< We need to remove all data created here....");
    storeConfig.resetFD();
  }

  public IStore createStore(String repoName)
  {
    // We might need to use the repoName to our advantage!!!
    System.out.println("************* ObjyStore creation ****************\n");
    return ObjyStoreUtil.createStore(storeConfig);
  }
}

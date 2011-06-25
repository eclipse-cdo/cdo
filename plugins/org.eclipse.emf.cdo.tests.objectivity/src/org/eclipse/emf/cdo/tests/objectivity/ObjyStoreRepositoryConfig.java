/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStoreConfig;
import org.eclipse.emf.cdo.server.objectivity.ObjyStoreUtil;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

/**
 * @author Eike Stepper
 */
public abstract class ObjyStoreRepositoryConfig extends RepositoryConfig
{
  private static final long serialVersionUID = 1L;

  private static ObjectivityStoreConfig storeConfig = new ObjectivityStoreConfig();

  public ObjyStoreRepositoryConfig(String name)
  {
    super(name);
  }

  @Override
  public void setUp() throws Exception
  {
    // System.out.println("ObjyStoreRepositry.setup() - STARTED");
    // long sTime = System.currentTimeMillis();
    super.setUp();
    // long eTime = System.currentTimeMillis();
    // System.out.println("ObjyStoreRepositry.setup() time: " + (eTime - sTime));
  }

  @Override
  protected void deactivateRepositories()
  {
    super.deactivateRepositories();
    // System.out.println("IS: We need to remove all data created here....");
    storeConfig.resetFD();
  }

  @Override
  protected IStore createStore(String repoName)
  {
    // We might need to use the repoName to our advantage!!!
    System.out.println("************* ObjyStore creation ****************\n");
    return ObjyStoreUtil.createStore(storeConfig);
  }

}

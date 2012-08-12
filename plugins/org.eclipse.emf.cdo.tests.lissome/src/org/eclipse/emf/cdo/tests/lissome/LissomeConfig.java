/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.lissome;

import org.eclipse.emf.cdo.common.CDOCommonRepository.IDGenerationLocation;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.internal.lissome.LissomeStore;
import org.eclipse.emf.cdo.server.internal.lissome.db.DBBrowserPage;
import org.eclipse.emf.cdo.server.internal.lissome.db.Index;
import org.eclipse.emf.cdo.server.internal.lissome.file.Journal;
import org.eclipse.emf.cdo.server.internal.lissome.file.Vob;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;

import org.eclipse.net4j.db.DBUtil;
import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.io.IOUtil;

import javax.sql.DataSource;

import java.io.File;

/**
 * @author Eike Stepper
 */
public class LissomeConfig extends RepositoryConfig
{
  public static final RepositoryConfig INSTANCE = new LissomeConfig(IDGenerationLocation.STORE);

  public static final String STORE_NAME = "Lissome";

  private static final long serialVersionUID = 1L;

  private static File reusableFolder;

  public LissomeConfig(IDGenerationLocation idGenerationLocation)
  {
    super(STORE_NAME, true, true, idGenerationLocation);
  }

  @Override
  protected String getStoreName()
  {
    return STORE_NAME;
  }

  @Override
  protected boolean isOptimizing()
  {
    return true;
  }

  @Override
  public boolean isRestartable()
  {
    return true;
  }

  public IStore createStore(String repoName)
  {
    if (reusableFolder == null)
    {
      reusableFolder = getCurrentTest().createTempFolder("lissome_", "_test");
      IOUtil.delete(reusableFolder);
    }

    IOUtil.ERR().println("Lissome folder: " + reusableFolder);

    boolean dropIfExists = !isRestarting();
    DataSource dataSource = Index.createDataSource(reusableFolder, repoName, null);
    DBUtil.createSchema(dataSource, repoName, dropIfExists);

    if (dropIfExists)
    {
      new File(reusableFolder, repoName + "." + LissomeStore.PERSISTENT_PROPERTIES_EXTENSION).delete();
      new File(reusableFolder, repoName + "." + Journal.EXTENSION).delete();
      new File(reusableFolder, repoName + "." + Vob.EXTENSION).delete();
    }

    LissomeStore store = new LissomeStore();
    store.setFolder(reusableFolder);
    return store;
  }

  @Override
  public void setUp() throws Exception
  {
    IPluginContainer.INSTANCE.registerFactory(new DBBrowserPage.Factory());
    super.setUp();
  }
}

/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.bundle;

import org.eclipse.emf.cdo.internal.server.store.NOOPStore;
import org.eclipse.emf.cdo.server.IRepositoryManager;

import org.eclipse.net4j.ui.UIActivator;
import org.eclipse.net4j.util.om.OMBundle;
import org.eclipse.net4j.util.om.OMPlatform;
import org.eclipse.net4j.util.om.log.OMLogger;
import org.eclipse.net4j.util.om.pref.OMPreference;
import org.eclipse.net4j.util.om.pref.OMPreferences;
import org.eclipse.net4j.util.om.trace.OMTracer;

/**
 * @author Eike Stepper
 */
public abstract class OM
{
  public static final String BUNDLE_ID = "org.eclipse.emf.cdo.ui"; //$NON-NLS-1$

  public static final OMBundle BUNDLE = OMPlatform.INSTANCE.bundle(BUNDLE_ID, OM.class);

  public static final OMTracer DEBUG = BUNDLE.tracer("debug"); //$NON-NLS-1$

  public static final OMLogger LOG = BUNDLE.logger();

  public static final OMPreferences PREFS = BUNDLE.preferences();

  public static final OMPreference<String[]> PREF_HISTORY_SELECT_PACKAGES = PREFS
      .initArray("PREF_HISTORY_SELECT_PACKAGES");

  static void start() throws Exception
  {
    IRepositoryManager.INSTANCE.addRepository("repo1", new NOOPStore());
    // Properties properties = BUNDLE.getConfigProperties();
    // String repositories = properties.getProperty("repositories");
    // if (repositories != null)
    // {
    // StringTokenizer tokenizer = new StringTokenizer(repositories, ",");
    // while (tokenizer.hasMoreTokens())
    // {
    // String repositoryName = tokenizer.nextToken().trim();
    // String storeType = properties.getProperty(repositoryName + ".type");
    // IDBAdapter adapter = DBUtil.getDBAdapter("derby");
    // DataSource dataSource = DBUtil.createDataSource(properties,
    // repositoryName + ".dataSource");
    // CDODBStoreManager storeManager = new CDODBStoreManager(adapter,
    // dataSource);
    // IRepositoryManager.INSTANCE.addRepository("repo1", storeManager);
    // }
    // }
  }

  /**
   * @author Eike Stepper
   */
  public static final class Activator extends UIActivator
  {
    public static Activator INSTANCE;

    public Activator()
    {
      super(BUNDLE);
      INSTANCE = this;
    }
  }
}

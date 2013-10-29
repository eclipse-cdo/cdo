/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.internal.admin;

import org.eclipse.emf.cdo.server.admin.CDORepositoryConfigurationManager;
import org.eclipse.emf.cdo.spi.server.InternalRepository;

/**
 * @author Christian W. Damus (CEA LIST)
 */
public interface InternalCDORepositoryConfigurationManager extends CDORepositoryConfigurationManager
{
  public static final String DEFAULT_CATALOG_PATH = "/catalog";

  /**
   * Sets the administrative repository, in which I maintain the catalog of
   * dynamic repositories that I manage.
   */
  public void setAdminRepository(InternalRepository repository);
}

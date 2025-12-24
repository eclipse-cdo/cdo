/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
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

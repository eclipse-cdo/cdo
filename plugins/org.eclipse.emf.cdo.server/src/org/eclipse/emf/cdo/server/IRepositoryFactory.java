/*
 * Copyright (c) 2007, 2009, 2011, 2012, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server;

/**
 * Creates CDO {@link IRepository repositories}.
 *
 * @author Eike Stepper
 */
public interface IRepositoryFactory
{
  /**
   * @since 2.0
   */
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.repositories"; //$NON-NLS-1$

  public String getRepositoryType();

  public IRepository createRepository();
}

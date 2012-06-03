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
package org.eclipse.emf.cdo.server.spi.admin;

import org.eclipse.emf.cdo.server.IRepository;

import org.eclipse.net4j.util.factory.ProductCreationException;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 4.1
 */
public interface CDOAdminHandler
{
  public String getType();

  public IRepository createRepository(String name, Map<String, Object> properties);

  public void deleteRepository(IRepository delegate);

  /**
   * @author Eike Stepper
   */
  public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
  {
    public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.admin.adminHandlers";

    public Factory(String type)
    {
      super(PRODUCT_GROUP, type);
    }

    public abstract CDOAdminHandler create(String description) throws ProductCreationException;
  }
}

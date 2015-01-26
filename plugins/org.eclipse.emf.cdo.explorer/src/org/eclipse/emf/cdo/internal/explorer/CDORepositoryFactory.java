/*
 * Copyright (c) 2004-2014 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.explorer;

import org.eclipse.emf.cdo.explorer.CDORepository;

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * @author Eike Stepper
 */
public abstract class CDORepositoryFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.explorer.repositories";

  public CDORepositoryFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  public abstract CDORepository create(String description) throws ProductCreationException;
}

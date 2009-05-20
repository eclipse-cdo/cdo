/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.internal.server;

import org.eclipse.net4j.util.factory.Factory;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public abstract class QueryHandlerFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.queryHandlerFactories"; //$NON-NLS-1$

  public QueryHandlerFactory(String language)
  {
    super(PRODUCT_GROUP, language);
  }
}

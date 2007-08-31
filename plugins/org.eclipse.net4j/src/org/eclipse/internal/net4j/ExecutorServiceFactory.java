/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.internal.net4j;

import org.eclipse.net4j.internal.util.concurrent.NamedExecutorService;
import org.eclipse.net4j.internal.util.factory.Factory;
import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.concurrent.ExecutorService;

/**
 * @author Eike Stepper
 */
public class ExecutorServiceFactory extends Factory<ExecutorService>
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.executorServices";

  public static final String TYPE = "default";

  public ExecutorServiceFactory()
  {
    super(PRODUCT_GROUP, TYPE);
  }

  public ExecutorService create(String description)
  {
    return new NamedExecutorService();
  }

  public static ExecutorService get(IManagedContainer container)
  {
    return (ExecutorService)container.getElement(PRODUCT_GROUP, TYPE, null);
  }
}

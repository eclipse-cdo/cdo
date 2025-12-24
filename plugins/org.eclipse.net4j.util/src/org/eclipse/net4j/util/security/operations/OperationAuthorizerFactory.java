/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.security.operations;

import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.ProductCreationException;

/**
 * If the meaning of this type isn't clear, there really should be more of a description here...
 *
 * @author Eike Stepper
 * @since 3.16
 */
public abstract class OperationAuthorizerFactory<CONTEXT> extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.net4j.util.security.operationAuthorizers"; //$NON-NLS-1$

  public OperationAuthorizerFactory(String type)
  {
    super(PRODUCT_GROUP, type);
  }

  /**
   * @since 3.0
   */
  @Override
  public abstract OperationAuthorizer<CONTEXT> create(String description) throws ProductCreationException;
}

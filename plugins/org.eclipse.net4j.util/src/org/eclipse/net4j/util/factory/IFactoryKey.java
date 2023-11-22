/*
 * Copyright (c) 2007, 2011, 2012, 2015, 2019, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

/**
 * Identifies a {@link IFactory factory} by {@link #getProductGroup() product group} and {@link #getType() type}.
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IFactoryKey
{
  /**
   * @since 3.23
   */
  public static final String DEFAULT_FACTORY_TYPE = "default";

  public String getProductGroup();

  public String getType();
}

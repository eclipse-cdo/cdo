/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 3.23
 */
public interface IPropertiesFactory extends IFactory
{
  public Object createWithProperties(Map<String, String> properties) throws ProductCreationException;
}

/*
 * Copyright (c) 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.properties;

import org.eclipse.net4j.util.registry.IRegistry;

/**
 * The container of a {@link IRegistry properties registry}.
 *
 * @since 3.5
 * @author Eike Stepper
 */
public interface IPropertiesContainer
{
  public IRegistry<String, Object> properties();
}

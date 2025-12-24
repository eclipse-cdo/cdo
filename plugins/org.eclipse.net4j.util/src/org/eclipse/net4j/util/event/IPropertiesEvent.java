/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.event;

import java.util.Map;

/**
 * @author Eike Stepper
 * @since 3.29
 */
public interface IPropertiesEvent extends IEvent
{
  public static final String PROP_TYPE = "type";

  public default String type()
  {
    Object type = properties().get(PROP_TYPE);
    return type != null ? type.toString() : null;
  }

  public Map<String, Object> properties();
}

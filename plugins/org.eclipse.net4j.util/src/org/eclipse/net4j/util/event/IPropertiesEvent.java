/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
  public static final String PROP_TYPE = "_type_";

  public default String type()
  {
    Object type = properties().get(PROP_TYPE);
    return type != null ? type.toString() : null;
  }

  public Map<String, Object> properties();
}

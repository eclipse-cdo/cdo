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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Eike Stepper
 * @since 3.29
 */
public class PropertiesEvent extends Event implements IPropertiesEvent
{
  private static final long serialVersionUID = 1L;

  private final Map<String, Object> properties;

  public PropertiesEvent(INotifier notifier, Map<String, Object> properties)
  {
    super(notifier);
    this.properties = Objects.requireNonNull(properties);
  }

  public PropertiesEvent(INotifier notifier)
  {
    this(notifier, new HashMap<>());
  }

  @Override
  public final Map<String, Object> properties()
  {
    return Collections.unmodifiableMap(properties);
  }

  public final Map<String, Object> modifiableProperties()
  {
    return properties;
  }

  public final PropertiesEvent setType(String type)
  {
    return addProperty(PROP_TYPE, type);
  }

  public final PropertiesEvent addProperty(Object value)
  {
    String name = value.getClass().getName();
    return addProperty(name, value);
  }

  public final <T> PropertiesEvent addProperty(Class<T> name, T value)
  {
    return addProperty(name.getName(), value);
  }

  public final PropertiesEvent addProperty(String name, Object value)
  {
    properties.put(name, value);
    return this;
  }

  public final PropertiesEvent removeProperty(String name)
  {
    properties.remove(name);
    return this;
  }

  public final PropertiesEvent removeProperties(String... names)
  {
    for (String name : names)
    {
      removeProperty(name);
    }

    return this;
  }

  public PropertiesEvent fire()
  {
    INotifier source = getSource();
    if (source instanceof Notifier)
    {
      Notifier notifier = (Notifier)source;
      notifier.fireEvent(this);
    }

    return this;
  }

  @Override
  protected String formatAdditionalParameters()
  {
    return properties.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(", "));
  }
}

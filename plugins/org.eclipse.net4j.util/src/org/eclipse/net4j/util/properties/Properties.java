/*
 * Copyright (c) 2011, 2012, 2015, 2019, 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.properties;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Contains a list of {@link Property properties}.
 *
 * @author Eike Stepper
 * @since 3.2
 */
public class Properties<RECEIVER> implements IProperties<RECEIVER>
{
  private final List<Property<RECEIVER>> properties = new ArrayList<>();

  private final Class<RECEIVER> receiverType;

  public Properties(Class<RECEIVER> receiverType)
  {
    this.receiverType = receiverType;
  }

  @Override
  public final Class<RECEIVER> getReceiverType()
  {
    return receiverType;
  }

  @Override
  public final void add(Property<RECEIVER> property)
  {
    CheckUtil.checkArg(property, "property"); //$NON-NLS-1$
    CheckUtil.checkArg(property.getName(), "property.getName()"); //$NON-NLS-1$
    properties.add(property);
  }

  @Override
  public final List<Property<RECEIVER>> getProperties()
  {
    return properties;
  }

  @Override
  public final Property<RECEIVER> getProperty(String name)
  {
    for (Property<RECEIVER> property : properties)
    {
      if (property.getName().equals(name))
      {
        return property;
      }
    }

    return null;
  }

  /**
   * @since 3.28
   */
  public final <TARGET_RECEIVER> Properties<TARGET_RECEIVER> adaptProperties(Class<TARGET_RECEIVER> targetReceiverType, String namePrefix,
      Function<TARGET_RECEIVER, RECEIVER> receiverConverter)
  {
    Properties<TARGET_RECEIVER> result = new Properties<>(targetReceiverType);

    for (Property<RECEIVER> property : properties)
    {
      Property<TARGET_RECEIVER> converted = adaptProperty(namePrefix, property, receiverConverter);
      result.add(converted);
    }

    return result;
  }

  private <TARGET_RECEIVER> Property<TARGET_RECEIVER> adaptProperty(String namePrefix, Property<RECEIVER> property,
      Function<TARGET_RECEIVER, RECEIVER> receiverConverter)
  {
    String name = StringUtil.safe(namePrefix) + property.getName();
    String label = property.getLabel();
    String description = property.getDescription();
    String category = property.getCategory();

    if (property instanceof Property.WithArguments)
    {
      Property.WithArguments<RECEIVER> propertyWithArguments = (Property.WithArguments<RECEIVER>)property;

      return new Property.WithArguments<TARGET_RECEIVER>(name, label, description, category)
      {
        @Override
        protected Object eval(TARGET_RECEIVER targetReceiver, Object[] args)
        {
          RECEIVER receiver = receiverConverter.apply(targetReceiver);
          return propertyWithArguments.eval(receiver, args);
        }
      };
    }

    return new Property<TARGET_RECEIVER>(name, label, description, category)
    {
      @Override
      protected Object eval(TARGET_RECEIVER targetReceiver)
      {
        RECEIVER receiver = receiverConverter.apply(targetReceiver);
        return property.eval(receiver);
      }
    };
  }
}

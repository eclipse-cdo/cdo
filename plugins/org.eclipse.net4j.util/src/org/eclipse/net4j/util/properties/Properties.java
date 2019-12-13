/*
 * Copyright (c) 2011, 2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
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

import java.util.ArrayList;
import java.util.List;

/**
 * Contains a list of {@link Property properties}.
 *
 * @author Eike Stepper
 * @since 3.2
 */
public class Properties<RECEIVER> implements IProperties<RECEIVER>
{
  private final List<Property<RECEIVER>> properties = new ArrayList<Property<RECEIVER>>();

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
    CheckUtil.checkArg(property, "property");
    CheckUtil.checkArg(property.getName(), "property.getName()");
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
}

/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Fuggerstr. 39, 10777 Berlin, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.impl;


import org.eclipse.net4j.core.Channel;

import org.eclipse.emf.cdo.core.impl.AbstractConverter;
import org.eclipse.emf.cdo.server.ColumnConverter;


public class ColumnConverterImpl extends AbstractConverter implements ColumnConverter
{
  public Object fromChannel(Channel channel, int dataType)
  {
    if (dataType > MIN_PRIMITIVE)
    {
      boolean isNull = channel.receiveBoolean();

      if (isNull)
      {
        return null;
      }

      if (dataType < MAX_PRIMITIVE)
      {
        dataType = -dataType;
      }
    }

    return dispatchFromChannel(channel, dataType);
  }

  public void toChannel(Channel channel, int dataType, Object value)
  {
    if (dataType > MIN_PRIMITIVE)
    {
      boolean isNull = value == null;
      channel.transmitBoolean(isNull);

      if (isNull)
      {
        return;
      }

      if (dataType < MAX_PRIMITIVE)
      {
        dataType = -dataType;
      }
    }

    dispatchToChannel(channel, dataType, value);
  }
}

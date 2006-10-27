/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.server.impl;


import org.eclipse.net4j.util.stream.ExtendedDataInput;
import org.eclipse.net4j.util.stream.ExtendedDataOutput;

import org.eclipse.emf.cdo.core.impl.AbstractConverter;
import org.eclipse.emf.cdo.server.ColumnConverter;

import java.io.IOException;


/**
 * @author Eike Stepper
 */
public class ColumnConverterImpl extends AbstractConverter implements ColumnConverter
{
  public Object fromChannel(ExtendedDataInput channel, int dataType) throws IOException
  {
    if (dataType > MIN_PRIMITIVE)
    {
      boolean isNull = channel.readBoolean();

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

  public void toChannel(ExtendedDataOutput channel, int dataType, Object value) throws IOException
  {
    if (dataType > MIN_PRIMITIVE)
    {
      boolean isNull = value == null;
      channel.writeBoolean(isNull);

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

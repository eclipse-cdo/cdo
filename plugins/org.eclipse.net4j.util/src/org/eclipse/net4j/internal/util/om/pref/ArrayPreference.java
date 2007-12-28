/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.internal.util.om.pref;

import org.eclipse.net4j.internal.util.bundle.OM;
import org.eclipse.net4j.util.om.pref.OMPreference;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * @author Eike Stepper
 */
public final class ArrayPreference extends Preference<String[]> implements OMPreference<String[]>
{
  private static final String SEPARATOR = ",";

  private static final String UTF_8 = "UTF-8";

  public ArrayPreference(Preferences preferences, String name, String[] defaultValue)
  {
    super(preferences, name, defaultValue);
  }

  @Override
  protected String getString()
  {
    String[] array = getValue();
    if (array.length == 0)
    {
      return null;
    }

    StringBuilder builder = new StringBuilder();
    for (String element : array)
    {
      if (builder.length() != 0)
      {
        builder.append(SEPARATOR);
        builder.append(" ");
      }

      try
      {
        String encoded = URLEncoder.encode(element, UTF_8);
        builder.append(encoded);
      }
      catch (UnsupportedEncodingException ex)
      {
        OM.LOG.error(ex);
        return null;
      }
    }

    return builder.toString();
  }

  @Override
  protected String[] convert(String value)
  {
    String[] array = value.split(SEPARATOR);
    if (array.length == 0)
    {
      return Preferences.DEFAULT_ARRAY;
    }

    for (int i = 0; i < array.length; i++)
    {
      try
      {
        array[i] = URLDecoder.decode(array[i].trim(), UTF_8);
      }
      catch (UnsupportedEncodingException ex)
      {
        OM.LOG.error(ex);
        return null;
      }
    }

    return array;
  }

  public Type getType()
  {
    return Type.ARRAY;
  }
}

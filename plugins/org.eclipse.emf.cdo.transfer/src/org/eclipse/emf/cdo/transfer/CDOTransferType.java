/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.transfer;

import org.eclipse.emf.cdo.internal.transfer.bundle.OM;

import org.eclipse.net4j.util.ObjectUtil;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 4.2
 */
public class CDOTransferType implements Comparable<CDOTransferType>
{
  /**
   * Must be declared/initialized <b>before</b> any transfer types!
   */
  private static final Map<String, CDOTransferType> MAP = new HashMap<String, CDOTransferType>();

  public static final CDOTransferType UNKNOWN = new CDOTransferType()
  {
    @Override
    public String toString()
    {
      return "<Unknown>";
    }
  };

  public static final CDOTransferType FOLDER = new CDOTransferType()
  {
    @Override
    public String toString()
    {
      return "<Folder>";
    }
  };

  public static final CDOTransferType MODEL = new CDOTransferType()
  {
    @Override
    public String toString()
    {
      return "<Model>";
    }
  };

  public static final CDOTransferType BINARY = new CDOTransferType()
  {
    @Override
    public String toString()
    {
      return "<Binary>";
    }
  };

  public static final Text UTF8 = text("UTF-8");

  public static final Set<CDOTransferType> STANDARD_TYPES = new HashSet<CDOTransferType>(Arrays.asList(UNKNOWN, FOLDER,
      MODEL, BINARY, UTF8));

  public static final Map<String, CDOTransferType> REGISTRY = Collections.unmodifiableMap(MAP);

  private CDOTransferType()
  {
    this(true);
  }

  private CDOTransferType(boolean register)
  {
    if (register)
    {
      MAP.put(toString(), this);
    }
  }

  @Override
  public int hashCode()
  {
    return toString().hashCode();
  }

  @Override
  public boolean equals(Object obj)
  {
    return ObjectUtil.equals(toString(), obj.toString());
  }

  public int compareTo(CDOTransferType o)
  {
    return toString().compareTo(o.toString());
  }

  public static Text text(String encoding)
  {
    CDOTransferType type = MAP.get(encoding);
    if (type instanceof Text)
    {
      return (Text)type;
    }

    if (type == null)
    {
      Text text = new Text(encoding);
      MAP.put(encoding, text);
      return text;
    }

    throw new IllegalArgumentException("Illegal encoding: " + encoding);
  }

  static
  {
    try
    {
      for (Charset charset : Charset.availableCharsets().values())
      {
        try
        {
          text(charset.toString());
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);
        }
      }
    }
    catch (Exception ex)
    {
      OM.LOG.error(ex);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class Text extends CDOTransferType
  {
    private String encoding;

    private Text(String encoding)
    {
      super(false);
      this.encoding = encoding;
    }

    public String getEncoding()
    {
      return encoding;
    }

    @Override
    public String toString()
    {
      return encoding;
    }
  }
}

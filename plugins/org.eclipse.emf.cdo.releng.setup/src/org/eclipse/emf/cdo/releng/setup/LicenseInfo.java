/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.setup;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.StringUtil;

import org.eclipse.equinox.p2.metadata.ILicense;

/**
 * @author Eike Stepper
 */
public final class LicenseInfo
{
  private static final String SEPARATOR = " ";

  private final String uuid;

  private final String name;

  public LicenseInfo(String string)
  {
    CheckUtil.checkArg(string, "string");

    int pos = string.indexOf(SEPARATOR);
    if (pos != -1)
    {
      uuid = string.substring(0, pos);
      name = string.substring(pos + 1).trim();
    }
    else
    {
      uuid = string;
      name = null;
    }
  }

  public LicenseInfo(String uuid, String name)
  {
    CheckUtil.checkArg(uuid, "uuid");

    this.uuid = uuid;
    this.name = name;
  }

  public LicenseInfo(ILicense license)
  {
    this(license.getUUID(), getFirstLine(license.getBody()));
  }

  public String getUUID()
  {
    return uuid;
  }

  public String getName()
  {
    return name;
  }

  public String getLabel()
  {
    if (!StringUtil.isEmpty(name))
    {
      return name;
    }

    return uuid;
  }

  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + (uuid == null ? 0 : uuid.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (obj == null)
    {
      return false;
    }

    if (getClass() != obj.getClass())
    {
      return false;
    }

    LicenseInfo other = (LicenseInfo)obj;
    if (uuid == null)
    {
      if (other.uuid != null)
      {
        return false;
      }
    }
    else if (!uuid.equals(other.uuid))
    {
      return false;
    }

    return true;
  }

  @Override
  public String toString()
  {
    String string = uuid;
    if (!StringUtil.isEmpty(name))
    {
      string += SEPARATOR + name;
    }

    return string;
  }

  public static String getFirstLine(String body)
  {
    int i = body.indexOf('\n');
    int j = body.indexOf('\r');
    if (i > 0)
    {
      if (j > 0)
      {
        return body.substring(0, i < j ? i : j);
      }

      return body.substring(0, i);
    }

    if (j > 0)
    {
      return body.substring(0, j);
    }

    return body;
  }
}

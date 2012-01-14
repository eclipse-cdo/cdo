/**
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.tasks.table;

/**
 * @author Eike Stepper
 */
public final class Version implements Comparable<Version>
{
  public static final Version NONE = new Version(0, 0);

  private int major;

  private int minor;

  private Version(int major, int minor)
  {
    this.major = major;
    this.minor = minor;
  }

  public int getMajor()
  {
    return major;
  }

  public int getMinor()
  {
    return minor;
  }

  public int compareTo(Version o)
  {
    return major < o.major ? 1 : major == o.major ? (minor < o.minor ? 1 : minor == o.minor ? 0 : -1) : -1;
  }

  @Override
  public int hashCode()
  {
    return 1000 * major ^ minor;
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj instanceof Version)
    {
      Version that = (Version)obj;
      return major == that.major && minor == that.minor;
    }

    return false;
  }

  @Override
  public String toString()
  {
    return major + "." + minor;
  }

  public static Version getVersion(String version)
  {
    try
    {
      String[] segments = version.split("\\.");
      int major = 0;
      int minor = 0;

      if (segments.length >= 1)
      {
        major = Integer.parseInt(segments[0]);
      }

      if (segments.length >= 2)
      {
        minor = Integer.parseInt(segments[1]);
      }

      return new Version(major, minor);
    }
    catch (Exception ex)
    {
      return NONE;
    }
  }
}

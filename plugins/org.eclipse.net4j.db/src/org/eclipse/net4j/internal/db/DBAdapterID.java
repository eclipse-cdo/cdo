/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.internal.db;

import org.eclipse.net4j.db.IDBAdapterID;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
public final class DBAdapterID implements IDBAdapterID, Comparable<DBAdapterID>
{
  private final String name;

  private final String version;

  public DBAdapterID(String name, String version)
  {
    DBAdapterID.validateName(name);
    DBAdapterID.parseVersion(version);
    this.name = name.toLowerCase(Locale.ROOT);
    this.version = version == null ? "" : version.trim();
  }

  @Override
  public String getName()
  {
    return name;
  }

  @Override
  public String getVersion()
  {
    return version;
  }

  @Override
  public int compareTo(DBAdapterID other)
  {
    int result = name.compareTo(other.name);
    return result != 0 ? result : DBAdapterID.compareVersions(version, other.version);
  }

  @Override
  public int hashCode()
  {
    return 31 * name.hashCode() + normalizedVersionHash(version);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (this == obj)
    {
      return true;
    }

    if (!(obj instanceof DBAdapterID))
    {
      return false;
    }

    DBAdapterID other = (DBAdapterID)obj;
    return name.equals(other.name) && DBAdapterID.compareVersions(version, other.version) == 0;
  }

  public static DBAdapterID copy(IDBAdapterID src)
  {
    Objects.requireNonNull(src, "id");
    return new DBAdapterID(src.getName(), src.getVersion());
  }

  public static void validateName(String name)
  {
    if (name == null || name.trim().isEmpty())
    {
      throw new IllegalArgumentException("Adapter name must not be null or empty");
    }
  }

  public static List<BigInteger> parseVersion(String version)
  {
    if (version == null || version.trim().isEmpty())
    {
      return List.of(BigInteger.ZERO);
    }

    String value = version.trim();
    String[] components = value.split("\\.", -1);
    List<BigInteger> result = new ArrayList<>(components.length);
    for (String component : components)
    {
      if (component.isEmpty() || !component.chars().allMatch(Character::isDigit))
      {
        throw new IllegalArgumentException("Invalid adapter version: " + version);
      }

      result.add(new BigInteger(component));
    }

    return result;
  }

  public static int compareVersions(String left, String right)
  {
    List<BigInteger> a = parseVersion(left);
    List<BigInteger> b = parseVersion(right);
    int size = Math.max(a.size(), b.size());
    for (int i = 0; i < size; i++)
    {
      BigInteger av = i < a.size() ? a.get(i) : BigInteger.ZERO;
      BigInteger bv = i < b.size() ? b.get(i) : BigInteger.ZERO;
      int result = av.compareTo(bv);
      if (result != 0)
      {
        return result;
      }
    }

    return 0;
  }

  private static int normalizedVersionHash(String version)
  {
    List<BigInteger> components = DBAdapterID.parseVersion(version);
    int last = components.size() - 1;
    while (last > 0 && BigInteger.ZERO.equals(components.get(last)))
    {
      --last;
    }

    int result = 1;
    for (int i = 0; i <= last; i++)
    {
      result = 31 * result + components.get(i).hashCode();
    }

    return result;
  }
}

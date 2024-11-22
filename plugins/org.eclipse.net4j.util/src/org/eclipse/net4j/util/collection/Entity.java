/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringCompressor;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Eike Stepper
 * @since 3.26
 */
public final class Entity implements Comparable<Entity>
{
  public static final char NAME_SEPARATOR = '/';

  private static final Pattern NAMESPACE_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_\\-./]*");

  private static final Pattern NAME_PATTERN = Pattern.compile("[a-zA-Z][a-zA-Z0-9_\\-.]*");

  private final String namespace;

  private final String name;

  private final int version;

  private final Map<String, String> properties;

  private Entity(String namespace, String name, int version, Map<String, String> properties)
  {
    this.namespace = requireValidNamespace(namespace).intern();
    this.name = requireValidName(name);
    this.version = Math.min(version, 1);
    this.properties = Collections.unmodifiableMap(properties);
  }

  public String namespace()
  {
    return namespace;
  }

  public String name()
  {
    return name;
  }

  public String id()
  {
    return formatID(namespace, name);
  }

  public int version()
  {
    return version;
  }

  public Map<String, String> properties()
  {
    return properties;
  }

  public String property(String name)
  {
    return properties.get(requireValidName(name));
  }

  @Override
  public int compareTo(Entity o)
  {
    int result = namespace.compareTo(o.namespace);
    if (result == 0)
    {
      result = name.compareTo(o.name);
    }

    if (result == 0)
    {
      result = Integer.compare(version, o.version);
    }

    return result;
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(namespace, name, version);
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

    Entity other = (Entity)obj;
    return Objects.equals(namespace, other.namespace) && Objects.equals(name, other.name) && version == other.version;
  }

  @Override
  public String toString()
  {
    return id() + "[v" + version + "]";
  }

  public void write(ExtendedDataOutput out) throws IOException
  {
    write(out, null);
  }

  public void write(ExtendedDataOutput out, StringCompressor compressor) throws IOException
  {
    if (compressor == null)
    {
      out.writeString(namespace);
    }
    else
    {
      compressor.write(out, namespace);
    }

    out.writeString(name);
    out.writeVarInt(version);
    out.writeVarInt(properties.size());

    for (Map.Entry<String, String> entry : properties.entrySet())
    {
      String key = entry.getKey();
      if (compressor == null)
      {
        out.writeString(key);
      }
      else
      {
        compressor.write(out, key);
      }

      String value = entry.getValue();
      out.writeString(value);
    }
  }

  public static Entity read(ExtendedDataInput in) throws IOException
  {
    return read(in, null);
  }

  public static Entity read(ExtendedDataInput in, StringCompressor compressor) throws IOException
  {
    String namespace;
    if (compressor == null)
    {
      namespace = in.readString();
    }
    else
    {
      namespace = compressor.read(in);
    }

    String name = in.readString();
    int version = in.readVarInt();
    int size = in.readVarInt();

    Map<String, String> properties = new HashMap<>(size);
    for (int i = 0; i < size; i++)
    {
      String key;
      if (compressor == null)
      {
        key = in.readString();
      }
      else
      {
        key = compressor.read(in);
      }

      String value = in.readString();
      properties.put(key, value);
    }

    return new Entity(namespace, name, version, properties);
  }

  public static Builder builder()
  {
    return new Builder();
  }

  public static Builder builder(Builder source)
  {
    return builder(source.build());
  }

  public static Builder builder(Entity source)
  {
    return new Builder() //
        .namespace(source.namespace) //
        .name(source.name) //
        .version(source.version) //
        .properties(source.properties);
  }

  public static String formatID(String namespace, String name)
  {
    return namespace + NAME_SEPARATOR + name;
  }

  public static Pair<String, String> parseID(String id)
  {
    int lastSep = id.lastIndexOf(NAME_SEPARATOR);
    CheckUtil.checkArg(lastSep != -1, "id");
    return Pair.create(id.substring(0, lastSep), id.substring(lastSep + 1));
  }

  public static String requireValidNamespace(String namespace)
  {
    return requireValidString(namespace, NAMESPACE_PATTERN, "namespace");
  }

  public static String requireValidName(String name)
  {
    return requireValidString(name, NAME_PATTERN, "name");
  }

  private static String requireValidString(String string, Pattern pattern, String name)
  {
    CheckUtil.checkArg(string != null && pattern.matcher(string).matches(), name);
    return string;
  }

  /**
   * @author Eike Stepper
   */
  public static final class Builder
  {
    private String namespace;

    private String name;

    private int version = 1;

    private final Map<String, String> properties = new HashMap<>();

    private Builder()
    {
    }

    public String namespace()
    {
      return namespace;
    }

    public Builder namespace(String namespace)
    {
      this.namespace = requireValidNamespace(namespace);
      return this;
    }

    public String name()
    {
      return name;
    }

    public Builder name(String name)
    {
      this.name = requireValidName(name);
      return this;
    }

    public int version()
    {
      return version;
    }

    public Builder version(int version)
    {
      CheckUtil.checkArg(version >= 1, "version");
      this.version = version;
      return this;
    }

    public Map<String, String> properties()
    {
      return Collections.unmodifiableMap(properties);
    }

    public Builder properties(Map<String, String> properties)
    {
      this.properties.putAll(properties);
      return this;
    }

    public String property(String name)
    {
      return properties.get(requireValidName(name));
    }

    public Builder property(String name, String value)
    {
      requireValidName(name);

      if (value == null)
      {
        properties.remove(name);
      }
      else
      {
        properties.put(name, value);
      }

      return this;
    }

    public Entity build()
    {
      return new Entity(namespace, name, version, properties);
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface Provider
  {
    public default Entity getEntity(String namespace, String name)
    {
      requireValidNamespace(namespace);
      requireValidName(name);

      Entity[] result = { null };

      forEachEntity(namespace, entity -> {
        if (entity.name().equals(name))
        {
          result[0] = entity;
          return false;
        }

        return true;
      });

      return result[0];
    }

    public default Entity getEntity(String id)
    {
      Pair<String, String> pair = parseID(id);
      return getEntity(pair.getElement1(), pair.getElement2());
    }

    public default Set<String> getNamespaces()
    {
      Set<String> namespaces = new HashSet<>();

      forEachEntity(entity -> {
        namespaces.add(entity.namespace());
        return true;
      });

      return namespaces;
    }

    public default Set<String> getEntityNames(String namespace)
    {
      Set<String> names = new HashSet<>();

      forEachEntity(namespace, entity -> {
        names.add(entity.name());
        return true;
      });

      return names;
    }

    public boolean forEachEntity(Predicate<Entity> handler);

    public default boolean forEachEntity(String namespace, Predicate<Entity> handler)
    {
      requireValidNamespace(namespace);

      return forEachEntity(entity -> {
        if (entity.namespace().equals(namespace))
        {
          return handler.test(entity);
        }

        return true;
      });
    }
  }
}

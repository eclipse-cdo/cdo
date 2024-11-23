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
import org.eclipse.net4j.util.concurrent.RWLock;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringCompressor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;
import java.util.stream.Stream;

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
    public default Stream<String> namespaces()
    {
      return entities().map(Entity::namespace).distinct();
    }

    public default Stream<String> names(String namespace)
    {
      requireValidNamespace(namespace);
      return entities(namespace).map(Entity::name).distinct();
    }

    public Stream<Entity> entities();

    public default Stream<Entity> entities(String namespace)
    {
      requireValidNamespace(namespace);
      return entities().filter(entity -> entity.namespace().equals(namespace));
    }

    public default Entity entity(String namespace, String name)
    {
      requireValidNamespace(namespace);
      requireValidName(name);
      return entities(namespace).filter(entity -> entity.name().equals(name)).findFirst().orElse(null);
    }

    public default Entity entity(String id)
    {
      Pair<String, String> pair = parseID(id);
      return entity(pair.getElement1(), pair.getElement2());
    }

    /**
     * @author Eike Stepper
     */
    public interface Supplier
    {
      public Provider getEntityProvider();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class NamespaceProvider implements Provider
  {
    private final String namespace;

    private final Map<String, Entity> entities = new HashMap<>();

    public NamespaceProvider(String namespace)
    {
      this.namespace = requireValidNamespace(namespace);
    }

    public Entity addEntity(Entity entity)
    {
      if (!entity.namespace.equals(namespace))
      {
        throw new IllegalArgumentException("Namespace mismatch");
      }

      return entities.put(entity.name(), entity);
    }

    public Entity removeEntity(String name)
    {
      return entities.remove(name);
    }

    public String namespace()
    {
      return namespace;
    }

    @Override
    public Stream<String> namespaces()
    {
      return Stream.of(namespace);
    }

    public Stream<String> names()
    {
      return names(namespace);
    }

    @Override
    public Stream<String> names(String namespace)
    {
      if (!this.namespace.equals(namespace))
      {
        return Stream.empty();
      }

      return entities.keySet().stream();
    }

    @Override
    public Stream<Entity> entities()
    {
      return entities.values().stream();
    }

    @Override
    public Stream<Entity> entities(String namespace)
    {
      if (!this.namespace.equals(namespace))
      {
        return Stream.empty();
      }

      return entities();
    }

    @Override
    public Entity entity(String namespace, String name)
    {
      if (!this.namespace.equals(namespace))
      {
        return null;
      }

      return entities.get(name);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class NamespaceComputer implements Provider
  {
    private final String namespace;

    public NamespaceComputer(String namespace)
    {
      this.namespace = requireValidNamespace(namespace);
    }

    public final String namespace()
    {
      return namespace;
    }

    @Override
    public final Stream<String> namespaces()
    {
      return Stream.of(namespace);
    }

    public Stream<String> names()
    {
      return names(namespace);
    }

    @Override
    public Stream<String> names(String namespace)
    {
      return computeNames().stream();
    }

    @Override
    public Stream<Entity> entities()
    {
      return names().map(this::computeEntity);
    }

    @Override
    public Stream<Entity> entities(String namespace)
    {
      if (!this.namespace.equals(namespace))
      {
        return Stream.empty();
      }

      return entities();
    }

    @Override
    public Entity entity(String namespace, String name)
    {
      if (!this.namespace.equals(namespace))
      {
        return null;
      }

      return computeEntity(requireValidName(name));
    }

    protected abstract Collection<String> computeNames();

    protected abstract Entity computeEntity(String name);
  }

  /**
   * @author Eike Stepper
   */
  public static final class ComposedProvider implements Provider
  {
    private final List<Provider> providers = new ArrayList<>();

    private final Map<String, List<Provider>> providersByNamespace = new HashMap<>();

    public ComposedProvider()
    {
    }

    public void addProvider(Provider provider)
    {
      if (providers.add(provider))
      {
        provider.namespaces().forEach(namespace -> {
          providersByNamespace.computeIfAbsent(namespace, k -> new ArrayList<>()).add(provider);
        });
      }
    }

    public void removeProvider(Provider provider)
    {
      if (providers.add(provider))
      {
        provider.namespaces().forEach(namespace -> {
          List<Provider> providers = providersByNamespace.get(namespace);
          if (providers != null)
          {
            providers.remove(provider);
          }
        });
      }
    }

    @Override
    public Stream<String> namespaces()
    {
      return providersByNamespace.keySet().stream();
    }

    @Override
    public Stream<String> names(String namespace)
    {
      requireValidNamespace(namespace);

      List<Provider> providers = providersByNamespace.get(namespace);
      int size = providers == null ? 0 : providers.size();
      if (size == 0)
      {
        return Stream.empty();
      }

      if (size == 1)
      {
        return providers.get(0).names(namespace);
      }

      List<Stream<String>> streams = new ArrayList<>(size);
      for (Provider provider : providers)
      {
        streams.add(provider.names(namespace));
      }

      return CollectionUtil.concat(streams).distinct();
    }

    @Override
    public Stream<Entity> entities()
    {
      int size = providers.size();
      if (size == 0)
      {
        return Stream.empty();
      }

      if (size == 1)
      {
        return providers.get(0).entities();
      }

      List<Stream<Entity>> streams = new ArrayList<>(size);
      for (Provider provider : providers)
      {
        streams.add(provider.entities());
      }

      return CollectionUtil.concat(streams).distinct();
    }

    @Override
    public Stream<Entity> entities(String namespace)
    {
      requireValidNamespace(namespace);

      List<Provider> providers = providersByNamespace.get(namespace);
      int size = providers.size();
      if (size == 0)
      {
        return Stream.empty();
      }

      if (size == 1)
      {
        return providers.get(0).entities(namespace);
      }

      List<Stream<Entity>> streams = new ArrayList<>(size);
      for (Provider provider : providers)
      {
        streams.add(provider.entities(namespace));
      }

      return CollectionUtil.concat(streams).distinct();
    }

    @Override
    public Entity entity(String namespace, String name)
    {
      requireValidNamespace(namespace);
      requireValidName(name);

      return entities(namespace).filter(entity -> entity.name().equals(name)).findFirst().orElse(null);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ConcurrentProvider implements Provider
  {
    private final Provider delegate;

    private final RWLock lock;

    public ConcurrentProvider(Provider delegate)
    {
      this(delegate, 1000L);
    }

    public ConcurrentProvider(Provider delegate, long timeoutMillis)
    {
      this.delegate = delegate;
      lock = new RWLock(timeoutMillis);
    }

    @Override
    public Stream<String> namespaces()
    {
      return read(() -> delegate.namespaces());
    }

    @Override
    public Stream<String> names(String namespace)
    {
      return read(() -> delegate.names(namespace));
    }

    @Override
    public Stream<Entity> entities()
    {
      return read(() -> delegate.entities());
    }

    @Override
    public Stream<Entity> entities(String namespace)
    {
      return read(() -> delegate.entities(namespace));
    }

    @Override
    public Entity entity(String namespace, String name)
    {
      return read(() -> delegate.entity(namespace, name));
    }

    @Override
    public Entity entity(String id)
    {
      return read(() -> delegate.entity(id));
    }

    public <V> V read(Callable<V> callable)
    {
      return lock.read(callable);
    }

    public void read(Runnable runnable)
    {
      lock.read(runnable);
    }

    public <V> V write(Callable<V> callable)
    {
      return lock.write(callable);
    }

    public void write(Runnable runnable)
    {
      lock.write(runnable);
    }
  }
}

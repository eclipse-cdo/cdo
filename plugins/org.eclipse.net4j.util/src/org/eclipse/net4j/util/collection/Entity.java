/*
 * Copyright (c) 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.collection;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.RWLock;
import org.eclipse.net4j.util.io.ExtendedDataInput;
import org.eclipse.net4j.util.io.ExtendedDataOutput;
import org.eclipse.net4j.util.io.StringCompressor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Consumer;
import java.util.function.Predicate;
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

  /**
   * @since 3.27
   */
  public String property(String name, String defaultValue)
  {
    String value = property(name);
    return value != null ? value : defaultValue;
  }

  /**
   * @since 3.27
   */
  public boolean property(String name, boolean defaultValue)
  {
    String value = property(name);
    return value != null ? Boolean.parseBoolean(value) : defaultValue;
  }

  /**
   * @since 3.27
   */
  public byte property(String name, byte defaultValue)
  {
    String value = property(name);
    return value != null ? Byte.parseByte(value) : defaultValue;
  }

  /**
   * @since 3.27
   */
  public int property(String name, int defaultValue)
  {
    String value = property(name);
    return value != null ? Integer.parseInt(value) : defaultValue;
  }

  /**
   * @since 3.27
   */
  public long property(String name, long defaultValue)
  {
    String value = property(name);
    return value != null ? Long.parseLong(value) : defaultValue;
  }

  /**
   * @since 3.27
   */
  public float property(String name, float defaultValue)
  {
    String value = property(name);
    return value != null ? Float.parseFloat(value) : defaultValue;
  }

  /**
   * @since 3.27
   */
  public double property(String name, double defaultValue)
  {
    String value = property(name);
    return value != null ? Double.parseDouble(value) : defaultValue;
  }

  public Entity filter(String... propertyNames)
  {
    return filter(Arrays.asList(propertyNames));
  }

  public Entity filter(Collection<String> propertyNames)
  {
    return filter(propertyName -> propertyNames.contains(propertyName));
  }

  public Entity filter(Predicate<String> propertyNameFilter)
  {
    return builder(this).retain(propertyNameFilter).build();
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

  public static Builder builder(String namespace)
  {
    return builder().namespace(namespace);
  }

  public static Builder builder(String namespace, String name)
  {
    return builder(namespace).name(name);
  }

  /**
   * @since 3.27
   */
  public static Builder builder(Tree tree)
  {
    Builder builder = builder();

    String namespace = tree.attribute("namespace");
    if (!StringUtil.isEmpty(namespace))
    {
      builder.namespace(namespace);
    }

    String name = tree.attribute("name");
    if (!StringUtil.isEmpty(name))
    {
      builder.name(name);
    }

    String version = tree.attribute("version");
    if (!StringUtil.isEmpty(version))
    {
      builder.version(Integer.parseInt(version));
    }

    tree.children("property", property -> {
      String propertyName = property.attribute("name");
      String propertyValue = property.attribute("value");
      if (!StringUtil.isEmpty(propertyName) && !StringUtil.isEmpty(propertyValue))
      {
        builder.property(propertyName, propertyValue);
      }
    });

    return builder;
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
    CheckUtil.checkArg(lastSep != -1, "Illegal id");
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

  private static String requireValidString(String string, Pattern pattern, String arg)
  {
    if (StringUtil.isEmpty(string))
    {
      throw new IllegalArgumentException("Missing " + arg);
    }

    if (!pattern.matcher(string).matches())
    {
      throw new IllegalArgumentException("Illegal " + arg + ": " + string);
    }

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

    private List<Consumer<Builder>> preBuildHandlers;

    private List<Consumer<Entity>> postBuildHandlers;

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
      CheckUtil.checkArg(version >= 1, "Illegal version");
      this.version = version;
      return this;
    }

    public Map<String, String> properties()
    {
      return Collections.unmodifiableMap(properties);
    }

    public Builder properties(Map<String, String> properties)
    {
      if (properties != null)
      {
        this.properties.putAll(properties);
      }

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

    public Builder remove(Predicate<String> namePredicate)
    {
      for (Iterator<String> it = properties.keySet().iterator(); it.hasNext();)
      {
        String name = it.next();
        if (namePredicate.test(name))
        {
          it.remove();
        }
      }

      return this;
    }

    public Builder retain(Predicate<String> namePredicate)
    {
      for (Iterator<String> it = properties.keySet().iterator(); it.hasNext();)
      {
        String name = it.next();
        if (!namePredicate.test(name))
        {
          it.remove();
        }
      }

      return this;
    }

    public Builder preBuild(Consumer<Builder> handler)
    {
      if (preBuildHandlers == null)
      {
        preBuildHandlers = new ArrayList<>(1);
      }

      preBuildHandlers.add(handler);
      return this;
    }

    public Builder postBuild(Consumer<Entity> handler)
    {
      if (postBuildHandlers == null)
      {
        postBuildHandlers = new ArrayList<>(1);
      }

      postBuildHandlers.add(handler);
      return this;
    }

    public Entity build()
    {
      if (preBuildHandlers != null)
      {
        for (Consumer<Builder> handler : preBuildHandlers)
        {
          handler.accept(this);
        }
      }

      Entity entity = new Entity(namespace, name, version, properties);

      if (postBuildHandlers != null)
      {
        for (Consumer<Entity> handler : postBuildHandlers)
        {
          handler.accept(entity);
        }
      }

      return entity;
    }
  }

  /**
   * @author Eike Stepper
   */
  public interface Store
  {
    public default boolean namespace(String namespace)
    {
      requireValidNamespace(namespace);
      return namespaces().anyMatch(n -> n.equals(namespace));
    }

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

    public default Store store(String namespace)
    {
      requireValidNamespace(namespace);

      return new Store()
      {
        @Override
        public boolean namespace(String n)
        {
          return namespace.equals(n);
        }

        @Override
        public Stream<String> namespaces()
        {
          return Stream.of(namespace);
        }

        @Override
        public Stream<String> names(String n)
        {
          if (!namespace.equals(n))
          {
            return Stream.empty();
          }

          return Store.super.names(n);
        }

        @Override
        public Stream<Entity> entities()
        {
          return entities(namespace);
        }

        @Override
        public Entity entity(String n, String name)
        {
          if (!namespace.equals(n))
          {
            return null;
          }

          return Store.super.entity(n, name);
        }

        @Override
        public Store store(String n)
        {
          if (!namespace.equals(n))
          {
            return null;
          }

          return this;
        }
      };
    }

    public static Store of(Object object)
    {
      if (object instanceof Store)
      {
        return (Store)object;
      }

      if (object instanceof Store.Provider)
      {
        return ((Store.Provider)object).getEntityStore();
      }

      return null;
    }

    /**
     * @author Eike Stepper
     */
    public interface Provider
    {
      public Store getEntityStore();
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class SingleNamespace implements Store
  {
    private final String namespace;

    public SingleNamespace(String namespace)
    {
      this.namespace = requireValidNamespace(namespace);
    }

    public final String namespace()
    {
      return namespace;
    }

    @Override
    public final boolean namespace(String namespace)
    {
      return this.namespace.equals(namespace);
    }

    @Override
    public final Stream<String> namespaces()
    {
      return Stream.of(namespace);
    }

    public final Stream<String> names()
    {
      return names(namespace);
    }

    @Override
    public final Stream<Entity> entities(String namespace)
    {
      if (!this.namespace.equals(namespace))
      {
        return Stream.empty();
      }

      return entities();
    }

    public final Builder entityBuilder()
    {
      return builder(namespace);
    }

    public final Builder entityBuilder(String name)
    {
      return entityBuilder().name(name);
    }

    @Override
    public final Store store(String namespace)
    {
      if (!this.namespace.equals(namespace))
      {
        return null;
      }

      return this;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class SingleNamespaceStore extends SingleNamespace
  {
    private final Map<String, Entity> entities = new HashMap<>();

    public SingleNamespaceStore(String namespace)
    {
      super(namespace);
    }

    public Builder addEntity()
    {
      return entityBuilder().postBuild(this::addEntity);
    }

    public Builder addEntity(String name)
    {
      return addEntity().name(name);
    }

    public Entity addEntity(Entity entity)
    {
      if (!namespace().equals(entity.namespace))
      {
        throw new IllegalArgumentException("Namespace mismatch");
      }

      return entities.put(entity.name(), entity);
    }

    public Entity removeEntity(String name)
    {
      return entities.remove(name);
    }

    @Override
    public Stream<String> names(String namespace)
    {
      if (!namespace().equals(namespace))
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
    public Entity entity(String namespace, String name)
    {
      if (!namespace().equals(namespace))
      {
        return null;
      }

      return entities.get(name);
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class SingleNamespaceComputer extends SingleNamespace
  {
    public SingleNamespaceComputer(String namespace)
    {
      super(namespace);
    }

    @Override
    public final Stream<String> names(String namespace)
    {
      return computeNames().stream();
    }

    @Override
    public final Stream<Entity> entities()
    {
      return names().map(this::computeEntity);
    }

    @Override
    public final Entity entity(String namespace, String name)
    {
      if (!namespace().equals(namespace))
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
  public static final class ComposedStore implements Store
  {
    private final List<Store> stores = new ArrayList<>();

    private final Map<String, List<Store>> storesByNamespace = new HashMap<>();

    public ComposedStore()
    {
    }

    public ComposedStore addStore(Store store)
    {
      if (store != null)
      {
        if (stores.add(store))
        {
          store.namespaces().forEach(namespace -> {
            storesByNamespace.computeIfAbsent(namespace, k -> new ArrayList<>()).add(store);
          });
        }
      }

      return this;
    }

    public ComposedStore removeStore(Store store)
    {
      if (store != null)
      {
        if (stores.remove(store))
        {
          store.namespaces().forEach(namespace -> {
            List<Store> namespaceStores = storesByNamespace.get(namespace);
            if (namespaceStores != null)
            {
              namespaceStores.remove(store);
            }
          });
        }
      }

      return this;
    }

    @Override
    public Stream<String> namespaces()
    {
      return storesByNamespace.keySet().stream();
    }

    @Override
    public Stream<String> names(String namespace)
    {
      requireValidNamespace(namespace);

      List<Store> namespaceStores = storesByNamespace.get(namespace);
      int size = namespaceStores == null ? 0 : namespaceStores.size();
      if (size == 0)
      {
        return Stream.empty();
      }

      if (size == 1)
      {
        return namespaceStores.get(0).names(namespace);
      }

      List<Stream<String>> streams = new ArrayList<>(size);
      for (Store store : namespaceStores)
      {
        streams.add(store.names(namespace));
      }

      return CollectionUtil.concat(streams).distinct();
    }

    @Override
    public Stream<Entity> entities()
    {
      int size = stores.size();
      if (size == 0)
      {
        return Stream.empty();
      }

      if (size == 1)
      {
        return stores.get(0).entities();
      }

      List<Stream<Entity>> streams = new ArrayList<>(size);
      for (Store store : stores)
      {
        streams.add(store.entities());
      }

      return CollectionUtil.concat(streams).distinct();
    }

    @Override
    public Stream<Entity> entities(String namespace)
    {
      requireValidNamespace(namespace);

      List<Store> namespaceStores = storesByNamespace.get(namespace);
      int size = namespaceStores == null ? 0 : namespaceStores.size();
      if (size == 0)
      {
        return Stream.empty();
      }

      if (size == 1)
      {
        return namespaceStores.get(0).entities(namespace);
      }

      List<Stream<Entity>> streams = new ArrayList<>(size);
      for (Store store : namespaceStores)
      {
        streams.add(store.entities(namespace));
      }

      return CollectionUtil.concat(streams).distinct();
    }

    @Override
    public Entity entity(String namespace, String name)
    {
      requireValidNamespace(namespace);
      requireValidName(name);

      List<Store> namespaceStores = storesByNamespace.get(namespace);
      int size = namespaceStores == null ? 0 : namespaceStores.size();
      if (size == 0)
      {
        return null;
      }

      if (size == 1)
      {
        return namespaceStores.get(0).entity(namespace, name);
      }

      for (Store store : namespaceStores)
      {
        Entity entity = store.entity(namespace, name);
        if (entity != null)
        {
          return entity;
        }
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static final class ConcurrentStore implements Store
  {
    private final Store delegate;

    private final RWLock lock;

    public ConcurrentStore(Store delegate)
    {
      this(delegate, 1000L);
    }

    public ConcurrentStore(Store delegate, long timeoutMillis)
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

/*
 * Copyright (c) 2026 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.emf.cdo.common.revision;

import org.eclipse.emf.cdo.common.model.CDOClassifierRef;
import org.eclipse.emf.cdo.common.model.CDOFeatureRef;
import org.eclipse.emf.cdo.common.model.CDOModelElementRef;
import org.eclipse.emf.cdo.common.model.CDOPackageRef;
import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * An immutable collection-loading configuration snapshot.
 * <p>
 * A {@code null} configuration means that modern partial collection loading is disabled. A non-null configuration,
 * including an empty configuration, enables the mode. The configuration itself only describes runtime defaults and
 * explicit model-element overrides; it does not evaluate metamodel annotations or perform collection loading.
 *
 * @author Eike Stepper
 * @since 4.38
 */
public final class CDOCollectionLoadingConfig
{
  /**
   * The wire kind of an {@link EPackage} key.
   */
  private static final byte PACKAGE = 0;

  /**
   * The wire kind of an {@link EClass} key.
   */
  private static final byte CLASS = 1;

  /**
   * The wire kind of an {@link EStructuralFeature} key.
   */
  private static final byte FEATURE = 2;

  private final ChunkConfig defaultChunkConfig;

  private final Map<CDOModelElementRef<?>, ChunkConfig> overrides;

  /**
   * Creates an empty enabled configuration.
   */
  public CDOCollectionLoadingConfig()
  {
    this(null, Collections.emptyMap());
  }

  /**
   * Creates an immutable configuration snapshot.
   *
   * @param defaultChunkConfig the optional model-independent default
   * @param overrides the explicit package, class, and feature overrides, keyed by materialized EMF elements or
   *          symbolic model-element references
   * @throws IllegalArgumentException if an override key is not a supported model element or symbolic reference
   */
  public CDOCollectionLoadingConfig(ChunkConfig defaultChunkConfig, Map<?, ChunkConfig> overrides)
  {
    this.defaultChunkConfig = defaultChunkConfig;
    Map<CDOModelElementRef<?>, ChunkConfig> copy = new LinkedHashMap<>();

    for (Map.Entry<?, ChunkConfig> entry : Objects.requireNonNull(overrides, "overrides").entrySet())
    {
      CDOModelElementRef<?> key = toRef(Objects.requireNonNull(entry.getKey(), "override key"));
      ChunkConfig value = Objects.requireNonNull(entry.getValue(), "override value");

      copy.put(key, value);
    }

    this.overrides = Collections.unmodifiableMap(copy);
  }

  /**
   * Returns the optional model-independent default.
   *
   * @return the default, or {@code null}
   */
  public ChunkConfig getDefaultChunkConfig()
  {
    return defaultChunkConfig;
  }

  /**
   * Returns the immutable explicit-override snapshot.
   *
   * @return the overrides keyed by symbolic model-element references
   */
  public Map<CDOModelElementRef<?>, ChunkConfig> getOverrides()
  {
    return overrides;
  }

  public ChunkConfig get(EPackage ePackage)
  {
    return get(new CDOPackageRef(ePackage));
  }

  public ChunkConfig get(EClass eClass)
  {
    return get(new CDOClassifierRef(eClass));
  }

  public ChunkConfig get(EStructuralFeature feature)
  {
    return get(new CDOFeatureRef(feature));
  }

  public ChunkConfig get(CDOModelElementRef<?> ref)
  {
    return overrides.get(Objects.requireNonNull(ref, "ref"));
  }

  /**
   * Writes this complete configuration snapshot.
   *
   * @param out the protocol output
   * @throws IOException if the snapshot cannot be written
   */
  public void write(CDODataOutput out) throws IOException
  {
    out.writeBoolean(true);
    out.writeBoolean(defaultChunkConfig != null);

    if (defaultChunkConfig != null)
    {
      defaultChunkConfig.write(out);
    }

    out.writeXInt(overrides.size());
    for (Map.Entry<CDOModelElementRef<?>, ChunkConfig> entry : overrides.entrySet())
    {
      CDOModelElementRef<?> key = entry.getKey();
      if (key instanceof CDOPackageRef)
      {
        out.writeByte(PACKAGE);
        ((CDOPackageRef)key).write(out);
      }
      else if (key instanceof CDOClassifierRef)
      {
        out.writeByte(CLASS);
        ((CDOClassifierRef)key).write(out);
      }
      else
      {
        out.writeByte(FEATURE);
        ((CDOFeatureRef)key).write(out);
      }

      entry.getValue().write(out);
    }
  }

  @Override
  public int hashCode()
  {
    return Objects.hash(defaultChunkConfig, overrides);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (!(obj instanceof CDOCollectionLoadingConfig))
    {
      return false;
    }

    CDOCollectionLoadingConfig other = (CDOCollectionLoadingConfig)obj;
    return Objects.equals(defaultChunkConfig, other.defaultChunkConfig) && Objects.equals(overrides, other.overrides);
  }

  @Override
  public String toString()
  {
    return "CDOCollectionLoadingConfig[default=" + defaultChunkConfig + ", overrides=" + overrides + "]";
  }

  private static CDOModelElementRef<?> toRef(Object key)
  {
    if (key instanceof CDOModelElementRef)
    {
      if (key instanceof CDOPackageRef || key instanceof CDOClassifierRef || key instanceof CDOFeatureRef)
      {
        return (CDOModelElementRef<?>)key;
      }

      throw new IllegalArgumentException("Unsupported collection-loading override key: " + key);
    }

    if (key instanceof EPackage)
    {
      return new CDOPackageRef((EPackage)key);
    }

    if (key instanceof EClass)
    {
      return new CDOClassifierRef((EClass)key);
    }

    if (key instanceof EStructuralFeature)
    {
      return new CDOFeatureRef((EStructuralFeature)key);
    }

    throw new IllegalArgumentException("Unsupported collection-loading override key: " + key);
  }

  /**
   * Reads a complete configuration without resolving symbolic model references.
   *
   * @param in the protocol input
   * @return the decoded configuration, or {@code null} when modern PCL is disabled
   * @throws IOException if a model identifier cannot be decoded
   */
  public static CDOCollectionLoadingConfig read(CDODataInput in) throws IOException
  {
    if (!in.readBoolean())
    {
      return null;
    }

    ChunkConfig defaultChunkConfig = in.readBoolean() ? ChunkConfig.read(in) : null;

    int size = in.readXInt();
    if (size < 0)
    {
      throw new IOException("Invalid collection-loading override count: " + size);
    }

    Map<CDOModelElementRef<?>, ChunkConfig> overrides = new LinkedHashMap<>();

    for (int i = 0; i < size; i++)
    {
      CDOModelElementRef<?> key;

      switch (in.readByte())
      {
      case PACKAGE:
        key = new CDOPackageRef(in);
        break;

      case CLASS:
        key = new CDOClassifierRef(in);
        break;

      case FEATURE:
        key = new CDOFeatureRef(in);
        break;

      default:
        throw new IOException("Invalid collection-loading override key kind");
      }

      overrides.put(key, ChunkConfig.read(in));
    }

    return new CDOCollectionLoadingConfig(defaultChunkConfig, overrides);
  }

  /**
   * The two independent collection-loading dimensions at one configuration level.
   *
   * @author Eike Stepper
   * @since 4.38
   */
  public static final class ChunkConfig
  {
    /**
     * Indicates that this dimension inherits from the next level.
     */
    public static final int INHERIT = -2;

    /**
     * Indicates that all values are loaded.
     */
    public static final int ALL = -1;

    /**
     * Indicates that no values are loaded.
     */
    public static final int NONE = 0;

    private final int initialChunkSize;

    private final int resolveChunkSize;

    /**
     * Creates a chunk configuration.
     *
     * @param initialChunkSize the initial-loading size
     * @param resolveChunkSize the later-resolution size
     * @throws IllegalArgumentException if either value is below {@link #INHERIT}
     */
    public ChunkConfig(int initialChunkSize, int resolveChunkSize)
    {
      validate(initialChunkSize, "initialChunkSize");
      validate(resolveChunkSize, "resolveChunkSize");

      this.initialChunkSize = initialChunkSize;
      this.resolveChunkSize = resolveChunkSize;
    }

    /**
     * Returns the initial-loading size.
     */
    public int getInitialChunkSize()
    {
      return initialChunkSize;
    }

    /**
     * Returns the later-resolution size.
     */
    public int getResolveChunkSize()
    {
      return resolveChunkSize;
    }

    @Override
    public int hashCode()
    {
      return 31 * initialChunkSize + resolveChunkSize;
    }

    @Override
    public boolean equals(Object obj)
    {
      if (obj == this)
      {
        return true;
      }

      if (!(obj instanceof ChunkConfig))
      {
        return false;
      }

      ChunkConfig other = (ChunkConfig)obj;
      return initialChunkSize == other.initialChunkSize && resolveChunkSize == other.resolveChunkSize;
    }

    @Override
    public String toString()
    {
      return "ChunkConfig[initial=" + initialChunkSize + ", resolve=" + resolveChunkSize + "]";
    }

    private void write(CDODataOutput out) throws IOException
    {
      out.writeXInt(initialChunkSize);
      out.writeXInt(resolveChunkSize);
    }

    private static ChunkConfig read(CDODataInput in) throws IOException
    {
      try
      {
        return new ChunkConfig(in.readXInt(), in.readXInt());
      }
      catch (IllegalArgumentException ex)
      {
        throw new IOException("Invalid collection-loading chunk configuration", ex);
      }
    }

    private static void validate(int value, String name)
    {
      if (value < INHERIT)
      {
        throw new IllegalArgumentException(name + " must be greater than or equal to " + INHERIT + ": " + value);
      }
    }
  }
}

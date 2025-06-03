/*
 * Copyright (c) 2008-2013, 2019, 2020, 2022-2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Erdal Karaca - added support for MAP Type
 */
package org.eclipse.emf.cdo.common.model;

import org.eclipse.emf.cdo.common.protocol.CDODataInput;
import org.eclipse.emf.cdo.common.protocol.CDODataOutput;
import org.eclipse.emf.cdo.spi.common.revision.CDOReferenceAdjuster;

import org.eclipse.net4j.util.container.IPluginContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EStructuralFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides access to all CDO-supported data types.
 *
 * @author Eike Stepper
 * @noimplement This interface is not intended to be implemented by clients.
 * @since 2.0
 */
public interface CDOType
{
  public static final CDOType OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.OBJECT;

  public static final CDOType BOOLEAN = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BOOLEAN;

  public static final CDOType BOOLEAN_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BOOLEAN_OBJECT;

  public static final CDOType BYTE = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BYTE;

  public static final CDOType BYTE_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BYTE_OBJECT;

  public static final CDOType CHAR = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.CHAR;

  public static final CDOType CHARACTER_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.CHARACTER_OBJECT;

  public static final CDOType DATE = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.DATE;

  public static final CDOType DOUBLE = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.DOUBLE;

  public static final CDOType DOUBLE_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.DOUBLE_OBJECT;

  public static final CDOType FLOAT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.FLOAT;

  public static final CDOType FLOAT_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.FLOAT_OBJECT;

  public static final CDOType INT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.INT;

  public static final CDOType INTEGER_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.INTEGER_OBJECT;

  public static final CDOType LONG = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.LONG;

  public static final CDOType LONG_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.LONG_OBJECT;

  public static final CDOType SHORT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.SHORT;

  public static final CDOType SHORT_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.SHORT_OBJECT;

  public static final CDOType STRING = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.STRING;

  public static final CDOType BYTE_ARRAY = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BYTE_ARRAY;

  /**
   * @since 3.0
   */
  public static final CDOType OBJECT_ARRAY = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.OBJECT_ARRAY;

  /**
   * @since 4.0
   */
  public static final CDOType MAP = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.MAP;

  /**
   * @since 4.1
   */
  public static final CDOType SET = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.SET;

  /**
   * @since 4.1
   */
  public static final CDOType LIST = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.LIST;

  /**
   * @since 2.0
   */
  public static final CDOType BIG_DECIMAL = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BIG_DECIMAL;

  /**
   * @since 2.0
   */
  public static final CDOType BIG_INTEGER = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BIG_INTEGER;

  /**
   * @since 3.0
   */
  public static final CDOType ENUM_ORDINAL = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.ENUM_ORDINAL;

  /**
   * @since 3.0
   */
  public static final CDOType ENUM_LITERAL = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.ENUM_LITERAL;

  /**
   * @since 4.0
   */
  public static final CDOType BLOB = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.BLOB;

  /**
   * @since 4.0
   */
  public static final CDOType CLOB = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.CLOB;

  /**
   * @since 4.2
   */
  public static final CDOType JAVA_CLASS = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.JAVA_CLASS;

  /**
   * @since 4.2
   */
  public static final CDOType JAVA_OBJECT = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.JAVA_OBJECT;

  public static final CDOType CUSTOM = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.CUSTOM;

  /**
   * @since 2.0
   * @deprecated As of 4.5 {@link org.eclipse.emf.ecore.util.FeatureMap feature maps} are no longer supported.
   */
  @Deprecated
  public static final CDOType FEATURE_MAP_ENTRY = null;

  /**
   * @since 4.3
   */
  public static final CDOType EXCEPTION = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.EXCEPTION;

  /**
   * @since 4.18
   */
  public static final CDOType HANDLER = org.eclipse.emf.cdo.internal.common.model.CDOTypeImpl.HANDLER;

  public String getName();

  /**
   * @since 4.0
   */
  public byte getTypeID();

  public boolean canBeNull();

  public Object getDefaultValue();

  public Object copyValue(Object value);

  /**
   * @since 4.0
   */
  public Object adjustReferences(CDOReferenceAdjuster adjuster, Object value, EStructuralFeature feature, int index);

  /**
   * @since 3.0
   */
  public Object readValue(CDODataInput in) throws IOException;

  /**
   * @since 3.0
   */
  public void writeValue(CDODataOutput out, Object value) throws IOException;

  /**
   * @since 2.0
   */
  public Object convertToEMF(EClassifier feature, Object value);

  /**
   * @since 2.0
   */
  public Object convertToCDO(EClassifier feature, Object value);

  /**
   * A {@link #writeValue(CDODataOutput, Object) serialization} and {@link #readValue(CDODataInput) deserialization}
   * strategy for values of custom {@link CDOType types}.
   *
   * @author Eike Stepper
   * @since 4.18
   */
  public abstract class Handler
  {
    public static final int DEFAULT_PRIORITY = 100;

    private final String type;

    public Handler(String type)
    {
      this.type = type;
    }

    public final String getType()
    {
      return type;
    }

    public int getPriority()
    {
      return DEFAULT_PRIORITY;
    }

    public abstract boolean canHandle(Object value);

    public abstract void writeValue(CDODataOutput out, Object value) throws IOException;

    public abstract Object readValue(CDODataInput in) throws IOException;

    /**
     * A global registry for {@link Handler type handlers}.
     *
     * @author Eike Stepper
     */
    public static final class Registry
    {
      public static final Registry INSTANCE = new Registry();

      private final Map<String, Handler> handlersByType = new HashMap<>();

      private final List<Handler> handlers = new ArrayList<>();

      private Registry()
      {
        IPluginContainer.INSTANCE.forEachElement(Factory.PRODUCT_GROUP, Handler.class, this::registerHandler);
        updateHandlers();
      }

      private void updateHandlers()
      {
        handlers.clear();
        handlers.addAll(handlersByType.values());
        handlers.sort(Comparator.comparingInt(Handler::getPriority).reversed());
      }

      private boolean registerHandler(Handler handler)
      {
        String type = handler.getType();
        Handler existingHandler = handlersByType.get(type);
        if (existingHandler == null || handler.getPriority() > existingHandler.getPriority())
        {
          handlersByType.put(type, handler);
          return true;
        }

        return false;
      }

      public void addHandler(Handler handler)
      {
        if (registerHandler(handler))
        {
          updateHandlers();
        }
      }

      public Handler getHandlerByType(String type)
      {
        return handlersByType.get(type);
      }

      public Handler getHandlerByValue(Object value)
      {
        for (Handler handler : handlers)
        {
          if (handler.canHandle(value))
          {
            return handler;
          }
        }

        return null;
      }
    }

    /**
     * Creates {@link Handler type handlers}.
     *
     * @author Eike Stepper
     */
    public static abstract class Factory extends org.eclipse.net4j.util.factory.Factory
    {
      public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.common.model.typeHandlers"; //$NON-NLS-1$

      public Factory(String type)
      {
        super(PRODUCT_GROUP, type);
      }

      @Override
      public abstract Handler create(String description) throws ProductCreationException;
    }
  }
}

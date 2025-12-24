/*
 * Copyright (c) 2023, 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.factory;

import org.eclipse.net4j.util.StringParser.EnumStringParser;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.collection.Tree;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.AnnotationFactory.InjectElement.Cardinality;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;
import java.util.function.Function;

/**
 * Creates products reflectively and configures them via product method annotations.
 *
 * @author Eike Stepper
 * @since 3.23
 */
public class AnnotationFactory<PRODUCT> extends TreeFactory.ContainerAware
{
  private final Class<PRODUCT> productType;

  private final Method[] methods;

  public AnnotationFactory(Class<PRODUCT> productType, IFactoryKey key)
  {
    super(key);
    this.productType = productType;
    methods = productType.getMethods();
  }

  public AnnotationFactory(Class<PRODUCT> productType, String productGroup, String type)
  {
    super(productGroup, type);
    this.productType = productType;
    methods = productType.getMethods();
  }

  public final Class<PRODUCT> getProductType()
  {
    return productType;
  }

  @Override
  protected final PRODUCT create(Tree config) throws ProductCreationException
  {
    try
    {
      PRODUCT product = createProduct(config);
      configureProduct(product, config);
      return product;
    }
    catch (Exception ex)
    {
      throw productCreationException(config, ex);
    }
  }

  protected PRODUCT createProduct(Tree config) throws Exception
  {
    Constructor<?> containerConfigConstructor = null;
    Constructor<?> configContainerConstructor = null;
    Constructor<?> configConstructor = null;
    Constructor<?> containerConstructor = null;
    Constructor<?> defaultConstructor = null;

    for (Constructor<?> constructor : productType.getConstructors())
    {
      Parameter[] parameters = constructor.getParameters();
      if (parameters.length == 2)
      {
        if (parameters[0].getType() == IManagedContainer.class && parameters[1].getType() == Tree.class)
        {
          containerConfigConstructor = constructor;
        }
        else if (parameters[0].getType() == Tree.class && parameters[1].getType() == IManagedContainer.class)
        {
          configContainerConstructor = constructor;
        }
      }
      else if (parameters.length == 1)
      {
        if (parameters[0].getType() == IManagedContainer.class)
        {
          containerConstructor = constructor;
        }
        else if (parameters[0].getType() == Tree.class)
        {
          configConstructor = constructor;
        }
      }
      else if (parameters.length == 0)
      {
        defaultConstructor = constructor;
      }
    }

    if (containerConfigConstructor != null)
    {
      @SuppressWarnings("unchecked")
      PRODUCT product = (PRODUCT)containerConfigConstructor.newInstance(getContainer(), config);
      return product;
    }

    if (configContainerConstructor != null)
    {
      @SuppressWarnings("unchecked")
      PRODUCT product = (PRODUCT)configContainerConstructor.newInstance(config, getContainer());
      return product;
    }

    if (configConstructor != null)
    {
      @SuppressWarnings("unchecked")
      PRODUCT product = (PRODUCT)configConstructor.newInstance(config);
      invokeAnnotatedMethod(product, InjectContainer.class);
      return product;
    }

    if (containerConstructor != null)
    {
      @SuppressWarnings("unchecked")
      PRODUCT product = (PRODUCT)containerConstructor.newInstance(getContainer());
      invokeAnnotatedMethod(product, InjectConfig.class, config);
      return product;
    }

    if (defaultConstructor != null)
    {
      @SuppressWarnings("unchecked")
      PRODUCT product = (PRODUCT)defaultConstructor.newInstance();
      invokeAnnotatedMethod(product, InjectContainer.class);
      invokeAnnotatedMethod(product, InjectConfig.class, config);
      return product;
    }

    throw new IllegalStateException("No suitable constructor found in " + productType);
  }

  protected void configureProduct(PRODUCT product, Tree config) throws Exception
  {
    for (Method method : methods)
    {
      try
      {
        injectAttribute(product, config, method);
      }
      catch (Exception ex)
      {
        throw new IllegalStateException("An attribute could not be injected via " + method, ex);
      }

      try
      {
        injectElement(product, config, method);
      }
      catch (Exception ex)
      {
        throw new IllegalStateException("An element could not be injected via " + method, ex);
      }
    }
  }

  protected void injectAttribute(PRODUCT product, Tree config, Method method) throws Exception
  {
    InjectAttribute annotation = method.getAnnotation(InjectAttribute.class);
    if (annotation != null)
    {
      String name = annotation.name();

      String value = config.attribute(name);
      if (value == null)
      {
        String defaultValue = annotation.defaultValue();
        if (!StringUtil.isEmpty(defaultValue))
        {
          value = defaultValue;
        }
      }

      if (value != null)
      {
        boolean stringConverters = annotation.stringConverters();
        if (stringConverters)
        {
          value = StringUtil.convert(value, getContainer());
        }

        String productGroup = annotation.productGroup();
        if (!StringUtil.isEmpty(productGroup))
        {
          boolean singleton = annotation.productSingleton();
          String descriptionAttribute = annotation.descriptionAttribute();

          Object element = createElement(productGroup, value, descriptionAttribute, config, singleton);
          if (element != null)
          {
            method.invoke(product, element);
          }

          return;
        }

        Class<?> parameterType = method.getParameters()[0].getType();
        boolean enumCaseSensitive = isEnumCaseSensitive();

        Object argument = StringUtil.parse(value, getContainer(), parameterType, enumCaseSensitive);
        method.invoke(product, argument);
      }
    }
  }

  protected boolean isEnumCaseSensitive()
  {
    return EnumStringParser.DEFAULT_CASE_SENSITIVE;
  }

  protected void injectElement(PRODUCT product, Tree config, Method method) throws IllegalAccessException, InvocationTargetException
  {
    InjectElement annotation = method.getAnnotation(InjectElement.class);
    if (annotation != null)
    {
      String name = annotation.name();
      String productGroup = annotation.productGroup();
      boolean singleton = annotation.productSingleton();
      String descriptionAttribute = annotation.descriptionAttribute();

      Cardinality cardinality = annotation.cardinality();
      if (cardinality == Cardinality.DETECT)
      {
        cardinality = detectCardinality(product, config, method);
      }

      Function<Tree, String> typeFunction;
      List<Tree> elementConfigs;

      boolean elementNameIsFactoryType = StringUtil.isEmpty(name);
      if (elementNameIsFactoryType)
      {
        typeFunction = Tree::name;
        elementConfigs = config.children();
      }
      else
      {
        String factoryTypeAttribute = annotation.factoryTypeAttribute();
        String defaultFactoryType = annotation.defaultFactoryType();
        String factoryTypePrefix = annotation.factoryTypePrefix();
        String factoryTypeSuffix = annotation.factoryTypeSuffix();
        typeFunction = elementConfig -> getElementType(elementConfig, factoryTypeAttribute, defaultFactoryType, factoryTypePrefix, factoryTypeSuffix);
        elementConfigs = config.children(name);
      }

      for (Tree elementConfig : elementConfigs)
      {
        String type = typeFunction.apply(elementConfig);

        Object element = createElement(productGroup, type, descriptionAttribute, elementConfig, singleton);
        if (element != null)
        {
          method.invoke(product, element);

          if (cardinality == Cardinality.FIRST)
          {
            break;
          }
        }
      }
    }
  }

  /**
   * @deprecated As of 3.29, use {@link #getElementType(Tree, String, String, String, String)}.
   */
  @Deprecated
  protected String getElementType(Tree elementConfig, String factoryTypeAttribute, String defaultFactoryType)
  {
    return getElementType(elementConfig, factoryTypeAttribute, defaultFactoryType, "", "");
  }

  /**
   * @since 3.29
   */
  protected String getElementType(Tree elementConfig, String factoryTypeAttribute, String defaultFactoryType, //
      String factoryTypePrefix, String factoryTypeSuffix)
  {
    String type = elementConfig.attribute(factoryTypeAttribute);
    if (type == null)
    {
      type = defaultFactoryType;
    }

    if (StringUtil.isEmpty(type))
    {
      throw new IllegalStateException("Factory type is not specified");
    }

    return StringUtil.safe(factoryTypePrefix) + type + StringUtil.safe(factoryTypeSuffix);
  }

  protected Object createElement(String productGroup, String type, String descriptionAttribute, Tree elementConfig, boolean singleton)
      throws ProductCreationException
  {
    IManagedContainer container = getContainer();

    if (!StringUtil.isEmpty(descriptionAttribute))
    {
      String description = elementConfig.attribute(descriptionAttribute);

      if (singleton)
      {
        return container.getElementOrNull(productGroup, type, description);
      }

      return container.createElement(productGroup, type, description);
    }

    if (singleton)
    {
      return container.getElementOrNull(productGroup, type, elementConfig);
    }

    return container.createElement(productGroup, type, elementConfig);
  }

  private Cardinality detectCardinality(PRODUCT product, Tree config, Method method)
  {
    String methodName = method.getName();
    if (methodName.startsWith("add") || methodName.startsWith("append") || methodName.startsWith("insert"))
    {
      return Cardinality.MULTIPLE;
    }

    return Cardinality.FIRST;
  }

  private <A extends Annotation> boolean invokeAnnotatedMethod(PRODUCT product, Class<A> annotationClass, Object... arguments) throws Exception
  {
    for (Method method : methods)
    {
      if (method.getAnnotation(annotationClass) != null)
      {
        method.invoke(product, arguments);
        return true;
      }
    }

    return false;
  }

  @Inherited
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface InjectContainer
  {
  }

  @Inherited
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface InjectConfig
  {
  }

  @Inherited
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface InjectAttribute
  {
    public String name();

    public String defaultValue() default "";

    public boolean stringConverters() default true;

    public String productGroup() default "";

    public boolean productSingleton() default false;

    public String descriptionAttribute() default "";
  }

  @Inherited
  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  public @interface InjectElement
  {
    public static final String FACTORY_TYPE_ATTRIBUTE = "type";

    public String name() default "";

    public String productGroup();

    public boolean productSingleton() default false;

    public String factoryTypeAttribute() default FACTORY_TYPE_ATTRIBUTE;

    public String defaultFactoryType() default "";

    /**
     * @since 3.29
     */
    public String factoryTypePrefix() default "";

    /**
     * @since 3.29
     */
    public String factoryTypeSuffix() default "";

    public String descriptionAttribute() default "";

    public Cardinality cardinality() default Cardinality.DETECT;

    /**
     * @author Eike Stepper
     */
    public enum Cardinality
    {
      DETECT, FIRST, MULTIPLE;
    }
  }
}

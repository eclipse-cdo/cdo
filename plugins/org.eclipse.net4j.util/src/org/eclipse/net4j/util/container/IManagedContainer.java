/*
 * Copyright (c) 2007-2009, 2011-2013, 2015, 2016, 2018-2021, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.container;

import org.eclipse.net4j.util.collection.Tree;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.registry.IRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A {@link IContainer container} that populates itself by means of element {@link #getFactoryRegistry() factories} and
 * {@link #getPostProcessors() post processors} .
 *
 * @author Eike Stepper
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface IManagedContainer extends IContainer<Object>, ILifecycle
{
  /**
   * Returns the name of this container, or <code>null</code> if no name has been set.
   *
   * @since 3.8
   */
  public String getName();

  /**
   * Sets the name of this container before it is activated.
   *
   * @since 3.8
   */
  public void setName(String name);

  public IRegistry<IFactoryKey, IFactory> getFactoryRegistry();

  public IManagedContainer registerFactory(IFactory factory);

  /**
   * @since 3.23
   */
  public IManagedContainer unregisterFactory(IFactory factory);

  public List<IElementProcessor> getPostProcessors();

  public void addPostProcessor(IElementProcessor postProcessor, boolean processExistingElements);

  public void addPostProcessor(IElementProcessor postProcessor);

  public void removePostProcessor(IElementProcessor postProcessor);

  public Set<String> getProductGroups();

  public Set<String> getFactoryTypes(String productGroup);

  public IFactory getFactory(String productGroup, String factoryType) throws FactoryNotFoundException;

  public Object putElement(String productGroup, String factoryType, String description, Object element);

  public String[] getElementKey(Object element);

  public Object[] getElements(String productGroup);

  public Object[] getElements(String productGroup, String factoryType);

  public Object getElement(String productGroup, String factoryType, String description) throws FactoryNotFoundException, ProductCreationException;

  /**
   * @since 2.0
   */
  public Object getElement(String productGroup, String factoryType, String description, boolean activate)
      throws FactoryNotFoundException, ProductCreationException;

  /**
   * @since 3.23
   */
  public <T> T getElementOrNull(String productGroup, String factoryType) throws ProductCreationException;

  /**
   * @since 3.14
   */
  public <T> T getElementOrNull(String productGroup, String factoryType, String description) throws ProductCreationException;

  /**
   * @since 3.23
   */
  public <T> T getElementOrNull(String productGroup, String factoryType, Tree config) throws ProductCreationException;

  /**
   * @since 3.23
   */
  public <T> T createElement(String productGroup, String factoryType, String description) throws FactoryNotFoundException, ProductCreationException;

  /**
   * @since 3.23
   */
  public <T> T createElement(String productGroup, String factoryType, Tree config) throws FactoryNotFoundException, ProductCreationException;

  /**
   * @since 3.22
   */
  public int countElements(String productGroup);

  /**
   * @since 3.22
   */
  public int countElements(String productGroup, String factoryType);

  public Object removeElement(String productGroup, String factoryType, String description);

  /**
   * @since 3.13
   */
  public <T> void forEachElement(String productGroup, Class<T> productType, Function<String, String> descriptionProvider, Consumer<T> consumer);

  /**
   * @since 3.13
   */
  public <T> void forEachElement(String productGroup, Class<T> productType, String description, Consumer<T> consumer);

  /**
   * @since 3.13
   */
  public <T> void forEachElement(String productGroup, Class<T> productType, Consumer<T> consumer);

  public void clearElements();

  public void loadElements(InputStream stream) throws IOException, FactoryNotFoundException, ProductCreationException;

  public void saveElements(OutputStream stream) throws IOException;

  /**
   * @author Eike Stepper
   * @since 3.4
   */
  public interface ContainerAware
  {
    /**
     * Assigns the container that I should use to get my dependencies.
     *
     * @param container the container in which I am created/registered
     */
    public void setManagedContainer(IManagedContainer container);
  }
}

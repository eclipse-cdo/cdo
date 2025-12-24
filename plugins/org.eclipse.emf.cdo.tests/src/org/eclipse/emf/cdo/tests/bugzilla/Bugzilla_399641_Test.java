/*
 * Copyright (c) 2013, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA) - initial API and implementation
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.internal.net4j.Net4jSessionFactory;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;

import org.eclipse.net4j.util.container.ContainerUtil;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.container.IManagedContainerFactory;
import org.eclipse.net4j.util.factory.Factory;
import org.eclipse.net4j.util.factory.IFactory;
import org.eclipse.net4j.util.factory.ProductCreationException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * Bug 399641: Tests that factories in managed containers can use their containers
 * to get dependencies.
 */
public class Bugzilla_399641_Test extends AbstractCDOTest
{
  public void testContainerAwareFactories() throws Exception
  {
    IManagedContainer container = ContainerUtil.createContainer();
    LifecycleUtil.activate(container);

    try
    {
      container.registerFactory(new MyFactory());
      container.registerFactory(new MyOtherFactory());

      Object product = container.getElement(MyFactory.PRODUCT_GROUP, MyFactory.TYPE, null);
      assertInstanceOf(MyProduct.class, product);
      assertNotNull(((MyProduct)product).getOther());
    }
    finally
    {
      LifecycleUtil.deactivate(container);
    }
  }

  public void testNet4jSessionFactory() throws Exception
  {
    IManagedContainer container = getClientContainer();

    IFactory factory = container.getFactory(Net4jSessionFactory.PRODUCT_GROUP, Net4jSessionFactory.TYPE);
    assertInstanceOf(IManagedContainerFactory.class, factory);

    IManagedContainer actualContainer = ((IManagedContainerFactory)factory).getManagedContainer();
    assertSame(container, actualContainer);
  }

  private static class MyProduct
  {
    private final MyOtherProduct other;

    MyProduct(MyOtherProduct other)
    {
      this.other = other;
    }

    public MyOtherProduct getOther()
    {
      return other;
    }
  }

  private static class MyFactory extends Factory implements IManagedContainerFactory
  {
    static final String PRODUCT_GROUP = MyFactory.class.getName();

    static final String TYPE = "default";

    private IManagedContainer container;

    public MyFactory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    @Override
    public Object create(String description) throws ProductCreationException
    {
      return new MyProduct((MyOtherProduct)getManagedContainer().getElement(MyOtherFactory.PRODUCT_GROUP, MyOtherFactory.TYPE, null));
    }

    @Override
    public IManagedContainer getManagedContainer()
    {
      return container;
    }

    @Override
    public void setManagedContainer(IManagedContainer container)
    {
      this.container = container;
    }

  }

  private static class MyOtherProduct
  {
    MyOtherProduct()
    {
      super();
    }
  }

  private static class MyOtherFactory extends Factory
  {
    static final String PRODUCT_GROUP = MyOtherFactory.class.getName();

    static final String TYPE = "default";

    public MyOtherFactory()
    {
      super(PRODUCT_GROUP, TYPE);
    }

    @Override
    public Object create(String description) throws ProductCreationException
    {
      return new MyOtherProduct();
    }

  }
}

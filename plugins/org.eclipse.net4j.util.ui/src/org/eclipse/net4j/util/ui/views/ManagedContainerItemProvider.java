/*
 * Copyright (c) 2007, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.IContainerEvent;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.factory.FactoryKey;
import org.eclipse.net4j.util.factory.IFactoryKey;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 * @since 3.17
 */
public class ManagedContainerItemProvider extends ContainerItemProvider<IContainer<Object>>
{
  private final IListener containerListener = new ContainerEventAdapter<Object>()
  {
    @Override
    protected void notifyContainerEvent(IContainerEvent<Object> event)
    {
      if (showFactories)
      {
        refreshViewer(true);
      }
    }
  };

  private final Map<String, ProductGroupWrapper> productGroupWrappers = new HashMap<>();

  private final Map<IFactoryKey, FactoryWrapper> factoryWrappers = new HashMap<>();

  private boolean showFactories = true;

  private Image productGroupImage;

  public ManagedContainerItemProvider()
  {
  }

  public boolean isShowFactories()
  {
    return showFactories;
  }

  public void setShowFactories(boolean showFactories)
  {
    if (showFactories != this.showFactories)
    {
      this.showFactories = showFactories;
      refreshViewer(true);
    }
  }

  @Override
  public IManagedContainer getInput()
  {
    return (IManagedContainer)super.getInput();
  }

  @Override
  public boolean hasChildren(Object element)
  {
    if (showFactories)
    {
      IManagedContainer input = getInput();
      if (element == input)
      {
        return !input.isEmpty();
      }

      if (element instanceof Wrapper)
      {
        Wrapper wrapper = (Wrapper)element;
        return wrapper.hasChildren();
      }
    }

    return super.hasChildren(element);
  }

  @Override
  public Object[] getChildren(Object element)
  {
    if (showFactories)
    {
      IManagedContainer input = getInput();
      if (element == input)
      {
        List<ProductGroupWrapper> result = new ArrayList<>();
        input.getProductGroups().forEach(productGroup -> result.add(getProductGroupWrapper(productGroup)));
        result.sort(null);
        return result.toArray();
      }

      if (element instanceof Wrapper)
      {
        Wrapper wrapper = (Wrapper)element;
        return wrapper.getChildren();
      }
    }

    return super.getChildren(element);
  }

  @Override
  public Object getParent(Object element)
  {
    if (showFactories)
    {
      IManagedContainer input = getInput();
      if (element == input)
      {
        return null;
      }

      if (element instanceof Wrapper)
      {
        Wrapper wrapper = (Wrapper)element;
        return wrapper.getParent();
      }
    }

    return super.getParent(element);
  }

  @Override
  public String getText(Object element)
  {
    if (showFactories)
    {
      if (element instanceof Wrapper)
      {
        Wrapper wrapper = (Wrapper)element;
        return wrapper.getText();
      }
    }

    return super.getText(element);
  }

  @Override
  public Image getImage(Object element)
  {
    if (showFactories)
    {
      if (element instanceof FactoryWrapper)
      {
        return SharedIcons.getImage(SharedIcons.OBJ_FACTORY);
      }

      if (element instanceof ProductGroupWrapper)
      {
        return getProductGroupImage();
      }
    }

    return SharedIcons.getImage(SharedIcons.OBJ_BEAN);
  }

  @Override
  public Color getForeground(Object element)
  {
    if (showFactories)
    {
      if (element instanceof Wrapper)
      {
        Wrapper wrapper = (Wrapper)element;
        if (wrapper.getElementCount() == 0)
        {
          return UIUtil.grayColor();
        }
      }
    }

    return super.getForeground(element);
  }

  @Override
  public void dispose()
  {
    if (productGroupImage != null)
    {
      productGroupImage.dispose();
      productGroupImage = null;
    }

    super.dispose();
  }

  @Override
  protected void connectInput(IContainer<Object> input)
  {
    super.connectInput(input);
    input.addListener(containerListener);
  }

  @Override
  protected void disconnectInput(IContainer<Object> input)
  {
    input.removeListener(containerListener);
    super.disconnectInput(input);
    productGroupWrappers.clear();
    factoryWrappers.clear();
  }

  private Image getProductGroupImage()
  {
    if (productGroupImage == null)
    {
      productGroupImage = OM.getImageDescriptor("icons/product_group.gif").createImage();
    }

    return productGroupImage;
  }

  private ProductGroupWrapper getProductGroupWrapper(String productGroup)
  {
    return productGroupWrappers.computeIfAbsent(productGroup, k -> new ProductGroupWrapper(productGroup));
  }

  private FactoryWrapper getFactoryWrapper(String productGroup, String factoryType)
  {
    IFactoryKey factoryKey = new FactoryKey(productGroup, factoryType);
    return factoryWrappers.computeIfAbsent(factoryKey, k -> new FactoryWrapper(productGroup, factoryType));
  }

  /**
   * @author Eike Stepper
   */
  public abstract class Wrapper implements Comparable<Wrapper>
  {
    protected Wrapper()
    {
    }

    public abstract boolean hasChildren();

    public abstract Object[] getChildren();

    public abstract Object getParent();

    public abstract int getElementCount();

    public abstract String getText();

    @Override
    public final int compareTo(Wrapper o)
    {
      String text1 = getText();
      String text2 = o.getText();
      return text1 == null ? text2 == null ? 0 : 1 : text1.compareTo(text2);
    }

    @Override
    public final String toString()
    {
      return getText();
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ProductGroupWrapper extends Wrapper
  {
    private final String productGroup;

    public ProductGroupWrapper(String productGroup)
    {
      this.productGroup = productGroup;
    }

    @Override
    public boolean hasChildren()
    {
      return !doGetChildren().isEmpty();
    }

    @Override
    public Object[] getChildren()
    {
      List<FactoryWrapper> result = new ArrayList<>();
      doGetChildren().forEach(factoryType -> result.add(getFactoryWrapper(productGroup, factoryType)));
      result.sort(null);
      return result.toArray();
    }

    private Set<String> doGetChildren()
    {
      return getInput().getFactoryTypes(productGroup);
    }

    @Override
    public Object getParent()
    {
      return getInput();
    }

    @Override
    public int getElementCount()
    {
      return getInput().countElements(productGroup);
    }

    @Override
    public String getText()
    {
      return productGroup;
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class FactoryWrapper extends Wrapper
  {
    private final String productGroup;

    private final String factoryType;

    public FactoryWrapper(String productGroup, String factoryType)
    {
      this.productGroup = productGroup;
      this.factoryType = factoryType;
    }

    @Override
    public boolean hasChildren()
    {
      return getElementCount() != 0;
    }

    @Override
    public Object[] getChildren()
    {
      return getInput().getElements(productGroup, factoryType);
    }

    @Override
    public Object getParent()
    {
      return getProductGroupWrapper(productGroup);
    }

    @Override
    public int getElementCount()
    {
      return getInput().countElements(productGroup, factoryType);
    }

    @Override
    public String getText()
    {
      return factoryType;
    }
  }
}

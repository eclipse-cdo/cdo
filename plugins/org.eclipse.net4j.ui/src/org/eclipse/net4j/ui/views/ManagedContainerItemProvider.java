/***************************************************************************
 * Copyright (c) 2004, 2005, 2006 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.views;

import org.eclipse.net4j.util.container.IManagedContainer;

import java.util.List;

/**
 * @author Eike Stepper
 */
@Deprecated
public class ManagedContainerItemProvider<CONTAINER extends IManagedContainer> extends ContainerItemProvider<CONTAINER>
{
  private boolean showProductGroups = true;

  private boolean showFactoryTypes = false;

  public ManagedContainerItemProvider()
  {
  }

  public boolean isShowProductGroups()
  {
    return showProductGroups;
  }

  public void setShowProductGroups(boolean showProductGroups)
  {
    this.showProductGroups = showProductGroups;
  }

  public boolean isShowFactoryTypes()
  {
    return showFactoryTypes;
  }

  public void setShowFactoryTypes(boolean showFactoryTypes)
  {
    this.showFactoryTypes = showFactoryTypes;
  }

  @Override
  protected Node createNode(Node parent, Object element)
  {
    if (element instanceof IManagedContainer)
    {
      return new ManagedContainerNode(parent, (IManagedContainer)element);
    }

    return super.createNode(parent, element);
  }

  /**
   * @author Eike Stepper
   */
  public class ManagedContainerNode extends ContainerNode
  {
    public ManagedContainerNode(Node parent, IManagedContainer container)
    {
      super(parent, container);
    }
  }

  /**
   * @author Eike Stepper
   */
  public class ProductGroupNode extends AbstractNode
  {
    private String productGroup;

    public ProductGroupNode(ContainerNode parent, String productGroup)
    {
      super(parent);
      this.productGroup = productGroup;
    }

    public String getProductGroup()
    {
      return productGroup;
    }

    public IManagedContainer getContainer()
    {
      ContainerNode parent = (ContainerNode)getParent();
      return (IManagedContainer)parent.getContainer();
    }

    public Object getElement()
    {
      return this;
    }

    @Override
    protected List<Node> createChildren()
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public class FactoryTypeNode extends AbstractNode
  {
    private String factoryType;

    public FactoryTypeNode(ProductGroupNode parent, String factoryType)
    {
      super(parent);
      this.factoryType = factoryType;
    }

    public ProductGroupNode getProductGroupNode()
    {
      return (ProductGroupNode)getParent();
    }

    public String getFactoryType()
    {
      return factoryType;
    }

    public IManagedContainer getContainer()
    {
      return getProductGroupNode().getContainer();
    }

    public Object getElement()
    {
      return this;
    }

    @Override
    protected List<Node> createChildren()
    {
      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public class ProductGroupFactoryTypeNode extends ProductGroupNode
  {
    private String factoryType;

    public ProductGroupFactoryTypeNode(ContainerNode parent, String productGroup, String factoryType)
    {
      super(parent, productGroup);
      this.factoryType = factoryType;
    }

    public String getFactoryType()
    {
      return factoryType;
    }

    @Override
    protected List<Node> createChildren()
    {
      return null;
    }
  }
}

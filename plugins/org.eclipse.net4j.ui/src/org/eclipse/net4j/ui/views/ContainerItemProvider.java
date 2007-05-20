/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.views;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.internal.net4j.util.container.ContainerEventAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class ContainerItemProvider<CONTAINER extends IContainer> extends ItemProvider<CONTAINER>
{
  private Map<Object, Node> nodes = new HashMap();

  private ContainerNode root;

  private IElementFilter rootElementFilter;

  public ContainerItemProvider()
  {
  }

  public ContainerItemProvider(IElementFilter rootElementFilter)
  {
    this.rootElementFilter = rootElementFilter;
  }

  public IElementFilter getRootElementFilter()
  {
    return rootElementFilter;
  }

  @Override
  public boolean hasChildren(Object element)
  {
    try
    {
      Node node = getNode(element);
      return !node.getChildren().isEmpty();
    }
    catch (RuntimeException ex)
    {
      return false;
    }
  }

  public Object[] getChildren(Object element)
  {
    try
    {
      Node node = getNode(element);
      List<Node> children = node.getChildren();
      for (Iterator it = children.iterator(); it.hasNext();)
      {
        Object child = it.next();
        if (!LifecycleUtil.isActive(child))
        {
          it.remove();
        }
      }

      Object[] result = new Object[children.size()];
      for (int i = 0; i < result.length; i++)
      {
        result[i] = children.get(i).getElement();
      }

      return result;
    }
    catch (RuntimeException ex)
    {
      return NO_CHILDREN;
    }
  }

  public Object getParent(Object element)
  {
    try
    {
      Node node = getNode(element);
      Node parentNode = node.getParent();
      return parentNode == null ? null : parentNode.getElement();
    }
    catch (RuntimeException ex)
    {
      return null;
    }
  }

  @Override
  protected void connectInput(CONTAINER input)
  {
    root = (ContainerNode)createNode(null, input);
    nodes.put(input, root);
  }

  @Override
  protected void disconnectInput(CONTAINER input)
  {
    root.dispose();
    root = null;
    nodes.clear();
  }

  protected ContainerNode getRoot()
  {
    return root;
  }

  protected Map<Object, Node> getNodes()
  {
    return nodes;
  }

  protected Node getNode(Object element)
  {
    Node node = root;
    if (element != getInput())
    {
      node = nodes.get(element);
    }

    if (node == null)
    {
      throw new IllegalStateException("No node for " + element);
    }

    return node;
  }

  protected Node createNode(Node parent, Object element)
  {
    if (element instanceof IContainer)
    {
      return new ContainerNode(parent, (IContainer)element);
    }

    return new LeafNode(parent, element);
  }

  protected boolean filterRootElement(Object element)
  {
    if (rootElementFilter != null)
    {
      return rootElementFilter.filter(element);
    }

    return true;
  }

  /**
   * @author Eike Stepper
   */
  public interface Node
  {
    public void dispose();

    public Object getElement();

    public Node getParent();

    public List<Node> getChildren();
  }

  /**
   * @author Eike Stepper
   */
  public abstract class AbstractNode implements Node
  {
    private Node parent;

    private List<Node> children;

    public AbstractNode(Node parent)
    {
      this.parent = parent;
    }

    public void dispose()
    {
      parent = null;
      if (children != null)
      {
        for (Node child : children)
        {
          child.dispose();
        }

        children.clear();
        children = null;
      }
    }

    @Override
    public String toString()
    {
      return getElement().toString();
    }

    public final Node getParent()
    {
      return parent;
    }

    public final List<Node> getChildren()
    {
      if (children == null)
      {
        children = createChildren();
      }

      return children;
    }

    protected abstract List<Node> createChildren();
  }

  /**
   * @author Eike Stepper
   */
  public class ContainerNode extends AbstractNode
  {
    private IContainer container;

    private IListener containerListener = new ContainerEventAdapter()
    {
      @Override
      protected void onAdded(IContainer container, Object element)
      {
        if (container == ContainerNode.this.container)
        {
          elementAdded(element);
        }
      }

      @Override
      protected void onRemoved(IContainer container, Object element)
      {
        if (container == ContainerNode.this.container)
        {
          elementRemoved(element);
        }
      }
    };

    public ContainerNode(Node parent, IContainer container)
    {
      super(parent);
      this.container = container;
    }

    public void dispose()
    {
      container.removeListener(containerListener);
      container = null;
      super.dispose();
    }

    public IContainer getContainer()
    {
      return container;
    }

    public Object getElement()
    {
      return container;
    }

    @Override
    protected List<Node> createChildren()
    {
      Object[] elements = container.getElements();
      List<Node> children = new ArrayList(elements.length);
      for (int i = 0; i < elements.length; i++)
      {
        Object element = elements[i];
        addChild(children, element);
      }

      container.addListener(containerListener);
      return children;
    }

    protected Node addChild(List<Node> children, Object element)
    {
      if (this != root || filterRootElement(element))
      {
        Node node = createNode(this, element);
        nodes.put(element, node);
        children.add(node);
        return node;
      }

      return null;
    }

    protected void elementAdded(final Object element)
    {
      if (addChild(getChildren(), element) != null)
      {
        refreshElement(getElement(), false);
      }
    }

    protected void elementRemoved(Object element)
    {
      Node node = nodes.remove(element);
      if (node != null)
      {
        Object parentElement = node.getParent().getElement();
        getChildren().remove(node);
        node.dispose();

        if (parentElement == null)
        {
          refreshElement(root, false);
        }
        else
        {
          refreshElement(parentElement, false);
        }
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public class LeafNode implements Node
  {
    private Node parent;

    private Object element;

    public LeafNode(Node parent, Object element)
    {
      this.parent = parent;
      this.element = element;
    }

    public void dispose()
    {
      parent = null;
      element = null;
    }

    public Node getParent()
    {
      return parent;
    }

    public Object getElement()
    {
      return element;
    }

    public List<Node> getChildren()
    {
      return Collections.EMPTY_LIST;
    }
  }
}

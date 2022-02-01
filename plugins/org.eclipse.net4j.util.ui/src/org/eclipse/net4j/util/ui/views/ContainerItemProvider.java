/*
 * Copyright (c) 2007-2013, 2015, 2019-2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.net4j.util.ui.views;

import org.eclipse.net4j.ui.shared.SharedIcons;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.container.ISlow;
import org.eclipse.net4j.util.container.SetContainer;
import org.eclipse.net4j.util.event.EventUtil;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.internal.ui.bundle.OM;
import org.eclipse.net4j.util.lifecycle.LifecycleState;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PartInitException;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Eike Stepper
 */
public class ContainerItemProvider<CONTAINER extends IContainer<Object>> extends ItemProvider<CONTAINER>
{
  private static Color pendingColor;

  private static Image pendingImage;

  private Map<Object, Node> nodes = new HashMap<>();

  private Node root;

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
      if (node != null)
      {
        return node.hasChildren();
      }
    }
    catch (RuntimeException ex)
    {
      //$FALL-THROUGH$
    }

    return false;
  }

  @Override
  public Object[] getChildren(Object element)
  {
    try
    {
      Node node = getNode(element);
      if (node != null)
      {
        List<Node> children = node.getChildren();
        for (Iterator<Node> it = children.iterator(); it.hasNext();)
        {
          Node child = it.next();
          if (child.isDisposed())
          {
            it.remove();
          }
          else
          {
            Object childElement = child.getElement();
            LifecycleState lifecycleState = LifecycleUtil.getLifecycleState(childElement);
            if (lifecycleState == LifecycleState.INACTIVE || lifecycleState == LifecycleState.DEACTIVATING)
            {
              handleInactiveElement(it, child);
            }
          }
        }

        Object[] result = new Object[children.size()];
        for (int i = 0; i < result.length; i++)
        {
          result[i] = children.get(i).getElement();
        }

        return result;
      }
    }
    catch (RuntimeException ex)
    {
      //$FALL-THROUGH$
    }

    return NO_ELEMENTS;
  }

  @Override
  public Object getParent(Object element)
  {
    try
    {
      Node node = getNode(element);
      if (node != null)
      {
        Node parentNode = node.getParent();
        return parentNode == null ? null : parentNode.getElement();
      }
    }
    catch (RuntimeException ex)
    {
      //$FALL-THROUGH$
    }

    return null;
  }

  /**
   * @since 3.4
   */
  public void clearNodesCache()
  {
    disposeRoot();

    CONTAINER input = getInput();
    initRoot(input);
  }

  private void initRoot(CONTAINER input)
  {
    root = createNode(null, input);
    if (root != null)
    {
      addNode(input, root);
    }
  }

  private void disposeRoot()
  {
    if (root != null)
    {
      root.dispose(); // Also disposes of all children
      root = null;
    }

    nodes.clear();
  }

  @Override
  protected void connectInput(CONTAINER input)
  {
    initRoot(input);
  }

  @Override
  protected void disconnectInput(CONTAINER input)
  {
    disposeRoot();
  }

  /**
   * @since 2.0
   */
  protected void handleInactiveElement(Iterator<Node> it, Node child)
  {
    it.remove();
    child.dispose();
  }

  protected void elementAdded(Object element, Object parent)
  {
    // Do nothing.
  }

  protected void elementRemoved(Object element, Object parent)
  {
    // Do nothing.
  }

  /**
   * @since 3.3
   */
  protected void handleElementEvent(IEvent event)
  {
    // Do nothing.
  }

  /**
   * @since 3.5
   */
  protected Object[] getContainerChildren(AbstractContainerNode containerNode, IContainer<?> container)
  {
    return container.getElements();
  }

  protected Node getRoot()
  {
    return root;
  }

  protected Map<Object, Node> getNodes()
  {
    return nodes;
  }

  protected Node getNode(Object element)
  {
    if (element == getInput())
    {
      return root;
    }

    return nodes.get(element);
  }

  protected Node createNode(Node parent, Object element)
  {
    if (element instanceof IContainer<?>)
    {
      return createContaineNode(parent, element);
    }

    return createLeafNode(parent, element);
  }

  protected LeafNode createLeafNode(Node parent, Object element)
  {
    return new LeafNode(parent, element);
  }

  @SuppressWarnings("unchecked")
  protected ContainerNode createContaineNode(Node parent, Object element)
  {
    return new ContainerNode(parent, (IContainer<Object>)element);
  }

  protected void addNode(Object element, Node node)
  {
    nodes.put(element, node);
  }

  protected Node removeNode(Object element)
  {
    return nodes.remove(element);
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
   * @since 3.1
   */
  protected void executeRunnable(Runnable runnable)
  {
    Thread thread = new Thread(runnable);
    thread.setDaemon(true);
    thread.start();
  }

  /**
   * @since 3.5
   */
  protected SlowElement createSlowElement(IContainer<?> container)
  {
    @SuppressWarnings("unchecked")
    IContainer<Object> objectContainer = (IContainer<Object>)container;

    String text = getSlowText(objectContainer);
    return new SlowElement(objectContainer, text);
  }

  /**
   * @since 3.5
   */
  protected boolean isComputeChildrenEagerly()
  {
    return true;
  }

  /**
   * @since 3.1
   */
  protected boolean isSlow(IContainer<Object> container)
  {
    return container instanceof ISlow;
  }

  /**
   * @since 3.1
   */
  protected String getSlowText(IContainer<Object> container)
  {
    return "Pending...";
  }

  /**
   * @since 3.1
   */
  protected String getErrorText(IContainer<Object> container)
  {
    return "Error";
  }

  /**
   * @since 3.3
   */
  @Override
  public void fillContextMenu(IMenuManager manager, ITreeSelection selection)
  {
    super.fillContextMenu(manager, selection);
    if (selection.size() == 1)
    {
      Object element = selection.getFirstElement();
      if (element instanceof ContainerItemProvider.ErrorElement)
      {
        manager.add(new Action("Open Error Log")
        {
          @Override
          public void run()
          {
            try
            {
              UIUtil.getActiveWorkbenchPage().showView(UIUtil.ERROR_LOG_ID);
            }
            catch (PartInitException ex)
            {
              OM.LOG.error(ex);
            }
          }
        });
      }
    }
  }

  @Override
  public Font getFont(Object obj)
  {
    if (obj instanceof ContainerItemProvider.SlowElement)
    {
      return getItalicFont();
    }

    return super.getFont(obj);
  }

  @Override
  public Color getForeground(Object obj)
  {
    if (obj instanceof ContainerItemProvider.SlowElement)
    {
      return pendingColor();
    }

    return super.getForeground(obj);
  }

  @Override
  public Image getImage(Object obj)
  {
    if (obj instanceof ContainerItemProvider.SlowElement)
    {
      return pendingImage();
    }

    if (obj instanceof ContainerItemProvider.ErrorElement)
    {
      return UIUtil.errorImage();
    }

    return super.getImage(obj);
  }

  /**
   * @since 3.12
   */
  public static Color pendingColor()
  {
    if (pendingColor == null)
    {
      pendingColor = UIUtil.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);
    }

    return pendingColor;
  }

  /**
   * @since 3.12
   */
  public static Image pendingImage()
  {
    if (pendingImage == null)
    {
      pendingImage = SharedIcons.getImage(SharedIcons.OBJ_PENDING);
    }

    return pendingImage;
  }

  /**
   * @since 3.5
   */
  public static IContainer<Object> createSlowInput(final String text)
  {
    return new SetContainer<Object>(Object.class)
    {
      {
        addElement(new ContainerItemProvider.SlowElement(this, text));
      }
    };
  }

  /**
   * @author Eike Stepper
   * @noextend This interface is not intended to be extended by clients.
   * @noimplement This interface is not intended to be implemented by clients.
   */
  public interface Node
  {
    /**
     * @since 3.9
     */
    public static final List<Node> NO_CHILDREN = Collections.emptyList();

    public boolean isDisposed();

    public void dispose();

    /**
     * @since 3.5
     */
    public void disposeChildren();

    public Object getElement();

    /**
     * @since 3.14
     */
    public Object getParentElement();

    public Node getParent();

    /**
     * @since 3.5
     */
    public boolean hasChildren();

    public List<Node> getChildren();

    public TreePath getTreePath();
  }

  /**
   * @author Eike Stepper
   */
  public abstract class AbstractNode implements Node
  {
    private Node parent;

    private boolean disposed;

    public AbstractNode(Node parent)
    {
      this.parent = parent;
    }

    @Override
    public final Object getParentElement()
    {
      return parent == null ? null : parent.getElement();
    }

    @Override
    public boolean isDisposed()
    {
      return disposed;
    }

    @Override
    public void dispose()
    {
      if (!disposed)
      {
        removeNode(getElement());
        parent = null;
        disposed = true;
      }
    }

    /**
     * @since 3.5
     */
    @Override
    public void disposeChildren()
    {
      // Do nothing.
    }

    @Override
    public String toString()
    {
      return MessageFormat.format("{0}[{1}]", getClass().getSimpleName(), getElement()); //$NON-NLS-1$
    }

    @Override
    public final Node getParent()
    {
      checkNotDisposed();
      return parent;
    }

    @Override
    public TreePath getTreePath()
    {
      TreePath parentPath = parent == null ? TreePath.EMPTY : parent.getTreePath();
      return parentPath.createChildPath(getElement());
    }

    /**
     * @since 3.5
     */
    @Override
    public boolean hasChildren()
    {
      return false;
    }

    protected void checkNotDisposed()
    {
      if (disposed)
      {
        throw new IllegalStateException("Node is already disposed"); //$NON-NLS-1$
      }
    }

    protected Node addChild(Collection<Node> children, Object element)
    {
      Node existing = getNode(element);
      if (existing != null)
      {
        if (!Objects.equals(existing.getParent(), this))
        {
          OM.LOG.warn("Duplicate node rejected: " + element);
        }

        return null;
      }

      if (this != root || filterRootElement(element))
      {
        Node node = createNode(this, element);
        if (node != null)
        {
          addNode(element, node);
          children.add(node);
          return node;
        }
      }

      return null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public abstract class AbstractContainerNode extends AbstractNode
  {
    private List<Node> children;

    protected IListener containerListener = new ContainerEventAdapter<Object>()
    {
      @Override
      protected void onAdded(IContainer<Object> container, Object element)
      {
        AbstractContainerNode.this.onAdded(container, element);
      }

      @Override
      protected void onRemoved(IContainer<Object> container, Object element)
      {
        AbstractContainerNode.this.onRemoved(container, element);
      }

      @Override
      protected void notifyOtherEvent(IEvent event)
      {
        updateLabels(event.getSource());
        handleElementEvent(event);
      }
    };

    public AbstractContainerNode(Node parent)
    {
      super(parent);
    }

    @Override
    public void dispose()
    {
      if (!isDisposed())
      {
        disposeChildren();

        containerListener = null;
        super.dispose();
      }
    }

    @Override
    public void disposeChildren()
    {
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

    /**
     * @since 3.4
     */
    @Override
    public boolean hasChildren()
    {
      checkNotDisposed();

      final IContainer<Object> container = getContainer();
      if (children == null && isSlow(container))
      {
        if (isComputeChildrenEagerly())
        {
          getChildren();
        }

        return true;
      }

      List<Node> children = getChildren();
      return children != null && !children.isEmpty();
    }

    @Override
    public final List<Node> getChildren()
    {
      checkNotDisposed();
      if (children == null)
      {
        children = createChildren();
      }

      return children;
    }

    @SuppressWarnings("unchecked")
    public IContainer<Object> getContainer()
    {
      return (IContainer<Object>)getElement();
    }

    protected List<Node> createChildren()
    {
      final List<Node> children = new ArrayList<>();
      final IContainer<Object> container = getContainer();

      if (isSlow(container))
      {
        final Node[] lazyNode = { null };

        SlowElement slowElement = createSlowElement(container);
        if (slowElement != null)
        {
          lazyNode[0] = addChild(children, slowElement);
        }

        Runnable runnable = new Runnable()
        {
          @Override
          public void run()
          {
            try
            {
              fillChildren(children, container);
            }
            catch (Exception ex)
            {
              OM.LOG.error(ex);
              addChild(children, new ErrorElement(container));
            }
            finally
            {
              if (lazyNode[0] != null)
              {
                children.remove(lazyNode[0]);
              }

              refreshElement(container, true);
            }
          }
        };

        executeRunnable(runnable);
      }
      else
      {
        fillChildren(children, container);
      }

      container.addListener(containerListener);
      return children;
    }

    /**
     * @since 3.1
     */
    protected void fillChildren(List<Node> children, IContainer<Object> container)
    {
      Object[] elements = getContainerChildren(this, container);
      for (int i = 0; i < elements.length; i++)
      {
        Object element = elements[i];
        addChild(children, element);
      }
    }

    protected void onAdded(IContainer<Object> container, Object element)
    {
      Node node = addChild(getChildren(), element);
      if (node != null)
      {
        refreshElement(container, true);
        revealElement(element);
        elementAdded(element, container);
      }
    }

    protected void onRemoved(IContainer<Object> container, Object element)
    {
      Node node = removeNode(element);
      if (node != null)
      {
        getChildren().remove(node);
        elementRemoved(element, container);

        Object rootElement = root.getElement();
        Object refreshElement = container == rootElement ? null : container;
        refreshElement(refreshElement, true);
        node.dispose();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public class ContainerNode extends AbstractContainerNode
  {
    private IContainer<Object> container;

    public ContainerNode(Node parent, IContainer<Object> container)
    {
      super(parent);
      this.container = container;
      if (container == null)
      {
        throw new IllegalArgumentException("container == null"); //$NON-NLS-1$
      }
    }

    @Override
    public void dispose()
    {
      if (!isDisposed())
      {
        container.removeListener(containerListener);
        super.dispose();
        container = null;
      }
    }

    @Override
    public Object getElement()
    {
      return container;
    }
  }

  /**
   * @author Eike Stepper
   */
  public class LeafNode extends AbstractNode implements IListener
  {
    private Object element;

    public LeafNode(Node parent, Object element)
    {
      super(parent);
      this.element = element;
      EventUtil.addListener(element, this);
    }

    @Override
    public void dispose()
    {
      if (!isDisposed())
      {
        EventUtil.removeListener(element, this);
        super.dispose();
        element = null;
      }
    }

    @Override
    public Object getElement()
    {
      checkNotDisposed();
      return element;
    }

    @Override
    public List<Node> getChildren()
    {
      checkNotDisposed();
      return NO_CHILDREN;
    }

    @Override
    public void notifyEvent(IEvent event)
    {
      updateLabels(event.getSource());
      handleElementEvent(event);
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.9
   */
  public class FixedChildrenNode extends LeafNode
  {
    private List<Node> children = null;

    public FixedChildrenNode(Node parent, Object element, Object[] childElements)
    {
      super(parent, element);

      for (Object childElement : childElements)
      {
        addChild(childElement);
      }
    }

    public FixedChildrenNode(Node parent, Object element, Iterable<?> childElements)
    {
      super(parent, element);

      for (Object childElement : childElements)
      {
        addChild(childElement);
      }
    }

    @Override
    public void dispose()
    {
      if (!isDisposed())
      {
        children = null;
        super.dispose();
      }
    }

    /**
     * @since 3.4
     */
    @Override
    public boolean hasChildren()
    {
      checkNotDisposed();
      return children == null ? false : !children.isEmpty();
    }

    @Override
    public List<Node> getChildren()
    {
      checkNotDisposed();
      return children == null ? NO_CHILDREN : children;
    }

    private void addChild(Object childElement)
    {
      if (children == null)
      {
        children = new ArrayList<>();
      }

      addChild(children, childElement);
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.5
   */
  public static class SlowElement
  {
    private final IContainer<Object> container;

    private final String text;

    public SlowElement(IContainer<Object> container, String text)
    {
      this.container = container;
      this.text = text;
    }

    public final IContainer<Object> getContainer()
    {
      return container;
    }

    public final String getText()
    {
      return text;
    }

    @Override
    public String toString()
    {
      return text;
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.1
   */
  public class ErrorElement
  {
    private IContainer<Object> container;

    public ErrorElement(IContainer<Object> container)
    {
      this.container = container;
    }

    public IContainer<Object> getContainer()
    {
      return container;
    }

    @Override
    public String toString()
    {
      return getErrorText(container);
    }
  }

  /**
   * @author Eike Stepper
   * @since 3.1
   * @deprecated As of 3.5 use {@link SlowElement}.
   */
  @Deprecated
  public class LazyElement extends SlowElement
  {
    /**
     * @since 3.5
     */
    public LazyElement(IContainer<Object> container, String text)
    {
      super(container, text);
    }

    public LazyElement(IContainer<Object> container)
    {
      this(container, getSlowText(container));
    }
  }

  /**
   * @since 3.5
   * @deprecated As of 3.12 use {@link #pendingColor()}.
   */
  @Deprecated
  public static final Color PENDING_COLOR = UIUtil.getDisplay().getSystemColor(SWT.COLOR_DARK_GRAY);

  /**
   * @since 3.5
   * @deprecated As of 3.12 use {@link #pendingImage()}.
   */
  @Deprecated
  public static final Image PENDING_IMAGE = SharedIcons.getImage(SharedIcons.OBJ_PENDING);

  /**
   * @since 3.5
   * @deprecated As of 3.12 use {@link UIUtil#errorImage()}.
   */
  @Deprecated
  public static final Image ERROR_IMAGE = SharedIcons.getImage(SharedIcons.OBJ_ERROR);
}

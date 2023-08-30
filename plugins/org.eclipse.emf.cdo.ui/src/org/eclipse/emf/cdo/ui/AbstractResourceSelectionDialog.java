/*
 * Copyright (c) 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceLeaf;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.CDOAdapterFactoryContentProvider;
import org.eclipse.emf.cdo.internal.ui.CDOContentProvider;
import org.eclipse.emf.cdo.internal.ui.CDOViewerComparator;
import org.eclipse.emf.cdo.internal.ui.RunnableViewerRefresh;
import org.eclipse.emf.cdo.internal.ui.ViewerUtil;
import org.eclipse.emf.cdo.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.session.CDORepositoryInfo;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.shared.SharedIcons;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.widgets.BaseDialog;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Eike Stepper
 * @since 4.15
 */
public abstract class AbstractResourceSelectionDialog<INPUT> extends BaseDialog<TreeViewer>
{
  private final Set<URI> uris = new HashSet<>();

  private final boolean multi;

  private TreeViewer viewer;

  public AbstractResourceSelectionDialog(Shell shell, boolean multi, String title, String message, ImageDescriptor descriptor)
  {
    super(shell, DEFAULT_SHELL_STYLE | SWT.APPLICATION_MODAL, title, message, OM.Activator.INSTANCE.getDialogSettings(), descriptor);
    this.multi = multi;
  }

  public final Set<URI> getURIs()
  {
    return uris;
  }

  public final TreeViewer getViewer()
  {
    return viewer;
  }

  protected abstract INPUT getInput();

  @Override
  protected void createUI(Composite parent)
  {
    ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry());
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());

    viewer = new TreeViewer(parent, multi ? SWT.MULTI : SWT.SINGLE);
    viewer.getTree().setLayoutData(UIUtil.createGridData());
    viewer.setContentProvider(new ResourcesContentProvider(adapterFactory));
    viewer.setLabelProvider(new ResourcesLabelProvider(adapterFactory, viewer));
    viewer.setComparator(new CDOViewerComparator());
    viewer.setInput(getInput());
    viewer.addDoubleClickListener(e -> okPressed());
    viewer.addSelectionChangedListener(e -> selectionChanged());

    setCurrentViewer(viewer);
    UIUtil.asyncExec(parent.getDisplay(), () -> selectionChanged());
  }

  protected void selectionChanged()
  {
    uris.clear();

    ITreeSelection selection = getCurrentViewer().getStructuredSelection();
    for (Object object : selection)
    {
      if (object instanceof CDOResource)
      {
        uris.add(((CDOResource)object).getURI());
      }
    }

    Button okButton = getButton(IDialogConstants.OK_ID);
    if (okButton != null)
    {
      okButton.setEnabled(!uris.isEmpty());
    }
  }

  protected boolean elementHasChildren(Object object, Predicate<Object> defaultHasChildren)
  {
    return defaultHasChildren.test(object);
  }

  protected Object[] elementGetChildren(Object object, Function<Object, Object[]> defaultGetChildren)
  {
    return defaultGetChildren.apply(object);
  }

  protected Object elementGetParent(Object object, Function<Object, Object> defaultGetParent)
  {
    return defaultGetParent.apply(object);
  }

  protected String elementGetText(Object object, Function<Object, String> defaultGetText)
  {
    return defaultGetText.apply(object);
  }

  protected Image elementGetImage(Object object, Function<Object, Image> defaultGetImage)
  {
    return defaultGetImage.apply(object);
  }

  protected Color elementGetForeground(Object object, Function<Object, Color> defaultGetForeground)
  {
    return defaultGetForeground.apply(object);
  }

  /**
   * @author Eike Stepper
   */
  private final class ResourcesContentProvider extends CDOContentProvider.ContextFree
  {
    private final ComposedAdapterFactory adapterFactory;

    private final CDOAdapterFactoryContentProvider delegate;

    public ResourcesContentProvider(ComposedAdapterFactory adapterFactory)
    {
      this.adapterFactory = adapterFactory;
      delegate = new CDOAdapterFactoryContentProvider(adapterFactory);
    }

    @Override
    public void inputChanged(Viewer newViewer, Object oldInput, Object newInput)
    {
      super.inputChanged(newViewer, oldInput, newInput);
      delegate.inputChanged(newViewer, oldInput, newInput);
    }

    @Override
    public boolean hasChildren(Object object)
    {
      return elementHasChildren(object, this::defaultHasChildren);
    }

    @Override
    public Object[] getChildren(Object object)
    {
      return elementGetChildren(object, this::defaultGetChildren);
    }

    @Override
    public Object getParent(Object object)
    {
      return elementGetParent(object, this::defaultGetParent);
    }

    @Override
    protected Object adapt(Object target, Object type)
    {
      return adapterFactory.adapt(target, type);
    }

    @Override
    protected Object[] modifyChildren(Object parent, Object[] children)
    {
      return children;
    }

    @Override
    protected ITreeContentProvider getContentProvider(Object object)
    {
      return delegate;
    }

    @Override
    protected RunnableViewerRefresh getViewerRefresh()
    {
      return delegate.getViewerRefresh();
    }

    private boolean defaultHasChildren(Object object)
    {
      if (object instanceof CDOView)
      {
        CDOView view = (CDOView)object;
        CDOResource rootResource = view.getRootResource();
        CDORevision revision = rootResource.cdoRevision();
        return CDORevisionUtil.hasChildRevisions(revision);
      }

      if (object instanceof CDOResourceFolder)
      {
        CDOResourceFolder folder = (CDOResourceFolder)object;
        CDORevision revision = folder.cdoRevision();
        return CDORevisionUtil.hasChildRevisions(revision);
      }

      if (object instanceof CDOResourceLeaf)
      {
        return false;
      }

      return super.hasChildren(object);
    }

    private Object[] defaultGetChildren(Object object)
    {
      if (object instanceof CDOView)
      {
        CDOView view = (CDOView)object;
        CDOResource rootResource = view.getRootResource();
        return super.getChildren(rootResource);
      }

      if (object instanceof CDOResourceFolder)
      {
        CDOResourceFolder folder = (CDOResourceFolder)object;
        return super.getChildren(folder);
      }

      if (object instanceof CDOResourceLeaf)
      {
        return new Object[0];
      }

      return super.getChildren(object);
    }

    private Object defaultGetParent(Object object)
    {
      if (object instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)object;
        CDOResourceFolder folder = node.getFolder();

        if (folder == null)
        {
          return node.cdoView();
        }

        return folder;
      }

      if (object instanceof CDOView)
      {
        return getInput();
      }

      return super.getParent(object);
    }
  }

  /**
   * @author Eike Stepper
   */
  private final class ResourcesLabelProvider extends AdapterFactoryLabelProvider.ColorProvider
  {
    public ResourcesLabelProvider(AdapterFactory adapterFactory, Viewer viewer)
    {
      super(adapterFactory, viewer);
    }

    @Override
    public String getText(Object object)
    {
      return elementGetText(object, this::defaultGetText);
    }

    @Override
    public Image getImage(Object object)
    {
      return elementGetImage(object, this::defaultGetImage);
    }

    @Override
    public Color getForeground(Object object)
    {
      return elementGetForeground(object, this::defaultGetForeground);
    }

    private String defaultGetText(Object object)
    {
      if (object instanceof CDOView)
      {
        CDOView view = (CDOView)object;

        CDOSession session = view.getSession();
        Object repository = session.properties().get("org.eclipse.emf.cdo.explorer.repositories.CDORepository");
        if (repository != null)
        {
          // Return the label of the CDORepositoryImpl.
          return repository.toString();
        }

        CDORepositoryInfo repositoryInfo = session.getRepositoryInfo();
        return repositoryInfo.getName();
      }

      return super.getText(object);
    }

    private Image defaultGetImage(Object object)
    {
      if (object instanceof CDOView)
      {
        return SharedIcons.getImage(SharedIcons.OBJ_REPO);
      }

      return super.getImage(object);
    }

    private Color defaultGetForeground(Object object)
    {
      if (object instanceof ViewerUtil.Pending)
      {
        return ContainerItemProvider.pendingColor();
      }

      if (object instanceof CDOObject)
      {
        Color color = CDOLabelProvider.getColor((CDOObject)object);
        if (color != null)
        {
          return color;
        }
      }

      return super.getForeground(object);
    }
  }
}

/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.ui.dialogs;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.internal.ui.CDOAdapterFactoryContentProvider;
import org.eclipse.emf.cdo.internal.ui.CDOContentProvider;
import org.eclipse.emf.cdo.internal.ui.RunnableViewerRefresh;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.assembly.AssemblyModule;
import org.eclipse.emf.cdo.lm.internal.client.LMResourceSetConfiguration;
import org.eclipse.emf.cdo.lm.ui.bundle.OM;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ui.UIUtil;
import org.eclipse.net4j.util.ui.widgets.BaseDialog;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class SelectModuleResourcesDialog extends BaseDialog<TreeViewer>
{
  private final Set<URI> uris = new HashSet<>();

  private final boolean multi;

  private final LMResourceSetConfiguration configuration;

  public SelectModuleResourcesDialog(Shell shell, boolean multi, LMResourceSetConfiguration configuration)
  {
    super(shell, DEFAULT_SHELL_STYLE | SWT.APPLICATION_MODAL, "Select Module Resources", "Select resources from the current module and its dependencies.",
        OM.Activator.INSTANCE.getDialogSettings(), OM.Activator.INSTANCE.loadImageDescriptor("icons/NewModule.png"));
    this.multi = multi;
    this.configuration = configuration;
  }

  public Set<URI> getURIs()
  {
    return uris;
  }

  @Override
  protected void createUI(Composite parent)
  {
    ComposedAdapterFactory adapterFactory = new ComposedAdapterFactory(EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry());
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());

    TreeViewer viewer = new TreeViewer(parent, multi ? SWT.MULTI : SWT.SINGLE);
    viewer.getTree().setLayoutData(UIUtil.createGridData());
    viewer.setContentProvider(new ModuleResourcesContentProvider(adapterFactory));
    viewer.setLabelProvider(new ModuleResourcesLabelProvider(adapterFactory));
    viewer.setInput(configuration);
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

  /**
   * @author Eike Stepper
   */
  private static final class ModuleResourcesContentProvider extends CDOContentProvider.ContextFree
  {
    private final ComposedAdapterFactory adapterFactory;

    private final CDOAdapterFactoryContentProvider delegate;

    private final Map<AssemblyModule, CDOView> views = new LinkedHashMap<>();

    private LMResourceSetConfiguration configuration;

    public ModuleResourcesContentProvider(ComposedAdapterFactory adapterFactory)
    {
      this.adapterFactory = adapterFactory;
      delegate = new CDOAdapterFactoryContentProvider(adapterFactory);
    }

    @Override
    public void inputChanged(Viewer newViewer, Object oldInput, Object newInput)
    {
      super.inputChanged(newViewer, oldInput, newInput);
      delegate.inputChanged(newViewer, oldInput, newInput);
      configuration = (LMResourceSetConfiguration)getInput();

      views.clear();

      if (configuration != null)
      {
        Assembly assembly = configuration.getAssembly();
        AssemblyModule rootModule = assembly.getRootModule();
        views.put(rootModule, CDOUtil.getViewSet(configuration.getResourceSet()).getViews()[0]);
        assembly.forEachDependency(module -> views.put(module, configuration.getView(module)));
      }
    }

    @Override
    public boolean hasChildren(Object object)
    {
      if (object instanceof CDOResource)
      {
        return false;
      }

      return super.hasChildren(object);
    }

    @Override
    public Object[] getChildren(Object object)
    {
      if (object == configuration)
      {
        // Return the assembly modules.
        return views.keySet().toArray();
      }

      if (object instanceof AssemblyModule)
      {
        AssemblyModule module = (AssemblyModule)object;
        CDOView view = views.get(module);
        CDOResource rootResource = view.getRootResource();
        return rootResource.getContents().toArray();
      }

      if (object instanceof CDOResourceFolder)
      {
        CDOResourceFolder folder = (CDOResourceFolder)object;
        return folder.getNodes().toArray();
      }

      return new Object[0];
    }

    @Override
    public Object getParent(Object object)
    {
      if (object instanceof CDOResourceNode)
      {
        CDOResourceNode node = (CDOResourceNode)object;

        if (node.isRoot())
        {
          CDOView view = node.cdoView();

          for (Map.Entry<AssemblyModule, CDOView> entry : views.entrySet())
          {
            if (entry.getValue() == view)
            {
              return entry.getKey();
            }
          }

          return null;
        }

        return node.getFolder();
      }

      if (object instanceof AssemblyModule)
      {
        return configuration;
      }

      return super.getParent(object);
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
  }

  /**
   * @author Eike Stepper
   */
  private static final class ModuleResourcesLabelProvider extends AdapterFactoryLabelProvider
  {
    public ModuleResourcesLabelProvider(AdapterFactory adapterFactory)
    {
      super(adapterFactory);
    }

    @Override
    public String getText(Object object)
    {
      if (object instanceof AssemblyModule)
      {
        AssemblyModule module = (AssemblyModule)object;
        return module.getName() + " " + module.getVersion();
      }

      return super.getText(object);
    }
  }
}

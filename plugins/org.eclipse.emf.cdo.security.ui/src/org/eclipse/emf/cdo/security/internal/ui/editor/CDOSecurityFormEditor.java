/*
 * Copyright (c) 2013, 2015, 2016, 2019, 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.editor;

import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.security.internal.ui.bundle.OM;
import org.eclipse.emf.cdo.security.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.security.provider.SecurityItemProviderAdapterFactory;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.util.CommitException;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.command.BasicCommandStack;
import org.eclipse.emf.common.command.CommandStack;
import org.eclipse.emf.common.command.CommandStackListener;
import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.edit.EMFEditPlugin;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;
import org.eclipse.emf.edit.domain.EditingDomain;
import org.eclipse.emf.edit.domain.IEditingDomainProvider;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.ReflectiveItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.resource.ResourceItemProviderAdapterFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.IFormPage;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.statushandlers.IStatusAdapterConstants;
import org.eclipse.ui.statushandlers.StatusAdapter;
import org.eclipse.ui.statushandlers.StatusManager;

import java.util.EventObject;

/**
 * The "Security Manager" editor.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class CDOSecurityFormEditor extends FormEditor implements IEditingDomainProvider
{
  public static final String ID = "org.eclipse.emf.cdo.security.ui.CDOSecurityFormEditor"; //$NON-NLS-1$

  private ComposedAdapterFactory adapterFactory;

  private CDOView view;

  private EditingDomain editingDomain;

  private CDOSecurityPage mainPage;

  private CommandStackListener dirtyStackListener;

  public CDOSecurityFormEditor()
  {
  }

  @Override
  protected void addPages()
  {
    try
    {
      // Don't show tabs because we have only the one
      ((CTabFolder)getContainer()).setTabHeight(0);

      mainPage = new CDOSecurityPage(this);
      addPage(mainPage);
    }
    catch (PartInitException e)
    {
      OM.LOG.error(e);
    }
  }

  @Override
  public boolean isDirty()
  {
    return view != null && view.isDirty();
  }

  @Override
  public void doSave(IProgressMonitor monitor)
  {
    if (view instanceof CDOTransaction)
    {
      try
      {
        ((CDOTransaction)view).commit(monitor);
        fireDirtyStateChanged();
      }
      catch (CommitException e)
      {
        StatusAdapter status = new StatusAdapter(new Status(IStatus.ERROR, OM.BUNDLE_ID, Messages.CDOSecurityFormEditor_0, e));
        status.setProperty(IStatusAdapterConstants.TITLE_PROPERTY, Messages.CDOSecurityFormEditor_1);
        status.setProperty(IStatusAdapterConstants.TIMESTAMP_PROPERTY, System.currentTimeMillis());
        StatusManager.getManager().handle(status, StatusManager.SHOW);
      }
    }
  }

  protected void fireDirtyStateChanged()
  {
    Display display = getContainer().getDisplay();
    if (display == Display.getCurrent())
    {
      firePropertyChange(IEditorPart.PROP_DIRTY);
    }
    else
    {
      display.asyncExec(new Runnable()
      {
        @Override
        public void run()
        {
          fireDirtyStateChanged();
        }
      });
    }
  }

  @Override
  public void doSaveAs()
  {
  }

  @Override
  public boolean isSaveAsAllowed()
  {
    // Cannot create new security realms using Save As
    return false;
  }

  boolean isReadOnly()
  {
    return view == null || view.isReadOnly();
  }

  @Override
  public void init(IEditorSite site, IEditorInput input) throws PartInitException
  {
    super.init(site, input);
    initializeEditingDomain();
  }

  @Override
  public void dispose()
  {
    try
    {
      if (dirtyStackListener != null)
      {
        if (editingDomain != null)
        {
          editingDomain.getCommandStack().removeCommandStackListener(dirtyStackListener);
        }

        dirtyStackListener = null;
      }
    }
    finally
    {
      super.dispose();
    }
  }

  protected void initializeEditingDomain()
  {
    try
    {
      CDOResource resource = getResource();
      view = resource.cdoView();
      dirtyStackListener = new CommandStackListener()
      {
        @Override
        public void commandStackChanged(final EventObject event)
        {
          fireDirtyStateChanged();
        }
      };

      BasicCommandStack commandStack = new BasicCommandStack();
      commandStack.addCommandStackListener(dirtyStackListener);

      ResourceSet resourceSet = view.getResourceSet();
      editingDomain = createEditingDomain(commandStack, resourceSet);

      // This adapter provides the EditingDomain of the Editor
      resourceSet.eAdapters().add(new AdapterFactoryEditingDomain.EditingDomainProvider(editingDomain));
    }
    catch (RuntimeException ex)
    {
      OM.LOG.error(ex);
      throw ex;
    }
  }

  protected EditingDomain createEditingDomain(CommandStack commandStack, ResourceSet resourceSet)
  {
    ComposedAdapterFactory.Descriptor.Registry registry = EMFEditPlugin.getComposedAdapterFactoryDescriptorRegistry();
    adapterFactory = new ComposedAdapterFactory(registry);
    adapterFactory.addAdapterFactory(new SecurityItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new ResourceItemProviderAdapterFactory());
    adapterFactory.addAdapterFactory(new ReflectiveItemProviderAdapterFactory());

    return new AdapterFactoryEditingDomain(adapterFactory, commandStack, resourceSet);
  }

  protected CDOResource getResource()
  {
    CDOResource result = null;

    IEditorInput input = getEditorInput();
    if (input instanceof CDOEditorInput)
    {
      CDOEditorInput cdoInput = (CDOEditorInput)input;
      result = cdoInput.getView().getResource(cdoInput.getResourcePath());
    }

    return result;
  }

  protected CDOView getView()
  {
    return view;
  }

  protected AdapterFactory getAdapterFactory()
  {
    return adapterFactory;
  }

  @Override
  public EditingDomain getEditingDomain()
  {
    return editingDomain;
  }

  public IActionBars getActionBars()
  {
    return ((CDOSecurityFormActionBarContributor)getEditorSite().getActionBarContributor()).getActionBars();
  }

  @Override
  public void setFocus()
  {
    super.setFocus();

    IFormPage page = getActivePageInstance();
    if (page != null && page instanceof CDOSecurityPage)
    {
      // Ensure that the first focusable part is focused so that the page's toolbar will not be focused
      // (which shows ugly blue highlights on the toolbar buttons, at least on Mac OS X)
      page.setFocus();
    }
  }

  @Override
  protected Composite createPageContainer(Composite parent)
  {
    Composite container = super.createPageContainer(parent);

    IWorkbenchHelpSystem helpSystem = PlatformUI.getWorkbench().getHelpSystem();
    helpSystem.setHelp(container, ID);

    return container;
  }
}

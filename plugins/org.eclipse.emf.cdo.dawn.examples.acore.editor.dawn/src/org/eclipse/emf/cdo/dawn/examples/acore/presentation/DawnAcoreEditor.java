/*
 * Copyright (c) 2010-2013, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *     Christian W. Damus (CEA) - bug 399933 NPE in editor when finishing Ecore wizard
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.presentation;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditorSupport;
import org.eclipse.emf.cdo.dawn.emf.editors.impl.DawnEMFEditorSupport;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.ui.DawnLabelProvider;
import org.eclipse.emf.cdo.dawn.ui.DawnSelectionViewerAdapterFactoryContentProvider;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;

/**
 * This is an example of a Acore model editor. <!-- begin-user-doc --> <!-- end-user-doc -->
 *
 * @generated
 */
public class DawnAcoreEditor extends AcoreEditor implements IDawnEditor
{
  private IDawnEditorSupport dawnEditorSupport;

  public static String ID = "org.eclipse.emf.cdo.dawn.examples.acore.presentation.DawnAcoreEditorID";

  public DawnAcoreEditor()
  {
    super();
    dawnEditorSupport = new DawnEMFEditorSupport(this);
  }

  @Override
  protected void setInput(IEditorInput input)
  {
    super.setInput(input);
    if (input instanceof DawnEditorInput)
    {
      dawnEditorSupport.setView(((DawnEditorInput)input).getView());
      dawnEditorSupport.registerListeners();
    }
  }

  @Override
  protected void setInputWithNotify(IEditorInput input)
  {
    super.setInput(input);
    if (input instanceof DawnEditorInput)
    {
      CDOResource resource = ((DawnEditorInput)input).getResource();
      URI uri = URI.createURI(((DawnEditorInput)input).getURI().toString());

      if (resource == null || resource.cdoView() == null)
      {
        ResourceSet resourceSet = editingDomain.getResourceSet();
        CDOTransaction transaction = CDOConnectionUtil.instance.openCurrentTransaction(resourceSet, uri.toString());

        resource = (CDOResource)resourceSet.getResource(uri, true);

        if (resource == null || resource.cdoView() == null)
        {
          resource = transaction.getOrCreateResource(uri.toString());
        }
      }

      ((DawnEditorInput)input).setResource(resource);
      dawnEditorSupport.setView(((DawnEditorInput)input).getView());
      dawnEditorSupport.registerListeners();
    }
  }

  @Override
  public void createPages()
  {
    super.createPages();

    if (!(getEditorInput() instanceof DawnEditorInput))
    {
      return;
    }

    selectionViewer.setContentProvider(new DawnSelectionViewerAdapterFactoryContentProvider(adapterFactory, ((DawnEditorInput)getEditorInput()).getResource()));
    selectionViewer.setLabelProvider(new DawnLabelProvider(adapterFactory, dawnEditorSupport.getView(), selectionViewer));
    parentViewer.setLabelProvider(new DawnLabelProvider(adapterFactory, dawnEditorSupport.getView(), selectionViewer));
    listViewer.setLabelProvider(new DawnLabelProvider(adapterFactory, dawnEditorSupport.getView(), selectionViewer));
    treeViewer.setLabelProvider(new DawnLabelProvider(adapterFactory, dawnEditorSupport.getView(), selectionViewer));
    tableViewer.setLabelProvider(new DawnLabelProvider(adapterFactory, dawnEditorSupport.getView(), selectionViewer));
    treeViewerWithColumns.setLabelProvider(new DawnLabelProvider(adapterFactory, dawnEditorSupport.getView(), selectionViewer));

    CDOResource resource = ((DawnEditorInput)getEditorInput()).getResource();

    selectionViewer.setInput(resource.getResourceSet());
    selectionViewer.setSelection(new StructuredSelection(resource), true);

    parentViewer.setContentProvider(new ReverseAdapterFactoryContentProvider(adapterFactory));

  }

  @Override
  public void doSave(IProgressMonitor progressMonitor)
  {
    CDOView view = dawnEditorSupport.getView();
    if (view instanceof CDOTransaction)
    {
      if (view.hasConflict())
      {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "conflict", "Your Resource is in conflict and cannot be committed");
      }
      else
      {
        super.doSave(progressMonitor);
      }
    }
    {
      super.doSave(progressMonitor);
    }
  }

  public String getContributorID()
  {
    return null;
  }

  public CDOView getView()
  {
    return dawnEditorSupport.getView();
  }

  public void setDirty()
  {
    dawnEditorSupport.setDirty(true);
  }

  @Override
  public void dispose()
  {
    try
    {
      super.dispose();
    }
    finally
    {
      dawnEditorSupport.close();
    }
  }

  public String getContributorId()
  {
    return ID;
  }

  public IDawnEditorSupport getDawnEditorSupport()
  {
    return dawnEditorSupport;
  }
}

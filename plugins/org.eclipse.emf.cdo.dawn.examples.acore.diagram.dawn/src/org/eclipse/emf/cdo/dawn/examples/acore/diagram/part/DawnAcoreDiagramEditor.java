/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.part;

import org.eclipse.emf.cdo.dawn.editors.IDawnEditor;
import org.eclipse.emf.cdo.dawn.editors.IDawnEditorSupport;
import org.eclipse.emf.cdo.dawn.gmf.editors.impl.DawnGMFEditorSupport;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.transaction.TransactionException;

import org.eclipse.emf.common.ui.URIEditorInput;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.AbstractDocumentProvider;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

/**
 * @author Martin Fluegge
 */
public class DawnAcoreDiagramEditor extends AcoreDiagramEditor implements IDawnEditor
{
  public static String ID = "org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.DawnAcoreDiagramEditor";

  private IDawnEditorSupport dawnEditorSupport;

  public DawnAcoreDiagramEditor()
  {
    super();
    AcoreDiagramEditorPlugin.getInstance().logInfo("CDO Editor ist starting");
    setDocumentProvider(new DawnAcoreDocumentProvider());
    setDawnEditorSupport(new DawnGMFEditorSupport(this));
  }

  @Override
  public void setInput(IEditorInput input)
  {
    AcoreDiagramEditorPlugin.getInstance().logInfo("Setting input for DawnAcoreDiagramEditor (" + input + ")");

    try
    {
      doSetInput(input, true);
    }
    catch (CoreException x)
    {
      x.printStackTrace(System.err);
      String title = x.getMessage();
      String msg = x.getMessage();
      Shell shell = getSite().getShell();
      ErrorDialog.openError(shell, title, msg, x.getStatus());
    }

    dawnEditorSupport.setView(((DawnEditorInput)input).getView());
  }

  @Override
  protected void initializeGraphicalViewer()
  {
    super.initializeGraphicalViewer();
    dawnEditorSupport.registerListeners();
  }

  @Override
  public void doSave(IProgressMonitor monitor)
  {
    try
    {
      dawnEditorSupport.setDirty(false);
      updateState(getEditorInput());
      validateState(getEditorInput());
      performSave(false, monitor);
    }
    catch (TransactionException e)
    {
      if (e.getMessage().contains("conflict"))
      {
        MessageDialog.openError(Display.getDefault().getActiveShell(), "conflict",
            "Your Resource is in conflict and cannot be committed");
      }
      else
      {
        throw e;
      }
    }
  }

  @Override
  public boolean isDirty()
  {
    return dawnEditorSupport.isDirty();
  }

  public String getContributorID()
  {
    return ID;
  }

  /**
   * Have to override this method to change the the DocuemtnProvider behavior.
   */
  @Override
  protected void setDocumentProvider(IEditorInput input)
  {
    if (input instanceof IFileEditorInput || input instanceof URIEditorInput || input instanceof CDOEditorInput)
    {
      setDocumentProvider(getDocumentProvider());
    }
    else
    {
      super.setDocumentProvider(input);
    }
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

  /**
   * @since 1.0
   */
  public CDOView getView()
  {
    return dawnEditorSupport.getView();
  }

  public void setDirty()
  {
    dawnEditorSupport.setDirty(true);
    ((AbstractDocumentProvider)getDocumentProvider()).changed(getEditorInput());
  }

  public void setDawnEditorSupport(IDawnEditorSupport dawnEditorSupport)
  {
    this.dawnEditorSupport = dawnEditorSupport;
  }

  public IDawnEditorSupport getDawnEditorSupport()
  {
    return dawnEditorSupport;
  }
}

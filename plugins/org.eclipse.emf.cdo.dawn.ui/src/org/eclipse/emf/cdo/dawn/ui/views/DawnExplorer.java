/*******************************************************************************
 * Copyright (c) 2010 Martin Fluegge (Berlin, Germany).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 ******************************************************************************/
package org.eclipse.emf.cdo.dawn.ui.views;

import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.dawn.util.connection.CDOConnectionUtil;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.internal.ui.editor.CDOEditor;
import org.eclipse.emf.cdo.internal.ui.views.CDOSessionsView;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.ui.CDOEditorUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.ui.views.ContainerItemProvider;
import org.eclipse.net4j.util.ui.views.IElementFilter;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * @author Martin Fluegge
 */
public class DawnExplorer extends CDOSessionsView
{
  /**
   * The ID of the view as specified by the extension.
   */
  public static final String ID = "org.eclipse.emf.cdo.dawn.ui.views.DawnExplorer";

  private CDOView view;

  public CDOView getView()
  {
    return view;
  }

  /**
   * The constructor.
   */
  public DawnExplorer()
  {

    CDOConnectionUtil.instance.init("repo1", "tcp", "localhost");
    CDOSession session = CDOConnectionUtil.instance.openSession();
    view = CDOConnectionUtil.instance.openView(session);
  }

  @Override
  public void dispose()
  {
    // actually no one else should use this view
    view.close();
  }

  @Override
  protected ContainerItemProvider<IContainer<Object>> createContainerItemProvider()
  {
    return new DawnItemProvider(getSite().getPage(), this, new IElementFilter()
    {
      public boolean filter(Object element)
      {
        return element instanceof CDOSession;
      }
    });
  }

  @Override
  protected void hookDoubleClick()
  {
    super.hookDoubleClick();

    getViewer().addDoubleClickListener(new IDoubleClickListener()
    {
      public void doubleClick(DoubleClickEvent event)
      {
        Object obj = ((IStructuredSelection)event.getSelection()).getFirstElement();
        if (obj instanceof CDOResource)
        {
          CDOResource resource = (CDOResource)obj;

          // TODO access the registry to get the right editor for the extension
          System.out.println(resource.getName());
          IEditorDescriptor[] editors = PlatformUI.getWorkbench().getEditorRegistry().getEditors(resource.getName());

          System.out.println("Editors:  " + editors);
          System.out.println("Editors size:  " + editors.length);

          String editorID = getEditorIdForDawnEditor(editors);

          if (editorID != null && !editorID.equals(""))
          {
            System.out.println("Opening Dawn Editor " + editorID);
            // CDOEditorInput editorInput = CDOEditorUtil.createCDOEditorInput(view, ((CDOResource)obj).getPath(),
            // true);
            // IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
            // return null != page.openEditor(new URIEditorInput(diagram.getURI()), DawnClassdiagramDiagramEditor.ID);

            // DawnExplorer.this.getSite().getPage().openEditor(new URIEditorInput(diagram.getURI(),
            // CDOEditor.EDITOR_ID);
            // DawnExplorer.this.getSite().getPage().openEditor(editorInput, CDOEditor.EDITOR_ID);
            try
            {
              // URIEditorInput editorInput=new URIEditorInput(resource.getURI());
              // DawnEditorInput editorInput=new DawnEditorInput(resource);
              DawnEditorInput editorInput = new DawnEditorInput(resource.getURI());
              // CDOEditorInput editorInput = CDOEditorUtil.createCDOEditorInput(view, ((CDOResource)obj).getPath(),
              // true);
              DawnExplorer.this.getSite().getPage().openEditor(editorInput, editorID);
            }
            catch (PartInitException e)
            {
              e.printStackTrace();
            }
          }
          else
          {
            System.out.println("Opening Resource " + resource.getName());

            CDOTransaction transaction = view.getSession().openTransaction();
            CDOEditorInput editorInput = CDOEditorUtil.createCDOEditorInput(transaction, ((CDOResource)obj).getPath(),
                true);
            try
            {
              DawnExplorer.this.getSite().getPage().openEditor(editorInput, CDOEditor.EDITOR_ID);
            }
            catch (PartInitException e)
            {
              e.printStackTrace();
            }
          }
        }
      }

      private String getEditorIdForDawnEditor(IEditorDescriptor[] editors)
      {
        String id = "";
        for (IEditorDescriptor editorDescriptor : editors)
        {
          // TODO make this more stable by getting the class name more reliably
          if (editorDescriptor.getId().contains(".Dawn"))
          {
            return editorDescriptor.getId();
          }
        }

        return id;
      }
    });
  }
}

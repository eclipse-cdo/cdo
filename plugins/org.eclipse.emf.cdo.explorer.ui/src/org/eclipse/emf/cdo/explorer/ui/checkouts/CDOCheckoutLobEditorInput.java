/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.eresource.CDOFileResource;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout.State;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager.CheckoutStateEvent;
import org.eclipse.emf.cdo.internal.ui.editor.CDOLobEditorInput;
import org.eclipse.emf.cdo.internal.ui.editor.CDOLobStorage;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.container.ContainerEventAdapter;
import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;
import org.eclipse.net4j.util.ui.GlobalPartAdapter;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

/**
 * A text editor will consult {@link CDOLobStorage} for this input.
 *
 * @author Eike Stepper
 */
public class CDOCheckoutLobEditorInput extends CDOLobEditorInput implements IPersistableElement
{
  protected static final String URI_TAG = "uri";

  protected static final String COMMIT_ON_SAVE_TAG = "commitOnSave";

  private final CDOCheckout checkout;

  public CDOCheckoutLobEditorInput(URI uri)
  {
    this(uri, false);
  }

  public CDOCheckoutLobEditorInput(URI uri, boolean commitOnSave)
  {
    super(getFile(uri), commitOnSave);
    setURI(uri);

    checkout = CDOExplorerUtil.getCheckout(getResource());
  }

  protected final CDOCheckout getCheckout()
  {
    return checkout;
  }

  @Override
  public IPersistableElement getPersistable()
  {
    return this;
  }

  @Override
  public void saveState(IMemento memento)
  {
    URI uri = getURI();
    memento.putString(URI_TAG, uri == null ? null : uri.toString());
    memento.putBoolean(COMMIT_ON_SAVE_TAG, isCommitOnSave());
  }

  @Override
  public String getFactoryId()
  {
    return ElementFactory.ID;
  }

  public static IEditorPart openEditor(IWorkbenchPage page, String editorID, URI uri) throws PartInitException
  {
    for (IEditorReference editorReference : page.getEditorReferences())
    {
      if (!ObjectUtil.equals(editorReference.getId(), editorID))
      {
        continue;
      }

      IEditorInput editorInput = editorReference.getEditorInput();
      if (!(editorInput instanceof CDOCheckoutLobEditorInput))
      {
        continue;
      }

      CDOCheckoutLobEditorInput lobInput = (CDOCheckoutLobEditorInput)editorInput;
      if (!lobInput.getURI().equals(uri))
      {
        continue;
      }

      IEditorPart editor = editorReference.getEditor(true);
      page.activate(editor);
      return editor;
    }

    IEditorInput lobInput = new CDOCheckoutLobEditorInput(uri, true);
    return page.openEditor(lobInput, editorID);
  }

  private static CDOFileResource<?> getFile(URI uri)
  {
    String id = uri.authority();
    CDOCheckout checkout = CDOExplorerUtil.getCheckout(id);
    if (checkout != null)
    {
      checkout.open();

      CDOView view;
      if (checkout.isReadOnly())
      {
        view = checkout.getView();
      }
      else
      {
        view = checkout.openTransaction();
      }

      CDOResourceNode node = view.getResourceNode(uri.path());
      if (node instanceof CDOFileResource)
      {
        return (CDOFileResource<?>)node;
      }
    }

    return null;
  }

  static
  {
    // Close CDOLob editors when checkouts become unavailable.
    CDOExplorerUtil.getCheckoutManager().addListener(new ContainerEventAdapter<CDOCheckout>()
    {
      @Override
      protected void onRemoved(IContainer<CDOCheckout> container, CDOCheckout checkout)
      {
        closeLobEditors(checkout);
      }

      @Override
      protected void notifyOtherEvent(IEvent event)
      {
        if (event instanceof CheckoutStateEvent)
        {
          CheckoutStateEvent e = (CheckoutStateEvent)event;
          if (e.getNewState() == State.Closed)
          {
            closeLobEditors(e.getCheckout());
          }
        }
      }

      private void closeLobEditors(CDOCheckout checkout)
      {
        for (IWorkbenchWindow window : PlatformUI.getWorkbench().getWorkbenchWindows())
        {
          for (IWorkbenchPage page : window.getPages())
          {
            for (IEditorReference reference : page.getEditorReferences())
            {
              IEditorPart editor = reference.getEditor(false);
              if (editor != null)
              {
                IEditorInput input = editor.getEditorInput();
                if (input instanceof CDOCheckoutLobEditorInput)
                {
                  CDOCheckoutLobEditorInput lobInput = (CDOCheckoutLobEditorInput)input;
                  if (lobInput.getCheckout() == checkout)
                  {
                    UIUtil.syncExec(() -> page.closeEditor(editor, false));
                  }
                }
              }
            }
          }
        }

      }
    });

    // Close CDOLob editor transaction when an editor is closed.
    new GlobalPartAdapter()
    {
      @Override
      public void partClosed(IWorkbenchPartReference partRef)
      {
        IWorkbenchPart part = partRef.getPart(false);
        if (part instanceof IEditorPart)
        {
          IEditorPart editor = (IEditorPart)part;

          IEditorInput input = editor.getEditorInput();
          if (input instanceof CDOCheckoutLobEditorInput)
          {
            CDOCheckoutLobEditorInput lobInput = (CDOCheckoutLobEditorInput)input;

            CDOView view = lobInput.getResource().cdoView();
            CDOCheckout checkout = CDOExplorerUtil.getCheckout(view);
            if (checkout == null || checkout.getView() != view)
            {
              LifecycleUtil.deactivate(view);
            }
          }
        }
      }
    };
  }

  /**
   * @author Eike Stepper
   */
  public static class ElementFactory implements IElementFactory
  {
    public static final String ID = "org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutLobEditorInput.ElementFactory";

    public ElementFactory()
    {
    }

    @Override
    public IAdaptable createElement(IMemento memento)
    {
      URI uri = URI.createURI(memento.getString(URI_TAG));
      boolean commitOnSave = memento.getBoolean(COMMIT_ON_SAVE_TAG);
      return new CDOCheckoutLobEditorInput(uri, commitOnSave);
    }
  }
}

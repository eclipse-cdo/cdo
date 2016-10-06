/*
 * Copyright (c) 2010-2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.ecoretools.diagram.part;

import org.eclipse.emf.cdo.dawn.gmf.util.DawnDiagramUpdater;
import org.eclipse.emf.cdo.dawn.transaction.DawnDiagramEditingDomainFactory;
import org.eclipse.emf.cdo.dawn.ui.DawnEditorInput;
import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.ui.URIEditorInput;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDiagramEditorPlugin;
import org.eclipse.emf.ecoretools.diagram.part.EcoreDocumentProvider;
import org.eclipse.emf.ecoretools.diagram.part.Messages;
import org.eclipse.emf.transaction.NotificationFilter;
import org.eclipse.emf.transaction.TransactionalEditingDomain;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.DiagramDocument;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDiagramDocument;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.document.IDocument;
import org.eclipse.gmf.runtime.diagram.ui.resources.editor.internal.util.DiagramIOUtil;
import org.eclipse.gmf.runtime.emf.core.resources.GMFResourceFactory;
import org.eclipse.gmf.runtime.notation.Diagram;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.part.FileEditorInput;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author Martin Fluegge
 */
@SuppressWarnings("restriction")
public class DawnEcoreDocumentProvider extends EcoreDocumentProvider
{

  public DawnEcoreDocumentProvider()
  {
    super();
    EcoreDiagramEditorPlugin.getInstance().logInfo("Using DawnEcoreDocumentProvider instead of the original one");
  }

  /**
   * override to change creation of editingdomain
   */
  @Override
  protected IDocument createEmptyDocument()
  {
    DiagramDocument document = new DiagramDocument();
    document.setEditingDomain(createEditingDomain());
    return document;
  }

  /**
   * override to change the EditingDomain
   */
  private TransactionalEditingDomain createEditingDomain()
  {
    TransactionalEditingDomain editingDomain = DawnDiagramEditingDomainFactory.getInstance().createEditingDomain();
    editingDomain.setID("org.eclipse.emf.cdo.dawn.ecoretools.diagram.EditingDomain"); //$NON-NLS-1$
    final NotificationFilter diagramResourceModifiedFilter = NotificationFilter
        .createNotifierFilter(editingDomain.getResourceSet())
        .and(NotificationFilter.createEventTypeFilter(Notification.ADD))
        .and(NotificationFilter.createFeatureFilter(ResourceSet.class, ResourceSet.RESOURCE_SET__RESOURCES));
    editingDomain.getResourceSet().eAdapters().add(new Adapter()
    {
      private Notifier myTarger;

      public Notifier getTarget()
      {
        return myTarger;
      }

      public boolean isAdapterForType(Object type)
      {
        return false;
      }

      public void notifyChanged(Notification notification)
      {
        if (diagramResourceModifiedFilter.matches(notification))
        {
          Object value = notification.getNewValue();
          if (value instanceof Resource)
          {
            ((Resource)value).setTrackingModification(true);
          }
        }
      }

      public void setTarget(Notifier newTarget)
      {
        myTarger = newTarget;
      }
    });

    return editingDomain;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  protected void setDocumentContent(IDocument document, IEditorInput element) throws CoreException
  {
    EcoreDiagramEditorPlugin.getInstance().logInfo("Editor Input: " + element.getName());

    IDiagramDocument diagramDocument = (IDiagramDocument)document;
    TransactionalEditingDomain domain = diagramDocument.getEditingDomain();
    if (element instanceof FileEditorInput)
    {
      IStorage storage = ((FileEditorInput)element).getStorage();
      Diagram diagram = DiagramIOUtil.load(domain, storage, true, getProgressMonitor());
      document.setContent(diagram);
    }
    else if (element instanceof URIEditorInput)
    {
      URIEditorInput editorInput = (URIEditorInput)element;

      URI uri = editorInput.getURI();
      Resource resource = null;
      try
      {
        URI dawnURI = URI.createURI(uri.toString().replace("cdo", "dawn"));
        resource = domain.getResourceSet().getResource(dawnURI, false);
        if (resource == null)
        {
          resource = domain.getResourceSet().getResource(dawnURI, true);
        }

        if (editorInput instanceof DawnEditorInput)
        {
          ((DawnEditorInput)editorInput).setResource((CDOResource)resource);
        }
        if (!resource.isLoaded())
        {
          try
          {
            Map options = new HashMap(GMFResourceFactory.getDefaultLoadOptions());
            resource.load(options);
          }
          catch (IOException e)
          {
            resource.unload();
            throw e;
          }
        }
        if (uri.fragment() != null)
        {
          EObject rootElement = resource.getEObject(uri.fragment());
          if (rootElement instanceof Diagram)
          {
            Diagram diagram = (Diagram)rootElement;
            DawnDiagramUpdater.initializeElement(diagram);
            document.setContent(diagram);

            return;
          }
        }
        else
        {
          for (Iterator<?> it = resource.getContents().iterator(); it.hasNext();)
          {
            Object rootElement = it.next();
            if (rootElement instanceof Diagram)
            {
              Diagram diagram = (Diagram)rootElement;
              DawnDiagramUpdater.initializeElement(diagram);
              document.setContent(diagram);

              return;
            }
          }
        }
        throw new RuntimeException(Messages.EcoreDocumentProvider_NoDiagramInResourceError);
      }
      catch (Exception e)
      {
        CoreException thrownExcp = null;
        if (e instanceof CoreException)
        {
          thrownExcp = (CoreException)e;
        }
        else
        {
          String msg = e.getLocalizedMessage();
          thrownExcp = new CoreException(new Status(IStatus.ERROR, EcoreDiagramEditorPlugin.ID, 0,
              msg != null ? msg : Messages.EcoreDocumentProvider_DiagramLoadingError, e));
        }
        throw thrownExcp;
      }
    }
    else
    {
      throw new CoreException(new Status(IStatus.ERROR, EcoreDiagramEditorPlugin.ID, 0,
          NLS.bind(Messages.EcoreDocumentProvider_IncorrectInputError,
              new Object[] { element, "org.eclipse.ui.part.FileEditorInput", //$NON-NLS-1$
                  "org.eclipse.emf.common.ui.URIEditorInput" }), //$NON-NLS-1$
          null));
    }
  }

  @Override
  public void changed(Object element)
  {
    if (element instanceof IEditorInput)
    {
      fireElementDirtyStateChanged(element, true);
    }
  }
}

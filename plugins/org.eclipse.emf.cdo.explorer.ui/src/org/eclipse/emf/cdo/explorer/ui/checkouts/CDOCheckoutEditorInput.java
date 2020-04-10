/*
 * Copyright (c) 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer.ui.checkouts;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.ui.bundle.OM;
import org.eclipse.emf.cdo.internal.ui.AbstractCDOEditorInput;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.ui.CDOEditorInput2;
import org.eclipse.emf.cdo.ui.CDOEditorInput3;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.CheckUtil;
import org.eclipse.net4j.util.om.monitor.EclipseMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor;
import org.eclipse.net4j.util.om.monitor.OMMonitor.Async;

import org.eclipse.emf.common.util.URI;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Eike Stepper
 */
public class CDOCheckoutEditorInput extends PlatformObject implements CDOEditorInput2, CDOEditorInput3, IPersistableElement
{
  protected static final String URI_TAG = "uri";

  private final URI uri;

  private CDOCheckout checkout;

  private CDOView view;

  private String resourcePath;

  private CDOID objectID;

  public CDOCheckoutEditorInput(URI uri)
  {
    CheckUtil.checkArg(uri, "uri is null");
    this.uri = uri;
  }

  public URI getURI()
  {
    return uri;
  }

  public CDOCheckout getCheckout()
  {
    if (checkout == null)
    {
      checkout = CDOExplorerUtil.getCheckout(uri);
    }

    return checkout;
  }

  @Override
  public CDOView getView()
  {
    return view;
  }

  @Override
  public CDOView openView(IProgressMonitor progressMonitor)
  {
    if (view == null)
    {
      CDOCheckout checkout = getCheckout();

      OMMonitor monitor = new EclipseMonitor(progressMonitor, "Opening checkout...");
      monitor.begin();

      try
      {
        Async async = monitor.forkAsync();

        try
        {
          checkout.open();

          progressMonitor.setTaskName("Opening view...");
          view = checkout.openView();
          configureView(view);
        }
        catch (Exception ex)
        {
          OM.LOG.error(ex);

          String message = ex.getLocalizedMessage();
          if (message == null || message.isEmpty())
          {
            message = "Could not open checkout.";
          }

          message += "\nCheck the error log for details.";
          progressMonitor.setTaskName(message);
        }
        finally
        {
          async.stop();
        }
      }
      finally
      {
        monitor.done();
      }
    }

    return view;
  }

  @Override
  public boolean isViewOwned()
  {
    return true;
  }

  @Override
  public String getResourcePath()
  {
    if (resourcePath == null)
    {
      resourcePath = CDOURIUtil.extractResourcePath(uri);
    }

    return resourcePath;
  }

  @Override
  public CDOID getObjectID()
  {
    if (objectID == null)
    {
      objectID = CDOID.NULL;

      if (uri.hasFragment())
      {
        CDOObject cdoObject = CDOUtil.getCDOObject(getView().getResourceSet().getEObject(uri, true));
        if (cdoObject != null)
        {
          objectID = cdoObject.cdoID();
        }
      }
    }

    return objectID == CDOID.NULL ? null : objectID;
  }

  @Override
  public void setObjectID(CDOID objectID)
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean exists()
  {
    return true;
  }

  @Override
  public ImageDescriptor getImageDescriptor()
  {
    CDOView view = getView();
    return CDOItemProvider.getViewImageDescriptor(view);
  }

  @Override
  public String getName()
  {
    String resourcePath = getResourcePath();
    if (resourcePath != null)
    {
      return new Path(resourcePath).lastSegment();
    }

    return getView().getSession().getRepositoryInfo().getName();
  }

  @Override
  public String getToolTipText()
  {
    CDOView view = getView();
    String resourcePath = getResourcePath();
    return AbstractCDOEditorInput.formatToolTipText(view, resourcePath);
  }

  @Override
  public IPersistableElement getPersistable()
  {
    if (OM.PREF_REMEMBER_OPEN_EDITORS.getValue() == Boolean.TRUE)
    {
      return this;
    }

    return null;
  }

  @Override
  public void saveState(IMemento memento)
  {
    memento.putString(URI_TAG, uri.toString());
  }

  @Override
  public String getFactoryId()
  {
    return ElementFactory.ID;
  }

  @Override
  public int hashCode()
  {
    return uri.hashCode();
  }

  @Override
  public boolean equals(Object o)
  {
    return this == o || o instanceof CDOCheckoutEditorInput && uri.equals(((CDOCheckoutEditorInput)o).getURI());
  }

  protected void configureView(CDOView view)
  {
    if (view instanceof CDOTransaction)
    {
      configureTransaction((CDOTransaction)view);
    }
  }

  protected void configureTransaction(CDOTransaction transaction)
  {
    CDOCheckoutEditorOpener.addConflictResolver(transaction);
  }

  /**
   * @author Eike Stepper
   */
  public static class ElementFactory implements IElementFactory
  {
    public static final String ID = "org.eclipse.emf.cdo.explorer.ui.checkouts.CDOCheckoutEditorInput.ElementFactory";

    public ElementFactory()
    {
    }

    @Override
    public IAdaptable createElement(IMemento memento)
    {
      URI uri = URI.createURI(memento.getString(URI_TAG));
      return new CDOCheckoutEditorInput(uri);
    }
  }
}

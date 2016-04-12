/*
 * Copyright (c) 2009-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Victor Roldan Betancort - maintenance
 */
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.ui.messages.Messages;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOEditorInput2;
import org.eclipse.emf.cdo.ui.CDOItemProvider;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOEditorInputImpl extends PlatformObject implements CDOEditorInput2
{
  private CDOView view;

  private boolean viewOwned;

  private String resourcePath;

  private CDOID objectID;

  public CDOEditorInputImpl(CDOView view, String resourcePath)
  {
    this(view, resourcePath, false);
  }

  public CDOEditorInputImpl(CDOView view, String resourcePath, boolean viewOwned)
  {
    this.view = view;
    this.viewOwned = viewOwned;
    this.resourcePath = resourcePath;
  }

  public CDOView getView()
  {
    return view;
  }

  public boolean isViewOwned()
  {
    return viewOwned;
  }

  public String getResourcePath()
  {
    return resourcePath;
  }

  public CDOID getObjectID()
  {
    return objectID;
  }

  public void setObjectID(CDOID objectID)
  {
    this.objectID = objectID;
  }

  public boolean exists()
  {
    return true;
  }

  public ImageDescriptor getImageDescriptor()
  {
    return CDOItemProvider.getViewImageDescriptor(view);
  }

  public String getName()
  {
    if (resourcePath != null)
    {
      return new Path(resourcePath).lastSegment();
    }

    return view.getSession().getRepositoryInfo().getName();
  }

  public IPersistableElement getPersistable()
  {
    return null;
  }

  public String getToolTipText()
  {
    if (view.isClosed())
    {
      return Messages.getString("CDOEditorInputImpl.0"); //$NON-NLS-1$
    }

    CDOSession session = view.getSession();
    String repositoryName = session.getRepositoryInfo().getName();

    StringBuilder builder = new StringBuilder();
    builder.append(repositoryName);
    if (resourcePath != null)
    {
      builder.append(resourcePath);
    }

    builder.append(" ["); //$NON-NLS-1$
    builder.append(session.getSessionID());
    builder.append(":"); //$NON-NLS-1$
    builder.append(view.getViewID());
    builder.append("]"); //$NON-NLS-1$

    if (view.isReadOnly())
    {
      builder.append(" readonly"); //$NON-NLS-1$
    }

    long timeStamp = view.getTimeStamp();
    if (timeStamp != CDOView.UNSPECIFIED_DATE)
    {
      builder.append(CDOCommonUtil.formatTimeStamp(timeStamp));
    }

    return builder.toString();
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(view) ^ ObjectUtil.hashCode(resourcePath);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof CDOEditorInputImpl)
    {
      CDOEditorInputImpl that = (CDOEditorInputImpl)obj;
      return ObjectUtil.equals(view, that.view) && ObjectUtil.equals(resourcePath, that.resourcePath);
    }

    return false;
  }
}

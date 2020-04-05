/*
 * Copyright (c) 2009-2012, 2016, 2019, 2020 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
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
 */
public abstract class AbstractCDOEditorInput extends PlatformObject implements CDOEditorInput2
{
  private String resourcePath;

  private CDOID objectID;

  public AbstractCDOEditorInput(String resourcePath, CDOID objectID)
  {
    this.resourcePath = resourcePath;
    this.objectID = objectID;
  }

  @Override
  public String getResourcePath()
  {
    return resourcePath;
  }

  @Override
  public CDOID getObjectID()
  {
    return objectID;
  }

  @Override
  public void setObjectID(CDOID objectID)
  {
    this.objectID = objectID;
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
    if (resourcePath != null)
    {
      return new Path(resourcePath).lastSegment();
    }

    return getView().getSession().getRepositoryInfo().getName();
  }

  @Override
  public IPersistableElement getPersistable()
  {
    return null;
  }

  @Override
  public String getToolTipText()
  {
    CDOView view = getView();
    String resourcePath = getResourcePath();
    return formatToolTipText(view, resourcePath);
  }

  @Override
  public int hashCode()
  {
    return ObjectUtil.hashCode(getView()) ^ ObjectUtil.hashCode(resourcePath) ^ ObjectUtil.hashCode(objectID);
  }

  @Override
  public boolean equals(Object obj)
  {
    if (obj == this)
    {
      return true;
    }

    if (obj instanceof AbstractCDOEditorInput)
    {
      AbstractCDOEditorInput that = (AbstractCDOEditorInput)obj;

      return ObjectUtil.equals(getView(), that.getView()) //
          && ObjectUtil.equals(resourcePath, that.resourcePath) //
          && ObjectUtil.equals(objectID, that.objectID);
    }

    return false;
  }

  public static String formatToolTipText(CDOView view, String resourcePath)
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
}

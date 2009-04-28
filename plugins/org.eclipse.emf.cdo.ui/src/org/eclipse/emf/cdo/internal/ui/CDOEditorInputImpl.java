/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
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

import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.ui.CDOEditorInput;
import org.eclipse.emf.cdo.ui.messages.Messages;
import org.eclipse.emf.cdo.view.CDOAudit;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.ObjectUtil;

import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 * @since 2.0
 */
public class CDOEditorInputImpl extends PlatformObject implements CDOEditorInput
{
  private CDOView view;

  private boolean viewOwned;

  private String resourcePath;

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

  public boolean exists()
  {
    return true;
  }

  public ImageDescriptor getImageDescriptor()
  {
    switch (view.getViewType())
    {
    case TRANSACTION:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR);

    case READONLY:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR_READONLY);

    case AUDIT:
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR_HISTORICAL);

    default:
      return null;
    }
  }

  public String getName()
  {
    if (resourcePath != null)
    {
      return new Path(resourcePath).lastSegment();
    }

    return view.getSession().repository().getName();
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
    String repositoryName = session.repository().getName();

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

    if (view.getViewType() != CDOView.Type.TRANSACTION)
    {
      builder.append(" readonly"); //$NON-NLS-1$
    }

    if (view instanceof CDOAudit)
    {
      builder.append(MessageFormat.format(" {0,date} {0,time}", ((CDOAudit)view).getTimeStamp())); //$NON-NLS-1$
    }

    return builder.toString();
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

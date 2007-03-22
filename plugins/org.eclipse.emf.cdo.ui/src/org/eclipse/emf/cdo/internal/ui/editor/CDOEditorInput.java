/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui.editor;

import org.eclipse.emf.cdo.internal.ui.bundle.SharedIcons;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import java.text.MessageFormat;

/**
 * @author Eike Stepper
 */
public class CDOEditorInput implements IEditorInput
{
  private CDOSession session;

  private String resourcePath;

  private boolean readOnly;

  private long timeStamp;

  public CDOEditorInput(CDOSession session, String resourcePath)
  {
    this.session = session;
    this.resourcePath = resourcePath;
  }

  public CDOEditorInput(CDOSession session, String resourcePath, boolean readOnly)
  {
    this(session, resourcePath);
    this.readOnly = readOnly;
  }

  public CDOEditorInput(CDOSession session, String resourcePath, long timeStamp)
  {
    this(session, resourcePath);
    this.timeStamp = timeStamp;
  }

  public CDOSession getSession()
  {
    return session;
  }

  public String getResourcePath()
  {
    return resourcePath;
  }

  public boolean isActual()
  {
    return !isHistorical() && !isReadOnly();
  }

  public boolean isHistorical()
  {
    return timeStamp != 0;
  }

  public boolean isReadOnly()
  {
    return readOnly;
  }

  public long getTimeStamp()
  {
    return timeStamp;
  }

  public boolean exists()
  {
    return true;
  }

  public ImageDescriptor getImageDescriptor()
  {
    if (isHistorical())
    {
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR_HISTORICAL);
    }

    if (isReadOnly())
    {
      return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR_READONLY);
    }

    return SharedIcons.getDescriptor(SharedIcons.OBJ_EDITOR);
  }

  public String getName()
  {
    return resourcePath;
  }

  public IPersistableElement getPersistable()
  {
    return null;
  }

  public String getToolTipText()
  {
    if (isHistorical())
    {
      return MessageFormat.format("{0} ({1,D})", resourcePath, timeStamp);
    }

    if (isReadOnly())
    {
      return MessageFormat.format("{0} (read only)", resourcePath);
    }

    return resourcePath;
  }

  public Object getAdapter(Class adapter)
  {
    return null;
  }
}

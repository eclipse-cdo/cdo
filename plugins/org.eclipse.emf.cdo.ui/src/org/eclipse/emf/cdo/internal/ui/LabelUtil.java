/***************************************************************************
 * Copyright (c) 2004 - 2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.internal.ui;

import org.eclipse.emf.cdo.CDOSession;
import org.eclipse.emf.cdo.CDOView;
import org.eclipse.emf.cdo.internal.ui.bundle.SharedIcons;

import org.eclipse.net4j.IConnector;

import org.eclipse.swt.graphics.Image;

import java.util.Date;

/**
 * @author Eike Stepper
 */
public final class LabelUtil
{
  private LabelUtil()
  {
  }

  public static String getText(Object obj)
  {
    if (obj instanceof CDOSession)
    {
      CDOSession session = (CDOSession)obj;
      IConnector connector = session.getChannel().getConnector();
      String repositoryName = session.getRepositoryName();
      return connector.getURL() + "/" + repositoryName;
    }

    if (obj instanceof CDOView)
    {
      CDOView view = (CDOView)obj;
      return view.isHistorical() ? new Date(view.getTimeStamp()).toString() : view.isReadOnly() ? "View"
          : "Transaction";
    }

    return null;
  }

  public static Image getImage(Object obj)
  {
    if (obj instanceof CDOSession)
    {
      return SharedIcons.getImage(SharedIcons.OBJ_SESSION);
    }

    if (obj instanceof CDOView)
    {
      CDOView view = (CDOView)obj;
      if (view.isHistorical())
      {
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_HISTORICAL);
      }

      if (view.isReadOnly())
      {
        return SharedIcons.getImage(SharedIcons.OBJ_EDITOR_READONLY);
      }

      return SharedIcons.getImage(SharedIcons.OBJ_EDITOR);
    }

    return null;
  }
}

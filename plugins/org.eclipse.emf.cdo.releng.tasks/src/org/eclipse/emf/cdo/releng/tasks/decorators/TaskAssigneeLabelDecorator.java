/*
 * Copyright (c) 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.releng.tasks.decorators;

import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.graphics.Image;

public class TaskAssigneeLabelDecorator extends BaseLabelProvider implements ILabelDecorator
{
  public Image decorateImage(Image image, Object element)
  {
    return null;
  }

  public String decorateText(String text, Object element)
  {
    if (element instanceof ITask)
    {
      ITask task = (ITask)element;
      String owner = task.getOwner();
      if (owner != null)
      {
        int at = owner.indexOf('@');
        if (at != -1)
        {
          owner = owner.substring(0, at);
        }

        if (owner.endsWith("-inbox"))
        {
          owner = "INBOX";
        }

        return text + "    [" + owner + "]";
      }
    }

    return null;
  }
}

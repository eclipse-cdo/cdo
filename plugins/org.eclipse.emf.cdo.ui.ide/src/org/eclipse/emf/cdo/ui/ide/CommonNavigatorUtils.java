/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Victor Roldan Betancort - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.ui.ide;

import org.eclipse.emf.cdo.ui.internal.ide.bundle.OM;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author Victor Roldan Betancort
 */
public class CommonNavigatorUtils
{
  public static Object createMessageProvider(final String message, final MessageType type)
  {
    return new IAdaptable()
    {
      @SuppressWarnings("rawtypes")
      public Object getAdapter(Class adapter)
      {
        if (adapter.equals(ILabelProvider.class))
        {
          return new LabelProvider()
          {
            @Override
            public String getText(Object element)
            {
              return message;
            }

            @Override
            public Image getImage(Object element)
            {
              switch (type)
              {
              case ERROR:
                return OM.getImageDescriptor(OM.ERROR_ICON).createImage();
              case WARNING:
                return OM.getImageDescriptor(OM.WARNING_ICON).createImage();
              case INFO:
                return OM.getImageDescriptor(OM.INFO_ICON).createImage();
              }

              return super.getImage(element);
            }
          };
        }

        return null;
      }

      /*
       * In case is not adapted, at least the message is shown through toString()
       */
      @Override
      public String toString()
      {
        return message;
      }
    };
  }

  public static Object[] createMessageProviderChild(final String message, final MessageType type)
  {
    Object[] result = { createMessageProvider(message, type) };
    return result;
  }

  /**
   * @author Victor Roldan Betancort
   */
  public static enum MessageType
  {
    ERROR, WARNING, INFO
  }
}

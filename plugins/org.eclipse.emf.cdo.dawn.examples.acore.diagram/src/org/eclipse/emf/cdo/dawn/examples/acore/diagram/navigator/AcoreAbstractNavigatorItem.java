/*
 * Copyright (c) 2010, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;

/**
 * @generated
 */
public abstract class AcoreAbstractNavigatorItem extends PlatformObject
{

  /**
   * @generated
   */
  static
  {
    final Class[] supportedTypes = new Class[] { ITabbedPropertySheetPageContributor.class };
    final ITabbedPropertySheetPageContributor propertySheetPageContributor = new ITabbedPropertySheetPageContributor()
    {
      public String getContributorId()
      {
        return "org.eclipse.emf.cdo.dawn.examples.acore.diagram"; //$NON-NLS-1$
      }
    };
    Platform.getAdapterManager().registerAdapters(new IAdapterFactory()
    {

      public Object getAdapter(Object adaptableObject, Class adapterType)
      {
        if (adaptableObject instanceof org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreAbstractNavigatorItem
            && adapterType == ITabbedPropertySheetPageContributor.class)
        {
          return propertySheetPageContributor;
        }
        return null;
      }

      public Class[] getAdapterList()
      {
        return supportedTypes;
      }
    }, org.eclipse.emf.cdo.dawn.examples.acore.diagram.navigator.AcoreAbstractNavigatorItem.class);
  }

  /**
   * @generated
   */
  private Object myParent;

  /**
   * @generated
   */
  protected AcoreAbstractNavigatorItem(Object parent)
  {
    myParent = parent;
  }

  /**
   * @generated
   */
  public Object getParent()
  {
    return myParent;
  }

}

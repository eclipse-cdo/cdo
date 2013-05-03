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

import org.eclipse.emf.cdo.dawn.examples.acore.diagram.part.AcoreVisualIDRegistry;

import org.eclipse.jface.viewers.ViewerSorter;

/**
 * @generated
 */
public class AcoreNavigatorSorter extends ViewerSorter
{

  /**
   * @generated
   */
  private static final int GROUP_CATEGORY = 7006;

  /**
   * @generated
   */
  public int category(Object element)
  {
    if (element instanceof AcoreNavigatorItem)
    {
      AcoreNavigatorItem item = (AcoreNavigatorItem)element;
      return AcoreVisualIDRegistry.getVisualID(item.getView());
    }
    return GROUP_CATEGORY;
  }

}

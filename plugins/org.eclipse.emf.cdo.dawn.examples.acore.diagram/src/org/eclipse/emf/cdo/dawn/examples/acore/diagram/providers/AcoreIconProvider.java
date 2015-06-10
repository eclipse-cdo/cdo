/*
 * Copyright (c) 2010, 2012, 2015 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 *
 */
package org.eclipse.emf.cdo.dawn.examples.acore.diagram.providers;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.gmf.runtime.common.core.service.AbstractProvider;
import org.eclipse.gmf.runtime.common.core.service.IOperation;
import org.eclipse.gmf.runtime.common.ui.services.icon.GetIconOperation;
import org.eclipse.gmf.runtime.common.ui.services.icon.IIconProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @generated
 */
public class AcoreIconProvider extends AbstractProvider implements IIconProvider
{

  /**
   * @generated
   */
  public Image getIcon(IAdaptable hint, int flags)
  {
    return AcoreElementTypes.getImage(hint);
  }

  /**
   * @generated
   */
  public boolean provides(IOperation operation)
  {
    if (operation instanceof GetIconOperation)
    {
      return ((GetIconOperation)operation).execute(this) != null;
    }
    return false;
  }
}

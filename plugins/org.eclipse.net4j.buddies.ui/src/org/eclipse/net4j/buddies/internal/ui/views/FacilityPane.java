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
package org.eclipse.net4j.buddies.internal.ui.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

/**
 * @author Eike Stepper
 */
public abstract class FacilityPane extends Composite
{
  public FacilityPane(Composite parent, int style)
  {
    super(parent, style);
  }

  public void hidden(FacilityPane newPane)
  {
  }

  public void showed(FacilityPane oldPane)
  {
  }

  protected abstract Control createUI(Composite parent);
}

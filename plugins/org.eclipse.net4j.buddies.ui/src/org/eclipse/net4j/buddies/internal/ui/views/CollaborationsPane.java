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

import org.eclipse.net4j.buddies.BuddiesUtil;
import org.eclipse.net4j.util.ui.UIUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

/**
 * @author Eike Stepper
 */
public class CollaborationsPane extends Composite
{
  private CollaborationsView viewPart;

  public CollaborationsPane(CollaborationsView viewPart, Composite parent)
  {
    super(parent, SWT.NONE);
    this.viewPart = viewPart;
    setLayout(UIUtil.createGridLayout(1));

    List list = new List(this, SWT.NONE);
    list.setLayoutData(UIUtil.createGridData());
    for (String facilityType : BuddiesUtil.getFacilityTypes())
    {
      list.add(facilityType);
    }
  }

  public CollaborationsView getViewPart()
  {
    return viewPart;
  }

}

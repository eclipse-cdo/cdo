/*
 * Copyright (c) 2011, 2012, 2015 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.appearance;

import org.eclipse.emf.cdo.dawn.ui.stylizer.DawnDefaultElementStylizer;

import org.eclipse.gef.EditPart;

/**
 * An EditPartStylizer can influence the visual representation of the models state. Dawn knows three states - default,
 * conflicted and locked. By implementing an own DawnStylizer you can influence the appearance of the three states for
 * your EditPart and it's related models. New stylizer can be registered to Dawn using the
 * <b>org.eclipse.emf.cdo.dawn.editpartstylizers</b> extension point.
 *
 * @author Martin Fluegge
 * @since 2.0
 */
public abstract class DawnEditPartStylizer extends DawnDefaultElementStylizer
{
  /**
   * @since 2.0
   */
  public abstract void setDefault(EditPart editPart);

  /**
   * @since 2.0
   */
  public abstract void setConflicted(EditPart editPart, int type);

  /**
   * @since 2.0
   */
  public abstract void setLocked(EditPart editPart, int type);

  @Override
  public void setDefault(Object element)
  {
    setDefault((EditPart)element);
  }

  @Override
  public void setConflicted(Object element, int type)
  {
    setConflicted((EditPart)element, type);
  }

  @Override
  public void setLocked(Object element, int type)
  {
    setLocked((EditPart)element, type);
  }
}

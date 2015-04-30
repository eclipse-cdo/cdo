/*
 * Copyright (c) 2010-2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.explorer;

import org.eclipse.net4j.util.container.IContainer;
import org.eclipse.net4j.util.event.IEvent;

import org.eclipse.core.runtime.IAdaptable;

import java.util.Collection;

/**
 * @author Eike Stepper
 * @since 4.4
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface CDOExplorerManager<T extends CDOExplorerElement> extends IContainer<T>, IAdaptable
{
  /**
   * @author Eike Stepper
   */
  public interface ElementsChangedEvent extends IEvent
  {
    public CDOExplorerManager<?> getSource();

    public ElementsChangedEvent.StructuralImpact getStructuralImpact();

    public Collection<Object> getChangedElements();

    /**
     * @author Eike Stepper
     */
    public static enum StructuralImpact
    {
      NONE, ELEMENT, PARENT
    }
  }
}

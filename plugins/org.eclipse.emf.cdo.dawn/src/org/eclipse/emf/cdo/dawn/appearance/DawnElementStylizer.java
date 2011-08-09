/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.appearance;

/**
 * This interface is used do influence the appearance of an UI element by a certain state. Implementations allow to
 * change the appearance for the adapted types of editors.
 * 
 * @author Martin Fluegge
 * @since 2.0
 */
public interface DawnElementStylizer<T>
{
  public void setDefault(T element);

  public void setConflicted(T element, int type);

  public void setLocked(T element, int type);
}

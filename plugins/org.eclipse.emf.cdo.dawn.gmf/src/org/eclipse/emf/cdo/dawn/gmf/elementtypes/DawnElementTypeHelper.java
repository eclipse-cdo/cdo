/*
 * Copyright (c) 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Martin Fluegge - initial API and implementation
 */
package org.eclipse.emf.cdo.dawn.gmf.elementtypes;

/**
 * 
 * @author Martin Fluegge
 *  @since 2.0
 */
import org.eclipse.gmf.runtime.emf.type.core.IElementType;
import org.eclipse.gmf.runtime.notation.View;

/**
 * @since 2.0
 */
public interface DawnElementTypeHelper
{
  /**
   * @since 2.0
   */
  IElementType getElementType(View view);
}

/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.net4j.ui.wizards;

import org.eclipse.net4j.ui.StaticContentProvider;

import org.eclipse.jface.viewers.ILabelProvider;

import java.util.Collection;

/**
 * @author Eike Stepper
 */
public class StaticSelectionStep extends SelectionStep
{
  private static final Object INPUT = new Object();

  /*
   * Input: Object[] elements
   */

  public StaticSelectionStep(String label, String key, Object[] elements, ILabelProvider labelProvider, int min, int max)
  {
    super(label, key, Object.class, INPUT, new StaticContentProvider(elements), labelProvider, min, max);
  }

  public StaticSelectionStep(String key, Object[] elements, ILabelProvider labelProvider, int min, int max)
  {
    this(key, key, elements, labelProvider, min, max);
  }

  public StaticSelectionStep(String key, Object[] elements, int min, int max)
  {
    this(key, key, elements, null, min, max);
  }

  /*
   * Input: Collection elements
   */

  public StaticSelectionStep(String label, String key, Collection elements, ILabelProvider labelProvider, int min,
      int max)
  {
    super(label, key, Object.class, INPUT, new StaticContentProvider(elements), labelProvider, min, max);
  }

  public StaticSelectionStep(String key, Collection elements, ILabelProvider labelProvider, int min, int max)
  {
    this(key, key, elements, labelProvider, min, max);
  }

  public StaticSelectionStep(String key, Collection elements, int min, int max)
  {
    this(key, key, elements, null, min, max);
  }

  /*
   * Input: Class enumClass
   */

  public StaticSelectionStep(String label, String key, Class enumClass, ILabelProvider labelProvider, int min, int max)
  {
    super(label, key, Object.class, INPUT, new StaticContentProvider(enumClass), labelProvider, min, max);
  }

  public StaticSelectionStep(String key, Class enumClass, ILabelProvider labelProvider, int min, int max)
  {
    this(key, key, enumClass, labelProvider, min, max);
  }

  public StaticSelectionStep(String key, Class enumClass, int min, int max)
  {
    this(key, key, enumClass, null, min, max);
  }
}

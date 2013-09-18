/*
 * Copyright (c) 2004-2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.expressions.impl;

import org.eclipse.emf.cdo.expressions.EvaluationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Eike Stepper
 */
public class EvaluationContextImpl extends HashMap<String, Object> implements EvaluationContext
{
  private static final long serialVersionUID = 1L;

  public EvaluationContextImpl()
  {
  }

  public EvaluationContextImpl(int initialCapacity, float loadFactor)
  {
    super(initialCapacity, loadFactor);
  }

  public EvaluationContextImpl(int initialCapacity)
  {
    super(initialCapacity);
  }

  public EvaluationContextImpl(Map<? extends String, ? extends Object> m)
  {
    super(m);
  }

  public EvaluationContextImpl(Object thisValue)
  {
    put(THIS, thisValue);
  }

  public Object getThis()
  {
    return get(THIS);
  }
}

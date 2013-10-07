/*
 * Copyright (c) 2013 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.util;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;

/**
 * A data-binding converter that converts an object (a reference or {@code null})
 * to a boolean indicating the presence of the object ({@code true} for a present
 * object, {@code false} for a {@code null}).
 * 
 * @author Christian W. Damus (CEA LIST)
 */
public class ObjectExistsConverter extends Converter
{

  public ObjectExistsConverter()
  {
    super(Object.class, Boolean.class);
  }

  public Object convert(Object fromObject)
  {
    return doConvert(fromObject);
  }

  protected boolean doConvert(Object fromObject)
  {
    return fromObject != null;
  }

  //
  // Nested types
  //

  // public static UpdateValueStrategy createUpdateValueStrategy()
  // {
  // UpdateValueStrategy result = new UpdateValueStrategy();
  // result.setConverter(new ObjectExistsConverter());
  // return result;
  // }

  public static class ObjectWritableConverter extends ObjectExistsConverter
  {
    @Override
    protected boolean doConvert(Object fromObject)
    {
      return super.doConvert(fromObject) && SecurityModelUtil.isEditable(fromObject);
    }

    public static UpdateValueStrategy createUpdateValueStrategy()
    {
      UpdateValueStrategy result = new UpdateValueStrategy();
      result.setConverter(new ObjectWritableConverter());
      return result;
    }
  }
}

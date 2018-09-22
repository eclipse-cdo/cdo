/*
 * Copyright (c) 2013 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.security.internal.ui.util;

import org.eclipse.emf.ecore.EStructuralFeature;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.conversion.Converter;

/**
 * A data-binding converter that converts an object (a reference or {@code null})
 * to a boolean indicating the presence of the object ({@code true} for a present
 * object, {@code false} for a {@code null}).
 *
 * @author Christian W. Damus (CEA LIST)
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
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

  public static UpdateValueStrategy createUpdateValueStrategy()
  {
    UpdateValueStrategy result = new UpdateValueStrategy();
    result.setConverter(new ObjectExistsConverter());
    return result;
  }

  /**
   * @author Christian W. Damus (CEA LIST)
   */
  public static class ObjectWritableConverter extends ObjectExistsConverter
  {
    private EStructuralFeature feature;

    public ObjectWritableConverter(EStructuralFeature feature)
    {
      this.feature = feature;
    }

    public ObjectWritableConverter()
    {
      this(null);
    }

    @Override
    protected boolean doConvert(Object fromObject)
    {
      return super.doConvert(fromObject) && SecurityUIUtil.isEditable(fromObject) && isFeatureWritable(fromObject);
    }

    protected boolean isFeatureWritable(Object fromObject)
    {
      return feature == null || feature.isChangeable() && feature.getEContainingClass().isInstance(fromObject);
    }

    public static UpdateValueStrategy createUpdateValueStrategy()
    {
      return createUpdateValueStrategy(null);
    }

    public static UpdateValueStrategy createUpdateValueStrategy(EStructuralFeature feature)
    {
      UpdateValueStrategy result = new UpdateValueStrategy();
      result.setConverter(new ObjectWritableConverter(feature));
      return result;
    }
  }
}

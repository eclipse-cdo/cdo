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

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.ui.provider.AdapterFactoryLabelProvider;

import org.eclipse.swt.graphics.Image;

/**
 * An {@link AdapterFactory}-based table label provider that defaults
 * {@linkplain #defaultColumnIndex() some column}'s labels to the
 * object's {@link IItemLabelProvider}.
 *
 * @author Christian W. Damus (CEA LIST)
 */
public class TableLabelProvider extends AdapterFactoryLabelProvider
{
  private int defaultColumnIndex;

  public TableLabelProvider(AdapterFactory adapterFactory)
  {
    this(adapterFactory, 0);
  }

  public TableLabelProvider(AdapterFactory adapterFactory, int defaultColumnIndex)
  {
    super(adapterFactory);
    this.defaultColumnIndex = defaultColumnIndex;
  }

  protected final int defaultColumnIndex()
  {
    return 0;
  }

  @Override
  public String getColumnText(Object object, int columnIndex)
  {
    return columnIndex == defaultColumnIndex ? getText(object) : super.getColumnText(object, columnIndex);
  }

  @Override
  public Image getColumnImage(Object object, int columnIndex)
  {
    return columnIndex == defaultColumnIndex ? getImage(object) : super.getColumnImage(object, columnIndex);
  }
}

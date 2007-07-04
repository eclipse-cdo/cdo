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
package org.eclipse.emf.cdo.internal.ui.editor;

import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.edit.domain.AdapterFactoryEditingDomain;

/**
 * @author Eike Stepper
 */
public class Test
{

  public static void main(String[] args)
  {
    AdapterFactoryEditingDomain editingDomain = new AdapterFactoryEditingDomain(null, null)
    {
      @Override
      public AdapterFactory getAdapterFactory()
      {

        URI uri = CDOUtil.createURI("/path");
        System.out.println(uri.isRelative() ? "relative" : "absolute");
        System.out.println(isReadOnlyURI(uri) ? "readonly" : "writable");
        return null;
      }
    };

    editingDomain.getAdapterFactory();
  }

}

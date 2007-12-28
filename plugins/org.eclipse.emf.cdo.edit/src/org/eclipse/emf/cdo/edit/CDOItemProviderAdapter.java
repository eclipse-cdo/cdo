/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.cdo.edit;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.edit.provider.ItemProviderAdapter;

import java.util.Collection;
import java.util.List;

/**
 * @author Eike Stepper
 */
public class CDOItemProviderAdapter extends ItemProviderAdapter
{
  public CDOItemProviderAdapter(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  @Override
  @SuppressWarnings("deprecation")
  public boolean hasChildren(Object object)
  {
    Collection<? extends EStructuralFeature> anyChildrenFeatures = getChildrenFeatures(object);
    if (anyChildrenFeatures.isEmpty())
    {
      anyChildrenFeatures = getChildrenReferences(object);
    }

    EObject eObject = (EObject)object;
    for (EStructuralFeature feature : anyChildrenFeatures)
    {
      if (feature.isMany())
      {
        List<?> children = (List<?>)eObject.eGet(feature);
        if (!children.isEmpty())
        {
          return true;
        }
      }
      else
      {
        if (eObject.eIsSet(feature))
        {
          return true;
        }
      }
    }

    return false;
  }
}

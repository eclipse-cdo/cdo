/*
 * Copyright (c) 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ui.internal.compare;

import org.eclipse.emf.cdo.common.lob.CDOBlob;
import org.eclipse.emf.cdo.common.lob.CDOClob;
import org.eclipse.emf.cdo.common.lob.CDOLob;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.AttributeChange;
import org.eclipse.emf.compare.rcp.ui.contentmergeviewer.accessor.legacy.ITypedElement;
import org.eclipse.emf.compare.rcp.ui.internal.contentmergeviewer.IModelUpdateStrategy;
import org.eclipse.emf.compare.rcp.ui.internal.contentmergeviewer.IModelUpdateStrategyProvider;
import org.eclipse.emf.compare.rcp.ui.internal.contentmergeviewer.accessor.factory.impl.AbstractAccessorFactory;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;

/**
 * An {@link org.eclipse.emf.compare.rcp.ui.contentmergeviewer.accessor.factory.IAccessorFactory accessor factory} for
 * {@link CDOLob}-typed {@link AttributeChange attribute changes}.
 *
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class CDOLobAttributeChangeAccessorFactory extends AbstractAccessorFactory implements IModelUpdateStrategyProvider
{
  public CDOLobAttributeChangeAccessorFactory()
  {
  }

  @Override
  public boolean isFactoryFor(Object target)
  {
    if (target instanceof AttributeChange)
    {
      EAttribute attribute = ((AttributeChange)target).getAttribute();
      if (attribute.isMany())
      {
        return false;
      }

      Class<?> instanceClass = attribute.getEAttributeType().getInstanceClass();
      return instanceClass == CDOClob.class || instanceClass == CDOBlob.class;
    }

    return false;
  }

  @Override
  public ITypedElement createLeft(AdapterFactory adapterFactory, Object target)
  {
    AttributeChange attributeChange = (AttributeChange)target;
    EObject left = attributeChange.getMatch().getLeft();
    if (left != null)
    {
      return new CDOLobAttributeChangeAccessor(left, (AttributeChange)target);
    }

    return null;
  }

  @Override
  public ITypedElement createRight(AdapterFactory adapterFactory, Object target)
  {
    AttributeChange attributeChange = (AttributeChange)target;
    EObject right = attributeChange.getMatch().getRight();
    if (right != null)
    {
      return new CDOLobAttributeChangeAccessor(right, (AttributeChange)target);
    }

    return null;
  }

  @Override
  public ITypedElement createAncestor(AdapterFactory adapterFactory, Object target)
  {
    AttributeChange attributeChange = (AttributeChange)target;
    EObject ancestor = attributeChange.getMatch().getOrigin();
    if (ancestor != null)
    {
      return new CDOLobAttributeChangeAccessor(ancestor, (AttributeChange)target);
    }

    return null;
  }

  @Override
  public IModelUpdateStrategy getModelUpdateStrategy()
  {
    return new CDOLobAttributeModelUpdateStrategy();
  }
}

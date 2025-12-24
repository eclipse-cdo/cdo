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

import org.eclipse.emf.cdo.eresource.CDOFileResource;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.rcp.ui.contentmergeviewer.accessor.legacy.ITypedElement;
import org.eclipse.emf.compare.rcp.ui.internal.contentmergeviewer.accessor.factory.impl.MatchAccessorFactory;
import org.eclipse.emf.compare.rcp.ui.mergeviewer.IMergeViewer.MergeViewerSide;
import org.eclipse.emf.ecore.EObject;

import java.util.function.Function;

/**
 * @author Eike Stepper
 */
@SuppressWarnings("restriction")
public class CDOMatchAccessorFactory extends MatchAccessorFactory
{
  public CDOMatchAccessorFactory()
  {
  }

  @Override
  public ITypedElement createLeft(AdapterFactory adapterFactory, Object target)
  {
    CDOFileResource<?> file = getFile(target, Match::getLeft);
    if (file != null)
    {
      return new CDOMatchAccessor(adapterFactory, (Match)target, file, MergeViewerSide.LEFT);
    }

    return super.createLeft(adapterFactory, target);
  }

  @Override
  public ITypedElement createRight(AdapterFactory adapterFactory, Object target)
  {
    CDOFileResource<?> file = getFile(target, Match::getRight);
    if (file != null)
    {
      return new CDOMatchAccessor(adapterFactory, (Match)target, file, MergeViewerSide.RIGHT);
    }

    return super.createRight(adapterFactory, target);
  }

  @Override
  public ITypedElement createAncestor(AdapterFactory adapterFactory, Object target)
  {
    CDOFileResource<?> file = getFile(target, Match::getOrigin);
    if (file != null)
    {
      return new CDOMatchAccessor(adapterFactory, (Match)target, file, MergeViewerSide.ANCESTOR);
    }

    return super.createAncestor(adapterFactory, target);
  }

  private static CDOFileResource<?> getFile(Object target, Function<Match, EObject> matchSideFunction)
  {
    if (target instanceof Match)
    {
      Match match = (Match)target;

      EObject matchSide = matchSideFunction.apply(match);
      if (matchSide instanceof CDOFileResource<?>)
      {
        return (CDOFileResource<?>)matchSide;
      }
    }

    return null;
  }
}

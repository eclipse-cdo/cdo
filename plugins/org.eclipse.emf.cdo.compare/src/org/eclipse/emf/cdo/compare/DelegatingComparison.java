/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.compare;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.Conflict;
import org.eclipse.emf.compare.Diff;
import org.eclipse.emf.compare.EMFCompareConfiguration;
import org.eclipse.emf.compare.Equivalence;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.MatchResource;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import java.lang.reflect.InvocationTargetException;

/**
 * @author Eike Stepper
 */
public class DelegatingComparison implements Comparison
{
  protected Comparison delegate;

  public DelegatingComparison(Comparison delegate)
  {
    this.delegate = delegate;
  }

  public Comparison getDelegate()
  {
    return delegate;
  }

  public EList<Adapter> eAdapters()
  {
    return delegate.eAdapters();
  }

  public boolean eDeliver()
  {
    return delegate.eDeliver();
  }

  public void eSetDeliver(boolean deliver)
  {
    delegate.eSetDeliver(deliver);
  }

  public void eNotify(Notification notification)
  {
    delegate.eNotify(notification);
  }

  public EList<MatchResource> getMatchedResources()
  {
    return delegate.getMatchedResources();
  }

  public EList<Match> getMatches()
  {
    return delegate.getMatches();
  }

  public EClass eClass()
  {
    return delegate.eClass();
  }

  public EList<Conflict> getConflicts()
  {
    return delegate.getConflicts();
  }

  public Resource eResource()
  {
    return delegate.eResource();
  }

  public EList<Equivalence> getEquivalences()
  {
    return delegate.getEquivalences();
  }

  public EObject eContainer()
  {
    return delegate.eContainer();
  }

  public EMFCompareConfiguration getConfiguration()
  {
    return delegate.getConfiguration();
  }

  public EList<Diff> getDifferences()
  {
    return delegate.getDifferences();
  }

  public EStructuralFeature eContainingFeature()
  {
    return delegate.eContainingFeature();
  }

  public EList<Diff> getDifferences(EObject element)
  {
    return delegate.getDifferences(element);
  }

  public Match getMatch(EObject element)
  {
    return delegate.getMatch(element);
  }

  public EReference eContainmentFeature()
  {
    return delegate.eContainmentFeature();
  }

  public boolean isThreeWay()
  {
    return delegate.isThreeWay();
  }

  public void setThreeWay(boolean value)
  {
    delegate.setThreeWay(value);
  }

  public EList<EObject> eContents()
  {
    return delegate.eContents();
  }

  public TreeIterator<EObject> eAllContents()
  {
    return delegate.eAllContents();
  }

  public boolean eIsProxy()
  {
    return delegate.eIsProxy();
  }

  public EList<EObject> eCrossReferences()
  {
    return delegate.eCrossReferences();
  }

  public Object eGet(EStructuralFeature feature)
  {
    return delegate.eGet(feature);
  }

  public Object eGet(EStructuralFeature feature, boolean resolve)
  {
    return delegate.eGet(feature, resolve);
  }

  public void eSet(EStructuralFeature feature, Object newValue)
  {
    delegate.eSet(feature, newValue);
  }

  public boolean eIsSet(EStructuralFeature feature)
  {
    return delegate.eIsSet(feature);
  }

  public void eUnset(EStructuralFeature feature)
  {
    delegate.eUnset(feature);
  }

  public Object eInvoke(EOperation operation, EList<?> arguments) throws InvocationTargetException
  {
    return delegate.eInvoke(operation, arguments);
  }
}

/*
 * Copyright (c) 2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.ecore.dependencies.provider;

import org.eclipse.emf.cdo.ecore.dependencies.DependenciesPackage;
import org.eclipse.emf.cdo.ecore.dependencies.Link;
import org.eclipse.emf.cdo.ecore.dependencies.bundle.DependenciesPlugin;

import org.eclipse.net4j.util.om.pref.OMPreference;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public final class ModelItemProviderForLinkViewer extends ModelItemProvider
{
  private static final Set<EStructuralFeature> GENERIC_FEATURES = createGenerics();

  public ModelItemProviderForLinkViewer(AdapterFactory adapterFactory)
  {
    super(adapterFactory);
  }

  @Override
  protected void preferenceChanged(OMPreference<?> preference)
  {
    super.preferenceChanged(preference);

    if (preference == DependenciesPlugin.PREF_SHOW_BROKEN_LINKS || preference == DependenciesPlugin.PREF_SHOW_GENERICS)
    {
      resetChildren();
    }
  }

  @Override
  protected EReference getChildrenFeature()
  {
    if (DependenciesPlugin.PREF_SHOW_BROKEN_LINKS.getValue())
    {
      return DependenciesPackage.Literals.MODEL__BROKEN_LINKS;
    }

    return DependenciesPackage.Literals.MODEL__OUTGOING_LINKS;
  }

  @Override
  protected Object getValue(EObject eObject, EStructuralFeature eStructuralFeature)
  {
    Object value = super.getValue(eObject, eStructuralFeature);

    if (eStructuralFeature == getChildrenFeature())
    {
      @SuppressWarnings("unchecked")
      List<Link> links = new ArrayList<>((List<Link>)value);

      if (!DependenciesPlugin.PREF_SHOW_GENERICS.getValue())
      {
        for (Iterator<Link> it = links.iterator(); it.hasNext();)
        {
          Link link = it.next();
          if (link.isBroken())
          {
            continue;
          }

          String uri = link.getSource().getUri().toString();
          if (uri.indexOf("@eGeneric") != -1)
          {
            it.remove();
            continue;
          }

          EReference reference = link.getReference();
          if (GENERIC_FEATURES.contains(reference))
          {
            it.remove();
            continue;
          }
        }
      }

      links.sort(Link.ALPHABETICAL_COMPARATOR);
      return links;
    }

    return value;
  }

  private static Set<EStructuralFeature> createGenerics()
  {
    Set<EStructuralFeature> generics = new HashSet<>();
    generics.add(EcorePackage.Literals.ECLASSIFIER__ETYPE_PARAMETERS);
    generics.add(EcorePackage.Literals.ECLASS__EGENERIC_SUPER_TYPES);
    generics.add(EcorePackage.Literals.EOPERATION__ETYPE_PARAMETERS);
    generics.add(EcorePackage.Literals.EOPERATION__EGENERIC_EXCEPTIONS);
    generics.add(EcorePackage.Literals.ETYPED_ELEMENT__EGENERIC_TYPE);
    return generics;
  }
}

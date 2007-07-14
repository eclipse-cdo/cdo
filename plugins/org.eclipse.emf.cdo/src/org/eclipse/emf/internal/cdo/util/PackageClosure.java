/***************************************************************************
 * Copyright (c) 2004-2007 Eike Stepper, Germany.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 **************************************************************************/
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.internal.protocol.bundle.CDOProtocol;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.internal.cdo.bundle.CDO;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO Handle generic types and type parameters
 * 
 * @author Eike Stepper
 */
public final class PackageClosure
{
  private static final ContextTracer TRACER = new ContextTracer(CDOProtocol.DEBUG_MODEL, PackageClosure.class);

  private static final Set<EPackage> EMPTY_CLOSURE = Collections.emptySet();

  private PackageClosure()
  {
  }

  public static Set<EPackage> calculate(Collection<EPackage> ePackages)
  {
    // Optimize no packages
    if (ePackages.isEmpty())
    {
      return EMPTY_CLOSURE;
    }

    // Optimize 1 package
    if (ePackages.size() == 1)
    {
      return calculate(ePackages.iterator().next());
    }

    // Handle >1 packages
    Set<EPackage> result = new HashSet();
    for (EPackage ePackage : ePackages)
    {
      Set<EPackage> packages = calculate(ePackage);
      result.addAll(packages);
    }

    return result;
  }

  /**
   * TODO Handle Ecore generics
   */
  public static Set<EPackage> calculate(EPackage ePackage)
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Package closure for " + ePackage.getNsURI());
    }

    Set<EClass> visited = new HashSet();

    List<EClassifier> classifiers = ePackage.getEClassifiers();
    for (EClassifier classifier : classifiers)
    {
      handleEClassifier(classifier, visited);
    }

    Set<EPackage> result = new HashSet();
    for (EClass eClass : visited)
    {
      final EPackage p = eClass.getEPackage();
      if (p != null)
      {
        if (result.add(p))
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("Found package " + p.getNsURI());
          }
        }
      }
      else
      {
        String msg = "Package == null for " + eClass.getName();
        CDO.LOG.warn(msg);
        if (TRACER.isEnabled())
        {
          TRACER.trace(msg);
        }
      }
    }

    return result;
  }

  private static void handleEClassifier(EClassifier classifier, Set<EClass> visited)
  {
    if (classifier instanceof EClass)
    {
      EClass eClass = (EClass)classifier;
      if (visited.add(eClass))
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Found class " + eClass.getName());
        }

        handleEStructuralFeatures(eClass.getEStructuralFeatures(), visited);
        handleEOperations(eClass.getEOperations(), visited);
        handleESuperTypes(eClass.getESuperTypes(), visited);
      }
    }
  }

  private static void handleEStructuralFeatures(List<EStructuralFeature> structuralFeatures, Set<EClass> visited)
  {
    for (EStructuralFeature structuralFeature : structuralFeatures)
    {
      handleEClassifier(structuralFeature.getEType(), visited);
    }
  }

  private static void handleEOperations(List<EOperation> operations, Set<EClass> visited)
  {
    for (EOperation operation : operations)
    {
      handleEClassifier(operation.getEType(), visited);
      handleEParameters(operation.getEParameters(), visited);
      handleEExceptions(operation.getEExceptions(), visited);
    }
  }

  private static void handleEParameters(List<EParameter> parameters, Set<EClass> visited)
  {
    for (EParameter parameter : parameters)
    {
      handleEClassifier(parameter.getEType(), visited);
    }
  }

  private static void handleEExceptions(List<EClassifier> exceptions, Set<EClass> visited)
  {
    for (EClassifier exception : exceptions)
    {
      handleEClassifier(exception, visited);
    }
  }

  private static void handleESuperTypes(List<EClass> superTypes, Set<EClass> visited)
  {
    for (EClass superType : superTypes)
    {
      handleEClassifier(superType, visited);
    }
  }
}

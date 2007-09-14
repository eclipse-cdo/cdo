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
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.internal.cdo.bundle.OM;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * TODO Handle generic types and type parameters
 * 
 * @author Eike Stepper
 */
@Deprecated
public class TraversingPackageClosure extends PackageClosure
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_MODEL, TraversingPackageClosure.class);

  public TraversingPackageClosure()
  {
  }

  @Override
  protected void collectContents(EPackage ePackage, Set<EPackage> visited)
  {
    Set<EClass> classes = new HashSet<EClass>();
    List<EClassifier> classifiers = ePackage.getEClassifiers();
    for (EClassifier classifier : classifiers)
    {
      handleEClassifier(classifier, classes);
    }

    for (EClass eClass : classes)
    {
      final EPackage p = eClass.getEPackage();
      if (p != null)
      {
        if (visited.add(p))
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
        OM.LOG.warn(msg);
        if (TRACER.isEnabled())
        {
          TRACER.trace(msg);
        }
      }
    }
  }

  private void handleEClassifier(EClassifier classifier, Set<EClass> visited)
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

  private void handleEStructuralFeatures(List<EStructuralFeature> structuralFeatures, Set<EClass> visited)
  {
    for (EStructuralFeature structuralFeature : structuralFeatures)
    {
      handleEClassifier(structuralFeature.getEType(), visited);
    }
  }

  private void handleEOperations(List<EOperation> operations, Set<EClass> visited)
  {
    for (EOperation operation : operations)
    {
      handleEClassifier(operation.getEType(), visited);
      handleEParameters(operation.getEParameters(), visited);
      handleEExceptions(operation.getEExceptions(), visited);
    }
  }

  private void handleEParameters(List<EParameter> parameters, Set<EClass> visited)
  {
    for (EParameter parameter : parameters)
    {
      handleEClassifier(parameter.getEType(), visited);
    }
  }

  private void handleEExceptions(List<EClassifier> exceptions, Set<EClass> visited)
  {
    for (EClassifier exception : exceptions)
    {
      handleEClassifier(exception, visited);
    }
  }

  private void handleESuperTypes(List<EClass> superTypes, Set<EClass> visited)
  {
    for (EClass superType : superTypes)
    {
      handleEClassifier(superType, visited);
    }
  }
}

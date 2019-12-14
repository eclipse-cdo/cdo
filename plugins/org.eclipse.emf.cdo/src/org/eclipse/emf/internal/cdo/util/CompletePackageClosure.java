/*
 * Copyright (c) 2007-2009, 2011, 2012, 2016, 2018, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - bug 246442
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class CompletePackageClosure extends PackageClosure
{
  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG_MODEL, CompletePackageClosure.class);

  public CompletePackageClosure()
  {
  }

  @Override
  protected void handleEPackage(EPackage ePackage, Set<EPackage> visitedPackages)
  {
    if (ePackage != null)
    {
      if (visitedPackages.add(ePackage))
      {
        Set<Object> visited = new HashSet<>();
        for (EClassifier classifier : ePackage.getEClassifiers())
        {
          handleEClassifier(classifier, visitedPackages, visited);
        }

        for (Object object : visited)
        {
          if (object instanceof EClassifier)
          {
            EClassifier classifier = (EClassifier)object;
            final EPackage p = classifier.getEPackage();
            if (p != null)
            {
              if (visitedPackages.add(p))
              {
                if (TRACER.isEnabled())
                {
                  TRACER.trace("Found package " + p.getNsURI()); //$NON-NLS-1$
                }
              }
            }
            else
            {
              OM.LOG.warn(MessageFormat.format(Messages.getString("CompletePackageClosure.0"), classifier.getName())); //$NON-NLS-1$
            }
          }
        }
      }
    }
  }

  protected void handleEClassifier(EClassifier classifier, Set<EPackage> visitedPackages, Set<Object> visited)
  {
    if (classifier != null && visited.add(classifier))
    {
      handleEPackage(classifier.getEPackage(), visitedPackages);
      handleETypeParameters(classifier.getETypeParameters(), visitedPackages, visited);
      if (classifier instanceof EClass)
      {
        EClass eClass = (EClass)classifier;
        handleEStructuralFeatures(eClass.getEStructuralFeatures(), visitedPackages, visited);
        handleEOperations(eClass.getEOperations(), visitedPackages, visited);
        handleEGenericTypes(eClass.getEGenericSuperTypes(), visitedPackages, visited);
      }
    }
  }

  protected void handleEStructuralFeatures(List<EStructuralFeature> structuralFeatures, Set<EPackage> visitedPackages, Set<Object> visited)
  {
    if (structuralFeatures != null)
    {
      for (EStructuralFeature structuralFeature : structuralFeatures)
      {
        handleEGenericType(structuralFeature.getEGenericType(), visitedPackages, visited);
      }
    }
  }

  protected void handleEOperations(List<EOperation> operations, Set<EPackage> visitedPackages, Set<Object> visited)
  {
    if (operations != null)
    {
      for (EOperation operation : operations)
      {
        handleEGenericType(operation.getEGenericType(), visitedPackages, visited);
        handleETypeParameters(operation.getETypeParameters(), visitedPackages, visited);
        handleEParameters(operation.getEParameters(), visitedPackages, visited);
        handleEGenericTypes(operation.getEGenericExceptions(), visitedPackages, visited);
      }
    }
  }

  protected void handleEParameters(List<EParameter> parameters, Set<EPackage> visitedPackages, Set<Object> visited)
  {
    if (parameters != null)
    {
      for (EParameter parameter : parameters)
      {
        handleEClassifier(parameter.getEType(), visitedPackages, visited);
        handleEGenericType(parameter.getEGenericType(), visitedPackages, visited);
      }
    }
  }

  protected void handleEGenericTypes(EList<EGenericType> genericTypes, Set<EPackage> visitedPackages, Set<Object> visited)
  {
    if (genericTypes != null)
    {
      for (EGenericType genericType : genericTypes)
      {
        handleEGenericType(genericType, visitedPackages, visited);
      }
    }
  }

  protected void handleEGenericType(EGenericType genericType, Set<EPackage> visitedPackages, Set<Object> visited)
  {
    if (genericType != null && visited.add(genericType))
    {
      handleEClassifier(genericType.getEClassifier(), visitedPackages, visited);
      handleEClassifier(genericType.getERawType(), visitedPackages, visited);
      handleEGenericType(genericType.getELowerBound(), visitedPackages, visited);
      handleEGenericType(genericType.getEUpperBound(), visitedPackages, visited);
      handleEGenericTypes(genericType.getETypeArguments(), visitedPackages, visited);
      handleETypeParameter(genericType.getETypeParameter(), visitedPackages, visited);
    }
  }

  protected void handleETypeParameters(EList<ETypeParameter> typeParameters, Set<EPackage> visitedPackages, Set<Object> visited)
  {
    if (typeParameters != null)
    {
      for (ETypeParameter typeParameter : typeParameters)
      {
        handleETypeParameter(typeParameter, visitedPackages, visited);
      }
    }
  }

  protected void handleETypeParameter(ETypeParameter typeParameter, Set<EPackage> visitedPackages, Set<Object> visited)
  {
    if (typeParameter != null)
    {
      handleEGenericTypes(typeParameter.getEBounds(), visitedPackages, visited);
    }
  }
}

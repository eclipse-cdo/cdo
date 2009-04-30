/**
 * Copyright (c) 2004 - 2009 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Simon McDuff - http://bugs.eclipse.org/246442 
 */
package org.eclipse.emf.internal.cdo.util;

import org.eclipse.emf.cdo.messages.Messages;

import org.eclipse.emf.internal.cdo.bundle.OM;

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
import org.eclipse.emf.ecore.EcorePackage;

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

  private boolean excludeEcore;

  public CompletePackageClosure()
  {
  }

  public CompletePackageClosure(boolean excludeEcore)
  {
    this.excludeEcore = excludeEcore;
  }

  @Override
  protected void collectContents(EPackage ePackage, Set<EPackage> visited)
  {

    if (ePackage != null && visited.add(ePackage))
    {
      if (excludeEcore && // Optimize EPackage comparison
          (EcorePackage.eINSTANCE == ePackage || EcorePackage.eNS_URI.equals(ePackage.getNsURI())))
      {
        return;
      }

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
              TRACER.trace("Found package " + p.getNsURI()); //$NON-NLS-1$
            }
          }
        }
        else
        {
          OM.LOG.warn(MessageFormat.format(Messages.getString("CompletePackageClosure.0"), eClass.getName())); //$NON-NLS-1$
        }
      }
    }
  }

  protected void handleEClassifiers(List<EClassifier> classifiers, Set<EClass> visited)
  {
    if (classifiers != null)
    {
      for (EClassifier classifier : classifiers)
      {
        handleEClassifier(classifier, visited);
      }
    }
  }

  protected void handleEClassifier(EClassifier classifier, Set<EClass> visited)
  {
    if (classifier instanceof EClass)
    {
      handleEClass((EClass)classifier, visited);
    }
  }

  protected void handleEClasses(List<EClass> classes, Set<EClass> visited)
  {
    if (classes != null)
    {
      for (EClass eClass : classes)
      {
        handleEClass(eClass, visited);
      }
    }
  }

  protected void handleEClass(EClass eClass, Set<EClass> visited)
  {
    if (eClass != null)
    {
      if (visited.add(eClass))
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Found class " + eClass.getName()); //$NON-NLS-1$
        }

        handleEStructuralFeatures(eClass.getEStructuralFeatures(), visited);
        handleEOperations(eClass.getEOperations(), visited);
        handleEGenericTypes(eClass.getEGenericSuperTypes(), visited);
        handleETypeParameters(eClass.getETypeParameters(), visited);
      }
    }
  }

  protected void handleEStructuralFeatures(List<EStructuralFeature> structuralFeatures, Set<EClass> visited)
  {
    if (structuralFeatures != null)
    {
      for (EStructuralFeature structuralFeature : structuralFeatures)
      {
        handleEGenericType(structuralFeature.getEGenericType(), visited);
      }
    }
  }

  protected void handleEOperations(List<EOperation> operations, Set<EClass> visited)
  {
    if (operations != null)
    {
      for (EOperation operation : operations)
      {
        handleEGenericType(operation.getEGenericType(), visited);
        handleETypeParameters(operation.getETypeParameters(), visited);
        handleEParameters(operation.getEParameters(), visited);
        handleEGenericTypes(operation.getEGenericExceptions(), visited);
      }
    }
  }

  protected void handleEParameters(List<EParameter> parameters, Set<EClass> visited)
  {
    if (parameters != null)
    {
      for (EParameter parameter : parameters)
      {
        handleEClassifier(parameter.getEType(), visited);
        handleEGenericType(parameter.getEGenericType(), visited);
      }
    }
  }

  protected void handleEGenericTypes(EList<EGenericType> genericTypes, Set<EClass> visited)
  {
    if (genericTypes != null)
    {
      for (EGenericType genericType : genericTypes)
      {
        handleEGenericType(genericType, visited);
      }
    }
  }

  protected void handleEGenericType(EGenericType genericType, Set<EClass> visited)
  {
    if (genericType != null)
    {
      handleEClassifier(genericType.getEClassifier(), visited);
      handleEClassifier(genericType.getERawType(), visited);
      handleEGenericType(genericType.getELowerBound(), visited);
      handleEGenericType(genericType.getEUpperBound(), visited);
      handleEGenericTypes(genericType.getETypeArguments(), visited);
      handleETypeParameter(genericType.getETypeParameter(), visited);
    }
  }

  protected void handleETypeParameters(EList<ETypeParameter> typeParameters, Set<EClass> visited)
  {
    if (typeParameters != null)
    {
      for (ETypeParameter typeParameter : typeParameters)
      {
        handleETypeParameter(typeParameter, visited);
      }
    }
  }

  protected void handleETypeParameter(ETypeParameter typeParameter, Set<EClass> visited)
  {
    if (typeParameter != null)
    {
      handleEGenericTypes(typeParameter.getEBounds(), visited);
    }
  }
}

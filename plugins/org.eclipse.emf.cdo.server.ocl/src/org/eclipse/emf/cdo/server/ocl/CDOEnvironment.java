/*
 * Copyright (c) 2013, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Christian W. Damus (CEA LIST) - initial API and implementation
 */
package org.eclipse.emf.cdo.server.ocl;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import org.eclipse.ocl.Environment;
import org.eclipse.ocl.TypeChecker;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironment;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.utilities.TypedElement;

import java.util.List;

/**
 * CDO-specific Ecore environment implementation.  It adds operations that assist in building efficient OCL queries.
 *
 * @author Christian W. Damus
 * @since 4.2
 */
class CDOEnvironment extends EcoreEnvironment
{
  public CDOEnvironment(EcoreEnvironmentFactory fac, Resource resource)
  {
    super(fac, resource);
  }

  public CDOEnvironment(
      Environment<EPackage, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint, EClass, EObject> parent)
  {
    super(parent);
  }

  @Override
  protected TypeChecker<EClassifier, EOperation, EStructuralFeature> createTypeChecker()
  {
    TypeChecker<EClassifier, EOperation, EStructuralFeature> result = super.createTypeChecker();
    if (result instanceof TypeChecker.Cached<?, ?, ?>)
    {
      return new DelegatingCachedTypeChecker((TypeChecker.Cached<EClassifier, EOperation, EStructuralFeature>)result);
    }

    return new DelegatingTypeChecker(result);
  }

  /**
   * @author Christian W. Damus
   */
  private static class DelegatingTypeChecker implements TypeChecker<EClassifier, EOperation, EStructuralFeature>
  {
    private final TypeChecker<EClassifier, EOperation, EStructuralFeature> delegate;

    public DelegatingTypeChecker(TypeChecker<EClassifier, EOperation, EStructuralFeature> delegate)
    {
      this.delegate = delegate;
    }

    /**
     * Overrides the superclass implementation to resolve generic result types of our
     * CDO-specific operations according to their individual semantics.
     */
    @Override
    public EClassifier getResultType(Object problemObject, EClassifier owner, EOperation operation, List<? extends TypedElement<EClassifier>> args)
    {
      CDOAdditionalOperation cdoOperation = CDOAdditionalOperation.getInstance(operation);
      if (cdoOperation != null)
      {
        return cdoOperation.getResultType(owner, operation, args);
      }

      return delegate.getResultType(problemObject, owner, operation, args);
    }

    //
    // Strict delegation of API
    //

    @Override
    public int getRelationship(EClassifier type1, EClassifier type2)
    {
      return delegate.getRelationship(type1, type2);
    }

    @Override
    public EClassifier getPropertyType(EClassifier owner, EStructuralFeature property)
    {
      return delegate.getPropertyType(owner, property);
    }

    @Override
    public EClassifier commonSuperType(Object problemObject, EClassifier type1, EClassifier type2)
    {
      return delegate.commonSuperType(problemObject, type1, type2);
    }

    @Override
    public boolean checkMutuallyComparable(Object problemObject, EClassifier type1, EClassifier type2, int opcode)
    {
      return delegate.checkMutuallyComparable(problemObject, type1, type2, opcode);
    }

    @Override
    public boolean exactTypeMatch(EClassifier type1, EClassifier type2)
    {
      return delegate.exactTypeMatch(type1, type2);
    }

    @Override
    public boolean compatibleTypeMatch(EClassifier type1, EClassifier type2)
    {
      return delegate.compatibleTypeMatch(type1, type2);
    }

    @Override
    public List<EOperation> getOperations(EClassifier owner)
    {
      return delegate.getOperations(owner);
    }

    @Override
    public List<EStructuralFeature> getAttributes(EClassifier owner)
    {
      return delegate.getAttributes(owner);
    }

    @Override
    public EOperation resolveGenericSignature(EClassifier owner, EOperation oper)
    {
      return delegate.resolveGenericSignature(owner, oper);
    }

    @Override
    public EClassifier findSignalMatching(EClassifier receiver, List<EClassifier> signals, String name, List<? extends TypedElement<EClassifier>> args)
    {
      return delegate.findSignalMatching(receiver, signals, name, args);
    }

    @Override
    public EOperation findOperationMatching(EClassifier owner, String name, List<? extends TypedElement<EClassifier>> args)
    {
      return delegate.findOperationMatching(owner, name, args);
    }

    @Override
    public boolean matchArgs(EClassifier owner, List<?> paramsOrProperties, List<? extends TypedElement<EClassifier>> args)
    {
      return delegate.matchArgs(owner, paramsOrProperties, args);
    }

    @Override
    public EStructuralFeature findAttribute(EClassifier owner, String name)
    {
      return delegate.findAttribute(owner, name);
    }

    @Override
    public boolean isStandardLibraryFeature(EClassifier owner, Object feature)
    {
      return delegate.isStandardLibraryFeature(owner, feature);
    }
  }

  /**
   * @author Christian W. Damus
   */
  private static class DelegatingCachedTypeChecker extends DelegatingTypeChecker implements TypeChecker.Cached<EClassifier, EOperation, EStructuralFeature>
  {
    private final TypeChecker.Cached<EClassifier, EOperation, EStructuralFeature> delegate;

    public DelegatingCachedTypeChecker(TypeChecker.Cached<EClassifier, EOperation, EStructuralFeature> delegate)
    {
      super(delegate);
      this.delegate = delegate;
    }

    @Override
    public EOperation getDynamicOperation(EClassifier dynamicType, EOperation staticOperation)
    {
      return delegate.getDynamicOperation(dynamicType, staticOperation);
    }

    @Override
    public void reset()
    {
      delegate.reset();
    }

    @Override
    public void setBypass(boolean bypass)
    {
      delegate.setBypass(bypass);
    }
  }
}

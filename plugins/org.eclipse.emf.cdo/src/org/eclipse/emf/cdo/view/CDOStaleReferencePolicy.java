/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.view;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;

import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Specifies a policy on how to deal with stale references.
 * 
 * @author Simon McDuff
 * @since 3.0
 */
public interface CDOStaleReferencePolicy
{
  /**
   * A default stale reference policy. It will throw an exception each time.
   */
  public static final CDOStaleReferencePolicy EXCEPTION = new CDOStaleReferencePolicy()
  {
    public Object processStaleReference(EObject source, EStructuralFeature feature, int index, CDOID target)
    {
      throw new ObjectNotFoundException(target);
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOStaleReferencePolicy.0"); //$NON-NLS-1$
    }
  };

  /**
   * Returns a proxy object with the appropriate EClass. The proxy object supports the {@link InternalEObject#eClass()
   * eClass()} and {@link InternalEObject#eIsProxy() eIsProxy()} methods. For all invocations to other methods the proxy
   * object throws an {@link ObjectNotFoundException}. The receiver can use {@link CDOUtil#isStaleObject(Object)} to
   * detect proxy objects.
   */
  public static final CDOStaleReferencePolicy PROXY = new CDOStaleReferencePolicy()
  {
    public Object processStaleReference(EObject source, final EStructuralFeature feature, int index, final CDOID target)
    {
      final EClassifier type = feature.getEType();
      InvocationHandler handler = new InvocationHandler()
      {
        public Object invoke(Object arg0, Method arg1, Object[] arg2) throws Throwable
        {
          if (arg1.getName().equals("eIsProxy")) //$NON-NLS-1$
          {
            return false;
          }

          if (arg1.getName().equals("eClass")) //$NON-NLS-1$
          {
            return type;
          }

          throw new ObjectNotFoundException(target);
        }
      };

      Class<?> instanceClass = type.getInstanceClass();
      Class<?>[] interfaces = null;

      // Be sure to have only interface
      if (instanceClass.isInterface())
      {
        interfaces = new Class<?>[] { instanceClass, InternalEObject.class, CDOStaleObject.class };
      }
      else
      {
        interfaces = new Class<?>[] { InternalEObject.class, CDOStaleObject.class };
      }

      return Proxy.newProxyInstance(instanceClass.getClassLoader(), interfaces, handler);
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOStaleReferencePolicy.1"); //$NON-NLS-1$
    }
  };

  /**
   * Returns an object that we want to return to the caller (clients). Exception thrown will be received by the caller
   * (clients).
   */
  public Object processStaleReference(EObject source, EStructuralFeature feature, int index, CDOID target);
}

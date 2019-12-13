/*
 * Copyright (c) 2009-2012, 2015, 2016, 2019 Eike Stepper (Loehne, Germany) and others.
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

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.CDOState;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.util.ObjectNotFoundException;
import org.eclipse.emf.cdo.view.CDOView.Options;

import org.eclipse.emf.internal.cdo.bundle.OM;
import org.eclipse.emf.internal.cdo.messages.Messages;

import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.notify.impl.BasicNotifierImpl.EAdapterList;
import org.eclipse.emf.common.util.Logger;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;
import org.eclipse.emf.ecore.util.BasicInternalEList;
import org.eclipse.emf.ecore.util.EContentsEList;
import org.eclipse.emf.ecore.util.ECrossReferenceEList;
import org.eclipse.emf.spi.cdo.InternalCDOObject;
import org.eclipse.emf.spi.cdo.InternalCDOView;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Specifies a policy on how to deal with stale references.
 *
 * @author Simon McDuff
 * @since 3.0
 */
public interface CDOStaleReferencePolicy
{
  /**
   * A stale reference policy that throws an {@link ObjectNotFoundException} each time.
   */
  public static final CDOStaleReferencePolicy EXCEPTION = new CDOStaleReferencePolicy()
  {
    @Override
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
   * A stale reference policy that returns dynamic Java proxies with the appropriate EClasses.
   */
  public static final CDOStaleReferencePolicy PROXY = new DynamicProxy();

  /**
   * @since 4.2
   */
  public static final CDOStaleReferencePolicy DEFAULT = PROXY;

  /**
   * Returns an object that we want to return to the caller (clients). Exception thrown will be received by the caller
   * (clients).
   */
  public Object processStaleReference(EObject source, EStructuralFeature feature, int index, CDOID target);

  /**
   * @author Eike Stepper
   * @since 4.4
   */
  public static class DynamicProxy implements CDOStaleReferencePolicy
  {
    private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, CDOStaleReferencePolicy.class);

    @Override
    public Object processStaleReference(final EObject source, final EStructuralFeature feature, int index, final CDOID target)
    {
      final EClassifier type = getType(source, feature, index, target);
      InvocationHandler handler = new InvocationHandler()
      {
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
        {
          String name = method.getName();

          if (TRACER.isEnabled())
          {
            TRACER.trace("Proxy invocation: " + target + "." + name + (args == null ? "()" : Arrays.asList(args)));
          }

          if (name.equals("cdoID")) //$NON-NLS-1$
          {
            return target;
          }

          if (name.equals("eIsProxy")) //$NON-NLS-1$
          {
            return false;
          }

          if (name.equals("eClass")) //$NON-NLS-1$
          {
            return type;
          }

          if (name.equals("eAdapters")) //$NON-NLS-1$
          {
            return new EAdapterList<Adapter>((Notifier)proxy);
          }

          if (name.equals("eContainer")) //$NON-NLS-1$
          {
            return null;
          }

          if (name.equals("eResource")) //$NON-NLS-1$
          {
            return null;
          }

          if (name.equals("eContents")) //$NON-NLS-1$
          {
            return EContentsEList.emptyContentsEList();
          }

          if (name.equals("eCrossReferences")) //$NON-NLS-1$
          {
            return ECrossReferenceEList.emptyCrossReferenceEList();
          }

          if (name.equals("eInvoke")) //$NON-NLS-1$
          {
            return null;
          }

          if (name.equals("eGet") && args != null && args.length >= 1) //$NON-NLS-1$
          {
            EStructuralFeature featureParam = (EStructuralFeature)args[0];
            if (featureParam.isMany())
            {
              return new BasicInternalEList<Object>(Object.class);
            }

            return featureParam.getDefaultValue();
          }

          if (name.equals("eIsSet")) //$NON-NLS-1$
          {
            return false;
          }

          if (name.equals("eSet")) //$NON-NLS-1$
          {
            return null;
          }

          if (name.equals("eUnset")) //$NON-NLS-1$
          {
            return null;
          }

          if (name.equals("equals") && args != null && args.length == 1) //$NON-NLS-1$
          {
            return target.equals(args[0]);
          }

          if (name.equals("hashCode")) //$NON-NLS-1$
          {
            return target.hashCode();
          }

          if (name.equals("toString")) //$NON-NLS-1$
          {
            return "StaleReference[" + type.getName() + "@" + target + "]";
          }

          Class<?> returnType = method.getReturnType();
          if (returnType == null || returnType == void.class)
          {
            return null;
          }

          if (returnType.isPrimitive())
          {
            if (returnType == boolean.class)
            {
              return false;
            }

            if (returnType == char.class)
            {
              return (char)0;
            }

            if (returnType == byte.class)
            {
              return (byte)0;
            }

            if (returnType == short.class)
            {
              return (short)0;
            }

            if (returnType == int.class)
            {
              return (int)0;
            }

            if (returnType == long.class)
            {
              return (long)0;
            }

            if (returnType == float.class)
            {
              return (float)0;
            }

            if (returnType == double.class)
            {
              return (double)0;
            }
          }

          if (List.class.isAssignableFrom(returnType))
          {
            return new BasicInternalEList<Object>(Object.class);
          }

          return null;
        }
      };

      Class<?> instanceClass = type.getInstanceClass();
      Class<?>[] interfaces = null;

      // Be sure to have only interface
      if (instanceClass.isInterface())
      {
        interfaces = new Class<?>[] { InternalEObject.class, CDOStaleObject.class, instanceClass };
      }
      else
      {
        interfaces = new Class<?>[] { InternalEObject.class, CDOStaleObject.class };
      }

      try
      {
        return Proxy.newProxyInstance(instanceClass.getClassLoader(), interfaces, handler);
      }
      catch (IllegalArgumentException ex)
      {
        String message = ex.getMessage();
        if (message != null && message.contains("CDOStaleObject"))
        {
          // Interface org.eclipse.emf.cdo.view.CDOStaleObject is not visible from class loader.
          // Use org.eclipse.emf.common.util.Logger instead.
          interfaces[1] = Logger.class;
          return Proxy.newProxyInstance(instanceClass.getClassLoader(), interfaces, handler);
        }

        throw ex;
      }
    }

    protected EClassifier getType(EObject source, EStructuralFeature feature, int index, CDOID target)
    {
      return feature.getEType();
    }

    @Override
    public String toString()
    {
      return Messages.getString("CDOStaleReferencePolicy.1"); //$NON-NLS-1$
    }

    /**
     * @author Eike Stepper
     */
    public static class Enhanced extends DynamicProxy implements CDOObjectHandler
    {
      private final ConcurrentMap<CDOID, EClassifier> types = new ConcurrentHashMap<CDOID, EClassifier>();

      private final CDOView view;

      private final CDOStaleReferencePolicy oldPolicy;

      public Enhanced(CDOView view)
      {
        this.view = view;

        for (InternalCDOObject object : ((InternalCDOView)view).getObjectsList())
        {
          addType(object);
        }

        view.addObjectHandler(this);

        Options options = view.options();
        oldPolicy = options.getStaleReferencePolicy();
        options.setStaleReferencePolicy(this);
      }

      public void dispose()
      {
        Options options = view.options();
        options.setStaleReferencePolicy(oldPolicy);

        view.removeObjectHandler(this);
        types.clear();
      }

      @Override
      public void objectStateChanged(CDOView view, CDOObject object, CDOState oldState, CDOState newState)
      {
        addType(object);
      }

      @Override
      protected EClassifier getType(EObject source, EStructuralFeature feature, int index, CDOID target)
      {
        EClassifier type = types.get(target);
        if (type != null)
        {
          return type;
        }

        return super.getType(source, feature, index, target);
      }

      private void addType(CDOObject object)
      {
        CDOID id = object.cdoID();
        EClass type = object.eClass();
        if (id != null && type != null)
        {
          types.putIfAbsent(id, type);
        }
      }
    }
  }
}

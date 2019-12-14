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

import org.eclipse.emf.cdo.eresource.CDOResource;

import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;

import org.eclipse.ocl.ecore.TypeType;
import org.eclipse.ocl.expressions.CollectionKind;
import org.eclipse.ocl.utilities.TypedElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Additional operations for use in OCL queries in the CDO context.
 *
 * @author Christian W. Damus
 * @since 4.2
 */
abstract class CDOAdditionalOperation extends AdapterImpl
{
  private final CDOEnvironment env;

  private final String name;

  private CDOAdditionalOperation(CDOEnvironment env, String name)
  {
    this.env = env;
    this.name = name;
  }

  public final CDOEnvironment getEnv()
  {
    return env;
  }

  public final String getName()
  {
    return name;
  }

  /**
   * Creates the ECore operation(s) that I implement and registers them in my environment on the appropriate owner type.
   */
  protected abstract void register();

  /**
   * Evaluates me on the specified {@code source} object with the given (possibly empty) {@code arguments}.
   */
  protected abstract Object evaluate(CDOEvaluationEnvironment evalEnv, Object source, Object[] arguments);

  /**
   * Resolves the possibly generic return type of an operation according to its relation to the source ({@code owner}) type and argument types.
   */
  protected EClassifier getResultType(EClassifier owner, EOperation operation, List<? extends TypedElement<EClassifier>> args)
  {
    EClassifier result = operation.getEType();
    return result != null ? result : env.getOCLStandardLibrary().getOclVoid();
  }

  protected final EOperation createEOperation(EClassifier resultType)
  {
    EOperation result = EcoreFactory.eINSTANCE.createEOperation();

    result.setName(getName());
    result.setEType(resultType);
    result.eAdapters().add(this);

    return result;
  }

  protected final EOperation createEOperation(EClassifier resultType, String paramName, EClassifier paramType)
  {
    EOperation result = createEOperation(resultType);

    EParameter param = EcoreFactory.eINSTANCE.createEParameter();
    param.setName(paramName);
    param.setEType(paramType);
    result.getEParameters().add(param);

    return result;
  }

  protected final EClassifier collectionType(CollectionKind kind, EClassifier elementType)
  {
    return (EClassifier)env.getTypeResolver().resolveCollectionType(kind, elementType);
  }

  static CDOAdditionalOperation getInstance(EOperation operation)
  {
    CDOAdditionalOperation result = null;

    // As a hacky filter to avoid the cost of iterating adapters for the vast majority of operations that
    // are not one of ours, check for our common prefix
    if (operation.getName().startsWith("cdo")) //$NON-NLS-1$
    {
      for (Object next : operation.eAdapters())
      {
        if (next instanceof CDOAdditionalOperation)
        {
          result = (CDOAdditionalOperation)next;
          break;
        }
      }
    }

    return result;
  }

  static void registerOperations(CDOEnvironment env)
  {
    new AllProperContents(env).register();
    new MatchesAnyStringAttribute(env).register();
  }

  /**
   * The <tt>cdoAllContents</tt> operation that collects all of the proper (non-cross-resource-contained)
   * elements within a {@link CDOResource} or an {@link EObject}.  An optional argument filters the result
   * to instances of a particular model class.
   *
   *  @author Christian W. Damus
   */
  private static class AllProperContents extends CDOAdditionalOperation
  {
    private static final String NAME = "cdoAllProperContents"; //$NON-NLS-1$

    private AllProperContents(CDOEnvironment env)
    {
      super(env, NAME);
    }

    @Override
    protected void register()
    {
      CDOEnvironment env = getEnv();
      EClassifier oclAny = env.getOCLStandardLibrary().getOclAny();
      EClassifier oclT = env.getOCLStandardLibrary().getT();
      EClassifier resultType = collectionType(CollectionKind.COLLECTION_LITERAL, oclT);

      // One variant without a filter type argument
      env.addHelperOperation(oclAny, createEOperation(resultType));

      // And one with
      env.addHelperOperation(oclAny, createEOperation(resultType, "type", env.getOCLStandardLibrary().getOclType())); //$NON-NLS-1$
    }

    @Override
    protected Object evaluate(CDOEvaluationEnvironment evalEnv, Object source, Object[] arguments)
    {
      Collection<EObject> result = new ArrayList<>();

      // Only resources and EObjects have contents
      Iterator<EObject> iter;
      if (source instanceof Resource)
      {
        iter = EcoreUtil.getAllProperContents((Resource)source, false);
      }
      else if (source instanceof EObject)
      {
        iter = EcoreUtil.getAllProperContents((EObject)source, false);
      }
      else
      {
        iter = Collections.<EObject> emptyList().iterator();
      }

      if (arguments.length == 1)
      {
        // Get the type-filter argument
        EClassifier typeFilter = (EClassifier)arguments[0];
        if (typeFilter == null)
        {
          typeFilter = EcorePackage.Literals.EOBJECT;
        }

        while (iter.hasNext())
        {
          EObject next = iter.next();
          if (!next.eIsProxy() && typeFilter.isInstance(next)) // Because it could be a containment proxy
          {
            result.add(next);
          }
        }

      }
      else
      {
        while (iter.hasNext())
        {
          EObject next = iter.next();
          if (!next.eIsProxy()) // Because it could be a containment proxy
          {
            result.add(next);
          }
        }
      }

      return result;
    }

    @Override
    protected EClassifier getResultType(EClassifier owner, EOperation operation, List<? extends TypedElement<EClassifier>> args)
    {
      // The result type of the type-filtered variant is a collection of the filter type
      EClassifier elementType = args.isEmpty() ? getEnv().getOCLStandardLibrary().getOclAny() : ((TypeType)args.get(0).getType()).getReferredType();
      return collectionType(CollectionKind.COLLECTION_LITERAL, elementType);
    }
  }

  /**
   * The <tt>cdoMatches</tt> operation queries whether a regular expression matches aany string-valued
   * attribute of an {@link EObject}.
   *
   * @author Christian W. Damus
   */
  private static class MatchesAnyStringAttribute extends CDOAdditionalOperation
  {
    private static final String NAME = "cdoMatches"; //$NON-NLS-1$

    private static final int CACHE_SIZE = 16;

    private Map<String, Matcher> matcherCache;

    private Map<EClass, List<EAttribute>> stringAttributes;

    private MatchesAnyStringAttribute(CDOEnvironment env)
    {
      super(env, NAME);
    }

    @Override
    protected void register()
    {
      CDOEnvironment env = getEnv();
      EClassifier oclAny = env.getOCLStandardLibrary().getOclAny();
      env.addHelperOperation(oclAny, createEOperation(env.getOCLStandardLibrary().getBoolean(), "regex", env.getOCLStandardLibrary().getString())); //$NON-NLS-1$
    }

    @Override
    protected Object evaluate(CDOEvaluationEnvironment evalEnv, Object source, Object[] arguments)
    {
      boolean result = false;

      // Only EObjects have String-valued attributes (or attributes at all!)
      if (source instanceof EObject)
      {
        EObject object = (EObject)source;
        Matcher m = getMatcher((String)arguments[0]);

        // Check all string-valued attributes of this EClass
        for (EAttribute next : getStringAttributes(object.eClass()))
        {
          if (!next.isMany())
          {
            String value = (String)object.eGet(next);
            result = value != null && m.reset(value).matches();
          }
          else
          {
            @SuppressWarnings("unchecked")
            List<String> valueList = (List<String>)object.eGet(next);
            for (int i = 0; !result && i < valueList.size(); i++)
            {
              String value = valueList.get(i);
              result = value != null && m.reset(value).matches();
            }
          }

          if (result)
          {
            break;
          }
        }
      }

      return result;
    }

    private Matcher getMatcher(String regex)
    {
      if (matcherCache == null)
      {
        matcherCache = new java.util.LinkedHashMap<String, Matcher>(CACHE_SIZE, 0.75f, true)
        {
          private static final long serialVersionUID = 1L;

          @Override
          protected boolean removeEldestEntry(Map.Entry<String, Matcher> eldest)
          {
            return size() > CACHE_SIZE;
          }
        };
      }

      Matcher result = matcherCache.get(regex);
      if (result == null)
      {
        result = Pattern.compile(regex).matcher(""); //$NON-NLS-1$
        matcherCache.put(regex, result);
      }

      return result;
    }

    private List<EAttribute> getStringAttributes(EClass eClass)
    {
      if (stringAttributes == null)
      {
        stringAttributes = new java.util.HashMap<>();
      }

      List<EAttribute> result = stringAttributes.get(eClass);
      if (result == null)
      {
        for (EAttribute next : eClass.getEAllAttributes())
        {
          EDataType type = next.getEAttributeType();
          if (type != null && type.getInstanceClass() == String.class)
          {
            if (result == null)
            {
              result = new ArrayList<>();
            }
            result.add(next);
          }
        }

        if (result == null)
        {
          result = Collections.emptyList();
        }

        stringAttributes.put(eClass, result);
      }

      return result;
    }
  }
}

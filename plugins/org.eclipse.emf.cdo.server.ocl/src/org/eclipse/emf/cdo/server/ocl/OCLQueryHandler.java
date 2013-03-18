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
package org.eclipse.emf.cdo.server.ocl;

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetDataRevisionProvider;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.ocl.Environment;
import org.eclipse.ocl.EvaluationEnvironment;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.ecore.BooleanLiteralExp;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.EcoreFactory;
import org.eclipse.ocl.ecore.EnumLiteralExp;
import org.eclipse.ocl.ecore.IntegerLiteralExp;
import org.eclipse.ocl.ecore.RealLiteralExp;
import org.eclipse.ocl.ecore.StringLiteralExp;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.util.ProblemAware;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A {@link IQueryHandler query handler} that evaluates OCL query expressions.
 *
 * @author Eike Stepper
 */
public class OCLQueryHandler implements IQueryHandler
{
  private static final EcoreFactory FACTORY = EcoreFactory.eINSTANCE;

  public static final String LANGUAGE_NAME = "ocl"; //$NON-NLS-1$

  public OCLQueryHandler()
  {
  }

  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    String queryString = info.getQueryString();
    CDOExtentMap extentMap = null;

    try
    {
      CDORevisionProvider revisionProvider = context.getView();
      CDOChangeSetData changeSetData = info.getChangeSetData();
      if (changeSetData != null)
      {
        revisionProvider = new CDOChangeSetDataRevisionProvider(revisionProvider, changeSetData);
      }

      ISession session = context.getView().getSession();
      CDOView view = CDOServerUtil.openView(session, context, revisionProvider);
      CDOPackageRegistry packageRegistry = view.getSession().getPackageRegistry();

      EcoreEnvironmentFactory envFactory = new EcoreEnvironmentFactory(packageRegistry);
      OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl = OCL.newInstance(envFactory);

      extentMap = createExtentMap(view, changeSetData, context);
      ocl.setExtentMap(extentMap);

      OCLHelper<EClassifier, ?, ?, Constraint> helper = ocl.createOCLHelper();

      EClassifier classifier;
      EObject object = null;

      Object contextParameter = info.getContext();
      if (contextParameter instanceof CDOID)
      {
        CDOID id = (CDOID)contextParameter;
        if (id.isNull())
        {
          classifier = getArbitraryContextClassifier(packageRegistry);
        }
        else
        {
          InternalCDOObject cdoObject = (InternalCDOObject)view.getObject(id);
          object = cdoObject.cdoInternalInstance();
          classifier = object.eClass();
        }
      }
      else if (contextParameter instanceof EClassifier)
      {
        classifier = (EClassifier)contextParameter;
      }
      else
      {
        classifier = getArbitraryContextClassifier(packageRegistry);
      }

      helper.setContext(classifier);

      Map<String, Object> parameters = new HashMap<String, Object>(info.getParameters());
      initEnvironment(ocl.getEnvironment(), packageRegistry, parameters);

      OCLExpression<EClassifier> expr = helper.createQuery(queryString);
      Query<EClassifier, EClass, EObject> query = ocl.createQuery(expr);
      if (query instanceof ProblemAware)
      {
        ProblemAware problemAware = (ProblemAware)query;
        Diagnostic problems = problemAware.getProblems();
        if (problems != null)
        {
          throw new DiagnosticException(problems);
        }
      }

      EvaluationEnvironment<EClassifier, ?, ?, EClass, EObject> evalEnv = query.getEvaluationEnvironment();
      Set<Entry<String, Object>> entrySet = parameters.entrySet();
      for (Entry<String, Object> parameter : entrySet)
      {
        String key = parameter.getKey();
        Object value = parameter.getValue();
        evalEnv.add(key, value);
      }

      Object result = evaluate(query, object);
      if (result instanceof Collection<?>)
      {
        for (Object element : (Collection<?>)result)
        {
          if (!addResult(element, context, view))
          {
            break;
          }
        }
      }
      else
      {
        addResult(result, context, view);
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex, "Problem executing OCL query: " + queryString);
    }
    finally
    {
      if (extentMap != null)
      {
        extentMap.cancel();
      }
    }
  }

  protected boolean addResult(Object result, IQueryContext context, CDOView view)
  {
    if (result instanceof EObject)
    {
      CDORevision revision = getRevision((EObject)result, view);
      return context.addResult(revision);
    }

    return context.addResult(result);
  }

  protected CDORevision getRevision(EObject object, CDOView view)
  {
    return FSMUtil.adapt(object, view).cdoRevision();
  }

  protected Object evaluate(Query<EClassifier, EClass, EObject> query, EObject object)
  {
    if (object == null)
    {
      return query.evaluate();
    }

    return query.evaluate(object);
  }

  protected CDOExtentMap createExtentMap(CDOView view, CDOChangeSetData changeSetData, IQueryContext context)
  {
    CDOExtentCreator creator = createsLazyExtents() ? new CDOExtentCreator.Lazy(view) : new CDOExtentCreator(view);
    creator.setChangeSetData(changeSetData);
    creator.setRevisionCacheAdder((CDORevisionCacheAdder)context.getView().getRepository().getRevisionManager());
    return new CDOExtentMap(creator);
  }

  protected boolean createsLazyExtents()
  {
    return false;
  }

  protected EClassifier getArbitraryContextClassifier(CDOPackageRegistry packageRegistry)
  {
    for (CDOPackageUnit packageUnit : packageRegistry.getPackageUnits())
    {
      for (CDOPackageInfo packageInfo : packageUnit.getPackageInfos())
      {
        if (!packageUnit.isSystem())
        {
          for (EClassifier classifier : packageInfo.getEPackage().getEClassifiers())
          {
            return classifier;
          }
        }
      }
    }

    throw new IllegalStateException("Context missing");
  }

  protected void initEnvironment(
      Environment<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> environment,
      CDOPackageRegistry packageRegistry, Map<String, Object> parameters)
  {
    OCLStandardLibrary<EClassifier> stdLib = environment.getOCLStandardLibrary();
    for (Entry<String, Object> parameter : parameters.entrySet())
    {
      String name = parameter.getKey();
      Object value = parameter.getValue();

      OCLExpression<EClassifier> initExpression = createInitExpression(stdLib, packageRegistry, value);

      Variable<EClassifier, ?> variable = FACTORY.createVariable();
      variable.setName(name);
      variable.setType(initExpression.getType());
      variable.setInitExpression(initExpression);

      addEnvironmentVariable(environment, variable);
    }
  }

  protected OCLExpression<EClassifier> createInitExpression(OCLStandardLibrary<EClassifier> stdLib,
      CDOPackageRegistry packageRegistry, Object value)
  {
    if (value instanceof String)
    {
      String v = (String)value;
      StringLiteralExp literal = FACTORY.createStringLiteralExp();
      literal.setType(stdLib.getString());
      literal.setStringSymbol(v);
      return literal;
    }

    if (value instanceof Boolean)
    {
      Boolean v = (Boolean)value;
      BooleanLiteralExp literal = FACTORY.createBooleanLiteralExp();
      literal.setType(stdLib.getBoolean());
      literal.setBooleanSymbol(v);
      return literal;
    }

    Integer integerValue = getInteger(value);
    if (integerValue != null)
    {
      IntegerLiteralExp literal = FACTORY.createIntegerLiteralExp();
      literal.setType(stdLib.getInteger());
      literal.setIntegerSymbol(integerValue);
      return literal;
    }

    Double doubleValue = getDouble(value);
    if (doubleValue != null)
    {
      RealLiteralExp literal = FACTORY.createRealLiteralExp();
      literal.setType(stdLib.getReal());
      literal.setRealSymbol(doubleValue);
      return literal;
    }

    if (value instanceof Enumerator)
    {
      Enumerator v = (Enumerator)value;
      String name = v.getName();

      EEnumLiteral eEnumLiteral = packageRegistry.getEnumLiteralFor(v);
      if (eEnumLiteral == null)
      {
        throw new IllegalArgumentException("Enum literal not found: " + name);
      }

      EnumLiteralExp literal = FACTORY.createEnumLiteralExp();
      literal.setType(eEnumLiteral.getEEnum());
      literal.setReferredEnumLiteral(eEnumLiteral);
      return literal;
    }

    throw new IllegalArgumentException("Unrecognized parameter type: " + value.getClass().getName());
  }

  private Integer getInteger(Object value)
  {
    if (value instanceof Integer)
    {
      return (Integer)value;
    }

    if (value instanceof Short)
    {
      return (int)(Short)value;
    }

    if (value instanceof Byte)
    {
      return (int)(Byte)value;
    }

    return null;
  }

  private Double getDouble(Object value)
  {
    if (value instanceof Double)
    {
      return (Double)value;
    }

    if (value instanceof Float)
    {
      return (double)(Float)value;
    }

    return null;
  }

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addEnvironmentVariable(
      Environment<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> environment,
      Variable<EClassifier, ?> variable)
  {
    environment.addElement(variable.getName(), (Variable)variable, true);
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new Factory());
  }

  /**
   * Creates {@link OCLQueryHandler} instances.
   *
   * @author Eike Stepper
   */
  public static class Factory extends QueryHandlerFactory
  {
    public Factory()
    {
      super(LANGUAGE_NAME);
    }

    @Override
    public OCLQueryHandler create(String description) throws ProductCreationException
    {
      return new OCLQueryHandler();
    }
  }
}

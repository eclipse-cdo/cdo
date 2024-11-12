/*
 * Copyright (c) 2010-2016, 2019-2021, 2024 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - Additional OCL operations for efficient queries
 */
package org.eclipse.emf.cdo.server.ocl;

import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionInterner;
import org.eclipse.emf.cdo.common.revision.CDORevisionProvider;
import org.eclipse.emf.cdo.common.util.CDOException;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.spi.common.commit.CDOChangeSetDataRevisionProvider;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.DiagnosticException;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.Enumerator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.FSMUtil;
import org.eclipse.emf.spi.cdo.InternalCDOObject;

import org.eclipse.ocl.Environment;
import org.eclipse.ocl.EnvironmentFactory;
import org.eclipse.ocl.EvaluationEnvironment;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ParserException;
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
import org.eclipse.ocl.options.ParsingOptions;
import org.eclipse.ocl.types.OCLStandardLibrary;
import org.eclipse.ocl.util.ProblemAware;
import org.eclipse.ocl.util.Tuple;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A {@link IQueryHandler query handler} that evaluates OCL query expressions.
 *
 * @author Eike Stepper
 */
public class OCLQueryHandler implements IQueryHandler
{
  public static final String LANGUAGE_NAME = "ocl"; //$NON-NLS-1$

  /**
   * @since 4.1
   */
  public static final String LAZY_EXTENTS_PARAMETER = "cdoLazyExtents";

  /**
   * Query parameter indicating the {@link EClass} to set as the implicit root class of the type
   * hierarchy.  The default is {@code null}.
   *
   * @since 4.2
   */
  public static final String IMPLICIT_ROOT_CLASS_PARAMETER = "cdoImplicitRootClass";

  private static final Set<String> SERVER_QUERY_PARAMETERS = Collections
      .unmodifiableSet(new java.util.HashSet<>(Arrays.asList(LAZY_EXTENTS_PARAMETER, IMPLICIT_ROOT_CLASS_PARAMETER)));

  private static final EcoreFactory FACTORY = EcoreFactory.eINSTANCE;

  private static final Method OLD_OCL_NEW_INSTANCE_METHOD = getOCLMethod("newInstance");

  private static final Method NEW_OCL_NEW_INSTANCE_METHOD = getOCLMethod("newInstanceAbstract");

  private boolean lazyExtents = true;

  private EClass implicitRootClass;

  public OCLQueryHandler()
  {
  }

  @Override
  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    CDOExtentMap extentMap = null;

    try
    {
      readParameters(info.getParameters());

      IView serverView = context.getView();

      CDORevisionProvider revisionProvider = serverView;
      CDOChangeSetData changeSetData = info.getChangeSetData();
      if (changeSetData != null)
      {
        revisionProvider = new CDOChangeSetDataRevisionProvider(revisionProvider, changeSetData);
      }

      CDOView view = CDOServerUtil.openView(serverView.getSession(), context, revisionProvider);
      extentMap = createExtentMap(view, changeSetData, context);
      OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl = createOCL(view, extentMap);

      ContextParameter contextParameter = getContextParameter(info, view);
      Query<EClassifier, EClass, EObject> query = createQuery(view, info, contextParameter, ocl);

      Object result = evaluate(query, contextParameter.getObject());
      if (result == ocl.getEnvironment().getOCLStandardLibrary().getInvalid())
      {
        throw new Exception("OCL query evaluated to 'invalid'. Run with '-Dorg.eclipse.ocl.debug=true' and visit the log for failure details.");
      }

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
      throw WrappedException.wrap(ex, "Problem executing OCL query: " + info.getQueryString());
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
    result = convertResult(result, view);
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
    creator.setRevisionInterner((CDORevisionInterner)context.getView().getRepository().getRevisionManager());
    return new CDOExtentMap(creator);
  }

  protected boolean createsLazyExtents()
  {
    return lazyExtents;
  }

  /**
   * @since 4.2
   */
  protected OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> createOCL(CDOView view, CDOExtentMap extentMap)
  {
    EcoreEnvironmentFactory envFactory = new CDOEnvironmentFactory(view.getSession().getPackageRegistry());

    OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl = createOCL(envFactory);
    CDOAdditionalOperation.registerOperations((CDOEnvironment)ocl.getEnvironment());
    ocl.setExtentMap(extentMap);
    return ocl;
  }

  /**
   * @since 4.2
   */
  protected Query<EClassifier, EClass, EObject> createQuery(CDOView view, CDOQueryInfo info, ContextParameter contextParameter,
      OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl) throws ParserException, DiagnosticException
  {
    Map<String, Object> parameters = new HashMap<>(info.getParameters());
    initEnvironment(ocl.getEnvironment(), view.getSession().getPackageRegistry(), parameters);

    OCLHelper<EClassifier, ?, ?, Constraint> helper = ocl.createOCLHelper();
    helper.setContext(contextParameter.getClassifier());

    OCLExpression<EClassifier> expr = helper.createQuery(info.getQueryString());
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

    setOCLQueryParameters(parameters, query);
    return query;
  }

  /**
   * @deprecated As of 4.2 no longer supported.
   */
  @Deprecated
  protected EClassifier getArbitraryContextClassifier(CDOPackageRegistry packageRegistry)
  {
    return ContextParameter.getArbitraryContextClassifier(packageRegistry);
  }

  protected void initEnvironment(Environment<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> environment, CDOPackageRegistry packageRegistry,
      Map<String, Object> parameters)
  {

    // initialize parsing options
    EClass implicitRootClass = getImplicitRootClass();
    if (implicitRootClass != null)
    {
      ParsingOptions.setOption(environment, ParsingOptions.implicitRootClass(environment), implicitRootClass);
    }

    // create variables for query parameters that should be passed through to the OCL query expression
    OCLStandardLibrary<EClassifier> stdLib = environment.getOCLStandardLibrary();
    for (Map.Entry<String, Object> parameter : parameters.entrySet())
    {
      String name = parameter.getKey();
      if (isOCLQueryParameter(name))
      {
        Object value = parameter.getValue();

        OCLExpression<EClassifier> initExpression = createInitExpression(stdLib, packageRegistry, value);

        Variable<EClassifier, ?> variable = FACTORY.createVariable();
        variable.setName(name);
        variable.setType(initExpression.getType());
        variable.setInitExpression(initExpression);

        addEnvironmentVariable(environment, variable);
      }
    }
  }

  /**
   * @since 4.2
   */
  protected EClass getImplicitRootClass()
  {
    return implicitRootClass;
  }

  protected OCLExpression<EClassifier> createInitExpression(OCLStandardLibrary<EClassifier> stdLib, CDOPackageRegistry packageRegistry, Object value)
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

  @SuppressWarnings({ "unchecked", "rawtypes" })
  protected void addEnvironmentVariable(Environment<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> environment,
      Variable<EClassifier, ?> variable)
  {
    environment.addElement(variable.getName(), (Variable)variable, true);
  }

  /**
   * @since 4.2
   */
  protected ContextParameter getContextParameter(CDOQueryInfo info, CDOView view)
  {
    return new ContextParameter(view, info);
  }

  /**
   * @since 4.2
   */
  protected void readParameters(Map<String, ?> queryParameters)
  {
    lazyExtents = readParameter(queryParameters, LAZY_EXTENTS_PARAMETER, lazyExtents);
    implicitRootClass = readParameter(queryParameters, IMPLICIT_ROOT_CLASS_PARAMETER, EClass.class, implicitRootClass);
  }

  /**
   * @since 4.2
   */
  protected boolean readParameter(Map<String, ?> queryParameters, String name, boolean defaultValue)
  {
    return readParameter(queryParameters, name, Boolean.class, defaultValue);
  }

  /**
   * @since 4.2
   */
  protected <T> T readParameter(Map<String, ?> queryParameters, String name, Class<T> type, T defaultValue)
  {
    T result = defaultValue;

    Object o = queryParameters.get(name);
    if (o != null)
    {
      try
      {
        result = type.cast(o);
      }
      catch (ClassCastException ex)
      {
        throw new IllegalArgumentException(
            "Parameter " + name + " must be a " + type.getSimpleName() + " but it is a " + o + " class " + o.getClass().getName(), ex);
      }
    }

    return result;
  }

  /**
   * @since 4.2
   */
  protected void setOCLQueryParameters(Map<String, Object> parameters, Query<EClassifier, EClass, EObject> query)
  {
    EvaluationEnvironment<EClassifier, ?, ?, EClass, EObject> evalEnv = query.getEvaluationEnvironment();
    for (Map.Entry<String, Object> parameter : parameters.entrySet())
    {
      String key = parameter.getKey();

      if (isOCLQueryParameter(key))
      {
        Object value = parameter.getValue();
        evalEnv.add(key, value);
      }
    }
  }

  /**
   * @since 4.2
   */
  protected boolean isOCLQueryParameter(String name)
  {
    return !SERVER_QUERY_PARAMETERS.contains(name);
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
  private Object convertResult(Object result, CDOView view)
  {
    if (result instanceof EObject)
    {
      return getRevision((EObject)result, view);
    }

    if (result instanceof Tuple)
    {
      Tuple tuple = (Tuple)result;
      EList properties = tuple.getTupleType().oclProperties();
      Object[] array = new Object[properties.size()];
      for (int i = 0; i < array.length; ++i)
      {
        array[i] = convertResult(tuple.getValue(properties.get(i)), view);
      }

      return array;
    }

    return result;
  }

  private static Method getOCLMethod(String methodName)
  {
    try
    {
      return OCL.class.getDeclaredMethod(methodName, EnvironmentFactory.class);
    }
    catch (Throwable ex)
    {
      //$FALL-THROUGH$
    }

    return null;
  }

  private static OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> createOCL(EcoreEnvironmentFactory envFactory)
  {
    try
    {
      if (OLD_OCL_NEW_INSTANCE_METHOD != null)
      {
        @SuppressWarnings("unchecked")
        OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl = //
            (OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject>)OLD_OCL_NEW_INSTANCE_METHOD.invoke(null, envFactory);
        return ocl;
      }

      if (NEW_OCL_NEW_INSTANCE_METHOD != null)
      {
        @SuppressWarnings("unchecked")
        OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl = //
            (OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject>)NEW_OCL_NEW_INSTANCE_METHOD.invoke(null, envFactory);
        return ocl;
      }
    }
    catch (ReflectiveOperationException ex)
    {
      throw WrappedException.wrap(ex);
    }

    throw new CDOException("OCL is missing");
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

  /**
   * An abstraction of the {@link EClassifier classifier} and/or {@link EObject object} of an OCL query context parameter.
   *
   * @author Eike Stepper
   * @since 4.2
   */
  protected static final class ContextParameter
  {
    private final EClassifier classifier;

    private final EObject object;

    public ContextParameter(CDOView view, CDOQueryInfo info)
    {
      Object contextParameter = info.getContext();
      if (contextParameter instanceof CDOID)
      {
        CDOID id = (CDOID)contextParameter;
        if (id.isNull())
        {
          CDOPackageRegistry packageRegistry = view.getSession().getPackageRegistry();
          classifier = getArbitraryContextClassifier(packageRegistry);
          object = null;
        }
        else
        {
          InternalCDOObject cdoObject = (InternalCDOObject)view.getObject(id);
          classifier = cdoObject.eClass();
          object = cdoObject.cdoInternalInstance();
        }
      }
      else if (contextParameter instanceof EClassifier)
      {
        classifier = (EClassifier)contextParameter;
        object = null;
      }
      else
      {
        CDOPackageRegistry packageRegistry = view.getSession().getPackageRegistry();
        classifier = getArbitraryContextClassifier(packageRegistry);
        object = null;
      }
    }

    public ContextParameter(EClassifier classifier, EObject object)
    {
      this.classifier = classifier;
      this.object = object;
    }

    public EClassifier getClassifier()
    {
      return classifier;
    }

    public EObject getObject()
    {
      return object;
    }

    protected static EClassifier getArbitraryContextClassifier(CDOPackageRegistry packageRegistry)
    {
      for (CDOPackageUnit packageUnit : packageRegistry.getPackageUnits())
      {
        for (CDOPackageInfo packageInfo : packageUnit.getPackageInfos())
        {
          for (EClassifier classifier : packageInfo.getEPackage().getEClassifiers())
          {
            return classifier;
          }
        }
      }

      throw new IllegalStateException("Context missing");
    }
  }
}

/**
 * Copyright (c) 2004 - 2010 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.server.ocl;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.model.CDOPackageInfo;
import org.eclipse.emf.cdo.common.model.CDOPackageRegistry;
import org.eclipse.emf.cdo.common.model.CDOPackageUnit;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.cache.CDORevisionCacheAdder;
import org.eclipse.emf.cdo.common.util.CDOQueryInfo;
import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IQueryContext;
import org.eclipse.emf.cdo.server.IQueryHandler;
import org.eclipse.emf.cdo.spi.server.QueryHandlerFactory;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.factory.ProductCreationException;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;

import org.eclipse.ocl.OCL;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Eike Stepper
 */
public class OCLQueryHandler implements IQueryHandler
{
  public static final String LANGUAGE_NAME = "ocl"; //$NON-NLS-1$

  public static final String CONTEXT_PARAMETER = "context"; //$NON-NLS-1$

  public OCLQueryHandler()
  {
  }

  public void executeQuery(CDOQueryInfo info, IQueryContext context)
  {
    String queryString = info.getQueryString();

    try
    {
      CDOView view = CDOServerUtil.openView(context.getView(), false);
      CDOPackageRegistry packageRegistry = view.getSession().getPackageRegistry();

      EcoreEnvironmentFactory envFactory = new EcoreEnvironmentFactory(packageRegistry);
      OCL<?, EClassifier, ?, ?, ?, ?, ?, ?, ?, Constraint, EClass, EObject> ocl = OCL.newInstance(envFactory);
      ocl.setExtentMap(createExtentMap(view, context));

      OCLHelper<EClassifier, ?, ?, Constraint> helper = ocl.createOCLHelper();

      CDOObject object = null;
      Object param = info.getParameters().get(CONTEXT_PARAMETER);
      if (param instanceof CDOID)
      {
        CDOID id = (CDOID)param;
        object = view.getObject(id);
        helper.setContext(object.eClass());
      }
      else
      {
        helper.setContext(getArbitraryContextClassifier(packageRegistry));
      }

      OCLExpression<EClassifier> expr = helper.createQuery(queryString);
      Query<EClassifier, EClass, EObject> query = ocl.createQuery(expr);

      Collection<?> results = (Collection<?>)query.evaluate(object);
      for (Object result : results)
      {
        if (result instanceof CDOObject)
        {
          CDORevision revision = ((CDOObject)result).cdoRevision();
          if (!context.addResult(revision))
          {
            break;
          }
        }
      }
    }
    catch (Exception ex)
    {
      System.err.println(queryString);
      ex.printStackTrace();
      throw WrappedException.wrap(ex, "Problem executing OCL query: " + queryString);
    }
  }

  protected EClassifier getArbitraryContextClassifier(CDOPackageRegistry packageRegistry)
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

    throw new IllegalStateException("Context parameter missing");
  }

  protected Map<EClass, ? extends Set<? extends EObject>> createExtentMap(CDOView view, IQueryContext context)
  {
    CDORevisionCacheAdder cacheAdder = (CDORevisionCacheAdder)context.getView().getRepository().getRevisionManager();
    OCLExtentCreator extentCreator = new CDOExtentCreator(view, cacheAdder);
    return new CDOExtentMap(extentCreator);
  }

  public static void prepareContainer(IManagedContainer container)
  {
    container.registerFactory(new Factory());
  }

  /**
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

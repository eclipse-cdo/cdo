package org.eclipse.emf.cdo.internal.server;

import org.eclipse.net4j.util.factory.Factory;

/**
 * @author Eike Stepper
 */
public abstract class QueryHandlerFactory extends Factory
{
  public static final String PRODUCT_GROUP = "org.eclipse.emf.cdo.server.queryHandlerFactories";

  public QueryHandlerFactory(String language)
  {
    super(PRODUCT_GROUP, language);
  }
}
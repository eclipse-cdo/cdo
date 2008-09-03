package org.eclipse.emf.cdo.server;

/**
 * @author Simon McDuff
 */
public interface IQueryContext
{
  public IView getView();

  /**
   * Adds the given object to the results of the associated query and returns <code>true</code> if more results are
   * allowed, <code>false</code> otherwise.
   */
  public boolean addResult(Object object);
}

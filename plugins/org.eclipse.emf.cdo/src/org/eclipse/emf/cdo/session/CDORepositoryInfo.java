package org.eclipse.emf.cdo.session;

/**
 * Describes a model repository a {@link CDOSession session} is connected to.
 * 
 * @author Eike Stepper
 * @see CDOSession#getRepositoryInfo()
 * @since 3.0
 */
public interface CDORepositoryInfo
{
  /**
   * Returns the name of this repository.
   * 
   * @see IRepository#getName()
   */
  public String getName();

  /**
   * Returns the UUID of this repository.
   * 
   * @see IRepository#getUUID()
   */
  public String getUUID();

  /**
   * Returns the creation time of this repository.
   * 
   * @see IRepository#getCreationTime()
   */
  public long getCreationTime();

  /**
   * Returns the approximate current time of this repository.
   * <p>
   * Same as calling <code>getCurrentTime(false)</code>.
   * 
   * @see #getCurrentTime(boolean)
   */
  public long getCurrentTime();

  /**
   * Returns the approximate current time of this repository by optionally refreshing the approximation from the server.
   */
  public long getCurrentTime(boolean forceRefresh);

  /**
   * Returns <code>true</code> if this repository supports auditing, <code>false</code> otherwise.
   * 
   * @see IRepository#isSupportingAudits()
   */
  public boolean isSupportingAudits();
}

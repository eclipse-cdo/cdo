package org.eclipse.emf.cdo.internal.common.revision;

/**
 * The built-in equals-equality CDO list.
 *
 *  @author Eike Stepper
 */
public class CDOListWithEqualsImpl extends CDOListImpl
{
  private static final long serialVersionUID = 1L;

  public CDOListWithEqualsImpl(int initialCapacity, int size)
  {
    super(initialCapacity, size);
  }

  @Override
  protected boolean useEquals()
  {
    return true;
  }
}

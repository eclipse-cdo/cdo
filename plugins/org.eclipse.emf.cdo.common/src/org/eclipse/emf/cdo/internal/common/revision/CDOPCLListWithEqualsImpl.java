package org.eclipse.emf.cdo.internal.common.revision;

/**
 * The built-in equals-equality list that tracks partially loaded values.
 *
 *  @author Eike Stepper
 */
public class CDOPCLListWithEqualsImpl extends CDOPCLListImpl
{
  private static final long serialVersionUID = 1L;

  public CDOPCLListWithEqualsImpl(int initialCapacity, int size, int initialChunk)
  {
    super(initialCapacity, size, initialChunk);
  }

  public CDOPCLListWithEqualsImpl(int initialCapacity, int size)
  {
    super(initialCapacity, size);
  }

  @Override
  protected boolean useEquals()
  {
    return true;
  }
}

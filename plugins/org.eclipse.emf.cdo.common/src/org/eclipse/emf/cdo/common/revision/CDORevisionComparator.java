package org.eclipse.emf.cdo.common.revision;

import java.util.Comparator;

/**
 * @author Eike Stepper
 * @since 4.0
 */
public class CDORevisionComparator implements Comparator<CDORevisionKey>
{
  public CDORevisionComparator()
  {
  }

  public int compare(CDORevisionKey rev1, CDORevisionKey rev2)
  {
    int result = rev1.getID().compareTo(rev2.getID());
    if (result == 0)
    {
      int version1 = rev1.getVersion();
      int version2 = rev2.getVersion();
      result = version1 < version2 ? -1 : version1 == version2 ? 0 : 1;
    }

    return result;
  }
}

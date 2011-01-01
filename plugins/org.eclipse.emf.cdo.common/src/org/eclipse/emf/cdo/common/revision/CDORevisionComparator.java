/**
 * Copyright (c) 2004 - 2011 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
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

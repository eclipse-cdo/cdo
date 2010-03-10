package org.eclipse.emf.cdo.ui.internal.branch.util;

import org.eclipse.zest.layouts.dataStructures.DisplayIndependentDimension;

public class GeometryUtils
{
  
  /**
   * Unifies both given dimension.
   * 
   * @param thisDimension the this dimension
   * @param thatDimension the that dimension
   * 
   * @return the display independent dimension
   */
  public static DisplayIndependentDimension union(DisplayIndependentDimension thisDimension, DisplayIndependentDimension thatDimension) {
    DisplayIndependentDimension union = new DisplayIndependentDimension(thisDimension);
    union.width += thatDimension.width;
    union.height += thatDimension.height;
    return union;
  }
}

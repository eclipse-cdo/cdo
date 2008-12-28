package org.eclipse.net4j.util.net4jutildefs.util;

import java.util.Collection;
import java.util.List;

/**
 * The Class Net4jUtilDefsUtil.
 */
public class Net4jUtilDefsUtil
{

  /**
   * Are equal.
   * 
   * @param thisObject
   *          the this object
   * @param thatObject
   *          the that object
   * @return true, if successful
   */
  public static boolean areEqual(Object thisObject, Object thatObject)
  {
    boolean areEqual = false;
    if (thisObject == null && thatObject == null)
    {
      areEqual = true;
    }
    else if (thisObject != null && thisObject != null)
    {
      areEqual = thisObject.equals(thatObject);
    }
    return areEqual;
  }

  /**
   * Are equal.
   * 
   * @param thisList
   *          the this list
   * @param thatList
   *          the that list
   * @return true, if successful
   */
  public static boolean areEqual(List<?> thisList, List<?> thatList)
  {
    boolean areEqual = false;
    if (thisList != null && thatList != null && thisList.size() == thatList.size())
    {
      for (int i = 0; i < thisList.size(); i++)
      {
        if (!areEqual(thisList.get(i), thatList.get(i)))
        {
          areEqual = false;
          break;
        }
      }
    }
    return areEqual;
  }

  /**
   * Do for all structural features.
   * 
   * @param visitor
   *          the visitor
   * @param structuralFeaturesList
   *          the structural features list
   */
  public static <EStructuralFeature> void doForAllStructuralFeatures(IVisitor<EStructuralFeature> visitor,
      Collection<EStructuralFeature> structuralFeatures)
  {
    new VisitorDelegator<EStructuralFeature>(structuralFeatures).visit(visitor);
  }

  /**
   * The Class VisitorDelegator. It visits all members of a Collection
   */
  private static class VisitorDelegator<V>
  {

    /** The visited list. */
    private Collection<V> visitedCollection;

    /**
     * Instantiates a new visitor delegator.
     * 
     * @param list
     *          the list
     */
    private VisitorDelegator(Collection<V> collection)
    {
      this.visitedCollection = collection;
    }

    /**
     * Visit a member.
     * 
     * @param visitor
     *          the visitor
     */
    private void visit(IVisitor<V> visitor)
    {
      for (V visitedMember : visitedCollection)
      {
        visitor.visit(visitedMember);
      }
    }

    /**
     * Visit a member and break the visits if the current visitor returns <code>flase</code>.
     * 
     * @param visitor
     *          the visitor
     */
    private void breakableVisit(IBreakingVisitor<V> visitor)
    {
      for (V visitedMember : visitedCollection)
      {
        if (!visitor.visit(visitedMember))
        {
          break;
        }
      }
    }
  }

  /**
   * The Interface IVisitor.
   */
  public interface IVisitor<V>
  {

    /**
     * Visit.
     * 
     * @param visitedMember
     *          the visited member
     */
    public void visit(V visitedMember);
  }

  /**
   * The Interface IBreakingVisitor.
   */
  public interface IBreakingVisitor<V>
  {

    /**
     * Visit.
     * 
     * @param visitedMember
     *          the visited member
     * @return true, if successful
     */
    public boolean visit(V visitedMember);
  }
}

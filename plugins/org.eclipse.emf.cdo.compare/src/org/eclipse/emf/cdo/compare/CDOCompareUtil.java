/*
 * Copyright (c) 2004 - 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.compare;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.net4j.util.lifecycle.ILifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleEventAdapter;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.compare.scope.AbstractComparisonScope;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.google.common.base.Function;
import com.google.common.collect.Iterators;

import java.util.Iterator;

/**
 * Various static methods that may help EMF Compare in a CDO scope.
 *
 * @author Eike Stepper
 */
public final class CDOCompareUtil
{
  private CDOCompareUtil()
  {
  }

  public static CDOComparison compare(CDOView leftView, CDOBranchPoint right)
  {
    return compare(leftView, right, true);
  }

  public static CDOComparison compare(CDOView leftView, CDOBranchPoint right, boolean threeWay)
  {
    CDOSession session = leftView.getSession();

    CDOView rightView;
    if (right instanceof CDOView)
    {
      rightView = (CDOView)right;
      if (rightView.getSession() != session)
      {
        throw new IllegalArgumentException("Sessions are different");
      }
    }
    else
    {
      rightView = session.openView(right);
    }

    CDOObject leftObject = leftView.getRootResource();
    Notifier rightObject = rightView.getRootResource();

    CDOView originView = null;
    Notifier originObject = null;
    if (threeWay)
    {
      CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(leftView, rightView);
      if (!ancestor.equals(leftView) && !ancestor.equals(rightView))
      {
        originView = session.openView(ancestor);
        originObject = originView.getRootResource();
      }
    }

    // Set<CDOID> ids;
    // if (originView != null)
    // {
    // MergeData mergeData = ((InternalCDOSession)session).getMergeData(leftView, rightView, null);
    // ids = mergeData.getIDs();
    // }
    // else
    // {
    // CDOChangeSetData changeSetData = leftView.compareRevisions(right);
    // ids = changeSetData.getChangeKinds().keySet();
    // }

    IComparisonScope scope = new CDOComparisonScope.AllContents(leftObject, rightObject, originObject);
    return createComparison(scope, leftView, rightView, originView);
  }

  public static CDOComparison compare(EObject leftRoot, CDOBranchPoint right)
  {
    return compare(leftRoot, right, true);
  }

  public static CDOComparison compare(EObject leftRoot, CDOBranchPoint right, boolean threeWay)
  {
    CDOObject leftObject = CDOUtil.getCDOObject(leftRoot);
    CDOView leftView = leftObject.cdoView();
    CDOBranchPoint left = CDOBranchUtil.copyBranchPoint(leftView);
    CDOSession session = leftView.getSession();

    CDOView rightView;
    if (right instanceof CDOView)
    {
      rightView = (CDOView)right;
      if (rightView.getSession() != session)
      {
        throw new IllegalArgumentException("Sessions are different");
      }
    }
    else
    {
      rightView = session.openView(right);
    }

    Notifier rightObject = rightView.getObject(leftObject);

    CDOView originView = null;
    Notifier originObject = null;
    if (threeWay)
    {
      CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(left, right);
      if (!ancestor.equals(left) && !ancestor.equals(right))
      {
        originView = session.openView(ancestor);
        originObject = originView.getObject(leftObject);
      }
    }

    IComparisonScope scope = new CDOComparisonScope.AllContents(leftObject, rightObject, originObject);
    return createComparison(scope, leftView, rightView, originView);
  }

  private static EMFCompare createComparator(IComparisonScope scope)
  {
    Function<EObject, String> idFunction = new CDOIDFunction();
    IEObjectMatcher matcher = new IdentifierEObjectMatcher.Builder().idFunction(idFunction).build();

    EMFCompare comparator = EMFCompare.newComparator(scope);
    comparator.setEObjectMatcher(matcher);
    return comparator;
  }

  private static CDOComparison createComparison(IComparisonScope scope, CDOView leftView, CDOView rightView,
      CDOView originView)
  {
    EMFCompare comparator = createComparator(scope);
    Comparison comparison = comparator.compare();
    return new CDOComparison(comparison, leftView, rightView, originView);
  }

  /**
   * @author Eike Stepper
   */
  public static class CDOComparison extends DelegatingComparison implements CloseableComparison
  {
    private CDOView leftView;

    private CDOView rightView;

    private CDOView originView;

    public CDOComparison(Comparison delegate, CDOView leftView, CDOView rightView, CDOView originView)
    {
      super(delegate);
      this.leftView = leftView;
      this.rightView = rightView;
      this.originView = originView;

      leftView.addListener(new LifecycleEventAdapter()
      {
        @Override
        protected void onDeactivated(ILifecycle lifecycle)
        {
          close();
        }
      });
    }

    public CDOView getLeftView()
    {
      return leftView;
    }

    public CDOView getRightView()
    {
      return rightView;
    }

    public CDOView getOriginView()
    {
      return originView;
    }

    public void close()
    {
      close(false);
    }

    public void close(boolean closeLeftView)
    {
      if (closeLeftView)
      {
        LifecycleUtil.deactivate(leftView);
      }

      LifecycleUtil.deactivate(rightView);
      LifecycleUtil.deactivate(originView);

      leftView = null;
      rightView = null;
      originView = null;
      delegate = null;
    }

    public boolean isClosed()
    {
      return delegate == null;
    }
  }

  /**
   * @author Eike Stepper
   */
  public static abstract class CDOComparisonScope extends AbstractComparisonScope
  {
    public CDOComparisonScope(Notifier left, Notifier right, Notifier origin)
    {
      super(left, right, origin);
    }

    public Iterator<? extends Resource> getCoveredResources(ResourceSet resourceSet)
    {
      return Iterators.emptyIterator();
    }

    public Iterator<? extends EObject> getCoveredEObjects(Resource resource)
    {
      return Iterators.emptyIterator();
    }

    /**
     * @author Eike Stepper
     */
    public static class AllContents extends CDOComparisonScope
    {
      public AllContents(Notifier left, Notifier right, Notifier origin)
      {
        super(left, right, origin);
      }

      public Iterator<? extends EObject> getChildren(EObject eObject)
      {
        return eObject.eAllContents();
      }
    }
  }

  /**
   * @author Eike Stepper
   */
  public static class CDOIDFunction implements Function<EObject, String>
  {
    public String apply(EObject o)
    {
      CDOObject object = CDOUtil.getCDOObject(o);
      CDOID id = object.cdoID();

      StringBuilder builder = new StringBuilder();
      CDOIDUtil.write(builder, id);
      return builder.toString();
    }
  }
}

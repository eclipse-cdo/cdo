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
import org.eclipse.emf.cdo.common.branch.CDOBranch;
import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.commit.CDOChangeSetData;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.compare.CDOComparisonScope.AllContents;
import org.eclipse.emf.cdo.compare.CDOComparisonScope.Minimal;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFolder;
import org.eclipse.emf.cdo.eresource.CDOResourceNode;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.spi.common.branch.CDOBranchUtil;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.spi.cdo.InternalCDOSession;
import org.eclipse.emf.spi.cdo.InternalCDOSession.MergeData;

import com.google.common.base.Function;

import java.util.HashSet;
import java.util.Set;

/**
 * Provides static factory methods that return CDO-scoped {@link Comparison comparisons}.
 * <p>
 * Two different {@link IComparisonScope scopes} are supported:
 * <ul>
 * <li>{@link AllContents CDOComparisonScope.AllContents} takes an arbitrary {@link CDOObject object} (including {@link CDOResourceNode resource nodes})
 * and returns {@link Match matches} for <b>all</b> elements of its {@link EObject#eAllContents() content tree}. This scope has the advantage that the comparison can
 * be rooted at specific objects that are different from (below of) the root resource. The disadvantage is that all the transitive children of this specific object are
 * matched, whether they differ or not. Major parts of huge repositories can be loaded to the client side easily, if no attention is paid.
 * The following method returns comparisons that are based on this scope algorithm:
 * <ul>
 * <li>{@link #compare(CDOObject, CDOView, CDOView[])}
 * </ul>
 * <li>{@link Minimal CDOComparisonScope.Minimal} takes a {@link CDOView view}/{@link CDOTransaction transaction}
 * and returns {@link Match matches} only for the <b>changed</b> elements of the entire content tree of its {@link CDOView#getRootResource() root resource}.
 * The advantage of this scope is that CDO-specific mechanisms are used to efficiently (remotely) determine the set of changed objects. Only those and their container
 * objects are considered as matches, making this scope scale seamlessly with the overall size of a repository.
 * The following method returns comparisons that are based on this scope algorithm:
 * <ul>
 * <li>{@link #compare(CDOView, CDOView, CDOView[])}
 * </ul>
 * </ul>
 * The {@link IComparisonScope#getOrigin() origin side} of a comparison is automatically {@link CDOBranchUtil#getAncestor(CDOBranchPoint, CDOBranchPoint) determined} by
 * inspecting the {@link CDOBranch branch tree} and used if its different from the left or right side.
 * <p>
 * The {@link IEObjectMatcher matcher} used by the comparisons is based on an {@link CDOIDFunction ID function} that considers the {@link CDOID}s of the {@link CDOObject objects}.
 * {@link CDOResource Resources} and {@link CDOResourceFolder folders} are treated as normal {@link EObject}s.
 *
 * @author Eike Stepper
 */
public final class CDOCompareUtil
{
  private CDOCompareUtil()
  {
  }

  /**
   * Takes an arbitrary {@link CDOObject object} (including {@link CDOResourceNode resource nodes}) and returns {@link Match matches} for <b>all</b> elements of its {@link EObject#eAllContents() content tree}. This scope has the advantage that the comparison can
   * be rooted at specific objects that are different from (below of) the root resource. The disadvantage is that all the transitive children of this specific object are
   * matched, whether they differ or not. Major parts of huge repositories can be loaded to the client side easily, if no attention is paid.
   */
  public static Comparison compare(CDOObject left, CDOView rightView, CDOView[] originView)
  {
    CDOView leftView = left.cdoView();
    assertSameSession(leftView, rightView);

    CDOView view = openOriginView(leftView, rightView, originView);

    CDOObject right = CDOUtil.getCDOObject(rightView.getObject(left));
    CDOObject origin = view == null ? null : CDOUtil.getCDOObject(view.getObject(left));

    IComparisonScope scope = new CDOComparisonScope.AllContents(left, right, origin);
    return createComparison(scope);
  }

  /**
   * Takes a {@link CDOView view}/{@link CDOTransaction transaction}
   * and returns {@link Match matches} only for the <b>changed</b> elements of the entire content tree of its {@link CDOView#getRootResource() root resource}.
   * The advantage of this scope is that CDO-specific mechanisms are used to efficiently (remotely) determine the set of changed objects. Only those and their container
   * objects are considered as matches, making this scope scale seamlessly with the overall size of a repository.
   */
  public static Comparison compare(CDOView leftView, CDOView rightView, CDOView[] originView)
  {
    assertSameSession(leftView, rightView);

    CDOView view = openOriginView(leftView, rightView, originView);
    Set<CDOID> ids = getAffectedIDs(leftView, rightView, view);
    return createComparison(leftView, rightView, view, ids);
  }

  public static Comparison compare(CDOView leftView, CDOView rightView, CDOView[] originView, Set<CDOID> ids)
  {
    assertSameSession(leftView, rightView);

    CDOView view = openOriginView(leftView, rightView, originView);
    return createComparison(leftView, rightView, view, ids);
  }

  public static Comparison compareUncommittedChanges(CDOTransaction transaction)
  {
    CDOSession session = transaction.getSession();
    CDOView lastView = session.openView(transaction.getLastUpdateTime());

    Set<CDOID> ids = new HashSet<CDOID>();
    ids.addAll(transaction.getNewObjects().keySet());
    ids.addAll(transaction.getDirtyObjects().keySet());
    ids.addAll(transaction.getDetachedObjects().keySet());

    return createComparison(transaction, lastView, null, ids);
  }

  private static void assertSameSession(CDOView view1, CDOView view2)
  {
    if (view1.getSession() != view2.getSession())
    {
      throw new IllegalArgumentException("Sessions are different");
    }
  }

  private static CDOView openOriginView(CDOView leftView, CDOView rightView, CDOView[] originView)
  {
    if (originView != null)
    {
      if (originView.length != 1)
      {
        throw new IllegalArgumentException("originView.length != 1");
      }

      if (originView[0] != null)
      {
        throw new IllegalArgumentException("originView[0] != null");
      }

      CDOBranchPoint ancestor = CDOBranchUtil.getAncestor(leftView, rightView);
      if (!ancestor.equals(leftView) && !ancestor.equals(rightView))
      {
        originView[0] = leftView.getSession().openView(ancestor);
        return originView[0];
      }
    }

    return null;
  }

  private static Set<CDOID> getAffectedIDs(CDOView leftView, CDOView rightView, CDOView originView)
  {
    if (originView != null)
    {
      InternalCDOSession session = (InternalCDOSession)leftView.getSession();
      MergeData mergeData = session.getMergeData(leftView, rightView, originView, false);
      return mergeData.getIDs();
    }

    CDOChangeSetData changeSetData = leftView.compareRevisions(rightView);
    return new HashSet<CDOID>(changeSetData.getChangeKinds().keySet());
  }

  private static Comparison createComparison(CDOView leftView, CDOView rightView, CDOView originView, Set<CDOID> ids)
  {
    IComparisonScope scope = new CDOComparisonScope.Minimal(leftView, rightView, originView, ids);
    return createComparison(scope);
  }

  private static Comparison createComparison(IComparisonScope scope)
  {
    Function<EObject, String> idFunction = new CDOIDFunction();
    IEObjectMatcher matcher = new IdentifierEObjectMatcher(idFunction);

    IComparisonFactory comparisonFactory = new DefaultComparisonFactory(new DefaultEqualityHelperFactory());
    IMatchEngine matchEngine = new CDOMatchEngine(matcher, comparisonFactory);
    EMFCompare comparator = EMFCompare.builder().setMatchEngine(matchEngine).build();

    Comparison comparison = comparator.compare(scope);
    comparison.eAdapters().add(new ComparisonScopeAdapter(scope));
    return comparison;
  }

  /**
   * @author Eike Stepper
   */
  private static final class CDOMatchEngine extends DefaultMatchEngine
  {
    private CDOMatchEngine(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
    {
      super(matcher, comparisonFactory);
    }

    @Override
    protected void match(Comparison comparison, IComparisonScope scope, final Notifier left, final Notifier right,
        final Notifier origin, Monitor monitor)
    {
      match(comparison, scope, (EObject)left, (EObject)right, (EObject)origin, monitor);
    }
  }
}

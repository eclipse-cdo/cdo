/*
 * Copyright (c) 2013, 2015, 2016, 2019-2022 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.compare;

import org.eclipse.emf.cdo.CDOElement;
import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.EMFUtil;
import org.eclipse.emf.cdo.internal.compare.CDOCompareFactoryImpl;
import org.eclipse.emf.cdo.util.CDOUtil;

import org.eclipse.net4j.util.ReflectUtil;
import org.eclipse.net4j.util.om.OMPlatform;

import org.eclipse.emf.common.CommonPlugin;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.compare.CompareFactory;
import org.eclipse.emf.compare.ComparePackage;
import org.eclipse.emf.compare.Comparison;
import org.eclipse.emf.compare.EMFCompare;
import org.eclipse.emf.compare.EMFCompare.Builder;
import org.eclipse.emf.compare.Match;
import org.eclipse.emf.compare.conflict.IConflictDetector;
import org.eclipse.emf.compare.diff.DefaultDiffEngine;
import org.eclipse.emf.compare.diff.FeatureFilter;
import org.eclipse.emf.compare.diff.IDiffEngine;
import org.eclipse.emf.compare.diff.IDiffProcessor;
import org.eclipse.emf.compare.equi.IEquiEngine;
import org.eclipse.emf.compare.match.DefaultComparisonFactory;
import org.eclipse.emf.compare.match.DefaultEqualityHelperFactory;
import org.eclipse.emf.compare.match.DefaultMatchEngine;
import org.eclipse.emf.compare.match.IComparisonFactory;
import org.eclipse.emf.compare.match.IEqualityHelperFactory;
import org.eclipse.emf.compare.match.IMatchEngine;
import org.eclipse.emf.compare.match.eobject.IEObjectMatcher;
import org.eclipse.emf.compare.match.eobject.IdentifierEObjectMatcher;
import org.eclipse.emf.compare.match.impl.MatchEngineFactoryRegistryImpl;
import org.eclipse.emf.compare.postprocessor.IPostProcessor;
import org.eclipse.emf.compare.req.IReqEngine;
import org.eclipse.emf.compare.scope.IComparisonScope;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.InternalEList;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A class with many overridable factory methods that help to create EMF {@link Comparison comparisons}.
 *
 * @author Eike Stepper
 */
public class CDOCompare
{
  private static final boolean IGNORE_RCP_REGISTRIES = OMPlatform.INSTANCE.isProperty("org.eclipse.emf.cdo.compare.CDOCompare.IGNORE_RCP_REGISTRIES");

  private static final boolean USE_RCP_REGISTRIES;

  private static final ThreadLocal<Supplier<CDOCompare>> SUPPLIER = new ThreadLocal<>();

  static
  {
    if (IGNORE_RCP_REGISTRIES)
    {
      USE_RCP_REGISTRIES = false;
    }
    else
    {
      boolean rcpRegistriesAvailable = false;

      try
      {
        rcpRegistriesAvailable = CommonPlugin.loadClass("org.eclipse.emf.compare.rcp", "org.eclipse.emf.compare.rcp.EMFCompareRCPPlugin") != null;
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }

      USE_RCP_REGISTRIES = rcpRegistriesAvailable;
    }

    // Bug 568373 - IllegalStateException in ReferenceChangeMerger:
    // Couldn't add in target because its parent hasn't been merged yet.
    EFactory factory = ComparePackage.eINSTANCE.getEFactoryInstance();
    if (factory == null || factory.getClass() != CDOCompareFactoryImpl.class)
    {
      ComparePackage.eINSTANCE.setEFactoryInstance(CDOCompareFactoryImpl.getInstance());
    }
  }

  /**
   * @since 4.6
   */
  public boolean useRCPRegistries()
  {
    return USE_RCP_REGISTRIES;
  }

  public Comparison compare(IComparisonScope scope)
  {
    Function<EObject, String> idFunction = createIDFunction();
    IEObjectMatcher matcher = createMatcher(idFunction);
    IEqualityHelperFactory equalityHelperFactory = createEqualityHelperFactory();
    IComparisonFactory comparisonFactory = createComparisonFactory(equalityHelperFactory);

    EMFCompare comparator = createComparator(matcher, comparisonFactory);
    return comparator.compare(scope);
  }

  protected CDOIDFunction createIDFunction()
  {
    return new CDOIDFunction();
  }

  protected IdentifierEObjectMatcher createMatcher(Function<EObject, String> idFunction)
  {
    return new CDOMatcher(idFunction);
  }

  protected IEqualityHelperFactory createEqualityHelperFactory()
  {
    return new DefaultEqualityHelperFactory();
  }

  protected IComparisonFactory createComparisonFactory(IEqualityHelperFactory equalityHelperFactory)
  {
    return new DefaultComparisonFactory(equalityHelperFactory);
  }

  /**
   * @since 4.5
   */
  @SuppressWarnings("restriction")
  protected Builder createComparatorBuilder()
  {
    Builder builder = EMFCompare.builder();

    if (useRCPRegistries())
    {
      org.eclipse.emf.compare.rcp.internal.extension.IEMFCompareBuilderConfigurator configurator = //
          org.eclipse.emf.compare.rcp.internal.extension.impl.EMFCompareBuilderConfigurator.createDefault();

      configurator.configure(builder);
    }

    return builder;
  }

  protected EMFCompare createComparator(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
  {
    Builder builder = createComparatorBuilder();

    IMatchEngine.Factory.Registry matchEngineFactoryRegistry = createMatchEngineFactoryRegistry(matcher, comparisonFactory);
    if (matchEngineFactoryRegistry != null)
    {
      builder.setMatchEngineFactoryRegistry(matchEngineFactoryRegistry);
    }

    IDiffEngine diffEngine = createDiffEngine();
    if (diffEngine != null)
    {
      builder.setDiffEngine(diffEngine);
    }

    IReqEngine reqEngine = createRequirementEngine();
    if (reqEngine != null)
    {
      builder.setRequirementEngine(reqEngine);
    }

    IEquiEngine equiEngine = createEquivalenceEngine();
    if (equiEngine != null)
    {
      builder.setEquivalenceEngine(equiEngine);
    }

    IPostProcessor.Descriptor.Registry<?> registry = createPostProcessorRegistry();
    if (registry != null)
    {
      builder.setPostProcessorRegistry(registry);
    }

    IConflictDetector conflictDetector = createConflictDetector();
    if (conflictDetector != null)
    {
      builder.setConflictDetector(conflictDetector);
    }

    return builder.build();
  }

  protected IMatchEngine.Factory.Registry createMatchEngineFactoryRegistry(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
  {
    if (useRCPRegistries())
    {
      return null;
    }

    IMatchEngine.Factory.Registry registry = new MatchEngineFactoryRegistryImpl();
    registry.add(new CDOMatchEngine.Factory(matcher, comparisonFactory));
    return registry;
  }

  protected IDiffEngine createDiffEngine()
  {
    if (useRCPRegistries())
    {
      return null;
    }

    return new CDODiffEngine();
  }

  protected IReqEngine createRequirementEngine()
  {
    return null;
  }

  protected IEquiEngine createEquivalenceEngine()
  {
    return null;
  }

  protected IPostProcessor.Descriptor.Registry<?> createPostProcessorRegistry()
  {
    return null;
  }

  protected IConflictDetector createConflictDetector()
  {
    return null;
  }

  /**
   * @since 4.7
   */
  public static CDOCompare create()
  {
    Supplier<CDOCompare> supplier = SUPPLIER.get();
    if (supplier != null)
    {
      SUPPLIER.remove();
      return supplier.get();
    }

    return new CDOCompare();
  }

  /**
   * @since 4.7
   */
  public static void setCompareSupplier(Supplier<CDOCompare> supplier)
  {
    SUPPLIER.set(supplier);
  }

  /**
   * @deprecated As 4.7 use {@link CDOCompareUtil#getScope(Comparison)}.
   */
  @Deprecated
  public static IComparisonScope getScope(Comparison comparison)
  {
    return CDOCompareUtil.getScope(comparison);
  }

  /**
   * An {@link CDOIDFunction ID function} that considers the {@link CDOID}s of {@link CDOObject objects}.
   *
   * @author Eike Stepper
   */
  public static class CDOIDFunction implements Function<EObject, String>
  {
    @Override
    public String apply(EObject o)
    {
      CDOObject object = CDOUtil.getCDOObject(o);
      CDOID id = object.cdoID();

      StringBuilder builder = new StringBuilder();
      CDOIDUtil.write(builder, id);
      return builder.toString();
    }
  }

  /**
   * A {@link IEObjectMatcher matcher} that treats {@link Resource resources} as {@link EObject EObjects}.
   *
   * @author Eike Stepper
   * @since 4.3
   */
  public static class CDOMatcher extends IdentifierEObjectMatcher
  {
    private static final Method GETPARENTEOBJECT_METHOD;

    private final Function<EObject, String> idComputation;

    public CDOMatcher(Function<EObject, String> idComputation)
    {
      super(idComputation);
      this.idComputation = idComputation;
    }

    @Override
    protected EObject getParentEObject(EObject eObject)
    {
      return CDOElement.getParentOf(eObject);
    }

    @Override
    protected Set<Match> matchPerId(Iterator<? extends EObject> leftEObjects, Iterator<? extends EObject> rightEObjects,
        Iterator<? extends EObject> originEObjects, List<EObject> leftEObjectsNoID, List<EObject> rightEObjectsNoID, List<EObject> originEObjectsNoID)
    {
      if (GETPARENTEOBJECT_METHOD == null)
      {
        return matchPerIdCompatibility(leftEObjects, rightEObjects, originEObjects, leftEObjectsNoID, rightEObjectsNoID, originEObjectsNoID);
      }

      return super.matchPerId(leftEObjects, rightEObjects, originEObjects, leftEObjectsNoID, rightEObjectsNoID, originEObjectsNoID);
    }

    private Set<Match> matchPerIdCompatibility( //
        Iterator<? extends EObject> leftEObjects, //
        Iterator<? extends EObject> rightEObjects, //
        Iterator<? extends EObject> originEObjects, //
        List<EObject> leftEObjectsNoID, //
        List<EObject> rightEObjectsNoID, //
        List<EObject> originEObjectsNoID)
    {
      final List<EObject> leftEObjectsNoID1 = leftEObjectsNoID;
      final List<EObject> rightEObjectsNoID1 = rightEObjectsNoID;
      final List<EObject> originEObjectsNoID1 = originEObjectsNoID;
      final Set<Match> matches = Sets.newLinkedHashSet();

      // This lookup map will be used by iterations on right and origin to find the match in which they
      // should add themselves
      final Map<String, Match> idToMatch = Maps.newHashMap();

      // We will try and mimic the structure of the input model.
      // These map do not need to be ordered, we only need fast lookup.
      final Map<EObject, Match> leftEObjectsToMatch = Maps.newHashMap();
      final Map<EObject, Match> rightEObjectsToMatch = Maps.newHashMap();
      final Map<EObject, Match> originEObjectsToMatch = Maps.newHashMap();

      // We'll only iterate once on each of the three sides, building the matches as we go
      while (leftEObjects.hasNext())
      {
        final EObject left = leftEObjects.next();

        final String identifier = idComputation.apply(left);
        if (identifier != null)
        {
          final Match match = CompareFactory.eINSTANCE.createMatch();
          match.setLeft(left);

          // Can we find a parent? Assume we're iterating in containment order
          final EObject parentEObject = getParentEObject(left);
          final Match parent = leftEObjectsToMatch.get(parentEObject);
          if (parent != null)
          {
            ((InternalEList<Match>)parent.getSubmatches()).addUnique(match);
          }
          else
          {
            matches.add(match);
          }

          idToMatch.put(identifier, match);
          leftEObjectsToMatch.put(left, match);
        }
        else
        {
          leftEObjectsNoID1.add(left);
        }
      }

      while (rightEObjects.hasNext())
      {
        final EObject right = rightEObjects.next();

        // Do we have an existing match?
        final String identifier = idComputation.apply(right);
        if (identifier != null)
        {
          Match match = idToMatch.get(identifier);
          if (match != null)
          {
            match.setRight(right);

            rightEObjectsToMatch.put(right, match);
          }
          else
          {
            // Otherwise, create and place it.
            match = CompareFactory.eINSTANCE.createMatch();
            match.setRight(right);

            // Can we find a parent?
            final EObject parentEObject = getParentEObject(right);
            final Match parent = rightEObjectsToMatch.get(parentEObject);
            if (parent != null)
            {
              ((InternalEList<Match>)parent.getSubmatches()).addUnique(match);
            }
            else
            {
              matches.add(match);
            }

            rightEObjectsToMatch.put(right, match);
            idToMatch.put(identifier, match);
          }
        }
        else
        {
          rightEObjectsNoID1.add(right);
        }
      }

      while (originEObjects.hasNext())
      {
        final EObject origin = originEObjects.next();

        // Do we have an existing match?
        final String identifier = idComputation.apply(origin);
        if (identifier != null)
        {
          Match match = idToMatch.get(identifier);
          if (match != null)
          {
            match.setOrigin(origin);

            originEObjectsToMatch.put(origin, match);
          }
          else
          {
            // Otherwise, create and place it.
            match = CompareFactory.eINSTANCE.createMatch();
            match.setOrigin(origin);

            // Can we find a parent?
            final EObject parentEObject = getParentEObject(origin);
            final Match parent = originEObjectsToMatch.get(parentEObject);
            if (parent != null)
            {
              ((InternalEList<Match>)parent.getSubmatches()).addUnique(match);
            }
            else
            {
              matches.add(match);
            }

            idToMatch.put(identifier, match);
            originEObjectsToMatch.put(origin, match);
          }
        }
        else
        {
          originEObjectsNoID1.add(origin);
        }
      }

      return matches;
    }

    static
    {
      Method method = null;

      try
      {
        method = ReflectUtil.getMethod(IdentifierEObjectMatcher.class, "getParentEObject", EObject.class);
      }
      catch (Throwable ex)
      {
        //$FALL-THROUGH$
      }

      GETPARENTEOBJECT_METHOD = method;
    }
  }

  /**
   * A {@link DefaultMatchEngine match engine} that treats {@link Resource resources} as {@link EObject EObjects}.
   *
   * @author Eike Stepper
   */
  public static class CDOMatchEngine extends DefaultMatchEngine
  {
    /**
     * @since 4.6
     */
    public CDOMatchEngine(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
    {
      super(matcher, comparisonFactory);
    }

    @Override
    protected void match(Comparison comparison, IComparisonScope scope, final Notifier left, final Notifier right, final Notifier origin, Monitor monitor)
    {
      // Omit special treatment of Resources (and ResourceSets). Just match EObjects.
      match(comparison, scope, (EObject)left, (EObject)right, (EObject)origin, monitor);
    }

    /**
     * Creates {@link CDOMatchEngine match engine} instances.
     *
     * @author Eike Stepper
     */
    public static class Factory implements IMatchEngine.Factory
    {
      private final IMatchEngine matchEngine;

      private int ranking;

      /**
       * Default factory configuration used by EMF Compare diff engine factory extension point
       *
       * @since 4.6
       */
      public Factory()
      {
        CDOCompare compare = CDOCompare.create();
        CDOIDFunction idFunction = compare.createIDFunction();
        IdentifierEObjectMatcher matcher = compare.createMatcher(idFunction);
        IEqualityHelperFactory equalityHelperFactory = compare.createEqualityHelperFactory();
        IComparisonFactory comparisonFactory = compare.createComparisonFactory(equalityHelperFactory);
        matchEngine = createMatchEngine(matcher, comparisonFactory);
      }

      public Factory(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
      {
        matchEngine = createMatchEngine(matcher, comparisonFactory);
      }

      protected Factory(IMatchEngine matchEngine)
      {
        this.matchEngine = matchEngine;
      }

      protected CDOMatchEngine createMatchEngine(IEObjectMatcher matcher, IComparisonFactory comparisonFactory)
      {
        return new CDOMatchEngine(matcher, comparisonFactory);
      }

      @Override
      public IMatchEngine getMatchEngine()
      {
        return matchEngine;
      }

      @Override
      public int getRanking()
      {
        return ranking;
      }

      @Override
      public void setRanking(int ranking)
      {
        this.ranking = ranking;
      }

      @Override
      public boolean isMatchEngineFactoryFor(IComparisonScope scope)
      {
        return scope instanceof CDOComparisonScope;
      }
    }
  }

  /**
   * A {@link IDiffEngine diff engine} that {@link #createFeatureFilter() creates} and uses CDO-specific {@link FeatureFilter feature filters}.
   *
   * @author Eike Stepper
   * @since 4.5
   * @see CDOFeatureFilter
   */
  public static class CDODiffEngine extends DefaultDiffEngine
  {
    public CDODiffEngine()
    {
    }

    public CDODiffEngine(IDiffProcessor processor)
    {
      super(processor);
    }

    @Override
    protected FeatureFilter createFeatureFilter()
    {
      return new CDOFeatureFilter();
    }
  }

  /**
   * A CDO-specific {@link FeatureFilter feature filter} that uses {@link EMFUtil#isPersistent(EStructuralFeature) EMFUtil.isPersistent()}
   * to determine whether a {@link EStructuralFeature feature} is {@link #isTransient(EStructuralFeature) transient}, or not.
   *
   * @author Eike Stepper
   * @since 4.5
   */
  public static class CDOFeatureFilter extends FeatureFilter
  {
    public CDOFeatureFilter()
    {
    }

    /**
     * TODO Remove this copied method when EMFCompare bug 570625 is resolved and released.
     */
    @Override
    protected boolean isIgnoredAttribute(EAttribute attribute)
    {
      return attribute == null || attribute.isDerived() || isTransient(attribute);
    }

    /**
     * @since 4.5
     */
    @Override
    protected boolean isTransient(EStructuralFeature feature)
    {
      return !EMFUtil.isPersistent(feature);
    }
  }
}

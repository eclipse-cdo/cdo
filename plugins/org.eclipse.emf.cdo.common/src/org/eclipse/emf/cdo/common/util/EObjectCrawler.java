/*
 * Copyright (c) 2025 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.common.util;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.common.model.CDOModelUtil;
import org.eclipse.emf.cdo.common.model.CDOType;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.ContainmentProxyStrategy;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.FeatureStrategy;
import org.eclipse.emf.cdo.common.revision.CDORevisionCrawler.Handler;
import org.eclipse.emf.cdo.common.revision.CDORevisionFactory;
import org.eclipse.emf.cdo.spi.common.model.InternalCDOClassInfo;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.lifecycle.Lifecycle;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.InternalEObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A crawler for arbitrary {@link EObject EObjects} that creates {@link CDORevision CDORevisions} on the fly and
 * feeds them to a {@link CDORevisionCrawler}.
 * <p>
 * The {@link ModelScope} inner class allows to register EObjects and then create an {@link EObjectCrawler} for the registered
 * objects.
 * <p>
 * Example usage:
 * <pre>
 * MessageDigest digest = MessageDigest.getInstance("SHA-1");
 * Handler handler = new CDORevisionCrawler.MessageDigestHandler(digest);
 *
 * ModelScope scope = new ModelScope();
 * scope.registerObject(EcorePackage.eINSTANCE, true);
 *
 * EObjectCrawler crawler = scope.createCrawler().handler(handler);
 * crawler.begin();
 * crawler.addObject(EcorePackage.Literals.EOBJECT);
 * crawler.finish();
 *
 * System.out.println(HexUtil.bytesToHex(digest.digest()));
 * </pre>
 * The example above registers the <code>EcorePackage</code> and all its contents in a model scope, creates an <code>EObjectCrawler</code> for
 * the scope, sets a handler that computes a SHA-1 message digest, begins the crawling session, adds the <code>EObject</code> {@link EClass class}
 * (which causes it to be crawled according to the default feature strategy), and finally finishes the crawling session.
 * <p>
 * At this point, the <code>digest</code> contains the SHA-1 message digest of the binary representation of all the
 * CDORevisions that were crawled.
 *
 * @author Eike Stepper
 * @since 4.27
 */
public final class EObjectCrawler extends Lifecycle
{
  private final ModelScope scope;

  private final Map<CDOID, CDORevision> revisions = new HashMap<>();

  private final CDORevisionFactory revisionFactory;

  private final CDORevisionCrawler revisionCrawler = new CDORevisionCrawler().revisionProvider(this::getRevision);

  private EObjectCrawler(ModelScope scope, CDORevisionFactory revisionFactory)
  {
    this.scope = Objects.requireNonNull(scope);
    this.revisionFactory = ObjectUtil.requireNonNullElse(revisionFactory, CDORevisionFactory.DEFAULT);
  }

  public Handler handler()
  {
    return revisionCrawler.handler();
  }

  public EObjectCrawler handler(Handler handler)
  {
    checkInactive();
    revisionCrawler.handler(handler);
    return this;
  }

  public ContainmentProxyStrategy containmentProxyStrategy()
  {
    return revisionCrawler.containmentProxyStrategy();
  }

  public EObjectCrawler containmentProxyStrategy(ContainmentProxyStrategy containmentProxyStrategy)
  {
    checkInactive();
    revisionCrawler.containmentProxyStrategy(containmentProxyStrategy);
    return this;
  }

  public FeatureStrategy featureStrategy()
  {
    return revisionCrawler.featureStrategy();
  }

  public EObjectCrawler featureStrategy(FeatureStrategy featureStrategy)
  {
    checkInactive();
    revisionCrawler.featureStrategy(featureStrategy);
    return this;
  }

  public long objectCount()
  {
    return revisionCrawler.revisionCount();
  }

  public EObjectCrawler addObject(EObject object)
  {
    checkActive();

    CDORevision revision = getRevision(object);
    if (object == null)
    {
      throw new IllegalStateException("No revision for object: " + object);
    }

    revisionCrawler.addRevision(revision);
    return this;
  }

  public EObjectCrawler begin()
  {
    activate();
    return this;
  }

  public EObjectCrawler finish()
  {
    deactivate();
    return this;
  }

  @Override
  protected void doActivate() throws Exception
  {
    revisionCrawler.begin();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    revisionCrawler.finish();
  }

  private CDORevision getRevision(EObject object)
  {
    CDOID id = scope.ids.get(object);
    if (id != null)
    {
      return getRevision(id);
    }

    throw new IllegalStateException("No revision for object: " + object);
  }

  private CDORevision getRevision(CDOID id)
  {
    return revisions.computeIfAbsent(id, this::createRevision);
  }

  private CDORevision createRevision(CDOID id)
  {
    InternalEObject object = (InternalEObject)scope.objects.get(id);
    if (object != null)
    {
      InternalCDORevision revision = (InternalCDORevision)revisionFactory.createRevision(object.eClass());
      revision.setID(id);

      InternalCDOClassInfo classInfo = revision.getClassInfo();
      EStructuralFeature[] allPersistentFeatures = classInfo.getAllPersistentFeatures();

      for (int i = 0, length = allPersistentFeatures.length; i < length; i++)
      {
        EStructuralFeature feature = allPersistentFeatures[i];

        Object setting = object.eGet(feature);
        featureToRevision(object, revision, feature, setting);
      }

      revision.setUnchunked();
      return revision;
    }

    throw new IllegalStateException("No revision for id: " + id);
  }

  private void featureToRevision(InternalEObject object, InternalCDORevision revision, EStructuralFeature feature, Object setting)
  {
    if (feature.isMany())
    {
      if (setting != null)
      {
        @SuppressWarnings("unchecked")
        EList<Object> instanceList = (EList<Object>)setting;
        int size = instanceList.size();

        if (feature.isUnsettable())
        {
          if (!object.eIsSet(feature))
          {
            // Avoid list creation for unset lists.
            return;
          }
        }
        else
        {
          if (size == 0)
          {
            // Avoid list creation for empty lists that can't be unset.
            return;
          }
        }

        // Get (and possibly create) the list here in order to support unsettable empty lists.
        CDOList revisionList = revision.getOrCreateList(feature, size);

        for (Object value : instanceList)
        {
          Object cdoValue = convertToCDO(feature, value);
          revisionList.add(cdoValue);
        }
      }
    }
    else
    {
      Object cdoValue = convertToCDO(feature, setting);
      revision.set(feature, 0, cdoValue);
    }
  }

  private Object convertToCDO(EStructuralFeature feature, Object value)
  {
    if (feature instanceof EReference)
    {
      if (value instanceof EObject)
      {
        CDOID id = scope.ids.get(value);
        if (id == null)
        {
          throw new IllegalStateException("No id for object: " + value);
        }

        value = id;
      }
    }
    else
    {
      CDOType type = CDOModelUtil.getType(feature.getEType());
      if (type != null)
      {
        value = type.convertToCDO(feature.getEType(), value);
      }
    }

    return value;
  }

  /**
   * A model scope for registering EObjects and creating an {@link EObjectCrawler} for them.
   * <p>
   * Example usage:
   * <pre>
   * ModelScope scope = new ModelScope();
   * scope.registerObject(rootEObject, true);
   *
   * EObjectCrawler crawler = scope.createCrawler();
   * </pre>
   * <p>
   * The example above registers the <code>rootEObject</code> and all its contents in a model scope, and then creates an
   * <code>EObjectCrawler</code> for the scope.
   * <p>
   * Note that once the {@link #createCrawler() crawler} has been created, the model scope is frozen and no further objects
   * can be registered.
   * <p>
   * The model scope can be used to create multiple {@link EObjectCrawler crawlers}.
   *
   * @author Eike Stepper
   */
  public static final class ModelScope
  {
    private final Map<EObject, CDOID> ids = new HashMap<>();

    private final Map<CDOID, EObject> objects = new HashMap<>();

    private long lastID;

    private boolean frozen;

    public ModelScope()
    {
    }

    public ModelScope registerObject(EObject object)
    {
      if (frozen)
      {
        throw new IllegalStateException("Model scope is frozen");
      }

      if (object != null)
      {
        ids.computeIfAbsent(object, key -> {
          CDOID id = CDOIDUtil.createLong(++lastID);
          objects.put(id, object);
          return id;
        });
      }

      return this;

    }

    public ModelScope registerObject(EObject object, boolean recursive)
    {
      if (object != null)
      {
        registerObject(object);

        if (recursive)
        {
          for (TreeIterator<EObject> it = object.eAllContents(); it.hasNext();)
          {
            EObject child = it.next();
            registerObject(child);
          }
        }
      }

      return this;
    }

    public EObjectCrawler createCrawler()
    {
      return createCrawler(null);
    }

    public EObjectCrawler createCrawler(CDORevisionFactory revisionFactory)
    {
      frozen = true;
      return new EObjectCrawler(this, revisionFactory);
    }
  }
}

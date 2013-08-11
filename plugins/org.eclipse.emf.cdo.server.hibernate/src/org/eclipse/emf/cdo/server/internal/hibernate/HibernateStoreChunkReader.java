/*
 * Copyright (c) 2008, 2009, 2011, 2012 Eike Stepper (Berlin, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - Implementation
 */
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.revision.CDOList;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStoreChunkReader;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.WrappedHibernateList;
import org.eclipse.emf.cdo.spi.common.revision.InternalCDORevision;
import org.eclipse.emf.cdo.spi.server.StoreChunkReader;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.collection.QueryableCollection;

import java.util.List;

/**
 * @author Eike Stepper
 */
public class HibernateStoreChunkReader extends StoreChunkReader implements IHibernateStoreChunkReader
{
  public HibernateStoreChunkReader(HibernateStoreAccessor accessor, CDORevision revision, EStructuralFeature feature)
  {
    super(accessor, revision, feature);
  }

  @Override
  public HibernateStoreAccessor getAccessor()
  {
    return (HibernateStoreAccessor)super.getAccessor();
  }

  public List<Chunk> executeRead()
  {
    // get a transaction, the hibernateStoreAccessor is placed in a threadlocal
    // so all db access uses the same session.
    final Session session = getAccessor().getHibernateSession();

    // reread the revision as it is probably unreferenced
    final InternalCDORevision latestRevision = getLatestRevision(session);
    Object value = latestRevision.getValue(getFeature());
    if (value instanceof WrappedHibernateList)
    {
      value = ((WrappedHibernateList)value).getDelegate();
    }

    // hibernate details...
    boolean useExtraLazyMode = false;
    boolean standardCDOList = false;
    QueryableCollection persister = null;
    CollectionEntry entry = null;
    if (value instanceof PersistentCollection)
    {
      final PersistentCollection persistentCollection = (PersistentCollection)value;
      persister = (QueryableCollection)((SessionFactoryImplementor)session.getSessionFactory())
          .getCollectionPersister(persistentCollection.getRole());
      entry = ((SessionImplementor)session).getPersistenceContext().getCollectionEntry(persistentCollection);

      useExtraLazyMode = !persister.getElementType().isEntityType();
      if (useExtraLazyMode && ((PersistentCollection)value).hasQueuedOperations())
      {
        session.flush();
      }
    }
    else
    {
      standardCDOList = true;
    }

    final List<Chunk> chunks = getChunks();
    for (Chunk chunk : chunks)
    {
      final int startIndex = chunk.getStartIndex();
      final int maxElements = chunk.size();
      if (standardCDOList)
      {
        // for eattributes just read them all, no chunking there...
        final CDOList list = (CDOList)value;
        if (startIndex >= list.size())
        {
          return chunks;
        }
        for (int i = startIndex; i < startIndex + maxElements; i++)
        {
          if (i >= list.size())
          {
            break;
          }
          addToChunk(chunk, i - startIndex, list.get(i));
        }
      }
      else if (useExtraLazyMode)
      {
        if (getFeature() instanceof EReference)
        {
          for (int i = startIndex; i < startIndex + maxElements; i++)
          {
            final Object object = persister.getElementByIndex(entry.getLoadedKey(), i, (SessionImplementor)session,
                latestRevision);
            // could happen if the index > size)
            if (object == null)
            {
              continue;
            }
            addToChunk(chunk, i - startIndex, object);
          }
        }
        else
        {
          // for eattributes just read them all, no chunking there...
          final List<?> list = (List<?>)value;
          if (startIndex >= list.size())
          {
            return chunks;
          }
          for (int i = startIndex; i < startIndex + maxElements; i++)
          {
            if (i >= list.size())
            {
              break;
            }
            addToChunk(chunk, i - startIndex, list.get(i));
          }
        }
      }
      else
      {
        final Query filterQuery = session.createFilter(value, "");
        filterQuery.setMaxResults(maxElements);
        filterQuery.setFirstResult(startIndex);
        int i = 0;
        for (Object object : filterQuery.list())
        {
          addToChunk(chunk, i++, object);
        }
      }
    }
    return chunks;
  }

  private InternalCDORevision getLatestRevision(Session session)
  {
    final CDOID id = getRevision().getID();

    final HibernateStore store = getAccessor().getStore();

    if (store.isAuditing() && store.getHibernateAuditHandler().getCDOAuditHandler().isAudited(id))
    {
      InternalCDORevision revision = store.getHibernateAuditHandler().readRevision(session, id,
          getRevision().getTimeStamp());
      // found one, use it
      if (revision != null)
      {
        return revision;
      }
    }

    return HibernateUtil.getInstance().getCDORevision(id);
  }

  private void addToRevisionCache(Object revision)
  {
    final InternalCDORevision internalRevision = (InternalCDORevision)revision;
    for (EStructuralFeature feature : internalRevision.getEClass().getEAllStructuralFeatures())
    {
      if (!isMappedFeature(internalRevision, feature))
      {
        continue;
      }

      if (feature.isMany() || feature instanceof EReference)
      {
        final Object value = internalRevision.getValue(feature);
        if (value instanceof WrappedHibernateList)
        {
          // force the size to be cached
          ((WrappedHibernateList)value).size();
        }
      }
    }

    getAccessor().addToRevisionCache(revision);
  }

  private void addToChunk(Chunk chunk, int i, Object object)
  {
    if (object instanceof CDORevision)
    {
      addToRevisionCache(object);
      chunk.add(i, HibernateUtil.getInstance().getCDOID(object));
    }
    else
    {
      chunk.add(i, object);
    }
  }

  private boolean isMappedFeature(InternalCDORevision revision, EStructuralFeature feature)
  {
    try
    {
      int featureID = revision.getClassInfo().getEClass().getFeatureID(feature);
      revision.getClassInfo().getPersistentFeatureIndex(featureID);
      return true;
    }
    catch (IllegalArgumentException ex)
    {
      return false;
    }
    catch (ArrayIndexOutOfBoundsException ex)
    {
      return false;
    }
  }

}

/***************************************************************************
 * Copyright (c) 2004 - 2008 Eike Stepper, Germany, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Martin Taal - moved cdopackage handler to other class, changed configuration
 **************************************************************************/
package org.eclipse.emf.cdo.server.internal.hibernate;

import org.eclipse.emf.cdo.common.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.common.id.CDOIDLibraryProvider;
import org.eclipse.emf.cdo.common.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.common.revision.CDORevision;
import org.eclipse.emf.cdo.internal.server.Store;
import org.eclipse.emf.cdo.internal.server.StoreAccessorPool;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.ITransaction;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateLibraryHandler;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;
import org.eclipse.emf.cdo.server.internal.hibernate.tuplizer.CDOInterceptor;
import org.eclipse.emf.cdo.spi.common.id.CDOIDLibraryProviderImpl;

import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;
import org.eclipse.net4j.util.om.trace.ContextTracer;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import java.io.InputStream;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernateStore extends Store implements IHibernateStore
{
  public static final String TYPE = "hibernate";

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateStore.class);

  private static final CDOIDObjectFactory CDOID_OBJECT_FACTORY = new CDOIDHibernateFactoryImpl();

  private static final IDLibraryProvider CDOID_LIBRARY_PROVIDER = new IDLibraryProvider();

  private static final CDOIDLibraryDescriptor CDOID_LIBRARY_DESCRIPTOR = CDOID_LIBRARY_PROVIDER
      .createDescriptor(CDOIDHibernateFactoryImpl.class.getName());

  /**
   * Used to give different extensions of Hibernate a context when initializing
   */
  private static ThreadLocal<HibernateStore> currentHibernateStore = new ThreadLocal<HibernateStore>();

  private Configuration hibernateConfiguration;

  private SessionFactory hibernateSessionFactory;

  private HibernatePackageHandler packageHandler;

  private IHibernateMappingProvider mappingProvider;

  private boolean doDropSchema = false;

  public HibernateStore(IHibernateMappingProvider mappingProvider)
  {
    super(TYPE, set(ChangeFormat.REVISION), set(RevisionTemporality.NONE), set(RevisionParallelism.NONE));
    this.mappingProvider = mappingProvider;
    packageHandler = new HibernatePackageHandler(this);

    if (TRACER.isEnabled() && mappingProvider != null)
    {
      TRACER.trace("HibernateStore with mappingProvider " + mappingProvider.getClass().getName());
    }
  }

  public Configuration getHibernateConfiguration()
  {
    return hibernateConfiguration;
  }

  public synchronized SessionFactory getHibernateSessionFactory()
  {
    if (hibernateSessionFactory == null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Initializing SessionFactory for HibernateStore");
      }

      currentHibernateStore.set(this);

      try
      {
        initConfiguration();
        hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();
      }
      finally
      {
        currentHibernateStore.set(null);
      }
    }

    return hibernateSessionFactory;
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return CDOID_OBJECT_FACTORY;
  }

  public CDOIDLibraryDescriptor getCDOIDLibraryDescriptor()
  {
    return CDOID_LIBRARY_DESCRIPTOR;
  }

  public CDOIDLibraryProvider getCDOIDLibraryProvider()
  {
    return CDOID_LIBRARY_PROVIDER;
  }

  @Override
  public HibernateStoreAccessor createReader(ISession session)
  {
    return new HibernateStoreAccessor(this, session);
  }

  @Override
  public HibernateStoreAccessor createWriter(ITransaction transaction)
  {
    return new HibernateStoreAccessor(this, transaction);
  }

  public synchronized int getNextPackageID()
  {
    return packageHandler.getNextPackageID();
  }

  public synchronized int getNextClassID()
  {
    return packageHandler.getNextClassID();
  }

  public synchronized int getNextFeatureID()
  {
    return packageHandler.getNextFeatureID();
  }

  public boolean wasCrashed()
  {
    return false;
  }

  public void repairAfterCrash()
  {
    throw new UnsupportedOperationException(); // TODO Implement me
  }

  public long getCreationTime()
  {
    return CDORevision.UNSPECIFIED_DATE;
  }

  public HibernatePackageHandler getPackageHandler()
  {
    return packageHandler;
  }

  // TODO: synchronize??
  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    packageHandler.activate();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    if (hibernateSessionFactory != null)
    {
      if (TRACER.isEnabled())
      {
        TRACER.trace("Closing SessionFactory");
      }

      hibernateSessionFactory.close();
      hibernateSessionFactory = null;
    }
    // and now do the drop action
    if (doDropSchema)
    {
      final Configuration conf = getHibernateConfiguration();
      final SchemaExport se = new SchemaExport(conf);
      se.drop(false, true);
    }

    packageHandler.deactivate();

    if (doDropSchema)
    {
      packageHandler.doDropSchema();
    }
    super.doDeactivate();
  }

  @Override
  protected StoreAccessorPool getReaderPool(ISession session, boolean forReleasing)
  {
    // TODO Consider usings multiple pools for readers (e.g. bound to the session context)
    return null;
  }

  @Override
  protected StoreAccessorPool getWriterPool(IView view, boolean forReleasing)
  {
    // TODO Consider usings multiple pools for writers (e.g. bound to the session context)
    return null;
  }

  // is called after a new package has been added
  // TODO: synchronize??
  // TODO: combine with doActivate/doDeactivate??
  // TODO: assumes that packageHandler has been reset
  protected void reInitialize()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Re-Initializing HibernateStore");
    }

    if (hibernateSessionFactory != null)
    {
      if (!hibernateSessionFactory.isClosed())
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Closing SessionFactory");
        }
        hibernateSessionFactory.close();
      }

      hibernateSessionFactory = null;
    }
  }

  protected void initConfiguration()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Initializing Configuration");
    }

    InputStream in = null;

    try
    {
      hibernateConfiguration = new Configuration();
      if (mappingProvider != null)
      {
        mappingProvider.setHibernateStore(this);
        mappingProvider.addMapping(hibernateConfiguration);
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Adding resource.hbm.xml to configuration");
      }

      in = OM.BUNDLE.getInputStream("mappings/resource.hbm.xml");
      hibernateConfiguration.addInputStream(in);
      hibernateConfiguration.setInterceptor(new CDOInterceptor());
      hibernateConfiguration.setProperties(HibernateUtil.getInstance().getPropertiesFromStore(this));

      // prevent the drop on close because the sessionfactory is also closed when
      // new packages are written to the db, so only do a real drop at deactivate
      if (hibernateConfiguration.getProperty(Environment.HBM2DDL_AUTO) != null
          && hibernateConfiguration.getProperty(Environment.HBM2DDL_AUTO).startsWith("create"))
      {
        doDropSchema = true;
        // note that the value create also re-creates the db and drops the old one
        hibernateConfiguration.setProperty(Environment.HBM2DDL_AUTO, "update");
      }
      else
      {
        doDropSchema = false;
      }
    }
    catch (Exception ex)
    {
      throw WrappedException.wrap(ex);
    }
    finally
    {
      IOUtil.close(in);
    }
  }

  public static HibernateStore getCurrentHibernateStore()
  {
    return currentHibernateStore.get();
  }

  /**
   * @author Eike Stepper
   */
  private static final class IDLibraryProvider extends CDOIDLibraryProviderImpl
  {
    public IDLibraryProvider()
    {
      addLibrary(CDOIDHibernateLibraryHandler.LIBRARY_NAME,
          org.eclipse.emf.cdo.server.hibernate.internal.id.bundle.OM.BUNDLE);
    }
  }
}

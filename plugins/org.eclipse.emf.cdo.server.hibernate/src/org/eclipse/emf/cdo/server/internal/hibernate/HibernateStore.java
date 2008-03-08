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

import org.eclipse.emf.cdo.internal.protocol.id.CDOIDLibraryDescriptorImpl;
import org.eclipse.emf.cdo.internal.server.Store;
import org.eclipse.emf.cdo.protocol.id.CDOIDLibraryDescriptor;
import org.eclipse.emf.cdo.protocol.id.CDOIDLibraryProvider;
import org.eclipse.emf.cdo.protocol.id.CDOIDObjectFactory;
import org.eclipse.emf.cdo.protocol.model.CDOPackage;
import org.eclipse.emf.cdo.server.ISession;
import org.eclipse.emf.cdo.server.IView;
import org.eclipse.emf.cdo.server.hibernate.IHibernateMappingProvider;
import org.eclipse.emf.cdo.server.hibernate.IHibernateStore;
import org.eclipse.emf.cdo.server.hibernate.internal.id.CDOIDHibernateFactoryImpl;
import org.eclipse.emf.cdo.server.internal.hibernate.bundle.OM;

import org.eclipse.net4j.internal.util.om.trace.ContextTracer;
import org.eclipse.net4j.util.WrappedException;
import org.eclipse.net4j.util.io.IOUtil;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Eike Stepper
 * @author Martin Taal
 */
public class HibernateStore extends Store implements IHibernateStore
{
  public static final String TYPE = "hibernate";

  private static final ContextTracer TRACER = new ContextTracer(OM.DEBUG, HibernateStore.class);

  private static final CDOIDObjectFactory CDOID_OBJECT_FACTORY = new CDOIDHibernateFactoryImpl();

  private static final IDLibraryHandler CDOID_LIBRARY_HANDLER = new IDLibraryHandler();

  /**
   * Used to give different extensions of Hibernate a context when initializing
   */
  private static ThreadLocal<HibernateStore> currentHibernateStore = new ThreadLocal<HibernateStore>();

  private Configuration hibernateConfiguration;

  private SessionFactory hibernateSessionFactory;

  private HibernatePackageHandler packageHandler;

  private IHibernateMappingProvider mappingProvider;

  public HibernateStore(IHibernateMappingProvider mappingProvider)
  {
    super(TYPE);
    this.mappingProvider = mappingProvider;
    packageHandler = new HibernatePackageHandler(this);

    if (TRACER.isEnabled() && mappingProvider != null)
    {
      TRACER.trace("With mappingProvider " + mappingProvider.getClass().getName());
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
        hibernateSessionFactory = hibernateConfiguration.buildSessionFactory();
      }
      finally
      {
        currentHibernateStore.set(null);
      }
    }

    return hibernateSessionFactory;
  }

  public boolean hasAuditingSupport()
  {
    return false;
  }

  public boolean hasBranchingSupport()
  {
    return false;
  }

  public boolean hasWriteDeltaSupport()
  {
    return false;
  }

  public CDOIDObjectFactory getCDOIDObjectFactory()
  {
    return CDOID_OBJECT_FACTORY;
  }

  public CDOIDLibraryDescriptor getCDOIDLibraryDescriptor()
  {
    return CDOID_LIBRARY_HANDLER;
  }

  public CDOIDLibraryProvider getCDOIDLibraryProvider()
  {
    return CDOID_LIBRARY_HANDLER;
  }

  @Override
  public HibernateStoreReader getReader(ISession session)
  {
    return (HibernateStoreReader)super.getReader(session);
  }

  @Override
  public HibernateStoreReader createReader(ISession session)
  {
    return new HibernateStoreReader(this, session);
  }

  @Override
  public HibernateStoreWriter getWriter(IView view)
  {
    return (HibernateStoreWriter)super.getWriter(view);
  }

  @Override
  public HibernateStoreWriter createWriter(IView view)
  {
    return new HibernateStoreWriter(this, view);
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

  public HibernatePackageHandler getPackageHandler()
  {
    return packageHandler;
  }

  // TODO: synchronize??
  @Override
  protected void doActivate() throws Exception
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Activating HibernateStore");
    }

    super.doActivate();

    // Activate the package store
    packageHandler.doActivate();

    initConfiguration();
    initSchema();
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    TRACER.trace("De-Activating HibernateStore");
    if (hibernateSessionFactory != null)
    {
      TRACER.trace("Closing SessionFactory");
      hibernateSessionFactory.close();
      hibernateSessionFactory = null;
    }

    packageHandler.doDeactivate();

    super.doDeactivate();
  }

  // is called after a new package has been added
  // TODO: synchronize??
  // TODO: combine with doActivate/doDeactivate??
  // TODO: assumes that packageHandler has been reset
  protected void reInitialize()
  {
    TRACER.trace("Re-Initializing HibernateStore");
    if (hibernateSessionFactory != null)
    {
      if (!hibernateSessionFactory.isClosed())
      {
        TRACER.trace("Closing SessionFactory");
        hibernateSessionFactory.close();
      }
      hibernateSessionFactory = null;
    }
    initConfiguration();
    initSchema();
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
      final Collection<CDOPackage> cdoPackages = getPackageHandler().getCDOPackages();
      final Collection<Object> ecoreStrs = new ArrayList<Object>();

      hibernateConfiguration = new Configuration();

      final String mapping;

      if (cdoPackages.size() > 0)
      {
        if (TRACER.isEnabled())
        {
          TRACER.trace("Mapping ecore to hibernate for CDOPackages:");
        }

        for (CDOPackage cdoPackage : cdoPackages)
        {
          if (TRACER.isEnabled())
          {
            TRACER.trace("adding ecore for CDOPackage " + cdoPackage.getPackageURI());
          }

          ecoreStrs.add(cdoPackage.getEcore());
        }
        // DISABLED to prevent teneo dependency
        // mapping = mappingProvider.provideMapping(ecoreStrs, properties);
        // TRACER.trace(mapping);
        // System.err.println(mapping);
        mapping = null;
        in = OM.BUNDLE.getInputStream("mappings/product.hbm.xml");
        hibernateConfiguration.addInputStream(in);
      }
      else
      {
        mapping = null;
        if (TRACER.isEnabled())
        {
          TRACER.trace("No CDOPackages found, ecore not mapped to hibernate");
        }
      }

      if (TRACER.isEnabled())
      {
        TRACER.trace("Adding resource.hbm.xml to configuration");
      }

      in = OM.BUNDLE.getInputStream("mappings/resource.hbm.xml");
      hibernateConfiguration.addInputStream(in);
      if (mapping != null)
      {
        TRACER.trace("Adding generated mapping to configuration");
        hibernateConfiguration.addXML(mapping);
      }

      hibernateConfiguration.setProperties(HibernateUtil.getInstance().getPropertiesFromStore(this));
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

  protected void initSchema()
  {
    if (TRACER.isEnabled())
    {
      TRACER.trace("Updating db schema for HibernateStore");
    }

    new SchemaUpdate(hibernateConfiguration).execute(true, true);
  }

  public static HibernateStore getCurrentHibernateStore()
  {
    return currentHibernateStore.get();
  }

  /**
   * @author Eike Stepper
   */
  private static final class IDLibraryHandler extends CDOIDLibraryDescriptorImpl implements CDOIDLibraryProvider
  {
    public IDLibraryHandler()
    {
      super(CDOIDHibernateFactoryImpl.class.getName(), null);
    }

    public InputStream getContents(String libraryName) throws IOException
    {
      File library = getFile(libraryName);
      return IOUtil.openInputStream(library);
    }

    public int getSize(String libraryName)
    {
      File library = getFile(libraryName);
      return (int)library.length();
    }

    private File getFile(String libraryName)
    {
      return null;
    }
  }
}

/*
 * Copyright (c) 2018, 2019, 2021 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 *    Christian W. Damus (CEA LIST) - bug 418452
 */
package org.eclipse.emf.cdo.internal.common;

import org.eclipse.emf.cdo.common.CDOCommonRepository;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOID.ObjectType;
import org.eclipse.emf.cdo.common.util.CDOCommonUtil;
import org.eclipse.emf.cdo.internal.common.messages.Messages;

import org.eclipse.net4j.util.AdapterUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.properties.Properties;
import org.eclipse.net4j.util.properties.Property;

import org.eclipse.core.runtime.IProgressMonitor;

import java.util.Set;

/**
 * @author Eike Stepper
 */
public abstract class AbstractRepositoryProperties<RECEIVER> extends Properties<RECEIVER> implements CDOCommonRepository
{
  private static final String CATEGORY_REPOSITORY = "Repository"; //$NON-NLS-1$

  public AbstractRepositoryProperties(Class<RECEIVER> receiverType)
  {
    super(receiverType);
    initProperties();
  }

  protected abstract CDOCommonRepository getRepository(RECEIVER receiver);

  protected void initProperties()
  {
    add(new Property<RECEIVER>("repositoryName", Messages.getString("RepositoryPropertyTester_10"), //
        Messages.getString("RepositoryPropertyTester_11"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).getName();
      }
    });

    add(new Property<RECEIVER>("repositoryUUID", Messages.getString("RepositoryPropertyTester_12"), //
        Messages.getString("RepositoryPropertyTester_13"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).getUUID();
      }
    });

    add(new Property<RECEIVER>("repositoryType", Messages.getString("RepositoryPropertyTester_14"), //
        Messages.getString("RepositoryPropertyTester_15"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).getType();
      }
    });

    add(new Property<RECEIVER>("repositoryState", Messages.getString("RepositoryPropertyTester_16"), //
        Messages.getString("RepositoryPropertyTester_17"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).getState();
      }
    });

    add(new Property<RECEIVER>("repositoryCreationTime", Messages.getString("RepositoryPropertyTester_18"), //
        Messages.getString("RepositoryPropertyTester_19"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return CDOCommonUtil.formatTimeStamp(getRepository(receiver).getCreationTime());
      }
    });

    add(new Property<RECEIVER>("authenticating")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).isAuthenticating();
      }
    });

    add(new Property<RECEIVER>("supportingLoginPeeks")
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).isSupportingLoginPeeks();
      }
    });

    add(new Property<RECEIVER>("supportingAudits", Messages.getString("RepositoryPropertyTester_20"), //
        Messages.getString("RepositoryPropertyTester_21"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).isSupportingAudits();
      }
    });

    add(new Property<RECEIVER>("supportingBranches", Messages.getString("RepositoryPropertyTester_22"), //
        Messages.getString("RepositoryPropertyTester_23"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).isSupportingBranches();
      }
    });

    add(new Property<RECEIVER>("supportingUnits", Messages.getString("RepositoryPropertyTester_28"), //
        Messages.getString("RepositoryPropertyTester_29"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).isSupportingBranches();
      }
    });

    add(new Property<RECEIVER>("serializeCommits", Messages.getString("RepositoryPropertyTester_38"), //
        Messages.getString("RepositoryPropertyTester_39"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).isSerializingCommits();
      }
    });

    add(new Property<RECEIVER>("ensureReferentialIntegrity", Messages.getString("RepositoryPropertyTester_30"), //
        Messages.getString("RepositoryPropertyTester_31"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).isEnsuringReferentialIntegrity();
      }
    });

    add(new Property<RECEIVER>("authorizingOperations", Messages.getString("RepositoryPropertyTester_44"), //
        Messages.getString("RepositoryPropertyTester_45"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).isAuthorizingOperations();
      }
    });

    add(new Property<RECEIVER>("idGenerationLocation", Messages.getString("RepositoryPropertyTester_32"), //
        Messages.getString("RepositoryPropertyTester_33"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).getIDGenerationLocation();
      }
    });

    add(new Property<RECEIVER>("commitInfoStorage", Messages.getString("RepositoryPropertyTester_42"), //
        Messages.getString("RepositoryPropertyTester_43"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).getCommitInfoStorage();
      }
    });

    add(new Property<RECEIVER>("storeType", Messages.getString("RepositoryPropertyTester_24"), //
        Messages.getString("RepositoryPropertyTester_25"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        return getRepository(receiver).getStoreType();
      }
    });

    add(new Property<RECEIVER>("objectIDTypes", Messages.getString("RepositoryPropertyTester_26"), //
        Messages.getString("RepositoryPropertyTester_27"), //$NON-NLS-1$
        CATEGORY_REPOSITORY)
    {
      @Override
      protected Object eval(RECEIVER receiver)
      {
        StringBuilder builder = new StringBuilder();
        CDOCommonRepository repository = getRepository(receiver);
        for (CDOID.ObjectType objectIDType : repository.getObjectIDTypes())
        {
          StringUtil.appendSeparator(builder, ", "); //$NON-NLS-1$
          builder.append(objectIDType);
        }

        return builder;
      }
    });
  }

  @Override
  @SuppressWarnings({ "unchecked", "rawtypes" })
  public Object getAdapter(Class adapter)
  {
    return AdapterUtil.adapt(this, adapter, false);
  }

  @Override
  public long getTimeStamp()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getName()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getUUID()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type getType()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public State getState()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public long getCreationTime()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public String getStoreType()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public Set<ObjectType> getObjectIDTypes()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public IDGenerationLocation getIDGenerationLocation()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CommitInfoStorage getCommitInfoStorage()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public CDOID getRootResourceID()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAuthenticating()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSupportingAudits()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSupportingLoginPeeks()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSupportingBranches()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSupportingUnits()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  @Deprecated
  public boolean isSupportingEcore()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isSerializingCommits()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isEnsuringReferentialIntegrity()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean isAuthorizingOperations()
  {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean waitWhileInitial(IProgressMonitor monitor)
  {
    throw new UnsupportedOperationException();
  }
}

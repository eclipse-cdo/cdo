/*
 * Copyright (c) 2009-2012, 2015, 2019 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Simon McDuff - initial API and implementation
 *    Eike Stepper - maintenance
 */
package org.eclipse.emf.cdo.tests.bugzilla;

import org.eclipse.emf.cdo.CDOObject;
import org.eclipse.emf.cdo.common.protocol.CDOProtocolConstants;
import org.eclipse.emf.cdo.eresource.CDOResource;
import org.eclipse.emf.cdo.eresource.CDOResourceFactory;
import org.eclipse.emf.cdo.eresource.impl.CDOResourceImpl;
import org.eclipse.emf.cdo.session.CDOSession;
import org.eclipse.emf.cdo.tests.AbstractCDOTest;
import org.eclipse.emf.cdo.tests.model1.Company;
import org.eclipse.emf.cdo.transaction.CDOTransaction;
import org.eclipse.emf.cdo.util.CDOURIUtil;
import org.eclipse.emf.cdo.util.CDOUtil;
import org.eclipse.emf.cdo.view.CDOView;
import org.eclipse.emf.cdo.view.CDOViewProvider;
import org.eclipse.emf.cdo.view.CDOViewProviderRegistry;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.impl.AdapterImpl;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.Resource.Factory.Registry;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Attaching adapters to resources does not load them?
 * <p>
 * See bug 265114
 *
 * @author Simon McDuff
 */
public class Bugzilla_265114_Test extends AbstractCDOTest
{
  @SuppressWarnings("deprecation")
  public void testResourceSet() throws Exception
  {
    {
      CDOSession session = openSession();
      CDOTransaction transaction = session.openTransaction();
      CDOResource resource = transaction.createResource(getResourcePath("/res1"));
      Company company = getModel1Factory().createCompany();
      resource.getContents().add(company);
      transaction.commit();
      transaction.close();
      session.close();
    }

    CDOSession session = openSession();
    ResourceSet rset = new ResourceSetImpl();

    Registry registry = rset.getResourceFactoryRegistry();
    Map<String, Object> map = registry.getProtocolToFactoryMap();
    map.put(CDOProtocolConstants.PROTOCOL_NAME, CDOResourceFactory.INSTANCE);
    CDOViewProvider viewProvider = new CDOViewProviderNewView(session);
    CDOViewProviderRegistry.INSTANCE.addViewProvider(viewProvider);

    try
    {
      TransactionEditingDomainSimulatedAdapter adapter = new TransactionEditingDomainSimulatedAdapter();
      rset.eAdapters().add(adapter);
      URI uriResource1 = CDOURIUtil.createResourceURI(session, getResourcePath("res1"));
      Resource resource1 = rset.getResource(uriResource1, true);

      CDOObject cdoResource = CDOUtil.getCDOObject((CDOResource)resource1);
      assertEquals(rset, cdoResource.cdoView().getResourceSet());
      assertEquals(1, adapter.getListOfContent().size());
    }
    finally
    {
      CDOViewProviderRegistry.INSTANCE.removeViewProvider(viewProvider);
    }
  }

  /**
   * @author Simon McDuff
   */
  public static class CDOViewProviderNewView implements CDOViewProvider
  {
    public static final int PRIORITY = DEFAULT_PRIORITY - 100;

    private final CDOSession session;

    public CDOViewProviderNewView(CDOSession session)
    {
      this.session = session;
    }

    @Override
    public int getPriority()
    {
      return PRIORITY;
    }

    @Override
    public String getRegex()
    {
      return null;
    }

    @Override
    public CDOView getView(URI uri, ResourceSet resourceSet)
    {
      return session.openTransaction(resourceSet);
    }

    @Override
    public URI getResourceURI(CDOView view, String path)
    {
      return null;
    }

    @Override
    public boolean matchesRegex(URI uri)
    {
      return true;
    }
  }

  /**
   * @author Simon McDuff
   */
  public static class TransactionEditingDomainSimulatedAdapter extends AdapterImpl
  {
    private List<EObject> listOfContent = new ArrayList<>();

    @Override
    public void notifyChanged(Notification msg)
    {
      if (msg.getEventType() == Notification.ADD)
      {
        Object newResource = msg.getNewValue();
        if (newResource instanceof CDOResourceImpl)
        {
          for (EObject object : ((CDOResource)newResource).getContents())
          {
            listOfContent.add(object);
          }
        }
      }
    }

    public List<EObject> getListOfContent()
    {
      return listOfContent;
    }
  }
}

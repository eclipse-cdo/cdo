/*
 * Copyright (c) 2022, 2023 Eike Stepper (Loehne, Germany) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Eike Stepper - initial API and implementation
 */
package org.eclipse.emf.cdo.lm.internal.client;

import org.eclipse.emf.cdo.common.branch.CDOBranchPoint;
import org.eclipse.emf.cdo.common.branch.CDOBranchPointRef;
import org.eclipse.emf.cdo.common.id.CDOID;
import org.eclipse.emf.cdo.common.id.CDOIDUtil;
import org.eclipse.emf.cdo.explorer.CDOExplorerUtil;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckout;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager.CheckoutInitializeEvent;
import org.eclipse.emf.cdo.explorer.checkouts.CDOCheckoutManager.CheckoutStateEvent;
import org.eclipse.emf.cdo.explorer.repositories.CDORepository;
import org.eclipse.emf.cdo.lm.Baseline;
import org.eclipse.emf.cdo.lm.Module;
import org.eclipse.emf.cdo.lm.ModuleType;
import org.eclipse.emf.cdo.lm.Stream;
import org.eclipse.emf.cdo.lm.System;
import org.eclipse.emf.cdo.lm.assembly.Assembly;
import org.eclipse.emf.cdo.lm.client.IAssemblyDescriptor;
import org.eclipse.emf.cdo.lm.client.IAssemblyManager;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException;
import org.eclipse.emf.cdo.lm.client.ISystemDescriptor.ResolutionException.Reason;
import org.eclipse.emf.cdo.lm.client.ISystemManager;
import org.eclipse.emf.cdo.lm.client.ISystemManager.BaselineCreatedEvent;
import org.eclipse.emf.cdo.lm.client.ISystemManager.ModuleDeletedEvent;
import org.eclipse.emf.cdo.lm.client.ISystemManager.StreamBranchChangedEvent;
import org.eclipse.emf.cdo.lm.internal.client.bundle.OM;
import org.eclipse.emf.cdo.lm.modules.ModuleDefinition;
import org.eclipse.emf.cdo.session.CDOSession;

import org.eclipse.net4j.util.ObjectUtil;
import org.eclipse.net4j.util.StringUtil;
import org.eclipse.net4j.util.concurrent.TaskQueue;
import org.eclipse.net4j.util.event.IEvent;
import org.eclipse.net4j.util.event.IListener;
import org.eclipse.net4j.util.io.IORuntimeException;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

import org.eclipse.core.runtime.IProgressMonitor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

/**
 * @author Eike Stepper
 */
public final class AssemblyManager extends LMManager<CDOCheckout, CDOCheckoutManager, IAssemblyDescriptor> implements IAssemblyManager
{
  public static final AssemblyManager INSTANCE = new AssemblyManager();

  public static final String PROP_SYSTEM_NAME = "systemName";

  public static final String PROP_BASELINE_ID = "baselineID";

  public static final String PROP_MODULE_TYPE = "moduleType";

  private static final String ASSEMBLY_FILE_NAME = "module.assembly";

  private static final String ASSEMBLY_UPDATE_FILE_NAME = "update.assembly";

  private static final String ERRORS_FILE_NAME = "resolution.errors";

  private static final String RESOLUTION_ERROR = "Module definition could not be resolved";

  private static final ThreadLocal<Boolean> CREATING_DESCRIPTOR = new ThreadLocal<>();

  private final IListener systemManagerListener = new IListener()
  {
    @Override
    public void notifyEvent(IEvent event)
    {
      if (event instanceof BaselineCreatedEvent)
      {
        BaselineCreatedEvent e = (BaselineCreatedEvent)event;
        ISystemDescriptor systemDescriptor = e.getSystemDescriptor();

        ObjectUtil.forEachSafe(getDescriptors(), assemblyDescriptor -> {
          if (assemblyDescriptor.getSystemDescriptor() == systemDescriptor)
          {
            ((AssemblyDescriptor)assemblyDescriptor).baselineAdded(e.getNewBaseline());
          }
        });
      }
      else if (event instanceof StreamBranchChangedEvent)
      {
        StreamBranchChangedEvent e = (StreamBranchChangedEvent)event;
        Stream stream = e.getStream();

        ObjectUtil.forEachSafe(getDescriptors(), assemblyDescriptor -> {
          Baseline baseline = assemblyDescriptor.getBaseline();
          if (baseline == stream)
          {
            CDOCheckout checkout = assemblyDescriptor.getCheckout();
            checkout.setBranchPoint(stream.getBranchPoint());
          }
        });
      }
      else if (event instanceof ModuleDeletedEvent)
      {
        ModuleDeletedEvent e = (ModuleDeletedEvent)event;
        ISystemDescriptor systemDescriptor = e.getSystemDescriptor();
        CDOID deletedModuleID = e.getDeletedModuleID();

        ObjectUtil.forEachSafe(getDescriptors(), assemblyDescriptor -> {
          if (assemblyDescriptor.getSystemDescriptor() == systemDescriptor)
          {
            ((AssemblyDescriptor)assemblyDescriptor).moduleDeleted(deletedModuleID);
          }
        });
      }
    }
  };

  private final TaskQueue<IAssemblyDescriptor> updateChecker = new TaskQueue<>()
  {
    @Override
    protected String getJobName(IAssemblyDescriptor descriptor)
    {
      return "Check for updates in " + descriptor.getName();
    }

    @Override
    protected void execute(IAssemblyDescriptor descriptor, IProgressMonitor monitor) throws Exception
    {
      ((AssemblyDescriptor)descriptor).checkForUpdates(monitor);
    }

    @Override
    protected void handleException(IAssemblyDescriptor descriptor, Exception ex)
    {
      if (isActive())
      {
        super.handleException(descriptor, ex);
      }
    }
  };

  private final Map<CDOCheckout, IAssemblyDescriptor> unfinalizedDescriptors = new WeakHashMap<>();

  private AssemblyManager()
  {
    super(CDOExplorerUtil.getCheckoutManager(), CDOCheckout.class);
  }

  public void scheduleUpdateCheck(IAssemblyDescriptor descriptor)
  {
    if (CREATING_DESCRIPTOR.get() != Boolean.TRUE)
    {
      updateChecker.schedule(descriptor);
    }
  }

  @Override
  public IAssemblyDescriptor getDescriptor(CDOCheckout explorerElement)
  {
    synchronized (this)
    {
      IAssemblyDescriptor descriptor = super.getDescriptor(explorerElement);
      if (descriptor == null)
      {
        descriptor = unfinalizedDescriptors.get(explorerElement);
      }

      return descriptor;
    }
  }

  @Override
  public IAssemblyDescriptor getDescriptor(EObject object)
  {
    IAssemblyDescriptor descriptor = AssemblyDescriptor.get(object);
    if (descriptor == null)
    {
      CDOCheckout checkout = CDOExplorerUtil.getCheckout(object);
      if (checkout != null)
      {
        descriptor = getDescriptor(checkout);
      }
    }

    return descriptor;
  }

  @Override
  public IAssemblyDescriptor[] getDescriptors(Baseline baseline)
  {
    List<IAssemblyDescriptor> result = new ArrayList<>();
    forEachDescriptor(d -> {
      if (d.getBaseline() == baseline)
      {
        result.add(d);
      }
    });

    return result.toArray(newArray(result.size()));
  }

  @Override
  public IAssemblyDescriptor createDescriptor(String label, Baseline baseline, IProgressMonitor monitor) throws Exception
  {
    Module module = baseline.getModule();
    String moduleName = module.getName();
    ModuleType moduleType = module.getType();
    System system = module.getSystem();

    SystemDescriptor systemDescriptor = (SystemDescriptor)ISystemManager.INSTANCE.getDescriptor(system);
    ModuleDefinition moduleDefinition = systemDescriptor.extractModuleDefinition(baseline);

    Assembly assembly = null;
    Reason[] reasons = null;

    try
    {
      assembly = systemDescriptor.resolve(moduleDefinition, baseline, monitor);
    }
    catch (ResolutionException ex)
    {
      reasons = ex.getReasons();
      OM.LOG.warn(ex);

      // Create an empty default assembly.
      assembly = systemDescriptor.createEmptyAssembly();
    }

    CDORepository moduleRepository = systemDescriptor.getModuleRepository(moduleName);
    if (moduleRepository != null)
    {
      CDOCheckout checkout = createCheckout(label, baseline, moduleRepository);

      Properties properties = new Properties();
      properties.setProperty(PROP_SYSTEM_NAME, system.getName());
      properties.setProperty(PROP_BASELINE_ID, CDOExplorerUtil.getCDOIDString(baseline.cdoID()));
      if (moduleType != null)
      {
        properties.setProperty(PROP_MODULE_TYPE, moduleType.getName());
      }

      saveProperties(checkout, properties);

      if (assembly != null)
      {
        saveAssembly(checkout, assembly, false);
      }

      if (reasons != null && reasons.length != 0)
      {
        AssemblyManager.saveErrors(checkout, reasons);
      }

      try
      {
        CREATING_DESCRIPTOR.set(Boolean.TRUE);
        checkout.open();
      }
      finally
      {
        CREATING_DESCRIPTOR.remove();
      }

      return getDescriptor(checkout);
    }

    return null;
  }

  @Override
  public String getModuleTypeProperty(CDOCheckout checkout)
  {
    Properties properties = loadProperties(checkout);
    if (properties != null)
    {
      Object property = properties.get(PROP_MODULE_TYPE);
      if (property instanceof String)
      {
        return (String)property;
      }
    }

    return null;
  }

  private CDOCheckout createCheckout(String label, Baseline baseline, CDORepository moduleRepository)
  {
    String type;
    boolean readOnly;
    if (baseline.isFloating())
    {
      type = CDOCheckout.TYPE_ONLINE_TRANSACTIONAL;
      readOnly = false;
    }
    else
    {
      type = CDOCheckout.TYPE_ONLINE_HISTORICAL;
      readOnly = true;
    }

    CDOBranchPointRef ref = baseline.getBranchPoint();
    CDOSession session = moduleRepository.acquireSession();
    int branchID;
    CDOID rootID;

    try
    {
      CDOBranchPoint branchPoint = ref.resolve(session.getBranchManager());
      branchID = branchPoint.getBranch().getID();
      rootID = session.getRepositoryInfo().getRootResourceID();
    }
    finally
    {
      moduleRepository.releaseSession();
    }

    CDOCheckoutManager checkoutManager = CDOExplorerUtil.getCheckoutManager();

    Properties properties = new Properties();
    properties.setProperty("type", type);
    properties.setProperty("label", checkoutManager.getUniqueLabel(label));
    properties.setProperty("repository", moduleRepository.getID());
    properties.setProperty("branchID", Integer.toString(branchID));
    properties.setProperty("timeStamp", Long.toString(ref.getTimeStamp()));
    properties.setProperty("readOnly", Boolean.toString(readOnly));
    properties.setProperty("rootID", CDOExplorerUtil.getCDOIDString(rootID));
    properties.setProperty("prefetch", StringUtil.TRUE);

    return checkoutManager.addCheckout(properties);
  }

  @Override
  protected IAssemblyDescriptor[] newArray(int size)
  {
    return new IAssemblyDescriptor[size];
  }

  @Override
  protected void doActivate() throws Exception
  {
    super.doActivate();
    ISystemManager.INSTANCE.addListener(systemManagerListener);
  }

  @Override
  protected void doDeactivate() throws Exception
  {
    ISystemManager.INSTANCE.removeListener(systemManagerListener);
    super.doDeactivate();
  }

  @Override
  protected void explorerElementAdded(CDOCheckout checkout)
  {
    if (checkout.isOpen())
    {
      initializeDescriptor(checkout);
      finalizeDescriptor(checkout);
    }
  }

  @Override
  protected void explorerElementRemoved(CDOCheckout checkout)
  {
    removeDescriptor(checkout);
  }

  @Override
  protected void explorerElementChanged(CDOCheckout checkout)
  {
    updateDescriptor(checkout);
  }

  @Override
  protected void notifyExplorerElementEvent(IEvent event)
  {
    if (event instanceof CheckoutInitializeEvent)
    {
      CheckoutInitializeEvent e = (CheckoutInitializeEvent)event;
      initializeDescriptor(e.getCheckout());

    }
    else if (event instanceof CheckoutStateEvent)
    {
      CheckoutStateEvent e = (CheckoutStateEvent)event;

      switch (e.getNewState())
      {
      case Open:
        finalizeDescriptor(e.getCheckout());
        break;

      case Closed:
        removeDescriptor(e.getCheckout());
        break;

      default:
      }
    }
    else
    {
      super.notifyExplorerElementEvent(event);
    }
  }

  private void initializeDescriptor(CDOCheckout checkout)
  {
    Properties properties = loadProperties(checkout);
    if (properties != null)
    {
      String systemName = properties.getProperty(PROP_SYSTEM_NAME);
      CDOID baselineID = CDOIDUtil.read(properties.getProperty(PROP_BASELINE_ID));

      synchronized (this)
      {
        if (!descriptors.containsKey(checkout) && !unfinalizedDescriptors.containsKey(checkout))
        {
          IAssemblyDescriptor descriptor = new AssemblyDescriptor(checkout, systemName, baselineID);
          LifecycleUtil.activate(descriptor);

          unfinalizedDescriptors.put(checkout, descriptor);
          ++count;
        }
      }
    }
  }

  private void finalizeDescriptor(CDOCheckout checkout)
  {
    IAssemblyDescriptor descriptor;
    synchronized (this)
    {
      descriptor = unfinalizedDescriptors.remove(checkout);
      if (descriptor != null)
      {
        descriptors.put(checkout, descriptor);
        ++count;
      }
    }

    if (descriptor != null)
    {
      scheduleUpdateCheck(descriptor);
      fireElementAddedEvent(descriptor);
    }
  }

  private void updateDescriptor(CDOCheckout checkout)
  {
    AssemblyDescriptor descriptor = (AssemblyDescriptor)getDescriptor(checkout);
    if (descriptor != null)
    {
      descriptor.checkoutChanged();
    }
  }

  private void removeDescriptor(CDOCheckout checkout)
  {
    IAssemblyDescriptor descriptor;
    synchronized (this)
    {
      descriptor = descriptors.remove(checkout);
      if (descriptor != null)
      {
        --count;
      }
    }

    if (descriptor != null)
    {
      LifecycleUtil.deactivate(descriptor);
      fireElementRemovedEvent(descriptor);
    }
  }

  private static File getStateFolder(CDOCheckout checkout, boolean createFolderOnDemand)
  {
    return checkout.getStateFolder(STATE_FOLDER_NAME, createFolderOnDemand);
  }

  private static File getAssemblyFile(CDOCheckout checkout, boolean createFolderOnDemand, boolean update)
  {
    File stateFolder = getStateFolder(checkout, createFolderOnDemand);
    return new File(stateFolder, update ? ASSEMBLY_UPDATE_FILE_NAME : ASSEMBLY_FILE_NAME);
  }

  private static File getErrorsFile(CDOCheckout checkout, boolean createFolderOnDemand)
  {
    File stateFolder = getStateFolder(checkout, createFolderOnDemand);
    return new File(stateFolder, ERRORS_FILE_NAME);
  }

  private static void deleteFile(File file) throws IOException
  {
    if (file.isFile())
    {
      if (!file.delete())
      {
        throw new IOException(file + " could not be deleted");
      }
    }
  }

  public static void saveAssembly(CDOCheckout checkout, Assembly assembly, boolean update) throws IOException
  {
    File file = getAssemblyFile(checkout, true, update);
    URI uri = URI.createFileURI(file.getAbsolutePath());

    ResourceSet assemblyResourceSet = new ResourceSetImpl();
    Resource assemblyResource = assemblyResourceSet.createResource(uri);
    assemblyResource.getContents().add(assembly);
    assemblyResource.save(null);
  }

  public static Assembly loadAssembly(CDOCheckout checkout, boolean update)
  {
    File file = getAssemblyFile(checkout, false, update);
    if (file.isFile())
    {
      URI uri = URI.createFileURI(file.getAbsolutePath());

      ResourceSet resourceSet = new ResourceSetImpl();
      Resource resource = resourceSet.getResource(uri, true);
      Assembly assembly = (Assembly)resource.getContents().get(0);
      return assembly;
    }

    return null;
  }

  public static void deleteAssembly(CDOCheckout checkout, boolean update) throws IOException
  {
    File file = getAssemblyFile(checkout, true, update);
    deleteFile(file);
  }

  @SuppressWarnings("restriction")
  public static void setCheckoutError(CDOCheckout checkout, String error)
  {
    ((org.eclipse.emf.cdo.internal.explorer.checkouts.CDOCheckoutImpl)checkout).setError(error);
  }

  public static List<String> saveErrors(CDOCheckout checkout, ResolutionException.Reason[] reasons) throws IOException
  {
    List<String> errors = new ArrayList<>();

    setCheckoutError(checkout, RESOLUTION_ERROR);
    File file = getErrorsFile(checkout, true);

    if (reasons != null && reasons.length != 0)
    {
      try (FileWriter fileWriter = new FileWriter(file); BufferedWriter writer = new BufferedWriter(fileWriter))
      {
        for (ResolutionException.Reason reason : reasons)
        {
          String error = reason.toString();
          errors.add(error);

          writer.write(error);
          writer.write(StringUtil.NL);
        }
      }
    }
    else
    {
      deleteFile(file);
    }

    return errors;
  }

  public static List<String> loadErrors(CDOCheckout checkout)
  {
    File file = getErrorsFile(checkout, true);
    if (file.isFile())
    {
      List<String> errors = new ArrayList<>();

      try (FileReader fileReader = new FileReader(file); BufferedReader reader = new BufferedReader(fileReader))
      {
        String error;
        while ((error = reader.readLine()) != null)
        {
          errors.add(error);
        }
      }
      catch (IOException ex)
      {
        throw new IORuntimeException(ex);
      }

      return errors;
    }

    return null;
  }

  public static void deleteErrors(CDOCheckout checkout) throws IOException
  {
    if (RESOLUTION_ERROR.equals(checkout.getError()))
    {
      setCheckoutError(checkout, null);
    }

    File file = getErrorsFile(checkout, true);
    deleteFile(file);
  }
}

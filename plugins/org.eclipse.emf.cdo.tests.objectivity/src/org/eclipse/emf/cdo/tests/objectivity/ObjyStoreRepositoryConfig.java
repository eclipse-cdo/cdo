package org.eclipse.emf.cdo.tests.objectivity;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.cdo.server.CDOServerUtil;
import org.eclipse.emf.cdo.server.IRepository;
import org.eclipse.emf.cdo.server.IRepositoryProvider;
import org.eclipse.emf.cdo.server.IStore;
import org.eclipse.emf.cdo.server.IRepository.Props;
import org.eclipse.emf.cdo.server.internal.objectivity.ObjectivityStoreConfig;
import org.eclipse.emf.cdo.server.objectivity.ObjyStoreUtil;
import org.eclipse.emf.cdo.tests.config.impl.RepositoryConfig;
import org.eclipse.net4j.util.container.IManagedContainer;
import org.eclipse.net4j.util.lifecycle.Lifecycle;
import org.eclipse.net4j.util.lifecycle.LifecycleUtil;

/**
 * @author Eike Stepper
 */
public class ObjyStoreRepositoryConfig extends RepositoryConfig
{
  private static final long serialVersionUID = 1L;
	public static ObjyStoreRepositoryConfig INSTANCE = new ObjyStoreRepositoryConfig();
	private static ObjectivityStoreConfig storeConfig = new ObjectivityStoreConfig();

  public ObjyStoreRepositoryConfig()
  {
  	super("Objectivity");
    org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.DEBUG.setEnabled(true);
    org.eclipse.emf.cdo.server.internal.objectivity.bundle.OM.INFO.setEnabled(true);
  }
  
  @Override
  public void setUp() throws Exception
  {
    // define the store config before we call super.setUp() -> it will createStore()
  	//storeConfig = new ObjectivityStoreConfig(this.getCurrentTest().getName());
//  	storeConfig = new ObjectivityStoreConfig();
//  	LifecycleUtil.activate(storeConfig);
    System.out.println("ObjyStoreRepositry.setup() - STARTED");
    long sTime = System.currentTimeMillis();
    super.setUp();
    long eTime = System.currentTimeMillis();
    System.out.println("ObjyStoreRepositry.setup() time: " + (eTime - sTime));
  }

  @Override
  public void tearDown() throws Exception
  {
    super.tearDown();
    //System.out.println(">>>>IS:<<<< We need to remove all data created here....");
    storeConfig.resetFD();
  	//LifecycleUtil.deactivate(storeConfig);
  }

	@Override
	protected IStore createStore(String repoName) {
		// We might need to use the repoName to our advantage!!!
	  System.out.println("************* ObjyStore creation ****************\n");
		return ObjyStoreUtil.createStore(storeConfig);
	}

	/***
	 * TODO - enable auditing and branching once we implement them!!!
	 */
    @Override
    protected void initRepositoryProperties(Map<String, String> props)
    {
      super.initRepositoryProperties(props);
      props.put(Props.SUPPORTING_AUDITS, "false");
      props.put(Props.SUPPORTING_BRANCHES, "false");
    }	
}

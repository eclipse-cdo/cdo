package org.eclipse.emf.cdo.tests.objectivity;

import org.eclipse.emf.cdo.tests.AllTestsAllConfigs;
import org.eclipse.emf.cdo.tests.ChunkingTest;
import org.eclipse.emf.cdo.tests.ExternalReferenceTest;
import org.eclipse.emf.cdo.tests.MultiValuedOfAttributeTest;
import org.eclipse.emf.cdo.tests.ResourceTest;
import org.eclipse.emf.cdo.tests.XATransactionTest;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_258933_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_259869_Test;
import org.eclipse.emf.cdo.tests.bugzilla.Bugzilla_266982_Test;
import org.eclipse.emf.cdo.tests.config.impl.ConfigTest;

import java.util.List;

public abstract class ObjyDBConfigs extends AllTestsAllConfigs
{
  @Override
  protected void initTestClasses(List<Class<? extends ConfigTest>> testClasses)
  {
    super.initTestClasses(testClasses);

    // this takes ages ...
    //testClasses.remove(Bugzilla_266982_Test.class);

    // completely disabled because of Bug 254455:
    testClasses.remove(MultiValuedOfAttributeTest.class);

    // and partially disabled because of the same Bug 254455 ->
    //testClasses.remove(ChunkingTest.class);
    //testClasses.add(DISABLED_ChunkingTest.class);

    // partially disabled because of Bug 283992:
    //testClasses.remove(ResourceTest.class);
    //testClasses.add(DISABLED_ResourceTest.class);


  }
}

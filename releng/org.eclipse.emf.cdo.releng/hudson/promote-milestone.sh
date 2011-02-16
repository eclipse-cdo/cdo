#!/bin/sh

stream=3.0
milestone=SR2-RC4
buildID=125

mkdir promote.tmp
pushd promote.tmp

/shared/common/apache-ant-1.7.1/bin/ant -f /shared/modeling/tools/promotion/download-artifacts.xml -Dbuild.url=https://hudson.eclipse.org/hudson/job/emf-cdo-maintenance/$buildID -Dartifact.prefix=result/site.p2/

label=`ls`
pushd $label

/shared/common/apache-ant-1.7.1/bin/ant -f /shared/modeling/tools/promotion/unpack-iu.xml -Dunpack.iu=org.apache.derby

PROBLEM_BUNDLES="$(
 for P in plugins/*.jar.pack.gz; do
  /shared/common/jdk-1.5.0-22.x86_64/bin/unpack200 --quiet "$P" /dev/null > /dev/null || basename "$P"
 done
)"

if [ -n "$PROBLEM_BUNDLES" ]; then
 echo "Problems encountered unpacking the following bundles:"
 echo "$PROBLEM_BUNDLES"
 exit 2
fi

cp -R . /home/data/httpd/download.eclipse.org/modeling/emf/cdo/updates/$stream/$stream-$milestone-$label

popd
popd

pushd /home/data/httpd/download.eclipse.org/modeling/emf/cdo/updates/$stream-milestones
/shared/common/apache-ant-1.7.1/bin/ant -f /shared/modeling/tools/promotion/manage-composite.xml -Dcomposite.name="CDO Model Repository Site" add -Dchild.repository=../$stream/$stream-$milestone-$label
popd

rm -rf promote.tmp

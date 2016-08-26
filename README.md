# aco-pfsp
Ant Colony Optimization solution for the permutation flow shop problem
Implementation of solution using different techniques learnt in the Swarm Intellgence course given at the ULB.


## Exec Command Line
Normal Execution:
<code>mvn exec:java -Dexec.mainClass="be.ac.intelligence.swarm.App" -Dexec.args="-instance C:\personal\f-p\instances\pfsp_instances_testing\a.txt"</code>
Debug Mode:
mvn exec:exec -Dexec.executable="java" -Dexec.args="-classpath %classpath -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000 be.ac.intelligence.swarm.App -instance '/home/fakefla/Documents/vub/swarm_intelligence/final-project/instances/pfsp_instances_testing/a.txt'"

# ONAP RAN-SIM

# Introduction :

RAN-SIM is a Radio Access Network Simulator, it is used to simulate the various functionalities of an eNodeB.

# Compilation Steps :

RAN-SIM can be compiled with `mvn clean install`. By default it executes:
- the standard unit tests
- the Spring integration tests
- BUT *does not build the docker images*

You can disable the integration tests by executing: `mvn clean install -DskipTests=true -Dmaven.test.skip=true`

# Building Docker images :

You can build docker images by executing profile "docker": `mvn clean install -P docker` 

#Running RAN-SIM :

-After successful build navigate to 'ran-simulator/ransim/docker' directory.
-Use docker-compose up -d to start RAN-SIM
-Access the GUI using the following url in the web browser: 'http://<yourIP>:8081/ransimui'

#Logging :
- Login to ransim pod and you can get the logs from /opt/app/policy/servers/ransim/logs/ransim-rest.log

#Configuration :
The following capabilities can be modified in the 'ransim.properties' file based on user capabilities and configurations.
        File directory:
           ran-simulator/ransim/ransimctrlr/packages/base/src/files/install/servers/ransim/bin/ransim.properties
            a) serverIdPrefix: Netconf server comman prefix (use default value present in the file)
            b) numberOfCellsPerNCServer: Maximum number of cells that can be handled in a single netconf server(use default value present in the file).
            c) numberOfProcessPerMc:
                        Maximum number of netconf servers that can run in a single machine(use default value present in the file, which is for a machine of 8 GB RAM)
                        (A single netconf server uses approximately 350MB).
            d) numberOfMachines:
                        Maximum number of machines available(use default value present in the file).
            e) GridSize: (Applicable only for HONEYCOMB representation)
                        The number of cells that can be accommodated along one side of the cluster for an auto-generated layout.
                         However, it has no relevance now, as the initial layout is generated from a file. So use default value as 1.
            f) strictValidateRansimAgentsAvailability:
                        A boolean value to check if any RAN-Sim agents are running (use default value present in the file).
            g) sdnrServerIp:
                        SDNR IP address
            h) sdnrServerPort:
                        SDNR port number
            i) sdnrServerUserid:
                        SDNR user ID
            j) sdnrServerPassword:
                         SDNR user password
            k) maxPciValueAllowed:
                        maximum value of the physical cell Id. (Default is 503).
           l)dumpFileName
                        Location of the dumpfile to load the topology.
                        The dump file is loaded from 'ran-simulator/ransim/docker/config'. A sample dump file - 'sample.json' contains deatils of 1000 cells.
                        For the controller to access the dump file from the the above location use the path - /tmp/ransim-install/config/sample.json

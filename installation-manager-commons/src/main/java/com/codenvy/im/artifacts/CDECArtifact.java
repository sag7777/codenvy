/*
/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.im.artifacts;

import com.codenvy.im.agent.Agent;
import com.codenvy.im.agent.AgentException;
import com.codenvy.im.agent.SecureShellAgent;
import com.codenvy.im.command.Command;
import com.codenvy.im.command.CommandException;
import com.codenvy.im.command.RemoteCommand;
import com.codenvy.im.config.CdecConfig;
import com.codenvy.im.config.ConfigException;
import com.codenvy.im.config.ConfigFactory;
import com.codenvy.im.utils.HttpTransport;
import com.codenvy.im.utils.Version;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;

/**
 * @author Anatoliy Bazko
 * @author Dmytro Nochevnov
 * */
@Singleton
public class CDECArtifact extends AbstractArtifact {
    private static final Logger LOG = LoggerFactory.getLogger(CDECArtifact.class);

    public static final String NAME = "cdec";

    private final HttpTransport transport;
    private final String        updateEndpoint;

    public enum InstallType {
        SINGLE_NODE_WITH_PUPPET_MASTER,
        MULTI_NODES_WITH_PUPPET_MASTER
    }

    @Inject
    public CDECArtifact(@Named("installation-manager.update_server_endpoint") String updateEndpoint,
                        HttpTransport transport) {
        super(NAME);
        this.updateEndpoint = updateEndpoint;
        this.transport = transport;
    }

    @Override
    public void install(Path pathToBinaries) throws CommandException, AgentException, ConfigException {
        install(pathToBinaries, InstallType.SINGLE_NODE_WITH_PUPPET_MASTER);
    }

    public void install(Path pathToBinaries, InstallType installType) throws CommandException, AgentException, ConfigException {
        List<Command> installCommands = getInstallCommands(pathToBinaries, installType);

        // display list of commands
        LOG.info(format("\n\n--- There are next %s commands will be executed:\n%s\n", installCommands.size(),
                        Arrays.toString(installCommands.toArray()).replace(", ", ",\n")));

        for (Command command : installCommands) {
            LOG.info(format("\n\n--- Executing %s-th command %s...\n", installCommands.lastIndexOf(command) + 1, command.toString()));
            LOG.info(command.execute());
        }

        LOG.info("\n\n--- Finish installation.\n");
    }

    @Override
    public String getInstalledVersion(String accessToken) throws IOException {
        return null;  // TODO issue CDEC-62
    }

    @Override
    public int getPriority() {
        return 2;
    }

    @Override
    public boolean isInstallable(Version versionToInstall, String accessToken) {
        return true;
    }

    @Override
    protected Path getInstalledPath() throws URISyntaxException {
        throw new UnsupportedOperationException();
    }

    private List<Command> getInstallCommands(Path pathToBinaries, InstallType type) throws AgentException, ConfigException {
        switch (type) {
            case SINGLE_NODE_WITH_PUPPET_MASTER:
                CdecConfig config = ConfigFactory.loadConfig(InstallType.SINGLE_NODE_WITH_PUPPET_MASTER.toString());
                return getInstallCdecOnSingleNodeWithPuppetMasterCommands(pathToBinaries, config);

            case MULTI_NODES_WITH_PUPPET_MASTER:
                return getInstallCdecOnMultiNodesWithPappetMasterCommands(pathToBinaries);

            default:
                return Collections.emptyList();
        }
    }

    /**
     * @throws ConfigException if required config parameter isn't present in configuration.
     * @throws AgentException if required agent isn't ready to perform commands.
     */
    protected List<Command> getInstallCdecOnSingleNodeWithPuppetMasterCommands(Path pathToBinaries, final CdecConfig config) throws
                                                                                                                              AgentException,
                                                                                                                              ConfigException {
        // check readiness of config
        if (config.getHost().isEmpty() || config.getSSHPort().isEmpty()) {
            throw new ConfigException(format("Installation config file '%s' is incomplete.", config.getConfigSource()));
        }

        LOG.info(format("\n\n--- Config file '%s' will being used to install CDEC.\n", config.getConfigSource()));

        List<Command> commands = new ArrayList<>();

        final Agent agent;
        // select use password or auth key
        if (!config.getPassword().isEmpty()) {
            agent = new SecureShellAgent(
                config.getHost(),
                Integer.valueOf(config.getSSHPort()),
                config.getUser(),
                config.getPassword()
            );
        } else {
            agent = new SecureShellAgent(
                config.getHost(),
                Integer.valueOf(config.getSSHPort()),
                config.getUser(),
                config.getPrivateKeyFileAbsolutePath(),
                null
            );
        }


        // TODO issue CDEC-59

        /**
         * CDEC installation sequence.
         * 1) On Puppet Master host :
         * 1.1 Validate all hosts name and set their if need. //TODO set ?
         * 1.2 Add rule in firewall for puppet master.
         * 1.3 Install puppet master;
         * 1.4 Install unzip if need //TODO
         * 1.5 Upload CDEC in puppet master.
         *
         * // http://www.jcraft.com/jsch/examples/ScpTo.java.html
         *
         * 1.6 Unzip CDEC in puppet;
         *
         * ssh.execute("sudo unzip " + remouteBinariesPath + " /etc/puppet/");
         *
         * 1.7 Configure CDEC in puppet master;
         * 1.8 Start puppet master
         *
         * ssh.execute("sudo service puppetmaster start");
         *
         * 2) On other hosts :
         * 1.1 Validate all hosts name and set their if need; //TODO set ?
         * 1.2 Install puppet client;
         * 1.3 Configure puppet client;
         *
         * String result = ssh.execute("sudo iptables " + rule); //TODO or echo in /etc/sysconfig/iptables
         *
         * 1.4 Start puppet client;
         * 3) Sign nodes connection request on puppet master;
         * 1.1 Validate nodes requests available;
         * 1.2 Sign nodes connection request.
         */

        commands.addAll(new ArrayList<Command>() {{
            add(new RemoteCommand("sudo setenforce 0", agent, "Disable SELinux"));
            add(new RemoteCommand("sudo cp /etc/selinux/config /etc/selinux/config.bak", agent, "Disable SELinux"));
            add(new RemoteCommand("sudo sed -i s/SELINUX=enforcing/SELINUX=disabled/g /etc/selinux/config", agent,
                                  "Disable SELinux"));
        }});

        commands.addAll(new ArrayList<Command>() {{
            add(new RemoteCommand(format("sudo rpm -ivh %s", config.getPuppetResourceUrl()), agent, "Install puppet client"));
            add(new RemoteCommand(format("sudo yum install %s -y", config.getPuppetVersion()), agent, "Install puppet client"));
        }});

        return commands;
    }

    /**
     * TODO issue CDEC-60
     */
    private List<Command> getInstallCdecOnMultiNodesWithPappetMasterCommands(Path pathToBinaries) {
    //                CdecConfig installationConfig = new CdecConfig();
    //
    //                installPuppetMaster(installationConfig.getPuppetMaster(),
    //                                    installationConfig.getHostsName(),
    //                                    pathToBinaries);
    //
    //                for (PuppetClientConfig clientConfig : installationConfig.getPuppetClients()) {
    //                    //            installPuppetClient(clientConfig, insta);
    //                }
    //
    //                signNodesOnPuppetMaster();
        return Collections.emptyList();
    }

}

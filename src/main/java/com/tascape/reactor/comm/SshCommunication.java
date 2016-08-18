/*
 * Copyright 2015 - 2016 Nebula Bay.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tascape.reactor.comm;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import com.tascape.reactor.SystemConfiguration;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author linsong wang
 */
public class SshCommunication extends EntityCommunication {
    private static final Logger LOG = LoggerFactory.getLogger(SshCommunication.class);

    public static final String SYSPROP_HOST = "reactor.comm.ssh.HOST";

    public static final String SYSPROP_PORT = "reactor.comm.ssh.PORT";

    public static final String SYSPROP_KEY = "reactor.comm.ssh.KEY";

    public static final String SYSPROP_USER = "reactor.comm.ssh.USER";

    public static final String SYSPROP_PASS = "reactor.comm.ssh.PASS";

    private final JSch jSch;

    private final String host;

    private final int port;

    private Session session;

    public static SshCommunication newInstance() throws JSchException {
        return newInstance("");
    }

    public static SshCommunication newInstance(String name) throws JSchException {
        SystemConfiguration sysConfig = SystemConfiguration.getInstance();
        String h = sysConfig.getProperty(SshCommunication.SYSPROP_HOST + name);
        if (h == null) {
            h = sysConfig.getProperty(SshCommunication.SYSPROP_HOST, "localhost");
        }
        int p = sysConfig.getIntProperty(SshCommunication.SYSPROP_PORT + name);
        if (p == Integer.MIN_VALUE) {
            p = sysConfig.getIntProperty(SshCommunication.SYSPROP_PORT, 22);
        }
        SshCommunication ssh = new SshCommunication(h, p);

        String k = sysConfig.getProperty(SshCommunication.SYSPROP_KEY + name);
        if (k == null) {
            k = sysConfig.getProperty(SshCommunication.SYSPROP_KEY);
        }
        String pp = sysConfig.getProperty(SshCommunication.SYSPROP_PASS + name);
        if (pp == null) {
            pp = sysConfig.getProperty(SshCommunication.SYSPROP_PASS);
        }
        if (null != k && null != pp) {
            ssh.setPrivateKey(k, pp);
        }

        String u = sysConfig.getProperty(SshCommunication.SYSPROP_USER + name);
        if (u == null) {
            u = sysConfig.getProperty(SshCommunication.SYSPROP_USER);
        }
        String pw = sysConfig.getProperty(SshCommunication.SYSPROP_PASS + name);
        if (pw == null) {
            pw = sysConfig.getProperty(SshCommunication.SYSPROP_PASS);
        }
        ssh.setUsernamePassword(u, pw);
        return ssh;
    }

    /**
     *
     * @param host host DNS name or IP
     * @param port ssh port
     */
    public SshCommunication(String host, int port) {
        this.jSch = new JSch();
        this.host = host;
        this.port = port;
    }

    public void setPrivateKey(String key, String passphrase) throws JSchException {
        this.jSch.addIdentity(key, passphrase);
    }

    public void setUsernamePassword(String username, String password) throws JSchException {
        this.session = this.jSch.getSession(username, host, port);
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);

        this.session.setPassword(password);
    }

    public void shell(String command, OutputStream out) throws JSchException {
        Channel shell = this.session.openChannel("shell");
        shell.setInputStream(IOUtils.toInputStream(command + "\n", Charset.defaultCharset()));
        shell.setOutputStream(out);
        shell.connect(2000);
    }

    public Channel shell(String command, File out) throws JSchException, IOException {
        Channel shell = this.session.openChannel("shell");
        shell.setInputStream(IOUtils.toInputStream(command + "\n", Charset.defaultCharset()));
        shell.setOutputStream(FileUtils.openOutputStream(out));
        shell.connect();
        return shell;
    }

    public void upload(File srcFile, String destFile, long timeout) throws JSchException, SftpException, IOException {
        Channel channel = this.session.openChannel("sftp");
        channel.setInputStream(System.in);
        channel.setOutputStream(System.out);
        channel.connect();

        new ChanneOperationTimer(channel, timeout).start();
        ChannelSftp sftp = (ChannelSftp) channel;
        sftp.put(FileUtils.openInputStream(srcFile), destFile);
        sftp.exit();
    }

    public void download(String srcFile, File destFile, long timeout) throws JSchException, SftpException, IOException {
        Channel channel = this.session.openChannel("sftp");
        channel.setInputStream(System.in);
        channel.setOutputStream(System.out);
        channel.connect();
        new ChanneOperationTimer(channel, timeout).start();

        ChannelSftp sftp = (ChannelSftp) channel;
        try (FileOutputStream out = FileUtils.openOutputStream(destFile)) {
            sftp.get(srcFile, out);
        } finally {
            sftp.exit();
        }
    }

    @Override
    public void connect() throws Exception {
        session.connect();
        LOG.debug("connected: {}", session.getClientVersion());
    }

    @Override
    public void disconnect() throws Exception {
        if (this.session != null) {
            this.session.disconnect();
        }
    }

    private static class ChanneOperationTimer extends Thread {
        private final Channel channel;

        private final long timeout;

        public ChanneOperationTimer(Channel channel, long timeout) {
            this.channel = channel;
            this.timeout = timeout;
        }

        @Override
        public void run() {
            try {
                Thread.sleep(timeout);
            } catch (InterruptedException ex) {
                LOG.warn("interrupted", ex);
            }
            if (channel != null) {
                channel.disconnect();
            }
        }
    }

    public static void main(String[] args) {
        try {
            SshCommunication ssh = new SshCommunication("localhost", 8022);
            ssh.setPrivateKey("~/dev/aws/key.pem", "");
            ssh.setUsernamePassword("ec2-user", "");
            ssh.connect();

            File out = new File("~/ssh.txt");
            ssh.shell("tail -f /usr/share/app/logs/application.log", FileUtils.openOutputStream(out));
            Desktop.getDesktop().open(out);
            ssh.shell("cat /usr/share/app/logs/daemon.log", System.out);

            ssh.upload(out, "/home/ubuntu/sh.txt", 1000);

            Thread.sleep(10000);
            ssh.disconnect();
        } catch (Exception ex) {
            LOG.error("", ex);
        }
    }
}

package com.fathzer.jdbbackup.destinations.sftp;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import com.fathzer.jdbbackup.DestinationManager;
import com.fathzer.jdbbackup.ProxyCompliant;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxyHTTP;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

/**
 * A destination manager that saves the backups to a sftp server.
 * <br>The address format is: sftp://user:pwd[@host[:port]][/path]/filename.
 */
public class SFTPManager implements DestinationManager<SFTPDestination>, ProxyCompliant {
	private ProxyHTTP proxy;

	@Override
	public void setProxy(Proxy p, PasswordAuthentication auth) {
		ProxyCompliant.super.setProxy(p, auth);
		if (!Proxy.NO_PROXY.equals(p)) {
			final InetSocketAddress addr = (InetSocketAddress) p.address();
			proxy = new ProxyHTTP(addr.getHostString(), addr.getPort());
			if (auth != null) {
				proxy.setUserPasswd(auth.getUserName(), String.valueOf(auth.getPassword()));
			}
		} else {
			proxy = null;
		}
	}

	@Override
	public SFTPDestination validate(String fileName, Function<String,CharSequence> extensionBuilder) {
		return new SFTPDestination(fileName, extensionBuilder);
	}

	@Override
	public void send(InputStream in, long size, SFTPDestination dest) throws IOException {
		try {
			final JSch jsch = new JSch();
			final Session session = jsch.getSession(dest.getUser(), dest.getHost(), dest.getPort());
			if (proxy != null) {
				session.setProxy(proxy);
			}
			session.setPassword(dest.getPassword());
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
			session.connect();
			try {
				send(session, dest, in);
			} finally {
				session.disconnect();
			}
		} catch (JSchException e) {
			throw new IOException(e);
		}
	}

	private static void send(final Session session, SFTPDestination dest, InputStream in) throws JSchException, IOException {
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		try {
			if (dest.getPath() != null) {
				mkdirs(channel, dest.getPath());
				channel.cd(dest.getPath());
			}
			channel.put(in, dest.getFilename());
		} catch (SftpException e) {
			throw new IOException(e);
		} finally {
			channel.exit();
			channel.disconnect();
		}
	}

	private static void mkdirs(ChannelSftp ch, String path) throws SftpException {
		final List<String> folders = new ArrayList<>(Arrays.asList(path.split("/")));
		StringBuilder fullPath;
		if (folders.get(0).isEmpty()) {
			// Absolute path
			fullPath = new StringBuilder("/");
			folders.remove(0);
		} else {
			// Relative Path
			fullPath = new StringBuilder("./");
		}
		for (String folder : folders) {
			final Collection<?> ls = ch.ls(fullPath.toString());
			if (!exists(ls, folder) && !folder.isEmpty()) {
				ch.mkdir(fullPath + folder);
			}
			fullPath.append(folder);
			fullPath.append("/");
		}
	}

	private static boolean exists(final Collection<?> ls, String folder) {
		boolean isExist = false;
		for (Object o : ls) {
			if (o instanceof LsEntry) {
				LsEntry e = (LsEntry) o;
				if (e.getAttrs().isDir() && e.getFilename().equals(folder)) {
					isExist = true;
				}
			}
		}
		return isExist;
	}

	@Override
	public String getScheme() {
		return "sftp";
	}
}

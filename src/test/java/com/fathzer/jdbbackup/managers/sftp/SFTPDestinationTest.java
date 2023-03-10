package com.fathzer.jdbbackup.managers.sftp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Date;

import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import com.fathzer.jdbbackup.utils.BasicExtensionBuilder;

class SFTPDestinationTest {

	@Test
	void test() {
		SFTPDestination m = new SFTPDestination("user:pwd@host:2222/path1/path2/filename", BasicExtensionBuilder.INSTANCE);
		assertEquals("user", m.getUser());
		assertEquals("pwd", m.getPassword());
		assertEquals("host", m.getHost());
		assertEquals(2222, m.getPort());
		assertEquals("path1/path2", m.getPath());
		assertEquals("filename.sql.gz", m.getFilename());
	}

	@Test
	void testDefault() {
		SFTPDestination m = new SFTPDestination("user:pwd@host/filename", BasicExtensionBuilder.INSTANCE);
		assertEquals("user", m.getUser());
		assertEquals("pwd", m.getPassword());
		assertEquals("host", m.getHost());
		assertEquals(22, m.getPort());
		assertNull(m.getPath());
		assertEquals("filename.sql.gz", m.getFilename());
		
		// With no host
		m = new SFTPDestination("user:pwd/filename", BasicExtensionBuilder.INSTANCE);
		assertEquals("127.0.0.1", m.getHost());
		assertEquals(22, m.getPort());
	}

	@Test
	void testPattern() {
		try (MockedConstruction<Date> mock = mockConstruction(Date.class)) {
			SFTPDestination m = new SFTPDestination("user:pwd@host/{d=yy}/filename{d=MM}", BasicExtensionBuilder.INSTANCE);
			assertEquals("70",m.getPath());
			assertEquals("filename01.sql.gz", m.getFilename());
		}
	}

	@Test
	void testWrongDestPath() {
		// Missing pwd
		assertThrows(IllegalArgumentException.class, () -> new SFTPDestination("user@host/filename", BasicExtensionBuilder.INSTANCE));
		// Missing login
		assertThrows(IllegalArgumentException.class, () -> new SFTPDestination("host/filename", BasicExtensionBuilder.INSTANCE));
		// Missing host
		assertThrows(IllegalArgumentException.class, () -> new SFTPDestination("user:pwd@/filename", BasicExtensionBuilder.INSTANCE));
		assertThrows(IllegalArgumentException.class, () -> new SFTPDestination("filename", BasicExtensionBuilder.INSTANCE));
		// Invalid port
		assertThrows(IllegalArgumentException.class, () -> new SFTPDestination("user:pwd@host:x/filename", BasicExtensionBuilder.INSTANCE));
		// Missing file
		assertThrows(IllegalArgumentException.class, () -> new SFTPDestination("user:pwd@host/", BasicExtensionBuilder.INSTANCE));
	}
}

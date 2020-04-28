package com.cscie599.gfn.ftp.downloader;

import org.apache.commons.net.ftp.FTPClient;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import java.io.IOException;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author PulkitBhanot
 */
public class BasePubmedTest {

    @Rule
    public TemporaryFolder rawFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder extractedFolder = new TemporaryFolder();

    FTPClient ftpClient;

    @BeforeEach
    void setUp() throws IOException {
        ftpClient = PowerMockito.mock(FTPClient.class);
        when(ftpClient.retrieveFile(Mockito.anyString(), Mockito.any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return true;
            }
        });
        when(ftpClient.getModificationTime(Mockito.any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return "20200212099009";
            }
        });
    }

}

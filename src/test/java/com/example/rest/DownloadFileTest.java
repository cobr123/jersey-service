package com.example.rest;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class DownloadFileTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {

        final String baseURI = "http://localhost:8081/";
        // start the server
        server = Main.startServer(baseURI);
        // create the client
        final Client client = ClientBuilder.newClient();

        target = client.target(baseURI);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testDownloadFile() throws IOException {
        final File file = new File(Main.getRootDir() + "DownloadFileTest.txt");
        FileUtils.writeStringToFile(file, "DownloadFileTest");
        file.deleteOnExit();
        final long id = LocalFile.getCRC(file);
        FileService.addFile(file);

        final String responseMsg = target.path("/files/id/" + id).request(MediaType.APPLICATION_OCTET_STREAM).get(String.class);
        assertEquals("DownloadFileTest", responseMsg);
    }
}

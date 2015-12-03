package com.example.rest;

import org.apache.commons.io.FileUtils;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.nio.file.Files;

import static org.junit.Assert.assertEquals;

public class GetFileByNameTest {

    private HttpServer server;
    private WebTarget target;
    private long id;

    @Before
    public void setUp() throws Exception {
        final File file = new File(Main.getRootDir() + "GetFileByNameTest.txt");
        FileUtils.writeStringToFile(file, "GetFileByNameTest");
        file.deleteOnExit();
        id = LocalFile.getCRC(file);

        final String baseURI = "http://localhost:8083/";
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
    public void testGetFileByName() throws IOException {
        final String responseMsg = target.path("/files/name/GetFileByNameTest").request(MediaType.APPLICATION_JSON).get(String.class);
        assertEquals("[{\"id\":" + id + ",\"name\":\"GetFileByNameTest.txt\"}]", responseMsg);
    }
}

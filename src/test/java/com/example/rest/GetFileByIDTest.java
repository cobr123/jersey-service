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
import java.io.*;

import static org.junit.Assert.assertEquals;

public class GetFileByIDTest {

    private HttpServer server;
    private WebTarget target;
    private long id;

    @Before
    public void setUp() throws Exception {
        final File file = new File(Main.getRootDir() + "GetFileByIDTest.txt");
        FileUtils.writeStringToFile(file, "GetFileByIDTest");
        file.deleteOnExit();
        id = LocalFile.getCRC(file);

        final String baseURI = "http://localhost:8082/";
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
    public void testGetFileByID() throws IOException {
        final String responseMsg = target.path("/files/id/" + id).request(MediaType.APPLICATION_JSON).get(String.class);
        assertEquals("[{\"id\":" + id + ",\"name\":\"GetFileByIDTest.txt\"}]", responseMsg);
    }
}

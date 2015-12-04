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
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class DeleteFileByIDTest {

    private HttpServer server;
    private WebTarget target;

    @Before
    public void setUp() throws Exception {
        final String baseURI = "http://localhost:8087/";
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
    public void testDeleteFileByID() throws IOException {
        final File file = new File(Main.getRootDir() + "DeleteFileByIDTest.txt");
        FileUtils.writeStringToFile(file, "DeleteFileByIDTest");
        final long id = LocalFile.getCRC(file);
        FileService.addFile(file);

        final Response response = target.path("/files/id/" + id).request(MediaType.APPLICATION_JSON).delete();
        assertEquals(200, response.getStatus());

        final Optional<?> match
                = Files.walk(Paths.get(Main.getRootDir()))
                .filter(Files::isRegularFile)
                .filter(f -> f.getFileName().toString().startsWith("DeleteFileByIDTest"))
                .findFirst();
        assertFalse(match.isPresent());
    }
}

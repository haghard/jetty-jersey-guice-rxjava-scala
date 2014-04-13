package ru.server.resources;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.server.domain.Video;
import ru.server.services.JavaWebService;

@Path("/video")
@Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
public class VideoResource {

  private final JavaWebService javaWebService;

  private Logger logger = LoggerFactory.getLogger("general");
  //private Logger logger = LoggerFactory.getLogger(VideoResource.class);

  @Inject
  VideoResource(JavaWebService javaWebService) {
    this.javaWebService = javaWebService;
  }

  @GET
  @Path("{accountId}")
  public Video getVideoAllPresentations(@PathParam("accountId") long accountId) {
    System.out.println(Thread.currentThread().getName());
    return new Video(1);
  }
}
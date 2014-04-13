package ru.server;

import org.apache.log4j.Logger;
import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

public class ServerServletContainerInitializer implements ServletContainerInitializer {

  private static Logger logger = Logger.getLogger(ServerServletContainerInitializer.class);

  public ServerServletContainerInitializer() {
  }

  @Override
  public void onStartup(Set<Class<?>> c, ServletContext ctx) throws ServletException {
    logger.info("onStartup !!!!!!!!!!!!!!!!!!!!!!!");
  }
}

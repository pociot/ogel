package com.pociot.ogel.configuration;

import java.sql.SQLException;
import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile(Profiles.H2_FILE_BASED)
public class H2FileBasedDBConfig {

  @Bean(initMethod = "start", destroyMethod = "stop")
  public Server h2DbServer() throws SQLException {
    return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
  }
}
